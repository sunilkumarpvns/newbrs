package com.elitecore.elitesm.datamanager.core.system.accessgroup;

import java.sql.Timestamp;
import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.GroupActionRelData;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.IGroupActionRelData;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.IGroupData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;

public interface AccessGroupDataManager extends DataManager{
	/**
     * This method creates a new Group.
     * Object of IGroupData must be supplied to it.
     * @param  groupData
     * @return
     */
	public IGroupData create(IGroupData groupData) throws DataManagerException;
	
	/**
     * This method returns all the GroupData
     * @return List
     */
	public List getAccessGroupList() throws DataManagerException;

	/**
     * This method delete a GroupData.
     * Group Id must be supplied to it.
     * @param  groupId
     * @throws DataManagerException
     */
	public String deleteAccessGroup(String groupId) throws DataManagerException;
	
	/**
	 * This method delete GroupActionRelData.
	 * Group Id must be supplied to it.
	 * @param groupId
	 * @throws DataManagerException
	 */
	public void deleteAccessGroupActionRel(String groupId) throws DataManagerException;
	
	/**
     * This method returns all the GroupData 
     * @param  groupData
     * @return List
     */
	public List getAccessGroupList(IGroupData groupData) throws DataManagerException;
	
	public List getGroupActionRelList(IGroupActionRelData groupActionRelData) throws DataManagerException;
	
	/**
     * This method updates GroupData.
     * Object of Staff Id must be supplied to it along with required Status.
     * @param  groupData, statusChangeDate
     * @return
     */
	public void updateAccessGroup(IGroupData groupData,Timestamp statusChangeDate) throws DataManagerException;
	
	/**
	 * This method delete a GroupActionRelData.
	 * @param actionId
	 * @param groupId
	 * @throws DataManagerException
	 */
	public void deleteGroupAction(String actionId,long groupId) throws DataManagerException;
	
	/**
	 * This method creates a new GroupActionRelData.
	 * actionId and groupId must be specified.
	 * @param actionId
	 * @param groupId
	 * @throws DataManagerException
	 */
	public void createGroupAction(String actionId,String groupId) throws DataManagerException;
	
	/**
	 * This method returns all the GroupActionRelData
	 * @return List
	 * @throws DataManagerException
	 */
	public List getGroupActionRelList() throws DataManagerException;
	
	public List getGroupActionRelList(String groupId) throws DataManagerException;
	public List getGroupActionRelList(List<String> groupIds) throws DataManagerException;
	public void updateActionGroup(List<GroupActionRelData> actionList,String groupId, IStaffData staffData) throws DataManagerException;
	public String getAccessGroupName(String groupId)throws DataManagerException;
}
