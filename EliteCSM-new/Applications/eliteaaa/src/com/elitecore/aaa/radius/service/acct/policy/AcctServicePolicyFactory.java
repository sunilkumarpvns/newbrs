package com.elitecore.aaa.radius.service.acct.policy;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.acct.policy.conf.AccountingPolicyData;
import com.elitecore.aaa.radius.service.acct.policy.handlers.conf.AcctServicePolicyHandlerData;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.ResponseAttributeAdditionHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ServicePolicyHandlerData;
import com.elitecore.aaa.radius.sessionx.ConcurrencySessionManager;
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
//TODO add post response handler
//TODO cui attributes in accounting is remaining
public class AcctServicePolicyFactory {
	private static final String MODULE = "ACCT-SP-FACTORY";
	private final RadAcctServiceContext serviceContext;

	public AcctServicePolicyFactory(RadAcctServiceContext serviceContext) {
		this.serviceContext = serviceContext;
	}

	public AcctServicePolicy create(AccountingPolicyData data) throws InitializationFailedException {
		checkNotNull(data, "Accounting policy data is null");
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Initializing Acct Service policy: \n" + data);
		}

		AcctServicePolicy servicePolicy = new AcctServicePolicy(serviceContext, data);
		initialize(data, servicePolicy);

		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Successfully initialized Acct Service policy: " + data.getPolicyName());
		}
		
		return servicePolicy;
	}

	private void initialize(AccountingPolicyData data, AcctServicePolicy servicePolicy) throws InitializationFailedException {
		filterDisabledHandlers(data);
		addRuleset(data, servicePolicy);
		setSessionManager(data, servicePolicy);
		addHandlers(data, servicePolicy);
		setPluginRequestHandler(data, servicePolicy);
		servicePolicy.init();
	}

	private void setPluginRequestHandler(AccountingPolicyData data, AcctServicePolicy servicePolicy) {
		servicePolicy.setPluginRequestHandler(serviceContext.createPluginRequestHandler(data.getPrePluginDataList(), data.getPostPluginDataList()));
	}

	private void setSessionManager(AccountingPolicyData data, AcctServicePolicy servicePolicy) throws InitializationFailedException {
		Optional<String> sessionManagerId = data.getSessionManagerId();
		if(sessionManagerId.isPresent()) {
			Optional<ConcurrencySessionManager> sessionManager = serviceContext.getServerContext().getLocalSessionManager(sessionManagerId.get());
			if(sessionManager.isPresent() == false) {
				throwInitializationException("Session Manager with id: " + data.getSessionManagerId() + " not found.");
			}
			servicePolicy.setSessionManager(sessionManager);
		}
	}

	private void addHandlers(AccountingPolicyData policyData, AcctServicePolicy servicePolicy) throws InitializationFailedException {
		
		for(AcctServicePolicyHandlerData handlerData : policyData.getHandlersData()) {
			RadAcctServiceHandler handler = handlerData.createHandler(serviceContext);
			handler.init();
			servicePolicy.addHandler(handler);
		}

		//adding the response attribute addition handler at last
		addResponseAttributeAdditionHandler(policyData, servicePolicy);
		
		addPostResponseHandler(policyData, servicePolicy);
	}
	
	private void filterDisabledHandlers(AccountingPolicyData policyData) {
		LogManager.getLogger().debug(MODULE, "Removing disabled handlers, only enabled handlers will be executed.");
		
		Collectionz.filter(policyData.getHandlersData(), ServicePolicyHandlerData.ENABLED);
		Collectionz.filter(policyData.getPostResponseHandler().getHandlersData(), ServicePolicyHandlerData.ENABLED);
		
		LogManager.getLogger().debug(MODULE, "Acct service flow after removal of disabled handlers: \n" + policyData);
	}

	private void addPostResponseHandler(AccountingPolicyData accountingPolicyData, AcctServicePolicy servicePolicy) throws InitializationFailedException {
		RadAcctServiceHandler postResponseHandler = accountingPolicyData.getPostResponseHandler().createHandler(serviceContext);
		postResponseHandler.init();
		servicePolicy.setPostResponseHandler(postResponseHandler);
	}

	private void addResponseAttributeAdditionHandler(AccountingPolicyData policyData, AcctServicePolicy servicePolicy)
	throws InitializationFailedException {
		ResponseAttributeAdditionHandlerData data = new ResponseAttributeAdditionHandlerData();
		data.setRadiusServicePolicyData(policyData.getRadiusServicePolicyData());
		RadAcctServiceHandler responseAttributeAdditionHandler = data.createHandler(serviceContext);
		responseAttributeAdditionHandler.init();
		servicePolicy.addHandler(responseAttributeAdditionHandler);
	}

	private void addRuleset(AccountingPolicyData policyData, AcctServicePolicy servicePolicy) throws InitializationFailedException {
		try {
			LogicalExpression ruleset = Compiler.getDefaultCompiler()
				.parseLogicalExpression(policyData.getRuleset());
			servicePolicy.setRuleset(ruleset);
		} catch (InvalidExpressionException ex) {
			throwInitializationException("Failed to initialize ruleset: " + policyData.getRuleset() 
					+ "Reason: " + ex.getMessage(), ex);
		}
	}

	private static void throwInitializationException(String reason) throws InitializationFailedException {
		throw new InitializationFailedException(reason);
	}

	private static void throwInitializationException(String reason, Exception e) throws InitializationFailedException {
		throw new InitializationFailedException(reason, e);
	}
}
