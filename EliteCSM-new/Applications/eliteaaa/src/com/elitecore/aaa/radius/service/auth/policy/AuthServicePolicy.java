package com.elitecore.aaa.radius.service.auth.policy;

import java.util.Collections;
import java.util.List;

import com.elitecore.aaa.core.policies.AAAServicePolicy;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.plugins.core.RadPluginRequestHandler;
import com.elitecore.aaa.radius.policies.servicepolicy.RadiusServicePolicy;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.AuthenticationChainHandler;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.conf.AuthenticationPolicyData;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.aaa.radius.sessionx.ConcurrencySessionManager;
import com.elitecore.aaa.radius.util.exprlib.RequestOnlyValueProvider;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

/**
 * Represents the Authentication service workflow through which the request will pass
 * if the ruleset provided is satisfied.
 * 
 * @author narendra.pathai
 *
 */
public class AuthServicePolicy extends AAAServicePolicy<RadAuthRequest>
implements RadiusServicePolicy<RadAuthRequest, RadAuthResponse> {
	public static final String MODULE = "AUTH-SP";
	
	private final AuthenticationPolicyData data;
	private LogicalExpression ruleset;
	private AuthenticationChainHandler authenticationChainHandler = new AuthenticationChainHandler();
	private RadPluginRequestHandler pluginRequestHandler;
	private Optional<ConcurrencySessionManager> sessionManager = Optional.absent();
	private RadAuthServiceHandler postResponseHandler;
	
	AuthServicePolicy(RadAuthServiceContext authServiceContext, AuthenticationPolicyData authenticationPolicyData) {
		super(authServiceContext);
		this.data = authenticationPolicyData;
	}

	@Override
	public void init() throws InitializationFailedException {
		//all initialized parts are provided by factory
	}

	public boolean assignRequest(RadAuthRequest serviceRequest) {
		return ruleset.evaluate(
				new RequestOnlyValueProvider(serviceRequest));
	}

	@Override
	public String getPolicyName() {
		return data.getPolicyName();
	}

	@Override
	public void handlePostPluginRequest(RadAuthRequest request, RadAuthResponse response, ISession session) {
		pluginRequestHandler.handlePostRequest(request, response, session);
	}

	@Override
	public void handlePrePluginRequest(RadAuthRequest request, RadAuthResponse response, ISession session) {
		pluginRequestHandler.handlePreRequest(request, response, session);
	}

	@Override
	public boolean isValidatePacket() {
		return data.isValidatePacket();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean checkForUserIdentityAttr(RadServiceRequest request,RadServiceResponse response) {
		boolean isValidRequest = false;
		if (request.getRadiusAttribute(WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue()) != null
				|| response.getClientData().getVendorType().equals("WiMAX-DHCP")) {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Request is from DHCP Server so skipping the user-identity check.");
			}					
			return true;
		}
		
		List<String> userIdentities;
		RadClientData clientData = ((AAAServerContext)getServiceContext()
				.getServerContext()).getServerConfiguration()
				.getRadClientConfiguration()
				.getClientData(((RadServiceRequest)request).getClientIp());
		
		userIdentities = Collectionz.firstNonEmpty(clientData.getUserIdentities(),
					data.getUserIdentities())
				.orElse(Collections.<String>emptyList());
		
		if (userIdentities.isEmpty()) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "No User Identity is configured in Authentication Policy: " 
						+ data.getPolicyName() + " and Client Profile: " + response.getClientData().getProfileName());
			}
			return isValidRequest;
		}
		
		final int configuredAttributeSize = userIdentities.size();
		for (int i = 0; i < configuredAttributeSize; i++) {
			if (request.getRadiusAttribute(userIdentities.get(i)) != null) {
				isValidRequest = true;
				break;
			}
		}
		
		if(isValidRequest == false) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "None of User Identity attributes: " + userIdentities 
						+ " was found in request.");
			}
		}
		
		return isValidRequest;
	}
	
	@Override
	public Optional<ConcurrencySessionManager> getSessionManager() {
		return sessionManager;
	}
	
	@Override
	public boolean applyResponseBehavior(RadAuthRequest request, RadAuthResponse response){
		boolean responseBehaviorApplicable = authenticationChainHandler.isResponseBehaviorApplicable();
		if (responseBehaviorApplicable) {
			data.getDefaultResponseBehavior().apply(this, request, response);
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Skipping application of default response behavior for policy: " + data.getPolicyName());
			}
		}
		return responseBehaviorApplicable;
	}

	@Override
	public void reInit() throws InitializationFailedException{

	}
	
	public RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> newAdditionalExecutor(RadAuthRequest request, RadAuthResponse response) {
		return new RadiusRequestExecutor<RadAuthRequest, RadAuthResponse>(postResponseHandler, request, response);
	}

	public RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> newExecutor(RadAuthRequest request, RadAuthResponse response) {
		return new RadiusRequestExecutor<RadAuthRequest, RadAuthResponse>(authenticationChainHandler, request, response);
	}
	
	public AuthenticationPolicyData getConfiguration() {
		return data;
	}

	void addHandler(RadAuthServiceHandler handler) {
		authenticationChainHandler.addHandler(handler);
	}
	
	void setRuleset(LogicalExpression ruleset) {
		this.ruleset = ruleset;
	}

	void setPostResponseHandler(RadAuthServiceHandler postResponseHandler) {
		this.postResponseHandler = postResponseHandler;
	}

	void setPluginRequestHandler(RadPluginRequestHandler pluginRequestHandler) {
		this.pluginRequestHandler = pluginRequestHandler;
	}

	void setSessionManager(Optional<ConcurrencySessionManager> sessionManager) {
		this.sessionManager = sessionManager;
	}
	
	@Override
	public String toString() {
		return String.format("\tName = %s, Rule set = %s", data.getPolicyName(),
				data.getRuleset());
	}
}
