package com.elitecore.aaa.rm.drivers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.aaa.core.conf.ClassicCSVAcctDriverConfiguration;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.crestel.conf.CrestelDriverConf;
import com.elitecore.aaa.core.drivers.DriverFactory;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.drivers.RadClassicCSVAcctDriver;
import com.elitecore.aaa.rm.drivers.conf.RMDiameterChargingDriverConf;
import com.elitecore.aaa.rm.drivers.conf.RMParlayDriverConfiguration;
import com.elitecore.aaa.rm.drivers.conf.impl.RMCrestelOCSv2DriverConfImpl;
import com.elitecore.core.driverx.IEliteDriver;

public class RMDriverFactory implements DriverFactory {

	private final AAAServerContext serverContext;

	public RMDriverFactory(@Nonnull AAAServerContext serverContext) {
		this.serverContext = serverContext;
	}
	
	@Override
	public @Nullable IEliteDriver createDriver(DriverTypes driverType, String driverInstanceId) {
		switch (driverType) {
		case RM_PARLAY_DRIVER:
			return new RMParlayDriver(serverContext, (RMParlayDriverConfiguration) serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(driverInstanceId));
		case RM_DIAMETER_DRIVER:
			return new RMDiameterChargingDriver(serverContext, (RMDiameterChargingDriverConf) serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(driverInstanceId));
		case RM_CRESTEL_CHARGING_DRIVER:
			return new RMCrestelChargingDriver(serverContext, (CrestelDriverConf) serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(driverInstanceId));
		case RM_CLASSIC_CSV_ACCT_DRIVER:
			return new RMRadClassicCSVAcctDriver(serverContext,(ClassicCSVAcctDriverConfiguration) serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(driverInstanceId));
		case RM_CRESTEL_OCSV2_DRIVER:
			return new RMCrestelOCSv2Driver(serverContext, (RMCrestelOCSv2DriverConfImpl) serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(driverInstanceId));
		case RAD_CLASSIC_CSV_ACCT_DRIVER:
			return new RadClassicCSVAcctDriver(serverContext, (ClassicCSVAcctDriverConfiguration) serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(driverInstanceId));
		default:
			return null;
		}
	}
}
