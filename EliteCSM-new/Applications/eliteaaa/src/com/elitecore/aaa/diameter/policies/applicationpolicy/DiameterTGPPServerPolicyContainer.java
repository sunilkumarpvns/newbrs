package com.elitecore.aaa.diameter.policies.applicationpolicy;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.Map;

import com.elitecore.aaa.diameter.applications.commons.DiameterSessionLocationHandler;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.DiameterServicePolicyConfiguration;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior;
import com.elitecore.aaa.diameter.policies.tgppserver.CommandCodeFlowData;
import com.elitecore.aaa.diameter.policies.tgppserver.TGPPServerPolicy;
import com.elitecore.aaa.diameter.policies.tgppserver.TGPPServerPolicy.CommandCodeFlow;
import com.elitecore.aaa.diameter.policies.tgppserver.TGPPServerPolicyData;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterApplicationHandler;
import com.elitecore.aaa.diameter.service.application.handlers.ResponseAttributeAdditionHandler;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterApplicationHandlerData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.SubscriberProfileRepositoryAware;
import com.elitecore.aaa.diameter.service.application.handlers.conf.SubscriberProfileRepositoryDetailsAware;
import com.elitecore.aaa.diameter.sessionmanager.DiameterSessionManager;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepository;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepositoryDetails;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.servicepolicy.ServicePolicy;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

public class DiameterTGPPServerPolicyContainer extends DiameterServicePolicyContainer {

	private static final String MODULE = "TGPP-SVR-POLICY-CNTNR";
	private Map<String, DiameterServicePolicyConfiguration> policyConfMap;
	
	public DiameterTGPPServerPolicyContainer(ServiceContext serviceContext,
			Map<String, DiameterServicePolicyConfiguration> policyConfMap,
			DiameterSessionManager diameterSessionManager) {
		super(serviceContext, policyConfMap, diameterSessionManager);
		this.policyConfMap = policyConfMap;
	}

	@Override
	protected ServicePolicy<ApplicationRequest> getPolicyObject(ServiceContext serviceContext, String policyId,
			DiameterSessionManager diameterSessionManager) throws InitializationFailedException {
		return createTGPPServerPolicy((TGPPServerPolicyData) policyConfMap.get(policyId));
	}

	private TGPPServerPolicy createTGPPServerPolicy(TGPPServerPolicyData data) throws InitializationFailedException {
		checkNotNull(data, "TGPP Server policy data is null");
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Initializing TGPP Server policy: \n" + data);
		}

		TGPPServerPolicy policy = new TGPPServerPolicy((DiameterServiceContext) getServiceContext(), data);
			initialize(data, policy);
		return policy;
	}

	private void initialize(TGPPServerPolicyData data, TGPPServerPolicy policy) throws InitializationFailedException {
		filterDisabledHandlers(data);

		addRuleSet(data, policy);

		createDefaultResponseBehavior(data, policy);
		
		createCommandCodeFlows(data, policy);
		
	}

	private void createDefaultResponseBehavior(TGPPServerPolicyData data,
			TGPPServerPolicy policy) {
		policy.setDefaultResponseBehavior(DefaultResponseBehavior.create(data.getDefaultResponseBehaviorType(), 
				data.getDefaultResponseBehaviorParameter()));
	}

	private void createCommandCodeFlows(TGPPServerPolicyData data, TGPPServerPolicy policy) throws InitializationFailedException {
		addUserConfiguredCommandCodeFlows(data, policy);
		
		addDefaultSTRCommandCodeFlowAtLast(data, policy);
		
		policy.init();
		
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Successfully initialized TGPP Service policy: " + policy.getPolicyName());
		}
	}

	private void addDefaultSTRCommandCodeFlowAtLast(TGPPServerPolicyData data,
			TGPPServerPolicy policy) {
		policy.addCommandCodeFlow(policy.create(data.createDefaultSTRFlowData()));
	}

	private void addUserConfiguredCommandCodeFlows(TGPPServerPolicyData data, TGPPServerPolicy policy)
			throws InitializationFailedException {
		
		for (CommandCodeFlowData commandCodeFlowData : data.getCommandCodeFlows()) {
			TGPPServerPolicy.CommandCodeFlow commandCodeFlow = policy.create(commandCodeFlowData);
			
			DiameterSubscriberProfileRepositoryDetails sprDetails = getSPRDetails(commandCodeFlowData);
			DiameterSubscriberProfileRepository spr = createRepository(sprDetails);
			
			addHandlers(commandCodeFlowData, commandCodeFlow, spr, sprDetails, data);
			
			policy.addCommandCodeFlow(commandCodeFlow);
		}
	}
	
	

	private void addHandlers(CommandCodeFlowData commandCodeFlowData, CommandCodeFlow commandCodeFlow,
			DiameterSubscriberProfileRepository spr, DiameterSubscriberProfileRepositoryDetails sprDetails, TGPPServerPolicyData policyData) throws InitializationFailedException {
		
		addSessionLocationHandler(policyData, commandCodeFlow);
		
		for(DiameterApplicationHandlerData handlerData : commandCodeFlowData.getHandlersData()) {
			if(handlerData instanceof SubscriberProfileRepositoryDetailsAware) {
				if(sprDetails == null) {
					throwInitializationException("Profile Lookup Driver details not found");
				}
				((SubscriberProfileRepositoryDetailsAware)handlerData).setSubscriberProfileRepositoryDetails(sprDetails);
			}
			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler = handlerData.createHandler((DiameterServiceContext) getServiceContext());
			if (handler instanceof SubscriberProfileRepositoryAware) {
				if(spr == null) {
					throwInitializationException("Profile Lookup Driver not found");
				}
				((SubscriberProfileRepositoryAware)handler).setSubscriberProfileRepository(spr);
			}
			handler.init();
			commandCodeFlow.addHandler(handler);
		}
		
		addResponseAttributeAdditionHandler(policyData, commandCodeFlow);
		addPostResponseHandler(commandCodeFlowData, commandCodeFlow);
	}

	private void addSessionLocationHandler(TGPPServerPolicyData policyData, CommandCodeFlow commandCodeFlow) throws InitializationFailedException {
		if (policyData.isSessionManagementEnabled()
				&& getDiameterSessionManager() != null) {
			DiameterSessionLocationHandler<ApplicationRequest, ApplicationResponse> locationHandler = 
				new DiameterSessionLocationHandler<ApplicationRequest, ApplicationResponse>(getDiameterSessionManager());
			locationHandler.init();
			commandCodeFlow.addHandler(locationHandler);
		}
	}

	private void addResponseAttributeAdditionHandler(TGPPServerPolicyData policyData, CommandCodeFlow commandCodeFlow) throws InitializationFailedException {
		ResponseAttributeAdditionHandler<ApplicationRequest, ApplicationResponse> responseAttributeAdditionHandler = new ResponseAttributeAdditionHandler<ApplicationRequest, ApplicationResponse>((DiameterServiceContext)getServiceContext(), policyData);
		responseAttributeAdditionHandler.init();
		commandCodeFlow.addHandler(responseAttributeAdditionHandler);
	}

	private void addPostResponseHandler(CommandCodeFlowData commandCodeFlowData, CommandCodeFlow commandCodeFlow) throws InitializationFailedException {
		if (commandCodeFlowData.getPostResponseHandlerData() == null) {
			return;
		}
		
		DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler = commandCodeFlowData.getPostResponseHandlerData().createHandler((DiameterServiceContext) getServiceContext());
		handler.init();
		commandCodeFlow.setPostResponseHandler(handler);
	}

	private void addRuleSet(TGPPServerPolicyData data, TGPPServerPolicy policy) throws InitializationFailedException {
		try {
			LogicalExpression ruleset = Compiler.getDefaultCompiler()
				.parseLogicalExpression(data.getRuleSet());
			policy.setRuleset(ruleset);
		} catch (InvalidExpressionException ex) {
			throwInitializationException("Failed to initialize ruleset: " + data.getRuleSet() 
					+ "Reason: " + ex.getMessage(), ex);
		}
	}

	private void throwInitializationException(String reason, InvalidExpressionException ex) throws InitializationFailedException {
		throw new InitializationFailedException(reason, ex);
	}

	private void filterDisabledHandlers(TGPPServerPolicyData data) {
		LogManager.getLogger().debug(MODULE, "Removing disabled handlers, only enabled handlers will be executed.");

		for (CommandCodeFlowData commandCodeFlowData : data.getCommandCodeFlows()) {
			Collectionz.filter(commandCodeFlowData.getHandlersData(), DiameterApplicationHandlerData.ENABLED);
			Collectionz.filter(commandCodeFlowData.getPostResponseHandlerData().getHandlersData(), DiameterApplicationHandlerData.ENABLED);
		}

		LogManager.getLogger().debug(MODULE, "TGPP Server policy after removal of disabled handlers: \n" + data);
	}

	private DiameterSubscriberProfileRepository createRepository(DiameterSubscriberProfileRepositoryDetails sprDetails) throws InitializationFailedException {
		DiameterSubscriberProfileRepository spr = null;

		if(sprDetails != null) {
			spr = new DiameterSubscriberProfileRepository((DiameterServiceContext) getServiceContext(), sprDetails);
			spr.init();
		}
		return spr;
	}

	//presently it is assumed that there will be a single profile repository
	private DiameterSubscriberProfileRepositoryDetails getSPRDetails(CommandCodeFlowData commandCodeflowData) {
		DiameterSubscriberProfileRepositoryDetails sprDetails = null;
		Iterator<DiameterApplicationHandlerData> handlerDataItr = commandCodeflowData.getHandlersData().iterator();
		while(handlerDataItr.hasNext()) {
			DiameterApplicationHandlerData handlerData = handlerDataItr.next();
			if(handlerData instanceof DiameterSubscriberProfileRepositoryDetails) {				 
				sprDetails = (DiameterSubscriberProfileRepositoryDetails) handlerData;
				break; // there can be only a single subscriber profile repository
			}
		}

		return sprDetails;
	}
	
	private static void throwInitializationException(String reason) throws InitializationFailedException {
		throw new InitializationFailedException(reason);
	}
}
