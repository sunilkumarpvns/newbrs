package com.elitecore.netvertexsm.datamanager.devicemgmt;

import java.sql.SQLException;
import java.util.List;

import com.elitecore.core.util.csv.CSVData;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.util.MessageData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.devicemgmt.data.TACDetailData;

public interface DeviceMgmtDataManager extends DataManager{

	public void create(TACDetailData tacDetailData) throws DataManagerException;
	
	public void update(TACDetailData tacDetailData) throws DataManagerException;
	
	public void updateByTAC(TACDetailData tacDetailData) throws DataManagerException;
	
	public void delete(Long[] tacDetailIds) throws DataManagerException;
	
	public TACDetailData getTACDetailData(Long tacDetailId) throws DataManagerException;
	
	public PageList search(TACDetailData tacDetailData, int pageNo, int pageSize) throws DataManagerException;

	public List<MessageData> uploadCSV(CSVData csvData) throws DataManagerException, SQLException ;
	
	public List<TACDetailData> getTACDetails() throws DataManagerException; 
		
	public List<TACDetailData> getTacDetails(Long[] tacDetailIds) throws DataManagerException; 
	
}
