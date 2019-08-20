 /*
*  EliteAAA Server
 *
 *  Elitecore Technologies Ltd., 904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 3rd August 2010 by Ezhava Baiju Dhanpal
 *  
 */

package com.elitecore.aaa.radius.service.base;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.drivers.DriverManager;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.util.cli.RadiusLogMonitor;
import com.elitecore.aaa.radius.conf.RadClientConfiguration;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.plugins.core.RadPluginManager;
import com.elitecore.aaa.radius.plugins.core.RadPluginRequestHandler;
import com.elitecore.aaa.radius.service.BaseRadServiceSummaryWriter;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.session.RadiusSessionId;
import com.elitecore.aaa.radius.session.imdg.HazelcastRadiusSession;
import com.elitecore.aaa.radius.session.imdg.RadiusSessionDataValueProvider;
import com.elitecore.aaa.radius.sessionx.ConcurrencySessionManager;
import com.elitecore.aaa.radius.sessionx.conf.SessionManagerData;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.EliteUDPService;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.util.logger.monitor.MonitorLogger;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;


/**
 * 
 * @author baiju
 *
 */
public abstract class BaseRadiusService<T extends RadServiceRequest, V extends RadServiceResponse>
extends EliteUDPService<T, V> {
	
	private static final String MODULE = "BS-RAD";	
	private DriverManager driverManager;
	private RadPluginManager pluginManager;
	private RadPluginRequestHandler pluginRequestHandler;
	
	private BaseRadServiceSummaryWriter  baseRadServiceSummaryWriter;
	private final RadiusLogMonitor logMonitor;
	
	public BaseRadiusService(AAAServerContext context, RadPluginManager pluginManager, RadiusLogMonitor logMonitor) {		
		super(context);
		this.logMonitor = checkNotNull(logMonitor, "logMonitor is null");
		this.pluginManager = checkNotNull(pluginManager, "pluginManager is null");
	}
	
	public BaseRadiusService(AAAServerContext context, 
			DriverManager driverManager,
			RadPluginManager pluginManager,
			RadiusLogMonitor logMonitor) {		
		this(context, pluginManager, logMonitor);
		this.pluginManager = pluginManager;
		setDriverManager(driverManager);
	}
	
	protected void setDriverManager(DriverManager driverManager) {
		this.driverManager = checkNotNull(driverManager, "Driver manager is null");
	}
	
	protected void initService() throws ServiceInitializationException{		
		super.initService();
		this.pluginRequestHandler = pluginManager.createRadPluginReqHandler(getRadPrePluginList(), getRadPostPluginList());
	}

	@Override
	public void reInit() {
//		try {
//			pluginManager.reInit();
//			this.pluginRequestHandler = (RadPluginRequestHandler)pluginManager.createPluginReqHandler(getRadPrePluginList(), getRadPostPluginList());
//		} catch (InitializationFailedException e) {
//			LogManager.getLogger().trace(e);
//		}
////		registerTranslationMappingPolicyData();
////		registerRadiusCopyPacketMappingTranslators();
	}
	
	/**
	 * 
	 */
	public final void handleServiceRequest(T request, V response) {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Received RADIUS request packet from " 
					+ request.getClientIp() + ":" + request.getClientPort()
					+ " on socket: " + request.getServerSocket() + request);
		}
		
		RadClientConfiguration radClientConfiguration =  ((AAAServerContext)getServerContext()).getServerConfiguration().getRadClientConfiguration();
		if(radClientConfiguration == null){
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {			
				LogManager.getLogger().error(MODULE, "RADIUS Client Configuration is not initialized.");				
			}
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {			
				LogManager.getLogger().warn(MODULE, "Received request from UNKNOWN client " + request.getClientIp() + ":" + request.getClientPort() + " , dropping request.");
			}			
						
			//TODO what if client configuration is not initialized?
			// need to increment any counters?
			response.markForDropRequest();
			return;
		}		
		RadClientData clientData = radClientConfiguration.getClientData(request.getClientIp());
		if(clientData == null){	
		// is Not Valid client			
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {			
				LogManager.getLogger().warn(MODULE, "Received request from UNKNOWN client " + request.getClientIp() + ":" + request.getClientPort() + " , dropping request.");
				getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.INVALID_CLIENT, MODULE, 
						"Received request from UNKNOWN client " + request.getClientIp() + ":" + request.getClientPort() + " , dropping request.",0,
						request.getClientIp() + ":" + request.getClientPort());
			}	
			incrementServTotalInvalidRequests(request);			
			response.markForDropRequest();
			return;
		}	
		
		// is a Valid client - setting the client data in response
		response.setClientData(clientData);

		
		if(request.getPacketType() == RadiusConstants.NAS_REBOOT_REQUEST){
			handleNasRebootRequest(request, response);
			response.setPacketType(RadiusConstants.NAS_REBOOT_RESPONSE);
			return;
		}
		
		if(!isValidRequest(request, response)){
			return;
		}
		
		if(request.getPacketType() == RadiusConstants.STATUS_SERVER_MESSAGE){
			handleStatusServerMessageRequest(request, response);
			return;
		}
				
		IRadiusAttribute sourceAddrAttr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_SRC_ADDR);
		
		if(sourceAddrAttr != null) {
			String strPacketAddress = request.getClientIp() + ":" + request.getClientPort();
			sourceAddrAttr.setStringValue(strPacketAddress);
			request.addInfoAttribute(sourceAddrAttr);
			LogManager.getLogger().debug(MODULE, "Src-Addr attribute = " + strPacketAddress + " added into request packet");
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Src-Addr attribute not found in elitecore dictionary");
		}

		IRadiusAttribute destinationAddrAttr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_DST_ADDR);
		
		if(destinationAddrAttr != null) {
			String strPacketAddress = request.getServerSocket().toString();
			destinationAddrAttr.setStringValue(strPacketAddress);
			request.addInfoAttribute(destinationAddrAttr);
			LogManager.getLogger().debug(MODULE, "Dst-Addr attribute = " + strPacketAddress + " added into request packet");
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Dst-Addr attribute not found in elitecore dictionary");
		}

		
		String strServerInsId = getServerContext().getServerInstanceId();
		if(strServerInsId!=null && strServerInsId.trim().length()>0) {			
			IRadiusAttribute serverInstanceAttr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_SERVER_INSTANCE_ID);
			if(serverInstanceAttr != null ){
				serverInstanceAttr.setStringValue(strServerInsId);
				request.addInfoAttribute(serverInstanceAttr);
				LogManager.getLogger().debug(MODULE, "Server Instance Id attribute = " + getServerContext().getServerInstanceId() + " added into request packet");
			}else {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Server Instance Id attribute not found in elitecore dictionary");
			}
		}else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Blank or Null value found for Server Instance Id");
		} 
		
		String strServerName = ((AAAServerContext)getServerContext()).getServerConfiguration().getServerName();
		if(strServerName!=null && strServerName.trim().length()>0) {			
			IRadiusAttribute serverInstanceAttr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.EC_SERVER_NAME);
			if(serverInstanceAttr != null ){
				serverInstanceAttr.setStringValue(strServerName);
				request.addInfoAttribute(serverInstanceAttr);
				LogManager.getLogger().debug(MODULE, "EC-Server Name attribute = " +strServerName + " added into request packet");
			}else {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "EC-Server Name attribute not found in elitecore dictionary");
			}
		}else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Blank or Null value found for EC-Server Name");
		} 

		String strDomainName = ((AAAServerContext)getServerContext()).getServerConfiguration().getDomainName();
		if(strDomainName!=null && strDomainName.trim().length()>0) {			
			IRadiusAttribute serverInstanceAttr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.EC_DOMAIN_NAME);
			if(serverInstanceAttr != null ){
				serverInstanceAttr.setStringValue(strDomainName);
				request.addInfoAttribute(serverInstanceAttr);
				LogManager.getLogger().debug(MODULE, "EC-Domain Name attribute = " + strDomainName + " added into request packet");
			}else {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "EC-Domain Name attribute not found in elitecore dictionary");
			}
		}else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Blank or Null value found for EC-Domain Name");
		} 
		
		ISession  session = getOrCreateSession(request);
		
		preRequestProcess(request, response);
		if(!response.isFurtherProcessingRequired()){
			updateOrReleaseSession(request, response, session);
			return;
		}
		
		handlePrePluginRequest(request, response, session);
		
		// changes so that if any plugin sets is further processing required = false, 
		// then return without any processing
		if(!response.isFurtherProcessingRequired()){
			updateOrReleaseSession(request, response, session);
			return;
		}
		
		//checking if the NAI processing (RFC 4282) is configured in the server 
		if(((AAAServerContext)getServerContext()).getServerConfiguration().isNAIEnabled()){
			handleNAIRequestProcess(request, response);
		}
		
		if(!response.isFurtherProcessingRequired()){
			updateOrReleaseSession(request, response, session);
			return;
		}	
		
		handleRadiusRequest(request, response, session);				
	
		if(!request.isFutherExecutionStopped()) {
			handlePostPluginRequest(request,response, session);
		}
		
		updateOrReleaseSession(request, response, session);
	}
	
	private void handleNasRebootRequest(T request, V response) {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Nas Reboot request received");

		//NAS Reboot will lookup based on NAS-IPv6(0:95),NAS-IPv4(0:4),NAS-Identifier(0:32)
		//if any of these is present in the request packet the session will be flushed
		Collection<SessionManagerData> sessionManagerList = ((AAAServerContext)getServerContext()).getServerConfiguration().getSessionManagerConfiguration().getSmConfigurationMap().values();
		for(SessionManagerData sessionManager : sessionManagerList){
			if(SessionManagerData.TYPE_LOCAL.equals(sessionManager.getType())){
				Optional<ConcurrencySessionManager> sessionManagerInst = ((AAAServerContext)getServerContext()).getLocalSessionManager(sessionManager.getInstanceId());
				if(sessionManagerInst.isPresent()) {
					sessionManagerInst.get().handleNasRebootRequest(request, response);
				}
			}
		}
	}
	
	protected abstract void handleStatusServerMessageRequest(T serviceRequest, V serviceResponse);
	

	/**
	 * 
	 * @param request
	 * @param response
	 */
	protected void preRequestProcess(T request, V  response) {
		int vendorType = response.getClientData().getVendorType();
		IRadiusAttribute radiusAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_VENDOR_TYPE);
		if(radiusAttribute != null){
			radiusAttribute.setIntValue(vendorType);
			request.addInfoAttribute(radiusAttribute);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Elitecore VSA Vendor-Type(21067:122) not found in dictionary, will not be added in request.");
			}
		}
	}
	
	@Override
	public final void handleAsyncServiceRequest(T request, V response) {
		ISession session = getOrCreateSession(request);
		
		request.startFurtherExecution();
		
		this.handleAsyncRadiusRequest(request, response, session);

		if(!request.isFutherExecutionStopped() && !response.isMarkedForDropRequest()) {
			handlePostPluginRequest(request,response, session);
		}
		
		updateOrReleaseSession(request, response, session);
	}
	
	protected boolean validateMessageAuthenticator(T radServiceRequest, byte[] msgAuthenticatorBytes, String strSecret){
		return RadiusUtility.validateMessageAuthenticator(radServiceRequest.getRequestBytes(), new byte[16], msgAuthenticatorBytes, strSecret);
	}
	
	protected boolean validateMessageAuthenticatorForStatusServer(T radServiceRequest, byte[] msgAuthenticatorBytes, String strSecret){
		return RadiusUtility.validateMessageAuthenticator(radServiceRequest.getRequestBytes(), radServiceRequest.getAuthenticator(), msgAuthenticatorBytes, strSecret);
	}
	
	/**
	 * This method execution MUST be in below order
	 * 1) Do the final pre processing as per service
	 * 2) Add received proxy attributes into response packet
	 * 3) Add response time attribute into response packet
	 * 
	 * The reason behind fixing the order is to achieve RFC compliance, as Radius RFC mentioned that any
	 * proxy attributes that we have received into request packet, Radius server has to send back in 
	 * same order.
	 * 
	 * The Response time is our VSA to find out the actual response time for processing the request.
	 * So it should add as last attribute to meet, real response time. 
	 */
	@Override
	protected final void finalPreResponseProcess(T request, V response) {
		if(request.getPacketType() == RadiusConstants.STATUS_SERVER_MESSAGE){
			return;
		}

		finalRadiusPreResponseProcess(request, response);
		
		if (response.isProcessingCompleted() == false) {
			return;
		}
		
		ArrayList<IRadiusAttribute> proxyAttributes = (ArrayList<IRadiusAttribute>)request.getRadiusAttributes(RadiusAttributeConstants.PROXY_STATE);
		if(proxyAttributes != null){
			Iterator<IRadiusAttribute> itr = proxyAttributes.iterator();
			while(itr.hasNext()){
				IRadiusAttribute radiusAttribute = itr.next();
				response.addAttribute(radiusAttribute);
			}
		}

		IRadiusAttribute responseTimeAttr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_RESPONSE_TIME);
		if(responseTimeAttr != null) {
			long endToEndResponseTime = System.nanoTime() - request.getRequestReceivedNano();
			responseTimeAttr.setLongValue(endToEndResponseTime/NANO_TO_MILLI);
			response.addInfoAttribute(responseTimeAttr);
			LogManager.getLogger().info(MODULE, "Response-time attribute added to response packet, Total response time : " + responseTimeAttr.getLongValue() +"ms");
		}else{
			LogManager.getLogger().info(MODULE, "Response-Time Attribute is not found in elitecore dictonary  , so this attribute is not included in response packet ");
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 */
	protected void finalRadiusPreResponseProcess(T request, V response) {
		//default implementation
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param session 
	 */
	protected abstract void handleRadiusRequest(T request, V response, ISession session);
	
	protected abstract void handleAsyncRadiusRequest(T request, V response, ISession session);
	
	protected final ESCommunicator getRadiusDriver(String driverInstanceId){
		return this.driverManager.getDriver(driverInstanceId);
	}
	protected abstract void incrementServTotalInvalidRequests(ServiceRequest request);
	protected abstract void incrementServTotalBadAuthenticators(String clientAddress);
	protected abstract void incrementServTotalMalformedRequest(RadServiceRequest request);
	
	protected void handleNAIRequestProcess(T radServiceRequest, V radServiceResponse){
		
	}
	
	protected final void handlePrePluginRequest(T serviceRequest, V serviceResponse, ISession session){
		this.pluginRequestHandler.handlePreRequest(serviceRequest, serviceResponse, session);
	}
	
	protected final void handlePostPluginRequest(T serviceRequest, V serviceResponse, ISession session){
		this.pluginRequestHandler.handlePostRequest(serviceRequest, serviceResponse, session);
	}
	
	protected final RadPluginRequestHandler createRadPluginReqHandler(List<PluginEntryDetail> prePluginList, List<PluginEntryDetail> postPluginList){
		return pluginManager.createRadPluginReqHandler(prePluginList, postPluginList);
	}

	@Override
	public  void generateAlertOnThreadNotAvailable(){
		getServerContext().generateSystemAlert(AlertSeverity.INFO, Alerts.THREADNOTAVAILABLE, getServiceIdentifier(), 
				"Worker Thread not available", 0, "Worker Thread not available");
	}
	
	@Override
	public  void generateAlertOnHighResponseTime(int endToEndResponseTime){
		getServerContext().generateSystemAlert(AlertSeverity.INFO, Alerts.HIGHAAARESPONSETIME, getServiceIdentifier(), "High Response Time : "+endToEndResponseTime+"ms.", endToEndResponseTime, getServerContext().getServerInstanceId());
	}
	
	public abstract List<PluginEntryDetail> getRadPrePluginList();
	
	public abstract List<PluginEntryDetail> getRadPostPluginList();
	
	protected abstract boolean isValidRequest(T radServiceRequest, V radServiceRsponse);
	
	public abstract boolean validatePacketAsPerRFC(T request);
	
    public long getClientRequestExpiryTime(InetAddress address) {
    	RadClientData clientData =  ((AAAServerContext)getServerContext()).getServerConfiguration().getRadClientConfiguration().getClientData(address.getHostAddress());
    	
    	if(clientData != null) {
    		return clientData.getTimeout();
    	}else {
    		return 3000;
    	}
    }
    
    @Override
	public boolean stopService() {
    	super.stopService();
		if (baseRadServiceSummaryWriter != null) {
			baseRadServiceSummaryWriter.close();
		}
		return true; 
	}
    
    protected final void registerServiceSummaryWriter(BaseRadServiceSummaryWriter  baseRadServiceSummaryWriter) {
		if(this.baseRadServiceSummaryWriter==null ){
			if(baseRadServiceSummaryWriter!=null){
				this.baseRadServiceSummaryWriter = baseRadServiceSummaryWriter;
				getServerContext().getTaskScheduler().scheduleIntervalBasedTask(baseRadServiceSummaryWriter);
			}else {
				LogManager.getLogger().warn(MODULE, "Null Service summry writer given for registration in service: " + getServiceIdentifier());
			}	
		}else{
			LogManager.getLogger().warn(MODULE, "Service Summary writer for service " + getServiceIdentifier() + " already registered");
		}
	}

    @Override
    protected final void applyMonitoryLogLevel(T request, V response) {
    	if (logMonitor.evaluate(request, response)) {
    		LogManager.getLogger().addThreadName(Thread.currentThread().getName());
			request.setParameter(MonitorLogger.MONITORED, true);
    	}
    }
    
	@Override
    protected final void removeMonitoryLogLevel(T request, V response) {
		if (request.getParameter(MonitorLogger.MONITORED) != null) {
			LogManager.getLogger().removeThreadName(Thread.currentThread().getName());
		}
    }
	
	protected ISession getOrCreateSession(T request) {
		ISession session = HazelcastRadiusSession.RAD_NO_SESSION;
		
		String sessionIdentity = RadiusSessionId.sessionId(request);
		if (Strings.isNullOrBlank(sessionIdentity)) {
			LogManager.getLogger().info(MODULE, "Radius-Session is not created.");
		} else {
			session = ((AAAServerContext)getServerContext()).getOrCreateRadiusSession(sessionIdentity);
		}
		return session;
	}
	
	private void updateOrReleaseSession(T request, V response, ISession session) {
		if (isSessionRelease(request, response)) {
			session.release();
		} else {
			session.update(new RadiusSessionDataValueProvider(request, response));
		}
	}
	
	 protected abstract boolean isSessionRelease(T request, V response);
	 
}
