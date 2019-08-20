package com.elitecore.netvertexsm.datamanager.servermgr.spr;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.spr.data.SPRData;

public interface SPRDataManager extends DataManager {
	
	public void delete(String[] selectedIds) throws DataManagerException;
	public void create(SPRData sprData) throws DataManagerException;
	public PageList search (SPRData sprData, int requiredPageNo, int pageSize) throws DataManagerException;
	public void update(SPRData sprData) throws DataManagerException;
	public SPRData getSPRData(String sprId) throws DataManagerException;
	public List<SPRData> getSPRDataList() throws DataManagerException;
	

}
