package com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr;

import java.sql.Connection;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.CoreSessionData;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.SessionConfData;

public interface SessionDataManager {
	
	public SessionConfData getSessionConfData()throws DataManagerException;
	
	public CoreSessionData getCoreSessionData(long coreSessionId)throws DataManagerException;
	
	public void updateSessionConf(SessionConfData sessionConfData)throws DataManagerException;

	public void deleteOldMapping(SessionConfData sessionConfData)throws DataManagerException;

	public void createNewMapping(SessionConfData sessionConfData)throws DataManagerException;


	public void createSessionConfiguration(SessionConfData sessionConfData)throws DataManagerException;

	public DatabaseDSData getDatabaseDS(Integer dataSourceID)throws DataManagerException;

	public void deleteActiveSession(Long[] csIDList, Connection connection) throws DataManagerException;

	public CoreSessionData getCoreSessionData(long coreSessionId,Connection connection) throws DataManagerException;

}