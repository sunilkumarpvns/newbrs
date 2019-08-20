package com.elitecore.elitesm.hibernate.core.system.databaseproperties;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.databaseproperties.DatabasePropertiesDataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.util.constants.ConfigConstant;

public class HDatabasePropertiesDataManager  extends HBaseDataManager implements DatabasePropertiesDataManager{

	@Override
	public void doAudit(String jsonString, IStaffData staffData) throws DataManagerException {
		
		staffData.setAuditId(getAuditId(ConfigConstant.DATABASEPROPERTIES));
		
		doAuditingJson(jsonString, staffData, ConfigConstant.UPDATE_DATABASEPROPERTIESFILE);
	}

}
