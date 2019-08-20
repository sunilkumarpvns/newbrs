package com.elitecore.aaa.diameter.conf.impl;

import java.util.Map;

import com.elitecore.aaa.diameter.conf.DiameterServiceConfigurable;
import com.elitecore.aaa.diameter.conf.DiameterServiceConfigurationDetail;
import com.elitecore.aaa.diameter.conf.DiameterTGPPServerConfigurable;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.DiameterServicePolicyConfiguration;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.TGPPServerPolicyConfigurable;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;

@ReadOrder(order = {"serverConfigurable", "policyConfigurable"})
public class DiameterTGPPServerCompositeConfigurable extends CompositeConfigurable implements DiameterServiceConfigurationDetail {

	@Configuration private DiameterTGPPServerConfigurable serverConfigurable;
	@Configuration private TGPPServerPolicyConfigurable policyConfigurable;
	
	@Override
	public boolean isEligible(Class<? extends Configurable> configurableClass) {
		return true;
	}

	@Override
	public DiameterServicePolicyConfiguration getDiameterServicePolicyConfiguration(String policyId) {
		return policyConfigurable.getIdToPolicyConfigurationMap().get(policyId);
	}

	@Override
	public Map<String, DiameterServicePolicyConfiguration> getDiameterServicePolicyConfiguration() {
		return policyConfigurable.getIdToPolicyConfigurationMap();
	}

	@Override
	public DiameterServiceConfigurable getDiameterServiceConfiguration() {
		return serverConfigurable;
	}

	@Override
	public String getKey() {
		return AAAServerConstants.DIA_TGPP_SERVER_SERVICE_ID;
	}

	@PostRead
	public void postRead() {
		
	}
	
	@PostWrite
	public void postWrite() {
		
	}
	
	@PostReload
	public void postReload() {
		
	}
}
