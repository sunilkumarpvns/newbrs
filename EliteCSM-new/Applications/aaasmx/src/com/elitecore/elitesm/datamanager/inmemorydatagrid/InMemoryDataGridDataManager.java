package com.elitecore.elitesm.datamanager.inmemorydatagrid;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.inmemorydatagrid.data.InMemoryDataGridData;

public interface InMemoryDataGridDataManager extends DataManager {
	
	public void update(InMemoryDataGridData widgetConfigData, IStaffData staffData)throws DataManagerException;
	public InMemoryDataGridData search()throws DataManagerException;
	public InMemoryDataGridData getInMemoryDataGridConfiguration() throws DataManagerException;

}
