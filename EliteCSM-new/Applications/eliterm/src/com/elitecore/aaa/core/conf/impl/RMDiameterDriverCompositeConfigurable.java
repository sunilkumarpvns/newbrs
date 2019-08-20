package com.elitecore.aaa.core.conf.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.elitecore.aaa.core.conf.DriverConfigurationProvider;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.aaa.rm.conf.impl.RMDiameterClassicCSVAcctDriverConfigurable;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;

@ReadOrder (order = { "diameterClassicCSVAcctDriverConfigurable" })
public class RMDiameterDriverCompositeConfigurable extends CompositeConfigurable implements DriverConfigurationProvider {
	
	@Configuration private RMDiameterClassicCSVAcctDriverConfigurable diameterClassicCSVAcctDriverConfigurable;
	
	private Map<String, DriverConfiguration> driverConfigurationMap;
	
	public RMDiameterDriverCompositeConfigurable() {
		 driverConfigurationMap = new HashMap<String, DriverConfiguration>();
	}
	
	@Override
	public DriverConfiguration getDriverConfiguration(String driverInstanceId) {
		return driverConfigurationMap.get(driverInstanceId);
	}

	@Override
	public Collection<DriverConfiguration> getDriverConfigurations() {
		return driverConfigurationMap.values();
	
	}

	@PostRead
	public void postReadProcessing() {
		for (DriverConfiguration driverConfiguration : diameterClassicCSVAcctDriverConfigurable.getDriverConfigurationList()) {
			driverConfigurationMap.put(driverConfiguration.getDriverInstanceId(), driverConfiguration);
		}
	}

	@Override
	public boolean isEligible(Class<? extends Configurable> configurableClass) {
		return false;
	}
	
	@PostReload
	public void postReloadProcessing() {
		
	}
	
	@PostWrite
	public void postWriteProcessing() {

	}
}
