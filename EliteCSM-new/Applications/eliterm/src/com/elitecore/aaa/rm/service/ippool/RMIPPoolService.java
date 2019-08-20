package com.elitecore.aaa.rm.service.ippool;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.conf.RMServerConfiguration;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.util.cli.RadiusLogMonitor;
import com.elitecore.aaa.core.util.cli.SetCommand;
import com.elitecore.aaa.core.util.cli.SetCommand.ConfigurationSetter;
import com.elitecore.aaa.mibs.rm.ippool.server.RMIPPoolServiceMIBListener;
import com.elitecore.aaa.radius.conf.RadClientConfiguration;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.plugins.core.RadPluginManager;
import com.elitecore.aaa.radius.plugins.core.RadPluginRequestHandler;
import com.elitecore.aaa.radius.policies.servicepolicy.RadiusServicePolicy;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.base.BaseRadiusService;
import com.elitecore.aaa.radius.service.base.RadServiceRequestImpl;
import com.elitecore.aaa.radius.service.base.RadServiceResponseImpl;
import com.elitecore.aaa.rm.conf.RMIPPoolConfiguration;
import com.elitecore.aaa.rm.service.ippool.handlers.RMIPPoolServiceHandler;
import com.elitecore.aaa.rm.service.ippool.handlers.RMIPPoolServiceHandlerImpl;
import com.elitecore.aaa.rm.service.ippool.handlers.RMIPPoolSessionCloserTask;
import com.elitecore.aaa.rm.service.ippool.snmp.IP_POOL_SERVICE_MIBImpl;
import com.elitecore.aaa.rm.service.ippool.snmp.IpPoolAAAClientEntryMBeanImpl;
import com.elitecore.aaa.rm.service.ippool.snmp.TableIpPoolAAAClientStatisticsTableImpl;
import com.elitecore.aaa.rm.service.ippool.snmp.TableIpPoolNASClientStatisticsTableImpl;
import com.elitecore.aaa.rm.service.ippool.snmp.autogen.IP_POOL_SERVICE_MIB;
import com.elitecore.aaa.rm.service.ippool.snmp.autogen.TableIpPoolAAAClientStatisticsTable;
import com.elitecore.aaa.rm.service.ippool.snmp.autogen.TableIpPoolNASClientStatisticsTable;
import com.elitecore.aaa.rm.util.cli.IPPoolServCommand;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
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
import com.elitecore.coreeap.util.constants.AttributeConstants;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.sun.management.snmp.SnmpStatusException;

public class RMIPPoolService extends BaseRadiusService<RMIPPoolRequest, RMIPPoolResponse> {

	private static final String MODULE = "RM-IPPOOL";
	private static final String SERVICE_ID = "IPPool-SERVICE";

	private RMIPPoolServiceContext iPPoolServiceContext;
	private RMIPPoolConfiguration iPPoolServiceConfiguration;
	private RMIPPoolServiceHandler rmIPPoolServiceHandler;
	private RMIPPoolServiceMIBListener ipPoolServiceMIBListener;
	private AAAServerContext context;
	public static final String IP_ADDRESS_RELEASE_MESSAGE = "ipAddress release";
	public static final String IP_UPDATE_MESSAGE = "ipAddress update";
	public static final String ACCT_UPDATE_MESSAGE = "ACCT_UPDATE";
	private ScheduledExecutorService scheduledExecutorService;
	private EliteRollingFileLogger serviceLogger;
	private IP_POOL_SERVICE_MIB ip_pool_service_MIB; 
		
	public RMIPPoolService(AAAServerContext context, RadPluginManager pluginManager, RadiusLogMonitor logMonitor) {
		super(context, pluginManager, logMonitor);
		this.context=context;
		iPPoolServiceConfiguration = ((RMServerConfiguration)context.getServerConfiguration()).getIPPoolConfiguration();
		ipPoolServiceMIBListener = new RMIPPoolServiceMIBListener(new RMIPPoolServiceMIBCounters(), context.getServerConfiguration().getRadClientConfiguration());
		ip_pool_service_MIB = new IP_POOL_SERVICE_MIBImpl(ipPoolServiceMIBListener);
	}

	protected void initService() throws ServiceInitializationException  {

		iPPoolServiceContext = new RMIPPoolServiceContext() {
	
			@Override
			public RMIPPoolConfiguration getIPPoolConfiguration() {
				return RMIPPoolService.this.getRMIPPoolConfiguration();
			}

			@Override
			public String getServiceIdentifier() {
				return RMIPPoolService.this.getServiceIdentifier();
			}

			@Override
			public RadPluginRequestHandler createPluginRequestHandler(
					List<PluginEntryDetail> prePluginList, List<PluginEntryDetail> postPluginList) {
				return createRadPluginReqHandler(prePluginList, postPluginList);
			}

			@Override
			public AAAServerContext getServerContext() {
				return (AAAServerContext) RMIPPoolService.this.getServerContext();
			}

			@Override
			public void submitAsyncRequest(RMIPPoolRequest serviceRequest,RMIPPoolResponse serviceResponse,
					AsyncRequestExecutor<RMIPPoolRequest, RMIPPoolResponse> requestExecutor) {
				
			}

			@Override
			public ESCommunicator getDriver(String driverInstanceId) {
				// TODO Auto-generated method stub
				return null;
			}
		};

		super.initService();
		initHandler();

		LogManager.getLogger().info(MODULE, String.valueOf(iPPoolServiceConfiguration)); 
		if (iPPoolServiceConfiguration.isServiceLevelLoggerEnabled()) {
			serviceLogger = 
				new EliteRollingFileLogger.Builder(getServerContext().getServerInstanceName(),
						getServerContext().getServerHome() + File.separator
							+ "logs" + File.separator + "rm-ippool")
				.rollingType(iPPoolServiceConfiguration.logRollingType())
				.rollingUnit(iPPoolServiceConfiguration.logRollingUnit())
				.maxRolledUnits(iPPoolServiceConfiguration.logMaxRolledUnits())
				.compressRolledUnits(iPPoolServiceConfiguration.isCompressLogRolledUnits())
				.sysLogParameters(iPPoolServiceConfiguration.getSysLogConfiguration().getHostIp(),
						iPPoolServiceConfiguration.getSysLogConfiguration().getFacility())
				.build();
			serviceLogger.setLogLevel(iPPoolServiceConfiguration.logLevel());
			
			LogManager.setLogger(getServiceIdentifier(), serviceLogger);
		}
		
		
		LogManager.getLogger().info(MODULE, "RM IPPool service initilized successfully.");

		if (iPPoolServiceConfiguration.isAutoSessionClosureEnabled()) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, " AutoSessionClosure is started for RM-IPPool service ");
			}
			scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new EliteThreadFactory(getServiceIdentifier(), getServiceIdentifier() + "-CLEANUP-TH", Thread.MAX_PRIORITY));			
			RMIPPoolSessionCloserTask rmipPoolSessionCloserTask = new RMIPPoolSessionCloserTask((AAAServerContext)getServerContext());
			scheduledExecutorService.scheduleWithFixedDelay(rmipPoolSessionCloserTask, iPPoolServiceConfiguration.getExecutionInterval(), iPPoolServiceConfiguration.getExecutionInterval(), TimeUnit.SECONDS);
		}
		
		RadClientConfiguration radClientConfiguration = ((AAAServerContext)getServerContext()).getServerConfiguration().getRadClientConfiguration();
		if(radClientConfiguration != null){
			this.ipPoolServiceMIBListener.init();
		}else{
			LogManager.getLogger().error(MODULE, "Can not initialize MIB counters for clients. Client configuration is null");			
		}

		registerServiceSummaryWriter(new RMIPPoolServiceSummaryWriter((RMIPPoolServiceContext)getServiceContext(),ipPoolServiceMIBListener));
		RMIPPoolConfigurationSetter ippoolConfigurationSetter = new RMIPPoolConfigurationSetter(iPPoolServiceContext);
		SetCommand.registerConfigurationSetter(ippoolConfigurationSetter);

		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		try {
			List<String> aaaClientList =((AAAServerContext) getServerContext()).getServerConfiguration().getRadClientConfiguration().getClientAddresses();
			ip_pool_service_MIB.populate(mBeanServer, null);
			
			TableIpPoolAAAClientStatisticsTable aaaClientTable = new TableIpPoolAAAClientStatisticsTableImpl(ip_pool_service_MIB,mBeanServer);

			for (int aaaClientIndex = 0 ; aaaClientIndex < aaaClientList.size() ;aaaClientIndex++) {
				String aaaClientIdentity = aaaClientList.get(aaaClientIndex);
				IpPoolAAAClientEntryMBeanImpl aaaClientEntry = new IpPoolAAAClientEntryMBeanImpl(aaaClientIndex+1,aaaClientIdentity,ipPoolServiceMIBListener);
				try{
					aaaClientTable.addEntry(aaaClientEntry,new ObjectName(aaaClientEntry.getObjectName()));
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Client Entry for AAA Client: " + aaaClientIdentity + " successfully added at Index: " + (aaaClientIndex+1));
				}catch (SnmpStatusException e) {
					LogManager.getLogger().error(MODULE, "Error while adding Entry for AAA Client: " + aaaClientIdentity + " in the AAA Client table. Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}catch(MalformedObjectNameException ex){
					LogManager.getLogger().error(MODULE, "Error while adding Entry for AAA Client: " + aaaClientIdentity + " in the AAA Client table. Reason: " + ex.getMessage());
					LogManager.getLogger().trace(MODULE, ex);
				}
			}
			TableIpPoolNASClientStatisticsTable nasClientTable = new TableIpPoolNASClientStatisticsTableImpl(ip_pool_service_MIB,mBeanServer);
			ipPoolServiceMIBListener.setNasClientTable(nasClientTable);
			context.registerSnmpMib(ip_pool_service_MIB);

		} catch (Throwable e) {
			LogManager.getLogger().error(MODULE, "Failed to initialize IP-POOL-SERVICE-MIB. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}
	
	@Override
	public boolean stopService() {
		super.stopService();
		if(scheduledExecutorService != null){			
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Stop session closer task for IP-Pool Service");
			}
			scheduledExecutorService.shutdown();
		}
		return true;
	}
	
	@Override
	public void reInit(){
		super.reInit();
		this.iPPoolServiceConfiguration = ((RMServerConfiguration)this.context.getServerConfiguration()).getIPPoolConfiguration();
		reInitLogLevel();
		reInitServiceHandler();
	}
	
	private void reInitServiceHandler() {
		try {
			rmIPPoolServiceHandler.reInit();
		} catch (InitializationFailedException e) {
			LogManager.getLogger().warn(MODULE, " Fail to Re-Initialize IPPool Service Handler, Reason: "+e.getMessage());
		}
	}

	private void reInitLogLevel() {
		if(iPPoolServiceConfiguration.isServiceLevelLoggerEnabled()){
			serviceLogger.setLogLevel(iPPoolServiceConfiguration.logLevel());
		}
	}

	// Initializing RMIPPoolHandler
	public void initHandler() throws ServiceInitializationException  {
		try {
			rmIPPoolServiceHandler = new RMIPPoolServiceHandlerImpl(iPPoolServiceContext);
			rmIPPoolServiceHandler.init();
		} catch (InitializationFailedException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, " Fail to Initialize Handler "+e.getMessage());
			}
			context.generateSystemAlert(AlertSeverity.WARN, Alerts.DATABASE_GENERIC, 
					MODULE, "Fail to Initialize Handler", 0,
					"Fail to Initialize Handler");
			
			throw new ServiceInitializationException("Fail to Initialize Handler . "+e.getMessage(), ServiceRemarks.UNKNOWN_PROBLEM);
		}
	}

	protected boolean startService() {
		return super.startService();
	}

	@Override
	public List<PluginEntryDetail> getRadPostPluginList() {
		return iPPoolServiceConfiguration.getPostPluginList();
	}

	@Override
	public List<PluginEntryDetail> getRadPrePluginList() {
		return iPPoolServiceConfiguration.getPrePluginList();
	}

	@Override
	protected void incrementRequestReceivedCounter(String clientAddress) {
		ipPoolServiceMIBListener.listenRMIPPoolServTotalRequestsEvent();
		ipPoolServiceMIBListener.listenRMIPPoolServTotalRequestsEvent(clientAddress);
	}
	
	@Override
	protected void incrementResponseCounter(ServiceRequest request, ServiceResponse response) {
		RMIPPoolResponse ipPoolResponse = (RMIPPoolResponse) response;
		ipPoolServiceMIBListener.listenRMIPPoolServTotalResponsesEvent(ipPoolResponse.getClientData().getClientIp());

		IRadiusAttribute nasIdentity = ((RMIPPoolRequest)request).getRadiusAttribute(true,RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_REQUESTER_ID);
		if(nasIdentity != null)
			ipPoolServiceMIBListener.listenNASIPPoolServTotalResponse(nasIdentity.getStringValue());

		int packetType = ipPoolResponse.getPacketType();
		
		switch (packetType) {
		case RadiusConstants.ACCESS_ACCEPT_MESSAGE:
			
			ipPoolServiceMIBListener.listenIPAddressOfferTotalResponse();
			ipPoolServiceMIBListener.listenIPAddressOfferTotalResponse(ipPoolResponse.getClientData().getClientIp());
			
			if(nasIdentity != null)
				ipPoolServiceMIBListener.listenNASIPPoolServTotalOfferResponse(nasIdentity.getStringValue());
			
			break;
		case RadiusConstants.ACCESS_REJECT_MESSAGE:
			ipPoolServiceMIBListener.listenIPAddressDeclineTotalResponse();
			ipPoolServiceMIBListener.listenIPAddressDeclineTotalResponse(ipPoolResponse.getClientData().getClientIp());
			
			if(nasIdentity != null)
				ipPoolServiceMIBListener.listenNASIPPoolServTotalDeclineResponse(nasIdentity.getStringValue());
			break;
	}
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
		case RadiusConstants.ACCESS_ACCEPT_MESSAGE:
			ipPoolServiceMIBListener.listenIPAddressOfferTotalResponse();
			ipPoolServiceMIBListener.listenIPAddressOfferTotalResponse(clientIp);
			break;
		case RadiusConstants.ACCESS_REJECT_MESSAGE:
			ipPoolServiceMIBListener.listenIPAddressDeclineTotalResponse();
			ipPoolServiceMIBListener.listenIPAddressDeclineTotalResponse(clientIp);
			break;
		}
	}

	@Override
	protected void incrementRequestDroppedCounter(ServiceRequest request) {
		ipPoolServiceMIBListener.listenRMIPPoolServTotalPacketsDroppedEvent(((RMIPPoolRequest)request).getClientIp());
		}
	
	@Override
	protected void incrementRequestDroppedCounter(String clientAddress , ServiceRequest request){
		ipPoolServiceMIBListener.listenRMIPPoolServTotalPacketsDroppedEvent(clientAddress);
		}

	@Override
	protected void incrementServTotalBadAuthenticators(String clientAddress) {
		ipPoolServiceMIBListener.listenRMIPPoolServTotalBadAuthenticatorsEvent(clientAddress);
	}

	@Override
	protected void incrementServTotalMalformedRequest(RadServiceRequest request) {
		ipPoolServiceMIBListener.listenRMIPPoolServTotalMalformedRequestsEvent(request.getClientIp());
	}

	@Override
	protected void incrementServTotalInvalidRequests(ServiceRequest request) {
		ipPoolServiceMIBListener.listenRMIPPoolServTotalInvalidRequestsEvent();
		}

	private void incrementIPPoolServTotalUnknownTypes(String clientAddress){
		ipPoolServiceMIBListener.listenRMIPPoolServTotalUnknownTypesEvent(clientAddress);
		}
	
	@Override
	protected void incrementDuplicateRequestReceivedCounter(
			ServiceRequest request) {
		RMIPPoolRequest rmIpPoolRequest = (RMIPPoolRequest)request;
		ipPoolServiceMIBListener.listenRMIPPoolServTotalDupRequestsEvent(rmIpPoolRequest.getClientIp());
	}

	@Override
	public void preRequestProcess(RMIPPoolRequest request,
			RMIPPoolResponse response) {

		RMIPPoolRequest ipPoolRequest = (RMIPPoolRequest)request;

		IRadiusAttribute nasID = ipPoolRequest.getRadiusAttribute(RadiusAttributeConstants.NAS_IDENTIFIER_STR);
		IRadiusAttribute nasIPV4Adr = ipPoolRequest.getRadiusAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS_STR);
		IRadiusAttribute nasIPV6Adr = ipPoolRequest.getRadiusAttribute(RadiusAttributeConstants.NAS_IPV6_ADDRESS_STR);

		IRadiusAttribute nasIdentity = Dictionary.getInstance().getAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_REQUESTER_ID);
		
		if(nasIdentity != null){
			if(nasID != null){
				nasIdentity.setStringValue(nasID.getStringValue());
				ipPoolRequest.addInfoAttribute(nasIdentity);

			}else if (nasIPV4Adr != null){
				nasIdentity.setStringValue(nasIPV4Adr.getStringValue());
				ipPoolRequest.addInfoAttribute(nasIdentity);

			}else if (nasIPV6Adr != null) {
				nasIdentity.setStringValue(nasIPV6Adr.getStringValue());
				ipPoolRequest.addInfoAttribute(nasIdentity);
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Attribute for Attribute-ID: "+RadiusConstants.ELITECORE_VENDOR_ID +":"+RadiusAttributeConstants.ELITE_REQUESTER_ID+" not added to response packet.Reason: Attribute not found in dictionary");
		}
	}

	@Override
	protected final void handleRadiusRequest(RMIPPoolRequest request,
			RMIPPoolResponse response, ISession session) {
		
		IRadiusAttribute nasId = request.getRadiusAttribute(true, RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_REQUESTER_ID);
		if(nasId != null){
			ipPoolServiceMIBListener.listenNASIPPoolServTotalRequest(nasId.getStringValue());
		}
		
		if(request.getPacketType()== RadiusConstants.ACCESS_REQUEST_MESSAGE){
			ipPoolServiceMIBListener.listenIPAddressDiscoverTotalRequest();
			ipPoolServiceMIBListener.listenIPAddressDiscoverTotalRequest(request.getClientIp());
			
			IRadiusAttribute nasIdentity = request.getRadiusAttribute(true,RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_REQUESTER_ID);
			if(nasIdentity != null)
				ipPoolServiceMIBListener.listenNASIPPoolServTotalDiscoverRequest(nasIdentity.getStringValue());
			
			response.setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
			rmIPPoolServiceHandler.handleIPAllocateRequest(request,response);
			
		}else if (request.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE) {
			IRadiusAttribute iRadiusAttribute=request.getRadiusAttribute(AttributeConstants.ACCT_STATUS_TYPE_STR);
			if(iRadiusAttribute.getIntValue()==RadiusAttributeValuesConstants.START){
				ipPoolServiceMIBListener.listenIPAddressTotalAllocationRequest();
				ipPoolServiceMIBListener.listenIPAddressTotalAllocationRequest(request.getClientIp());
			
				IRadiusAttribute nasIdentity = request.getRadiusAttribute(true,RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_REQUESTER_ID);
				if(nasIdentity != null)
					ipPoolServiceMIBListener.listenNASIPPoolServTotalAllocationRequest(nasIdentity.getStringValue());
				
				if(!areMandatoryAttributesAvailable(request)){
					ipPoolServiceMIBListener.listenRMIPPoolServTotalInvalidRequests(request.getClientIp());
					if(nasIdentity != null)
						ipPoolServiceMIBListener.listenNASIPPoolServTotalInvalidRequests(nasIdentity.getStringValue());
					response.markForDropRequest();
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Receive Accounting request is Dropped. Reason: Request does not contains either Acct-Session-Id or Class-Avpair or Framed-Address.");
					return;
				}
				response.setPacketType(RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE);
				rmIPPoolServiceHandler.handleIPUpdateOrReleaseRequest(request,response,IP_UPDATE_MESSAGE);
			}
			else if(iRadiusAttribute.getIntValue()==RadiusAttributeValuesConstants.STOP){
				ipPoolServiceMIBListener.listenIPAddressTotalReleaseRequest();
				ipPoolServiceMIBListener.listenIPAddressTotalReleaseRequest(request.getClientIp());

				IRadiusAttribute nasIdentity = request.getRadiusAttribute(true,RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_REQUESTER_ID);
				if(nasIdentity != null)
					ipPoolServiceMIBListener.listenNASIPPoolServTotalReleaseRequest(nasIdentity.getStringValue());
				
				if(!areMandatoryAttributesAvailable(request)){
					ipPoolServiceMIBListener.listenRMIPPoolServTotalInvalidRequests(request.getClientIp());
					if(nasIdentity != null)
						ipPoolServiceMIBListener.listenNASIPPoolServTotalInvalidRequests(nasIdentity.getStringValue());
					response.markForDropRequest();
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Receive Accounting request is Dropped. Reason: Request does not contains either Acct-Session-Id or Class-Avpair or Framed-Address.");
					return;
				}
				response.setPacketType(RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE);
				rmIPPoolServiceHandler.handleIPUpdateOrReleaseRequest(request,response,IP_ADDRESS_RELEASE_MESSAGE );
			} else if ( iRadiusAttribute.getIntValue()==RadiusAttributeValuesConstants.INTERIM_UPDATE){
				ipPoolServiceMIBListener.listenIPAddressTotalUpdateRequest();
				ipPoolServiceMIBListener.listenIPAddressTotalUpdateRequest(request.getClientIp());
				
				IRadiusAttribute nasIdentity = request.getRadiusAttribute(true,RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_REQUESTER_ID);
				if(nasIdentity != null)
					ipPoolServiceMIBListener.listenNASIPPoolServTotalUpdateRequest(nasIdentity.getStringValue());
				
				if(!areMandatoryAttributesAvailable(request)){
					ipPoolServiceMIBListener.listenRMIPPoolServTotalInvalidRequests(request.getClientIp());
					if(nasIdentity != null)
						ipPoolServiceMIBListener.listenNASIPPoolServTotalInvalidRequests(nasIdentity.getStringValue());
					response.markForDropRequest();
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Receive Accounting request is Dropped. Reason: Request does not contains either Acct-Session-Id or Class-Avpair or Framed-Address.");
					return;
				}
				response.setPacketType(RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE);
				rmIPPoolServiceHandler.handleIPUpdateOrReleaseRequest(request,response,ACCT_UPDATE_MESSAGE );
			}
		} else if (request.getPacketType() == RadiusConstants.IP_ADDRESS_ALLOCATE_MESSAGE) {
			ipPoolServiceMIBListener.listenIPAddressTotalAllocationRequest();
			ipPoolServiceMIBListener.listenIPAddressTotalAllocationRequest(request.getClientIp());

			IRadiusAttribute nasIdentity = request.getRadiusAttribute(true,RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_REQUESTER_ID);
			if(nasIdentity != null)
				ipPoolServiceMIBListener.listenNASIPPoolServTotalDiscoverRequest(nasIdentity.getStringValue());
			
			if(!areMandatoryAttributesAvailable(request)){
				ipPoolServiceMIBListener.listenRMIPPoolServTotalInvalidRequests(request.getClientIp());
				if(nasIdentity != null)
					ipPoolServiceMIBListener.listenNASIPPoolServTotalInvalidRequests(nasIdentity.getStringValue());
				response.markForDropRequest();
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Receive Accounting request is Dropped. Reason: Request does not contains either Acct-Session-Id or Class-Avpair or Framed-Address.");
				return;
			}
			
			response.setPacketType(RadiusConstants.IP_ADDRESS_ALLOCATE_MESSAGE);    		
    		rmIPPoolServiceHandler.handleIPAllocateRequest(request,response);
		} else if (request.getPacketType() == RadiusConstants.IP_ADDRESS_RELEASE_MESSAGE) {
       		ipPoolServiceMIBListener.listenIPAddressTotalReleaseRequest();
       		ipPoolServiceMIBListener.listenIPAddressTotalReleaseRequest(request.getClientIp());
       	
       		IRadiusAttribute nasIdentity = request.getRadiusAttribute(true,RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_REQUESTER_ID);
       		if(nasIdentity != null)
       			ipPoolServiceMIBListener.listenNASIPPoolServTotalReleaseRequest(nasIdentity.getStringValue());
       		
       		if(!areMandatoryAttributesAvailable(request)){
				ipPoolServiceMIBListener.listenRMIPPoolServTotalInvalidRequests(request.getClientIp());
				if(nasIdentity != null)
					ipPoolServiceMIBListener.listenNASIPPoolServTotalInvalidRequests(nasIdentity.getStringValue());
				response.markForDropRequest();
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Receive Accounting request is Dropped. Reason: Request does not contains either Acct-Session-Id or Class-Avpair or Framed-Address.");
				return;
			}
       		response.setPacketType(RadiusConstants.IP_ADDRESS_RELEASE_MESSAGE);
			rmIPPoolServiceHandler.handleIPUpdateOrReleaseRequest(request,response,IP_ADDRESS_RELEASE_MESSAGE );
		} else if (request.getPacketType() == RadiusConstants.IP_UPDATE_MESSAGE) {
       		ipPoolServiceMIBListener.listenIPAddressTotalUpdateRequest();
       		ipPoolServiceMIBListener.listenIPAddressTotalUpdateRequest(request.getClientIp());
       	
       		IRadiusAttribute nasIdentity = request.getRadiusAttribute(true,RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_REQUESTER_ID);
       		if(nasIdentity != null)
				ipPoolServiceMIBListener.listenNASIPPoolServTotalUpdateRequest(nasIdentity.getStringValue());
       		
       		if(!areMandatoryAttributesAvailable(request)){
				ipPoolServiceMIBListener.listenRMIPPoolServTotalInvalidRequests(request.getClientIp());
				if(nasIdentity != null)
					ipPoolServiceMIBListener.listenNASIPPoolServTotalInvalidRequests(nasIdentity.getStringValue());
				response.markForDropRequest();
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Receive Accounting request is Dropped. Reason: Request does not contains either Acct-Session-Id or Class-Avpair or Framed-Address.");
				return;
			}
			response.setPacketType(RadiusConstants.IP_UPDATE_MESSAGE);
       		rmIPPoolServiceHandler.handleIPUpdateOrReleaseRequest(request,response,IP_UPDATE_MESSAGE);
		} else {			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE,"Received Unknown Request Type : " + request.getPacketType() +", Dropping request");
			}
			response.markForDropRequest();
			incrementIPPoolServTotalUnknownTypes(request.getClientIp());
		}
	}

	/**
     * This method is mean to used for validate the request come for IP-Pool Service.
     * if request contains the Acct-Session-ID , Class attribute , Framed-IP-Address
     * than it is valid for IP-Pool service.
     * else request is invalid.
     */

    private boolean areMandatoryAttributesAvailable(RadServiceRequest request){

        IRadiusAttribute accSessionId = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_ID_STR);
        IRadiusAttribute classAttrbt = request.getRadiusAttribute(RadiusAttributeConstants.CLASS_STR);
        IRadiusAttribute framedIpAddr = request.getRadiusAttribute(RadiusAttributeConstants.FRAMED_IP_ADDRESS_STR);

        if(accSessionId == null){
            return false;
        }else{
            return classAttrbt != null || framedIpAddr != null ;
        }
    }

	@Override
	protected void handleStatusServerMessageRequest(RMIPPoolRequest serviceRequest,
			RMIPPoolResponse serviceResponse) {
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
	protected boolean isValidRequest(RMIPPoolRequest radServiceRequest,
			RMIPPoolResponse radServiceRsponse) {
		return true;
	}

	@Override
	protected int getMainThreadPriority() {
		return iPPoolServiceConfiguration.mainThreadPriority();
	}

	@Override
	protected int getMaxRequestQueueSize() {
		return iPPoolServiceConfiguration.maxRequestQueueSize();
	}

	@Override
	protected int getMaxThreadPoolSize() {
		return iPPoolServiceConfiguration.maxThreadPoolSize();
	}

	@Override
	protected int getMinThreadPoolSize() {
		return iPPoolServiceConfiguration.minThreadPoolSize();
	}

	@Override
	protected int getSocketReceiveBufferSize() {
		return iPPoolServiceConfiguration.socketReceiveBufferSize();
	}

	@Override
	protected int getSocketSendBufferSize() {
		return iPPoolServiceConfiguration.socketSendBufferSize();
	}

	@Override
	protected int getThreadKeepAliveTime() {
		return iPPoolServiceConfiguration.threadKeepAliveTime();
	}

	@Override
	protected int getWorkerThreadPriority() {
		return iPPoolServiceConfiguration.workerThreadPriority();
	}

	@Override
	public String getKey() {
		return iPPoolServiceConfiguration.getKey();
	}

	@Override
	protected ServiceContext getServiceContext() {
		return this.iPPoolServiceContext;
	}

	@Override
	public void readConfiguration() throws LoadConfigurationException {

	}

	@Override
	public String getServiceIdentifier() {
		return SERVICE_ID;
	}

	public RMIPPoolConfiguration getRMIPPoolConfiguration() {
		return iPPoolServiceConfiguration;
	}

	@Override
	public RMIPPoolRequest formServiceSpecificRequest(
			InetAddress sourceAddress, int sourcePort, byte[] requestBytes, SocketDetail serverSocketDetail) {
		return new RMIPPoolRequestImpl(requestBytes, sourceAddress, sourcePort, serverSocketDetail);
	}

	private class RMIPPoolRequestImpl extends RadServiceRequestImpl implements
			RMIPPoolRequest {
		private RadiusServicePolicy<RMIPPoolRequest, RMIPPoolResponse> servicePolicy;

		public RMIPPoolRequestImpl(byte[] requestBytes,InetAddress sourceAddress, int sourcePort, SocketDetail serverSocketDetail) {
			super(requestBytes, sourceAddress, sourcePort, serverSocketDetail);
		}

		@SuppressWarnings("unused")
		public RadiusServicePolicy<RMIPPoolRequest, RMIPPoolResponse> getServicePolicy() {
			return servicePolicy;
		}
		
		@Override
		public RadServiceRequest clone() {
			return super.clone();
		}
	}

	@Override
	public RMIPPoolResponse formServiceSpecificResposne(RMIPPoolRequest serviceRequest) {
		return new RMIPPoolResponseImpl(serviceRequest.getAuthenticator(), serviceRequest.getIdentifier());
	}

	private class RMIPPoolResponseImpl extends RadServiceResponseImpl implements
			RMIPPoolResponse {

		public RMIPPoolResponseImpl(byte[] authenticator, int identifier) {
			super(authenticator, identifier);
		}

		@Override
		public RadiusPacket generatePacket() {
			RadiusPacket responsePacket = new RadiusPacket();
			responsePacket.setPacketType(getPacketType());
			responsePacket.setIdentifier(getIdentifier());
			
			if (getPacketType() != RadiusConstants.ACCESS_REJECT_MESSAGE) {
				responsePacket.addAttributes(getAttributeList());
			} else{
				if(getResponseMessege() != null){
					responsePacket.addAttribute(getRadiusAttribute(RadiusAttributeConstants.REPLY_MESSAGE));
				}
			}
			//responsePacket.reencryptAttributes(null,null, getRequestAuthenticator()	, getClientData().getSharedSecret());
			responsePacket.refreshPacketHeader();
			responsePacket.refreshInfoPacketHeader();
			
			IRadiusAttribute msgAuthenticatorAttribute = responsePacket.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
			if (msgAuthenticatorAttribute != null) {
				msgAuthenticatorAttribute.setValueBytes(RadiusUtility.generateMessageAuthenticator(responsePacket.getBytes(), getRequestAuthenticator(), getClientData().getSharedSecret(getPacketType())));
			}
			
			responsePacket.refreshPacketHeader();
			responsePacket.refreshInfoPacketHeader();
			
			responsePacket.setAuthenticator(RadiusUtility.generateRFC2865ResponseAuthenticator((responsePacket),
			getRequestAuthenticator(), getClientData().getSharedSecret(getPacketType())));
			
			return responsePacket;
		}
		
		@Override
		public RadServiceResponse clone() {
			return super.clone();
		}

	}

	@Override
	public boolean validatePacketAsPerRFC(RMIPPoolRequest request) {
		return false;
	}

	public class RMIPPoolConfigurationSetter implements ConfigurationSetter{
		private ServiceContext serviceContext;
		private static final String REALTIME = "realtime";
		
		public RMIPPoolConfigurationSetter(ServiceContext serviceContext){
			this.serviceContext = serviceContext;
		}
		
		@Override
		public String execute(String... parameters) {
			if(parameters[2].equalsIgnoreCase("log")){
				if(parameters.length >= 4){
					if(((RMIPPoolServiceContext)serviceContext).getIPPoolConfiguration().isServiceLevelLoggerEnabled()){
						if (serviceLogger instanceof EliteRollingFileLogger) {
							EliteRollingFileLogger logger = (EliteRollingFileLogger)serviceLogger;
							if (logger.isValidLogLevel(parameters[3]) == false) {
								return "Invalid log level: " + parameters[3];
							}
							logger.setLogLevel(parameters[3]);
							return "Configuration Changed Successfully";
							
						}
					}else{
						return "Error : IP Pool log are disabled";
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
			if(!parameters[1].equalsIgnoreCase("ippool")){
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
			out.println("Usage : set service ippool [<options>]");
			out.println();
			out.println("where options include:");		
			out.println("     log { all | debug | error | info | off | trace | warn }");
			out.println("     		Set the log level of the IP Pool Service. ");			
			out.close();
			return stringWriter.toString();
		}

		@Override
		public String getHotkeyHelp() {
			return "'ippool':{'log':{'off':{},'error':{},'warn':{},'info':{},'debug':{},'trace':{},'all':{}}}";
		}

		@Override
		public int getConfigurationSetterType() {
			return SERVICE_TYPE;
		}
	}

	@Override
	protected void handleAsyncRadiusRequest(RMIPPoolRequest request,
			RMIPPoolResponse response, ISession session) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ICommand> getCliCommands() {
		List<ICommand> cmdList = new ArrayList<ICommand>();
		cmdList.add(new IPPoolServCommand(ipPoolServiceMIBListener) {
			@Override
			public String getServiceThreadSummary() {
				return getThreadSummary();
			}
		});
		return cmdList;
	}

	@Override
	public boolean isDuplicateDetectionEnabled() {
		return iPPoolServiceConfiguration.isDuplicateRequestDetectionEnabled();
	}
	
	@Override
	public int getDuplicateDetectionQueuePurgeInterval() {
		return iPPoolServiceConfiguration.getDupicateRequestQueuePurgeInterval();
	}
	
	@Override
	public String getServiceName() {
		return "RM IP Pool Service";

	}
	
	@Override
	protected boolean validateMessageAuthenticator(RMIPPoolRequest radServiceRequest, byte[] msgAuthenticatorBytes,String strSecret) {
		//RM services do not validate the message authenticator received in request
		return true;
	}

	@Override
	public List<SocketDetail> getSocketDetails() {
		return iPPoolServiceConfiguration.getSocketDetails();
	}
	
	@Override
	protected final void shutdownLogger() {
		Closeables.closeQuietly(serviceLogger);
	}

	@Override
	public int getDefaultServicePort() {
		return AAAServerConstants.DEFAULT_RM_IP_POOL_SERVICE_PORT;
	}

	@Override
	public boolean isSessionRelease(RMIPPoolRequest request, RMIPPoolResponse response) {
		return false;
	}
}
