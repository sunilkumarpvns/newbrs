package com.elitecore.aaa.rm.service.concurrentloginservice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.core.conf.RMServerConfiguration;
import com.elitecore.aaa.core.policies.accesspolicy.AccessPolicyManager;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.util.cli.RadiusLogMonitor;
import com.elitecore.aaa.core.util.cli.SetCommand;
import com.elitecore.aaa.core.util.cli.SetCommand.ConfigurationSetter;
import com.elitecore.aaa.mibs.rm.concurrentloginservice.server.RMConcServiceMIBListener;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.plugins.core.RadPluginManager;
import com.elitecore.aaa.radius.plugins.core.RadPluginRequestHandler;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.base.BaseRadiusService;
import com.elitecore.aaa.radius.service.base.RadServiceRequestImpl;
import com.elitecore.aaa.radius.service.base.RadServiceResponseImpl;
import com.elitecore.aaa.radius.sessionx.ConcurrencySessionManager;
import com.elitecore.aaa.rm.conf.RMConcurrentLoginServiceConfiguration;
import com.elitecore.aaa.rm.util.cli.ConcServCommand;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.serverx.policies.ManagerInitialzationException;
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
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class RMConcurrentLoginService extends BaseRadiusService< RadConcLoginRequest, RadConcLoginResponse> {
	
	private static final String MODULE = "RM_CONCURRENT_LOGIN_SERVICE";
	private static final String SERVICE_ID = "RM_CONCURRENT_LOGIN";			
	
	private RMConcurrentLoginServiceConfiguration concurrentLoginServiceConfiguration;
	private RMConcurrentLoginServiceContext concurrentLoginServiceContext;
	private RMConcServiceMIBListener concServiceMIBListener;
	private AAAServerContext serverContext;
	private ConcurrencySessionManager concurrentSessionManager ;
	private EliteRollingFileLogger serviceLogger;
	
	public RMConcurrentLoginService(AAAServerContext context, RadPluginManager pluginManager, RadiusLogMonitor logMonitor) {
		super(context, pluginManager, logMonitor);		
		this.serverContext = context;
		concurrentLoginServiceConfiguration = ((RMServerConfiguration)context.getServerConfiguration()).getRMConcurrentLoginServiceConfiguration();	
		concServiceMIBListener  = new RMConcServiceMIBListener(new RMConcServiceMIBCounters(context));
	}
	
	@Override
	protected void initService() throws ServiceInitializationException{		
		
		concurrentLoginServiceContext = new RMConcurrentLoginServiceContext(){

			@Override
			public RMConcurrentLoginServiceConfiguration getRMConcurrentLoginServiceConfiguration() {
				return concurrentLoginServiceConfiguration;
			}

			@Override
			public ESCommunicator getDriver(String driverInstanceId) {				
				return null;
			}
			@Override
			public String getServiceIdentifier() {
				return  RMConcurrentLoginService.this.getServiceIdentifier();
			}

			@Override
			public AAAServerContext getServerContext() {
				return (AAAServerContext) RMConcurrentLoginService.this.getServerContext();
			}

			@Override
			public void submitAsyncRequest(RadConcLoginRequest serviceRequest, RadConcLoginResponse serviceResponse,
					AsyncRequestExecutor<RadConcLoginRequest, RadConcLoginResponse> requestExecutor) {
				 RMConcurrentLoginService.this.submitAsyncRequest(serviceRequest, serviceResponse, requestExecutor);
				
			}

			@Override
			public RadPluginRequestHandler createPluginRequestHandler(List<PluginEntryDetail> prePluginList, 
					List<PluginEntryDetail> postPluginList) {
				return createRadPluginReqHandler(prePluginList, postPluginList);
			}
			
		};
		
		super.initService();	
		
		Optional<ConcurrencySessionManager> optionalSessionMgr = serverContext.getLocalSessionManager(concurrentLoginServiceConfiguration.getSessionManagerInstanceId());
		if(optionalSessionMgr.isPresent() == false) {
			throw new ServiceInitializationException("Concurrent Session Manager is not initialized", ServiceRemarks.UNKNOWN_PROBLEM);
		}
		concurrentSessionManager = optionalSessionMgr.get();
		
//		concurrentSessionManager.init();
		LogManager.getLogger().info(MODULE, String.valueOf(concurrentLoginServiceConfiguration));
		if (concurrentLoginServiceConfiguration.isServiceLevelLoggerEnabled()) {
			serviceLogger = 
				new EliteRollingFileLogger.Builder(getServerContext().getServerInstanceName(),
						concurrentLoginServiceConfiguration.getLogLocation())
				.rollingType(concurrentLoginServiceConfiguration.logRollingType())
				.rollingUnit(concurrentLoginServiceConfiguration.logRollingUnit())
				.maxRolledUnits(concurrentLoginServiceConfiguration.logMaxRolledUnits())
				.compressRolledUnits(concurrentLoginServiceConfiguration.isCompressLogRolledUnits())
				.sysLogParameters(concurrentLoginServiceConfiguration.getSysLogConfiguration().getHostIp(),
						concurrentLoginServiceConfiguration.getSysLogConfiguration().getFacility())
				.build();
			serviceLogger.setLogLevel(concurrentLoginServiceConfiguration.logLevel());
			LogManager.setLogger(getServiceIdentifier(), serviceLogger);
		}	
		
		
		try {
			AccessPolicyManager.getInstance().initCache(getServiceContext().getServerContext().getServerHome());
			registerCacheable(AccessPolicyManager.getInstance());
		} catch (ManagerInitialzationException e) {
			LogManager.getLogger().error(MODULE,"Error Caching Access-Policies. Reason :" + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
		registerServiceSummaryWriter(new RMConcServiceSummaryWriter(concurrentLoginServiceContext));
		RMConcurrentLoginConfigurationSetter concurrentLoginConfigurationSetter = new RMConcurrentLoginConfigurationSetter(concurrentLoginServiceContext);
		SetCommand.registerConfigurationSetter(concurrentLoginConfigurationSetter);
		
		LogManager.getLogger().info(MODULE,"RM Concurrent Login service initialized successfully.");				
		
	}
	
	@Override
	protected boolean startService() {
		return super.startService();
	}
	
	@Override
	public void reInit(){
		super.reInit();
		this.concurrentLoginServiceConfiguration = ((RMServerConfiguration)this.serverContext.getServerConfiguration()).getRMConcurrentLoginServiceConfiguration();
		reInitLogLevel();	
	}
	
	
	private void reInitLogLevel() {
		if(concurrentLoginServiceConfiguration.isServiceLevelLoggerEnabled()){
			serviceLogger.setLogLevel(concurrentLoginServiceConfiguration.logLevel());
		}
	}

	@Override
	public List<PluginEntryDetail> getRadPostPluginList() {
		return this.concurrentLoginServiceConfiguration.getPostPluginList();
	}

	@Override
	public List<PluginEntryDetail> getRadPrePluginList() {
		return this.concurrentLoginServiceConfiguration.getPrePluginList();
	}

	@Override
	protected void incrementRequestReceivedCounter(String clientAddress) {
		concServiceMIBListener.listenRMConcServTotalRequestsEvent();
		concServiceMIBListener.listenRMConcServTotalRequestsEvent(clientAddress);
	}
	
	@Override
	protected void incrementResponseCounter(ServiceResponse response) {
		RadConcLoginResponse concResponse = (RadConcLoginResponse) response;
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
		switch (packetType) {
		case RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE:
		case RadiusConstants.ACCESS_ACCEPT_MESSAGE:
		case RadiusConstants.ACCESS_REJECT_MESSAGE:
			concServiceMIBListener.listenRMConcServTotalResponsesEvent(clientIp);
		}
	}

	@Override
	protected void incrementRequestDroppedCounter(ServiceRequest request) {
		concServiceMIBListener.listenRMConcServTotalPacketsDroppedEvent(((RadiusConcLoginRequestImpl)request).getClientIp());
	}
	
	@Override
	protected void incrementRequestDroppedCounter(String clientAddress , ServiceRequest request){
		concServiceMIBListener.listenRMConcServTotalPacketsDroppedEvent(clientAddress);
	}

	@Override
	protected void incrementServTotalBadAuthenticators(String clientAddress) {
		concServiceMIBListener.listenRMConcServTotalBadAuthenticatorsEvent(clientAddress);
	}

	@Override
	protected void incrementServTotalMalformedRequest(RadServiceRequest request) {
		concServiceMIBListener.listenRMConcServTotalMalformedRequestsEvent(request.getClientIp());
	}

	@Override
	protected void incrementServTotalInvalidRequests(ServiceRequest request) {
		concServiceMIBListener.listenRMConcServTotalInvalidRequestsEvent();
	}

	private void incrementConcServTotalUnknownTypes(String clientAddress){
		concServiceMIBListener.listenRMConcServTotalUnknownTypesEvent(clientAddress);
	}
	
	@Override
	protected void incrementDuplicateRequestReceivedCounter(
			ServiceRequest request) {
		RadConcLoginRequest radConcLoginRequest = (RadConcLoginRequest)request;
		concServiceMIBListener.listenRMConcServTotalDupRequestsEvent(radConcLoginRequest.getClientIP());
	}

	@Override
	protected final void handleRadiusRequest(RadConcLoginRequest request, RadConcLoginResponse response, ISession session) {					
		 
		LogManager.getLogger().info(MODULE, "Handling request for Concurrent Login Service");		
		int packetType = request.getPacketType();

		if(packetType == RadiusConstants.ACCESS_REQUEST_MESSAGE){
			LogManager.getLogger().info(MODULE, "Authentication request is send to Concurrent Login Service");
			LogManager.getLogger().info(MODULE, "Forwading the request to concurrency session manager for further processing.");
			
			response.setFurtherProcessingRequired(true);
			response.setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);			
			response.setResponseMessage(AuthReplyMessageConstant.AUTHENTICATION_SUCCESS);
			this.concurrentSessionManager.handleAuthenticationRequest((RadConcLoginRequest)request, (RadConcLoginResponse)response, concurrentLoginServiceContext);
		}else if(packetType == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
			LogManager.getLogger().info(MODULE, "Accounting request is send to Concurrent Login Service");
			LogManager.getLogger().info(MODULE, "Forwading the request to concurrency session manager for further processing.");
			response.setPacketType(RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE);
			response.setFurtherProcessingRequired(true);
			this.concurrentSessionManager.handleAccountingRequest((RadConcLoginRequest)request, (RadConcLoginResponse)response,concurrentLoginServiceContext);
		}else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE,"Received Unknown Request Type : " + request.getPacketType() +", Dropping request");
			}
			response.markForDropRequest();
			incrementConcServTotalUnknownTypes(request.getClientIp());
		}
	}

	@Override
	protected void handleStatusServerMessageRequest(RadConcLoginRequest serviceRequest, 
			RadConcLoginResponse serviceResponse) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Status Server message received.");
			
		serviceResponse.setPacketType(RadiusConstants.STATUS_SERVER_MESSAGE);
		
		//as per RFC if request contains Message Authenticator then need to add in response too
		if(serviceRequest.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR) != null){
			IRadiusAttribute msgAuthenticatorAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
			//as generate packet does add valid bytes
			msgAuthenticatorAttr.setValueBytes(new byte[0]);
			serviceResponse.addAttribute(msgAuthenticatorAttr);
		}
			
	}

	@Override
	protected boolean isValidRequest(RadConcLoginRequest radServiceRequest,
			RadConcLoginResponse radServiceRsponse){
			//TODO check in previous version
		return true;
	}

	@Override
	protected int getMainThreadPriority() {
		return this.concurrentLoginServiceConfiguration.mainThreadPriority();
	}

	@Override
	protected int getMaxRequestQueueSize() {
		return this.concurrentLoginServiceConfiguration.maxRequestQueueSize();
	}

	@Override
	protected int getMaxThreadPoolSize() {
		return this .concurrentLoginServiceConfiguration.maxThreadPoolSize();
	}

	@Override
	protected int getMinThreadPoolSize() {
		return this.concurrentLoginServiceConfiguration.minThreadPoolSize();
	}

	@Override
	protected int getSocketReceiveBufferSize() {
		return this.concurrentLoginServiceConfiguration.socketReceiveBufferSize();
	}

	@Override
	protected int getSocketSendBufferSize() {
		return this.concurrentLoginServiceConfiguration.socketSendBufferSize();
	}

	@Override
	protected int getThreadKeepAliveTime() {
		return this.concurrentLoginServiceConfiguration.threadKeepAliveTime();
	}

	@Override
	protected int getWorkerThreadPriority() {
		return this.concurrentLoginServiceConfiguration.workerThreadPriority();
	}

	@Override
	public String getKey() {
		return this.concurrentLoginServiceConfiguration.getKey();
	}

	@Override
	protected ServiceContext getServiceContext() {
		return this.concurrentLoginServiceContext;
	}

	@Override
	public void readConfiguration() throws LoadConfigurationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getServiceIdentifier() {
		return SERVICE_ID;
	}

	private class RadiusConcLoginResponseImpl extends RadServiceResponseImpl implements RadConcLoginResponse {

		public RadiusConcLoginResponseImpl(byte[] authenticator,int identifier) {
			super(authenticator,identifier);
		}

		@Override
		public RadiusPacket generatePacket() {
			RadiusPacket responsePacket = new RadiusPacket();
			responsePacket.setPacketType(getPacketType());
			responsePacket.setIdentifier(getIdentifier());
			responsePacket.addAttributes(getAttributeList());
			//responsePacket.reencryptAttributes(null,null, getRequestAuthenticator()	, getClientData().getSharedSecret());
			responsePacket.refreshPacketHeader();
			
			IRadiusAttribute msgAuthenticatorAttribute = responsePacket.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
			if (msgAuthenticatorAttribute != null) {
				msgAuthenticatorAttribute.setValueBytes(RadiusUtility.generateMessageAuthenticator(responsePacket.getBytes(), getRequestAuthenticator(), getClientData().getSharedSecret(getPacketType())));
			}
			
			responsePacket.refreshPacketHeader();
			responsePacket.refreshInfoPacketHeader();
			RadiusUtility.generateRFC2865ResponseAuthenticator(responsePacket, getRequestAuthenticator(),getClientData().getSharedSecret(getPacketType()));
			return responsePacket;
		}
		
		@Override
		public RadServiceResponse clone() {
			return super.clone();
		}
	}

	private class RadiusConcLoginRequestImpl extends RadServiceRequestImpl implements RadConcLoginRequest{		

		public RadiusConcLoginRequestImpl(byte[] requestBytes, InetAddress sourceAddress, int sourcePort, SocketDetail serverSocketDetail) {
			super(requestBytes, sourceAddress, sourcePort, serverSocketDetail);
		}

		@Override
		public String getClientIP() {
			return getClientIP();
		}
		
		@Override
		public RadServiceRequest clone() {
			return super.clone();
		}
	}

	
	@Override
	public RadConcLoginResponse formServiceSpecificResposne( RadConcLoginRequest serviceRequest) {
		return new RadiusConcLoginResponseImpl(serviceRequest.getAuthenticator(),serviceRequest.getIdentifier());
	}

	@Override
	public RadConcLoginRequest formServiceSpecificRequest(
			InetAddress sourceAddress, int sourcePort, byte[] requestBytes, SocketDetail serverSocketDetail) {
		return new RadiusConcLoginRequestImpl(requestBytes, sourceAddress, sourcePort, serverSocketDetail);
	}
	
	@Override
	public boolean validatePacketAsPerRFC(RadConcLoginRequest request){
		return true;
	}

	public class RMConcurrentLoginConfigurationSetter implements ConfigurationSetter{
		private ServiceContext serviceContext;
		private static final String REALTIME = "realtime";
		
		public RMConcurrentLoginConfigurationSetter(ServiceContext serviceContext){
			this.serviceContext = serviceContext;
		}
		
		@Override
		public String execute(String... parameters) {
			if(parameters[2].equalsIgnoreCase("log")){
				if(parameters.length >= 4){
					if(((RMConcurrentLoginServiceContext)serviceContext).getRMConcurrentLoginServiceConfiguration().isServiceLevelLoggerEnabled()){
						if (serviceLogger instanceof EliteRollingFileLogger) {
							EliteRollingFileLogger logger = (EliteRollingFileLogger)serviceLogger;
							if (logger.isValidLogLevel(parameters[3]) == false) {
								return "Invalid log level: " + parameters[3];
							}
							logger.setLogLevel(parameters[3]);
							return "Configuration Changed Successfully";
							
						}
					}else{
						return "Error : Concurrent Login log are disabled";
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
			if(!parameters[1].equalsIgnoreCase("concurrent")){
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
			out.println("Usage : set service concurrent [<options>]");
			out.println();
			out.println("where options include:");		
			out.println("     log { all | debug | error | info | off | trace | warn }");
			out.println("     		Set the log level of the Concurrent Login Service. ");			
			out.close();
			return stringWriter.toString();
		}

		@Override
		public String getHotkeyHelp() {
			return "'concurrent':{'log':{'off':{},'error':{},'warn':{},'info':{},'debug':{},'trace':{},'all':{}}}";
		}

		@Override
		public int getConfigurationSetterType() {
			return SERVICE_TYPE;
		}
	}


	@Override
	protected void handleAsyncRadiusRequest(RadConcLoginRequest request,
			RadConcLoginResponse response, ISession session) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ICommand> getCliCommands() {
		List<ICommand> cmdList = new ArrayList<ICommand>();
		cmdList.add(new ConcServCommand() {
			
			@Override
			public String getServiceThreadSummary() {
				return getThreadSummary();
			}
		});
		return cmdList;
	}

	@Override
	public boolean isDuplicateDetectionEnabled() {
		return concurrentLoginServiceConfiguration.isDuplicateRequestDetectionEnabled();
	}
	
	@Override
	public int getDuplicateDetectionQueuePurgeInterval() {
		return concurrentLoginServiceConfiguration.getDupicateRequestQueuePurgeInterval();
	}

	@Override
	public String getServiceName() {
		return "RM Concurrent Login Policy";
		
	}
	
	@Override
	protected boolean validateMessageAuthenticator(RadConcLoginRequest radServiceRequest,
			byte[] msgAuthenticatorBytes,String strSecret) {
		//RM services do not validate the message authenticator received in request
		return true;
	}

	@Override
	public List<SocketDetail> getSocketDetails() {
		return concurrentLoginServiceConfiguration.getSocketDetails();
	}
	
	@Override
	protected final void shutdownLogger() {
		Closeables.closeQuietly(serviceLogger);
	}

	@Override
	public int getDefaultServicePort() {
		return AAAServerConstants.DEFAULT_RM_CONCURRENT_LOGIN_SERVICE_PORT;
	}

	@Override
	protected boolean isSessionRelease(RadConcLoginRequest request, RadConcLoginResponse response) {
		return false;
	}

}
