package com.elitecore.netvertexsm.datamanager.core.system.accessgroup;

import com.elitecore.corenetvertex.sm.acl.RoleModuleActionData;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.IRoleActionRelData;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.IRoleData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;

import java.sql.Timestamp;
import java.util.List;

public interface AccessGroupDataManager extends DataManager{
	/**
     * This method creates a new Group.
     * Object of IGroupData must be supplied to it.
     * @param  groupData
     * @return
     */
	public IRoleData create(IRoleData groupData) throws DataManagerException;
	
	/**
     * This method returns all the GroupData
     * @return List
     */
	public List getAccessGroupList() throws DataManagerException;

	/**
     * This method delete a GroupData.
     * Group Id must be supplied to it.
     * @param  roleId
     * @throws DataManagerException
     */
	public void deleteAccessGroup(long roleId) throws DataManagerException;
	
	/**
	 * This method delete GroupActionRelData.
	 * Group Id must be supplied to it.
	 * @param roleId
	 * @throws DataManagerException
	 */
	public void deleteAccessGroupActionRel(long roleId) throws DataManagerException;
	
	/**
     * This method returns all the GroupData 
     * @param  groupData
     * @return List
     */
	public List getAccessGroupList(IRoleData groupData) throws DataManagerException;
	
	public List getRoleActionRelList(IRoleActionRelData groupActionRelData) throws DataManagerException;
	
	/**
     * This method updates GroupData.
     * Object of Staff Id must be supplied to it along with required Status.
     * @param  groupData, statusChangeDate
     * @return
     */
	public void updateAccessGroup(IRoleData groupData,Timestamp statusChangeDate) throws DataManagerException;
	
	/**
	 * This method delete a GroupActionRelData.
	 * @param actionId
	 * @param roleId
	 * @throws DataManagerException
	 */
	public void deleteRoleAction(String actionId,long roleId) throws DataManagerException;
	
	/**
	 * This method creates a new GroupActionRelData.
	 * actionId and roleId must be specified.
	 * @param actionId
	 * @param roleId
	 * @throws DataManagerException
	 */
	public void createRoleAction(String actionId,long roleId) throws DataManagerException;
	
	/**
	 * This method returns all the GroupActionRelData
	 * @return List
	 * @throws DataManagerException
	 */
	public List getRoleActionRelList() throws DataManagerException;
	
	public List getRoleActionRelList(long roleId) throws DataManagerException;
	public List getRoleActionRelList(List<Long> roleIds) throws DataManagerException;
	public void updateActionRole(List actionList,long roleId) throws DataManagerException;
	public void createRoleActionRelation(List<RoleModuleActionData> roleModuleActionRelList,long roleId) throws DataManagerException;
	public void updateRoleActionRelation(List<RoleModuleActionData> lstRoleModuleActionRelList,long roleId) throws DataManagerException;
	public List getRoleModuleActionRelList(long roleId) throws DataManagerException;
	public void deleteRoleModuleActionRelation(long roleId) throws DataManagerException;
	public IStaffData getStaffBelongingRole(long roleId) throws DataManagerException;
}
