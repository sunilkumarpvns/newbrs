package com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping.data.RuleMappingData;

public interface RuleMappingDataManager {
	public PageList search(RuleMappingData ruleMappingData, int requiredPageNo,Integer pageSize) throws DataManagerException;
	public void create(RuleMappingData ruleMappingData) throws DataManagerException;
	public void delete(Long[] ruleMappingIDS)  throws DataManagerException;
	public void update(RuleMappingData ruleMappingData) throws DataManagerException;
	public RuleMappingData getRuleMappingData(long ruleMappingId) throws DataManagerException;
	public List<RuleMappingData> getRoutingTableDataList() throws DataManagerException;
}
