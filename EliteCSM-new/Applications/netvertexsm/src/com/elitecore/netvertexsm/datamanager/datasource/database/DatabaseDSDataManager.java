package com.elitecore.netvertexsm.datamanager.datasource.database;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.sqloperationexception.DatabaseConnectionException;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.IDatabaseDSData;

public interface DatabaseDSDataManager {

	public void create(IDatabaseDSData databaseDSData) throws DataManagerException,DuplicateInstanceNameFoundException;

	public List getAllList() throws DataManagerException;

	public void updateStatus(long transactionId, String commonStatusId,Timestamp timestamp) throws DataManagerException;

	public void delete(long transactionId) throws DataManagerException;

	public PageList search(IDatabaseDSData databaseDSData, int pageNo,int pageSize) throws DataManagerException;

	public List getList(IDatabaseDSData databaseDSData) throws DataManagerException;

	public void updateDatabaseDSDetail(IDatabaseDSData databaseDSData) throws DataManagerException,DuplicateInstanceNameFoundException;
	
	public IDatabaseDSData getDatabaseDSData(Long databaseId) throws DataManagerException;
	
	public Connection getConnection(IDatabaseDSData databaseDSData) throws DatabaseConnectionException;
	
	public Connection getDBConnection(Long databaseDSId) throws DatabaseConnectionException;
	
	public Set<String> getDataTypeList(Long databaseId) throws DataManagerException;
	
	public DatabaseDSData getDatabaseDS(Long databaseDSID) throws DataManagerException;
}
