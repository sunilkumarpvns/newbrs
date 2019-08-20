package com.elitecore.elitesm.datamanager.history;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;

public interface HistoryDataManager {

	public List<DatabaseHistoryData> getDatabaseDSHistoryData(String auditUId)throws DataManagerException;

	public IDatabaseDSData getDatabaseDSDataDetails(Long auditId)throws DataManagerException;
	public List<DatabaseHistoryData> getDatabaseDSHistoryDataByName(String moduleName)throws DataManagerException;
}
