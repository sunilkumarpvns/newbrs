package com.elitecore.elitesm.datamanager.datasource.database;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.DatabaseConnectionException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;

public interface DatabaseDSDataManager extends DataManager{

	@Override
	public String create(Object object) throws DataManagerException,DuplicateInstanceNameFoundException;

	public List getAllList() throws DataManagerException;

	public void updateStatus(String transactionId, String commonStatusId,Timestamp timestamp) throws DataManagerException;

	public String deleteById(String transactionId) throws DataManagerException;
	
	public String deleteByName(String dataSourceName) throws DataManagerException;

	public PageList search(IDatabaseDSData databaseDSData, int pageNo,int pageSize) throws DataManagerException;

	public List getList(IDatabaseDSData databaseDSData) throws DataManagerException;

	public void updateDatabaseDSDetailById(IDatabaseDSData databaseDSData,IStaffData staffData,String actionAlias) throws DataManagerException,DuplicateInstanceNameFoundException;
	
	public void updateDatabaseDSDetailByName(IDatabaseDSData idatabaseDSData, IStaffData staffData, String actionAlias, String name) throws DataManagerException;
	
	public IDatabaseDSData getDatabaseDSDataById(String databaseId) throws DataManagerException;
	
	public Set<String> getDataTypeList(IDatabaseDSData databaseDSData) throws DataManagerException;

	public Connection getConnection(IDatabaseDSData databaseDSData, boolean isDescrept) throws DatabaseConnectionException;
	
	public IDatabaseDSData getDatabaseDSDataByName(String databaseName) throws DataManagerException;

	public String getDatabaseIdFromName(String databaseName) throws DataManagerException;
}
