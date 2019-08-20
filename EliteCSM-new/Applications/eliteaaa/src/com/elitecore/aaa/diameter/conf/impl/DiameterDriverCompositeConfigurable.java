package com.elitecore.aaa.diameter.conf.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.core.conf.DriverConfigurationProvider;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.aaa.diameter.service.application.drivers.conf.impl.DiameterClassicCSVAcctDriverConfigurable;
import com.elitecore.aaa.diameter.service.application.drivers.conf.impl.DiameterCrestelDriverOCSv2DriverConfigurable;
import com.elitecore.aaa.diameter.service.application.drivers.conf.impl.DiameterCrestelRatingDriverConfigurable;
import com.elitecore.aaa.diameter.service.application.drivers.conf.impl.DiameterDbAcctDriverConfigurable;
import com.elitecore.aaa.diameter.service.application.drivers.conf.impl.DiameterDbAuthDriverConfigurable;
import com.elitecore.aaa.diameter.service.application.drivers.conf.impl.DiameterDetailLocalAcctConfigurable;
import com.elitecore.aaa.diameter.service.application.drivers.conf.impl.DiameterHSSAuthDriverConfigurable;
import com.elitecore.aaa.diameter.service.application.drivers.conf.impl.DiameterHttpAuthDriverConfigurable;
import com.elitecore.aaa.diameter.service.application.drivers.conf.impl.DiameterLDAPAuthDriverConfigurable;
import com.elitecore.aaa.diameter.service.application.drivers.conf.impl.DiameterMAPGWAuthDriverConfigurable;
import com.elitecore.aaa.diameter.service.application.drivers.conf.impl.DiameterUserFileDriverConfigurable;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;

@ReadOrder(order = {"diameterUserFileAuthDriverConfigurable", "diameterDbDriverConfigurable",
		 "diameterLDAPAuthDriverConfigurable","diameterHttpAuthDriverConfigurable",
		 "diameterMAPGWAuthDriverConfigurable","diameterClassicCSVAcctDriverConfigurable",
		 "diameterDbAcctDriverConfigurable","diameterDetailLocalAcctConfigurable",
		 "diameterCrestelDriverConfigurable","diameterCrestelDriverOCSv2DriverConfigurable",
		 "diameterHSSAuthDriverConfigurable"})

public class DiameterDriverCompositeConfigurable extends CompositeConfigurable 
implements DriverConfigurationProvider {
	
	@Configuration private DiameterUserFileDriverConfigurable diameterUserFileAuthDriverConfigurable;
	@Configuration private DiameterDbAuthDriverConfigurable diameterDbDriverConfigurable;
	@Configuration private DiameterLDAPAuthDriverConfigurable diameterLDAPAuthDriverConfigurable;
	@Configuration private DiameterHttpAuthDriverConfigurable diameterHttpAuthDriverConfigurable;
	@Configuration private DiameterMAPGWAuthDriverConfigurable diameterMAPGWAuthDriverConfigurable;
	@Configuration private DiameterClassicCSVAcctDriverConfigurable diameterClassicCSVAcctDriverConfigurable;
	@Configuration private DiameterDbAcctDriverConfigurable  diameterDbAcctDriverConfigurable;
	@Configuration private DiameterDetailLocalAcctConfigurable diameterDetailLocalAcctConfigurable;
	@Configuration private DiameterCrestelRatingDriverConfigurable diameterCrestelDriverConfigurable;
	@Configuration private DiameterCrestelDriverOCSv2DriverConfigurable diameterCrestelDriverOCSv2DriverConfigurable;
	@Configuration private DiameterHSSAuthDriverConfigurable diameterHSSAuthDriverConfigurable;
	
	private Map<String, DriverConfiguration> driverConfigurationMap;
	
	public DiameterDriverCompositeConfigurable() {
		this.driverConfigurationMap = new HashMap<String, DriverConfiguration>();
	}

	@Override
	public Collection<DriverConfiguration> getDriverConfigurations() {
		return this.driverConfigurationMap.values();
	}

	@Override
	public DriverConfiguration getDriverConfiguration(String driverInstanceId) {
		return this.driverConfigurationMap.get(driverInstanceId);
	}

	@Override
	public boolean isEligible(Class<? extends Configurable> configurableClass) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@PostRead
	public void postReadProcessing() {
		
		List<DriverConfiguration> driverConfList = new ArrayList<DriverConfiguration>();

		driverConfList.addAll(diameterUserFileAuthDriverConfigurable.getDiameterUserfileAuthDriverList());
		driverConfList.addAll(diameterDbDriverConfigurable.getAuthdriverDetails());
		driverConfList.addAll(diameterLDAPAuthDriverConfigurable.getDiameterLdapDriverList());
		driverConfList.addAll(diameterHttpAuthDriverConfigurable.getHttpAuthDriverConfigurationImpls());
		driverConfList.addAll(diameterMAPGWAuthDriverConfigurable.getMapGWAuthDriverConfigList());
		driverConfList.addAll(diameterClassicCSVAcctDriverConfigurable.getDriverConfigurationList());
		driverConfList.addAll(diameterDbAcctDriverConfigurable.getDbAcctdriverDetails());
		driverConfList.addAll(diameterDetailLocalAcctConfigurable.getDetailLocalAcctDriverConfigList());			
		driverConfList.addAll(diameterCrestelDriverConfigurable.getCrestelDriverConfImplList());
		driverConfList.addAll(diameterCrestelDriverOCSv2DriverConfigurable.getCrestelDriverConfImplList());
		driverConfList.addAll(diameterHSSAuthDriverConfigurable.getDiaHSSAuthDriverList());

		int size = driverConfList.size();
		Map<String, DriverConfiguration> tempDriverMap = new HashMap<String, DriverConfiguration>();
		DriverConfiguration driverConfiguration;

		for(int i=0;i<size;i++){
			driverConfiguration = driverConfList.get(i); 
			tempDriverMap.put(driverConfiguration.getDriverInstanceId(), driverConfiguration);
		}
		this.driverConfigurationMap = tempDriverMap;	
	}

	@PostReload
	public void postReloadProcessing() {
		
	}
	@PostWrite
	public void postWriteProcessing() {

	}

}
