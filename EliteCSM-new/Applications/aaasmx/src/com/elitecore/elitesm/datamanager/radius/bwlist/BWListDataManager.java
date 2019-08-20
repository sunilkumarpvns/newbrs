package com.elitecore.elitesm.datamanager.radius.bwlist;


import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateEntityFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.bwlist.data.BWListData;

public interface BWListDataManager extends DataManager{

	public BWListData create(BWListData bwlistData) throws DataManagerException,DuplicateEntityFoundException;
	
	public PageList search(BWListData bwListData, int requiredPageNo,Integer pageSize)  throws DataManagerException;

	public void delete(String bwId)throws DataManagerException;

	public void updateStatus(String bwId, String commonStatusId) throws DataManagerException;

	public BWListData getBWListData(String bwId)throws DataManagerException;

	public void update(BWListData bwlistOldData, BWListData bwlistData, IStaffData staffData)throws DataManagerException;

}
 