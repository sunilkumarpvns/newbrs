package com.elitecore.netvertexsm.util.driver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.corenetvertex.constants.DriverTypes;
import com.elitecore.netvertexsm.server.EliteScheduler;
import com.elitecore.netvertexsm.util.driver.cdr.NVSMCSVDriver;
import com.elitecore.netvertexsm.util.driver.cdr.NVSMDBCDRDriver;
import com.elitecore.netvertexsm.util.driver.cdr.conf.DBCDRDriverConfiguration;
import com.elitecore.netvertexsm.util.driver.cdr.conf.impl.CSVDriverConfigurationImpl;
import com.elitecore.netvertexsm.ws.logger.Logger;

public class CDRDriverManager {

	private static final String MODULE = "DRV-MGR";
	
	private static Map<Integer, CDRDriver> cdrDriverMap = new HashMap<Integer, CDRDriver>();
	
	
	public static void init(List<DriverConfiguration> driverConfigurations) throws InitializationFailedException{
		CDRDriver cdrDriver;
		
		
		
		TaskScheduler takScheduler = EliteScheduler.getInstance().TASK_SCHEDULER;
		
		for(DriverConfiguration configuration : driverConfigurations) {
			try{
				switch (configuration.getDriverTypeId()) {
					case DriverTypes.CSV_CDR_DRIVER :
						cdrDriver = new NVSMCSVDriver((CSVDriverConfigurationImpl)configuration,takScheduler);
						cdrDriver.init();
						cdrDriverMap.put(configuration.getDriverInstanceId(), cdrDriver);
						break;
						
					case DriverTypes.DB_CDR_DRIVER :
						cdrDriver = new NVSMDBCDRDriver((DBCDRDriverConfiguration) configuration,takScheduler);
						cdrDriver.init();
						cdrDriverMap.put(configuration.getDriverInstanceId(), cdrDriver);
						break;
					     
					default :
						break;
				}
			} catch(DriverInitializationFailedException ex){
				Logger.logError(MODULE, "Error in initializing CDR driver " + configuration.getDriverName() + ". Reason: " + ex.getMessage());
				Logger.logTrace(MODULE, ex);
			} 
		}
	}
	
	public static CDRDriver getCDRDriver(int driverInstanceId) {
		return cdrDriverMap.get(driverInstanceId);
	}
	
	public static void stop() {
		for(CDRDriver cdrDriver : cdrDriverMap.values()) {
			cdrDriver.stop();
		}
	}
	
	
	
}
