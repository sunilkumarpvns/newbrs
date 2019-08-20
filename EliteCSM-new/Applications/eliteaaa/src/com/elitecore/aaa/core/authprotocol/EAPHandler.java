package com.elitecore.aaa.core.authprotocol;

import java.security.PrivateKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.elitecore.aaa.core.conf.EAPConfigurationData;
import com.elitecore.aaa.core.conf.EAPConfigurationData.VendorSpecificCertificate;
import com.elitecore.aaa.core.eap.util.TemporaryIdentityGeneratorImpl;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.tls.TrustedCAConfiguration;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.coreeap.cipher.providers.DefaultCipherProvider;
import com.elitecore.coreeap.cipher.providers.constants.CipherSuites;
import com.elitecore.coreeap.commons.configuration.IEapConfigurationContext;
import com.elitecore.coreeap.commons.util.TemporaryIdentityGenerator;
import com.elitecore.coreeap.commons.util.cipher.ICipherProvider;
import com.elitecore.coreeap.data.AAAEapRespData;
import com.elitecore.coreeap.data.ICustomerAccountInfo;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.fsm.eap.EapStateMachine;
import com.elitecore.coreeap.fsm.eap.IEapStateMachine;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.fsm.IEnum;
import com.elitecore.coreeap.util.constants.fsm.events.EapEvents;
import com.elitecore.coreeap.util.tls.KeyExchangeFactory;
import com.elitecore.coreeap.util.tls.SignatureFactory;
import com.elitecore.coreradius.commons.util.RadiusUtility;

public class EAPHandler{
	public static final String MODULE = "EAP-Handler";
	
	private static final String REQUEST_TIMEOUT_FOR_SIM = "request-timeout-for-sim";  	
	private static final String REQUEST_TIMEOUT_FOR_AKA= "request-timeout-for-aka";
	private static final String MAPGW_CONNECTIONPOOL_SIZE_FOR_SIM = "mapgw-connection-pool-size-for-sim";
	private static final String MAPGW_CONNECTIONPOOL_SIZE_FOR_AKA = "mapgw-connection-pool-size-for-aka";
	private static final String MAX_SIM_REAUTH_COUNT = "max-sim-reauth-count";
	
	private boolean bSessionManagement = true;	
	private IEapConfigurationContext eapConfigurationContext;
	private EAPConfigurationData eapConfig;
	private ServiceContext serviceContext;	
	private IEapStateMachine eapStateMachine ;
	private String eapSessionId ;	
	private int eapAttributeID;
	private boolean isTestMode = false;
	private SignatureFactory signatureFactory;
	private KeyExchangeFactory keyExchangeFactory;
	
	public EAPHandler(ServiceContext serviceContext ,EAPConfigurationData eapConfiguration,int eapAttributeID, boolean isTestMode){
		this.serviceContext=serviceContext;
		this.eapConfig = eapConfiguration;		
		this.eapAttributeID  = eapAttributeID;
		this.isTestMode = isTestMode;
		this.signatureFactory = new SignatureFactory(isTestMode);
		this.keyExchangeFactory = new KeyExchangeFactory(isTestMode);
	}

	public void init() throws InitializationFailedException{		
		
		List<Integer> certificateTypes = eapConfig.getTLSConfiguration().getCertificateConfiguration().getCertificateTypes();
		int[] types = new int[certificateTypes.size()];
		int k=0;
		for(int i:certificateTypes){
			types[k++]=i;
		}
		
		/*
		 * Fetching trusted CA Certificates from database as well as from the system/cert/trustedcertificates
		 */
		TrustedCAConfiguration trustedCAConfig = ((AAAServerContext)(serviceContext.getServerContext())).getServerConfiguration().getTrustedCAConfiguration();
		final List<X509Certificate> trustedX509CertificateList = trustedCAConfig.getCACertificates();
		

		/* Fetching vendor certificates
		 */
		final List<VendorSpecificCertificate> vendorSpecificCertList = eapConfig.getTLSConfiguration().getCertificateConfiguration().getVendorSpecificCertList();
		

		/*
		 * Fetching all the CRLs 
		 */
		final List<X509CRL> x509CRLList = ((AAAServerContext)(serviceContext.getServerContext())).getServerConfiguration().getCRLConfiguration().getCRLs();
		
		final int[] supportedCertificateTypes = types;
		final TemporaryIdentityGenerator identityGenerator = new TemporaryIdentityGeneratorImpl();
		final List<Integer> enabledEAPMethodList = eapConfig.getEnableAuthMethods();
		
		eapConfigurationContext = new IEapConfigurationContext(){
			
			@Override
			public List<byte[]> getServerCertificateChain() {
				return eapConfig.getTLSConfiguration().getCertificateConfiguration().getServerCertificateChain();
			}
			
			@Override
			public List<byte[]> getServerCertificateChain(String oui) {
				List<byte[]> certificateChainList = Collections.emptyList();
				for (EAPConfigurationData.VendorSpecificCertificate vendorCert : vendorSpecificCertList) {
					if(RadiusUtility.matches(oui, vendorCert.getOui())) {
						certificateChainList = vendorCert.getServerCertificateChain();
						break;
					}
				}
				return certificateChainList;
			}
			
			public ICipherProvider getCipherProvider(CipherSuites cipherSuite, ProtocolVersion protocolVersion) {				
				return new DefaultCipherProvider(cipherSuite, protocolVersion, eapConfigurationContext.isTestMode());
			}

			public int getFragmentSize() {
				return eapConfig.getMaxEapPacketSize();			
			}

			public ProtocolVersion getMinProtocolVersion() {
				return eapConfig.getTLSConfiguration().getMinProtocolVersion();
			}
			
			public ProtocolVersion getMaxProtocolVersion() {
				return eapConfig.getTLSConfiguration().getMaxProtocolVersion();
			}
			
			public PrivateKey getServerPrivateKey() {
				return eapConfig.getTLSConfiguration().getCertificateConfiguration().getPrivateKey();
			}

			public int[] getSupportedCertificateTypes() {
				return supportedCertificateTypes;
			}

			public List<X509Certificate> getTrustedX509CertificatesList() {
				return trustedX509CertificateList;
			}

			public List<Integer> getSupportedCiphersuiteIDs() {
				return eapConfig.getTLSConfiguration().getCipherSuiteIDs();					
			}

			public boolean isTLSSendCertificateRequest() {
				return eapConfig.getTLSConfiguration().getIsTlsCertificateRequest();
			}

			public boolean isTTLSSendCertificateRequest() {
				return eapConfig.getIsTTLSCertificateRequest();
			}

			public boolean isTestMode() {
				return isTestMode;
			}

			public int getDefaultNegotiationMethod() {
				return eapConfig.getDefaultNegotiationMethod();
			}

			public List<X509CRL> getCRLList() {
				return x509CRLList;
			}

			public List<Integer> getEnabledAuthMethods() {
				return enabledEAPMethodList;
			}
			public int getSessionResumptionLimit() {
				return eapConfig.getTLSConfiguration().getSessionResumptionLimit();
			}

			public PrivateKey getServerPrivateKey(String oui) {
				PrivateKey key = null;
				for (EAPConfigurationData.VendorSpecificCertificate vendorCert : vendorSpecificCertList) {
					if(RadiusUtility.matches(oui, vendorCert.getOui())) {
						key = vendorCert.getVendorPrivateKey();
						break;
					}
				}
				return key;
			}

			public int getNoOfSIMTriplets() {
				return eapConfig.getSIMConfiguration().getNumberOfTriplet();
			}

			public int getTripletDS() {
				return eapConfig.getSIMConfiguration().getTripletDS();
			}

			public int getTripletDSForAKA() {
				return eapConfig.getAKAConfiguration().getQuintupletDS();
			}

			public String getLocalHostForAKA() {
				return eapConfig.getAKAConfiguration().getLocalHost();
			}

			public String getRemoteHostForAKA() {
				return eapConfig.getAKAConfiguration().getRemoteHost();
			}

			public String getLocalHostForSIM() {
				return eapConfig.getSIMConfiguration().getLocalHost();
			}

			public String getRemoteHostForSIM() {
				return eapConfig.getSIMConfiguration().getRemoteHost();
			}

			public boolean isPEAPSendCertificateRequest() {
				return eapConfig.getIsPEAPCertificateRequest();
			}

			public int getPEAPDefaultNegotiationMethod() {
				return eapConfig.getDefaultPEAPNegotiationMethod();
			}

			public int getTTLSDefaultNegotiationMethod() {
				return eapConfig.getDefaultTTLSNegotiationMethod();
			}

			@Override
			public long getRequestTimeoutForSIM() {
				long timeout = 1000;
				try {
					timeout = Numbers.parseLong(System.getProperty(REQUEST_TIMEOUT_FOR_SIM), timeout);
				} catch (NumberFormatException e){
					LogManager.getLogger().warn(MODULE, "Invalid Request Timeout Configured for SIM. Default Timeout: " + timeout + " will be considered.");
				}
				return timeout; 
			}

			@Override
			public long getRequestTimeoutForAKA() {
				long timeout = 1000;
				try {
					timeout = Numbers.parseLong(System.getProperty(REQUEST_TIMEOUT_FOR_AKA),timeout);
				} catch (NumberFormatException e){
					LogManager.getLogger().warn(MODULE, "Invalid Request Timeout Configured for AKA. Default Timeout: " + timeout + " will be considered.");
				}
				return timeout; 
			}

			@Override
			public int getMAPGWConnectionPoolSizeForSIM() {
				int poolSize = 20;
				try {
					poolSize = Numbers.parseInt(System.getProperty(MAPGW_CONNECTIONPOOL_SIZE_FOR_SIM), poolSize);
				} catch (NumberFormatException e){
					LogManager.getLogger().warn(MODULE, "Invalid Map GW Connection pool size Configured for SIM. Default Timeout: " + poolSize + " will be considered.");
				}
				return poolSize;
			}

			@Override
			public int getMAPGWConnectionPoolSizeForAKA() {
				int poolSize = 20;
				try {
					poolSize = Numbers.parseInt(System.getProperty(MAPGW_CONNECTIONPOOL_SIZE_FOR_AKA), poolSize);
				} catch (NumberFormatException e){
					LogManager.getLogger().warn(MODULE, "Invalid Map GW Connection pool size Configured for AKA. Default Timeout: " + poolSize + " will be considered.");
				}
				return poolSize;
			}

			@Override
			public TemporaryIdentityGenerator getTemporaryIdentityGenerator() {
				return identityGenerator;
			}

			@Override
			public int getPseudoIdentityMethod(int eapMethod) {
				if (eapMethod == EapTypeConstants.SIM.typeId){
					return eapConfig.getSIMConfiguration().getPseudoGenMethod();
				} else if (eapMethod == EapTypeConstants.AKA.typeId){
					return eapConfig.getAKAConfiguration().getPseudonymGenMethod();
				}
				return 0;
			}

			@Override
			public String getPseudoIdentityPrefix(int eapMethod) {
				if (eapMethod == EapTypeConstants.SIM.typeId){
					return eapConfig.getSIMConfiguration().getPseudoPrefix();
				} else if (eapMethod == EapTypeConstants.AKA.typeId){ 
					return eapConfig.getAKAConfiguration().getPseudonymPrefix();
				}
				return null;
			}

			@Override
			public boolean isPseudoHexEncodingSupported(int eapMethod) {
				if (eapMethod == EapTypeConstants.SIM.typeId){
					return eapConfig.getSIMConfiguration().getIsPseudoHexEncoding();
				} else if (eapMethod == EapTypeConstants.AKA.typeId){
					return eapConfig.getAKAConfiguration().getIsPseudoHexEncoding();
				}
				return true;
			}

			@Override
			public int getFastReauthIdentityMethod(int eapMethod) {
				if (eapMethod == EapTypeConstants.SIM.typeId){
					return eapConfig.getSIMConfiguration().getFastReauthGenMethod();
				} else if (eapMethod == EapTypeConstants.AKA.typeId){
					// TODO
				}
				return 0;
			}

			@Override
			public String getFastReauthIdentityPrefix(int eapMethod) {
				if (eapMethod == EapTypeConstants.SIM.typeId){
					return eapConfig.getSIMConfiguration().getFastReauthPrefix();
				} else if (eapMethod == EapTypeConstants.AKA.typeId){ 
					//TODO
				}
				return null;
			}

			@Override
			public boolean isFastReauthHexEncodingSupported(int eapMethod) {
				if (eapMethod == EapTypeConstants.SIM.typeId){
					return eapConfig.getSIMConfiguration().getIsFastReauthHexEncoding();
				} else if (eapMethod == EapTypeConstants.AKA.typeId){
					//TODO
				}
				return true;
			}

			@Override
			public int getEAPAttributeID() {
				return eapAttributeID;
			}


			@Override
			public KeyExchangeFactory getKeyExchangeFactory() {
				return keyExchangeFactory;
			}

			@Override
			public SignatureFactory getSignatureFactory() {
				return signatureFactory;
			}

			@Override
			public boolean isValidateCertificateExpiry() {
				return eapConfig.getTLSConfiguration().getValidateCertificateExpiry();
			}

			@Override
			public boolean isValidateCertificateRevocation() {
				return eapConfig.getTLSConfiguration().getValidateCertificateRevocation();
			}

			@Override
			public boolean isValidateMac() {
				return eapConfig.getTLSConfiguration().getValidateMac();
			}

			@Override
			public boolean isValidateClientCertificate() {
				return eapConfig.getTLSConfiguration().getValidateClientCertificate();
			}
			
			@Override
			public int getMaxSIMReauthCount() {
				int count = 10;
				try {
					count = Numbers.parseInt(System.getProperty(MAX_SIM_REAUTH_COUNT), 10);
					
					if (count < 0) {
						count = 10;
						if (LogManager.getLogger().isWarnLogLevel()) {
							LogManager.getLogger().warn(MODULE, "Invalid maximum reauthentication count Configured for SIM. Default Reauth Count: " + count + " will be considered.");
						}
					}
				} catch (NumberFormatException e){
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
						LogManager.getLogger().warn(MODULE, "Invalid maximum reauthentication count Configured for SIM. Default Reauth Count: " + count + " will be considered.");
					}
				}
				return count;
			}
			
			};

			EAPSessionCleanupExecutionTask eapCleanupTask = new EAPSessionCleanupExecutionTask(eapConfig.getSessionDurationForCleanup(),eapConfig.getSessionCleanupInterval());
			serviceContext.getServerContext().getTaskScheduler().scheduleIntervalBasedTask(eapCleanupTask);
			
	}

	class EAPSessionCleanupExecutionTask extends BaseIntervalBasedTask{

		private long initialDelay;
		private long intervalSeconds;
		
		public EAPSessionCleanupExecutionTask(long initialDelay,long intervalSeconds){
			this.initialDelay = initialDelay;
			this.intervalSeconds = intervalSeconds;
		}
		
		@Override
		public long getInitialDelay() {
			return initialDelay;
		}

		@Override
		public boolean isFixedDelay() {
			return true;
		}

		@Override
		public long getInterval() {
			return (int)intervalSeconds;
		}

		@Override
		public void execute(AsyncTaskContext context) {
			int noOfSessions = ((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().getEapSessions().size();				
			Set<String> eapSessionIds = ((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().getEapSessions().keySet();
			ArrayList<String> temp = new ArrayList<String>();
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Total number of EAPSessions : "+noOfSessions);			

			//original process
			synchronized (((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().getEapSessions()) {
				Iterator<String> itr = eapSessionIds.iterator();
				while(itr.hasNext()){
					String eapSessionId = itr.next();
					long timeElapsed = System.currentTimeMillis() - getStartUpTimeStamp(eapSessionId);
					if(timeElapsed > (1000 * eapConfig.getSessionDurationForCleanup())){ 
						try{						
							temp.add(eapSessionId);																		
						}catch(Exception e){
							if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(MODULE, "Problem while removing eap session for " + eapSessionId);							
							LogManager.getLogger().trace(MODULE, e);
						}
					}				
				}									
				((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().removeEAPSession(temp);																		

				if((noOfSessions - ((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().getEapSessions().size())>0){
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO))				
						LogManager.getLogger().info(MODULE, "Number of sessions removed is : "+(noOfSessions - ((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().getEapSessions().size()));			
				}
			}
		}
		
	}
				
	public IEnum handleRequest(AAAEapRespData aaaEapRespData,String eapSessionId,ICustomerAccountInfoProvider provider) throws EAPException{
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Eap Response Received: \n" + aaaEapRespData.getEapRespPacket());
		IEapStateMachine eapStateMachine;
		if(isSessionManagement()){
			eapStateMachine = ((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().getEAPStateMachine(eapSessionId, this.eapConfigurationContext);
		}else{
			if(this.eapSessionId == null || !this.eapSessionId.equalsIgnoreCase(eapSessionId)){
				this.eapSessionId = eapSessionId;
				this.eapStateMachine = new EapStateMachine(eapConfigurationContext);
			}
			eapStateMachine = this.eapStateMachine;
		}
		
		IEnum event = eapStateMachine.processRequest(aaaEapRespData,provider);
		
		if(event != EapEvents.EapSuccessEvent){
			eapStateMachine.clearAccountInfoProvider();
			eapStateMachine.clearCustomerAccountInfo();
		} else {
			if (eapStateMachine.getSessionIdentities() != null){
				String[] sessionIdentities = eapStateMachine.getSessionIdentities();
				for (int i=0 ; i<sessionIdentities.length ; i++){
					if (sessionIdentities[i] != null){
						if (sessionIdentities[i].indexOf('@') != -1){
							((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().addEAPSession(eapStateMachine, sessionIdentities[i].substring(0, sessionIdentities[i].indexOf('@')));
						} else {
							((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().addEAPSession(eapStateMachine, sessionIdentities[i]);
						}
					}
				}
			}
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Eap Response has been processed for Session id :" + eapSessionId);
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))	
			LogManager.getLogger().debug(MODULE, "Event  :" + event);
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Eap packet generated after processing: \n" + aaaEapRespData.getEapReqPacket());
		
		return event;
	}

	public String getIdentity(String sessionId){
		IEapStateMachine eapStateMachine = null;
		if(isSessionManagement()){
			if(((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().isSessionExist(sessionId)){				
				eapStateMachine = ((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().getEAPStateMachine(sessionId, eapConfigurationContext);
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Eap Session does not exist For Session Id : " + sessionId);
				return null;
			}
		}else{
			if(this.eapSessionId == null || !this.eapSessionId.equalsIgnoreCase(eapSessionId)){				
				return null;		
			}
			eapStateMachine = this.eapStateMachine;
		}
		return eapStateMachine.getIdentity();
	}

	public int getAuthenticationMethod(String sessionId) {
		IEapStateMachine eapStateMachine = null;
		if(isSessionManagement()){
			if(((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().isSessionExist(sessionId)){				
				eapStateMachine = ((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().getEAPStateMachine(sessionId, eapConfigurationContext);
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Eap Session does not exist For Session Id : " + sessionId);
				return 0;
			}
		}else{
			if(this.eapSessionId == null || !this.eapSessionId.equalsIgnoreCase(eapSessionId)){				
				return 0;		
			}
			eapStateMachine = this.eapStateMachine;
		}
		return eapStateMachine.getCurrentMethod();		
	}

	public long getStartUpTimeStamp(String sessionId) {
		IEapStateMachine eapStateMachine = null;
		if(isSessionManagement()){
			if(((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().isSessionExist(sessionId)){				
				eapStateMachine = ((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().getEAPStateMachine(sessionId,eapConfigurationContext);
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Eap Session does not exist For Session Id : " + sessionId);
				return 0;
			}
		}else{
			if(this.eapSessionId == null || !this.eapSessionId.equalsIgnoreCase(eapSessionId)){				
				return 0;		
			}
			eapStateMachine = this.eapStateMachine;
		}
		return eapStateMachine.getStartTimeStamp();		
	}

	public ICustomerAccountInfo getCustomerAccountInfo(String sessionId) {
		IEapStateMachine eapStateMachine = null;
		if(isSessionManagement()){
			if(((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().isSessionExist(sessionId)){				
				eapStateMachine = ((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().getEAPStateMachine(sessionId, eapConfigurationContext);
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Eap Session does not exist For Session Id : " + sessionId);
				return null;
			}
		}else{
			if(this.eapSessionId == null || !this.eapSessionId.equalsIgnoreCase(eapSessionId)){				
				return null;		
			}
			eapStateMachine = this.eapStateMachine;
		}
		if (isIdentityCached(eapStateMachine)) {
			return eapStateMachine.getCustomerAccountInfo(eapStateMachine.getUserIdentity());
		}
		return eapStateMachine.getCustomerAccountInfo();		
	}

	private boolean isIdentityCached(IEapStateMachine eapStateMachine) {
		return eapStateMachine.getUserIdentity() != null && eapStateMachine.getUserIdentity().trim().length() > 0;
	}

	public boolean isDuplicateRequest(String sessionId){
		IEapStateMachine eapStateMachine = null;
		if(isSessionManagement()){
			if(((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().isSessionExist(sessionId)){				
				eapStateMachine = ((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().getEAPStateMachine(sessionId, eapConfigurationContext);
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Eap Session does not exist For Session Id : " + sessionId);
				return false;
			}
		}else{
			if(this.eapSessionId == null || !this.eapSessionId.equalsIgnoreCase(eapSessionId)){				
				return false;		
			}
			eapStateMachine = this.eapStateMachine;
		}
		return eapStateMachine.isDuplicateRequest();
	}

	public String getFailureReason(String sessionId){
		IEapStateMachine eapStateMachine = null;
		if(isSessionManagement()){
			if(((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().isSessionExist(sessionId)){				
				eapStateMachine = ((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().getEAPStateMachine(sessionId,eapConfigurationContext);
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Eap Session does not exist For Session Id : " + sessionId);
				return null;
			}
		}else{
			if(this.eapSessionId == null || !this.eapSessionId.equalsIgnoreCase(eapSessionId)){				
				return null;		
			}
			eapStateMachine = this.eapStateMachine;
		}
		return eapStateMachine.getFailureReason();	
	}
	public boolean validateMAC(String sessionId,String macValue){
		IEapStateMachine eapStateMachine = null;
		if(isSessionManagement()){
			if(((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().isSessionExist(sessionId)){				
				eapStateMachine = ((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().getEAPStateMachine(sessionId, this.eapConfigurationContext);
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Eap Session does not exist For Session Id : " + sessionId);
				return false;
			}
		}else{
			if(this.eapSessionId == null || !this.eapSessionId.equalsIgnoreCase(eapSessionId)){				
				return false;		
			}
			eapStateMachine = this.eapStateMachine;
		}
		return eapStateMachine.validateMAC(macValue);
	}

	public boolean isSessionManagement() {
		return bSessionManagement;
	}

	public void setSessionManagement(boolean sessionManagement) {
		bSessionManagement = sessionManagement;
	}

	public IEapConfigurationContext getEapContext() {
		return eapConfigurationContext;
	}

	public byte[] getLastEAPRequestBytes(String sessionId){
		IEapStateMachine eapStateMachine = null;
		if(isSessionManagement()){
			eapStateMachine =((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().getEAPStateMachine(sessionId, this.eapConfigurationContext);
			if(eapStateMachine==null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Eap Session does not exist For Session Id : " + sessionId);
					return null;
			}
		}else{
			if(this.eapSessionId == null || !this.eapSessionId.equalsIgnoreCase(eapSessionId)){				
				return null;		
			}
			eapStateMachine = this.eapStateMachine;
		}
		return eapStateMachine.getLastEAPRequestBytes();
	}

	public String getIgnoreReason(String sessionId){
		IEapStateMachine eapStateMachine = null;
		if(isSessionManagement()){
			eapStateMachine =((AAAServerContext)serviceContext.getServerContext()).getEapSessionManager().getEAPStateMachine(sessionId, this.eapConfigurationContext);
			if(eapStateMachine==null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Eap Session does not exist For Session Id : " + sessionId);
					return null;
			}
		}else{
			if(this.eapSessionId == null || !this.eapSessionId.equalsIgnoreCase(eapSessionId)){				
				return null;		
			}
			eapStateMachine = this.eapStateMachine;
		}
		return eapStateMachine.getIgnoreReason();
	}
	public boolean isTreatInvalidPacketAsFatal() {
		return eapConfig.getIsTreatInvalidPacketAsFatal();
	}
	
	public void reInit(EAPConfigurationData eapConfiguration){
		this.eapConfig = eapConfiguration;
	}
}

