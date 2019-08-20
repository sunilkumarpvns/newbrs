package com.elitecore.netvertexsm.datamanager.servergroup;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servergroup.data.ServerInstanceGroupData;
import com.elitecore.netvertexsm.datamanager.servergroup.data.ServerInstanceGroupRelationData;

/**
 * Created by aditya on 11/5/16.
 */
public interface ServerInstanceGroupDataManager extends DataManager {

    PageList search(ServerInstanceGroupData serverInstanceGroupData, int requiredPageNo,Integer pageSize, String staffBelongingGroups) throws DataManagerException;
    void create(ServerInstanceGroupData serverInstanceGroupData) throws DataManagerException;
    void update(ServerInstanceGroupData serverInstanceGroupData) throws DataManagerException;
    void swapInstances(String serverInstanceGroupId) throws DataManagerException;
    ServerInstanceGroupData getServerIntanceGroupData(String id) throws DataManagerException;
    List getServerInstanceGroupRelationDatas() throws DataManagerException;
    void delete(String ids) throws DataManagerException;
    public List<ServerInstanceGroupRelationData> getServerInstanceGroupRelationDatasBy(String serverGroupId) throws DataManagerException;
	public void manageOrder(String[] serverInstanceGroupId)throws DataManagerException;
	public Integer getMaxOrder()throws DataManagerException;
	public ServerInstanceGroupRelationData getServerInstanceRelationDatasBy(String serverinstanceId) throws DataManagerException;
}
