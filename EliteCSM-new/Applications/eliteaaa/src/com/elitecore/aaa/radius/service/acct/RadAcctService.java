/*
 *  EliteAAA Server
 *
 *  Elitecore Technologies Ltd., 904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 3rd August 2010 by Ezhava Baiju Dhanpal
 *  
 */


package com.elitecore.aaa.radius.service.acct;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.drivers.DriverManager;
import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.server.axixserver.InMemoryRequestHandler;
import com.elitecore.aaa.core.util.cli.RadiusLogMonitor;
import com.elitecore.aaa.core.util.cli.SetCommand;
import com.elitecore.aaa.mibs.radius.accounting.server.RadiusAcctServiceMIBListener;
import com.elitecore.aaa.radius.conf.RadAcctConfiguration;
import com.elitecore.aaa.radius.conf.RadESConfiguration.RadESTypeConstants;
import com.elitecore.aaa.radius.handler.RadProxyHandler;
import com.elitecore.aaa.radius.plugins.core.RadPluginManager;
import com.elitecore.aaa.radius.plugins.core.RadPluginRequestHandler;
import com.elitecore.aaa.radius.service.acct.policy.AcctServicePolicy;
import com.elitecore.aaa.radius.service.base.BaseRadiusAcctService;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.aaa.radius.sessionx.ConcurrencySessionManager;
import com.elitecore.aaa.radius.util.RadiusProcessHelper;
import com.elitecore.aaa.statistics.StatisticsConstants;
import com.elitecore.aaa.statistics.radius.acct.RadAcctErrorsData;
import com.elitecore.aaa.statistics.radius.acct.RadAcctErrorsLiveUpdater;
import com.elitecore.aaa.statistics.radius.acct.RadAcctErrorsNotifier;
import com.elitecore.aaa.statistics.radius.acct.RadAcctErrorsRRDUpdater;
import com.elitecore.aaa.statistics.radius.acct.RadAcctRespTimeData;
import com.elitecore.aaa.statistics.radius.acct.RadAcctRespTimeLiveUpdater;
import com.elitecore.aaa.statistics.radius.acct.RadAcctRespTimeNotifier;
import com.elitecore.aaa.statistics.radius.acct.RadAcctRespTimeRRDUpdater;
import com.elitecore.aaa.statistics.radius.acct.RadAcctSummaryData;
import com.elitecore.aaa.statistics.radius.acct.RadAcctSummaryLiveUpdater;
import com.elitecore.aaa.statistics.radius.acct.RadAcctSummaryNotifier;
import com.elitecore.aaa.statistics.radius.acct.RadAcctSummaryRRDUpdater;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.AsyncRequestExecutor;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.system.comm.ILocalResponseListener;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.MalformedNAIException;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * 
 * @author Elitecore Technologies Ltd.
 *
 */
public class RadAcctService extends BaseRadiusAcctService {
	
	private static final String MODULE = "RAD-ACCT";
	private static final String SERVICE_ID = "RAD-ACCT";
	private RadAcctServiceContext acctServiceContext;
	
    private RadAcctRespTimeNotifier radAcctRespTimeNotifier;
    private RadAcctErrorsNotifier radAcctErrorsNotifier;
    private RadAcctSummaryNotifier radAcctSummaryNotifier;
    private RadProxyHandler<RadAcctRequest, RadAcctResponse> radProxyHandler = null;
	private final AAAServerContext serverContext;
	
	public RadAcctService(AAAServerContext context, 
			DriverManager driverManager,
			RadPluginManager pluginManager,
			RadiusLogMonitor logMonitor) {
		super(context, driverManager, pluginManager, logMonitor);
		this.serverContext = context;
	}
	
	public String getServiceIdentifier() {
		return SERVICE_ID;
	}

	
	@Override
	protected ServiceContext getServiceContext() {
		return acctServiceContext;
	}

	@Override
	protected void initService() throws ServiceInitializationException {
		acctServiceContext = new RadAcctServiceContext() {
			public AAAServerContext getServerContext() {
				return (AAAServerContext) RadAcctService.this.getServerContext();
			}

			@Override
			public RadAcctConfiguration getAcctConfiguration() {
				return getRadAcctConfiguration();
			}
			
			@Override
			public String getServiceIdentifier() {
				return  RadAcctService.this.getServiceIdentifier();
			}

			@Override
			public void submitAsyncRequest(RadAcctRequest serviceRequest,
					RadAcctResponse serviceResponse,
					AsyncRequestExecutor<RadAcctRequest, RadAcctResponse> requestExecutor) {
				RadAcctService.this.submitAsyncRequest(serviceRequest, serviceResponse, requestExecutor);
			}
			
			public ESCommunicator getDriver(String driverInstanceId){
				return getRadiusDriver(driverInstanceId);
			}

			@Override
			public RadPluginRequestHandler createPluginRequestHandler(List<PluginEntryDetail> prePluginList, List<PluginEntryDetail> postPluginList) {
				return createRadPluginReqHandler(prePluginList, postPluginList);
			}
		};
		
		super.initService();
		
		
		AcctServiceStatisticsUpdater acctServiceStatisticsUpdater = new AcctServiceStatisticsUpdater(1,1);
		getServerContext().getTaskScheduler().scheduleIntervalBasedTask(acctServiceStatisticsUpdater);

		AcctStatisticsUpdaterCleaner acctStatisticsUpdaterCleaner = new AcctStatisticsUpdaterCleaner(5,5);
		getServerContext().getTaskScheduler().scheduleIntervalBasedTask(acctStatisticsUpdaterCleaner);
		
		radAcctRespTimeNotifier = new RadAcctRespTimeNotifier(new RadAcctRespTimeData());
		radAcctErrorsNotifier = new RadAcctErrorsNotifier(new RadAcctErrorsData());
		radAcctSummaryNotifier = new RadAcctSummaryNotifier(new RadAcctSummaryData());
		if(getRadAcctConfiguration().isRrdResponseTimeEnabled()){			
			RadAcctRespTimeRRDUpdater rrdUpdater = new RadAcctRespTimeRRDUpdater(getServerContext().getServerHome());
			rrdUpdater.init();
			radAcctRespTimeNotifier.addObserver(StatisticsConstants.RRDUPDATER,rrdUpdater);
		}
		
		
		if(getRadAcctConfiguration().isRrdErrorsEnabled()){			
			RadAcctErrorsRRDUpdater rrdUpdater = new RadAcctErrorsRRDUpdater(getServerContext().getServerHome());
			rrdUpdater.init();
			radAcctErrorsNotifier.addObserver(StatisticsConstants.RRDUPDATER,rrdUpdater);
		}
		
		if(getRadAcctConfiguration().isRrdSummaryEnabled()){			
			RadAcctSummaryRRDUpdater rrdUpdater = new RadAcctSummaryRRDUpdater(getServerContext().getServerHome());
			rrdUpdater.init();
			radAcctSummaryNotifier.addObserver(StatisticsConstants.RRDUPDATER,rrdUpdater);
		}
		RadiusAcctServiceMIBListener.init();
		RadAcctConfigurationSetter radAcctConfigurationSetter = new RadAcctConfigurationSetter(acctServiceContext);
		SetCommand.registerConfigurationSetter(radAcctConfigurationSetter);
		
		//this handler should be added only when the NAI request process is enabled 
		if(((AAAServerContext)getServerContext()).getServerConfiguration().isNAIEnabled()){
			radProxyHandler = new RadProxyHandler<RadAcctRequest, RadAcctResponse>((RadAcctServiceContext)getServiceContext(),RadESTypeConstants.RAD_ACCT_PROXY.type);
			radProxyHandler.init();
		}
	}

	@Override
	protected synchronized boolean startService() {
		if (super.startService() == false) {
			return false;
		}
		
		registerInMemoryRequestHandler();
		return true;
	}

	private void registerInMemoryRequestHandler() {
		EliteAAAServiceExposerManager.getInstance().registerRadAcctServiceInMemoryRequestHandler(new InMemoryRequestHandler() {

			@Override
			public void handleRequest(byte[] requestBytes, ILocalResponseListener responseListener) 
					throws UnknownHostException {
				RadAcctService.this.handleLocalRequest(new LocalRequestHandler(InetAddress.getLocalHost(), 
							0, requestBytes, responseListener), 
						responseListener);
			}

		});
	}

	@Override
	protected final void handleRadiusRequest(RadAcctRequest request,RadAcctResponse response, ISession session) {
		setServicePolicy(request);
		AcctServicePolicy servicePolicy = request.getServicePolicy();
		if(servicePolicy != null) {

			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Selected Accounting service policy: " + servicePolicy.getPolicyName());
			}
			
			servicePolicy.applyResponseBehavior(request, response);
			
			if(response.isMarkedForDropRequest() || response.isFurtherProcessingRequired() == false) {
				return;
			}
			
			servicePolicy.handlePrePluginRequest(request, response, session);
			
			if (servicePolicy.checkForUserIdentityAttr(request, response) == false) {
				RadiusProcessHelper.dropResponse(response);
				return;
			}
			
			if(servicePolicy.isValidatePacket() && validatePacketAsPerRFC(request) == false){
				RadiusProcessHelper.dropResponse(response);
				return;
			}
			
			addServicePolicyLevelCUI(request,servicePolicy);
			
			request.setExecutor(servicePolicy.newExecutor(request, response));
			request.getExecutor().startRequestExecution(session);
			
			if(!request.isFutherExecutionStopped()) {
				servicePolicy.handlePostPluginRequest(request, response, session);
			}
		}else {
			acctServiceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.RADIUS_SERVICE_POLICY_NOT_SATISFIED, 
					MODULE, "No Accounting Service Policy Satisfied.", 0, "Accounting Service Policy");
			
			LogManager.getLogger().debug(MODULE, "No Policy Satisfied");
			response.markForDropRequest();
			response.setFurtherProcessingRequired(false);
			
			return;
		}
		
	}
	
	@Override
	protected void postResponseProcessing(RadAcctRequest serviceRequest,
			RadAcctResponse serviceResponse) {
		super.postResponseProcessing(serviceRequest, serviceResponse);
		
		if(serviceRequest.getServicePolicy() == null) {
			return;
		}
		
		RadiusRequestExecutor<RadAcctRequest, RadAcctResponse> additional = serviceRequest.getServicePolicy().newAdditionalExecutor(serviceRequest, serviceResponse);
		if(additional == null) {
			return;
		}
		
		//resetting the flag to allow further execution in case of ACCESS_REJECT
		serviceResponse.setFurtherProcessingRequired(true);
		
		serviceRequest.setExecutor(additional);
		
		// Session will be not available for post read processing , as it is not required now so it does not make any sense to hold session for this process.
		additional.startRequestExecution(ISession.NO_SESSION);
	}
	
	
	private void addServicePolicyLevelCUI(RadAcctRequest request, AcctServicePolicy servicePolicy) {
		
		IRadiusAttribute cuiAttribute = request.getRadiusAttribute(RadiusAttributeConstants.CUI, true);
		if(cuiAttribute==null){
			String strCUIAttrId = servicePolicy.getConfiguration().getCuiConfiguration().getAccountingCuiAttribute();
			if(strCUIAttrId != null && strCUIAttrId.trim().length()>0){
				IRadiusAttribute configuredCUIAttr = request.getRadiusAttribute(strCUIAttrId);
				if(configuredCUIAttr!=null){
					cuiAttribute = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.CUI);
					if(cuiAttribute!=null){
						cuiAttribute.setStringValue(configuredCUIAttr.getStringValue());
						request.addInfoAttribute(cuiAttribute);
						if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
							LogManager.getLogger().info(MODULE, "Accounting request packet after adding CUI Attribute : " + request);
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().warn(MODULE, "CUI attribute not found from dictionary");
					}
				}else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Service policy : "+servicePolicy.getPolicyName()+" , Configured CUI Attribute :"+strCUIAttrId+" not found in request packet");
				}
			}
		}	
		
	}

	public RadAcctRespTimeNotifier getRadAcctRespTimeNotifier(){
		return radAcctRespTimeNotifier;
	}
	public RadAcctErrorsNotifier getRadAcctErrorsNotifier(){
		return radAcctErrorsNotifier;
	}
	public RadAcctSummaryNotifier getRadAcctSummaryNotifier(){
		return radAcctSummaryNotifier;
	}
	
	class AcctServiceStatisticsUpdater extends BaseIntervalBasedTask{
		RadAcctRespTimeData radAcctRespTimeData;
		RadAcctErrorsData radAcctErrorsData;
		RadAcctSummaryData radAcctSummaryData;

		private long initialDelay;
		private long intervalSeconds;
		
		public AcctServiceStatisticsUpdater(long initialDelay,long intervalSeconds){
			this.initialDelay = initialDelay;
			this.intervalSeconds = intervalSeconds;
			radAcctRespTimeData = new RadAcctRespTimeData();
			radAcctErrorsData = new RadAcctErrorsData();
			radAcctSummaryData = new RadAcctSummaryData();
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
			return intervalSeconds;
		}

		@Override
		public void execute(AsyncTaskContext context) {
			radAcctRespTimeData.setTotalAvgResponseTime(getAvgResponseTime());
			
			if(radAcctRespTimeNotifier!=null){
				radAcctRespTimeNotifier.updateServiceStatisticsData(radAcctRespTimeData);
			}
			
			//Request Errors
			radAcctErrorsData.setBadAuthenticators(RadiusAcctServiceMIBListener.getRadiusAccServTotalBadAuthenticators());
			radAcctErrorsData.setInvalidRequests(RadiusAcctServiceMIBListener.getRadiusAccServTotalInvalidRequests());
			radAcctErrorsData.setUnknownRequests(RadiusAcctServiceMIBListener.getRadiusAccServTotalUnknownTypes());
			radAcctErrorsData.setDropped(RadiusAcctServiceMIBListener.getRadiusAccServTotalPacketsDropped());
			radAcctErrorsData.setDuplicateRequests(RadiusAcctServiceMIBListener.getRadiusAccServTotalDupRequests());
			radAcctErrorsData.setMalformedRequests(RadiusAcctServiceMIBListener.getRadiusAccServTotalMalformedRequests());
			
			if(radAcctErrorsNotifier!=null){
				radAcctErrorsNotifier.updateServiceStatisticsData(radAcctErrorsData);
			}
			
			
			
			//Summary
			
			radAcctSummaryData.setDropped(RadiusAcctServiceMIBListener.getRadiusAccServTotalPacketsDropped());
			radAcctSummaryData.setAccountingRequest(RadiusAcctServiceMIBListener.getRadiusAccServTotalRequests());
			if(radAcctSummaryNotifier!=null){
				radAcctSummaryNotifier.updateServiceStatisticsData(radAcctSummaryData);
			}
			
			
		}
		
	}
	class AcctStatisticsUpdaterCleaner extends BaseIntervalBasedTask{

		
		private long initialDelay;
		private long intervalSeconds;
		
		public AcctStatisticsUpdaterCleaner (long initialDelay,long intervalSeconds){
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
			return intervalSeconds;
		}
		
		@Override
		public void execute(AsyncTaskContext context) {
			
			if(radAcctRespTimeNotifier!=null){
				radAcctRespTimeNotifier.removeExpiredObservers();
			}
			if(radAcctErrorsNotifier!=null){
				radAcctErrorsNotifier.removeExpiredObservers();
			}
			if(radAcctSummaryNotifier!=null){
				radAcctSummaryNotifier.removeExpiredObservers();
			}
			
		}
		
	}
	public List<Long[]> retrieveAcctResponsTimeData(String serverManagerSessionId){


		RadAcctRespTimeNotifier notifier=getRadAcctRespTimeNotifier();
		RadAcctRespTimeLiveUpdater liveUpdater = (RadAcctRespTimeLiveUpdater) notifier.getObserver(serverManagerSessionId);
		if(liveUpdater==null){
			liveUpdater = new RadAcctRespTimeLiveUpdater();
			notifier.addObserver(serverManagerSessionId,liveUpdater);
		}

		return liveUpdater.reset();

	}

	public List<Long[]> retrieveAcctErrorsData(String serverManagerSessionId){


		RadAcctErrorsNotifier notifier=getRadAcctErrorsNotifier();
		RadAcctErrorsLiveUpdater liveUpdater = (RadAcctErrorsLiveUpdater) notifier.getObserver(serverManagerSessionId);
		if(liveUpdater==null){
			liveUpdater = new RadAcctErrorsLiveUpdater();
			notifier.addObserver(serverManagerSessionId,liveUpdater);
		}
		return liveUpdater.reset();

	}

	public List<Long[]> retrieveAcctSummaryData(String serverManagerSessionId){

		RadAcctSummaryNotifier notifier=getRadAcctSummaryNotifier();
		RadAcctSummaryLiveUpdater liveUpdater = (RadAcctSummaryLiveUpdater) notifier.getObserver(serverManagerSessionId);
		if(liveUpdater==null){
			liveUpdater = new RadAcctSummaryLiveUpdater();
			notifier.addObserver(serverManagerSessionId,liveUpdater);
		}
		return liveUpdater.reset();

	}
	
	
	public List<Long[]> retrieveAcctResponsTimeData(Date startDate, Date endDate) throws IOException{


		
		RadAcctRespTimeNotifier notifier=getRadAcctRespTimeNotifier();
		RadAcctRespTimeRRDUpdater rrdUpdater = (RadAcctRespTimeRRDUpdater) notifier.getObserver(StatisticsConstants.RRDUPDATER);
		if(rrdUpdater==null){
			rrdUpdater = new RadAcctRespTimeRRDUpdater(getServerContext().getServerHome());
			notifier.addObserver(StatisticsConstants.RRDUPDATER,rrdUpdater);
		}

		return rrdUpdater.getData(startDate, endDate);

	}

	public List<Long[]> retrieveAcctErrorsData(Date startDate, Date endDate)  throws IOException{


		RadAcctErrorsNotifier notifier=getRadAcctErrorsNotifier();
		RadAcctErrorsRRDUpdater rrdUpdater = (RadAcctErrorsRRDUpdater) notifier.getObserver(StatisticsConstants.RRDUPDATER);
		if(rrdUpdater==null){
			rrdUpdater = new RadAcctErrorsRRDUpdater(getServerContext().getServerHome());
			notifier.addObserver(StatisticsConstants.RRDUPDATER,rrdUpdater);
		}
		return rrdUpdater.getData(startDate, endDate);

	}

	public List<Long[]> retrieveAcctSummaryData(Date startDate, Date endDate)  throws IOException{

		RadAcctSummaryNotifier notifier=getRadAcctSummaryNotifier();
		RadAcctSummaryRRDUpdater rrdUpdater = (RadAcctSummaryRRDUpdater) notifier.getObserver(StatisticsConstants.RRDUPDATER);
		if(rrdUpdater==null){
			rrdUpdater = new RadAcctSummaryRRDUpdater(getServerContext().getServerHome());
			notifier.addObserver(StatisticsConstants.RRDUPDATER,rrdUpdater);
		}
		return rrdUpdater.getData(startDate, endDate);

	}

	@Override
	public List<PluginEntryDetail> getRadPostPluginList() {
		return getRadAcctConfiguration().getPostPluginList();
	}

	@Override
	public List<PluginEntryDetail> getRadPrePluginList() {
		return getRadAcctConfiguration().getPrePluginList();
	}

	@Override
	protected void handleAsyncRadiusRequest(RadAcctRequest request, RadAcctResponse response, ISession session){
		if(response.isMarkedForDropRequest()) {
			response.setFurtherProcessingRequired(false);
			response.setProcessingCompleted(true);
			return;
		} else {		
			AcctServicePolicy servicePolicy = request.getServicePolicy();
			
			request.getExecutor().resumeRequestExecution(session);
			
			if(!request.isFutherExecutionStopped() && !response.isMarkedForDropRequest()) {
				servicePolicy.handlePostPluginRequest(request, response, session);
			}
		}
	}

	@Override
	protected void handleNAIRequestProcess(RadAcctRequest radServiceRequest,
			RadAcctResponse radServiceResponse) {

		IRadiusAttribute userName = radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.USER_NAME);
		if(userName != null){
			String userNameStr = userName.getStringValue();
			if(RadiusUtility.isNAIDecorated(userNameStr)){
				if(RadiusUtility.isOurRealm(userNameStr,((AAAServerContext)getServerContext()).getServerConfiguration().getRealmNames())){
					if(RadiusUtility.isValidForProxy(userNameStr)){
						String proxyRealm = RadiusUtility.getProxyRealm(userNameStr);
						if(RadiusUtility.isValidRealmAccordingToABNF(proxyRealm)){
							try{
								String transformedNAI = RadiusUtility.transformNAI(userNameStr,((AAAServerContext)getServerContext()).getServerConfiguration().getRealmNames());
								// checks if the transformed realm contains again our own realm name if so handle the request locally
								if(RadiusUtility.checkRealms(((AAAServerContext)getServerContext()).getServerConfiguration().getRealmNames(), transformedNAI)){
									if(RadiusUtility.isValidUserAccordingToABNF(transformedNAI)){
										userName.setStringValue(transformedNAI);
										stripNAIDecoration(radServiceRequest);
										return;
									}else{
										radServiceResponse.markForDropRequest();
										radServiceResponse.setFurtherProcessingRequired(false);
										radServiceResponse.setProcessingCompleted(true);
										LogManager.getLogger().debug(MODULE, "Username invalid in Acct request according to RFC 4282. So dropping the request");
										return;
									}
								}else{
									// not for local handling so proxying the request
									userName.setStringValue(transformedNAI);
									radProxyHandler.handleRequest(radServiceRequest, radServiceResponse);
								}

							}catch(MalformedNAIException ex){
								LogManager.getLogger().debug(MODULE, "Malformed NAI found in Request packet. So dropping the request.");
								radServiceResponse.markForDropRequest();
								radServiceResponse.setFurtherProcessingRequired(false);
								radServiceResponse.setProcessingCompleted(true);
								return;
							}
						}else{
							LogManager.getLogger().debug(MODULE, "Proxy realm invalid in Acct Request according to RFC 4282. So dropping the request");
							radServiceResponse.markForDropRequest();
							radServiceResponse.setFurtherProcessingRequired(false);
							radServiceResponse.setProcessingCompleted(true);
							return;
						}
					}else{
						if(!RadiusUtility.isValidUserAccordingToABNF(userNameStr)){
							LogManager.getLogger().debug(MODULE, "Username invalid in Acct request according to RFC 4282. So dropping the request");
							radServiceResponse.markForDropRequest();
							radServiceResponse.setFurtherProcessingRequired(false);
							radServiceResponse.setProcessingCompleted(true);
							return;
						}
						stripNAIDecoration(radServiceRequest);
					}
				}else{
					LogManager.getLogger().debug(MODULE, "Malformed NAI received in Request Packet. So dropping the request");
					radServiceResponse.markForDropRequest();
					radServiceResponse.setFurtherProcessingRequired(false);
					radServiceResponse.setProcessingCompleted(true);
					return;
				}
			}else{
				if(!RadiusUtility.isValidUserAccordingToABNF(userNameStr)){
					LogManager.getLogger().debug(MODULE, "Username invalid in Acct request according to RFC 4282. So dropping the request");
					radServiceResponse.markForDropRequest();
					radServiceResponse.setFurtherProcessingRequired(false);
					radServiceResponse.setProcessingCompleted(true);
					return;
				}
			}

		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "User-Name(0:1) attribute not found in request packet. Skipping NAI processing.");
			}
		}
	}
	
	private void stripNAIDecoration(RadAcctRequest radServiceRequest){
		IRadiusAttribute userNameAttr = radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.USER_NAME);
		String userName = userNameAttr.getStringValue();
		int indexOfOpeningCurlyBrace = userName.indexOf('{');
		int indexOfClosingCurlyBrace = userName.indexOf('}');
		if(indexOfOpeningCurlyBrace == 0 && indexOfClosingCurlyBrace != -1 && indexOfClosingCurlyBrace < userName.length()-1 && indexOfClosingCurlyBrace-indexOfOpeningCurlyBrace > 1){
			String naiDecoration = userName.substring(indexOfOpeningCurlyBrace+1,indexOfClosingCurlyBrace);
			userName = userName.substring(indexOfClosingCurlyBrace + 1);
			IRadiusAttribute naiDecorationAttr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_NAI_DECORATION);
			if(naiDecorationAttr != null){
				naiDecorationAttr.setStringValue(naiDecoration);
				userNameAttr.setStringValue(userName);
				radServiceRequest.addInfoAttribute(naiDecorationAttr);
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "ELITE-NAI-Decoration(21067:204) attribute not found in Dictionary. Skipping stripping of NAI Decoration.");
				}
			}
		}
	}

	@Override
	public String getServiceName() {
		return "Accounting Service";
	}
	
	@Override
	protected AAAServerContext getServerContext() {
		return this.serverContext;
	}

	@Override
	public void readConfiguration() throws LoadConfigurationException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected final void finalRadiusPreResponseProcess(RadAcctRequest request, RadAcctResponse response) {
		super.finalRadiusPreResponseProcess(request, response);
		if(response.isFurtherProcessingRequired() == false) {
			return;
		}
		
		doSessionManagement(request, response);
	}

	private void doSessionManagement(RadAcctRequest request, RadAcctResponse response) {
		AcctServicePolicy servicePolicy = request.getServicePolicy();
		if(servicePolicy == null) {
			return;
		}
		
		Optional<ConcurrencySessionManager> sessionManager = servicePolicy.getSessionManager();
		if(sessionManager.isPresent()) {
			sessionManager.get().handleAccountingRequest(request, response, acctServiceContext);
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Session manager not found for policy: " + servicePolicy.getPolicyName() + ", Skipping session management");
			}
		}
	}

	@Override
	public List<SocketDetail> getSocketDetails() {
		return getRadAcctConfiguration().getSocketDetails();
	} 
	
	@Override
	public boolean isSessionRelease(RadAcctRequest request, RadAcctResponse response) {
		return isSessionStatusStop(request) && response.isMarkedForDropRequest() == false
				&& onExternalCommunication(request, response) == false;
	}

	private boolean isSessionStatusStop(RadAcctRequest request) {
		return  request.getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE) != null && 
				request.getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE).getIntValue() == RadiusAttributeValuesConstants.STOP;
	}

	private boolean onExternalCommunication(RadAcctRequest request, RadAcctResponse response) {
		return response.isProcessingCompleted() == false && request.isFutherExecutionStopped()
				&& response.isFurtherProcessingRequired() == false;
	}
}