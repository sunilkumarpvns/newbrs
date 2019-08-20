package com.elitecore.aaa.core.conf.impl;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.elitecore.aaa.core.conf.DriverConfigurationProvider;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.conf.impl.RadClassicCSVDriverConfigurable;
import com.elitecore.aaa.radius.conf.impl.RadDBAcctDriverConfigurable;
import com.elitecore.aaa.radius.conf.impl.RadDetailLocalDriverConfigurable;
import com.elitecore.aaa.radius.drivers.DriverConfigurable;
import com.elitecore.aaa.radius.drivers.conf.impl.RadDBAuthDriverConfigurable;
import com.elitecore.aaa.radius.drivers.conf.impl.RadHSSAuthDriverConfigurable;
import com.elitecore.aaa.radius.drivers.conf.impl.RadHttpAuthDriverConfigurable;
import com.elitecore.aaa.radius.drivers.conf.impl.RadLDAPAuthDriverConfigurable;
import com.elitecore.aaa.radius.drivers.conf.impl.RadUserFileAuthDriverConfigurable;
import com.elitecore.aaa.radius.drivers.conf.impl.RadWebServiceAuthDriverConfigurable;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;

@ReadOrder(order = {
		"radUserFileAuthDriverConfigurable", "radLDAPAuthDriverConfigurable",
		"radDBAuthDriverConfigurable", "radHttpAuthDriverConfigurable",
		"mapGWAuthDriverConfigurable", "radWebServiceAuthDriverConfigurable",
		"radHSSAuthDriverConfigurable","radClassicCSVDriverConfigurable",
		"radDetailLocalDriverConfigurable" ,"radDBAcctDriverConfigurable"
})
public class RadiusDriversConfigurable extends CompositeConfigurable implements DriverConfigurationProvider {
	@Configuration private RadUserFileAuthDriverConfigurable radUserFileAuthDriverConfigurable;
	@Configuration private RadLDAPAuthDriverConfigurable radLDAPAuthDriverConfigurable ;
	@Configuration private RadDBAuthDriverConfigurable radDBAuthDriverConfigurable;
	@Configuration private RadHttpAuthDriverConfigurable radHttpAuthDriverConfigurable;
	@Configuration private RadMAPGWAuthDriverConfigurable mapGWAuthDriverConfigurable ;
	@Configuration private RadWebServiceAuthDriverConfigurable radWebServiceAuthDriverConfigurable ;
	@Configuration private RadHSSAuthDriverConfigurable radHSSAuthDriverConfigurable;
	@Configuration private RadClassicCSVDriverConfigurable radClassicCSVDriverConfigurable;
	@Configuration private RadDetailLocalDriverConfigurable radDetailLocalDriverConfigurable;
	@Configuration private RadDBAcctDriverConfigurable radDBAcctDriverConfigurable;

	private Map<String, DriverConfiguration> driverConfigurationMap;

	public RadiusDriversConfigurable() {
		driverConfigurationMap = new LinkedHashMap<String, DriverConfiguration>();
	}

	@Override
	public boolean isEligible(Class<? extends Configurable> configurableClass) {
		return false;
	}

	@PostRead
	public void postReadProcessing() {
		AAAConfigurationContext configurationContext = (AAAConfigurationContext)getConfigurationContext();

		/**
		 * Post Processing is required after reading Driver Configuration.
		 */
		postReadProcessingForRadAuthDriverConfigurable();

		postReadProcessingForAcctServiceDrivers(configurationContext.getServerContext());
	}
	
	@PostWrite
	public void postWriteProcessing() {}
	
	@PostReload
	public void postReloadProcessing() {}


	private void postReadProcessingForRadAuthDriverConfigurable() {
		addToMap(radUserFileAuthDriverConfigurable);
		addToMap(radLDAPAuthDriverConfigurable);
		addToMap(radDBAuthDriverConfigurable);
		addToMap(radHttpAuthDriverConfigurable);
		addToMap(mapGWAuthDriverConfigurable);
		addToMap(radWebServiceAuthDriverConfigurable);
		addToMap(radHSSAuthDriverConfigurable);
	}

	private void postReadProcessingForAcctServiceDrivers(AAAServerContext serverContext) {
		addToMap(radClassicCSVDriverConfigurable);
		addToMap(radDetailLocalDriverConfigurable);
		addToMap(radDBAcctDriverConfigurable);
	}
	
	private void addToMap(DriverConfigurable driversConfigurable) {
		for (DriverConfiguration driverConfiguration : driversConfigurable.getDriverConfigurationList()) {
			driverConfigurationMap.put(driverConfiguration.getDriverInstanceId(), driverConfiguration);
		}
	}

	public DriverConfiguration getDriverConfiguration(String driverInstanceId) {
		return this.driverConfigurationMap.get(driverInstanceId);
	}

	public Collection<DriverConfiguration> getDriverConfigurations() {		
		return this.driverConfigurationMap.values();
	}
}
