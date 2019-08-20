package com.elitecore.aaa.radius.service.base.policy.handler.conf;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.ChargeableUserIdentityConfiguration;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyData;

public abstract class ServicePolicyHandlerDataSupport implements ServicePolicyHandlerData {

	private boolean enabled = true;
	private String handlerName;
	
	/* Transient Fields */
	private RadiusServicePolicyData radiusServicePolicyData;
	private AAAServerContext aaaServerContext;

	
	@XmlElement(name = "enabled", defaultValue = "true")
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	@XmlElement(name = "handler-name" , type = String.class)
	public String getHandlerName() {
		return handlerName;
	}
	
	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}
	
	@XmlTransient
	public ChargeableUserIdentityConfiguration getCuiConfiguration() {
		return this.radiusServicePolicyData.getCuiConfiguration();
	}

	@XmlTransient
	@Override
	public String getPolicyName() {
		return this.radiusServicePolicyData.getName();
	}
	
	@XmlTransient
	public List<String> getUserIdentities() {
		return this.radiusServicePolicyData.getUserIdentities();
	}
	
	@XmlTransient
	@Override
	public RadiusServicePolicyData getRadiusServicePolicyData() {
		return this.radiusServicePolicyData;
	}
	
	@Override
	public void setRadiusServicePolicyData(RadiusServicePolicyData data) {
		this.radiusServicePolicyData = data;
	}
	
	@Override
	@XmlTransient
	public AAAServerContext getServerContext() {
		return aaaServerContext;
	}
	
	@Override
	public void setServerContext(AAAServerContext aaaServerContext) {
		this.aaaServerContext = aaaServerContext;
	}

	public void postRead() {
		//no-op
	}

}
