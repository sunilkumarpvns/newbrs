package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.gracepolicy.GracePolicyBLManager;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name = "authorization-handler")
@XmlType(propOrder = {"wimaxEnabled", "defaultSessionTimeoutInSeconds", "rejectOnCheckItemNotFound",
		"rejectOnRejectItemNotFound", "acceptOnPolicyNotFound", "gracePolicy"})
@ValidObject
public class DiameterAuthorizationHandlerData extends DiameterApplicationHandlerDataSupport implements Validator {
	private boolean rejectOnRejectItemNotFound;
	private boolean rejectOnCheckItemNotFound = true;
	private boolean acceptOnPolicyNotFound;
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
	public boolean isAcceptOnPolicyNotFound() {
		return acceptOnPolicyNotFound;
	}
	
	public void setAcceptOnPolicyNotFound(boolean acceptOnPolicyNotFound) {
		this.acceptOnPolicyNotFound = acceptOnPolicyNotFound;
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
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Enabled", getEnabled());
		object.put("Wimax", wimaxEnabled);
		object.put("Default Session Timeout", defaultSessionTimeoutInSeconds);
		object.put("Reject On Reject Item Not Found", rejectOnRejectItemNotFound);
		object.put("Reject On Check Item Not Found", rejectOnCheckItemNotFound);
		object.put("Accept On Policy On Found", acceptOnPolicyNotFound);
		object.put("Grace Policy", gracePolicy);
		return object;
	}
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;

		if (Strings.isNullOrBlank(gracePolicy) == false) {
			try {
				GracePolicyBLManager gracePolicyBLManager = new GracePolicyBLManager();
				gracePolicyBLManager.getGracePolicyByName(gracePolicy,false);
			} catch (Exception e) {
				RestUtitlity
						.setValidationMessage(context,
								"Configured Grace Policy is invalid in Authorization handler");
				isValid = false;
			}
		}

		return isValid;
	}
}
