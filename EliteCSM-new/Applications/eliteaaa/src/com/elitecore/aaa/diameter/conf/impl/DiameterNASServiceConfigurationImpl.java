package com.elitecore.aaa.diameter.conf.impl;

import java.util.Map;

import com.elitecore.aaa.diameter.conf.DiameterServiceConfigurable;
import com.elitecore.aaa.diameter.conf.DiameterServiceConfigurationDetail;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.DiameterServicePolicyConfiguration;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.NASPolicyConfigurable;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;

@ReadOrder(order = {"diameterNASServiceConfigurable", "nasPolicyConfigurable"})

public class DiameterNASServiceConfigurationImpl extends CompositeConfigurable implements DiameterServiceConfigurationDetail{
	
	private final static String KEY = "DIA_NAS";
	
	@Configuration private DiameterNasServiceConfigurable diameterNASServiceConfigurable ;
	@Configuration private NASPolicyConfigurable nasPolicyConfigurable;

	@Override
	public DiameterServicePolicyConfiguration getDiameterServicePolicyConfiguration(
			String policyId) {
		return this.nasPolicyConfigurable.getNASServicePolicyConfiguration(policyId);
	}

	@Override
	public Map<String, DiameterServicePolicyConfiguration> getDiameterServicePolicyConfiguration() {
		return this.nasPolicyConfigurable.getNASServicePolicyConfigurationMap();
	}

	@Override
	public DiameterServiceConfigurable getDiameterServiceConfiguration() {
		return diameterNASServiceConfigurable;
	}

	@Override
	public boolean isEligible(Class<? extends Configurable> configurableClass) {
		// TODO Auto-generated method stub
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
	public String toString() {
		return diameterNASServiceConfigurable.toString();
	}
}
