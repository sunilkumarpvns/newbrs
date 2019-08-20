package com.elitecore.netvertexsm.datamanager.core.system.group;

import java.util.List;

import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.IRoleData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;

public interface GroupDataManager extends DataManager {

	public void create(GroupData groupData) throws DataManagerException;
	
	public void update(GroupData groupData) throws DataManagerException;
	
	public void delete(String[] groupDataIds) throws DataManagerException;
	
	public GroupData getGroupData(String groupDataId) throws DataManagerException;
	
	public PageList search(GroupData groupData, int pageNo, int pageSize) throws DataManagerException;
	
	public List<GroupData> getGroupDatas() throws DataManagerException;

	public String getGroupNames(List<String> groupIds) throws DataManagerException;

	public IRoleData getRoleDataForGroupData(String groupId) throws DataManagerException;

	List<GroupData> getGroupDataFromIds(List<String> ids)throws  DataManagerException;

}
