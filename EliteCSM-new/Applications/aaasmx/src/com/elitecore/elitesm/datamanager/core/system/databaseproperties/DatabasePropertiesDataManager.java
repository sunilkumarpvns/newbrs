package com.elitecore.elitesm.datamanager.core.system.databaseproperties;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;

public interface DatabasePropertiesDataManager extends DataManager {

	public void doAudit(String jsonString , IStaffData staffData) throws DataManagerException;
	
}
