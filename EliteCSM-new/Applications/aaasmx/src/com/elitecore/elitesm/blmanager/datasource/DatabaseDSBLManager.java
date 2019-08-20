package com.elitecore.elitesm.blmanager.datasource;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.EliteGenericValidator;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.datasource.database.DatabaseDSDataManager;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

public class DatabaseDSBLManager extends BaseBLManager {

	private static final String MODULE = "DATABASE_DS_BLMANAGER";


	public DatabaseDSDataManager getDatabaseDSDataManager(IDataManagerSession session) {
		DatabaseDSDataManager databaseDSDataManager = (DatabaseDSDataManager) DataManagerFactory.getInstance().getDataManager(DatabaseDSDataManager.class, session);
		return databaseDSDataManager; 
	}

	public void create(IDatabaseDSData databaseDSData, IStaffData staffData) throws DataManagerException {
		List<IDatabaseDSData> lisDatabaseDSDatas = new ArrayList<IDatabaseDSData>();
		lisDatabaseDSDatas.add(databaseDSData);
		create(lisDatabaseDSDatas, staffData,"");
	}
	
	public Map<String, List<Status>> create(List<IDatabaseDSData> databaseDSData, IStaffData staffData, String isPartialSuccess) throws DataManagerException {
		return insertRecords(DatabaseDSDataManager.class, databaseDSData, staffData, ConfigConstant.CREATE_DATABASE_DATASOURCE, isPartialSuccess);	
	}
	
	public List<IDatabaseDSData> getDatabaseDSList() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSDataManager(session);

		List<IDatabaseDSData> lstDatabasedsList;

		if (databaseDSDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{

			lstDatabasedsList = databaseDSDataManager.getAllList();

		}catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			closeSession(session);
		}
		return lstDatabasedsList; 
	}
	
	public void updateStatusValidate(List<?> lstDatabaseDSIds,String commonStatusId)throws DataValidationException{

		// commonStatusId
		if(EliteGenericValidator.isBlankOrNull(commonStatusId)){
			throw (new DataValidationException("Invalid Database Datasource  commonStatusId",(MODULE+"."+"commonStatusId").toLowerCase()));
		}
	}	

	public void deleteDatabaseDSDetailById(List<String> listOfIdOrName, IStaffData staffData) throws DataManagerException{
		delete(listOfIdOrName, staffData, BY_ID);
	}
	
	public void deleteDatabaseDSDetailByName(List<String> listOfIdOrName, IStaffData staffData) throws DataManagerException{
		delete(listOfIdOrName, staffData, BY_NAME);
	}
	
	private void delete(List<String> listOfIdOrName, IStaffData staffData, boolean isIdorName) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSDataManager(session);

		if(databaseDSDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}

		try{
			session.beginTransaction();
			if(Collectionz.isNullOrEmpty(listOfIdOrName) == false){
				int size = listOfIdOrName.size();
				for(int i=0;i<size;i++){
					if(Strings.isNullOrBlank(listOfIdOrName.get(i)) == false){
						String strIdOrName = listOfIdOrName.get(i).trim();

						String name = null;
						if(isIdorName){
							name = databaseDSDataManager.deleteById(strIdOrName);
						}else{
							name = databaseDSDataManager.deleteByName(strIdOrName);
						}
						
						staffData.setAuditName(name);
						AuditUtility.doAuditing(session,staffData, ConfigConstant.DELETE_DATABASE_DATASOURCE);
					}
				}
			}
			
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception exp){
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(),exp);
		}finally{
			closeSession(session);
		}
	}
	
	public PageList search(IDatabaseDSData databaseDSData, IStaffData staffData, int pageNo, Integer pageSize) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSDataManager(session);

		PageList lstDatabaseDsList;

		if (databaseDSDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();	
			lstDatabaseDsList = databaseDSDataManager.search(databaseDSData, pageNo, pageSize);
			
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_DATABASE_DATASOURCE);
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception e){
			rollbackSession(session);
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			closeSession(session);
		}

		return lstDatabaseDsList; 
	}
	
	public void updateDatabaseDSDetailById(IDatabaseDSData databaseDSData,IStaffData staffData) throws DataManagerException,DuplicateInstanceNameFoundException {
		updateDatabaseDSDetailByName(databaseDSData, staffData, null);
	}

	public Set<String> getDataTypeList(String databaseId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSDataManager(session);

		try{
			IDatabaseDSData databaseDSData = databaseDSDataManager.getDatabaseDSDataById(databaseId);
			Set<String> datatypes = databaseDSDataManager.getDataTypeList(databaseDSData);
			return datatypes;
		}catch(DuplicateInstanceNameFoundException e){
			throw new DuplicateInstanceNameFoundException("Action failed :"+e.getMessage());	
		}catch(DataManagerException exp){
			throw new DataManagerException("Action failed :"+exp.getMessage(),exp);
		}catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			closeSession(session);
		}
	}
	
	public IDatabaseDSData getDatabaseDSDataById(String databaseId) throws DataManagerException{
		return getDatabaseDSData(databaseId,BY_ID);
	}
	
	public Connection getConnection(String databaseId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSDataManager(session);

		try{
			IDatabaseDSData databaseDSData = databaseDSDataManager.getDatabaseDSDataById(databaseId);
			return databaseDSDataManager.getConnection(databaseDSData, true);
		}catch(DataManagerException exp){
			throw new DataManagerException("Action failed :"+exp.getMessage());
		}catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			closeSession(session);
		}
	}


	public IDatabaseDSData getDatabaseDSDataByName(String databaseName) throws DataManagerException{
		return getDatabaseDSData(databaseName,BY_NAME);
	}
	
	private IDatabaseDSData getDatabaseDSData(Object searchVal, boolean isByIdOrName) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSDataManager(session);

		try{
			IDatabaseDSData databaseDSData = null;
			if (isByIdOrName){
				databaseDSData = databaseDSDataManager.getDatabaseDSDataById((String)searchVal);
			} else {
				databaseDSData = databaseDSDataManager.getDatabaseDSDataByName((String)searchVal);
			}
			return databaseDSData;
		}catch(DataManagerException exp){
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			closeSession(session);
		}
	}
	
	
	public void updateDatabaseDSDetailByName(IDatabaseDSData databaseDSData,IStaffData staffData, String name) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSDataManager(session);
		
		if(databaseDSDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}
		
		try{
			session.beginTransaction();
			
			if(name==null){
				databaseDSDataManager.updateDatabaseDSDetailById(databaseDSData,staffData,ConfigConstant.UPDATE_DATABASE_DATASOURCE);
			} else {
				databaseDSDataManager.updateDatabaseDSDetailByName(databaseDSData,staffData,ConfigConstant.UPDATE_DATABASE_DATASOURCE,name);
			}
		
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			closeSession(session);
		}
	}
	
	/**
	 * Encrypts plain text password
	 * @param password
	 * @return encrypted password
	 * @throws NoSuchEncryptionException
	 * @throws EncryptionFailedException
	 */
	public String encryptPassword(String password) throws NoSuchEncryptionException, EncryptionFailedException{
		String encryptedPassword = PasswordEncryption.getInstance().crypt(password,PasswordEncryption.ELITE_PASSWORD_CRYPT);
		return encryptedPassword;
	}
	
	public String getDatabaseIdFromName(String databaseName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSDataManager(session);
		String databaseId = "";
		if (databaseDSDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		try {
			databaseId = databaseDSDataManager.getDatabaseIdFromName(databaseName);
		} catch(DataManagerException e){
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			throw new DataManagerException(e.getMessage(),e.getCause());
		} finally {
			closeSession(session);
		}
		return databaseId;
	}
	
	/**
	 * This Function is used for retriving table fields of provided table name.
	 * @param databaseDatasourceId - configured database datasource Id. 
	 * @param tableName - configured database datasource table name.
	 * @return - set of columns for provided table name. 
	 */
	public Set<String>  getTableFieldList(String databaseDatasourceId,String tableName){
		
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		ResultSetMetaData rsMetaData = null;
		Set<String> dbFieldList = new HashSet<String>();
		
		DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
		try {
			connection = databaseDSBLManager.getConnection((databaseDatasourceId));
			String query = getQueryForDBField(tableName);
			Logger.logDebug(MODULE, "Retriving columns from table: "+tableName);
			prepareStatement = connection.prepareStatement(query);
			resultSet = prepareStatement.executeQuery();
			rsMetaData = resultSet.getMetaData();
			
			for(int i=1;i<=rsMetaData.getColumnCount();i++) {
				dbFieldList.add(rsMetaData.getColumnName(i).toUpperCase());
			}
			
		}catch(DataManagerException e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}catch(SQLException e) {
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}catch(Exception e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		} finally {
			if(resultSet != null){
				try{resultSet.close();}catch(SQLException e){}
			}
			if(prepareStatement != null){
				try{prepareStatement.close();}catch(SQLException e){}
			}
			if(connection != null){
				try{connection.close();}catch(SQLException e){}
			}
		}
		return dbFieldList;
	}
	
	private String getQueryForDBField(String tblName) {
		return new StringBuilder(26+tblName.length()).append("SELECT * FROM ")
						   .append(tblName)
						   .append(" where 1=0").toString();
	}
}