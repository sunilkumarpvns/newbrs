/*
 *  EliteAAA Server
 *
 *  Elitecore Technologies Ltd., 904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 3rd August 2010 by Ezhava Baiju Dhanpal
 *  
 */


package com.elitecore.aaa.radius.service.auth;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.drivers.DriverManager;
import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.server.axixserver.InMemoryRequestHandler;
import com.elitecore.aaa.core.util.cli.RadiusLogMonitor;
import com.elitecore.aaa.core.util.cli.SetCommand;
import com.elitecore.aaa.mibs.radius.authentication.server.RadiusAuthServiceMIBListener;
import com.elitecore.aaa.radius.conf.RadAuthConfiguration;
import com.elitecore.aaa.radius.conf.RadESConfiguration.RadESTypeConstants;
import com.elitecore.aaa.radius.handler.RadProxyHandler;
import com.elitecore.aaa.radius.plugins.core.RadPluginManager;
import com.elitecore.aaa.radius.plugins.core.RadPluginRequestHandler;
import com.elitecore.aaa.radius.policies.radiuspolicy.RadiusPolicyManager;
import com.elitecore.aaa.radius.policies.servicepolicy.RadiusServicePolicy;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.auth.handlers.BWListHandler;
import com.elitecore.aaa.radius.service.auth.handlers.BWMode;
import com.elitecore.aaa.radius.service.auth.policy.AuthServicePolicy;
import com.elitecore.aaa.radius.service.base.BaseRadiusAuthService;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.aaa.radius.util.HotlineUtility;
import com.elitecore.aaa.radius.util.RadiusProcessHelper;
import com.elitecore.aaa.statistics.StatisticsConstants;
import com.elitecore.aaa.statistics.radius.auth.RadAuthErrorsData;
import com.elitecore.aaa.statistics.radius.auth.RadAuthErrorsLiveUpdater;
import com.elitecore.aaa.statistics.radius.auth.RadAuthErrorsNotifier;
import com.elitecore.aaa.statistics.radius.auth.RadAuthErrorsRRDUpdater;
import com.elitecore.aaa.statistics.radius.auth.RadAuthRejectReasonsData;
import com.elitecore.aaa.statistics.radius.auth.RadAuthRejectReasonsNotifier;
import com.elitecore.aaa.statistics.radius.auth.RadAuthRejectReasonsRRDUpdater;
import com.elitecore.aaa.statistics.radius.auth.RadAuthRejectResonsLiveUpdater;
import com.elitecore.aaa.statistics.radius.auth.RadAuthRespTimeData;
import com.elitecore.aaa.statistics.radius.auth.RadAuthRespTimeLiveUpdater;
import com.elitecore.aaa.statistics.radius.auth.RadAuthRespTimeNotifier;
import com.elitecore.aaa.statistics.radius.auth.RadAuthRespTimeRRDUpdater;
import com.elitecore.aaa.statistics.radius.auth.RadAuthSummaryData;
import com.elitecore.aaa.statistics.radius.auth.RadAuthSummaryLiveUpdater;
import com.elitecore.aaa.statistics.radius.auth.RadAuthSummaryNotifier;
import com.elitecore.aaa.statistics.radius.auth.RadAuthSummaryRRDUpdater;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.serverx.policies.PolicyFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.AsyncRequestExecutor;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.system.comm.ILocalResponseListener;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.MalformedNAIException;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusPacketTypeConstant;

/**
 * 
 * @author baiju
 *
 */

public class RadAuthService extends BaseRadiusAuthService {

	private static final String MODULE = "RAD-AUTH";
	private static final String SERVICE_ID = "RAD-AUTH";	
	private RadAuthServiceContext authServiceContext;
	private BWListHandler bwListHandler = null;
	private RadProxyHandler<RadAuthRequest, RadAuthResponse> radProxyHandler = null;


	private RadAuthRespTimeNotifier radAuthRespTimeNotifier;
	private RadAuthErrorsNotifier radAuthErrorsNotifier;
	private RadAuthSummaryNotifier radAuthSummaryNotifier;
	private RadAuthRejectReasonsNotifier radAuthRejectReasonsNotifier;

	public RadAuthService(AAAServerContext serverContext, 
			DriverManager radiusManager,
			RadPluginManager pluginManager,
			RadiusLogMonitor logMonitor) {
		super(serverContext, radiusManager, pluginManager, logMonitor);
	}

	public String getServiceIdentifier() {
		return SERVICE_ID;
	}


	@Override
	protected final void handleRadiusRequest(RadAuthRequest request, RadAuthResponse response, ISession session) {
		setServicePolicy(request);
		if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Authentication process started");

		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Blacklist verification process started");

		if (this.bwListHandler.isBlockedUser(request,BWMode.Post_Policy)){

			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Request identified as blacklisted, sending ACCESS-REJECT");

			response.setResponseMessage(AuthReplyMessageConstant.ACCOUNT_IS_BLACKLISTED);
			response.setFurtherProcessingRequired(false);			
			response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);			
			return;	
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Blacklist verification process completed, request is valid");
		}

		RadiusServicePolicy<RadAuthRequest, RadAuthResponse> servicePolicy = request.getServicePolicy();


		if (servicePolicy != null){
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Selected Authentication service policy: " + servicePolicy.getPolicyName());

			//ELITEAAA-2168, Configurable Authentication Response behavior
			if (servicePolicy.applyResponseBehavior(request, response)){
				return;
			}

			servicePolicy.handlePrePluginRequest(request, response, session);

			if (servicePolicy.checkForUserIdentityAttr(request, response) == false) {
				RadiusProcessHelper.rejectResponse(response, AuthReplyMessageConstant.PACKET_VALIDATION_FAILED);
				return;
			}

			if (servicePolicy.isValidatePacket() && validatePacketAsPerRFC(request) == false) {
				incrementServTotalMalformedRequest(request);
				RadiusProcessHelper.rejectResponse(response, AuthReplyMessageConstant.PACKET_VALIDATION_FAILED);
				return;
			}

			request.setExecutor(servicePolicy.newExecutor(request, response));
			request.getExecutor().startRequestExecution(session);


			if (!request.isFutherExecutionStopped()) {
				
				if (response.getPacketType() == RadiusConstants.ACCESS_REJECT_MESSAGE) {
					doHotLining(request, response);
				}
				
				servicePolicy.handlePostPluginRequest(request, response, session);
			}
		} else {
			authServiceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.RADIUS_SERVICE_POLICY_NOT_SATISFIED, 
					MODULE, "No Authentication Service Policy Satisfied.", 0, "Authentication Service Policy");
			response.setFurtherProcessingRequired(false);
			response.setResponseMessage(AuthReplyMessageConstant.NO_POLICY_SATISFIED);			
			response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
			return;	
		}
	}

	public void handleAsyncRadiusAuthRequest(RadAuthRequest request, RadAuthResponse response, ISession session) {
		if (response.isMarkedForDropRequest()) {
			response.setFurtherProcessingRequired(false);
			response.setProcessingCompleted(true);
			return;
		} else if (response.getPacketType() != RadiusConstants.ACCESS_ACCEPT_MESSAGE) {

			if (response.getPacketType() == RadiusConstants.ACCESS_REJECT_MESSAGE) {
				doHotLining(request, response);
			}

			response.setFurtherProcessingRequired(false);
			response.setProcessingCompleted(true);
		} else {
			request.getExecutor().resumeRequestExecution(session);

			RadiusServicePolicy<RadAuthRequest, RadAuthResponse> servicePolicy = request.getServicePolicy();

			if (!request.isFutherExecutionStopped() && !response.isMarkedForDropRequest()) {
				
				if (response.getPacketType() == RadiusConstants.ACCESS_REJECT_MESSAGE) {
					doHotLining(request, response);
				}
				
				servicePolicy.handlePostPluginRequest(request, response, session);
			}
		}
	}
	
	@Override
	protected void postResponseProcessing(RadAuthRequest serviceRequest,
			RadAuthResponse serviceResponse) {
		super.postResponseProcessing(serviceRequest, serviceResponse);
		
		if (serviceRequest.getServicePolicy() == null) {
			return;
		}
		
		RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> additional = serviceRequest.getServicePolicy().newAdditionalExecutor(serviceRequest, serviceResponse);
		if (additional == null) {
			return;
		}

		//resetting the flag to allow further execution in case of ACCESS_REJECT
		serviceResponse.setFurtherProcessingRequired(true);
		
		serviceRequest.setExecutor(additional);
		
		// Session will be not available for post read processing , as it is not required now so it does not make any sense to hold session for this process. 
		additional.startRequestExecution(ISession.NO_SESSION);
	}

	@Override
	public final void finalAuthPreResponseProcess(RadAuthRequest request, RadAuthResponse response) {

		if (response.getPacketType() == RadiusConstants.ACCESS_REJECT_MESSAGE) {
			return;
		}
		
		applyReplyItem(request, response);
		addOrRemoveSessionTimeOutAttr(request,response);
		addMSKRevalidationTimeToSessionTimeout(request, response);
		convertVSAToClassAttribute(request, response);

		/*
		 * This is the code for adding the TERMINATION_ACTION attribute when all the
		 * following conditions are satisfied
		 * 1) Packet type is ACCESS_ACCEPT
		 * 2) WiMAX is enabled in Auth Service Policy
		 * 3) Client profile contains WiMAX as supported vendor
		 * 4) State or Session timeout attributes are present in packet 
		 */
		Boolean wimaxEnabled = (Boolean) request.getParameter(AAAServerConstants.WIMAX_ENABLED);
		
		if(wimaxEnabled != null && wimaxEnabled) {
			if(response.getClientData() != null && response.getClientData().isSupportedVendorId(RadiusConstants.WIMAX_VENDOR_ID)){
				if(response.getRadiusAttribute(RadiusAttributeConstants.SESSION_TIMEOUT) != null 
						|| response.getRadiusAttribute(RadiusAttributeConstants.STATE) != null){
					IRadiusAttribute terminationActionAttr = response.getRadiusAttribute(RadiusAttributeConstants.TERMINATION_ACTION);
					if(terminationActionAttr != null){
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
							LogManager.getLogger().warn(MODULE, "Sending TERMINATION-ACTION(0:29) = "+ terminationActionAttr.getStringValue() + " into ACCESS ACCEPT");
						}
					}else{
						terminationActionAttr = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.TERMINATION_ACTION);
						if(terminationActionAttr != null){
							terminationActionAttr.setIntValue(RadiusAttributeValuesConstants.RADIUS_REQUEST);
							response.addAttribute(terminationActionAttr);
						}else{
							LogManager.getLogger().warn(MODULE, "TERMINATION-ACTION(0:29) attribute not found in dictionary. Attribute will not be added in response.");
						}
					}
				}
			}
		}
	}

	/*
	 * Do not change the order of the execution of this method as fail-fast approach is used.
	 */
	private void addMSKRevalidationTimeToSessionTimeout(RadAuthRequest request, RadAuthResponse response) {
		if (request.getParameter(AAAServerConstants.MSK_REVALIDATION_TIME) == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "MSK Revalidation time will not be added, Reason: MSK Revalidation time parameter not found");
			}
			return;
		}
		long mskRevalidationTime = 0;
		try {
			 mskRevalidationTime = (Long)request.getParameter(AAAServerConstants.MSK_REVALIDATION_TIME);
		} catch (NumberFormatException e) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Skipping the MSK Revalidation time addition, "
						+ "Reason: Found invalid MSK Revalidation time: " + (String)request.getParameter(AAAServerConstants.MSK_REVALIDATION_TIME));
			}
			return;
		}
		
		if (mskRevalidationTime <= 0) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Skipping the MSK Revalidation time addition, "
						+ "Reason: It is disabled by setting its value: " + mskRevalidationTime);
			}
			return;
		}
		
		IRadiusAttribute sessionTimeoutAttribute = response.getRadiusAttribute(true, RadiusAttributeConstants.SESSION_TIMEOUT);
		if (sessionTimeoutAttribute != null 
				&& sessionTimeoutAttribute.getStringValue() != null 
				&& sessionTimeoutAttribute.getStringValue().isEmpty() == false) {
			
			sessionTimeoutAttribute.setLongValue(sessionTimeoutAttribute.getIntValue() + mskRevalidationTime);
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Successfully added MSK Revalidation time: " + mskRevalidationTime + " to session timeout attribute");
			}
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Skipping the MSK Revalidation time addition, "
						+ "Reason: Session timeout attribute(0:27) not found");
			}
			return;
		}
	}

	//TODO ALERT! default session timeout will only be considered in case of authorization handler applied
	//TODO ALERT! default session timeout will not be added if nobody has added, when authorization handler is not applied
	private void addOrRemoveSessionTimeOutAttr(RadAuthRequest request,RadAuthResponse response) {
		List<IRadiusAttribute> responseSessionTimeOutAttrList = (ArrayList<IRadiusAttribute>)response.getRadiusAttributes(RadiusAttributeConstants.SESSION_TIMEOUT);

		Integer sessionTimeoutFromAuthorization = (Integer)request.getParameter(AAAServerConstants.SESSION_TIMEOUT);
		if (sessionTimeoutFromAuthorization != null 
				&& sessionTimeoutFromAuthorization <= AAAServerConstants.SESSION_TIMEOUT_DISABLED) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Session-Timeout attribute will not be added in response.");
			}
			response.removeAllAttributes(responseSessionTimeOutAttrList, true);
			return;
		}

		long minimumSessionTime = 0;
		if (Collectionz.isNullOrEmpty(responseSessionTimeOutAttrList) == false) {
			minimumSessionTime = responseSessionTimeOutAttrList.get(0).getLongValue();

			int numOfAttrs = responseSessionTimeOutAttrList.size();
			for (int i = 0; i < numOfAttrs; i++) {
				IRadiusAttribute sessionTimeAttr = responseSessionTimeOutAttrList.get(i);
				if (minimumSessionTime > sessionTimeAttr.getLongValue()) {
					minimumSessionTime = sessionTimeAttr.getLongValue();
				}
				response.removeAttribute(sessionTimeAttr, true);							
			}
		}

		AccountData accountData = request.getAccountData();
		if (accountData != null) {
			if (accountData.getMaxSessionTime() > 0 
					&& accountData.getMaxSessionTime() < minimumSessionTime) {
				minimumSessionTime = accountData.getMaxSessionTime();
			}
			
			if(accountData.isExpiryDateCheckRequired() && accountData.getExpiryDate()!=null && !accountData.isGracePeriodApplicable() 
					&& request.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID + ":" + RadiusAttributeConstants.HOTLINE_REASON, true) == null){
				long accountExpiryTime = (accountData.getExpiryDate().getTime() - System.currentTimeMillis())/1000;
				if (accountExpiryTime < minimumSessionTime) {
					minimumSessionTime = accountExpiryTime;
				}
			}
		}

		if (minimumSessionTime > 0) {
			IRadiusAttribute sessionTimeoutAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
			sessionTimeoutAttribute.setLongValue(minimumSessionTime);
			response.addAttribute(sessionTimeoutAttribute);
		} else {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Session-Timeout attribute will not be added in response.");
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void applyReplyItem(RadAuthRequest request, RadAuthResponse response) {
		RadiusServicePolicy<RadAuthRequest, RadAuthResponse> radiusServicePolicy = request.getServicePolicy();
		if(radiusServicePolicy == null) {
			return;
		}

		try{
			Object replyItem = request.getParameter(AAAServerConstants.CUSTOMER_REPLY_ITEM);
			if(replyItem != null){
				// if reply item is present then so will be other parameters of radius policy
				RadiusPolicyManager.getInstance(RadiusConstants.RADIUS_AUTHORIZATION_POLICY)
				.applyReplyItems(request, response,
						(List<String>)request.getParameter(AAAServerConstants.SATISFIED_POLICIES), 
						(String)replyItem,
						(Boolean)request.getParameter(AAAServerConstants.REJECT_ON_CHECK_ITEM_NOT_FOUND),
						(Boolean)request.getParameter(AAAServerConstants.REJECT_ON_REJECT_ITEM_NOT_FOUND),
						(Boolean)request.getParameter(AAAServerConstants.CONTINUE_ON_POLICY_NOT_FOUND));
			}

			Object satisfiedClientPolicies = request.getParameter(AAAServerConstants.SATISFIED_CLIENT_POLICIES);
			if(satisfiedClientPolicies != null){
				RadiusPolicyManager.getInstance(RadiusConstants.RADIUS_AUTHORIZATION_POLICY).
				applyReplyItems(request, response,
						(List<String>)satisfiedClientPolicies, 
						null,
						(Boolean)request.getParameter(AAAServerConstants.REJECT_ON_CHECK_ITEM_NOT_FOUND),
						(Boolean)request.getParameter(AAAServerConstants.REJECT_ON_REJECT_ITEM_NOT_FOUND),
						(Boolean)request.getParameter(AAAServerConstants.CONTINUE_ON_POLICY_NOT_FOUND));
			}
		}catch (Exception e) {
			LogManager.getLogger().trace(e);
		}
	}


	@Override
	protected ServiceContext getServiceContext() {
		return authServiceContext;
	}

	@Override
	protected void initService() throws ServiceInitializationException{

		authServiceContext = new RadAuthServiceContext() {
			public AAAServerContext getServerContext() {
				return (AAAServerContext) RadAuthService.this.getServerContext();
			}

			public RadAuthConfiguration getAuthConfiguration() {
				return getRadAuthConfiguration();
			}

			@Override
			public String getServiceIdentifier() {
				return  RadAuthService.this.getServiceIdentifier();
			}

			@Override
			public void submitAsyncRequest(RadAuthRequest serviceRequest,
					RadAuthResponse serviceResponse,
					AsyncRequestExecutor<RadAuthRequest, RadAuthResponse> requestExecutor) {
				RadAuthService.this.submitAsyncRequest(serviceRequest, serviceResponse, requestExecutor);
			}

			@Override
			public ESCommunicator getDriver(String driverInstanceId) {
				return getRadiusDriver(driverInstanceId);
			}

			@Override
			public RadPluginRequestHandler createPluginRequestHandler(List<PluginEntryDetail> prePluginList, List<PluginEntryDetail> postPluginList) {				
				return createRadPluginReqHandler(prePluginList, postPluginList);
			}

			@Override
			public void submitLocalRequest(byte[] requestBytes,ILocalResponseListener responseListener) throws UnknownHostException { 
				RadAuthService.this.handleLocalRequest(new LocalRequestHandler(InetAddress.getByName("localhost"),0,requestBytes, responseListener),responseListener);

			}

			@Override
			public boolean isBlockedUser(RadAuthRequest request,
					BWMode mode) {
				return bwListHandler.isBlockedUser(request, mode);
			}


		};

		super.initService();

		this.bwListHandler = new BWListHandler(authServiceContext);
		this.bwListHandler.init();
		getServiceContext().getServerContext().registerCacheable(bwListHandler);

		AuthServiceStatisticsUpdater authServiceStatisticsUpdater = new AuthServiceStatisticsUpdater(1,1);
		getServerContext().getTaskScheduler().scheduleIntervalBasedTask(authServiceStatisticsUpdater);

		AuthStatisticsUpdaterCleaner authStatisticsUpdaterCleaner = new AuthStatisticsUpdaterCleaner(5,5);
		getServerContext().getTaskScheduler().scheduleIntervalBasedTask(authStatisticsUpdaterCleaner);

		radAuthRespTimeNotifier = new RadAuthRespTimeNotifier(new RadAuthRespTimeData());
		radAuthErrorsNotifier = new RadAuthErrorsNotifier(new RadAuthErrorsData());
		radAuthSummaryNotifier = new RadAuthSummaryNotifier(new RadAuthSummaryData());
		radAuthRejectReasonsNotifier = new RadAuthRejectReasonsNotifier(new RadAuthRejectReasonsData());
		if(getRadAuthConfiguration().isRrdResponseTimeEnabled()){			
			RadAuthRespTimeRRDUpdater rrdUpdater = new RadAuthRespTimeRRDUpdater(getServerContext().getServerHome());
			rrdUpdater.init();
			radAuthRespTimeNotifier.addObserver(StatisticsConstants.RRDUPDATER,rrdUpdater);
		}

		if(getRadAuthConfiguration().isRrdErrorsEnabled()){			
			RadAuthErrorsRRDUpdater rrdUpdater = new RadAuthErrorsRRDUpdater(getServerContext().getServerHome());
			rrdUpdater.init();
			radAuthErrorsNotifier.addObserver(StatisticsConstants.RRDUPDATER,rrdUpdater);
		}

		if(getRadAuthConfiguration().isRrdSummaryEnabled()){			
			RadAuthSummaryRRDUpdater rrdUpdater = new RadAuthSummaryRRDUpdater(getServerContext().getServerHome());
			rrdUpdater.init();
			radAuthSummaryNotifier.addObserver(StatisticsConstants.RRDUPDATER,rrdUpdater);
		}
		if(getRadAuthConfiguration().isRrdRejectReasonsEnabled()){			
			RadAuthRejectReasonsRRDUpdater rrdUpdater = new RadAuthRejectReasonsRRDUpdater(getServerContext().getServerHome());
			rrdUpdater.init();
			radAuthRejectReasonsNotifier.addObserver(StatisticsConstants.RRDUPDATER,rrdUpdater);
		}

		RadAuthConfigurationSetter authConfigurationSetter = new RadAuthConfigurationSetter(authServiceContext);
		SetCommand.registerConfigurationSetter(authConfigurationSetter);

		//this handler should be added only when the NAI Request process is enabled
		if(((AAAServerContext)getServerContext()).getServerConfiguration().isNAIEnabled()){
			radProxyHandler = new RadProxyHandler<RadAuthRequest, RadAuthResponse>((RadAuthServiceContext)getServiceContext(),RadESTypeConstants.RAD_AUTH_PROXY.type);
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
		EliteAAAServiceExposerManager.getInstance().registerRadAuthServiceInMemoryRequestHandler(new InMemoryRequestHandler() {

			@Override
			public void handleRequest(byte[] requestBytes, ILocalResponseListener responseListener) 
					throws UnknownHostException {
				RadAuthService.this.handleLocalRequest(new LocalRequestHandler(InetAddress.getLocalHost(), 
							0, requestBytes, responseListener), 
						responseListener);
			}

		});
	}
	
	public RadAuthRespTimeNotifier getRadAuthRespTimeNotifier(){
		return radAuthRespTimeNotifier;
	}
	public RadAuthErrorsNotifier getRadAuthErrorsNotifier(){
		return radAuthErrorsNotifier;
	}
	public RadAuthSummaryNotifier getRadAuthSummaryNotifier(){
		return radAuthSummaryNotifier;
	}
	public RadAuthRejectReasonsNotifier getRadAuthRejectReasonsNotifier(){
		return radAuthRejectReasonsNotifier;
	}

	class AuthServiceStatisticsUpdater extends BaseIntervalBasedTask{
		RadAuthRespTimeData radAuthRespTimeData;
		RadAuthErrorsData radAuthErrorsData;
		RadAuthSummaryData radAuthSummaryData;
		RadAuthRejectReasonsData radAuthRejectReasonsData;

		private long initialDelay;
		private long intervalSeconds;

		public AuthServiceStatisticsUpdater (long initialDelay,long intervalSeconds){
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
			radAuthRespTimeData = new RadAuthRespTimeData();
			radAuthErrorsData = new RadAuthErrorsData();
			radAuthSummaryData = new RadAuthSummaryData();
			radAuthRejectReasonsData = new RadAuthRejectReasonsData();
			//Average Response Time
			radAuthRespTimeData.setTotalAvgResponseTime(getAvgResponseTime());

			if(radAuthRespTimeNotifier!=null){
				radAuthRespTimeNotifier.updateServiceStatisticsData(radAuthRespTimeData);
			}

			//Request Errors
			radAuthErrorsData.setInvalidRequests(RadiusAuthServiceMIBListener.getRadiusAuthServTotalInvalidRequests());
			radAuthErrorsData.setUnknownRequests(RadiusAuthServiceMIBListener.getRadiusAuthServTotalUnknownTypes());
			radAuthErrorsData.setDropped(RadiusAuthServiceMIBListener.getRadiusAuthServTotalPacketsDropped());
			radAuthErrorsData.setDuplicateRequests(RadiusAuthServiceMIBListener.getRadiusAuthServTotalDupAccessRequests());
			radAuthErrorsData.setMalformedRequests(RadiusAuthServiceMIBListener.getRadiusAuthServTotalMalformedAccessRequests());
			radAuthErrorsData.setBadAuthenticators(RadiusAuthServiceMIBListener.getRadiusAuthServTotalBadAuthenticators());
			if(radAuthErrorsNotifier!=null){
				radAuthErrorsNotifier.updateServiceStatisticsData(radAuthErrorsData);
			}

			//Summary
			radAuthSummaryData.setAccessAccept(RadiusAuthServiceMIBListener.getRadiusAuthServTotalAccessAccepts());
			radAuthSummaryData.setAccessChallenge(RadiusAuthServiceMIBListener.getRadiusAuthServTotalAccessChallenges());
			radAuthSummaryData.setAccessReject(RadiusAuthServiceMIBListener.getRadiusAuthServTotalAccessRejects());
			radAuthSummaryData.setAccessRequest(RadiusAuthServiceMIBListener.getRadiusAuthServTotalAccessRequests());
			radAuthSummaryData.setDropped(RadiusAuthServiceMIBListener.getRadiusAuthServTotalPacketsDropped());
			if(radAuthSummaryNotifier!=null){
				radAuthSummaryNotifier.updateServiceStatisticsData(radAuthSummaryData);
			}

			//Reject Reasons
			radAuthRejectReasonsData.setUserNotFound(1);

			if(radAuthRejectReasonsNotifier!=null){
				radAuthRejectReasonsNotifier.updateServiceStatisticsData(radAuthRejectReasonsData);
			}

		}

	}

	class AuthStatisticsUpdaterCleaner extends BaseIntervalBasedTask{


		private long initialDelay;
		private long intervalSeconds;

		public AuthStatisticsUpdaterCleaner (long initialDelay,long intervalSeconds){
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
			if(radAuthRespTimeNotifier!=null){
				radAuthRespTimeNotifier.removeExpiredObservers();
			}
			if(radAuthErrorsNotifier!=null){
				radAuthErrorsNotifier.removeExpiredObservers();
			}
			if(radAuthSummaryNotifier!=null){
				radAuthSummaryNotifier.removeExpiredObservers();
			}
			if(radAuthRejectReasonsNotifier!=null){
				radAuthRejectReasonsNotifier.removeExpiredObservers();
			}

		}

	}
	public List<Long[]> retrieveAuthResponsTimeData(String serverManagerSessionId){

		RadAuthRespTimeNotifier notifier=getRadAuthRespTimeNotifier();
		RadAuthRespTimeLiveUpdater liveUpdater = (RadAuthRespTimeLiveUpdater) notifier.getObserver(serverManagerSessionId);
		if(liveUpdater==null){
			liveUpdater = new RadAuthRespTimeLiveUpdater();
			notifier.addObserver(serverManagerSessionId,liveUpdater);
		}

		return liveUpdater.reset();
	}
	public List<Long[]> retrieveAuthErrorsData(String serverManagerSessionId){
		RadAuthErrorsNotifier notifier=getRadAuthErrorsNotifier();
		RadAuthErrorsLiveUpdater liveUpdater = (RadAuthErrorsLiveUpdater) notifier.getObserver(serverManagerSessionId);
		if(liveUpdater==null){
			liveUpdater = new RadAuthErrorsLiveUpdater();
			notifier.addObserver(serverManagerSessionId,liveUpdater);
		}
		return liveUpdater.reset();
	}
	public List<Long[]> retrieveAuthRejectReasonsData(String serverManagerSessionId){
		RadAuthRejectReasonsNotifier notifier=getRadAuthRejectReasonsNotifier();
		RadAuthRejectResonsLiveUpdater liveUpdater = (RadAuthRejectResonsLiveUpdater) notifier.getObserver(serverManagerSessionId);
		if(liveUpdater==null){
			liveUpdater = new RadAuthRejectResonsLiveUpdater();
			notifier.addObserver(serverManagerSessionId,liveUpdater);
		}
		return liveUpdater.reset();
	}
	public List<Long[]> retrieveAuthSummaryData(String serverManagerSessionId){

		RadAuthSummaryNotifier notifier=getRadAuthSummaryNotifier();
		RadAuthSummaryLiveUpdater liveUpdater = (RadAuthSummaryLiveUpdater) notifier.getObserver(serverManagerSessionId);
		if(liveUpdater==null){
			liveUpdater = new RadAuthSummaryLiveUpdater();
			notifier.addObserver(serverManagerSessionId,liveUpdater);
		}
		return liveUpdater.reset();
	}
	public List<Long[]> retrieveAuthResponsTimeData(Date startDate, Date endDate) throws IOException {
		RadAuthRespTimeNotifier notifier=getRadAuthRespTimeNotifier();
		RadAuthRespTimeRRDUpdater rrdUpdater = (RadAuthRespTimeRRDUpdater) notifier.getObserver(StatisticsConstants.RRDUPDATER);
		if(rrdUpdater==null){
			rrdUpdater = new RadAuthRespTimeRRDUpdater(getServerContext().getServerHome());
			notifier.addObserver(StatisticsConstants.RRDUPDATER,rrdUpdater);
		}
		return rrdUpdater.getData(startDate, endDate);
	}

	public List<Long[]> retrieveAuthErrorsData(Date startDate, Date endDate) throws IOException {
		RadAuthErrorsNotifier notifier=getRadAuthErrorsNotifier();
		RadAuthErrorsRRDUpdater rrdUpdater = (RadAuthErrorsRRDUpdater) notifier.getObserver(StatisticsConstants.RRDUPDATER);
		if(rrdUpdater==null){
			rrdUpdater = new RadAuthErrorsRRDUpdater(getServerContext().getServerHome());
			notifier.addObserver(StatisticsConstants.RRDUPDATER,rrdUpdater);
		}
		return rrdUpdater.getData(startDate, endDate);
	}
	public List<Long[]> retrieveAuthSummaryData(Date startDate, Date endDate) throws IOException{
		RadAuthSummaryNotifier notifier=getRadAuthSummaryNotifier();
		RadAuthSummaryRRDUpdater rrdUpdater = (RadAuthSummaryRRDUpdater) notifier.getObserver(StatisticsConstants.RRDUPDATER);
		if(rrdUpdater==null){
			rrdUpdater = new RadAuthSummaryRRDUpdater(getServerContext().getServerHome());
			notifier.addObserver(StatisticsConstants.RRDUPDATER,rrdUpdater);
		}
		return rrdUpdater.getData(startDate, endDate);
	}
	public List<Long[]> retrieveAuthRejectReasonsData(Date startDate, Date endDate) throws IOException{
		RadAuthRejectReasonsNotifier notifier=getRadAuthRejectReasonsNotifier();
		RadAuthRejectReasonsRRDUpdater rrdUpdater = (RadAuthRejectReasonsRRDUpdater) notifier.getObserver(StatisticsConstants.RRDUPDATER);
		if(rrdUpdater==null){
			rrdUpdater = new RadAuthRejectReasonsRRDUpdater(getServerContext().getServerHome());
			notifier.addObserver(StatisticsConstants.RRDUPDATER,rrdUpdater);
		}
		return rrdUpdater.getData(startDate, endDate);
	}

	@Override
	public List<PluginEntryDetail> getRadPostPluginList() {		
		return getRadAuthConfiguration().getPostPluginList();
	}

	@Override
	public List<PluginEntryDetail> getRadPrePluginList() {	
		return getRadAuthConfiguration().getPrePluginList();
	}

	@Override
	protected void handleNAIRequestProcess(RadAuthRequest radServiceRequest,
			RadAuthResponse radServiceResponse) {

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
										LogManager.getLogger().debug(MODULE, "Username invalid in Auth Request according to RFC 4282. So dropping the request");
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
							LogManager.getLogger().debug(MODULE, "Proxy realm invalid in Auth Request according to RFC 4282. So dropping the request");
							radServiceResponse.markForDropRequest();
							radServiceResponse.setFurtherProcessingRequired(false);
							radServiceResponse.setProcessingCompleted(true);
							return;
						}
					}else{
						if(!RadiusUtility.isValidUserAccordingToABNF(userNameStr)){
							LogManager.getLogger().debug(MODULE, "Username invalid in Auth request according to RFC 4282. So dropping the request");
							radServiceResponse.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
							radServiceResponse.setResponseMessage(AuthReplyMessageConstant.PACKET_VALIDATION_FAILED);
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
					LogManager.getLogger().debug(MODULE, "Username invalid in Auth request according to RFC 4282. So dropping the request");
					radServiceResponse.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
					radServiceResponse.setResponseMessage(AuthReplyMessageConstant.PACKET_VALIDATION_FAILED);
					radServiceResponse.setFurtherProcessingRequired(false);
					radServiceResponse.setProcessingCompleted(true);
					return;
				}
			}

		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "User-Name(0:1) attribute not found in request. Skipping NAI processing.");
			}
		}
	}

	private void stripNAIDecoration(RadAuthRequest radServiceRequest){
		IRadiusAttribute naiDecorationAttr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_NAI_DECORATION);
		if(naiDecorationAttr!=null){
			IRadiusAttribute userNameAttr = radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.USER_NAME);
			String userName = userNameAttr.getStringValue();
			int indexOfOpeningCurlyBrace = userName.indexOf('{');
			int indexOfClosingCurlyBrace = userName.indexOf('}');
			if(indexOfOpeningCurlyBrace == 0 && indexOfClosingCurlyBrace != -1 && indexOfClosingCurlyBrace < userName.length()-1 && indexOfClosingCurlyBrace-indexOfOpeningCurlyBrace > 1){
				String naiDecoration = userName.substring(indexOfOpeningCurlyBrace+1,indexOfClosingCurlyBrace);
				userName = userName.substring(indexOfClosingCurlyBrace + 1);
				naiDecorationAttr.setStringValue(naiDecoration);
				userNameAttr.setStringValue(userName);
				radServiceRequest.addInfoAttribute(naiDecorationAttr);
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "ELITE-NAI-Decoration(21067:204) attribute not found in Dictionary. Skipping stripping of NAI Decoration.");
			}
		}
	}

	@Override
	public String getServiceName() {
		return "Authenication Service";

	}

	/**
	 * Apply hotline policy on request which are eligible for hotlining.
	 * it will be applied in order of, i.e priority 
	 * 1) profile level hot line policy
	 * 2) radius client profile level hot line policy 
	 * 
	 * if profile level hot line policy is found than it will be applied on request,
	 * otherwise policy configured at client profile level is applicable if it is configured.
	 * 
	 * Reject reason will be added in 21067:124 (ELITECORE:HOTLINE_REASON),  
	 * Reject will convert to Accept/Success.
	 * Reply message change the Authentication Success.
	 * 
	 * @param request
	 * @param response
	 */
	private void doHotLining(RadAuthRequest request, RadAuthResponse response) {

		RadiusServicePolicy<RadAuthRequest, RadAuthResponse> radiusServicePolicy = request.getServicePolicy();
		if (radiusServicePolicy instanceof AuthServicePolicy == false) {
			return;
		}
		
		AccountData accountData = request.getAccountData();
		
		String hotlinePolicy = null;
		
		if (accountData != null && accountData.getHotlinePolicy() != null && accountData.getHotlinePolicy().trim().length() > 0) {
			hotlinePolicy = accountData.getHotlinePolicy();
		} else {
			hotlinePolicy = response.getClientData().getHotlinePolicy();
		}

		if (hotlinePolicy == null || hotlinePolicy.trim().length() == 0) {
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(MODULE, "Request eligible for hotlining, but no hotline policy found.");
			}	
			return;
		}

		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Request eligible for hotlining using policy: "+ hotlinePolicy);
		}

		IRadiusAttribute hotlineReasonAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.HOTLINE_REASON);
		if (hotlineReasonAttribute != null) {
			hotlineReasonAttribute.setStringValue(response.getResponseMessege());
			request.addInfoAttribute(hotlineReasonAttribute);
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(MODULE, "HotlineReasonAttribute(21067:124) not found in elitecore dictionary");
			}
		}

		try {
			HotlineUtility.applyHotline(request, response, hotlinePolicy, true, true);
			response.setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
			response.setResponseMessage(AuthReplyMessageConstant.HOTLINE_SUCCESS);
			response.setFurtherProcessingRequired(false);
			
		} catch(ParserException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Hotline policy parsing failed, Reason: "+e.getMessage());
			}
		} catch(PolicyFailedException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE,"Failed to apply hotline policy, Reason: "+e.getMessage());
			}
		}
	}
	
	@Override
	public boolean isSessionRelease(RadAuthRequest request, RadAuthResponse response) {

		return response.getPacketType() == RadiusPacketTypeConstant.ACCESS_REJECT.packetTypeId;
	}
}
