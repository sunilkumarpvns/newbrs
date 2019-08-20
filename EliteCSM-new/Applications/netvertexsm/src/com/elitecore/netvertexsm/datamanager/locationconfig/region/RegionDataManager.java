package com.elitecore.netvertexsm.datamanager.locationconfig.region;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.locationconfig.region.data.RegionData;


public interface RegionDataManager extends DataManager {
	
	public void create(RegionData regionData) throws DataManagerException;
	public void createRegionByList(List<RegionData> regionDataList) throws DataManagerException;
	
	public void update(RegionData regionData) throws DataManagerException;
	
	public void delete(Long[] regionIds) throws DataManagerException;
	
	public RegionData getRegionData(Long regionId) throws DataManagerException;
	
	public PageList search(RegionData regionData, int pageNo, int pageSize) throws DataManagerException;
	public List<RegionData> getRegionDataList() throws DataManagerException;
	

}
