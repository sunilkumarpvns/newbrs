package com.elitecore.aaa.diameter.service.application.handlers.conf;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.json.JSONObject;

import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterApplicationHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterAuthWimaxHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterAuthorizationChainHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterAuthorizationHandler;
import com.elitecore.aaa.util.constants.AAAServerConstants;

@XmlRootElement(name = "authorization-handler")
public class DiameterAuthorizationHandlerData extends DiameterApplicationHandlerDataSupport {
	private boolean rejectOnRejectItemNotFound;
	private boolean rejectOnCheckItemNotFound = true;
	private boolean acceptOnPolicyOnFound;
	private boolean wimaxEnabled;
	private String gracePolicy;
	private long defaultSessionTimeoutInSeconds = AAAServerConstants.POLICY_DEFAULT_SESSION_TIMEOUT_IN_SECS;
	
	@XmlElement(name = "reject-on-reject-item-not-found", type = boolean.class, defaultValue = "true")
	public boolean isRejectOnRejectItemNotFound() {
		return rejectOnRejectItemNotFound;
	}
	public void setRejectOnRejectItemNotFound(boolean rejectOnRejectNotFound) {
		this.rejectOnRejectItemNotFound = rejectOnRejectNotFound;
	}

	@XmlElement(name = "reject-on-check-item-not-found", type = boolean.class)
	public boolean isRejectOnCheckItemNotFound() {
		return rejectOnCheckItemNotFound;
	}
	
	public void setRejectOnCheckItemNotFound(boolean rejectOnCheckNotFound) {
		this.rejectOnCheckItemNotFound = rejectOnCheckNotFound;
	}
	
	@XmlElement(name = "continue-on-policy-not-found", type = boolean.class)
	public boolean isContinueOnPolicyNotFound() {
		return acceptOnPolicyOnFound;
	}
	
	public void setContinueOnPolicyNotFound(boolean acceptOnPolicyOnFound) {
		this.acceptOnPolicyOnFound = acceptOnPolicyOnFound;
	}

	@XmlElement(name = "wimax")
	public boolean isWimaxEnabled() {
		return wimaxEnabled;
	}
	
	public void setWimaxEnabled(boolean wimaxEnabled) {
		this.wimaxEnabled = wimaxEnabled;
	}
	
	@XmlElement(name = "grace-policy")
	public String getGracePolicy() {
		return gracePolicy;
	}
	
	public void setGracePolicy(String gracePolicy) {
		this.gracePolicy = gracePolicy;
	}
	
	@XmlElement(name = "default-session-timeout-in-secs")
	public long getDefaultSessionTimeoutInSeconds() {
		return defaultSessionTimeoutInSeconds;
	}
	
	public void setDefaultSessionTimeoutInSeconds(long defaultSessionTimeoutInSecs) {
		this.defaultSessionTimeoutInSeconds = defaultSessionTimeoutInSecs;
	}
	
	@Override
	public DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> createHandler(
			DiameterServiceContext context) {
		DiameterAuthorizationChainHandler diameterAuthorizationChainHandler = new DiameterAuthorizationChainHandler();
		diameterAuthorizationChainHandler.addHandler(new DiameterAuthorizationHandler<ApplicationRequest, ApplicationResponse>(context, this));
		if(isWimaxEnabled()) {
			diameterAuthorizationChainHandler.addHandler(new DiameterAuthWimaxHandler<ApplicationRequest, ApplicationResponse>(context, context.getServerContext().getServerConfiguration().getWimaxConfiguration(),
					context.getServerContext().getServerConfiguration().getSpiKeysConfiguration(), 
					context.getServerContext().getWimaxSessionManager(), 
					context.getServerContext().getEapSessionManager(), 
					context.getServerContext().getKeyManager()));
		}
		return diameterAuthorizationChainHandler;
	}
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Enabled", isEnabled());
		object.put("Wimax", wimaxEnabled);
		object.put("Default Session Timeout", defaultSessionTimeoutInSeconds);
		object.put("Reject On Reject Item Not Found", rejectOnRejectItemNotFound);
		object.put("Reject On Check Item Not Found", rejectOnCheckItemNotFound);
		object.put("Accept On Policy On Found", acceptOnPolicyOnFound);
		object.put("Grace Policy", gracePolicy);
		return object;
	}
}
