package com.elitecore.aaa.diameter.service.drivers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.elitecore.aaa.core.conf.impl.ClassicCSVAcctDriverConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.service.application.drivers.cdr.DiameterCSVDriver;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverNotFoundException;
import com.elitecore.core.commons.drivers.TypeNotSupportedException;
import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.diameterapi.core.drivers.DiameterCDRDriverFactory;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;

public class DiameterCDRDriverFactoryImpl implements DiameterCDRDriverFactory {

	private AAAServerContext serverContext;
	private Map<String, CDRDriver<DiameterPacket>> driverConfigurationById = null;
	private Map<String, CDRDriver<DiameterPacket>> driverConfigurationByName = null;

	public DiameterCDRDriverFactoryImpl(AAAServerContext serverContext) {
		this.serverContext = serverContext;
		driverConfigurationById  = new HashMap<String, CDRDriver<DiameterPacket>>();
		driverConfigurationByName = new HashMap<String, CDRDriver<DiameterPacket>>();
	}
	
	@Override
	@Nullable public CDRDriver<DiameterPacket> getDriverById(String driverId) throws DriverInitializationFailedException,DriverNotFoundException, TypeNotSupportedException{
		DriverConfiguration driverConfiguration = serverContext.getServerConfiguration().getDiameterDriverConfiguration().getDriverConfiguration(driverId);
		
		if(driverConfiguration == null){
			throw new DriverNotFoundException("Driver Configuration not found for the Driver-Id: " + driverId);
		}
		
		if (driverConfiguration.getDriverType() == DriverTypes.NAS_CLASSIC_CSV_ACCT_DRIVER) {
			CDRDriver<DiameterPacket> cdrDriver = driverConfigurationById.get(driverId);
			
			if (cdrDriver != null) {
				return cdrDriver;
			}
			
			cdrDriver = createCDRDriver(driverConfiguration);
			driverConfigurationById.put(driverId, cdrDriver);
			driverConfigurationByName.put(driverConfiguration.getDriverName(), cdrDriver);
			return cdrDriver;
		
		}
		throw new TypeNotSupportedException("Driver Type mismatches for Name : " + driverConfiguration.getDriverName() , driverConfiguration.getDriverType().value); 
	}

	@Override
	@Nullable public CDRDriver<DiameterPacket> getDriver(String driverName) throws DriverInitializationFailedException,DriverNotFoundException, TypeNotSupportedException {
		
		Collection<DriverConfiguration> driverConfigurationList = serverContext.getServerConfiguration().getDiameterDriverConfiguration().getDriverConfigurations();
		if(Collectionz.isNullOrEmpty(driverConfigurationList)){
			throw new DriverNotFoundException("No Driver Configuration found");
		}
		CDRDriver<DiameterPacket> cdrDriver = driverConfigurationByName.get(driverName);
		if(cdrDriver != null){
			return cdrDriver;
		}
		for(DriverConfiguration driverConfiguration : driverConfigurationList){
			if(driverConfiguration.getDriverName().equals(driverName)){
				return getDriverById(driverConfiguration.getDriverInstanceId());
			}
		}
		throw new DriverNotFoundException("Driver Configuration not found for the Driver Name: " + driverName);
	}
	
	CDRDriver<DiameterPacket> createCDRDriver(DriverConfiguration driverConfiguration)
			throws DriverInitializationFailedException {
		CDRDriver<DiameterPacket> cdrDriver;
		cdrDriver =  new DiameterCSVDriver(serverContext.getServerHome(), 
				(ClassicCSVAcctDriverConfigurationImpl) driverConfiguration,
				serverContext.getTaskScheduler());
		cdrDriver.init();
		return cdrDriver;
	}

}
