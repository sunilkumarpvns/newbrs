package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;

public class DriverInstanceNameAdapter extends XmlAdapter<String, String>{

	private static final String MODULE = "DRIVER-NAME-ADAPTER";

	@Override
	public String unmarshal(String driverName) throws Exception {
		
		
		if (Strings.isNullOrBlank(driverName)) {
			LogManager.getLogger().error(MODULE, "Driver must be specified");
			return null;
		}
		
		String driverId = RestValidationMessages.INVALID;
		try {
			DriverBLManager driverBLManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByName(driverName);
			if (driverInstanceData == null) {
				LogManager.getLogger().error(MODULE, "Driver detail for name: " + driverName + " does not exist.");
				return driverId;
			}
			return driverInstanceData.getDriverInstanceId();
		} catch (Exception e) {
			e.printStackTrace();
			return driverId;
		}
	}

	@Override
	public String marshal(String driverInstanceId) throws Exception {
		
		if (driverInstanceId == null) {
			return null;
		}
		
		String driverName = "";
		DriverBLManager driverBLManager = new DriverBLManager();
		
		try {
			return driverBLManager.getDriverNameById(driverInstanceId);
		} catch (Exception e) {
			return driverName;
		}
	}
}
