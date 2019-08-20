package com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup;

import java.util.List;


import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCCodeGroupRelData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCGroupData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;

public interface MCCMNCGroupDataManager {

	public PageList search(MCCMNCGroupData mccmncGroupData, int requiredPageNo,Integer pageSize) throws DataManagerException;
	public void create(MCCMNCGroupData mccmncGroupData) throws DataManagerException;
	public List<MCCMNCGroupData> getmccmncGroupDataList() throws DataManagerException;
	public void delete(Long[] mccmncGroupIDS)  throws DataManagerException;
	public MCCMNCGroupData getMCCMNCGroupData(long mccmncGroupId) throws DataManagerException;
	public void update(MCCMNCGroupData mccmncGroupData) throws DataManagerException;
	public  List<MCCMNCCodeGroupRelData> getMCCMNCodeGroupRelDataList(Long mccmncGroupID) throws DataManagerException;
	public List<MCCMNCCodeGroupRelData> getMCCMNCodeGroupRelDataList() throws DataManagerException;
	public void deleteMCCMNCCodeGroupOnRelData(Long mccmncGroupId) throws DataManagerException;
	public List<MCCMNCGroupData> getmccmncGroupDataList(String restrictionSql) throws DataManagerException;
    public MCCMNCGroupData getMCCMNCGroupByNetwork(MCCMNCCodeGroupRelData mccmncCodeGroupRelData)throws DataManagerException;
	
}
