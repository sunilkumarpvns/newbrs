package com.elitecore.aaa.radius.policies.servicepolicy;

import java.util.Iterator;

import com.elitecore.aaa.core.policies.AAAServicePolicy;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.DynAuthServicePolicyConfiguration;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthRequest;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthResponse;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthServiceContext;
import com.elitecore.aaa.radius.service.dynauth.handlers.DynAuthChainHandler;
import com.elitecore.aaa.radius.service.dynauth.handlers.DynAuthHandler;
import com.elitecore.aaa.radius.service.dynauth.handlers.PostDynAuthServiceRequestHandler;
import com.elitecore.aaa.radius.service.dynauth.handlers.RadDynAuthServiceHandler;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.aaa.radius.sessionx.ConcurrencySessionManager;
import com.elitecore.aaa.radius.util.exprlib.RequestOnlyValueProvider;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
public class DynAuthServicePolicy extends AAAServicePolicy<RadDynAuthRequest>
implements RadiusServicePolicy<RadDynAuthRequest, RadDynAuthResponse> {
	private static final String MODULE = "DYN-AUTH-SERVICE-POLICY";

	private final String dynAuthPolicyId;
	private DynAuthServicePolicyConfiguration servicePolicyConfiguration;
	private LogicalExpression ruleset;
	private final String policyName;
	private DynAuthChainHandler dynAuthChainHandler = new DynAuthChainHandler();

	public DynAuthServicePolicy(RadDynAuthServiceContext serviceContext, String dynAuthPolicyId) {
		super(serviceContext);
		this.dynAuthPolicyId = dynAuthPolicyId;
		this.servicePolicyConfiguration = serviceContext.getDynAuthConfiguration().getDynAuthServicePolicyConfiguraion(dynAuthPolicyId);
		this.policyName = servicePolicyConfiguration.getPolicyName();
		
		/* Adding Handlers in order, in which they should be applied. */
		dynAuthChainHandler.addHandler(new DynAuthHandler(serviceContext,dynAuthPolicyId));
		dynAuthChainHandler.addHandler(new PostDynAuthServiceRequestHandler(serviceContext,servicePolicyConfiguration.getPolicyId()));

	}

	@Override
	public void init() throws InitializationFailedException {
		LogManager.getLogger().info(MODULE, "Dyna Auth SP: " + policyName + ", initialization process started");
		initRuleset();
		
		//handler initializtion;
		Iterator<RadDynAuthServiceHandler> iterator = dynAuthChainHandler.iterator();
		while(iterator.hasNext()){
			iterator.next().init();
		}
	}


	private void initRuleset() throws InitializationFailedException{
		try {
			ruleset = Compiler.getDefaultCompiler().parseLogicalExpression(
					servicePolicyConfiguration.getRadiusRuleSet());
		} catch (InvalidExpressionException e) {
			throw new InitializationFailedException("Failed to initialize ruleset: "
					+ servicePolicyConfiguration.getRadiusRuleSet(), e);
		}
	}

	@Override
	public boolean assignRequest(RadDynAuthRequest serviceRequest) {
		return ruleset.evaluate(new RequestOnlyValueProvider(serviceRequest));
	}

	@Override
	public String getPolicyName() {
		return servicePolicyConfiguration.getPolicyName();
	}

	public String toString() {
		return this.servicePolicyConfiguration.toString();
	}
	@Override
	public boolean isValidatePacket() {
		return servicePolicyConfiguration.getIsValidatePacket();
	}

	@Override
	public boolean checkForUserIdentityAttr(RadServiceRequest request,
			RadServiceResponse response) {
		return true;
	}

	@Override
	public Optional<ConcurrencySessionManager> getSessionManager() {
		return Optional.absent();
	}

	@Override
	public boolean applyResponseBehavior(RadDynAuthRequest request, RadDynAuthResponse response) {
		return false;
	}

	@Override
	public void reInit() throws InitializationFailedException{
		this.servicePolicyConfiguration = ((RadDynAuthServiceContext)getServiceContext()).getDynAuthConfiguration().getDynAuthServicePolicyConfiguraion(dynAuthPolicyId);
		initRuleset();
		reInitHandlers();
	}
	
	private void reInitHandlers() throws InitializationFailedException{
		Iterator<RadDynAuthServiceHandler> iterator = dynAuthChainHandler.iterator();
		while(iterator.hasNext()){
			iterator.next().reInit();
		}
	}

	@Override
	public RadiusRequestExecutor<RadDynAuthRequest, RadDynAuthResponse> newExecutor(RadDynAuthRequest request, RadDynAuthResponse response) {
		return new RadiusRequestExecutor<RadDynAuthRequest, RadDynAuthResponse>(dynAuthChainHandler, request, response);
	}

	@Override
	public void handlePrePluginRequest(RadDynAuthRequest request,
			RadDynAuthResponse response, ISession session) {
		
	}

	@Override
	public void handlePostPluginRequest(RadDynAuthRequest request,
			RadDynAuthResponse response, ISession session) {
		
	}

	@Override
	public RadiusRequestExecutor<RadDynAuthRequest, RadDynAuthResponse> newAdditionalExecutor(
			RadDynAuthRequest request, RadDynAuthResponse response) {
		//Additional processing is not yet supported in DynAuth
		throw new UnsupportedOperationException("Additional Processing is not supported in DynAuth service");
	}

}
