package com.elitecore.aaa.diameter.conf.impl;

import java.util.Map;

import com.elitecore.aaa.diameter.conf.DiameterServiceConfigurable;
import com.elitecore.aaa.diameter.conf.DiameterServiceConfigurationDetail;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.DiameterServicePolicyConfiguration;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.EAPPolicyConfigurable;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;

@ReadOrder(order = {"diameterEAPServiceConfigurable", "eapPolicyConfigurable"})
		 
public class DiameterEAPServiceConfigurationImpl extends CompositeConfigurable implements DiameterServiceConfigurationDetail{
	
	private final static String KEY = "DIA_EAP";
	
	@Configuration private DiameterEAPServiceConfigurable diameterEAPServiceConfigurable;
	@Configuration private EAPPolicyConfigurable eapPolicyConfigurable;

		
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
	public DiameterServicePolicyConfiguration getDiameterServicePolicyConfiguration(
			String policyId) {
		return this.eapPolicyConfigurable.getEapServicePolicyConfiguration(policyId);
	}

	@Override
	public Map<String, DiameterServicePolicyConfiguration> getDiameterServicePolicyConfiguration() {
		return this.eapPolicyConfigurable.getEapServicePolicyConfigurationMap();
	}

	@Override
	public DiameterServiceConfigurable getDiameterServiceConfiguration() {
		return diameterEAPServiceConfigurable;
	}

	@Override
	public String toString() {
		return diameterEAPServiceConfigurable.toString();
	}
}
