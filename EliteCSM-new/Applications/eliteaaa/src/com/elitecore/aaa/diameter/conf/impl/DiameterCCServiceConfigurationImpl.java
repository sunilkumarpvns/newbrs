package com.elitecore.aaa.diameter.conf.impl;

import java.util.Map;

import com.elitecore.aaa.diameter.conf.DiameterServiceConfigurable;
import com.elitecore.aaa.diameter.conf.DiameterServiceConfigurationDetail;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.DiameterServicePolicyConfiguration;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.CCPolicyConfigurable;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.DiameterCCServiceConfigurable;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;


@ReadOrder(order = {"diameterCCServiceConfigurable", "ccPolicyConfigurable"})

public class DiameterCCServiceConfigurationImpl extends CompositeConfigurable implements DiameterServiceConfigurationDetail{
	
	private final static String KEY = "DIA_CC";
	
	@Configuration private DiameterCCServiceConfigurable diameterCCServiceConfigurable;
	@Configuration private CCPolicyConfigurable ccPolicyConfigurable;


	@Override
	public boolean isEligible(Class<? extends Configurable> configurableClass) {
		return false;
	}
	
	
	@PostRead
	public void postReadProcessing() {
		
	}
	
	@PostReload
	public void postReloadProcessing() {
		
	}
	
	@PostWrite
	public void postWriteProcessing() {

	}
		
	@Override
	public String getKey() {
		return KEY;
	}


	@Override
	public DiameterServicePolicyConfiguration getDiameterServicePolicyConfiguration(
			String policyId) {
		return this.ccPolicyConfigurable.getCcPolicyConfiguration(policyId);
	}

	@Override
	public Map<String, DiameterServicePolicyConfiguration> getDiameterServicePolicyConfiguration() {
		return this.ccPolicyConfigurable.getCcPolicyConfigurationsMap();
	}

	@Override
	public DiameterServiceConfigurable getDiameterServiceConfiguration() {
		return diameterCCServiceConfigurable;
	}

	@Override
	public String toString() {
		return this.diameterCCServiceConfigurable.toString();
	}
}
