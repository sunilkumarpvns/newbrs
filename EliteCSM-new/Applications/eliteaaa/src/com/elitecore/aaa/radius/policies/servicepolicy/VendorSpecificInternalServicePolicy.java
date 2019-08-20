package com.elitecore.aaa.radius.policies.servicepolicy;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.AuthenticationChainHandler;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthVendorSpecificServiceHandler;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.aaa.radius.sessionx.ConcurrencySessionManager;
import com.elitecore.aaa.radius.util.exprlib.RequestOnlyValueProvider;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

public class VendorSpecificInternalServicePolicy implements RadiusServicePolicy<RadAuthRequest, RadAuthResponse> {
	public static final String MODULE = "WIMAX-INTERNAL-SP";
	public final static String NAME = "InternalWimanx3GPP-POLICY"; 
	private static final String RULESET = String.valueOf(RadiusConstants.WIMAX_VENDOR_ID) + ":"  + String.valueOf(WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue())
		+ "=\"*\"";

	private LogicalExpression ruleSet;
	private AuthenticationChainHandler authenticationChainHandler = new AuthenticationChainHandler();
	private AuthenticationChainHandler additionalChainHandler = new AuthenticationChainHandler();
	 
	public VendorSpecificInternalServicePolicy(RadAuthServiceContext serviceContext){
		authenticationChainHandler.addHandler(new RadAuthVendorSpecificServiceHandler(serviceContext, true, true));
	}
	
	private void createRuleSet() throws InvalidExpressionException {
		ruleSet = Compiler.getDefaultCompiler().parseLogicalExpression(RULESET);
	}

	@Override
	public void handlePostPluginRequest(RadAuthRequest request,
			RadAuthResponse response, ISession session) {
	}

	@Override
	public void handlePrePluginRequest(RadAuthRequest request,
			RadAuthResponse response, ISession session) {
	}

	@Override
	public boolean assignRequest(RadAuthRequest serviceRequest) {
		return ruleSet.evaluate(new RequestOnlyValueProvider((RadServiceRequest) serviceRequest));
	}

	@Override
	public String getPolicyName() {
		return NAME;
	}

	@Override
	public void init() throws InitializationFailedException {
		try {
			LogManager.getLogger().info(MODULE, "Auth SP: WIMAX, parsing ruleset");
			createRuleSet();
			LogManager.getLogger().info(MODULE, "Auth SP: WIMAX, parse ruleset completed");
			
		} catch (InvalidExpressionException e) {
			// This situation should not occur ideally as the ruleset is static
			LogManager.getLogger().error(MODULE, "Auth SP: WIMAX, problem parsing ruleset - reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
			throw new InitializationFailedException(e);
		}

	}
	@Override
	public String toString(){
		return "    Rule set = " + RULESET;
	}
	@Override
	public boolean isValidatePacket() {
		return false;
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
	public boolean applyResponseBehavior(RadAuthRequest request, RadAuthResponse response) {
		return false;
	}
	@Override
	public RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> newExecutor(RadAuthRequest request, RadAuthResponse response) {
		return new RadiusRequestExecutor<RadAuthRequest, RadAuthResponse>(authenticationChainHandler, request, response);
	}

	@Override
	public RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> newAdditionalExecutor(RadAuthRequest request, RadAuthResponse response) {
		return new RadiusRequestExecutor<RadAuthRequest, RadAuthResponse>(additionalChainHandler, request, response);
	}
}
