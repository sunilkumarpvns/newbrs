package com.elitecore.netvertexsm.datamanager.locationconfig.area;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.locationconfig.area.data.AreaData;

public interface AreaDataManager extends DataManager {
	
	public void create(AreaData areaMasterData) throws DataManagerException;
	
	public void update(AreaData areaMasterData) throws DataManagerException;
	
	public void delete(Long[] areaMasterIDs) throws DataManagerException;
	
	public AreaData getAreaData(Long areaId) throws DataManagerException;
	
	public List<AreaData> getAreasByCity(Long cityID) throws DataManagerException;
	
	public PageList search(AreaData areaMasterData, int pageNo, int pageSize) throws DataManagerException;
	
}