package com.elitecore.aaa.radius.service.dynauth;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.conf.impl.ImdgIndexDetail;
import com.elitecore.aaa.core.drivers.DriverManager;
import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.server.axixserver.InMemoryRequestHandler;
import com.elitecore.aaa.core.util.cli.RadiusLogMonitor;
import com.elitecore.aaa.core.util.cli.SetCommand;
import com.elitecore.aaa.core.util.cli.SetCommand.ConfigurationSetter;
import com.elitecore.aaa.mibs.radius.dynauth.client.RadiusDynAuthClientMIB;
import com.elitecore.aaa.mibs.radius.dynauth.client.autogen.RADIUS_DYNAUTH_CLIENT_MIB;
import com.elitecore.aaa.mibs.radius.dynauth.client.autogen.TableRadiusDynAuthServerTable;
import com.elitecore.aaa.mibs.radius.dynauth.client.extended.RADIUS_DYNAUTH_CLIENT_MIBImpl;
import com.elitecore.aaa.mibs.radius.dynauth.client.extended.RadiusDynAuthServerEntryMBeanImpl;
import com.elitecore.aaa.mibs.radius.dynauth.server.RadiusDynAuthServerMIB;
import com.elitecore.aaa.mibs.radius.dynauth.server.autogen.RADIUS_DYNAUTH_SERVER_MIB;
import com.elitecore.aaa.mibs.radius.dynauth.server.autogen.TableRadiusDynAuthClientTable;
import com.elitecore.aaa.mibs.radius.dynauth.server.extended.RADIUS_DYNAUTH_SERVER_MIBImpl;
import com.elitecore.aaa.mibs.radius.dynauth.server.extended.RadiusDynAuthClientEntryMBeanImpl;
import com.elitecore.aaa.mibs.radius.dynauth.server.extended.TableRadiusDynAuthClientTableImpl;
import com.elitecore.aaa.mibs.radius.dynauth.server.extended.TableRadiusDynAuthServerTableImpl;
import com.elitecore.aaa.radius.conf.RadClientConfiguration;
import com.elitecore.aaa.radius.conf.RadDynAuthConfiguration;
import com.elitecore.aaa.radius.conf.impl.DynAuthDBScanDetail;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.plugins.core.RadPluginManager;
import com.elitecore.aaa.radius.plugins.core.RadPluginRequestHandler;
import com.elitecore.aaa.radius.policies.radiuspolicy.RadiusPolicyManager;
import com.elitecore.aaa.radius.policies.servicepolicy.DynAuthServicePolicy;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.DynAuthServicePolicyConfiguration;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.base.BaseRadiusService;
import com.elitecore.aaa.radius.service.base.RadServiceRequestImpl;
import com.elitecore.aaa.radius.service.base.RadServiceResponseImpl;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.aaa.radius.util.cli.DynAuthServCommand;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.policies.ManagerInitialzationException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.AsyncRequestExecutor;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.servicex.UDPServiceRequest;
import com.elitecore.core.system.comm.ILocalResponseListener;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.util.cli.cmd.ICommand;
import com.elitecore.core.util.logger.EliteRollingFileLogger;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.DynAuthErrorCode;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.sun.management.snmp.SnmpStatusException;

public class RadDynAuthService extends BaseRadiusService<RadDynAuthRequest, RadDynAuthResponse>{

	private static final String MODULE = "RAD-DYNAUTH";
	private static final String SERVICE_ID = "RAD-DYNAUTH";
	public static final String NAS_IP_ADDRESS = "0:4";
	public static final String NAS_IPV6_ADDRESS = "0:95";

	private RadDynAuthConfiguration dynAuthConfiguration;
	private RadDynAuthServiceContext dynAuthServiceContext;
	private List<DynAuthServicePolicy> dynDuthServicePolicies;
	private RadiusDynAuthServerMIB dynAuthServiceMIB;
	private EliteRollingFileLogger serviceLogger;

	//FIXME ELITEAAA-2767 statistic for request related counter was not updated properly when net-mask client is configured
	// so need to add radclient here but remove when proper solution is given.
	private RadClientConfiguration radClientConfiguration;
	
	
	public RadDynAuthService(AAAServerContext context, 
			DriverManager radiusDriverManager,
			RadPluginManager pluginManager,
			RadiusLogMonitor monitor) {
		super(context, radiusDriverManager, pluginManager, monitor);
		dynAuthConfiguration = context.getServerConfiguration().getDynAuthConfiguration();
		dynDuthServicePolicies =  new ArrayList<DynAuthServicePolicy>();
		this.dynAuthServiceMIB = new RadiusDynAuthServerMIB(new RadiusDynAuthServiceMIBCounters(context));
		radClientConfiguration = context.getServerConfiguration().getRadClientConfiguration();
	}

	@Override
	public List<PluginEntryDetail> getRadPostPluginList() {
		return  dynAuthConfiguration.getPostPluginList();
	}

	@Override
	public List<PluginEntryDetail> getRadPrePluginList() {
		return dynAuthConfiguration.getPrePluginList();
	}

	@Override
	protected final void handleRadiusRequest(RadDynAuthRequest request,
			RadDynAuthResponse response, ISession session) {

		setServicePolicy(request);
		DynAuthServicePolicy servicePolicy = request.getServicePolicy();

		if(servicePolicy != null){
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Selected Dyna-Auth service policy: " + servicePolicy.getPolicyName());

			servicePolicy.handlePrePluginRequest(request, response, session);

			if(servicePolicy.isValidatePacket()){
				if(!validatePacketAsPerRFC(request)){
					incrementServTotalMalformedRequest(request);
					IRadiusAttribute attr = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.ERROR_CAUSE);
					if(attr!=null){
						attr.setIntValue(DynAuthErrorCode.MissingAttribute.value);
						response.addAttribute(attr);
					}	

					if(request.getPacketType()==RadiusConstants.COA_REQUEST_MESSAGE)
						response.setPacketType(RadiusConstants.COA_NAK_MESSAGE);
					else
						response.setPacketType(RadiusConstants.DISCONNECTION_NAK_MESSAGE);
					response.setFurtherProcessingRequired(false);
					return;
				}	
			}

			request.setExecutor(servicePolicy.newExecutor(request, response));
			request.getExecutor().startRequestExecution(session);

			if(!request.isFutherExecutionStopped()) {
				servicePolicy.handlePostPluginRequest(request, response, session);
			}

		}else{
			dynAuthServiceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.RADIUS_SERVICE_POLICY_NOT_SATISFIED, 
					MODULE, "No Dyna Auth Service Policy Satisfied.", 0, "Dyna Auth Service Policy");
			
			LogManager.getLogger().debug(MODULE, "No Policy SatisFied");
			response.setFurtherProcessingRequired(false);
			if(request.getPacketType()==RadiusConstants.COA_REQUEST_MESSAGE ){
				response.setPacketType(RadiusConstants.COA_NAK_MESSAGE);
			}else{
				response.setPacketType(RadiusConstants.DISCONNECTION_NAK_MESSAGE);
			}
			ILocalResponseListener localResponseListener = getLocalResponseListener(request);
			if(localResponseListener!=null)
				localResponseListener.responseReceived(response.getResponseBytes());

		}

	}

	@Override
	protected void handleStatusServerMessageRequest(RadDynAuthRequest serviceRequest,
			RadDynAuthResponse serviceResponse) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Status Server message received.");

		serviceResponse.setPacketType(RadiusConstants.COA_ACK_MESSAGE);

		//as per RFC if request contains Message Authenticator then need to add in response too
		if(serviceRequest.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR) != null){
			IRadiusAttribute msgAuthenticatorAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
			//as generate packet does add valid bytes
			msgAuthenticatorAttr.setValueBytes(new byte[0]);
			serviceResponse.addAttribute(msgAuthenticatorAttr);
		}
	}

	@Override
	protected void incrementServTotalBadAuthenticators(String clientAddress) {
		RadClientData clientData = radClientConfiguration.getClientData(clientAddress);
		if(clientData != null) {
			dynAuthServiceMIB.listenRadiusDynAuthServTotalBadAuthenticatorsRequestsEvent(clientData.getClientIp());
		}
	}
	
	@Override
	protected void incrementRequestDroppedCounter(ServiceRequest request){
		if(request!=null){
			RadDynAuthRequest dynAuthRequest = (RadDynAuthRequest)request;
			RadClientData clientData = radClientConfiguration.getClientData(dynAuthRequest.getSourceAddress().getHostAddress());
			if(clientData != null) {
				switch(dynAuthRequest.getPacketType()){
				case RadiusConstants.COA_REQUEST_MESSAGE:
					dynAuthServiceMIB.listenRadiusDynAuthServTotalCOAPacketsDroppedEvent(clientData.getClientIp());
					break;

				case RadiusConstants.DISCONNECTION_REQUEST_MESSAGE:
					dynAuthServiceMIB.listenRadiusDynAuthServTotalDisConnectPacketsDroppedEvent(clientData.getClientIp());
					break;
				default:
					dynAuthServiceMIB.listenRadiusDynAuthServTotalInvalidRequestsEvent();
				}
			}
		}	
	}

	@Override
	protected void incrementServTotalInvalidRequests(ServiceRequest request) {
		dynAuthServiceMIB. listenRadiusDynAuthServTotalInvalidRequestsEvent();
		if(((RadDynAuthRequest)request).getPacketType()==RadiusConstants.COA_REQUEST_MESSAGE)
			dynAuthServiceMIB.listenRadiusDynAuthServTotalClientCoAInvalidClientAddressesEvent();
		else if(((RadDynAuthRequest)request).getPacketType()==RadiusConstants.DISCONNECTION_REQUEST_MESSAGE)
			dynAuthServiceMIB.listenRadiusDynAuthServTotalClientDisConnectInvalidClientAddressesEvent();
	}	
	
	@Override
	protected void incrementRequestReceivedCounter(UDPServiceRequest serviceRequest){
		dynAuthServiceMIB.listenRadiusDynAuthServTotalRequestsEvent();
		RadClientData clientData = radClientConfiguration.getClientData(serviceRequest.getSourceAddress().getHostAddress());
		if(clientData != null) {
			if(((RadDynAuthRequest)serviceRequest).getPacketType()==RadiusConstants.COA_REQUEST_MESSAGE){
				dynAuthServiceMIB.listenRadiusDynAuthServTotalCOARequestsEvent();
				dynAuthServiceMIB.listenRadiusDynAuthServTotalCOARequestsEvent(clientData.getClientIp());
			} else if(((RadDynAuthRequest)serviceRequest).getPacketType()==RadiusConstants.DISCONNECTION_REQUEST_MESSAGE) {
				dynAuthServiceMIB.listenRadiusDynAuthServTotalDisconRequestsEvent();
				dynAuthServiceMIB.listenRadiusDynAuthServTotalDisconRequestsEvent(clientData.getClientIp());
			}
		}
	}

	@Override
	protected void incrementResponseCounter(ServiceResponse response){

		RadDynAuthResponse dynAuthResponse = (RadDynAuthResponse)response;

		incrementResponseCounter(dynAuthResponse.getClientData().getClientIp(), 
				dynAuthResponse.getPacketType());
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
		case RadiusConstants.COA_ACK_MESSAGE:
			dynAuthServiceMIB.listenRadiusDynAuthServTotalCOAAckEvent(clientIp);			
			break;
		case RadiusConstants.COA_NAK_MESSAGE:
			dynAuthServiceMIB.listenRadiusDynAuthServTotalCOANakEvent(clientIp);
			break;
		case RadiusConstants.DISCONNECTION_ACK_MESSAGE:
			dynAuthServiceMIB.listenRadiusDynAuthServTotalDisConnectAckEvent(clientIp);			
			break;
		case RadiusConstants.DISCONNECTION_NAK_MESSAGE:
			dynAuthServiceMIB.listenRadiusDynAuthServTotalDisConnectNakEvent(clientIp);
			break;
		}
	}

	@Override
	protected boolean isValidRequest(RadDynAuthRequest radServiceRequest,
			RadDynAuthResponse radServiceRsponse) {

		if(radServiceRequest.getPacketType() != RadiusConstants.COA_REQUEST_MESSAGE && radServiceRequest.getPacketType() != RadiusConstants.DISCONNECTION_REQUEST_MESSAGE && radServiceRequest.getPacketType() != RadiusConstants.STATUS_SERVER_MESSAGE){

			dynAuthServiceMIB.listenRadiusDynAuthServTotalUnknownTypesEvent(radServiceRequest.getClientIp());
			radServiceRsponse.markForDropRequest();
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Unknown request packet (packet type: "+ radServiceRequest.getPacketType() + ") received from " + radServiceRequest.getClientIp() + ":" + radServiceRequest.getClientPort());
			return false;
		}

		if (radServiceRequest.getPacketType() == RadiusConstants.STATUS_SERVER_MESSAGE) {
			return isValidStatusServerMessage(radServiceRequest, radServiceRsponse);
		} else {
			return isValidDynAuthRequest(radServiceRequest, radServiceRsponse);
		}
	}

	private boolean isValidDynAuthRequest(RadDynAuthRequest radServiceRequest,
			RadDynAuthResponse radServiceRsponse) {
		return isRequestAuthenticatorValid(radServiceRequest, radServiceRsponse) &&
				isMessageAuthenticatorValid(radServiceRequest, radServiceRsponse);
	}

	private boolean isValidStatusServerMessage(RadDynAuthRequest radServiceRequest,
			RadDynAuthResponse radServiceRsponse) {
		IRadiusAttribute msgAuthenticator = radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
		if(msgAuthenticator != null){
			if(msgAuthenticator.getValueBytes().length == 16 && validateMessageAuthenticatorForStatusServer(radServiceRequest,msgAuthenticator.getValueBytes(),radServiceRsponse.getClientData().getSharedSecret(radServiceRequest.getPacketType()))){

				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Valid message authenticator");	
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Packet Dropped. Invalid Message Authenticator.");

				radServiceRsponse.setFurtherProcessingRequired(false);
				radServiceRsponse.markForDropRequest();
				incrementServTotalBadAuthenticators(radServiceRequest.getClientIp());
				incrementServTotalBadAuthenticators(radServiceRequest.getClientIp(),radServiceRequest.getPacketType());
				return false;
			}
		}
		return true;
	}

	private boolean isMessageAuthenticatorValid(RadDynAuthRequest radServiceRequest,
			RadDynAuthResponse radServiceRsponse) {
		IRadiusAttribute msgAuthenticator = radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
		if(msgAuthenticator != null){
			if(msgAuthenticator.getValueBytes().length == 16 && validateMessageAuthenticator(radServiceRequest,msgAuthenticator.getValueBytes(),radServiceRsponse.getClientData().getSharedSecret(radServiceRequest.getPacketType()))){

				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Valid message authenticator");	
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Packet Dropped. Invalid Message Authenticator.");

				radServiceRsponse.setFurtherProcessingRequired(false);
				radServiceRsponse.markForDropRequest();
				incrementServTotalBadAuthenticators(radServiceRequest.getClientIp());
				incrementServTotalBadAuthenticators(radServiceRequest.getClientIp(),radServiceRequest.getPacketType());
				return false;
			}
		}
		return true;
	}

	private boolean isRequestAuthenticatorValid(RadDynAuthRequest radServiceRequest,
			RadDynAuthResponse radServiceRsponse) {
		byte[] requestAuthenticator = null;
		RadiusPacket tmpRadiusPacket = new RadiusPacket();
		tmpRadiusPacket.setBytes(radServiceRequest.getRequestBytes());
		tmpRadiusPacket.setAuthenticator(new byte[16]);

		requestAuthenticator = RadiusUtility.generateRFC2866RequestAuthenticator(tmpRadiusPacket, radServiceRsponse.getClientData().getSharedSecret(radServiceRequest.getPacketType()));
		if(!RadiusUtility.isByteArraySame(requestAuthenticator,radServiceRequest.getAuthenticator())) {
			radServiceRsponse.markForDropRequest();
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Request Dropped:Invalid Request Authenticator.");
			return false;
		}
		return true;
	}

	private void incrementServTotalBadAuthenticators(String clientIp,int packetType) {
		RadClientData clientData = radClientConfiguration.getClientData(clientIp);
		if(clientData != null) {
			if(packetType==RadiusConstants.COA_REQUEST_MESSAGE) {
				dynAuthServiceMIB.listenRadiusDynAuthServTotalBadAuthenticatorsCOARequestsEvent(clientData.getClientIp());
			} else {
				dynAuthServiceMIB.listenRadiusDynAuthServTotalBadAuthenticatorsDisConnectRequestsEvent(clientData.getClientIp());
			}
		}
	}

	@Override
	protected int getMainThreadPriority() {
		return dynAuthConfiguration.mainThreadPriority();
	}

	@Override
	protected int getMaxRequestQueueSize() {
		return dynAuthConfiguration.maxRequestQueueSize();
	}

	@Override
	protected int getMaxThreadPoolSize() {
		return dynAuthConfiguration.maxThreadPoolSize();
	}

	@Override
	protected int getMinThreadPoolSize() {
		return dynAuthConfiguration.minThreadPoolSize();
	}

	@Override
	protected int getSocketReceiveBufferSize() {
		return dynAuthConfiguration.socketReceiveBufferSize();
	}

	@Override
	protected int getSocketSendBufferSize() {
		return dynAuthConfiguration.socketSendBufferSize();
	}

	@Override
	protected int getThreadKeepAliveTime() {
		return dynAuthConfiguration.threadKeepAliveTime();
	}

	@Override
	protected int getWorkerThreadPriority() {
		return dynAuthConfiguration.workerThreadPriority();
	}

	@Override
	public String getKey() {
		return dynAuthConfiguration.getKey();
	}

	@Override
	protected ServiceContext getServiceContext() {
		return dynAuthServiceContext;
	}

	@Override
	public void readConfiguration() throws LoadConfigurationException {

	}

	@Override
	public String getServiceIdentifier() {
		return SERVICE_ID;
	}


	@Override
	protected void initService() throws ServiceInitializationException{

		if (dynAuthConfiguration.isServiceLevelLoggerEnabled()) {
			serviceLogger = 
				new EliteRollingFileLogger.Builder(getServerContext().getServerInstanceName(),
						dynAuthConfiguration.getLogLocation())
				.rollingType(dynAuthConfiguration.logRollingType())
				.rollingUnit(dynAuthConfiguration.logRollingUnit())
				.maxRolledUnits(dynAuthConfiguration.logMaxRolledUnits())
				.compressRolledUnits(dynAuthConfiguration.isCompressLogRolledUnits())
				.sysLogParameters(dynAuthConfiguration.getSysLogConfiguration().getHostIp(),
						dynAuthConfiguration.getSysLogConfiguration().getFacility())
				.build();
			serviceLogger.setLogLevel(dynAuthConfiguration.logLevel());
			
			LogManager.setLogger(getServiceIdentifier(), serviceLogger);
		}


		dynAuthServiceContext = new RadDynAuthServiceContext(){

			@Override
			public ESCommunicator getDriver(String driverInstanceId) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public AAAServerContext getServerContext() {
				return (AAAServerContext) RadDynAuthService.this.getServerContext();
			}
			@Override
			public String getServiceIdentifier() {
				return  RadDynAuthService.this.getServiceIdentifier();
			}

			@Override
			public void submitAsyncRequest(RadDynAuthRequest serviceRequest,
					RadDynAuthResponse serviceResponse,
					AsyncRequestExecutor<RadDynAuthRequest, RadDynAuthResponse> requestExecutor) {
				RadDynAuthService.this.submitAsyncRequest(serviceRequest, serviceResponse, requestExecutor);

			}

			@Override
			public RadPluginRequestHandler createPluginRequestHandler(
					List<PluginEntryDetail> prePluginList, List<PluginEntryDetail> postPluginList) {
				return createRadPluginReqHandler(prePluginList, postPluginList);
			}

			@Override
			public RadDynAuthConfiguration getDynAuthConfiguration() {
				return dynAuthConfiguration;
			}

		};

		super.initService();
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, String.valueOf(dynAuthConfiguration));
		}

		if(((AAAServerContext)getServerContext()).getServerConfiguration().getRadClientConfiguration() != null){
			this.dynAuthServiceMIB.init();
		}else{
			LogManager.getLogger().error(MODULE, "Can not initialize MIB counters for clients. Client configuration is null");			
		}

		DynAuthDBScanDetail dynAuthDBScanDetail  = dynAuthConfiguration.getExternalDataBaseDetail().getDatasourceDetail();
		if(dynAuthDBScanDetail.getIsEnabled()){
			if(dynAuthDBScanDetail.getDataSourceName()!=null){
				String dataSourceName = dynAuthDBScanDetail.getDataSourceName();
				DBDataSource dbScannerDataSource=null;
				DBDataSource dataSourceFromConf;
				Map<String, DBDataSource> tempDatasourceMap = ((AAAServerContext)getServerContext()).getServerConfiguration().getDatabaseDSConfiguration().getDatasourceMap();
				if(tempDatasourceMap!=null && tempDatasourceMap.size()>0){

					for(Map.Entry<String, DBDataSource> dataSoucce :tempDatasourceMap.entrySet()){
						dataSourceFromConf = dataSoucce.getValue();
						if(dataSourceFromConf!=null && dataSourceName.equals(dataSourceFromConf.getDataSourceName())){
							dbScannerDataSource = dataSourceFromConf;
							break;
						}	
					}
				}	
				if(dbScannerDataSource==null){
					throw new ServiceInitializationException("Invalid datasource configured for External System", ServiceRemarks.DATASOURCE_NOT_CONFIGURED);
				}
				
				DBConnectionManager dbConnectionManager	= DBConnectionManager.getInstance(dbScannerDataSource.getDataSourceName());
				try{
					dbConnectionManager.init(dbScannerDataSource, getServerContext().getTaskScheduler());
				}catch (Exception e) {
					//TODO MALAV: NARENDRA: throw new ServiceInitializationException("Invalid datasource configured for External System", ServiceRemarks.DATASOURCE_NOT_CONFIGURED);
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Dyna Auth DB Scanning Task for Dyna Auth service failed ,Reason : Failed to initialize datasouce configured for External System");
				}	

			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "DB interface for Dyna Auth Service is disabled.");
		}

		initServicePolicy();

		//JIRA-ELITEAAA-2163, initializing Radius Policy Manager used for applying Add Items
		try {
			RadiusPolicyManager.getInstance(getServiceIdentifier()).initCache(getServiceContext().getServerContext(), false);
		} catch (ManagerInitialzationException e) {
			LogManager.getLogger().error(MODULE,"Error Caching Radius-Policies. Reason :" + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		registerServiceSummaryWriter(new RadDynAuthServiceSummaryWriter(this.dynAuthServiceContext));
		RadDynaAuthConfigurationSetter radDynaAuthConfigurationSetter=new RadDynaAuthConfigurationSetter(dynAuthServiceContext);
		SetCommand.registerConfigurationSetter(radDynaAuthConfigurationSetter);

		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		try {
			RADIUS_DYNAUTH_CLIENT_MIB radius_DYNAUTH_CLIENT_MIB = new RADIUS_DYNAUTH_CLIENT_MIBImpl();
			radius_DYNAUTH_CLIENT_MIB.populate(mBeanServer, null);
			TableRadiusDynAuthServerTable dynAuthServerTable = new TableRadiusDynAuthServerTableImpl(radius_DYNAUTH_CLIENT_MIB,mBeanServer);
			Map<String, String> serverMap = RadiusDynAuthClientMIB.getServerMap();
			int serverIndex = 1;
			String dynauthServerIdentity = "";
			try{
				for (Entry<String, String> entry : serverMap.entrySet()) {
					RadiusDynAuthServerEntryMBeanImpl dynAuthServerEntry = new RadiusDynAuthServerEntryMBeanImpl(serverIndex++, entry.getValue());
					dynAuthServerTable.addEntry(dynAuthServerEntry,new ObjectName(dynAuthServerEntry.getObjectName()));
				}
			}catch(SnmpStatusException sse){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error while adding Entry for Dynauth Server: "+dynauthServerIdentity+" in Dynauth Server table. Reason: "+sse.getMessage());
				}
				LogManager.getLogger().trace(sse);
			}catch (MalformedObjectNameException ex) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error while adding Entry for Dynauth Server: "+dynauthServerIdentity+" in Dynauth Server table. Reason: "+ex.getMessage());
				}
				LogManager.getLogger().trace(ex);
			}
			RadiusDynAuthClientMIB.setDynAuthServerTable(dynAuthServerTable);
			((AAAServerContext)getServerContext()).registerSnmpMib(radius_DYNAUTH_CLIENT_MIB);
		} catch (Throwable t) {
			LogManager.getLogger().error(MODULE, "RADIUS Dynauth client MIB registration failed. Reason: " + t.getMessage());
			LogManager.getLogger().trace(MODULE, t);
		}
	
		try {
			RADIUS_DYNAUTH_SERVER_MIB radius_DYNAUTH_SERVER_MIB = new RADIUS_DYNAUTH_SERVER_MIBImpl();
			radius_DYNAUTH_SERVER_MIB.populate(mBeanServer, null);
			TableRadiusDynAuthClientTable dynAuthClientTable = new TableRadiusDynAuthClientTableImpl(radius_DYNAUTH_SERVER_MIB,mBeanServer);
			Map<String, String> clientMap = RadiusDynAuthServerMIB.getClientMap();
			int clientIndex = 1;
			String dynauthClientIdentity = "";
			try{
				for (Entry<String, String> entry : clientMap.entrySet()) {
					RadiusDynAuthClientEntryMBeanImpl dynAuthClientEntry = new RadiusDynAuthClientEntryMBeanImpl(clientIndex++, entry.getValue());
					dynAuthClientTable.addEntry(dynAuthClientEntry,new ObjectName(dynAuthClientEntry.getObjectName()));
				}
			}catch(SnmpStatusException sse){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error while adding Entry for Dynauth Client: "+dynauthClientIdentity+" in Dynauth Client table. Reason: "+sse.getMessage());
				}
				LogManager.getLogger().trace(sse);
			}catch (MalformedObjectNameException ex) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error while adding Entry for Dynauth Client: "+dynauthClientIdentity+" in Dynauth Client table. Reason: "+ex.getMessage());
				}
				LogManager.getLogger().trace(ex);
			}
			RadiusDynAuthServerMIB.setDynAuthClientTable(dynAuthClientTable);
			((AAAServerContext)getServerContext()).registerSnmpMib(radius_DYNAUTH_SERVER_MIB);
		} catch (Throwable t) {
			LogManager.getLogger().error(MODULE, "RADIUS Dynauth server MIB registration failed. Reason: " + t.getMessage());
			LogManager.getLogger().trace(MODULE, t);
		}
	}

	@Override
	protected synchronized boolean startService() {
		if (super.startService() == false) {
			return false;
		}
		
		startDBScanningTask();
		registerInMemoryRequestHandler();
		return true;
	}

	private void startDBScanningTask() {
		DynAuthDBScanDetail dynAuthDBScanDetail  = dynAuthConfiguration.getExternalDataBaseDetail().getDatasourceDetail();
		DynAuthDBScanningTask dynAuthDBScanningTask = new DynAuthDBScanningTask(dynAuthServiceContext, 
				dynAuthDBScanDetail.getScanningPeriod(), dynAuthDBScanDetail.getScanningPeriod()) {

			@Override
			public void handleLocalRequest(byte[] requestBytes, ILocalResponseListener responseListener) 
					throws UnknownHostException {
				RadDynAuthService.this.handleLocalRequest(new LocalRequestHandler(InetAddress.getLocalHost(),
							0, requestBytes, responseListener),
						responseListener);
			}};
			
		dynAuthDBScanningTask.init();
		getServerContext().getTaskScheduler().scheduleIntervalBasedTask(dynAuthDBScanningTask);
	}

	private void registerInMemoryRequestHandler() {
		EliteAAAServiceExposerManager.getInstance().registerDynAuthServiceInMemoryRequestHandler(new InMemoryRequestHandler() {
			
			@Override
			public void handleRequest(byte[] requestBytes,  ILocalResponseListener responseListener) 
					throws UnknownHostException {
				RadDynAuthService.this.handleLocalRequest(new LocalRequestHandler(InetAddress.getLocalHost(),
							0, requestBytes, responseListener), 
						responseListener);
			}
			
		});
	}

	@Override
	public void reInit() {
		super.reInit();
		this.dynAuthConfiguration = ((AAAServerContext)getServerContext()).getServerConfiguration().getDynAuthConfiguration();
		this.radClientConfiguration = ((AAAServerContext)getServerContext()).getServerConfiguration().getRadClientConfiguration();
		reInitLogLevel();
//		reInitServicePolicy();
	}


	private void reInitServicePolicy() {

		List<DynAuthServicePolicy> tmpDynAuthServicePolicies= new ArrayList<DynAuthServicePolicy>();
		DynAuthServicePolicy servicePolicy = null;
		Map<String, DynAuthServicePolicyConfiguration> dynAuthServConfMap = dynAuthServiceContext.getDynAuthConfiguration().getDynAuthServicePolicyConfiguraionMap();
		for(Entry<String, DynAuthServicePolicyConfiguration> entry:dynAuthServConfMap.entrySet()){
			try{
				servicePolicy = (DynAuthServicePolicy)getServicepolicyDetail(entry.getValue().getPolicyName());
				if(servicePolicy!=null){
					servicePolicy.reInit();
					tmpDynAuthServicePolicies.add(servicePolicy);
				}

			}catch(Exception e){
				LogManager.getLogger().error(MODULE, "Failed to Initialize the Service Policy " +  entry.getValue().getPolicyName() + ". Reason : " + e.getMessage());
				LogManager.getLogger().trace(e);
			}
		}
		this.dynDuthServicePolicies = tmpDynAuthServicePolicies;
	}

	private void reInitLogLevel() {
		if(dynAuthConfiguration.isServiceLevelLoggerEnabled()){
			serviceLogger.setLogLevel(dynAuthConfiguration.logLevel());
		}
	}

	@Override
	public final RadDynAuthRequest formServiceSpecificRequest(InetAddress sourceAddress, int sourcePort, byte[] requestBytes, SocketDetail serverSocketDetail) {
		return new RadiusDynAuthRequestImpl(requestBytes, sourceAddress, sourcePort, serverSocketDetail);
	}

	@Override
	public final RadDynAuthResponse formServiceSpecificResposne(RadDynAuthRequest serviceRequest) {
		return new RadiusDynAuthResponseImpl(serviceRequest.getAuthenticator(), serviceRequest.getIdentifier());
	}
	@Override
	public List<ICommand> getCliCommands() {	
		List<ICommand> cmdList = new ArrayList<ICommand>();

		cmdList.add(new DynAuthServCommand(getServerContext()) {		
			@Override
			public String getServiceThreadSummary() {				
				return getThreadSummary();
			}

			@Override
			public String getServicepolicyList() {
				return getServicePolicyList();
			}

			@Override
			public String getServicePolicyDetail(String commandParameters) {
				DynAuthServicePolicy servicePolicy = getServicepolicyDetail(commandParameters);
				if(servicePolicy!=null){
					return servicePolicy.toString();
				}else {
					return "Service Policy: "+ commandParameters+" is not available in the cache";
				}

			}
		});	

		return cmdList;
	}

	private DynAuthServicePolicy getServicepolicyDetail(String policyName){
		if (dynDuthServicePolicies != null && !dynDuthServicePolicies.isEmpty()) {
			for(DynAuthServicePolicy servicePolicy:dynDuthServicePolicies) {
				if(servicePolicy.getPolicyName().equalsIgnoreCase(policyName)){
					return servicePolicy;
				}
			}
		}
		return null;

	}
	private String getServicePolicyList(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		if (dynDuthServicePolicies != null && !dynDuthServicePolicies.isEmpty()) {
			out.println();
			out.println(" -- Rad DynAuth Service Policies -- ");
			out.println();
			for (DynAuthServicePolicy servicePolicy : dynDuthServicePolicies) {
				out.println(servicePolicy.getPolicyName());
			}
		} else {
			out.println("No policy available in the cache");
		}
		out.close();
		return stringBuffer.toString();	
	}

	private class RadiusDynAuthRequestImpl extends RadServiceRequestImpl implements RadDynAuthRequest {
		DynAuthServicePolicy servicePolicy;
		private RadiusRequestExecutor<RadDynAuthRequest, RadDynAuthResponse> executor;
		
		public RadiusDynAuthRequestImpl(byte[] requestBytes, InetAddress sourceAddress, int sourcePort, SocketDetail serverSocketDetail) {
			super(requestBytes, sourceAddress, sourcePort, serverSocketDetail);

		}
		private void setServicePolicy(DynAuthServicePolicy servicePolicy){
			this.servicePolicy = servicePolicy;
		}

		public DynAuthServicePolicy getServicePolicy() {
			return servicePolicy;
		}

		@Override
		public void addInfoAttribute(IRadiusAttribute radiusAttribute){
			if(radiusAttribute==null || radiusAttribute.getVendorID()==RadiusConstants.ELITECORE_VENDOR_ID){
				return;
			}
			super.addInfoAttribute(radiusAttribute);
		}
		
		public void setExecutor(RadiusRequestExecutor<RadDynAuthRequest, RadDynAuthResponse> executor) {
			this.executor = executor;
		}
		
		@Override
		public RadiusRequestExecutor<RadDynAuthRequest, RadDynAuthResponse> getExecutor() {
			return executor;
		}
		
		@Override
		public RadServiceRequest clone() {
			RadiusDynAuthRequestImpl clone = (RadiusDynAuthRequestImpl)super.clone();
			clone.executor = null;
			return clone;
		}
	}

	private class RadiusDynAuthResponseImpl extends RadServiceResponseImpl implements RadDynAuthResponse {

		public RadiusDynAuthResponseImpl(byte[] authenticator,int identifier) {
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

				msgAuthenticatorAttribute.setValueBytes(new byte[16]);

				responsePacket.refreshPacketHeader();

				responsePacket.setAuthenticator(getRequestAuthenticator());
				msgAuthenticatorAttribute.setValueBytes(RadiusUtility.HMAC("MD5", responsePacket.getBytes(), getClientData().getSharedSecret(getPacketType())));
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


	private void initServicePolicy(){

		List<DynAuthServicePolicy> tmpDynAuthServicePolicies= new ArrayList<DynAuthServicePolicy>();
		DynAuthServicePolicy servicePolicy = null;
		Map<String, DynAuthServicePolicyConfiguration> dynAuthServConfMap = dynAuthServiceContext.getDynAuthConfiguration().getDynAuthServicePolicyConfiguraionMap();
		for(Entry<String, DynAuthServicePolicyConfiguration> entry:dynAuthServConfMap.entrySet()){
			try{
				servicePolicy = new DynAuthServicePolicy(dynAuthServiceContext,entry.getKey());
				servicePolicy.init();
				tmpDynAuthServicePolicies.add(servicePolicy);				
			}catch(Exception e){
				LogManager.getLogger().error(MODULE, "Failed to Initialize the Service Policy " +  entry.getValue().getPolicyName() + ". Reason : " + e.getMessage());
				LogManager.getLogger().trace(e);
			}
		}
		this.dynDuthServicePolicies = tmpDynAuthServicePolicies;

	}
	@Override
	public boolean validatePacketAsPerRFC(RadDynAuthRequest request){


		IRadiusAttribute nasIdAttribute = request.getRadiusAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);

		if (nasIdAttribute == null) {
			nasIdAttribute = request.getRadiusAttribute(RadiusAttributeConstants.NAS_IDENTIFIER);
		}

		if (nasIdAttribute == null) {
			nasIdAttribute = request.getRadiusAttribute(RadiusAttributeConstants.NAS_IPV6_ADDRESS);
		}
		if (nasIdAttribute == null) {
			return false;
		}

		IRadiusAttribute sessionIdAttribute = request.getRadiusAttribute(RadiusAttributeConstants.USER_NAME);

		if (sessionIdAttribute == null) {
			sessionIdAttribute = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_ID);
		}
		if (sessionIdAttribute == null) {
			sessionIdAttribute = request.getRadiusAttribute(RadiusAttributeConstants.NAS_PORT);
		}
		if (sessionIdAttribute == null) {
			sessionIdAttribute = request.getRadiusAttribute(RadiusAttributeConstants.FRAMED_IP_ADDRESS);
		}
		if (sessionIdAttribute == null) {
			sessionIdAttribute = request.getRadiusAttribute(RadiusAttributeConstants.VENDOR_SPECIFIC);
		}
		if (sessionIdAttribute == null) {
			sessionIdAttribute = request.getRadiusAttribute(RadiusAttributeConstants.CALLED_STATION_ID);
		}
		if (sessionIdAttribute == null) {
			sessionIdAttribute = request.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
		}
		if (sessionIdAttribute == null) {
			sessionIdAttribute = request.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
		}
		if (sessionIdAttribute == null) {
			sessionIdAttribute = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_MULTI_SESSION_ID);
		}
		if (sessionIdAttribute == null) {
			sessionIdAttribute = request.getRadiusAttribute(RadiusAttributeConstants.NAS_PORT_ID);
		}
		if (sessionIdAttribute == null) {
			sessionIdAttribute = request.getRadiusAttribute(RadiusAttributeConstants.CUI);
		}
		if (sessionIdAttribute == null) {
			sessionIdAttribute = request.getRadiusAttribute(RadiusAttributeConstants.FRAMED_INTERFACE_ID);
		}        
		if (sessionIdAttribute == null) {
			sessionIdAttribute = request.getRadiusAttribute(RadiusAttributeConstants.FRAMED_IPV6_PREFIX);
		}         

		if (sessionIdAttribute == null) {
			return false;
		}
		return true;

	}

	@Override
	protected void incrementServTotalMalformedRequest(RadServiceRequest request) {
		RadClientData clientData = radClientConfiguration.getClientData(request.getClientIp());
		if(clientData != null) {
			if(request.getPacketType()==RadiusConstants.COA_REQUEST_MESSAGE) {
				dynAuthServiceMIB.listenRadiusDynAuthServTotalMalformedCOARequestsEvent(clientData.getClientIp());
			} else {
				dynAuthServiceMIB.listenRadiusDynAuthServTotalMalformedDisConnectRequestsEvent(clientData.getClientIp());
			}
		}
	}

	@Override
	protected void incrementDuplicateRequestReceivedCounter(
			ServiceRequest request) {
		RadDynAuthRequest radDynAuthRequest = (RadDynAuthRequest)request;
		RadClientData clientData = radClientConfiguration.getClientData(radDynAuthRequest.getClientIp());
		if(clientData != null) {
			switch(radDynAuthRequest.getPacketType()){
			case RadiusConstants.COA_REQUEST_MESSAGE:
				dynAuthServiceMIB.listenRadiusDynAuthServTotalDupCOARequestsEvent(clientData.getClientIp());
				break;
			case RadiusConstants.DISCONNECTION_REQUEST_MESSAGE:
				dynAuthServiceMIB.listenRadiusDynAuthServTotalDupDisConnectEvent(clientData.getClientIp());
				break;
			}
		}
	}

	@Override
	protected void handleAsyncRadiusRequest(RadDynAuthRequest request,
			RadDynAuthResponse response, ISession session) {

		if(response.isMarkedForDropRequest()) {
			response.setFurtherProcessingRequired(false);
			response.setProcessingCompleted(true);

		}else if (response.getPacketType() == RadiusConstants.COA_NAK_MESSAGE || response.getPacketType() != RadiusConstants.DISCONNECTION_NAK_MESSAGE) {
			response.setFurtherProcessingRequired(false);
		}else {
			DynAuthServicePolicy servicePolicy  =  request.getServicePolicy();
			
			request.getExecutor().resumeRequestExecution(session);

			if(!request.isFutherExecutionStopped()) {
				servicePolicy.handlePostPluginRequest(request, response, session);
			}

		}

	}
	/**
	 * This method returns LocalResponseListener parameter from ServiceRequest.
	 * LocalResponseListener will present in request as parameter if this
	 * request is coming from Database or Web service interface. 
	 *  
	 * @param request
	 * 
	 * @return ILocalResponseListener
	 */

	private ILocalResponseListener getLocalResponseListener(RadServiceRequest request){
		try{
			if(request.getParameter("LocalResponseListener")!=null){
				LogManager.getLogger().info(MODULE, "Notifying Local Response Listener as this request received from Database or WebService interface .");
				return (ILocalResponseListener)request.getParameter("LocalResponseListener");

			}	
		}catch (Exception e) {
			LogManager.getLogger().debug(MODULE, "Problem During Notifying Local Response Listener Reason : "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		return null;

	}
	private void setServicePolicy(RadDynAuthRequest request){		
		for(DynAuthServicePolicy dynAuthServicePolicy:dynDuthServicePolicies){
			if(dynAuthServicePolicy.assignRequest(request)){
				((RadiusDynAuthRequestImpl)request).setServicePolicy(dynAuthServicePolicy);
				IRadiusAttribute infoAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_SATISFIED_SERVICE_POLICY);
				if(infoAttribute != null){
					infoAttribute.setStringValue(dynAuthServicePolicy.getPolicyName());
					request.addInfoAttribute(infoAttribute);
				}
//				request.setHandlers(dynAuthServicePolicy.getHandlers());
				break;
			}
		}
	}
	public class RadDynaAuthConfigurationSetter implements ConfigurationSetter{
		private ServiceContext serviceContext;
		private static final String REALTIME = "realtime";

		public RadDynaAuthConfigurationSetter(ServiceContext serviceContext){
			this.serviceContext = serviceContext;
		}

		@Override
		public String execute(String... parameters) {
			if(parameters[2].equalsIgnoreCase("log")){
				if(parameters.length >= 4){
					if(((RadDynAuthServiceContext )serviceContext).getDynAuthConfiguration().isServiceLevelLoggerEnabled()){
						if (serviceLogger instanceof EliteRollingFileLogger) {
							EliteRollingFileLogger logger = (EliteRollingFileLogger)serviceLogger;
							if (logger.isValidLogLevel(parameters[3]) == false) {
								return "Invalid log level: " + parameters[3];
							}
							logger.setLogLevel(parameters[3]);
							return "Configuration Changed Successfully";

						}
					}else{
						return "Error : DynaAuth log are disabled";
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
			if(!parameters[1].equalsIgnoreCase("dynaauth")){
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
			out.println("Usage : set service dynaauth [<options>]");
			out.println();
			out.println("where options include:");		
			out.println("     log { all | debug | error | info | off | trace | warn }");
			out.println("     		Set the log level of the DynaAuth Service. ");			
			out.close();
			return stringWriter.toString();
		}

		@Override
		public String getHotkeyHelp() {
			return "'dynaauth':{'log':{'off':{},'error':{},'warn':{},'info':{},'debug':{},'trace':{},'all':{}}}";
		}

		@Override
		public int getConfigurationSetterType() {
			return SERVICE_TYPE;
		}
	}


	@Override
	public boolean isDuplicateDetectionEnabled() {
		return dynAuthConfiguration.isDuplicateRequestDetectionEnabled();
	}

	@Override
	public int getDuplicateDetectionQueuePurgeInterval() {
		return dynAuthConfiguration.getDupicateRequestQueuePurgeInterval();
	}

	@Override
	public String getServiceName() {
		return "DynAuth Service";
	}

	@Override
	public List<SocketDetail> getSocketDetails() {
		return dynAuthConfiguration.getSocketDetails();
	}
	
	@Override
	protected final void shutdownLogger() {
		Closeables.closeQuietly(serviceLogger);
	}
	@Override
	public int getDefaultServicePort() {
		return AAAServerConstants.DEFAULT_RAD_DYNAUTH_PORT;
	}

	@Override
	protected boolean isSessionRelease(RadDynAuthRequest request, RadDynAuthResponse response) {
		return false;
	}
	
	@Override
	protected ISession getOrCreateSession(RadDynAuthRequest request) {
		AAAServerContext serverContext = (AAAServerContext) getServerContext();
		Map<String, ImdgIndexDetail> radiusIndexFieldMap = serverContext.getServerConfiguration()
				.getImdgConfigurable().getImdgConfigData().getImdgRadiusConfig().getRadiusIndexFieldMap();
		
		ImdgIndexDetail framedIp = radiusIndexFieldMap.get(RadiusAttributeConstants.FRAMED_IP_ADDRESS_STR);
		ImdgIndexDetail callingStation = radiusIndexFieldMap.get(RadiusAttributeConstants.CALLING_STATION_ID_STR);

		String framedIpAddress = getAttributeValue(request, framedIp.getAttributeList());
		String callingStationId = getAttributeValue(request, callingStation.getAttributeList());

		ISession session = null;
		
		if (!Strings.isNullOrBlank(framedIpAddress)) {
			session = retriveSession(framedIp.getImdgIndex(), framedIpAddress);
		} else if (!Strings.isNullOrBlank(callingStationId)) {
			session = retriveSession(callingStation.getImdgIndex(), callingStationId);
		} 

		return session;
	}

	private ISession retriveSession(String index, String attributeValue) {
		Collection<String> sessionIdentities = ((AAAServerContext)getServerContext()).search(index, attributeValue);
		if (!sessionIdentities.isEmpty()) {
			return (((AAAServerContext)getServerContext())).getOrCreateRadiusSession(sessionIdentities.iterator().next());
		} else {
			LogManager.getLogger().info(MODULE, "A Radius session is not found for this request.");
			return ISession.NO_SESSION;
		}
	}
	
	private String getAttributeValue(RadServiceRequest request, List<String> attributeList) {
		for(String attribute : attributeList) {
			IRadiusAttribute radAttribute = request.getRadiusAttribute(attribute);
			if (radAttribute != null) {
				return radAttribute.getStringValue();
			}
		}
		return null;
	}

}
