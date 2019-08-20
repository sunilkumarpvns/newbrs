package com.elitecore.aaa.core.conf.impl;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.core.conf.DriverConfigurationProvider;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.aaa.rm.conf.impl.RMCrestelChargingDriverConfigurable;
import com.elitecore.aaa.rm.conf.impl.RMCrestelOCSv2DriverConfigurable;
import com.elitecore.aaa.rm.conf.impl.RMDiaChargingClassicCSVAcctConfigurable;
import com.elitecore.aaa.rm.conf.impl.RMDiameterChargingDriverConfigurable;
import com.elitecore.aaa.rm.conf.impl.RMParlayDriverConfigurable;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;

@ReadOrder(order = { "rmDiaChargingClassicCSVAcctConfigurable","rmCrestelOCSv2DriverConfigurable",
					"rmParlayDriverConfigurable", "rmDiameterChargingDriverConfigurable",
					"rmCrestelChargingDriverConfigurable"
})

public class RMDriversConfigurable extends CompositeConfigurable implements DriverConfigurationProvider {

	/***
	 * Driver related to Charging Service
	 */
	@Configuration private RMDiaChargingClassicCSVAcctConfigurable rmDiaChargingClassicCSVAcctConfigurable;
	@Configuration private RMCrestelOCSv2DriverConfigurable rmCrestelOCSv2DriverConfigurable;
	@Configuration private RMParlayDriverConfigurable rmParlayDriverConfigurable;
	@Configuration private RMDiameterChargingDriverConfigurable rmDiameterChargingDriverConfigurable;
	@Configuration private RMCrestelChargingDriverConfigurable rmCrestelChargingDriverConfigurable;

	private List<DriverConfiguration> driverConfigurations;
	private Map<String, DriverConfiguration> driverConfigurationMap;
	
	public RMDriversConfigurable() {
		this.driverConfigurations = new LinkedList<DriverConfiguration>();
		this.driverConfigurationMap = new LinkedHashMap<String, DriverConfiguration>();
	}
	
	@PostRead
	public void postReadProcessing() {
		/**
		 * Post Processing is required after reading Driver Configuration.
		 */
		postReadProcessingForChargingServiceDriverConfigurable();

		storeInDataStructures();
	}
	
	private void postReadProcessingForChargingServiceDriverConfigurable() {
		driverConfigurations.addAll(rmDiaChargingClassicCSVAcctConfigurable.getDriverConfigurationList());
		driverConfigurations.addAll(rmCrestelOCSv2DriverConfigurable.getDriverList());
		driverConfigurations.addAll(rmParlayDriverConfigurable.getDriverList());
		driverConfigurations.addAll(rmDiameterChargingDriverConfigurable.getDriverList());
		driverConfigurations.addAll(rmCrestelChargingDriverConfigurable.getDriverList());
	}

	private void storeInDataStructures() {
		for(DriverConfiguration driverConfiguration : driverConfigurations) {
			this.driverConfigurationMap.put(driverConfiguration.getDriverInstanceId(), driverConfiguration);
		}
	}
	
	@PostWrite
	public void postWriteProcessing() {}
	
	@PostReload
	public void postReloadProcessing() {}
	
	@Override
	public DriverConfiguration getDriverConfiguration(String driverInstanceId) {
		return this.driverConfigurationMap.get(driverInstanceId);
	}

	@Override
	public Collection<DriverConfiguration> getDriverConfigurations() {
		return driverConfigurations;
	}

	@Override
	public boolean isEligible(Class<? extends Configurable> configurableClass) {
		return false;
	}

}
