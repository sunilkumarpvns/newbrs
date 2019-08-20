package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.util.constants.RadiusServicePolicyConstants;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.gracepolicy.GracePolicyBLManager;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name = "authorization-handler")
@XmlType(propOrder = {"wimaxEnabled", "threeGPPEnabled", "defaultSessionTimeout",
		"radiusPolicy", "gracePolicy"})
@ValidObject
public class AuthorizationHandlerData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData,AcctServicePolicyHandlerData, SubscriberProfileRespositoryDetailsAware, Validator {

	private String gracePolicy;
	
	@Valid
	private RadiusPolicyDetail radiusPolicy;
	
	private int defaultSessionTimeout;
	private String wimaxEnabled;
	private String threeGPPEnabled;

	/* Transient Fields */
	private RadiusSubscriberProfileRepositoryDetails userProfileDetails;
	
	public AuthorizationHandlerData() {
		this.wimaxEnabled = RadiusServicePolicyConstants.WIMAX_ENABLED;
		this.threeGPPEnabled = RadiusServicePolicyConstants.TGPP_ENABLED;
	}
	
	@XmlElement(name = "wimax")
	@Pattern(regexp = RestValidationMessages.BOOLEAN_REGEX, message = "Invalid Value of Wimax in Authorization handler. It could be 'true' and 'false'")
	public String getWimaxEnabled() {
		return wimaxEnabled;
	}
	
	public void setWimaxEnabled(String wimaxEnabled) {
		this.wimaxEnabled = wimaxEnabled.toLowerCase();
	}
	
	@XmlElement(name = "three-gpp")
	@Pattern(regexp = RestValidationMessages.BOOLEAN_REGEX, message = "Invalid Value of 3GPP in Authorization handler. It could be 'true' and 'false'")
	public String getThreeGPPEnabled() {
		return threeGPPEnabled;
	}
	
	public void setThreeGPPEnabled(String threeGPPEnabled) {
		this.threeGPPEnabled = threeGPPEnabled;
	}
	
	
	@XmlElement(name = "default-session-timeout")
	public int getDefaultSessionTimeout() {
		return defaultSessionTimeout;
	}
	
	public void setDefaultSessionTimeout(int defaultSessionTimeout) {
		this.defaultSessionTimeout = defaultSessionTimeout;
	}
	
	@XmlElement(name = "grace-policy",type = String.class)
	public String getGracePolicy() {
		return gracePolicy;
	}
	public void setGracePolicy(String gracePolicy) {
		this.gracePolicy = gracePolicy;
	}
	
	@XmlElement(name = "radius-policy")
	public RadiusPolicyDetail getRadiusPolicy() {
		return radiusPolicy;
	}
	public void setRadiusPolicy(RadiusPolicyDetail radiusPolicy) {
		this.radiusPolicy = radiusPolicy;
	}

	@XmlTransient
	public RadiusSubscriberProfileRepositoryDetails getSubscriberProfileRepositoryDetails() {
		return userProfileDetails;
	}
	
	@Override
	public void setSubscriberProfileRepositoryDetails(RadiusSubscriberProfileRepositoryDetails details) {
		this.userProfileDetails = details;
	}
	
	@Override
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
		return null;
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("Authorization Handler | Enabled: %s", 10, ' '), getEnabled()));
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "WiMAX", getWimaxEnabled()));
		out.println(format("%-30s: %s", "3GPP", getThreeGPPEnabled()));
		out.println(getRadiusPolicy());
		out.println(format("%-30s: %s", "Default Session Timeout", getDefaultSessionTimeout()));
		out.println(format("%-30s: %s", "Grace Policy", getGracePolicy() != null ? getGracePolicy() : ""));
		out.println(repeat("-", 70));
		out.close();
		return writer.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Enabled", getEnabled());
		object.put("Wimax", wimaxEnabled);
		object.put("3GPP", threeGPPEnabled);
		object.put("Default Session Timeout ", defaultSessionTimeout);
		object.put("Reject On Reject Item Not Found", radiusPolicy.getRejectOnRejectItemNotFound());
		object.put("Reject On Check Item Not Found", radiusPolicy.getRejectOnCheckItemNotFound());
		object.put("Accept On Policy On Found", radiusPolicy.getAcceptOnPolicyOnFound());
		object.put("Grace Policy", gracePolicy);
		return object;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;

		if (Strings.isNullOrBlank(gracePolicy) == false) {
			try {
				GracePolicyBLManager gracePolicyBLManager = new GracePolicyBLManager();
				gracePolicyBLManager.getGracePolicyByName(gracePolicy, ConfigManager.chekForCaseSensitivity());
			} catch (Exception e) {
				RestUtitlity
						.setValidationMessage(context,
								"Configured Grace Policy is invalid in Authorization handler");
				isValid = false;
			}
		}

		return isValid;
	}

	@Override
	public RadAcctServiceHandler createHandler(
			RadAcctServiceContext serviceContext) {
		return null;
	}
}
