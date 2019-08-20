package com.elitecore.aaa.radius.service.auth.policy;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.util.Iterator;

import com.elitecore.aaa.core.subscriber.SubscriberProfileRepositoryAware;
import com.elitecore.aaa.core.subscriber.conf.SubscriberProfileRespositoryDetailsAware;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.conf.AuthenticationPolicyData;
import com.elitecore.aaa.radius.service.auth.policy.handlers.HAAuthenticatedIdentityAttributeAdditionHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.AuthServicePolicyHandlerData;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.ResponseAttributeAdditionHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ServicePolicyHandlerData;
import com.elitecore.aaa.radius.sessionx.ConcurrencySessionManager;
import com.elitecore.aaa.radius.subscriber.RadiusSubscriberProfileRepository;
import com.elitecore.aaa.radius.subscriber.conf.RadiusSubscriberProfileRepositoryDetails;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

/**
 * 
 * @author narendra.pathai
 *
 */
//TODO RESTRUCTURE - Add XMLType(propOrder = {}) in all Configuration POJOs
//TODO Implement toString in all handler configurations
//TODO implement reInit in all handlers
public class AuthServicePolicyFactory {
	private static final String MODULE = "AUTH-SP-FACTORY";
	private final RadAuthServiceContext serviceContext;

	public AuthServicePolicyFactory(RadAuthServiceContext serviceContext) {
		this.serviceContext = serviceContext;
	}

	public AuthServicePolicy create(AuthenticationPolicyData policyData) throws InitializationFailedException {
		checkNotNull(policyData, "Authentication policy data is null");
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Initializing Auth Service policy: \n" + policyData);
		}

		AuthServicePolicy servicePolicy = new AuthServicePolicy(serviceContext, policyData);
		initialize(policyData, servicePolicy);
		
		return servicePolicy;
	}

	private void initialize(AuthenticationPolicyData policyData, AuthServicePolicy servicePolicy) throws InitializationFailedException {
		filterDisabledHandlers(policyData);
		
		addRuleSet(policyData, servicePolicy);

		RadiusSubscriberProfileRepositoryDetails sprDetails = getSPRDetails(policyData);
		RadiusSubscriberProfileRepository spr = createRepository(sprDetails);

		setSessionManager(policyData, servicePolicy);

		addHandlers(policyData, servicePolicy, spr, sprDetails);

		setPluginRequestHandler(policyData, servicePolicy);

		servicePolicy.init();
		
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Successfully initialized Auth Service policy: " + policyData.getPolicyName());
		}
	}

	private void filterDisabledHandlers(AuthenticationPolicyData policyData) {
		LogManager.getLogger().debug(MODULE, "Removing disabled handlers, only enabled handlers will be executed.");
		
		Collectionz.filter(policyData.getHandlersData(), ServicePolicyHandlerData.ENABLED);
		Collectionz.filter(policyData.getPostResponseHandler().getHandlersData(), ServicePolicyHandlerData.ENABLED);
		
		LogManager.getLogger().debug(MODULE, "Auth service flow after removal of disabled handlers: \n" + policyData);
	}

	private void setPluginRequestHandler(AuthenticationPolicyData policyData, AuthServicePolicy servicePolicy) {
		servicePolicy.setPluginRequestHandler(serviceContext.createPluginRequestHandler(policyData.getPrePluginDataList(), policyData.getPostPluginDataList()));
	}

	private void setSessionManager(AuthenticationPolicyData authenticationPolicyData, AuthServicePolicy servicePolicy) throws InitializationFailedException {
		Optional<String> sessionManagerId = authenticationPolicyData.getSessionManagerId();
		if(sessionManagerId.isPresent()) {
			Optional<ConcurrencySessionManager> sessionManager = serviceContext.getServerContext().getLocalSessionManager(sessionManagerId.get());
			if(sessionManager.isPresent() == false) {
				throwInitializationException("Session Manager with id: " + authenticationPolicyData.getSessionManagerId() + " not found.");
			}
			servicePolicy.setSessionManager(sessionManager);
		}
	}

	private void addRuleSet(AuthenticationPolicyData policyData, AuthServicePolicy servicePolicy) throws InitializationFailedException {
		try {
			LogicalExpression ruleset = Compiler.getDefaultCompiler()
				.parseLogicalExpression(policyData.getRuleset());
			servicePolicy.setRuleset(ruleset);
		} catch (InvalidExpressionException ex) {
			throwInitializationException("Failed to initialize ruleset: " + policyData.getRuleset() 
					+ "Reason: " + ex.getMessage(), ex);
		}
	}

	private void addHandlers(AuthenticationPolicyData policyData, AuthServicePolicy servicePolicy, RadiusSubscriberProfileRepository spr, RadiusSubscriberProfileRepositoryDetails sprDetails) throws InitializationFailedException {
		servicePolicy.addHandler(new HAAuthenticatedIdentityAttributeAdditionHandler(serviceContext));

		for(AuthServicePolicyHandlerData handlerData : policyData.getHandlersData()) {
			if(handlerData instanceof SubscriberProfileRespositoryDetailsAware) {
				if(sprDetails == null) {
					throwInitializationException("Profile Lookup Driver details not found");
				}
				((SubscriberProfileRespositoryDetailsAware)handlerData).setSubscriberProfileRepositoryDetails(sprDetails);
			}
			RadAuthServiceHandler handler = handlerData.createHandler(serviceContext);
			if(handler instanceof SubscriberProfileRepositoryAware) {
				if(spr == null) {
					throwInitializationException("Profile Lookup Driver not found");
				}
				((SubscriberProfileRepositoryAware)handler).setSubscriberProfileRepository(spr);
			}
			handler.init();
			servicePolicy.addHandler(handler);
		}

		//adding the response attribute addition handler at last
		addResponseAdditionHandler(policyData, servicePolicy);

		addPostResponseHandler(policyData, servicePolicy);
	}

	private void addPostResponseHandler(AuthenticationPolicyData authenticationPolicyData, AuthServicePolicy servicePolicy) throws InitializationFailedException {
		RadAuthServiceHandler postResponseHandler = authenticationPolicyData.getPostResponseHandler().createHandler(serviceContext);
		postResponseHandler.init();
		servicePolicy.setPostResponseHandler(postResponseHandler);
	}

	private void addResponseAdditionHandler(AuthenticationPolicyData authenticationPolicyData, 
			AuthServicePolicy servicePolicy) throws InitializationFailedException {
		ResponseAttributeAdditionHandlerData data = new ResponseAttributeAdditionHandlerData();
		data.setRadiusServicePolicyData(authenticationPolicyData.getRadiusServicePolicyData());
		RadAuthServiceHandler responseAttributeAdditionHandler = data.createHandler(serviceContext);
		responseAttributeAdditionHandler.init();
		servicePolicy.addHandler(responseAttributeAdditionHandler);
	}

	private static void throwInitializationException(String reason) throws InitializationFailedException {
		throw new InitializationFailedException(reason);
	}

	private static void throwInitializationException(String reason, Exception e) throws InitializationFailedException {
		throw new InitializationFailedException(reason, e);
	}

	private RadiusSubscriberProfileRepository createRepository(RadiusSubscriberProfileRepositoryDetails sprDetails) throws InitializationFailedException {
		RadiusSubscriberProfileRepository spr = null;

		if(sprDetails != null) {
			spr = new RadiusSubscriberProfileRepository(serviceContext, sprDetails);
			spr.init();
		}
		return spr;
	}

	//presently it is assumed that there will be a single profile repository
	private RadiusSubscriberProfileRepositoryDetails getSPRDetails(AuthenticationPolicyData authenticationPolicyData) {
		RadiusSubscriberProfileRepositoryDetails sprDetails = null;
		Iterator<AuthServicePolicyHandlerData> handlerDataItr = authenticationPolicyData.getHandlersData().iterator();
		while(handlerDataItr.hasNext()) {
			AuthServicePolicyHandlerData handlerData = handlerDataItr.next();
			if(handlerData instanceof RadiusSubscriberProfileRepositoryDetails) {				 
				sprDetails = (RadiusSubscriberProfileRepositoryDetails) handlerData;
				break; // there can be only a single subscriber profile repository
			}
		}

		return sprDetails;
	}
}