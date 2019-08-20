package com.elitecore.aaa.core.drivers;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.aaa.core.conf.MAPGWAuthDriverConfiguration;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.coreeap.data.sim.PDPContext;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.sim.ulticom.UlticomCommunicator;

public abstract class MAPGWAuthDriver extends BaseAuthDriver {
	private static final String MODULE = "MAPGW AUTHDRIVER";
	
	/** {@link #driverID} and {@link #driverIDCounter} are used for connection index with Signalware library. <br>
	 * Every instance of MAP Gateway driver will  have unique {@link #driverID} so that TCP connection of that driver can be used 
	 * to make any query to AuthGateway
	 */
	private static int driverIDCounter = 0;
	private int driverID;
	
	private MAPGWRequestorConnectionPool mapGWRequestorConnectionPool;
	
	//private static final String  DEFAULT_ULTICOM_MAPGW_IDENTITY = "111111111111111";
	private UlticomCommunicator ulticomCommunicator;
	private AtomicLong maxQueryTimeoutCount;
	protected MAPGWAuthDriverConfiguration mapGWAuthDriverConfiguration;

	private MAPDriverESIStatistics driverStatistics;
	
	public MAPGWAuthDriver(AAAServerContext serverContext,MAPGWAuthDriverConfiguration mapGWAuthDriverConfiguration) {
		super(serverContext);
		this.mapGWAuthDriverConfiguration=mapGWAuthDriverConfiguration;
		maxQueryTimeoutCount = new AtomicLong(0);
		ulticomCommunicator = new UlticomCommunicator();
		driverID = driverIDCounter++;
	}

	// For now all errors are considered as permanent
	@Override
	protected void initInternal() throws DriverInitializationFailedException{		
		super.initInternal();
		if (mapGWAuthDriverConfiguration.getIsSAIEnabled()){
			boolean isCommunicatorInitialized = ulticomCommunicator.init(mapGWAuthDriverConfiguration.getNoOfMAPGWConnections(), LogManager.getLogger().getCurrentLogLevel(), mapGWAuthDriverConfiguration.getRemoteHost(), mapGWAuthDriverConfiguration.getLocalHost(), driverID);
			if (!isCommunicatorInitialized){
				LogManager.getLogger().error(MODULE, "Failed to initialize Connection to MAP Gateway.");
				throw new DriverInitializationFailedException("Failed to initialize Connection to MAP Gateway");
			}
		}
		UlticomResponseListener listener = new UlticomResponseListenerImpl();
		mapGWRequestorConnectionPool = new MAPGWRequestorConnectionPool(listener, mapGWAuthDriverConfiguration.getRemoteHost(), mapGWAuthDriverConfiguration.getLocalHost(), mapGWAuthDriverConfiguration.getNoOfMAPGWConnections(), mapGWAuthDriverConfiguration.getRequestTimeout());
		boolean isInitialized = mapGWRequestorConnectionPool.init(driverID, mapGWAuthDriverConfiguration.getRemoteHost(), mapGWAuthDriverConfiguration.getLocalHost());
		if (!isInitialized){
			throw new DriverInitializationFailedException("MAP Gateway Connection pool could not be initialized.");
		}
		//TODO currently Signalware does not differentiates between timeout and invalid IMSI whenever it will be implemented then below code can be uncommented.
		/* boolean bResponseGetSuccessfully = sendDummyRequest();
		if(!bResponseGetSuccessfully){
			new DriverInitializationFailedException("MAPGW Auth Driver : "+getDriverName()+"initialization failed , Reason : ");
		}
		*/
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "MAPGW Auth Driver "+getName()+" initialized successfully" );
	}
	
	public AccountData getAccountData(ServiceRequest serviceRequest, String identity){
		AccountData tmpAccountData = null;
		
		List<String> driverLevelUIAttr =  getUserIdentityAttributes();
		if (driverLevelUIAttr != null){
			for (String userIdAttr : driverLevelUIAttr){
				String userIdValue = getValueForIdentityAttribute(serviceRequest, userIdAttr); 
				if ( userIdValue != null){
					identity = userIdValue;
					break;
				}
			}
		}
		
		identity = Utility.getIMSIFromIdentity(identity);
		
		if(identity == null || identity.length() < 15){
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Invalid IMSI received, Reason: IMSI shoud be atleast 15 digit decimal value" );
			return null;
		}
		
		int eapMethodType = getEAPMethodType(serviceRequest);
		if (isSAIRequired(eapMethodType)) {
			if (eapMethodType == EapTypeConstants.SIM.typeId){
				driverStatistics.incrementSIMTotalRequests();
				tmpAccountData = fetchTriplets(identity);
			} else { 
				driverStatistics.incrementAKATotalRequests();
				tmpAccountData = fetchQuintets(identity);
			}
			if (tmpAccountData == null){
				if(maxQueryTimeoutCount.incrementAndGet()>=mapGWAuthDriverConfiguration.getMaxQueryTimeoutCount()){
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Total number of query timeouts exceeded then configured max number of query timeouts,so System "+getName()+" marked as DEAD");
					markDead();
				}
				return null;
			}
		}
		if (!mapGWAuthDriverConfiguration.isRestoreDataEnabled())
			return tmpAccountData;
		
		Map<String, String> profileFieldMap = new HashMap<String,String>();
		if(identity == null || identity.length() < 15){
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Invalid IMSI: " + identity + " received, Reason: IMSI shoud be 15 digit decimal value" );
			return null;
		}
				
		if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
			LogManager.getLogger().warn(MODULE, "Requested Identity to MAP G/W : " + identity);

		UlticomResponseListener listener = new UlticomResponseListenerImpl();
		driverStatistics.incrementProfileRequests();
		mapGWRequestorConnectionPool.requestProfile(identity, listener, driverID);
		
		if (listener.isFailure()){
			
			if(maxQueryTimeoutCount.incrementAndGet()>=mapGWAuthDriverConfiguration.getMaxQueryTimeoutCount()){
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Total number of query timeouts exceeded then configured max number of query timeouts,so System "+getName()+" marked as DEAD");
				markDead();
			}
			return tmpAccountData;
		}
		
		maxQueryTimeoutCount.set(0);
		
		profileFieldMap = listener.getProfileFieldMap();
		if(profileFieldMap == null || profileFieldMap.size() == 0){
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "User not found. Blank profile received from MAP G/W.");
			return tmpAccountData;
		}
		MapGWAccountDataValueProvider valueProvider = new MapGWAccountDataValueProvider(profileFieldMap);
		AccountData accountData = buildAccountData(serviceRequest,valueProvider, mapGWAuthDriverConfiguration.getAccountDataFieldMapping());
		
		if ( accountData != null ){
			
			// If listner has PDP Contexts set after profile request adding them to account data
			List<PDPContext> pdpContexts = listener.getPDPContexts();
			if (pdpContexts != null && pdpContexts.size() > 0) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "PDP Contexts for IMSI: " + identity + " are:" + pdpContexts); 
				accountData.setPdpContexts(pdpContexts.toArray(new PDPContext[pdpContexts.size()]));
			}
			
			accountData.setUserIdentity(identity);
			if (tmpAccountData != null)
				accountData.setPassword(tmpAccountData.getPassword());
		}
		return accountData;
	}
	
	/**
	 * Send authentication info (SAI) communication is to be done only in case
	 * when the SAI configuration is enabled and eap method is one of EAP_AKA,
	 * EAP_AKA_PRME or EAP_SIM, making it a Universal MAP-GW driver which can 
	 * be used in hybrid mode for all types of request.
	 * 
	 * <p>This change has been incorporated with
	 * <a href="https://jira.eliteaaa.com:8443/browse/ELITEAAA-2594">ELITEAAA-2594</a> 
	 * 
	 * @param eapMethodType
	 * @return
	 */
	private boolean isSAIRequired(int eapMethodType) {
		return mapGWAuthDriverConfiguration.getIsSAIEnabled()
			&& (eapMethodType == EapTypeConstants.AKA.typeId
					|| eapMethodType == EapTypeConstants.AKA_PRIME.typeId
					|| eapMethodType == EapTypeConstants.SIM.typeId);
	}

	private AccountData fetchTriplets(final String identity) {
		final AccountData accountData = new AccountData();
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Requested Identity to MAP G/W : " + identity);
		ulticomCommunicator.getTriplets(identity, mapGWAuthDriverConfiguration.getNumberOfSIMTriplets(), new UlticomCommunicator.UlticomResponseListener() {
			
			public void onSuccess(String authVectors) {
				accountData.setPassword("0:" +authVectors);
				driverStatistics.incrementSIMSuccessResponse();
			}
			
			public void onFailure(String reason) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Failed to retrieve triplets. Reason: " + reason);
				}
				accountData.setPassword(new String[] {null});
				driverStatistics.incrementSIMErrorResponse();
			}

			@Override
			public void markDriverDead() {
				MAPGWAuthDriver.this.markDead();
			}
			
			@Override
			public void onTimeout() {
				driverStatistics.incrementSIMTimeouts();
				if(maxQueryTimeoutCount.incrementAndGet()>=mapGWAuthDriverConfiguration.getMaxQueryTimeoutCount()){
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Total number of query timeouts exceeded then configured max number of query timeouts,so System "+getName()+" marked as DEAD");
					markDead();
				}
			}
			
		}, mapGWAuthDriverConfiguration.getRequestTimeout(), driverID);
		if (accountData.getPassword() != null){
			return accountData;
		}
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Failure in retrieving triplets for identity: " + identity);
		}
		return null;
	}

	private AccountData fetchQuintets(final String identity) {
		final AccountData accountData = new AccountData();
		
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Requested Identity to MAP G/W : " + identity);
		ulticomCommunicator.getQuintuplet(identity, new UlticomCommunicator.UlticomResponseListener() {
			
			public void onSuccess(String authVectors) {
				accountData.setPassword("0:" +authVectors);
				driverStatistics.incrementAKASuccessResponse();
			}
			
			public void onFailure(String reason) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Failed to retrieve Quintets. Reason: " + reason);
				}
				accountData.setPassword(new String[] {null});
				driverStatistics.incrementAKAErrorResponse();
			}

			@Override
			public void markDriverDead() {
				MAPGWAuthDriver.this.markDead();
			}
			
			@Override
			public void onTimeout() {
				driverStatistics.incrementAKATimeouts();
				if(maxQueryTimeoutCount.incrementAndGet()>=mapGWAuthDriverConfiguration.getMaxQueryTimeoutCount()){
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Total number of query timeouts exceeded then configured max number of query timeouts,so System "+getName()+" marked as DEAD");
					markDead();
				}
			}
			
		}, mapGWAuthDriverConfiguration.getRequestTimeout(), driverID);
		if (accountData.getPassword() != null){
			return accountData;
		}
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Failure in retrieving quintets for identity: " + identity);
		}
		return null;
	}

	@Override
	protected AccountData fetchAccountData(ServiceRequest serviceRequest, List<String> userIdentities,ChangeCaseStrategy caseStrategy, boolean bTrimUserIdentity, StripUserIdentityStrategy stripStrategy, String realmSeparator) throws DriverProcessFailedException {
		
		String userIdentityValue= getUserIdentityValue(serviceRequest);				
		if(userIdentityValue!=null) {
			serviceRequest.setParameter(AAAServerConstants.USER_IDENTITY, userIdentityValue);
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Getting Account Data For User Name : "+userIdentityValue);
			return getAccountData(serviceRequest, getStrippedAndTrimedUserIdentity(userIdentityValue, caseStrategy, bTrimUserIdentity, stripStrategy, realmSeparator));
		}
		else
			return null;
		
	}
	public interface UlticomResponseListener{
		public void onSuccess(Map<String, String> profileFieldMap, List<PDPContext> pdpContexts);
		public List<PDPContext> getPDPContexts();
		public void onFailure(String reason);
		public Map<String, String> getProfileFieldMap();
		public boolean isFailure();
		public void resetFailure();
		public void markDriverDead();
		public void onTimeout();
	}
	
	@Override
	protected AccountData fetchAccountData(ServiceRequest serviceRequest,ChangeCaseStrategy caseStrategy, boolean bTrimUserIdentity, StripUserIdentityStrategy stripStrategy, String realmSeparator, String userIdentity)throws DriverProcessFailedException {
		return getAccountData(serviceRequest, userIdentity);
	}
	
	@Override
	public void scan() {
		markAlive();
		//TODO currently Signalware does not differentiates between timeout and invalid IMSI whenever it will be implemented then below code can be uncommented.
		/*if(sendDummyRequest()){
			markAlive();
		}else {
			markDead();
			getLogger().warn(MODULE, "MAPGW Auth Driver : " + getDriverName()+ " is still dead.");
		}*/
	}
	private String getStrippedAndTrimedUserIdentity(String userIdentityValue,ChangeCaseStrategy caseStrategy, boolean bTrimUserIdentity, StripUserIdentityStrategy stripStrategy, String realmSeparator) {
		if (bTrimUserIdentity) {
			userIdentityValue = userIdentityValue.trim();
		}
		userIdentityValue = caseStrategy.apply(userIdentityValue);
		
		userIdentityValue = stripStrategy.apply(userIdentityValue, realmSeparator);
		
		return userIdentityValue;		
	}
	
	@Override
	public void stop(){
		super.stop();
		mapGWRequestorConnectionPool.stop(mapGWAuthDriverConfiguration.getRemoteHost(), mapGWAuthDriverConfiguration.getLocalHost(),driverID);
		if (mapGWAuthDriverConfiguration.getIsSAIEnabled())
			ulticomCommunicator.stop(mapGWAuthDriverConfiguration.getRemoteHost(), mapGWAuthDriverConfiguration.getLocalHost(), driverID);
	}
	public class UlticomResponseListenerImpl implements UlticomResponseListener{

		private boolean isFailure= false;
		private Map<String, String> profileFieldMap = null;
		private List<PDPContext> pdpContexts;
		
		@Override
		public void onSuccess(Map<String, String> profileFieldMap, List<PDPContext> pdpContexts) {
			this.profileFieldMap = profileFieldMap;
			this.pdpContexts = pdpContexts;
			isFailure = false;
			driverStatistics.incrementProfileSuccessRespnse();
		}

		public Map<String, String> getProfileFieldMap(){
			return profileFieldMap;
		}
		
		@Override
		public List<PDPContext> getPDPContexts() {
			return pdpContexts;
		}

		@Override
		public void onFailure(String reason) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, reason);
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "User not found. Could not locate customer,reason : " + reason);
			isFailure = true;
			driverStatistics.incrementProfileErrorRespnse();
		}

		public boolean isFailure(){
			return isFailure;
		}

		@Override
		public void resetFailure() {
			isFailure = false;
		}

		@Override
		public void markDriverDead() {
			MAPGWAuthDriver.this.markDead();
		}
		
		@Override
		public void onTimeout() {
			driverStatistics.incrementProfileTimeouts();
			if(maxQueryTimeoutCount.incrementAndGet()>=mapGWAuthDriverConfiguration.getMaxQueryTimeoutCount()){
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Total number of query timeouts exceeded then configured max number of query timeouts,so System "+getName()+" marked as DEAD");
				markDead();
			}
		}
	}
	
	public class MapGWAccountDataValueProvider implements AccountDataValueProvider{
		
		Map<String, String> profileFieldMap;
		public MapGWAccountDataValueProvider(Map<String, String> profileFieldMap) {
			this.profileFieldMap = profileFieldMap;
		}
		@Override
		public String getStringValue(String fieldName) {
			return profileFieldMap.get(fieldName);
		}

		@Override
		public Date getDateValue(String fieldName) {
			return Timestamp.valueOf(profileFieldMap.get(fieldName));
		}

		
	}
	
	@Override
	public String getDriverInstanceId() {
		return this.mapGWAuthDriverConfiguration.getDriverInstanceId();
	}

	@Override
	public String getName() {
		return this.mapGWAuthDriverConfiguration.getDriverName();
	}

	@Override
	protected int getStatusCheckDuration() {
		return this.mapGWAuthDriverConfiguration.getStatusCheckDuration();
	}
	public abstract String getUserIdentityValue(ServiceRequest request) ;
	
	protected abstract int getEAPMethodType(ServiceRequest request);
	
	public List<String> getUserIdentityAttributes(){
		return mapGWAuthDriverConfiguration.getUserIdentityAttributes();
	}

	@Override
	public void markDead() {
		maxQueryTimeoutCount.set(0);
		super.markDead();
		if (mapGWAuthDriverConfiguration.getIsSAIEnabled()){
			ulticomCommunicator.shutdownSctpAuthConnection(driverID);
		}
		mapGWRequestorConnectionPool.shutdownSctpAuthConnection(driverID);
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "MAPGW Auth Driver "+getName()+" cleanup completed" );
	}
	
	@Override
	protected ESIStatisticsImpl createESIStatistics() {
		driverStatistics = new MAPDriverESIStatistics();
		return driverStatistics;
	}
//	private boolean sendDummyRequest() {
//		UlticomResponseListener listener = new UlticomResponseListenerImpl();
//		mapGWRequestorConnectionPool.requestProfile(DEFAULT_ULTICOM_MAPGW_IDENTITY, listener);
//		return !listener.isFailure();
//				
//	}
	
	class MAPDriverESIStatistics extends ESIStatisticsImpl {

		private AtomicLong SIMRequests = new AtomicLong(0);
		private AtomicLong AKARequests = new AtomicLong(0);
		private AtomicLong profileRequests = new AtomicLong(0);

		private AtomicLong SIMSuccessResponse = new AtomicLong(0);
		private AtomicLong AKASuccessResponse = new AtomicLong(0);
		private AtomicLong profileSuccessResponse = new AtomicLong(0);

		private AtomicLong SIMErrorResponse = new AtomicLong(0);
		private AtomicLong AKAErrorResponse = new AtomicLong(0);
		private AtomicLong profileErrorResponse = new AtomicLong(0);

		private AtomicLong SIMTimeouts = new AtomicLong(0);
		private AtomicLong AKATimeouts = new AtomicLong(0);
		private AtomicLong profileTimeouts = new AtomicLong(0);

		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append(super.toString())
			.append("\n" + StringUtility.fillChar("Type", 35) + " ")
			.append(StringUtility.fillChar("AKA", 5)).append(StringUtility.fillChar("SIM", 5)).append(StringUtility.fillChar("Restore Data", 14)).append(StringUtility.fillChar("Total", 7))
			.append("\n" + StringUtility.fillChar("MAP Requests", 35) + ":" + StringUtility.fillChar(Long.toString(getAKARequests()), 5) + StringUtility.fillChar(Long.toString(getSIMRequests()), 5) + StringUtility.fillChar(Long.toString(getProfileRequests()), 14) + StringUtility.fillChar(Long.toString(getTotalMAPRequests()), 7))
			.append("\n" + StringUtility.fillChar("MAP Success Responses", 35) + ":" + StringUtility.fillChar(Long.toString(getAKASuccessResponse()), 5) + StringUtility.fillChar(Long.toString(getSIMSuccessResponse()), 5) + StringUtility.fillChar(Long.toString(getProfileSuccessResponse()), 14) + StringUtility.fillChar(Long.toString(getTotalMAPSuccessResponses()), 7))
			.append("\n" + StringUtility.fillChar("MAP Error Responses", 35) + ":" + StringUtility.fillChar(Long.toString(getAKAErrorResponse()), 5) + StringUtility.fillChar(Long.toString(getSIMErrorResponse()), 5) + StringUtility.fillChar(Long.toString(getProfileErrorResponse()), 14) + StringUtility.fillChar(Long.toString(getTotalMAPErrorResponses()), 7))
			.append("\n" + StringUtility.fillChar("MAP Timeouts", 35) + ":" + StringUtility.fillChar(Long.toString(getAKATimeouts()), 5) + StringUtility.fillChar(Long.toString(getSIMTimeouts()), 5) + StringUtility.fillChar(Long.toString(getProfileTimeouts()), 14) + StringUtility.fillChar(Long.toString(getTotalMAPTimeouts()), 7));
			return buffer.toString();
		}

		public void incrementSIMTotalRequests() {
			SIMRequests.incrementAndGet();
		}

		public void incrementAKATotalRequests() {
			AKARequests.incrementAndGet();
		}

		public void incrementProfileRequests() {
			profileRequests.incrementAndGet();
		}

		public void incrementSIMSuccessResponse() {
			SIMSuccessResponse.incrementAndGet();
		}

		public void incrementAKASuccessResponse() {
			AKASuccessResponse.incrementAndGet();
		}

		public void incrementProfileSuccessRespnse() {
			profileSuccessResponse.incrementAndGet();
		}

		public void incrementSIMErrorResponse() {
			SIMErrorResponse.incrementAndGet();
		}

		public void incrementAKAErrorResponse() {
			AKAErrorResponse.incrementAndGet();
		}

		public void incrementProfileErrorRespnse() {
			profileErrorResponse.incrementAndGet();
		}

		public void incrementSIMTimeouts() {
			SIMTimeouts.incrementAndGet();
		}

		public void incrementAKATimeouts() {
			AKATimeouts.incrementAndGet();
		}

		public void incrementProfileTimeouts() {
			profileTimeouts.incrementAndGet();
		}

		public long getSIMRequests() {
			return SIMRequests.get();
		}

		public long getAKARequests() {
			return AKARequests.get();
		}

		public long getProfileRequests() {
			return profileRequests.get();
		}

		public long getSIMSuccessResponse() {
			return SIMSuccessResponse.get();
		}

		public long getAKASuccessResponse() {
			return AKASuccessResponse.get();
		}

		public long getProfileSuccessResponse() {
			return profileSuccessResponse.get();
		}

		public long getSIMErrorResponse() {
			return SIMErrorResponse.get();
		}

		public long getAKAErrorResponse() {
			return AKAErrorResponse.get();
		}

		public long getProfileErrorResponse() {
			return profileErrorResponse.get();
		}

		public long getSIMTimeouts() {
			return SIMTimeouts.get();
		}

		public long getAKATimeouts() {
			return AKATimeouts.get();
		}

		public long getProfileTimeouts() {
			return profileTimeouts.get();
		}

		private long getTotalMAPRequests() {
			return getSIMRequests() + getAKARequests() + getProfileRequests();
		}

		private long getTotalMAPSuccessResponses() {
			return getSIMSuccessResponse() + getAKASuccessResponse() + getProfileSuccessResponse();
		}

		private long getTotalMAPErrorResponses() {
			return getSIMErrorResponse() + getAKAErrorResponse() + getProfileErrorResponse();
		}

		private long getTotalMAPTimeouts() {
			return getSIMTimeouts() + getAKATimeouts() + getProfileTimeouts();
		}
	}
	
}
