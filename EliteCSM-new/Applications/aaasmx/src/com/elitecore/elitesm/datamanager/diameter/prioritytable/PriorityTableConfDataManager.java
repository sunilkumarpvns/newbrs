package com.elitecore.elitesm.datamanager.diameter.prioritytable;

import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.prioritytable.data.PriorityTableData;

public interface PriorityTableConfDataManager extends DataManager{ 
	
	public List<PriorityTableData> getPriorityTableList() throws DataManagerException;
	public void updatePriorityTable(List<PriorityTableData> lstPriorityTableData, IStaffData staffData) throws DataManagerException;
	
}
