package com.elitecore.aaa.diameter.service.application.handlers.conf;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.diameter.policies.tgppserver.TGPPServerPolicyData;

public abstract class DiameterApplicationHandlerDataSupport implements DiameterApplicationHandlerData {

	private boolean enabled;
	private String handlerName;
	
	/*
	 * Transient properties
	 */
	private TGPPServerPolicyData policyData;
	private AAAConfigurationContext configurationContext;
	private String policyName;

	@XmlElement(name = "enabled")
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	@XmlTransient
	public TGPPServerPolicyData getPolicyData() {
		return policyData;
	}

	@Override
	public void setPolicyData(TGPPServerPolicyData policyData) {
		this.policyData = policyData;
	}
	
	@XmlTransient
	public String getPolicyName() {
		return policyName;
	}
	
	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}
	
	@XmlTransient
	public AAAConfigurationContext getConfigurationContext() {
		return configurationContext;
	}

	public void setConfigurationContext(AAAConfigurationContext configurationContext) {
		this.configurationContext = configurationContext;
	}

	@Override
	public void postRead() {
		setPolicyName(getPolicyData().getName());
	}

	@XmlElement(name = "handler-name", type = String.class)
	public String getHandlerName() {
		return handlerName;
	}

	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}
}
