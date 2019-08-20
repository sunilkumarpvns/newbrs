package com.elitecore.aaa.diameter.policies.applicationpolicy;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.elitecore.aaa.core.conf.impl.UpdateIdentityParamsDetail;
import com.elitecore.aaa.core.constant.AuthMethods;
import com.elitecore.aaa.core.policies.AAAServicePolicy;
import com.elitecore.aaa.diameter.applications.commons.DiameterSessionLocationHandler;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.EapServicePolicyConfiguration;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.AuthProfileLevelAttributeAdditionHandler;
import com.elitecore.aaa.diameter.service.application.handlers.CUIAdditionHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterApplicationHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterAuthWimaxHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterAuthenticationHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterAuthorizationHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterChainHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterConcurrencyHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterConcurrencyInternalHandler;
import com.elitecore.aaa.diameter.service.application.handlers.ResponseAttributeAdditionHandler;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterAuthenticationHandlerData;
import com.elitecore.aaa.diameter.sessionmanager.DiameterSessionManager;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepository;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepositoryDetails;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterAVPValueProvider;
import com.elitecore.diameterapi.plugins.DiameterPluginManager;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

public class EapAppPolicy extends AAAServicePolicy<ApplicationRequest> 
implements ApplicationPolicy {

	private static final String MODULE = "EAP-APP-PLCY";
	private LogicalExpression ruleSet;
	private String policyId;	
	private EapServicePolicyConfiguration policyConfiguration;
	private DiameterChainHandler<DiameterApplicationHandler<ApplicationRequest, ApplicationResponse>> eapChainHandler;
	private DiameterSubscriberProfileRepository accountInfoProvider;
	private String policyName;
	private DiameterPluginManager pluginManager;
	private final @Nullable DiameterSessionManager diameterSessionManager;
	private DefaultResponseBehavior defaultResponseBehavior;
	private DiameterServiceContext serviceContext;
	private DiameterSessionLocationHandler<ApplicationRequest, ApplicationResponse> diameterSessionLocationHandler;

	

	public EapAppPolicy(DiameterServiceContext serviceContext,
			String policyId, DiameterSessionManager diameterSessionManager) {
		super(serviceContext);
		this.serviceContext = serviceContext;
		this.policyId = policyId;
		this.policyConfiguration = (EapServicePolicyConfiguration)serviceContext.getDiameterServiceConfigurationDetail().getDiameterServicePolicyConfiguration(policyId);
		this.policyName = policyConfiguration.getName();
		this.eapChainHandler = new DiameterChainHandler<DiameterApplicationHandler<ApplicationRequest, ApplicationResponse>>();
		this.pluginManager = new DiameterPluginManager(serviceContext.getServerContext().getDiameterPluginManager().getNameToPluginMap());
		this.diameterSessionManager = diameterSessionManager;
	}

	@Override
	public boolean assignRequest(ApplicationRequest serviceRequest) {
		return ruleSet.evaluate(new DiameterAVPValueProvider(serviceRequest.getDiameterRequest()));
	}

	@Override
	public String getPolicyName() {
		return this.policyName;
	}

	@Override
	public void init() throws InitializationFailedException {

		LogManager.getLogger().info(MODULE, "EAP Policy: " + policyName + ", initialization process started");
		initRuleset();

		createSubscriberProfileRespository();
		
		createDefaultResponseBehavior();
		
		int requestType = policyConfiguration.getRequestType();
		if (requestType == EapServicePolicyConfiguration.AUTHENTICATE_ONLY || requestType == EapServicePolicyConfiguration.AUTHENTICATE_AND_AUTHORIZE){
			DiameterAuthenticationHandlerData authenticationHandlerData = new DiameterAuthenticationHandlerData();
			authenticationHandlerData.setPolicyName(policyConfiguration.getName());
			authenticationHandlerData.setEapConfigId(policyConfiguration.getEapConfId());
			authenticationHandlerData.setEnabled(true);
			authenticationHandlerData.getAutheMethodHandlerTypes().add(AuthMethods.EAP);
			authenticationHandlerData.setSubscriberProfileRepositoryDetails(accountInfoProvider.getData());
			
			DiameterAuthenticationHandler handler = new DiameterAuthenticationHandler((DiameterServiceContext)getServiceContext(), authenticationHandlerData);
			handler.setSubscriberProfileRepository(accountInfoProvider);
			this.eapChainHandler.addHandler(handler);
		}
		
		if (policyConfiguration.isSessionManagementEnabled() && diameterSessionManager != null) {
			this.diameterSessionLocationHandler = new DiameterSessionLocationHandler<ApplicationRequest, ApplicationResponse>(diameterSessionManager);
			this.diameterSessionLocationHandler.init();
			this.eapChainHandler.addHandler(this.diameterSessionLocationHandler);
		}

		if (requestType == EapServicePolicyConfiguration.AUTHORIZE_ONLY || requestType == EapServicePolicyConfiguration.AUTHENTICATE_AND_AUTHORIZE){
			DiameterAuthorizationHandler<ApplicationRequest, ApplicationResponse> handler = new DiameterAuthorizationHandler<ApplicationRequest, ApplicationResponse>((DiameterServiceContext)getServiceContext(), policyConfiguration.getAuthorizationHandlerData());
			handler.setSubscriberProfileRepository(accountInfoProvider);
			this.eapChainHandler.addHandler(handler);
			if (policyConfiguration.getAuthorizationHandlerData().isWimaxEnabled()) {
				this.eapChainHandler.addHandler(new DiameterAuthWimaxHandler<ApplicationRequest, ApplicationResponse>(getServiceContext(),
						serviceContext.getServerContext().getServerConfiguration().getWimaxConfiguration(),
						serviceContext.getServerContext().getServerConfiguration().getSpiKeysConfiguration(), 
						serviceContext.getServerContext().getWimaxSessionManager(),
						serviceContext.getServerContext().getEapSessionManager(), 
						serviceContext.getServerContext().getKeyManager()));
			}
		}

		addConcurrencyHandlers();
		
		addResponseAttributeAdditionHandlers();
		
		eapChainHandler.init();
		
		pluginManager.registerInPlugins(policyConfiguration.getPrePlugins());
		LogManager.getLogger().info(MODULE, "Registered In plugins: " + policyConfiguration.getPrePlugins());

		pluginManager.registerOutPlugins(policyConfiguration.getPostPlugins());
		LogManager.getLogger().info(MODULE, "Registered Out plugins: " + policyConfiguration.getPostPlugins());
		
		LogManager.getLogger().info(MODULE, "EAP Policy: " + policyName + ", intialize successfully.");
	}

	public void handleSessionTerminationRequest(ApplicationRequest applicationRequest,
			ApplicationResponse applicationResponse, DiameterSession session) {
		if(policyConfiguration.isSessionManagementEnabled()) {
			this.diameterSessionLocationHandler.handleRequest(applicationRequest, applicationResponse, session);
		}
	}
	
	private void addResponseAttributeAdditionHandlers() throws InitializationFailedException {
		eapChainHandler.addHandler(new CUIAdditionHandler<ApplicationRequest, ApplicationResponse>(policyConfiguration.getCuiConfiguration()));
		
		AuthProfileLevelAttributeAdditionHandler<ApplicationRequest, ApplicationResponse> profileAttributeAddtionHandler 
			= new AuthProfileLevelAttributeAdditionHandler<ApplicationRequest, ApplicationResponse>();
		eapChainHandler.addHandler(profileAttributeAddtionHandler);
				
		eapChainHandler.addHandler(new ResponseAttributeAdditionHandler<ApplicationRequest, ApplicationResponse>(
				(DiameterServiceContext)getServiceContext(), policyId));
	}

	private void createSubscriberProfileRespository() throws InitializationFailedException {
		DiameterSubscriberProfileRepositoryDetails repoDetails = new DiameterSubscriberProfileRepositoryDetails();
		repoDetails.setPolicyName(policyConfiguration.getName());
		repoDetails.setUserIdentities(policyConfiguration.getUserIdentities());
		
		Map<String, Integer> driverInstanceMap = policyConfiguration.getAuthDriverInstanceIdsMap();
		for(Entry<String, Integer> driverDetail :driverInstanceMap.entrySet()){				
			repoDetails.addPrimaryDriverDetail(driverDetail.getKey(), driverDetail.getValue());
		}

		List<String> additionalDriverIds = policyConfiguration.getAdditionalDrivers();
		for (String additionalDriverId : additionalDriverIds){
			repoDetails.addAdditionalDriverDetail(additionalDriverId);
		}
		
		repoDetails.getDriverDetails().setDriverScript(policyConfiguration.getDriverScript());
		
		UpdateIdentityParamsDetail updateIdentity = repoDetails.getUpdateIdentity();
		updateIdentity.setCase(policyConfiguration.getCaseSensitivity());
		updateIdentity.setStripIdentity(policyConfiguration.getStripUserIdentity());
		updateIdentity.setIsTrimIdentity(policyConfiguration.getTrimUserIdentity());
		updateIdentity.setIsTrimPassword(policyConfiguration.isBTrimPassword());
		updateIdentity.setSeparator(policyConfiguration.getRealmSeparator());
		
		accountInfoProvider = new DiameterSubscriberProfileRepository((DiameterServiceContext) getServiceContext(), repoDetails);
		accountInfoProvider.init();
	}
	
	private void addConcurrencyHandlers() throws InitializationFailedException {
		
		if (Strings.isNullOrBlank(policyConfiguration.getDiameterConcurrencyConfigId()) == false) {
			DiameterConcurrencyHandler<ApplicationRequest, ApplicationResponse> diameterConcurrencyHandler = 
					new DiameterConcurrencyHandler<ApplicationRequest, ApplicationResponse> (
							(DiameterServiceContext)getServiceContext(), policyConfiguration.getDiameterConcurrencyConfigId());
			eapChainHandler.addHandler(diameterConcurrencyHandler);
		}
		
		if (Strings.isNullOrBlank(policyConfiguration.getAdditionalDiameterConcuurencyConfigId())) {
			return;
		}
		
		/* If additional concurrency is present then we need to add a default plugin handler which will replace the concurrent
		 * login policy avp to the other policy so that multi-level concurrency can be achieved
		 */
		
		String additionalConcurrentLoginPolicy = System.getProperty(policyConfiguration.getName() + "." + DiameterConcurrencyInternalHandler.ADDITIONAL_CONCURRENT_LOGIN_POLICY);
		if (Strings.isNullOrBlank(additionalConcurrentLoginPolicy) == false) {
			eapChainHandler.addHandler(new DiameterConcurrencyInternalHandler<ApplicationRequest, ApplicationResponse>(additionalConcurrentLoginPolicy));
			LogManager.getLogger().info(MODULE, "Additional concurrent login policy: " + additionalConcurrentLoginPolicy 
					+ " found for policy: " + policyConfiguration.getName());
		} else {
			LogManager.getLogger().info(MODULE, "No additional concurrent login policy found in miscellaneous " +
					"configuration. So will use concurrent login policy configured in subscriber profile");
		}
		
		DiameterConcurrencyHandler<ApplicationRequest, ApplicationResponse> diameterConcurrencyHandler = 
				new DiameterConcurrencyHandler<ApplicationRequest, ApplicationResponse> (
						(DiameterServiceContext)getServiceContext(), policyConfiguration.getAdditionalDiameterConcuurencyConfigId());
		eapChainHandler.addHandler(diameterConcurrencyHandler);
	}

	@Override
	public void handleRequest(ApplicationRequest request, ApplicationResponse response,
			DiameterSession session){
		if (applyDefaultResponseBehavior(request, response)) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Default Response behavior applied");
			}
			return;
		}
		
		pluginManager.applyInPlugins(request.getDiameterRequest(), response.getDiameterAnswer(), session);
		
		RadiusRequestExecutor<ApplicationRequest, ApplicationResponse> requestExecutor = 
			new RadiusRequestExecutor<ApplicationRequest, ApplicationResponse>(eapChainHandler, request, response);
		request.setExecutor(requestExecutor);
		
		requestExecutor.startRequestExecution(session);
		
		if (response.isFurtherProcessingRequired() 
				&& response.isMarkedForDropRequest() == false
				&& response.isProcessingCompleted()) {
			pluginManager.applyOutPlugins(request.getDiameterRequest(), response.getDiameterAnswer(), session);
		}
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		this.policyConfiguration = (EapServicePolicyConfiguration)((DiameterServiceContext)getServiceContext()).getDiameterServiceConfigurationDetail().getDiameterServicePolicyConfiguration(policyId);
		initRuleset();
	}

	private void initRuleset() throws InitializationFailedException{

		try {
			LogManager.getLogger().info(MODULE, "EAP Policy: " + policyName + ", parsing ruleset");
			this.ruleSet = Compiler.getDefaultCompiler().parseLogicalExpression(policyConfiguration.getRuleSet());
			LogManager.getLogger().info(MODULE, "EAP Policy: " + policyName + ", parse ruleset completed");
		} catch (InvalidExpressionException e) {
			throw new InitializationFailedException("Failed to parse ruleset: " 
					+ policyConfiguration.getRuleSet() , e);
		}
	}

	public boolean isSessionManagementEnabled() {
		return policyConfiguration.isSessionManagementEnabled();
	}
	
	private void createDefaultResponseBehavior() {
		this.defaultResponseBehavior = DefaultResponseBehavior.create(
				policyConfiguration.getDefaultResponseBehaviorType(), policyConfiguration.getDefaultResponseBehaviorParameter());
	}
	
	private boolean applyDefaultResponseBehavior(
			ApplicationRequest applicationRequest,
			ApplicationResponse applicationResponse) {
		boolean isResponseBehaviorApplicable = eapChainHandler.isResponseBehaviorApplicable();
		if (isResponseBehaviorApplicable) {
			defaultResponseBehavior.apply(applicationRequest, applicationResponse);
			return true;
		}
		return false;
	}
}
