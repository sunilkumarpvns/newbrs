package com.elitecore.aaa.diameter.policies.applicationpolicy;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import com.elitecore.aaa.core.conf.impl.UpdateIdentityParamsDetail;
import com.elitecore.aaa.core.policies.AAAServicePolicy;
import com.elitecore.aaa.diameter.applications.commons.DiameterSessionLocationHandler;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.NasServicePolicyConfiguration;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.AuthProfileLevelAttributeAdditionHandler;
import com.elitecore.aaa.diameter.service.application.handlers.CUIAdditionHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterApplicationHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterAuthenticationHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterAuthorizationHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterChainHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterConcurrencyHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterConcurrencyInternalHandler;
import com.elitecore.aaa.diameter.service.application.handlers.NasAcctHandler;
import com.elitecore.aaa.diameter.service.application.handlers.ResponseAttributeAdditionHandler;
import com.elitecore.aaa.diameter.sessionmanager.DiameterSessionManager;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepository;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepositoryDetails;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterAVPValueProvider;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.plugins.DiameterPluginManager;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

public class NasAppPolicy extends AAAServicePolicy<ApplicationRequest> 
implements ApplicationPolicy {
	private static final String MODULE = "NAS-APP-PLCY";
	
	private LogicalExpression ruleSet;
	private String policyId;	
	private String policyName;
	private NasServicePolicyConfiguration policyConfiguration;
	private DiameterChainHandler<DiameterApplicationHandler<ApplicationRequest, ApplicationResponse>> authenticationHandlers;
	private DiameterChainHandler<DiameterApplicationHandler<ApplicationRequest, ApplicationResponse>> accountingHandlers;	
	private DiameterSubscriberProfileRepository accountInfoProvider;
	private DiameterPluginManager authPluginManager;
	private DiameterPluginManager acctPluginManager;
	private DefaultResponseBehavior defaultResponseBehavior;


	private final @Nullable DiameterSessionManager diameterSessionManager;

	private DiameterServiceContext serviceContext;
	
	private DiameterSessionLocationHandler<ApplicationRequest, ApplicationResponse> diameterSessionLocationHandler;
	
	public NasAppPolicy(DiameterServiceContext serviceContext, String policyId, 
			@Nullable DiameterSessionManager diameterSessionManager) {
		super(serviceContext);
		this.serviceContext = serviceContext;
		this.policyId = policyId;
		this.policyConfiguration = (NasServicePolicyConfiguration)serviceContext.getDiameterServiceConfigurationDetail().getDiameterServicePolicyConfiguration(policyId);
		this.policyName = policyConfiguration.getName(); 
		this.authenticationHandlers = new DiameterChainHandler<DiameterApplicationHandler<ApplicationRequest, ApplicationResponse>>();
		this.accountingHandlers = new DiameterChainHandler<DiameterApplicationHandler<ApplicationRequest, ApplicationResponse>>();
		this.authPluginManager = new DiameterPluginManager(serviceContext.getServerContext().getDiameterPluginManager().getNameToPluginMap());
		this.acctPluginManager = new DiameterPluginManager(serviceContext.getServerContext().getDiameterPluginManager().getNameToPluginMap());
		this.diameterSessionManager = diameterSessionManager;
	}


	@Override
	public String getPolicyName() {
		return policyConfiguration.getName();
	}

	@Override
	public void init() throws InitializationFailedException {
		
		LogManager.getLogger().info(MODULE, "NAS Policy: " + policyConfiguration.getName() + ", initialization process started");
		initRuleset();
		
		createDefaultResponseBehavior();
		
		createSubscriberProfileRepository();
		
		int requestType = this.policyConfiguration.getRequestType();
		if (requestType == NasServicePolicyConfiguration.AUTHENTICATE_ONLY || requestType == NasServicePolicyConfiguration.AUTHENTICATE_AND_AUTHORIZE){
			DiameterAuthenticationHandler handler = new DiameterAuthenticationHandler((DiameterServiceContext)getServiceContext(), policyConfiguration.getAuthenticationHandlerData());
			handler.setSubscriberProfileRepository(accountInfoProvider);
			
			handler.init();
			authenticationHandlers.addHandler(handler);
		}
		
		if (policyConfiguration.isSessionManagementEnabled() && diameterSessionManager != null) {
			DiameterSessionLocationHandler<ApplicationRequest, ApplicationResponse> sessionLocationHandler = 
					new DiameterSessionLocationHandler<ApplicationRequest, ApplicationResponse>(diameterSessionManager);
			sessionLocationHandler.init();
			authenticationHandlers.addHandler(sessionLocationHandler);
		}

		/*Initializing Authorization Handler, If configured */
		if (requestType == NasServicePolicyConfiguration.AUTHORIZE_ONLY || requestType == NasServicePolicyConfiguration.AUTHENTICATE_AND_AUTHORIZE){
			DiameterAuthorizationHandler<ApplicationRequest, ApplicationResponse> handler = 
					new DiameterAuthorizationHandler<ApplicationRequest, ApplicationResponse>((DiameterServiceContext)getServiceContext(), policyConfiguration.getAuthorizationHandlerData());
			handler.setSubscriberProfileRepository(accountInfoProvider);
			
			handler.init();
			authenticationHandlers.addHandler(handler);
		}
		
		addConcurrencyHandlers();
		
		if (policyConfiguration.isSessionManagementEnabled() && diameterSessionManager != null) {
			this.diameterSessionLocationHandler = new DiameterSessionLocationHandler<ApplicationRequest, ApplicationResponse>(diameterSessionManager);
			this.diameterSessionLocationHandler.init();
			accountingHandlers.addHandler(diameterSessionLocationHandler);
		}
		
		NasAcctHandler cdrHandler = new NasAcctHandler((DiameterServiceContext)getServiceContext(), (NasServicePolicyConfiguration)serviceContext.getDiameterServiceConfigurationDetail().getDiameterServicePolicyConfiguration(policyConfiguration.getId()));
		cdrHandler.init();
		accountingHandlers.addHandler(cdrHandler);
		
		addResponseAttributeAdditionHandlers();
		
		List<PluginEntryDetail> authPrePlugins = policyConfiguration.getNasAuthDetail().getPrePlugins();
		this.authPluginManager.registerInPlugins(authPrePlugins);
		LogManager.getLogger().info(MODULE, "Registered Auth Pre-Plugins: " + authPrePlugins);

		List<PluginEntryDetail> authPostPlugins = policyConfiguration.getNasAuthDetail().getPostPlugins();
		this.authPluginManager.registerOutPlugins(authPostPlugins);
		LogManager.getLogger().info(MODULE, "Registered Auth Post-Plugins: " + authPostPlugins);

		List<PluginEntryDetail> acctPrePlugins = policyConfiguration.getNasAcctDetail().getPrePlugins();
		this.acctPluginManager.registerInPlugins(acctPrePlugins);
		LogManager.getLogger().info(MODULE, "Registered Acct Pre-Plugins: " + acctPrePlugins);

		List<PluginEntryDetail> acctPostPlugins = policyConfiguration.getNasAcctDetail().getPostPlugins();
		this.acctPluginManager.registerOutPlugins(acctPostPlugins);
		LogManager.getLogger().info(MODULE, "Registered Acct Post-Plugins: " + acctPostPlugins);
		
		LogManager.getLogger().info(MODULE, "NAS Policy: " + policyConfiguration.getName() + ", initialized successfully.");
	}

	public void handleSessionTerminationRequest(ApplicationRequest applicationRequest,
			ApplicationResponse applicationResponse, DiameterSession session) {
		if(policyConfiguration.isSessionManagementEnabled()) {
			this.diameterSessionLocationHandler.handleRequest(applicationRequest, applicationResponse, session);
		}
	}
	
	private void createDefaultResponseBehavior() {
		this.defaultResponseBehavior = DefaultResponseBehavior.create(
				policyConfiguration.getDefaultResponseBehaviorType(), policyConfiguration.getDefaultResponseBehaviorParameter());
	}


	private void addConcurrencyHandlers() throws InitializationFailedException {
		
		if (Strings.isNullOrEmpty(policyConfiguration.getDiameterConcurrencyConfigId()) == false) {
			DiameterConcurrencyHandler<ApplicationRequest, ApplicationResponse> diameterConcurrencyHandler = 
					new DiameterConcurrencyHandler<ApplicationRequest, ApplicationResponse> (
							(DiameterServiceContext)getServiceContext(), policyConfiguration.getDiameterConcurrencyConfigId());
			diameterConcurrencyHandler.init();
			authenticationHandlers.addHandler(diameterConcurrencyHandler);
		}
		
		if (Strings.isNullOrEmpty(policyConfiguration.getAdditionalDiameterConcuurencyConfigId())) {
			return;
		}
		
		/* If additional concurrency is present then we need to add a default plugin handler which will replace the concurrent
		 * login policy avp to the other policy so that multi-level concurrency can be achieved
		 */
		
		String additionalConcurrentLoginPolicy = System.getProperty(policyConfiguration.getName() + "." + DiameterConcurrencyInternalHandler.ADDITIONAL_CONCURRENT_LOGIN_POLICY);
		if (Strings.isNullOrBlank(additionalConcurrentLoginPolicy) == false) {
			DiameterConcurrencyInternalHandler<ApplicationRequest, ApplicationResponse> diameterConcurrencyInternalHandler
				= new DiameterConcurrencyInternalHandler<ApplicationRequest, ApplicationResponse>(additionalConcurrentLoginPolicy);
			diameterConcurrencyInternalHandler.init();
			authenticationHandlers.addHandler(diameterConcurrencyInternalHandler);
			
			LogManager.getLogger().info(MODULE, "Additional concurrent login policy: " + additionalConcurrentLoginPolicy 
					+ " found for policy: " + policyConfiguration.getName());
		} else {
			LogManager.getLogger().info(MODULE, "No additional concurrent login policy found in miscellaneous " +
					"configuration. So will use concurrent login policy configured in subscriber profile");
		}
		
		DiameterConcurrencyHandler<ApplicationRequest, ApplicationResponse> diameterConcurrencyHandler = 
				new DiameterConcurrencyHandler<ApplicationRequest, ApplicationResponse> (
						(DiameterServiceContext)getServiceContext(), policyConfiguration.getAdditionalDiameterConcuurencyConfigId());
		diameterConcurrencyHandler.init();
		authenticationHandlers.addHandler(diameterConcurrencyHandler);
	}

	private void addResponseAttributeAdditionHandlers() throws InitializationFailedException {
		CUIAdditionHandler<ApplicationRequest, ApplicationResponse> cuiAdditionHandler 
			= new CUIAdditionHandler<ApplicationRequest, ApplicationResponse>(policyConfiguration.getCuiConfiguration());
		cuiAdditionHandler.init();
		authenticationHandlers.addHandler(cuiAdditionHandler);
		
		AuthProfileLevelAttributeAdditionHandler<ApplicationRequest, ApplicationResponse> profileAttributeAddtionHandler 
			= new AuthProfileLevelAttributeAdditionHandler<ApplicationRequest, ApplicationResponse>();
		profileAttributeAddtionHandler.init();
		authenticationHandlers.addHandler(profileAttributeAddtionHandler);
		
		ResponseAttributeAdditionHandler<ApplicationRequest, ApplicationResponse> responseAttributeAdditionHandler
			= new ResponseAttributeAdditionHandler<ApplicationRequest, ApplicationResponse>(
				(DiameterServiceContext)getServiceContext(), policyId);
		responseAttributeAdditionHandler.init();
		authenticationHandlers.addHandler(responseAttributeAdditionHandler);

		accountingHandlers.addHandler(responseAttributeAdditionHandler);
	}

	private void createSubscriberProfileRepository() throws InitializationFailedException {
		DiameterSubscriberProfileRepositoryDetails repoDetails = new DiameterSubscriberProfileRepositoryDetails();
		repoDetails.setPolicyName(policyConfiguration.getName());
		repoDetails.setAnonymousProfileIdentity(policyConfiguration.getAnonymousProfileIdentity());
		repoDetails.setUserIdentities(policyConfiguration.getUserIdentities());
		
		Map<String, Integer> driverInstanceMap = policyConfiguration.getAuthDriverInstanceIdsMap();
		for(Entry<String, Integer> driverDetail :driverInstanceMap.entrySet()){				
			repoDetails.addPrimaryDriverDetail(driverDetail.getKey(), driverDetail.getValue());
		}
		
		Map<String,String> secondaryDriverInstanceIdsMap = policyConfiguration.getSecondaryAndCacheDriverRelMap();

		if(secondaryDriverInstanceIdsMap != null && !secondaryDriverInstanceIdsMap.isEmpty()){

			Set<String> set = secondaryDriverInstanceIdsMap.keySet();
			Iterator<String> iterator =  set.iterator();

			while(iterator.hasNext()){
				String secondaryDriverId  = iterator.next();
				String cacheDriverId = secondaryDriverInstanceIdsMap.get(secondaryDriverId);
				repoDetails.addSecondaryDriverDetail(secondaryDriverId, cacheDriverId);
			}
		}

		List<String> additionalDriverIds = policyConfiguration.getAdditionalDrivers();
		for (String additionalDriverId : additionalDriverIds){
			repoDetails.addAdditionalDriverDetail(additionalDriverId);
		}
		
		repoDetails.getDriverDetails().setDriverScript(policyConfiguration.getAuthDriverScript());
		
		UpdateIdentityParamsDetail updateIdentity = repoDetails.getUpdateIdentity();
		updateIdentity.setCase(policyConfiguration.getCaseSensitivity());
		updateIdentity.setStripIdentity(policyConfiguration.getStripUserIdentity());
		updateIdentity.setIsTrimIdentity(policyConfiguration.getTrimUserIdentity());
		updateIdentity.setIsTrimPassword(policyConfiguration.isBTrimPassword());
		updateIdentity.setSeparator(policyConfiguration.getRealmSeparator());
		
		accountInfoProvider = new DiameterSubscriberProfileRepository((DiameterServiceContext) getServiceContext(), repoDetails);
		accountInfoProvider.init();
	}

	@Override
	public void handleRequest(ApplicationRequest request, ApplicationResponse response,
			DiameterSession session) {
		if (applyDefaultResponseBehavior(request, response)) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Default Response behavior applied");
			}
			return;
		}
		
		if (request.getDiameterRequest().getCommandCode() == CommandCode.ACCOUNTING.code) {
			handleAccountingRequest(request, response, session);
		} else {
			handleAuthenticationRequest(request, response, session);
		}
	}
	
	public void resumeRequest(ApplicationRequest request, ApplicationResponse response,
			DiameterSession session) {
		if (request.getDiameterRequest().getCommandCode() == CommandCode.ACCOUNTING.code) {
			resumeAccountingRequest(request, response, session);
		} else {
			resumeAuthenticationRequest(request, response, session);
		}
	}

	private void resumeAuthenticationRequest(ApplicationRequest request,
			ApplicationResponse response, DiameterSession session) {
		request.getExecutor().resumeRequestExecution(session);
		
		if (response.isFurtherProcessingRequired() 
				&& response.isMarkedForDropRequest() == false
				&& response.isProcessingCompleted()) {
			acctPluginManager.applyOutPlugins(request.getDiameterRequest(), response.getDiameterAnswer(), session);
		}
	}

	private void resumeAccountingRequest(ApplicationRequest request,
			ApplicationResponse response, DiameterSession session) {
		request.getExecutor().resumeRequestExecution(session);
		
		if (response.isFurtherProcessingRequired() 
				&& response.isMarkedForDropRequest() == false
				&& response.isProcessingCompleted()) {
			authPluginManager.applyOutPlugins(request.getDiameterRequest(), response.getDiameterAnswer(), session);
		}
	}

	private void handleAuthenticationRequest(ApplicationRequest request, ApplicationResponse response,
			DiameterSession session) {
		authPluginManager.applyInPlugins(request.getDiameterRequest(), response.getDiameterAnswer(), session);
		
		RadiusRequestExecutor<ApplicationRequest, ApplicationResponse> executor 
			= new RadiusRequestExecutor<ApplicationRequest, ApplicationResponse>(authenticationHandlers, request, response); 
		request.setExecutor(executor);
		executor.startRequestExecution(session);
		
		if (response.isFurtherProcessingRequired() 
				&& response.isMarkedForDropRequest() == false
				&& response.isProcessingCompleted()) {
			authPluginManager.applyOutPlugins(request.getDiameterRequest(), response.getDiameterAnswer(), session);
		}
	}

	private void handleAccountingRequest(ApplicationRequest request,
			ApplicationResponse response, DiameterSession session) {
		acctPluginManager.applyInPlugins(request.getDiameterRequest(), response.getDiameterAnswer(), session);
		
		RadiusRequestExecutor<ApplicationRequest, ApplicationResponse> executor 
			= new RadiusRequestExecutor<ApplicationRequest, ApplicationResponse>(accountingHandlers, request, response); 
		request.setExecutor(executor);
		executor.startRequestExecution(session);
		
		if (response.isFurtherProcessingRequired() 
				&& response.isMarkedForDropRequest() == false
				&& response.isProcessingCompleted()) {
			acctPluginManager.applyOutPlugins(request.getDiameterRequest(), response.getDiameterAnswer(), session);
		}
	}

	public String getPolicyId(){
		return policyId;
	}
	
	@Override
	public boolean assignRequest(ApplicationRequest serviceRequest) {		
		return ruleSet.evaluate(new DiameterAVPValueProvider(serviceRequest.getDiameterRequest()));
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		this.policyConfiguration = (NasServicePolicyConfiguration)((DiameterServiceContext)getServiceContext()).getDiameterServiceConfigurationDetail().getDiameterServicePolicyConfiguration(getPolicyId());
		initRuleset();
		reInitHandlers();
	}


	private void initRuleset() throws InitializationFailedException {

		try {
			LogManager.getLogger().info(MODULE, "NAS Policy: " + policyName + ", parsing ruleset");
			this.ruleSet = Compiler.getDefaultCompiler().parseLogicalExpression(policyConfiguration.getRuleSet());
			LogManager.getLogger().info(MODULE, "NAS Policy: " + policyName + ", parse ruleset completed");
			
		} catch (InvalidExpressionException e) {
			throw new InitializationFailedException("Failed to parse ruleset: " 
					+ policyConfiguration.getRuleSet() , e);
		}		
	}
	
	private void reInitHandlers() throws InitializationFailedException {
		for (DiameterApplicationHandler<?, ?> handler : authenticationHandlers) {
			handler.reInit();
		}
		
		for (DiameterApplicationHandler<?, ?> handler : accountingHandlers) {
			handler.reInit();
		}
	}


	public boolean isSessionManagementEnabled() {
		return policyConfiguration.isSessionManagementEnabled();
	}


	private boolean applyDefaultResponseBehavior(
			ApplicationRequest applicationRequest,
			ApplicationResponse applicationResponse) {
		
		boolean isResponseBehaviorApplicable = false;
		if (applicationRequest.getDiameterRequest().getCommandCode() == CommandCode.ACCOUNTING.code) {
			isResponseBehaviorApplicable = accountingHandlers.isResponseBehaviorApplicable();
		} else {
			isResponseBehaviorApplicable = authenticationHandlers.isResponseBehaviorApplicable();
		}
		if(isResponseBehaviorApplicable) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Request is eligible for Default Response behavior.");
			}
			defaultResponseBehavior.apply(applicationRequest, applicationResponse);
			return true;
		}
		return false;
		
	}
}
