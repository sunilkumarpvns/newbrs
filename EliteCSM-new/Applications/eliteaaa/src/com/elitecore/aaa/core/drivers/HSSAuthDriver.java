package com.elitecore.aaa.core.drivers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.elitecore.aaa.core.conf.HSSAuthDriverConfiguration;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.data.DataFieldMapping;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.conf.impl.RoutingEntryDataImpl;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.drivers.TransientFailureException;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;
import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.core.common.transport.VirtualInputStream;
import com.elitecore.diameterapi.core.common.transport.VirtualOutputStream;
import com.elitecore.diameterapi.core.common.transport.constant.SecurityStandard;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.core.stack.ElementRegistrationFailedException;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerDataImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerGroupImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.Application;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.mibs.constants.ServiceTypes;

public abstract class HSSAuthDriver extends BaseAuthDriver {

	private static final String SIPDATA_DELIMETER = ";";
	
	private HSSAuthDriverConfiguration hssAuthDriverConf;
	private AAAServerContext serverContext;
	private VirtualInputStream virtualInputStream;
	private Map<Integer, HSSRequestHandler> hbhToResponseHandlerMap;
	private ApplicationEnum tgppApplicationIdentifier;
	
	/**
	 * For AKA-->  parentAVPID, RandAVPId, XresAVPId, CkAVPId, IkAVPId, AutnAVPId
	 * <br />
	 * For SIM-->  parentAVPID, RandAVPId, SresAVPId, KcAVPId
	 */
	private String[] authVectorAVPs;

	public HSSAuthDriver(AAAServerContext serverContext, DriverConfiguration driverConfiguration) {
		super(serverContext);
		this.serverContext = serverContext;
		this.hbhToResponseHandlerMap = new ConcurrentHashMap<Integer, HSSAuthDriver.HSSRequestHandler>();
		this.hssAuthDriverConf =  (HSSAuthDriverConfiguration) driverConfiguration;
	}

	// All errors of HSS auth driver are considered as permanent
	@Override
	protected void initInternal() throws TransientFailureException, 
	DriverInitializationFailedException {
		super.initInternal();
		initVirtualPeer();
		registerRoutingEntry();
		generatePasswordAVPConstants();
		initApplicationEnum();
	}
	
	@Override
	public void reInit() throws DriverInitializationFailedException {
		this.hssAuthDriverConf =  (HSSAuthDriverConfiguration) serverContext.getServerConfiguration()
				.getDiameterDriverConfiguration().getDriverConfiguration(getDriverInstanceId());
		registerRoutingEntry();
		generatePasswordAVPConstants();
		initApplicationEnum();
	}

	@Override
	public String getDriverInstanceId() {
		return hssAuthDriverConf.getDriverInstanceId();
	}

	@Override
	public void scan() {
		markAlive();
	}

	@Override
	public String getName() {
		return hssAuthDriverConf.getDriverName();
	}

	@Override
	protected AccountData fetchAccountData(ServiceRequest serviceRequest,
			ChangeCaseStrategy caseStrategy, boolean btrimUserIdentity,
			StripUserIdentityStrategy stripStrategy, String realmSeparator,
			String userIdentity)
					throws DriverProcessFailedException {
		
		String sipdata = null;
		if (userIdentity != null && userIdentity.contains(SIPDATA_DELIMETER)) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(getModule(), "Synchronization failure occurred. "
						+ "So splitting user identity: " + userIdentity);
			}
			
			String[] splitIdentity = userIdentity.split(SIPDATA_DELIMETER);
			userIdentity = splitIdentity[0];
			sipdata = splitIdentity[1];
			
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(getModule(), "Splitted User Identity: " + userIdentity);
				LogManager.getLogger().debug(getModule(), "Splitted SIP data: " + sipdata);
			}
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(getModule(), "User identity not found or it does not contain the sip data: " + userIdentity);
			}
		}
		
		String finalUserIdentity = getUserIdentityAttributeValue(userIdentity, serviceRequest);
		if(finalUserIdentity == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(getModule(), "Unable to fetch User Profile with driver: " +getName()+
							", Reason: UserIdentity not found");
			}
			return null;
		}
		
		finalUserIdentity = trimStripValidateUserIdentity(caseStrategy, btrimUserIdentity, 
				stripStrategy, realmSeparator, finalUserIdentity);

		if(finalUserIdentity == null){
			return null;
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(getModule(), "Fetching User Profile for Identity: " + finalUserIdentity);
		}
		
		
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getModule(), "Building Diameter Request for UserIdentity: " + userIdentity );
			}
		
		DiameterRequest diameterRequest = new DiameterRequest();
		
		set3gppProperties(diameterRequest);
		
		addVendorSpecificAppIdAvpIn(diameterRequest);
		
		addDestinationRealmAvpIn(diameterRequest);
		
		addDestinationHostAvpIn(diameterRequest);

		addSipAuthNumberItemsAvpIn(diameterRequest);
			
		addAdditionalAttributes(serviceRequest, diameterRequest);
		
		if (sipdata != null) {
			addAvpForSynchronizationFailureIn(diameterRequest, sipdata);
		}
		
		addUserNameAvpIn(diameterRequest, finalUserIdentity);
		
		addSessionIdAvpIn(diameterRequest, finalUserIdentity);
		
		HSSRequestHandler handler = new HSSRequestHandler(serviceRequest, diameterRequest);
		hbhToResponseHandlerMap.put(diameterRequest.getHop_by_hopIdentifier(), handler);
		handler.handleRequest();
		return handler.getAccountData();
	}
	
	private void addSessionIdAvpIn(DiameterRequest diameterRequest, String userIdentity) {
		IDiameterAVP avp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.SESSION_ID);
		if (avp == null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(getModule(), DiameterAVPConstants.SESSION_ID_STR + 
						" AVP will not be added to Diameter Request with HbH-ID=" + 
						diameterRequest.getHop_by_hopIdentifier() + ", Reason: AVP not found in dictionary");
			}
		}else{
			avp.setStringValue(userIdentity + ";" + 
					diameterRequest.getHop_by_hopIdentifier() + ";" + 
					diameterRequest.getEnd_to_endIdentifier());
			diameterRequest.addAvp(avp);
		}
	}
		
	private void addUserNameAvpIn(DiameterRequest diameterRequest, String userIdentity) {
		IDiameterAVP avp;
		avp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.USER_NAME);
		if(avp == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(getModule(), DiameterAVPConstants.USER_NAME_STR + 
						" AVP will not be added to Diameter Request with HbH-ID=" + diameterRequest.getHop_by_hopIdentifier() + 
						", Reason: AVP not found in dictionary");
			}
		} else {
			avp.setStringValue(userIdentity);
			diameterRequest.addAvp(avp);
		}
	}
			
	private void addAvpForSynchronizationFailureIn(DiameterRequest diameterRequest, String sipdata) {
		IDiameterAVP avp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.TGPP_SIP_AUTHORIZATION);
		if (avp == null) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(getModule(), DiameterAVPConstants.TGPP_SIP_AUTHORIZATION + 
						" AVP will not be added to Diameter Request with HbH-ID=" + 
						diameterRequest.getHop_by_hopIdentifier() + ", Reason: AVP not found in dictionary");
				}
			}else{
			avp.setStringValue(sipdata);
			AvpGrouped sipAuthDataItems = (AvpGrouped) diameterRequest.getAVP(DiameterAVPConstants.TGPP_SIP_AUTH_DATA_ITEMS);
			
			if (sipAuthDataItems == null) {
				sipAuthDataItems = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.TGPP_SIP_AUTH_DATA_ITEMS);
				sipAuthDataItems.addSubAvp(avp);
				diameterRequest.addAvp(sipAuthDataItems);
			} else {
				sipAuthDataItems.addSubAvp(avp);
			}
			LogManager.getLogger().debug(getModule(), "Successfully added SIP Auth data Items: " + sipAuthDataItems);
				}
			}

	private void addAdditionalAttributes(ServiceRequest serviceRequest,
			DiameterRequest diameterRequest) {
		if(hssAuthDriverConf.getAdditionalAttributes() != null 
				&& hssAuthDriverConf.getAdditionalAttributes().trim().length() > 0){
			addAdditionalAttributes(diameterRequest, serviceRequest);
		}
	}

	private void addSipAuthNumberItemsAvpIn(DiameterRequest diameterRequest) {
		IDiameterAVP avp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.TGPP_SIP_NUMBER_AUTH_ITEMS);
		if(avp == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(getModule(), DiameterAVPConstants.DESTINATION_REALM_STR + 
						" AVP will not be added to Diameter Request with HbH-ID=" + diameterRequest.getHop_by_hopIdentifier() + 
						", Reason: AVP not found in dictionary");
			}
		}else{
			avp.setInteger(hssAuthDriverConf.getNumberOfTriplets());
			diameterRequest.addAvp(avp);
		}
	}

	private void addDestinationHostAvpIn(DiameterRequest diameterRequest) {
		IDiameterAVP avp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.DESTINATION_HOST);
		if(avp == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(getModule(), DiameterAVPConstants.DESTINATION_HOST_STR + 
						" AVP will not be added to Diameter Request with HbH-ID=" + diameterRequest.getHop_by_hopIdentifier() + 
						", Reason: AVP not found in dictionary");
			}
		}else{
			avp.setStringValue("*");
			diameterRequest.addAvp(avp);
		}
	}
		
	private void addDestinationRealmAvpIn(DiameterRequest diameterRequest) {
		IDiameterAVP avp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.DESTINATION_REALM);
		if(avp == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(getModule(), DiameterAVPConstants.DESTINATION_REALM_STR + 
						" AVP will not be added to Diameter Request with HbH-ID=" + diameterRequest.getHop_by_hopIdentifier() + 
						", Reason: AVP not found in dictionary");
			}
		}else{
			avp.setStringValue("*");
			diameterRequest.addAvp(avp);
		}
	}

	private void addVendorSpecificAppIdAvpIn(DiameterRequest diameterRequest) {
		AvpGrouped vendorSpeceficAppID = (AvpGrouped)DiameterDictionary.getInstance()
				.getAttribute(DiameterAVPConstants.VENDOR_SPECIFIC_APPLICATION_ID);
		
		if(vendorSpeceficAppID == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(getModule(), DiameterAVPConstants.VENDOR_SPECIFIC_APPLICATION_ID_STR + 
						" AVP will not be added to Diameter Request with HbH-ID=" + 
						diameterRequest.getHop_by_hopIdentifier() + ", Reason: AVP not found in dictionary");
			}
		} else {
			
			IDiameterAVP subAVP = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.VENDOR_ID);
			if (subAVP == null) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(getModule(), DiameterAVPConstants.VENDOR_ID_STR + " AVP will not be added to " +
							DiameterAVPConstants.VENDOR_SPECIFIC_APPLICATION_ID_STR + " AVP , Reason: AVP not found in dictionary");
				}
			}else{
				subAVP.setInteger(tgppApplicationIdentifier.getVendorId());
				vendorSpeceficAppID.addSubAvp(subAVP);
			}
			subAVP = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.AUTH_APPLICATION_ID);
			if (subAVP == null) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(getModule(), DiameterAVPConstants.VENDOR_SPECIFIC_APPLICATION_ID_STR + 
						" AVP will not be added to Diameter Request with HbH-ID=" + diameterRequest.getHop_by_hopIdentifier() + 
							", Reason: " + DiameterAVPConstants.AUTH_APPLICATION_ID_STR + " AVP not found in dictionary");
			}
		}else{
				subAVP.setInteger(tgppApplicationIdentifier.getApplicationId());
				vendorSpeceficAppID.addSubAvp(subAVP);
				diameterRequest.addAvp(vendorSpeceficAppID);
			}
		}
	}

	private void addAdditionalAttributes(DiameterRequest diameterRequest, ServiceRequest serviceRequest){

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(getModule(), "Adding Additional Attributes in Diameter Request with Session-ID=" + 
					diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
		}
		ValueProvider valueProvider = getServiceRequestValueProvider(serviceRequest);
		try {
			List<IDiameterAVP> additionalAVPs = DiameterUtility
					.getDiameterAttributes(hssAuthDriverConf.getAdditionalAttributes(), valueProvider);
			diameterRequest.addAvps(additionalAVPs);
		} catch (Exception e) {

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(getModule(), "Not Adding Additional AVPs, Reason: " + e.getMessage());
			}
		}
		
	}

	protected abstract ValueProvider getServiceRequestValueProvider(ServiceRequest serviceRequest);

	private String getUserIdentityAttributeValue(String userIdentity, ServiceRequest serviceRequest) {

		List<String> driverLevelUIAttr =  hssAuthDriverConf.getUserIdentityAttributes();

		if (driverLevelUIAttr == null){
			return userIdentity;
		}
		for (String userIdAttr : driverLevelUIAttr) {
		
			String userIdValue = getValueForIdentityAttribute(serviceRequest, userIdAttr); 
			if (userIdValue != null) {
				return userIdValue;
			}
		}
		return null;
	}

	private String trimStripValidateUserIdentity(ChangeCaseStrategy caseStrategy,
			boolean bTrimUserIdentity, StripUserIdentityStrategy stripStrategy,
			String realmSeparator, String userIdentityValue) {
		
		if (bTrimUserIdentity) {
			userIdentityValue = userIdentityValue.trim();
		}
		
		userIdentityValue = caseStrategy.apply(userIdentityValue);
		
		userIdentityValue = stripStrategy.apply(userIdentityValue, realmSeparator);
		
		userIdentityValue = Utility.getIMSIFromIdentity(userIdentityValue);
		
		if ( userIdentityValue == null || userIdentityValue.length() < 15 ) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(getModule(), "Invalid IMSI: " + userIdentityValue + 
						" received, Reason: IMSI shoud be atleast 15 digit decimal value" );
			return null;
		}
		return userIdentityValue;
	}

	private AccountData buildAccountData(ServiceRequest serviceRequest, DiameterAnswer diameterAnswer){
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(getModule(), "Building Account Data from Diameter Answer with HbH-ID=" + 
					diameterAnswer.getHop_by_hopIdentifier() + 
					", Session-ID="+ diameterAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID));
		}
		AccountDataValueProvider valueProvider = getAccountDataValueProvider(diameterAnswer);
		AccountData accountData = buildAccountData(serviceRequest, valueProvider, hssAuthDriverConf.getAccountDataFieldMapping());
		buildPassword(accountData, valueProvider, diameterAnswer, serviceRequest);
		return accountData;
	}

	private void buildPassword(AccountData accountData, 
			AccountDataValueProvider valueProvider, 
			DiameterAnswer diameterAnswer, ServiceRequest serviceRequest) {
		

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(getModule(), "Generating User-Password from Diameter Answer with HbH-ID=" + 
					diameterAnswer.getHop_by_hopIdentifier() + 
					", Session-ID="+ diameterAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID));
		}
		
		IDiameterAVP resultCodeAvp = diameterAnswer.getAVP(DiameterAVPConstants.RESULT_CODE);
		if(resultCodeAvp == null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(getModule(), "Result-code AVP not recieved, fetching " + DiameterAVPConstants.EXPERIMENTAL_RESULT_STR + 
						" AVP from Diameter Answer with Session-ID=" + 
						diameterAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID));
			}
			AvpGrouped experimentalResultCode = (AvpGrouped) diameterAnswer.getAVP(DiameterAVPConstants.EXPERIMENTAL_RESULT);
			if(experimentalResultCode == null || experimentalResultCode.getGroupedAvp().size() == 0){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(getModule(), DiameterAVPConstants.EXPERIMENTAL_RESULT_CODE_STR + 
							" AVP not found, not generating User-Password from Diameter Answer with Session-ID=" + 
							diameterAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID));
				}
				return;
			}
			resultCodeAvp = experimentalResultCode.getSubAttribute(DiameterAVPConstants.EXPERIMENTAL_RESULT_CODE);
			if(resultCodeAvp == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(getModule(), DiameterAVPConstants.EXPERIMENTAL_RESULT_CODE_STR + 
							" AVP not found in " + DiameterAVPConstants.EXPERIMENTAL_RESULT_STR + 
							" AVP, not generating User-Password from Diameter Answer with Session-ID=" + 
							diameterAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID));
				}
				return;
			}
		}
		
		int resultCode = (int) resultCodeAvp.getInteger();
		if(resultCode != ResultCode.DIAMETER_SUCCESS.code) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(getModule(), "Result-code: " +	ResultCode.fromCode(resultCode) + 
						" recieved, not generating User-Password from Diameter Answer with Session-ID=" + 
						diameterAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID));
			accountData.setPassword((String)null);
			return;
		}
		ArrayList<IDiameterAVP> passwordAVPs = diameterAnswer.getAVPList(authVectorAVPs[0]);
		if(passwordAVPs == null || passwordAVPs.size() == 0){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(getModule(), "Not Generating Password, Reason: " + authVectorAVPs[0] + 
						" AVP not found in Diameter Answer with Session-ID=" + 
						diameterAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID));
			accountData.setPassword((String)null);
			return;
		}
		
		if(passwordAVPs.size() < hssAuthDriverConf.getNumberOfTriplets()){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(getModule(), "Not Generating Password, Reason: " + 
						hssAuthDriverConf.getNumberOfTriplets() + " " +  authVectorAVPs[0] + 
						" AVPs required, " + passwordAVPs.size() + 
						" arrived in Diameter Answer with Session-ID=" + 
						diameterAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID));
			}
			accountData.setPassword((String)null);
			return;
		}
		
		int eapMethodType = getEAPMethodType(serviceRequest);
		if (eapMethodType == EapTypeConstants.AKA.typeId || 
				eapMethodType == EapTypeConstants.AKA_PRIME.typeId ||
				eapMethodType == EapTypeConstants.IDENTITY.typeId){
			accountData.setPassword(getQuintets(passwordAVPs, diameterAnswer));

		} else if(eapMethodType == EapTypeConstants.SIM.typeId) { 
			accountData.setPassword(getTriplets(passwordAVPs, diameterAnswer));
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(getModule(), "Not Generating Password, Reason: EAP Method Type: " + 
						EapTypeConstants.getName(eapMethodType) + " ("+eapMethodType+") not supported.");
			}
		}
		
	}

	private String getQuintets(ArrayList<IDiameterAVP> passwordAVPs, DiameterAnswer answer) {
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(getModule(), "Generating AKA Quintets from Diameter Answer with Session-ID="+ 
						answer.getAVPValue(DiameterAVPConstants.SESSION_ID));
		}
		
		if(authVectorAVPs.length < 6){
			
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(getModule(), "Not generating User-Password, " +
						"Reason: Insufficient Auth Vectors configured in Password Mapping");
			}
			return null;
		}
		StringBuilder authVectors = new StringBuilder();
		authVectors.append("0");
		
		for(int i = 0 ; i < passwordAVPs.size() ; i++){
			
			authVectors.append(":");
			IDiameterAVP vectorAVP = ((AvpGrouped)passwordAVPs.get(i)).getSubAttribute(authVectorAVPs[1]);
			String vectorValue = null;
			
			if(vectorAVP == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(getModule(), "Not appending Rand of Vector: " + (i+1) + 
							", Reason: " + authVectorAVPs[1] + 
							" AVP not forund in Diameter Answer with HbH-ID=" + 
							answer.getHop_by_hopIdentifier());
			}else{
				/*
				 * Rand = 16 bytes
				 */
				vectorValue = TLSUtility.bytesToHex(Arrays.copyOf(vectorAVP.getValueBytes(), 16));
				if(vectorValue != null){
					authVectors.append(vectorValue);
				}
			}
			authVectors.append(",");
			vectorAVP = ((AvpGrouped)passwordAVPs.get(i)).getSubAttribute(authVectorAVPs[2]);
			if(vectorAVP == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(getModule(), "Not appending Xres of Vector: " + (i+1) + 
							", Reason: " + authVectorAVPs[2] + 
							" AVP not forund in Diameter Answer with HbH-ID=" + 
							answer.getHop_by_hopIdentifier());
			}else{
				vectorValue = TLSUtility.bytesToHex(vectorAVP.getValueBytes());
				if(vectorValue != null){
					authVectors.append(vectorValue);
				}
			}
			authVectors.append(",");
			vectorAVP = ((AvpGrouped)passwordAVPs.get(i)).getSubAttribute(authVectorAVPs[3]);
			if(vectorAVP == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(getModule(), "Not appending Ck of Vector: " + (i+1) + 
							", Reason: " + authVectorAVPs[3] + 
							" AVP not forund in Diameter Answer with HbH-ID=" + 
							answer.getHop_by_hopIdentifier());
			}else{
				vectorValue = TLSUtility.bytesToHex(vectorAVP.getValueBytes());
				if(vectorValue != null){
					authVectors.append(vectorValue);
				}
			}
			authVectors.append(",");
			vectorAVP = ((AvpGrouped)passwordAVPs.get(i)).getSubAttribute(authVectorAVPs[4]);
			if(vectorAVP == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(getModule(), "Not appending Ik of Vector: " + (i+1) + 
							", Reason: " + authVectorAVPs[4] + 
							" AVP not forund in Diameter Answer with HbH-ID=" + 
							answer.getHop_by_hopIdentifier());
			}else{
				vectorValue = TLSUtility.bytesToHex(vectorAVP.getValueBytes());
				if(vectorValue != null){
					authVectors.append(vectorValue);
				}
			}
			authVectors.append(",");
			vectorAVP = ((AvpGrouped)passwordAVPs.get(i)).getSubAttribute(authVectorAVPs[5]);
			if(vectorAVP == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(getModule(), "Not appending Autn of Vector: " + (i+1) + 
							", Reason: " + authVectorAVPs[5] + 
							" AVP not forund in Diameter Answer with HbH-ID=" + 
							answer.getHop_by_hopIdentifier());
			}else{
				/*
				 * Autn comes in concatenation with Rand, Reading last 16 bytes
				 */
				int startIndex = vectorAVP.getValueBytes().length - 16;
				vectorValue = TLSUtility.bytesToHex(Arrays.copyOfRange(vectorAVP.getValueBytes(), 
						startIndex, vectorAVP.getValueBytes().length));
				if(vectorValue != null){
					authVectors.append(vectorValue);
				}
			}
		}
		return authVectors.toString();
	}

	private String getTriplets(ArrayList<IDiameterAVP> passwordAVPs, DiameterAnswer answer) {
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(getModule(), "Generating SIM Triplets from Diameter Answer with Session-ID="+ 
						answer.getAVPValue(DiameterAVPConstants.SESSION_ID));
		}
		
		if(authVectorAVPs.length < 4){
			
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(getModule(), "Not generating User-Password, " +
						"Reason: Insufficient Auth Vectors configured in Password Mapping");
			}
			return null;
		}
		StringBuilder authVectors = new StringBuilder();
		authVectors.append("0");
		
		for(int i = 0 ; i < passwordAVPs.size() ; i++){
			
			authVectors.append(":");
			IDiameterAVP vectorAVP = ((AvpGrouped)passwordAVPs.get(i)).getSubAttribute(authVectorAVPs[1]);
			String vectorValue = null;
			
			if(vectorAVP == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(getModule(), "Not appending Rand of Vector: " + (i+1) + 
							", Reason: " + authVectorAVPs[1] + 
							" AVP not forund in Diameter Answer with HbH-ID=" + 
							answer.getHop_by_hopIdentifier());
			}else{

				vectorValue = TLSUtility.bytesToHex(vectorAVP.getValueBytes());
				if(vectorValue != null){
					authVectors.append(vectorValue);
				}
			}
			authVectors.append(",");
			vectorAVP = ((AvpGrouped)passwordAVPs.get(i)).getSubAttribute(authVectorAVPs[2]);
			if(vectorAVP == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(getModule(), "Not appending Sres of Vector: " + (i+1) + 
							", Reason: " + authVectorAVPs[2] + 
							" AVP not forund in Diameter Answer with HbH-ID=" + 
							answer.getHop_by_hopIdentifier());
			}else{
				vectorValue = TLSUtility.bytesToHex(vectorAVP.getValueBytes());
				if(vectorValue != null){
					authVectors.append(vectorValue);
				}
			}
			authVectors.append(",");
			vectorAVP = ((AvpGrouped)passwordAVPs.get(i)).getSubAttribute(authVectorAVPs[3]);
			if(vectorAVP == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(getModule(), "Not appending Kc of Vector: " + (i+1) + 
							", Reason: " + authVectorAVPs[3] + 
							" AVP not forund in Diameter Answer with HbH-ID=" + 
							answer.getHop_by_hopIdentifier());
			}else{
				vectorValue = TLSUtility.bytesToHex(vectorAVP.getValueBytes());
				if(vectorValue != null){
					authVectors.append(vectorValue);
				}
			}
		}
		LogManager.getLogger().debug(getModule(), "Password: " + authVectors);
		return authVectors.toString();
	}

	protected abstract int getEAPMethodType(ServiceRequest request);
	
	protected abstract String getModule();

	private void set3gppProperties(DiameterRequest diameterRequest) {

		diameterRequest.setCommandCode(hssAuthDriverConf.getCommandCode());
		diameterRequest.setApplicationID(tgppApplicationIdentifier.getApplicationId());
	}
	
	private void generatePasswordAVPConstants() {
		List<DataFieldMapping> passwordFieldMappings = hssAuthDriverConf.getAccountDataFieldMapping()
						.getFieldMapping(AccountDataFieldMapping.USER_PASSWORD);

		String passwordString = null;
		if(passwordFieldMappings != null && passwordFieldMappings.size() > 0){
			passwordString = passwordFieldMappings.get(0).getFieldName();
		}
	
		/*
		 * Currently SIP-Auth-Data-Items are considered default for AKA, 
		 * future scope lies for SIM method Default AVPs
		 */
		if(passwordString != null && passwordString.trim().length() > 0){
			authVectorAVPs = ParserUtility.splitString(passwordString, ',', ';');
		}
		
		if(authVectorAVPs == null){
			authVectorAVPs = new String[6];
			authVectorAVPs[0] = DiameterAVPConstants.TGPP_SIP_AUTH_DATA_ITEMS;
			authVectorAVPs[1] = DiameterAVPConstants.TGPP_SIP_AUTHENTICATE;
			authVectorAVPs[2] = DiameterAVPConstants.TGPP_SIP_AUTHORIZATION;
			authVectorAVPs[3] = DiameterAVPConstants.TGPP_SIP_CONFIDENTIALITY_KEY;
			authVectorAVPs[4] = DiameterAVPConstants.TGPP_SIP_INTEGRITY_KEY;
			authVectorAVPs[5] = DiameterAVPConstants.TGPP_SIP_AUTHENTICATE;
		}else{
			for (int i = 0; i < authVectorAVPs.length; i++) {
				authVectorAVPs[i] = authVectorAVPs[i].trim();
			}
		}
		
	}

	private void registerRoutingEntry() throws DriverInitializationFailedException{

		//Adding Advanced condition so that Driver Specific Entry is selected.
		String driverAdvancedCondition = DiameterAVPConstants.EC_REQUESTER_ID+"=\""+getName() + "\""; 
				
		RoutingEntryDataImpl routingEntryData = new RoutingEntryDataImpl();
		routingEntryData.setRoutingName(getName());
		routingEntryData.setApplicationIds(String.valueOf(hssAuthDriverConf.getApplicationId()));
		routingEntryData.setAdvancedCondition(driverAdvancedCondition);
		
		List<PeerGroupImpl> peerGroupList = new ArrayList<PeerGroupImpl>();
		PeerGroupImpl peerGroup = new PeerGroupImpl();
		peerGroup.setPeerInfoList(hssAuthDriverConf.getPeerList());
		peerGroupList.add(peerGroup);
		
		routingEntryData.setPeerGroupList(peerGroupList);

		routingEntryData.setRoutingAction(RoutingActions.RELAY.routingAction);
		
		routingEntryData.setTransActionTimeOut(hssAuthDriverConf.getRequestTimeout());
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(getModule(), "Driver: " + getName() + " registered Routing Entry: " + routingEntryData);
		try {
			serverContext.registerPriorityRoutingEntry(routingEntryData);
		} catch (ElementRegistrationFailedException e) {
			
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(getModule(), "Routing Entry Registration failed, Reason: " + e.getMessage());
			throw new DriverInitializationFailedException(e);
		}
	}
	
	private void initApplicationEnum() throws DriverInitializationFailedException {

		String strApplicationId = hssAuthDriverConf.getApplicationId();
		if(strApplicationId == null || strApplicationId.trim().length() == 0)
			throw new DriverInitializationFailedException("Application Id not Found for Driver: " + getName());

		String[] appId = strApplicationId.split(":");
		final int applicationId;
		final int vendorId;
		try {
			if(appId.length == 1) {
				vendorId = 10415;
				applicationId = Integer.parseInt(appId[0].trim());
			} else if(appId.length == 2) {
				vendorId = Integer.parseInt(appId[0].trim());
				applicationId = Integer.parseInt(appId[1].trim());
			} else {
				throw new DriverInitializationFailedException("Invalid Application Id: " + strApplicationId + 
						" found for Driver: " + getName());
			}

		} catch (NumberFormatException e) {
			throw new DriverInitializationFailedException(e);
		}
		ApplicationEnum applicationEnum = ApplicationIdentifier.fromApplicationIdentifiers(applicationId);
		if(applicationEnum == null){
			applicationEnum = new ApplicationEnum() {

				@Override
				public long getVendorId() {
					return vendorId;
				}

				@Override
				public ServiceTypes getApplicationType() {
					return ServiceTypes.AUTH;
				}

				@Override
				public long getApplicationId() {
					return applicationId;
				}

				@Override
				public Application getApplication() {
					return Application.TGPP_SWX;
				}
				
				@Override
				public String toString() {
					return new StringBuilder()
					.append(getVendorId())
					.append(":")
					.append(getApplicationId())
					.append(" [").append(getApplication().getDisplayName()).append("]").toString();
				}
			};
		}
		tgppApplicationIdentifier = applicationEnum;
	}
	
	private void initVirtualPeer() throws DriverInitializationFailedException{
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(getModule(), "Initializing Diameter HSS Virtual Peer for Driver: " + getName()); 
		}
		PeerDataImpl peerData = new PeerDataImpl();
		peerData.setPeerName(getName());
		peerData.setHostIdentity(getName());
		peerData.setSecurityStandard(SecurityStandard.NONE);
		peerData.setRemoteIPAddress("localhost");
		peerData.setInitiateConnectionDuration(0);
		peerData.setWatchdogInterval(0);
		
		try {
			this.virtualInputStream = this.serverContext.registerVirtualPeer(peerData, 
				new VirtualOutputStream() {

				@Override
				public void send(Packet diaPacket) {
					DiameterPacket packet = (DiameterPacket) diaPacket;

					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(getModule(), "Diameter Answer received with HbH-ID=" + packet.getHop_by_hopIdentifier() + 
								", Session-ID="+ packet.getAVPValue(DiameterAVPConstants.SESSION_ID));
					}

					HSSRequestHandler handler = hbhToResponseHandlerMap.remove(packet.getHop_by_hopIdentifier());
					if(handler == null){
						if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
							LogManager.getLogger().error(getModule(), "Dropping Response with HbH-ID=" + packet.getHop_by_hopIdentifier() + 
									", Reason: Handler not found for Response, Possibly Request Timed Out or Unknown Response.");
						return;
					}
					handler.responseReceived((DiameterAnswer) packet);
				}
			});
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(getModule(), "Diameter Virtual Peer initialized successfully for Driver: " + getName()); 
			}
		} catch (ElementRegistrationFailedException e) {
			LogManager.getLogger().trace(getModule(),e);
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(getModule(), "Error while registering Virtual Peers reason: "+ e.getMessage());
			throw new DriverInitializationFailedException(e);
		}
	}

	private class HSSRequestHandler{

		private ServiceRequest serviceRequest;
		private DiameterRequest diameterRequest;
		private AccountData accountData;
		
		private ReentrantLock waitForResponselock = new ReentrantLock();;
		private Condition processDone = waitForResponselock.newCondition(); 
		private boolean done;
		
		public HSSRequestHandler(ServiceRequest originRequest, DiameterRequest destRequest) {
			this.serviceRequest = originRequest;
			this.diameterRequest = destRequest;
		}

		public void responseReceived(DiameterAnswer diameterAnswer) {

			accountData = buildAccountData(serviceRequest, diameterAnswer);

			waitForResponselock.lock();
			done = true;
			processDone.signal();
			if(waitForResponselock.isLocked())
				waitForResponselock.unlock();
		}
		
		public AccountData getAccountData(){
			return accountData;
		}

		private void handleRequest() {
			
			TimeoutHandler timeoutHandler = new TimeoutHandler(diameterRequest.getHop_by_hopIdentifier());
			serverContext.getTaskScheduler().scheduleSingleExecutionTask(timeoutHandler);
			
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(getModule(), "Sending Diameter Request with HbH-ID= " + diameterRequest.getHop_by_hopIdentifier()+ 
						", Session-ID="+ diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
			}
			//Send to Virtual Peer
			HSSAuthDriver.this.virtualInputStream.received(diameterRequest);
			
			waitForResponselock.lock();
			try {
				if(!done){
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(getModule(), "Waiting for Response with Session-ID="+ 
								diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
					}
					processDone.await();
				}

			} catch(InterruptedException e) {
			} finally {
				if(waitForResponselock.isHeldByCurrentThread())
					waitForResponselock.unlock();
			}
		}

		public void requestTimedOut() {

			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(getModule(), "Marked Request for Drop, Reason: Time Out occured for Request with HbH-ID="+ 
						diameterRequest.getHop_by_hopIdentifier());
			}
			waitForResponselock.lock();
			done=true;
			processDone.signal();
			if(waitForResponselock.isHeldByCurrentThread())
				waitForResponselock.unlock();
		}
	}
	
	private class TimeoutHandler implements SingleExecutionAsyncTask{
	
		private int hopByHopId;

		public TimeoutHandler(int hopByHopId) {
			this.hopByHopId = hopByHopId;
	}

	@Override
		public long getInitialDelay() {
			return hssAuthDriverConf.getRequestTimeout();
	}

	@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.MILLISECONDS;
	}

		@Override
		public void execute(AsyncTaskContext context) {
			HSSRequestHandler handler = hbhToResponseHandlerMap.remove(hopByHopId);
			if(handler != null){
				handler.requestTimedOut();
			}			
		}
	
	}
	
	private AccountDataValueProvider getAccountDataValueProvider(DiameterAnswer response) {
		return new HSSAuthAccountDataValueProvider(response);
	}

	@Override
	protected int getStatusCheckDuration() {
		return ALWAYS_ALIVE;
	}
	
	private class HSSAuthAccountDataValueProvider implements AccountDataValueProvider {

		DiameterAnswer hssResponse;

		public HSSAuthAccountDataValueProvider(DiameterAnswer hssResponse) {
			this.hssResponse = hssResponse;
		}

		@Override
		public String getStringValue(String fieldName) {
			IDiameterAVP attribute = hssResponse.getAVP(fieldName, true);
			if (attribute != null) {
				return attribute.getStringValue();
			}
			return null;
		}

		@Override
		public Date getDateValue(String fieldName) {
			IDiameterAVP attribute = hssResponse.getAVP(fieldName, true);
			if(attribute == null)
				return null;
			
			java.sql.Date date = new java.sql.Date(Long.parseLong(attribute.getStringValue()));	
			return date;
		}
	}

}
