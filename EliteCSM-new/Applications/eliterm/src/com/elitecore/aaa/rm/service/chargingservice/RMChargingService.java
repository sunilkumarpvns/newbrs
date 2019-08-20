package com.elitecore.aaa.rm.service.chargingservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.conf.impl.RMServerConfigurationImpl;
import com.elitecore.aaa.core.drivers.DriverManager;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.util.cli.RadiusLogMonitor;
import com.elitecore.aaa.core.util.cli.SetCommand;
import com.elitecore.aaa.core.util.cli.SetCommand.ConfigurationSetter;
import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.aaa.mibs.rm.chargingservice.server.RMChargingServiceMIBListener;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.plugins.core.RadPluginManager;
import com.elitecore.aaa.radius.plugins.core.RadPluginRequestHandler;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.base.BaseRadiusService;
import com.elitecore.aaa.radius.service.base.RadServiceRequestImpl;
import com.elitecore.aaa.radius.service.base.RadServiceResponseImpl;
import com.elitecore.aaa.rm.conf.RMChargingServiceConfiguration;
import com.elitecore.aaa.rm.policies.RMChargingPolicy;
import com.elitecore.aaa.rm.policies.conf.impl.RMChargingPolicyConfigurationImpl;
import com.elitecore.aaa.rm.service.chargingservice.snmp.CHARGING_SERVICE_MIBImpl;
import com.elitecore.aaa.rm.service.chargingservice.snmp.ChargingClientEntryMBeanImpl;
import com.elitecore.aaa.rm.service.chargingservice.snmp.TableChargingClientStatsTableImpl;
import com.elitecore.aaa.rm.service.chargingservice.snmp.autogen.CHARGING_SERVICE_MIB;
import com.elitecore.aaa.rm.service.chargingservice.snmp.autogen.TableChargingClientStatsTable;
import com.elitecore.aaa.rm.translator.RadiusToCrestelOCSv2Translator;
import com.elitecore.aaa.rm.translator.RadiusToDiameterTranslator;
import com.elitecore.aaa.rm.translator.RadiusToMapTranslator;
import com.elitecore.aaa.rm.translator.RadiusToRatingTranslator;
import com.elitecore.aaa.rm.util.cli.ChargingServCommand;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.AsyncRequestExecutor;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.util.cli.cmd.ICommand;
import com.elitecore.core.util.logger.EliteRollingFileLogger;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.diameterapi.core.common.PolicyDataRegistrationFailedException;
import com.elitecore.diameterapi.core.translator.TranslationAgent;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorPolicyData;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.sun.management.snmp.SnmpStatusException;

public class RMChargingService extends BaseRadiusService<RMChargingRequest, RMChargingResponse> {

	private static final String MODULE = "RM-CHRGN-SRVC";
	private RMChargingServiceConfiguration serviceConfiguration;
	private RMChargingServiceContext serviceContext;
	private AAAServerContext serverContext;
	private DriverManager driverManager = null;
	private List<RMChargingPolicy> servicePolicies;
	private RMChargingServiceMIBListener chargingServiceMIBListener;
	protected static final String SYSTEM_PATH = "system";
	private static String SYS_ORIGIN_STATE_ID_FILE = "_sys.originstateid";
	int count = 1;
	
	private CHARGING_SERVICE_MIB charging_service_mib;
	private EliteRollingFileLogger serviceLogger;
	private static final String CHARGING_LOG_FILE_PREFIX = "rm-charging";
	
	public RMChargingService(AAAServerContext serverContext, 
			DriverManager driverManager,
			RadPluginManager pluginManager,
			RadiusLogMonitor logMonitor) {
		super(serverContext, driverManager, pluginManager, logMonitor);
		this.serverContext = serverContext;
		this.serviceConfiguration =  ((RMServerConfigurationImpl)serverContext.getServerConfiguration()).getRmChargingServiceConfiguration();
		this.servicePolicies = new ArrayList<RMChargingPolicy>();
		chargingServiceMIBListener = new RMChargingServiceMIBListener(new RMChargingServiceMIBCounters(),serverContext.getServerConfiguration().getRadClientConfiguration());
		charging_service_mib = new CHARGING_SERVICE_MIBImpl(chargingServiceMIBListener);
	}
	
	
	@Override
	protected void initService() throws ServiceInitializationException{
		serviceContext = new RMChargingServiceContext() {

			@Override
			public AAAServerContext getServerContext() {				
				return serverContext;
			}
			@Override
			public String getServiceIdentifier() {
				return  RMChargingService.this.getServiceIdentifier();
			}
			@Override
			public ESCommunicator getDriver(String driverInstanceId) { 
				return getRadiusDriver(driverInstanceId);
			}
			@Override
			public RMChargingServiceConfiguration getChargingConfiguration() {
				return serviceConfiguration;
			}
			@Override
			public RadPluginRequestHandler createPluginRequestHandler(
					List<PluginEntryDetail> prePluginList, List<PluginEntryDetail> postPluginList) {
				return createRadPluginReqHandler(prePluginList, postPluginList);
			}			
			@Override
			public void submitAsyncRequest(RMChargingRequest serviceRequest,
					RMChargingResponse serviceResponse,
					AsyncRequestExecutor<RMChargingRequest, RMChargingResponse> requestExecutor){
				RMChargingService.this.submitAsyncRequest(serviceRequest, serviceResponse, requestExecutor);
			}
		};
		
		// To Read Origin State Id Avp
		Parameter.getInstance().setOriginStateId(readOriginStateId());

		super.initService();

		LogManager.getLogger().info(MODULE, String.valueOf(serviceConfiguration)); 
		if (serviceConfiguration.isServiceLevelLoggerEnabled()) {
			serviceLogger = 
				new EliteRollingFileLogger.Builder(getServerContext().getServerInstanceName(),
						getServerContext().getServerHome() + File.separator
							+ "logs" + File.separator + CHARGING_LOG_FILE_PREFIX)
				.rollingType(serviceConfiguration.logRollingType())
				.rollingUnit(serviceConfiguration.logRollingUnit())
				.maxRolledUnits(serviceConfiguration.logMaxRolledUnits())
				.compressRolledUnits(serviceConfiguration.isCompressLogRolledUnits())
				.sysLogParameters(serviceConfiguration.getSysLogConfiguration().getHostIp(),
						serviceConfiguration.getSysLogConfiguration().getFacility())
				.build();
			serviceLogger.setLogLevel(serviceConfiguration.logLevel());
			LogManager.setLogger(getServiceIdentifier(), serviceLogger);
		}
		
		/* Registering Translator */				
		TranslationAgent.getInstance().registerTranslator(new RadiusToMapTranslator(getServerContext()));
		TranslationAgent.getInstance().registerTranslator(new RadiusToDiameterTranslator(getServerContext()));
		TranslationAgent.getInstance().registerTranslator(new RadiusToRatingTranslator(getServerContext()));
		TranslationAgent.getInstance().registerTranslator(new RadiusToCrestelOCSv2Translator(getServerContext()));
		
		registerTranslationMappingPolicyData();
		
		initServiecpolicies();
		registerServiceSummaryWriter(new RMChargingServiceSummaryWriter(serviceContext,this.chargingServiceMIBListener));
		
		RMChargingConfigurationSetter chargingConfigurationSetter = new RMChargingConfigurationSetter(serviceContext);
		SetCommand.registerConfigurationSetter(chargingConfigurationSetter);
		
		initChargingListenerAndMIB();
		
		LogManager.getLogger().info(MODULE,"RM Charging service initialized successfully.");
	}

	private void initChargingListenerAndMIB() {
		
		chargingServiceMIBListener.init();

		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		
		try {
			charging_service_mib.populate(mBeanServer, null);
			List<String> clientData = serverContext.getServerConfiguration().getRadClientConfiguration().getClientAddresses();
			
			if(clientData != null && !(clientData.isEmpty())){
				registerChargingClientData(mBeanServer, clientData);
			}

			serverContext.registerSnmpMib(charging_service_mib);
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Failed to initialize CHARGING_SERVICE_MIB. Reason: "+e.getMessage());
				LogManager.getLogger().trace(e);
			}
		}
	}


	private void registerChargingClientData(MBeanServer mBeanServer, List<String> clientData) {
		TableChargingClientStatsTable chargingClientTable = new TableChargingClientStatsTableImpl(charging_service_mib,mBeanServer);
		int clientIndexForTableEntry = 1;
		String clientAddress = "";
		try{
			for (int clientIndex = 0; clientIndex < clientData.size(); clientIndex++) {
				clientAddress = clientData.get(clientIndex);
				ChargingClientEntryMBeanImpl clientEntry = new ChargingClientEntryMBeanImpl(clientIndexForTableEntry,clientAddress, chargingServiceMIBListener);
				chargingClientTable.addEntry(clientEntry,new ObjectName(clientEntry.getObjectName()));

				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Entry for Charging Client: " + clientAddress + " successfully added at Index: " + (clientIndexForTableEntry));
				}
				clientIndexForTableEntry++;
			}
		} catch (SnmpStatusException sse) {
			LogManager.getLogger().error(MODULE, "Error while adding Entry for Charging Client: " + clientAddress + " in charging client table, Reason: "+sse.getMessage());
			LogManager.getLogger().trace(sse);
		} catch (MalformedObjectNameException ex) {
			LogManager.getLogger().error(MODULE, "Error while adding Entry for Charging Client: " + clientAddress + " in charging client table, Reason: "+ex.getMessage());
			LogManager.getLogger().trace(ex);
		}
	}
	
	private void registerTranslationMappingPolicyData(){
		/* Registering Translator policies */
		Map<String, TranslatorPolicyData> policyData = serverContext.getServerConfiguration().getTranslationMappingConfiguration().getTranslatorPolicyDataMap();
		for(Entry<String,TranslatorPolicyData> entry: policyData.entrySet()){
			try {
				TranslatorPolicyData translatorPolicyData = entry.getValue();
				if( (translatorPolicyData.getFromTranslatorId().equalsIgnoreCase(AAATranslatorConstants.RADIUS_TRANSLATOR))  
						&&
					isSupportedOutgoingTranslation(translatorPolicyData)) {
					
					TranslationAgent.getInstance().registerPolicyData(entry.getValue());
				}
				
			} catch (PolicyDataRegistrationFailedException e) {
				LogManager.getLogger().error(MODULE, "PolicyData Registraton Failed for :" + entry.getValue().getTransMapConfId() + " reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}


	private boolean isSupportedOutgoingTranslation(TranslatorPolicyData translatorPolicyData) {
		return AAATranslatorConstants.MAP_TRANSLATOR.equalsIgnoreCase(translatorPolicyData.getToTranslatorId())
				|| AAATranslatorConstants.DIAMETER_TRANSLATOR.equalsIgnoreCase(translatorPolicyData.getToTranslatorId())
				|| AAATranslatorConstants.RATING_TRANSLATOR.equalsIgnoreCase(translatorPolicyData.getToTranslatorId())
				|| AAATranslatorConstants.CRESTEL_OCSv2_TRANSLATOR.equalsIgnoreCase(translatorPolicyData.getToTranslatorId());
	}
	
	@Override
	public void reInit() {
		super.reInit();
		this.serviceConfiguration =  ((RMServerConfigurationImpl)this.serverContext.getServerConfiguration()).getRmChargingServiceConfiguration();
		
		reInitLogLevel();
	}
	
	@Override
	public boolean stopService() {
		super.stopService();
		if (driverManager != null) {
			driverManager.stop();
		}
		return true;
	}
	private void reInitLogLevel() {
		if(serviceConfiguration.isServiceLevelLoggerEnabled()){
			serviceLogger.setLogLevel(serviceConfiguration.logLevel());
		}
	}

	private void reInitServicePolicy() {
		RMChargingPolicy servicePolicy = null;
		ArrayList<RMChargingPolicy> tmpAuthServicePolicies= new ArrayList<RMChargingPolicy>();
		Map<String, RMChargingPolicyConfigurationImpl> authServConfMap = serviceConfiguration.getPolicyConfigrationMap();
		for(Entry<String, RMChargingPolicyConfigurationImpl> entry:authServConfMap.entrySet()){
			try{
				servicePolicy = getServicePolicy(entry.getValue().getPolicyName());
				if(servicePolicy!=null){
					servicePolicy.reInit();
					tmpAuthServicePolicies.add(servicePolicy);
				}
			}catch(Exception e){
				LogManager.getLogger().error(MODULE, "Failed to Re-Initialize the Service Policy " +  entry.getValue().getPolicyName() + ". Reason : " + e.getMessage());
				LogManager.getLogger().trace(e);
			}
		}
		this.servicePolicies = tmpAuthServicePolicies;
	}



	private RMChargingPolicy getServicePolicy(String policyName) {

		if (this.servicePolicies != null && !servicePolicies.isEmpty()) {
			for(RMChargingPolicy servicePolicy:servicePolicies) {
				if(servicePolicy.getPolicyName().equals(policyName)){
					return servicePolicy;
				}
			}
		}
		return null;	

	}


	private void initServiecpolicies(){
		
		ArrayList<RMChargingPolicy> tmpAuthServicePolicies= new ArrayList<RMChargingPolicy>();
		Map<String, RMChargingPolicyConfigurationImpl> authServConfMap = ((RMChargingServiceContext)getServiceContext()).getChargingConfiguration().getPolicyConfigrationMap();
		for(Entry<String, RMChargingPolicyConfigurationImpl> entry:authServConfMap.entrySet()){
			try{
				RMChargingPolicy servicePolicy = new RMChargingPolicy((RMChargingServiceContext)getServiceContext(),entry.getKey());
				servicePolicy.init();
				tmpAuthServicePolicies.add(servicePolicy);			
			}catch(Exception e){
				LogManager.getLogger().error(MODULE, "Failed to Initialize the Service Policy " +  entry.getValue().getPolicyName() + ". Reason : " + e.getMessage());
				LogManager.getLogger().trace(e);
			}
		}
		this.servicePolicies = tmpAuthServicePolicies;
	}
	
	@Override
	protected void incrementRequestReceivedCounter(String clientAddress) {
		chargingServiceMIBListener.listenRMChargingServTotalRequestsEvent();
		chargingServiceMIBListener.listenRMChargingServTotalRequestsEvent(clientAddress);
	}
	
	@Override
	protected void incrementResponseCounter(ServiceResponse response) {
		RMChargingResponse concResponse = (RMChargingResponse) response;
		incrementResponseCounter(concResponse.getClientData().getClientIp(), 
				concResponse.getPacketType());
	}
	
	@Override
	protected void incrementResponseCounter(String sourceAddress,
			byte[] responseBytes) {
		RadClientData clientData = ((AAAServerContext)getServerContext()).getServerConfiguration()
			.getRadClientConfiguration().getClientData(sourceAddress);
		
		if (clientData == null) {
			return;
		}
		
		incrementResponseCounter(clientData.getClientIp(), 
				RadiusPacket.parsePacketType(responseBytes));
	}

	private void incrementResponseCounter(String clientIp, int packetType) {
		chargingServiceMIBListener.listenRMChargingServTotalResponsesEvent(clientIp);
		switch (packetType) {
		case RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE:
			chargingServiceMIBListener.listenRmChargingServTotalAcctResponse();
			chargingServiceMIBListener.listenRmChargingServTotalAcctResponse(clientIp);
			break;
		case RadiusConstants.ACCESS_ACCEPT_MESSAGE:
			chargingServiceMIBListener.listenRmChargingServTotalAccessAccept();
			chargingServiceMIBListener.listenRmChargingServTotalAccessAccept(clientIp);
			break;
		case RadiusConstants.ACCESS_REJECT_MESSAGE:
			chargingServiceMIBListener.listenRMChargingServTotalAccessRejects();
			chargingServiceMIBListener.listenRMChargingServTotalAccessRejects(clientIp);
			break;
		default :
			break;
		}
	}


	@Override
	protected void incrementRequestDroppedCounter(ServiceRequest request) {
		chargingServiceMIBListener.listenRMChargingServTotalPacketsDroppedEvent(((RMChargingRequest)request).getClientIp());
	}
	
	@Override
	protected void incrementRequestDroppedCounter(String clientAddress , ServiceRequest request){
		chargingServiceMIBListener.listenRMChargingServTotalPacketsDroppedEvent(clientAddress);
	}

	@Override
	protected void incrementServTotalBadAuthenticators(String clientAddress) {
		chargingServiceMIBListener.listenRMChargingServTotalBadAuthenticatorsEvent(clientAddress);
	}

	@Override
	protected void incrementServTotalMalformedRequest(RadServiceRequest request) {
		chargingServiceMIBListener.listenRMChargingServTotalMalformedRequestsEvent(request.getClientIp());
	}

	@Override
	protected void incrementServTotalInvalidRequests(ServiceRequest request) {
		chargingServiceMIBListener.listenRMChargingServTotalInvalidRequestsEvent();
	}

	private void incrementChargingServTotalUnknownTypes(String clientAddress){
		chargingServiceMIBListener.listenRMChargingServTotalUnknownTypesEvent(clientAddress);
	}
	
	@Override
	protected void incrementDuplicateRequestReceivedCounter(
			ServiceRequest request) {
		RMChargingRequest rmChargingRequest = (RMChargingRequest)request;
		chargingServiceMIBListener.listenRMChargingServTotalDupRequestsEvent(rmChargingRequest.getClientIp());
	}

	@Override
	protected void handleStatusServerMessageRequest(RMChargingRequest serviceRequest,
			RMChargingResponse serviceResponse) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Status Server message received.");

		serviceResponse.setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
		
		//as per RFC if request contains Message Authenticator then need to add in response too
		if(serviceRequest.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR) != null){
			IRadiusAttribute msgAuthenticatorAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
			//as generate packet does add valid bytes
			msgAuthenticatorAttr.setValueBytes(new byte[0]);
			serviceResponse.addAttribute(msgAuthenticatorAttr);
		}

	}
	
	@Override
	protected final void handleRadiusRequest(RMChargingRequest request, RMChargingResponse response, ISession session){
		setServicePolicy(request);
		RMChargingPolicy servicePolicy = request.getServicePolicy();
		
		updateRequestSpecificCounter(request);
		
		if(servicePolicy != null){
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Selected RMCharging service policy: " + servicePolicy.getPolicyName());
			servicePolicy.handlePrePluginRequest(request, response);
			
			if(!response.isFurtherProcessingRequired())
				return;
			
			servicePolicy.handleRequest(request, response);
			
			if(response.isFurtherProcessingRequired())
				return;
			
			servicePolicy.handlePostPluginRequest(request, response);
			
		}else{
			//TODO - ID 0002 - Default policy from the base must be always available.
			//authServiceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.RADIUS_SERVICE_POLICY_NOT_SATISFIED, MODULE, "No Authentication Service Policy Satisfied.");
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "No Policy Satisfied for request : "+request);
			
			getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.RADIUS_SERVICE_POLICY_NOT_SATISFIED, MODULE, 
					"No RM charging service Policy Satisfied.", 0, "RM Charging service Policy");
			
			if(request.getPacketType() == RadiusConstants.ACCESS_REQUEST_MESSAGE){
				response.setFurtherProcessingRequired(false);
				response.setResponseMessage(AuthReplyMessageConstant.NO_POLICY_SATISFIED);			
				response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
			}else if(request.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
				response.markForDropRequest();
				response.setFurtherProcessingRequired(false);
			}	
			
		}
		
	}
	
	private void updateRequestSpecificCounter(RMChargingRequest request) {
		
		if(request.getPacketType() == RadiusConstants.ACCESS_REQUEST_MESSAGE){
			chargingServiceMIBListener.listenRmChargingServTotalAccessRequest();
			chargingServiceMIBListener.listenRmChargingServTotalAccessRequest(request.getClientIp());
		}else if(request.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
			chargingServiceMIBListener.listenRmChargingServTotalAcctRequest();
			chargingServiceMIBListener.listenRmChargingServTotalAcctRequest(request.getClientIp());
			
			IRadiusAttribute acctStatusStr = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE);

			switch (acctStatusStr.getIntValue()) {
			
			case RadiusAttributeValuesConstants.START:
				chargingServiceMIBListener.listenRmChargingServTotalAcctStartRequest();
				chargingServiceMIBListener.listenRmChargingServTotalAcctStartRequest(request.getClientIp());
				break;
			case RadiusAttributeValuesConstants.STOP:
				chargingServiceMIBListener.listenRmChargingServTotalAcctStopRequest();
				chargingServiceMIBListener.listenRmChargingServTotalAcctStopRequest(request.getClientIp());
				break;
			case RadiusAttributeValuesConstants.INTERIM_UPDATE:
				chargingServiceMIBListener.listenRmChargingServTotalAcctUpdateRequest();
				chargingServiceMIBListener.listenRmChargingServTotalAcctUpdateRequest(request.getClientIp());
				break;
			default : 
				break;
			}
		}else{
			incrementChargingServTotalUnknownTypes(request.getClientIp());
		}		
	}

	private void setServicePolicy(RMChargingRequest request){
		for(RMChargingPolicy servicePolicy:servicePolicies){
			if(servicePolicy.assignRequest(request)){				
				((RMChargingRequestImpl)request).setServicePolicy(servicePolicy);
				IRadiusAttribute infoAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_SATISFIED_SERVICE_POLICY);
				if(infoAttribute != null){
					infoAttribute.setStringValue(servicePolicy.getPolicyName());
					request.addInfoAttribute(infoAttribute);				
				}
				break;
			}
		}
	}
	@Override
	public RMChargingRequest formServiceSpecificRequest(InetAddress sourceAddress, int sourcePort, byte[] requestBytes, SocketDetail serverSocketDetail) {
		return new RMChargingRequestImpl(requestBytes, sourceAddress, sourcePort, serverSocketDetail);
	}

	@Override
	public RMChargingResponse formServiceSpecificResposne(RMChargingRequest serviceRequest) {
		return new RMChargingResponseImpl(serviceRequest.getAuthenticator(), serviceRequest.getIdentifier());
	}

	/**  Configuration Parameters **/
	@Override
	protected int getMainThreadPriority() {
		return serviceConfiguration.mainThreadPriority();
	}

	@Override
	protected int getMaxRequestQueueSize() {
		return serviceConfiguration.maxRequestQueueSize();
	}

	@Override
	protected int getMaxThreadPoolSize() {
		return serviceConfiguration.maxThreadPoolSize();
	}

	@Override
	protected int getMinThreadPoolSize() {
		return serviceConfiguration.minThreadPoolSize();
	}

	@Override
	protected int getSocketReceiveBufferSize() {
		return serviceConfiguration.socketReceiveBufferSize();
	}

	@Override
	protected int getSocketSendBufferSize() {
		return serviceConfiguration.socketSendBufferSize();
	}

	@Override
	protected int getThreadKeepAliveTime() {
		return serviceConfiguration.threadKeepAliveTime();
	}

	@Override
	protected int getWorkerThreadPriority() {
		return serviceConfiguration.workerThreadPriority();
	}

	@Override
	public String getKey() {
		return serviceConfiguration.getKey();
	}

	@Override
	protected ServiceContext getServiceContext() {
		return serviceContext;
	}

	@Override
	public String getServiceIdentifier() {
		return serviceConfiguration.getServiceIdentifier();
	}
	
	
	/** Unused Methods **/
	@Override
	public void readConfiguration() throws LoadConfigurationException {
		// TODO Remove this method
	}
	@Override
	public List<PluginEntryDetail> getRadPostPluginList() {
		return this.serviceConfiguration.getPostPluginList();
	}

	@Override
	public List<PluginEntryDetail> getRadPrePluginList() {
		return this.serviceConfiguration.getPrePluginList();
	}

	@Override
	protected void handleAsyncRadiusRequest(RMChargingRequest request,
			RMChargingResponse response, ISession session) {
		// Do nothing
	}
	
	@Override
	protected boolean isValidRequest(RMChargingRequest radServiceRequest,
			RMChargingResponse radServiceRsponse) {
		return true;
	}

	@Override
	public boolean validatePacketAsPerRFC(RMChargingRequest request) {		
		return true;
	}
	
	private class RMChargingRequestImpl extends RadServiceRequestImpl implements RMChargingRequest{
		private RMChargingPolicy servicePolicy;

		public RMChargingRequestImpl(byte[] requestBytes, InetAddress sourceAddress, int sourcePort, SocketDetail serverSocketDetail) {
			super(requestBytes, sourceAddress, sourcePort, serverSocketDetail);
			
		}
		private void setServicePolicy(RMChargingPolicy servicePolicy){
			this.servicePolicy = servicePolicy;
		}

		@Override
		public RMChargingPolicy getServicePolicy() {
			return servicePolicy;
		}
		
		@Override
		public RadServiceRequest clone() {
			return super.clone();
		}
	}
	
	private class RMChargingResponseImpl extends RadServiceResponseImpl implements RMChargingResponse {
		
		public RMChargingResponseImpl(byte[] authenticator,int identifier) {
			super(authenticator,identifier);
			setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
		}
		@Override
		public RadiusPacket generatePacket() {
			RadiusPacket responsePacket = new RadiusPacket();			
			responsePacket.setPacketType(getPacketType());
			responsePacket.setIdentifier(getIdentifier());
			
			responsePacket.addAttributes(getAttributeList());
			responsePacket.reencryptAttributes(null,null, getRequestAuthenticator()	, getClientData().getSharedSecret(getPacketType()));
			responsePacket.refreshPacketHeader();
			responsePacket.refreshInfoPacketHeader();
			
			IRadiusAttribute msgAuthenticatorAttribute = responsePacket.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
			if (responsePacket.getRadiusAttribute(RadiusAttributeConstants.EAP_MESSAGE)!=null || msgAuthenticatorAttribute != null) {
				
				if(msgAuthenticatorAttribute == null){
					msgAuthenticatorAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);				
					responsePacket.addAttribute(msgAuthenticatorAttribute);
					responsePacket.refreshPacketHeader();
				}
				
				msgAuthenticatorAttribute.setValueBytes(RadiusUtility.generateMessageAuthenticator(responsePacket.getBytes(), getRequestAuthenticator(), getClientData().getSharedSecret(getPacketType())));
			}
			responsePacket.refreshPacketHeader();
			responsePacket.refreshInfoPacketHeader();
			responsePacket.setAuthenticator(RadiusUtility.generateRFC2865ResponseAuthenticator((responsePacket),getRequestAuthenticator(),getClientData().getSharedSecret(getPacketType())));
			return responsePacket;
		}
		
		@Override
		public RadServiceResponse clone() {
			return super.clone();
		}
	}
	
	
	@Override
	public List<ICommand> getCliCommands() {
		List<ICommand> cmdList = new ArrayList<ICommand>();
		cmdList.add(new ChargingServCommand(this.chargingServiceMIBListener) {
			
			@Override
			public String getServiceThreadSummary() {
				return getThreadSummary();
			}
		});

		
		return cmdList;
	}

	@Override
	public boolean isDuplicateDetectionEnabled() {
		return serviceConfiguration.isDuplicateRequestDetectionEnabled();
	}
	
	@Override
	public int getDuplicateDetectionQueuePurgeInterval() {
		return serviceConfiguration.getDupicateRequestQueuePurgeInterval();
	}

	@Override
	public String getServiceName() {
		return "RM Charging Service";
		
	}
	
	// To Read and Write Origin State Id
	public int readOriginStateId(){

		String serverHome = getServiceContext().getServerContext().getServerHome() + File.separator + SYSTEM_PATH;
		
		try {
			File infoFile = new File(serverHome + File.separator + SYS_ORIGIN_STATE_ID_FILE);

			if (infoFile.exists()) {
				readSysOriginStateId(infoFile);
			}
			
			writeSysOriginStateId(new File(serverHome));
			
		} catch (Exception e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().trace(MODULE,"Error occured while reading server info, reason: "+ e.getMessage());
			}
			LogManager.getLogger().trace(e);
		}
		return count;
	}


	private void writeSysOriginStateId(File systemFile) {
		PrintWriter detailWriter = null;
		try {
			detailWriter = new PrintWriter(new FileWriter(new File(systemFile, SYS_ORIGIN_STATE_ID_FILE), false)); //NOSONAR - Reason: Resource should be closed
			detailWriter.println(count);
		} catch (Exception e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().error(MODULE,"Problem writing Origin-state-id to sys file, reason: "+ e.getMessage());
			}
			LogManager.getLogger().trace(e);
		} finally {
			Closeables.closeQuietly(detailWriter);
		}
	}

	private void readSysOriginStateId(File infoFile) throws IOException {
		BufferedReader bufferedReader = null;
		bufferedReader = new BufferedReader(new FileReader(infoFile));
		String strTemp = bufferedReader.readLine();
		try {
			if (strTemp != null && strTemp.trim().length()>0) {
				count =  Integer.parseInt(strTemp);
				if (count >= Integer.MAX_VALUE) {
					count = 1;
				} else {
					count++;
				}
			}
		}catch (Exception e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().error(MODULE,"Problem in reading Origin-state-id to sys file, reason: "+ e.getMessage()+", Setting up a Default value");
			}
			LogManager.getLogger().trace(e);
		} finally {
			Closeables.closeQuietly(bufferedReader);
		}
	}
	
	@Override
	protected boolean validateMessageAuthenticator(RMChargingRequest radServiceRequest,
			byte[] msgAuthenticatorBytes,String strSecret) {
		//RM services do not validate the message authenticator received in request
		return true;
	}
	
	public class RMChargingConfigurationSetter implements ConfigurationSetter{
		private ServiceContext serviceContext;
		private static final String CHARGING = "charging";
		private static final String REALTIME = "realtime";
		
		public RMChargingConfigurationSetter(ServiceContext serviceContext){
			this.serviceContext = serviceContext;
		}
		
		@Override
		public String execute(String... parameters) {
			if(parameters[2].equalsIgnoreCase("log")){
				if(parameters.length >= 4){
					if(((RMChargingServiceContext)serviceContext).getChargingConfiguration().isServiceLevelLoggerEnabled()){
						if (serviceLogger instanceof EliteRollingFileLogger) {
							EliteRollingFileLogger logger = (EliteRollingFileLogger)serviceLogger;
							if (logger.isValidLogLevel(parameters[3]) == false) {
								return "Invalid log level: " + parameters[3];
							}
							logger.setLogLevel(parameters[3]);
							return "Configuration Changed Successfully";
							
						}
					}else{
						return "Error : charging log are disabled";
					}
				}
			}
			return getHelpMsg();
		}

		@Override
		public boolean isEligible(String... parameters) {
			if(parameters.length == 0){
				return false;
			}
			if(!parameters[0].equalsIgnoreCase("service")){
				return false;
			}
			if(!parameters[1].equalsIgnoreCase(CHARGING)){
				return false;
			}
			if(parameters[2].equalsIgnoreCase("log")){
				return true;
			}else if(parameters[2].equalsIgnoreCase("-help")){
				return true;
			}

			return false;
		}

		@Override
		public String getHelpMsg() {
			StringWriter stringWriter = new StringWriter();
			PrintWriter out = new PrintWriter(stringWriter);
			out.println();
			out.println("Usage : set service charging [<options>]");
			out.println();
			out.println("where options include:");		
			out.println("     log { all | debug | error | info | off | trace | warn }");
			out.println("     		Set the log level of the Charging Service. ");			
			out.close();
			return stringWriter.toString();
		}

		@Override
		public String getHotkeyHelp() {
			return "'"+CHARGING+"':{'log':{'off':{},'error':{},'warn':{},'info':{},'debug':{},'trace':{},'all':{}}}";
		}

		@Override
		public int getConfigurationSetterType() {
			return SERVICE_TYPE;
		}
	}


	@Override
	public List<SocketDetail> getSocketDetails() {
		return serviceConfiguration.getSocketDetails();
	}
	
	@Override
	protected final void shutdownLogger() {
		Closeables.closeQuietly(serviceLogger);
	}

	@Override
	public int getDefaultServicePort() {
		return AAAServerConstants.DEFAULT_RM_CHARGING_SERVICE_PORT;
	}

	@Override
	protected boolean isSessionRelease(RMChargingRequest request, RMChargingResponse response) {
		return false;
	}
}
