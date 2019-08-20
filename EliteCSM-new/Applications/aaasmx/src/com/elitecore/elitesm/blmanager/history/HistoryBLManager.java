package com.elitecore.elitesm.blmanager.history;


import java.util.List;

import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.history.HistoryDataManager;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;

public class HistoryBLManager extends BaseBLManager {

	public HistoryDataManager getHistoryDataManager(IDataManagerSession session) {
		HistoryDataManager databaseDSDataManager = (HistoryDataManager) DataManagerFactory.getInstance().getDataManager(HistoryDataManager.class, session);
		return databaseDSDataManager; 
	}

	public List<DatabaseHistoryData> getHistoryData(String auditUId) throws DataManagerException  {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		HistoryDataManager historyDataManager = getHistoryDataManager(session);
		List<DatabaseHistoryData> lstDatabaseDSHistoryDatas = null;
		try{
			
			lstDatabaseDSHistoryDatas = historyDataManager.getDatabaseDSHistoryData(auditUId);
			session.close();
			return lstDatabaseDSHistoryDatas;
		}catch(DuplicateInstanceNameFoundException e){
			session.close();
			throw new DuplicateInstanceNameFoundException("Action failed :"+e.getMessage());	
		}catch(DataManagerException exp){
			session.close();
			throw new DataManagerException("Action failed :"+exp.getMessage(),exp);
		}catch(Exception e){
			session.close();
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			closeSession(session);
		}
	}

	public List<DatabaseHistoryData> getHistoryDataByModuleName(String moduleName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		HistoryDataManager historyDataManager = getHistoryDataManager(session);
		return historyDataManager.getDatabaseDSHistoryDataByName(moduleName);
	}
}



