package com.elitecore.aaa.radius.service.auth.policy.handlers.conf;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONObject;

import com.elitecore.aaa.core.conf.impl.RadiusPolicyDetail;
import com.elitecore.aaa.core.subscriber.conf.SubscriberProfileRespositoryDetailsAware;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.AuthorizationChainHandler;
import com.elitecore.aaa.radius.service.auth.handlers.BWMode;
import com.elitecore.aaa.radius.service.auth.handlers.BlackListServiceHandler;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthVendorSpecificServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.AuthorizationHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ServicePolicyHandlerDataSupport;
import com.elitecore.aaa.radius.subscriber.conf.RadiusSubscriberProfileRepositoryDetails;

@XmlRootElement(name = "authorization-handler")
public class AuthorizationHandlerData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData, SubscriberProfileRespositoryDetailsAware {

	private String gracePolicy;
	private RadiusPolicyDetail radiusPolicy;
	private int defaultSessionTimeout;
	private boolean wimaxEnabled;
	private boolean threeGPPEnabled;

	/* Transient Fields */
	private RadiusSubscriberProfileRepositoryDetails userProfileDetails;
	
	
	@XmlElement(name = "wimax")
	public boolean isWimaxEnabled() {
		return wimaxEnabled;
	}
	
	public void setWimaxEnabled(boolean wimaxEnabled) {
		this.wimaxEnabled = wimaxEnabled;
	}
	
	@XmlElement(name = "three-gpp")
	public boolean isThreeGPPEnabled() {
		return threeGPPEnabled;
	}
	
	public void setThreeGPPEnabled(boolean threeGPPEnabled) {
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
		AuthorizationChainHandler authorizationChainHandler = new AuthorizationChainHandler();
		authorizationChainHandler.addHandler(new BlackListServiceHandler(serviceContext, BWMode.Post_Profile))
		.addHandler(new AuthorizationHandler(serviceContext, this));
		
		if(isWimaxEnabled() || isThreeGPPEnabled()) {
			authorizationChainHandler.addHandler(new RadAuthVendorSpecificServiceHandler(serviceContext, 
					isWimaxEnabled(), isThreeGPPEnabled()));
		}
		return authorizationChainHandler;
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("Authorization Handler | Enabled: %s", 10, ' '), isEnabled()));
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "WiMAX", isWimaxEnabled()));
		out.println(format("%-30s: %s", "3GPP", isThreeGPPEnabled()));
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
		object.put("Enabled", isEnabled());
		object.put("Wimax", wimaxEnabled);
		object.put("3GPP", threeGPPEnabled);
		object.put("Default Session Timeout ", defaultSessionTimeout);
		object.put("Reject On Reject Item Not Found", radiusPolicy.isRejectOnRejectItemNotFound());
		object.put("Reject On Check Item Not Found", radiusPolicy.isRejectOnCheckItemNotFound());
		object.put("Accept On Policy On Found", radiusPolicy.isAcceptOnPolicyOnFound());
		object.put("Grace Policy", gracePolicy);
		return object;
	}
}
