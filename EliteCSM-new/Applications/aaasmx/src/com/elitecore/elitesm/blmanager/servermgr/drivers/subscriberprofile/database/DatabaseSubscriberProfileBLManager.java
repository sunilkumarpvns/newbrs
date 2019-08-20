package com.elitecore.elitesm.blmanager.servermgr.drivers.subscriberprofile.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.InvalidSQLStatementException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.datasource.database.DatabaseDSDataManager;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.DriverDataManager;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.data.SubscriberProfileData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.DatabaseSubscriberProfileDataManager;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DatasourceSchemaData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatabaseSubscriberProfileRecordBean;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatasourceSchemaData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.ISQLParamPoolValueData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.exception.DatasourceSchemaMisMatchException;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.SQLPoolValueData;
import com.elitecore.elitesm.ws.exception.DatabaseConnectionException;
import com.elitecore.elitesm.ws.exception.SubscriberProfileWebServiceException;
import com.elitecore.elitesm.ws.subscriber.SubscriberProfileWebServiceBLManager;

public class DatabaseSubscriberProfileBLManager extends BaseBLManager {

	private static final String MODULE = "DatabaseSubscriberProfileBLManager";

	/**
	 * @author dhanajibagdure
	 * @return Returns DatabaseSubscriberProfileDataManager
	 * @purpose This method is generated to returns DataManager instance for DatasourceData.
	 */
	private DatabaseSubscriberProfileDataManager getDatabaseSubscriberProfileDataManager(IDataManagerSession session) {
		DatabaseSubscriberProfileDataManager databaseSubscriberProfileDataManager = (DatabaseSubscriberProfileDataManager) DataManagerFactory.getInstance().getDataManager(DatabaseSubscriberProfileDataManager.class, session);
		return databaseSubscriberProfileDataManager; 
	}
	private DriverDataManager getDriverDataManager(IDataManagerSession session) {		
		DriverDataManager driverDataManager = (DriverDataManager) DataManagerFactory.getInstance().getDataManager(DriverDataManager.class, session);
		return driverDataManager;		
	}
	private DatabaseDSDataManager getDatabaseDSManager(IDataManagerSession session) {		
		DatabaseDSDataManager databaseDSDataManager = (DatabaseDSDataManager) DataManagerFactory.getInstance().getDataManager(DatabaseDSDataManager.class, session);
		return databaseDSDataManager;		
	}

	public List<IDatasourceSchemaData> getColumnNames(DBAuthDriverData dbAuthDriverData) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseSubscriberProfileDataManager databaseSubscriberProfileDataManager = getDatabaseSubscriberProfileDataManager(session);

		List<IDatasourceSchemaData> lstFieldName = null;


		List<IDatasourceSchemaData> lstColumnList = null;
		IDatasourceSchemaData datasourceSchemaData = new DatasourceSchemaData();
		DatabaseDSDataManager databaseDSManager = getDatabaseDSManager(session);
		IDatabaseDSData databaseDSData = databaseDSManager.getDatabaseDSDataById(dbAuthDriverData.getDatabaseId());
		Connection con = databaseDSManager.getConnection(databaseDSData, true);
		List<IDatasourceSchemaData> lstDatasourceSchema = databaseSubscriberProfileDataManager.getDatabaseSchemaList(dbAuthDriverData,con);

		List<IDatasourceSchemaData> lstEqualFieldName = new ArrayList<IDatasourceSchemaData>();

		try{
			lstFieldName = databaseSubscriberProfileDataManager.getFieldNames(dbAuthDriverData,con);
			if(lstDatasourceSchema != null && lstDatasourceSchema.size() > 0){
				datasourceSchemaData = (IDatasourceSchemaData)lstDatasourceSchema.get(0);
				lstColumnList = databaseSubscriberProfileDataManager.getColumnNames(dbAuthDriverData);

			}

			if(lstFieldName.size() != lstColumnList.size()){
				Logger.logInfo(MODULE, "Columns mismatch.Update Data Source Schema for db driver.");
				dbAuthDriverData.setDatasourceSchemaSet(new HashSet<IDatasourceSchemaData>(lstDatasourceSchema));
				DatabaseSubscriberProfileBLManager profileBLManager = new DatabaseSubscriberProfileBLManager();
				profileBLManager.updateDatabaseSubscribeProfileSchema(dbAuthDriverData);
				
				lstColumnList = databaseSubscriberProfileDataManager.getColumnNames(dbAuthDriverData);
			}
			
			if(lstFieldName.size() == lstColumnList.size()){
				for(int i=0;i<lstFieldName.size();i++){
					for(int j=0;j<lstColumnList.size();j++){
						if(((IDatasourceSchemaData)lstFieldName.get(i)).getFieldName().equalsIgnoreCase(((IDatasourceSchemaData)lstColumnList.get(j)).getFieldName()))

						{						
							datasourceSchemaData = (DatasourceSchemaData)lstFieldName.get(i);
							lstEqualFieldName.add(datasourceSchemaData);

						}							
					}
				}
			}else
			{
				throw new DatasourceSchemaMisMatchException("Actual  & Defined Datasouce Data Schema is not in sink (not match in terms of no of column)");   
			}
		}
		catch (DatasourceSchemaMisMatchException hExp) {             
			throw hExp;
		}
		catch(DataManagerException de){
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			try {
				if(con != null) {
					con.close();
				}
			}catch (SQLException e) {
				Logger.logTrace(MODULE, e.toString());
			}
			closeSession(session);
		}	
		return lstEqualFieldName;	

	}

	public List<IDatabaseSubscriberProfileRecordBean> getDatabaseSubscriberProfileRecord(DBAuthDriverData dbAuthDriverData,String fieldName, String fieldId) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseSubscriberProfileDataManager databaseSubscriberProfileDataManager = getDatabaseSubscriberProfileDataManager(session);

		List<IDatabaseSubscriberProfileRecordBean> lstDataSource = null;

		if (databaseSubscriberProfileDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		Connection con = null;
		try{
			con = getConnection(dbAuthDriverData,session);
			lstDataSource = databaseSubscriberProfileDataManager.getDatabaseSubscriberProfileRecord(dbAuthDriverData,con,fieldName,fieldId);
		}catch(DataManagerException de){
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			try {
				if(con != null) {
					con.close();
				}
			}catch (SQLException e) {
				Logger.logTrace(MODULE, e.toString());
			}
			closeSession(session);
		}

		return lstDataSource;		
	}

	public List<String> getInputFieldsForDuplicateRecord(DBAuthDriverData dbAuthDriverData) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseSubscriberProfileDataManager databaseSubscriberProfileDataManager = getDatabaseSubscriberProfileDataManager(session);

		List<String> uniqueKeyList = null;

		if (databaseSubscriberProfileDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		Connection connection = null;
		try{

			connection = getConnection(dbAuthDriverData,session);
			String tableName = dbAuthDriverData.getTableName();
			uniqueKeyList = databaseSubscriberProfileDataManager.getInputFieldsForDuplicateRecord(connection,tableName);
			return uniqueKeyList;
		}catch(DataManagerException de){
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			try {
				if(connection != null) {
					connection.close();
				}
			}catch (SQLException e) {
				Logger.logTrace(MODULE, e.toString());
			}
			closeSession(session);
		}
	}

	public void updateDatabaseSubscriberProfileRecord(DBAuthDriverData dbAuthDriverData, List<IDatabaseSubscriberProfileRecordBean> lstDataRecordField,String fieldName ,String fieldId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseSubscriberProfileDataManager databaseSubscriberProfileDataManager = getDatabaseSubscriberProfileDataManager(session);
		
		if(databaseSubscriberProfileDataManager == null ){
			throw new DataManagerException("Data Manager implementaion not found for" + getClass().getName());
		}
		
		Connection con=null;
		try{
			session.beginTransaction();
			con = getConnection(dbAuthDriverData,session);
			databaseSubscriberProfileDataManager.updateDatabaseSubscriberProfileRecord(con,lstDataRecordField,dbAuthDriverData.getTableName(),fieldName ,fieldId);
			session.commit();
			Logger.logDebug(MODULE,"updated successfully..");
		}catch(DataManagerException de){
			rollbackSession(session);
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			try {
				if(con != null) {
					con.close();
				}
			}catch (SQLException e) {
				Logger.logTrace(MODULE, e.toString());
			}
			closeSession(session);
		}
	}	

	public PageList getSerachFieldData(DBAuthDriverData dbAuthDriverData,String searchData,int pageNo,int pageSize,Map filterData) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseSubscriberProfileDataManager DatabaseSubscriberProfileDataManager = getDatabaseSubscriberProfileDataManager(session);
		PageList lstFieldData;
		if(DatabaseSubscriberProfileDataManager == null){
			throw new DataManagerException("Data Manager implementaion not found for" + getClass().getName());
		}

		try{
			session.beginTransaction();
			lstFieldData = DatabaseSubscriberProfileDataManager.getSearchFieldData(dbAuthDriverData,getConnection(dbAuthDriverData,session),searchData,pageNo,pageSize,filterData);
			session.commit();
		}catch(DataManagerException de){
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}

		return lstFieldData;		
	}	

	public void deleteSubscriberProfileRecords(DBAuthDriverData dbAuthDriverData, List<String> lstSubscriberProfileIds,
	IDatasourceSchemaData datasourceSchemaData) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseSubscriberProfileDataManager databaseSubscriberProfileDataManager = getDatabaseSubscriberProfileDataManager(session);
		if(databaseSubscriberProfileDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}
		
		Connection con = null;
		try{
			session.beginTransaction();
			con = getConnection(dbAuthDriverData, session);
			if(lstSubscriberProfileIds != null){
				for(int i=0;i<lstSubscriberProfileIds.size();i++){
					if(lstSubscriberProfileIds.get(i) != null)
						databaseSubscriberProfileDataManager.deleteSubscriberProfileRecord(dbAuthDriverData, con,datasourceSchemaData,lstSubscriberProfileIds.get(i).toString());
				}
				session.commit();
			}else{
				session.rollback();
				throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
			}
		}catch(DataManagerException de){
			rollbackSession(session);
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			try {
				if(con != null) {
					con.close();
				}
			}catch (SQLException e) {
				Logger.logTrace(MODULE, e.toString());
			}
			closeSession(session);
		}
	}


	public void updateValuePool(IDatasourceSchemaData datasourceSchemaData,String fieldId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseSubscriberProfileDataManager databaseSubscriberProfileDataManager = getDatabaseSubscriberProfileDataManager(session);

		if(databaseSubscriberProfileDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();
			databaseSubscriberProfileDataManager.updateValuePool(datasourceSchemaData,fieldId);
			session.commit();

		}catch(DataManagerException de){
			rollbackSession(session);
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public List<IDatasourceSchemaData> getParamValuePoolList(IDatasourceSchemaData datasourceSchemaData) throws DataManagerException {


		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseSubscriberProfileDataManager databaseSubscriberProfileDataManager = getDatabaseSubscriberProfileDataManager(session);
		List<IDatasourceSchemaData> paramValuePoolList;
		if (databaseSubscriberProfileDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			paramValuePoolList = databaseSubscriberProfileDataManager.getParamValuePoolList(datasourceSchemaData);
		}catch(DataManagerException de){
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return paramValuePoolList;

	}

	public IDatasourceSchemaData getDatasourceSchema(IDatasourceSchemaData datasourceSchemaData) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseSubscriberProfileDataManager databaseSubscriberProfileDataManager = getDatabaseSubscriberProfileDataManager(session);

		try {
			datasourceSchemaData = databaseSubscriberProfileDataManager.getDatasourceSchema(datasourceSchemaData);
			return datasourceSchemaData ;
		} catch(DataManagerException de){
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public void updateQueryPool(ISQLParamPoolValueData sqlData,String fieldId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseSubscriberProfileDataManager databaseSubscriberProfileDataManager = getDatabaseSubscriberProfileDataManager(session); 
		//SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if(databaseSubscriberProfileDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}
		try{
			session.beginTransaction();
			databaseSubscriberProfileDataManager.updateQueryPool(sqlData,fieldId);
			session.commit();
		}catch(DataManagerException de){
			rollbackSession(session);
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	public void updateQueryPool(ISQLParamPoolValueData sqlData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseSubscriberProfileDataManager DatabaseSubscriberProfileDataManager = getDatabaseSubscriberProfileDataManager(session); 

		if(DatabaseSubscriberProfileDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}

		try{
			session.beginTransaction();
			DatabaseSubscriberProfileDataManager.updateQueryPool(sqlData);
			session.commit();
		}catch(DataManagerException de){
			rollbackSession(session);
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}
	  
	public void createSchema(List<IDatasourceSchemaData> datasourceSchemaList) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseSubscriberProfileDataManager DatabaseSubscriberProfileDataManager = getDatabaseSubscriberProfileDataManager(session);

		if (DatabaseSubscriberProfileDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();
			DatabaseSubscriberProfileDataManager.createSchema(datasourceSchemaList);
			session.commit();
		}catch(DataManagerException de){
			rollbackSession(session);
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	public void updateDatabaseSubscribeProfileSchema(DBAuthDriverData dbAuthDriverData) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseSubscriberProfileDataManager databaseSubscriberProfileDataManager = getDatabaseSubscriberProfileDataManager(session);
		DriverDataManager driverDataManager =getDriverDataManager(session);

		if (databaseSubscriberProfileDataManager == null || driverDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}

		try{
			session.beginTransaction();
			databaseSubscriberProfileDataManager.updateDatabaseSubscribeProfileSchema(dbAuthDriverData.getDbAuthId(),dbAuthDriverData.getDatasourceSchemaSet());
			commit(session);

		}catch(DataManagerException de){
			rollbackSession(session);
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}
	
	public List<IDatabaseSubscriberProfileRecordBean> getDatabaseDataSchema(DBAuthDriverData dbAuthDriverData) throws DataManagerException{


		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		DatabaseSubscriberProfileDataManager databaseSubscriberProfileDataManager = getDatabaseSubscriberProfileDataManager(session);

		if (driverDataManager == null || databaseSubscriberProfileDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for DriverDataManager/DatabaseSubscriberProfileDataManager/DatabaseDSDataManager ");
		}
		
		Connection con=null;
		try{
			con =getConnection(dbAuthDriverData,session);
			List<IDatabaseSubscriberProfileRecordBean> list = databaseSubscriberProfileDataManager.getDatabaseDataSchema(dbAuthDriverData,con);
			return list;
		}catch(DataManagerException de){
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			try {
				if(con != null) {
					con.close();
				}
			}catch (SQLException e) {
				Logger.logTrace(MODULE, e.toString());
			}
			closeSession(session);
		}

	}

	public List<SQLPoolValueData> getPoolValueFromQuery(DBAuthDriverData dbAuthDriverData,String queryString) throws DataManagerException,InvalidSQLStatementException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		DatabaseSubscriberProfileDataManager databaseSubscriberProfileDataManager = getDatabaseSubscriberProfileDataManager(session);

		if (driverDataManager == null || databaseSubscriberProfileDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}
		
		Connection con=null;
		try{
			con = getConnection(dbAuthDriverData,session);
			List<SQLPoolValueData> list = databaseSubscriberProfileDataManager.getPoolValueFromQuery(con,queryString);
			return list;
		}catch(InvalidSQLStatementException de){
			throw de;
		}catch(DataManagerException de){
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			try {
				if(con != null) {
					con.close();
				}
			}catch (SQLException e) {
				Logger.logTrace(MODULE, e.toString());
			}
			closeSession(session);
		}

	}

	public void addDatabaseSusbscriberProfileRecord(DBAuthDriverData dbAuthDriverData, List<IDatabaseSubscriberProfileRecordBean> lstSubscriberProfileRecordBean)  throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		DatabaseSubscriberProfileDataManager databaseSubscriberProfileDataManager = getDatabaseSubscriberProfileDataManager(session);

		if (driverDataManager == null || databaseSubscriberProfileDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}

		Connection con=null; 
		try{

			session.beginTransaction();
			con = getConnection(dbAuthDriverData,session);
			databaseSubscriberProfileDataManager.addDatabaseSusbscriberProfileRecord(dbAuthDriverData,con,lstSubscriberProfileRecordBean);
			session.commit();
		}catch(DataManagerException de){
			rollbackSession(session);
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			try {
				if(con != null) {
					con.close();
				}
			}catch (SQLException e) {
				Logger.logTrace(MODULE, e.toString());
			}
			closeSession(session);
		}

	}

	private Connection getConnection(DBAuthDriverData dbAuthDriverData,IDataManagerSession session) throws DataManagerException, Exception {
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSManager(session);
		if (databaseDSDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}
 
		IDatabaseDSData databaseDSData = databaseDSDataManager.getDatabaseDSDataById(dbAuthDriverData.getDatabaseId());
		
		Connection con = databaseDSDataManager.getConnection(databaseDSData, true);
		con.setAutoCommit(false);
		return con;
	}
	
	public SubscriberProfileData getSubscriberProfileData(String name) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseSubscriberProfileDataManager databaseSubscriberProfileDataManager = getDatabaseSubscriberProfileDataManager(session);
		
		if(databaseSubscriberProfileDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}
		
		SubscriberProfileData subscriberProfileData = null;
		try {
			 subscriberProfileData = databaseSubscriberProfileDataManager.getSubscriberProfileData(name);
		} catch(DataManagerException exp){
			throw exp;
		}catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
		return subscriberProfileData;
	}
	
	public void addSubscriberProfileData(SubscriberProfileData subscriberProfileData, IStaffData staffData) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseSubscriberProfileDataManager databaseSubscriberProfileDataManager = getDatabaseSubscriberProfileDataManager(session);
		
		if(databaseSubscriberProfileDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}
		
		try {
			session.beginTransaction();
			databaseSubscriberProfileDataManager.addSubscriberProfileData(subscriberProfileData);
			
			staffData.setAuditName(subscriberProfileData.getUserIdentity());
			AuditUtility.doAuditing(session,staffData, ConfigConstant.ADD_SUBSCRIBE_PROFILE);
			commit(session);
		} catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
	}
	public void deleteSubscriberProfileRecords(List<String> subscriberProfileNames, IStaffData staffData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseSubscriberProfileDataManager databaseSubscriberProfileDataManager = getDatabaseSubscriberProfileDataManager(session);
		
		if(databaseSubscriberProfileDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}
		
		try {
			session.beginTransaction();
			if (Collectionz.isNullOrEmpty(subscriberProfileNames) == false) {
				for (String userIdentity : subscriberProfileNames) {
					 databaseSubscriberProfileDataManager.deleteSubscriberProfileData(userIdentity);
					 staffData.setAuditName(userIdentity);
					AuditUtility.doAuditing(session,staffData, ConfigConstant.DELETE_SUBSCRIBE_PROFILE);
				}	
				commit(session);
			}
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
		} finally {
			closeSession(session);
		}
	}
	
	public void getSubscriberUsingUserIdentity(String name) throws SubscriberProfileWebServiceException, SQLException, DatabaseConnectionException, DataManagerException{
		SubscriberProfileWebServiceBLManager subscriberBLManager = new SubscriberProfileWebServiceBLManager();
		
		try {
			Map<String, Map<String, String>> findByUserIdentity = subscriberBLManager.findByUserIdentity(name.toLowerCase());
			if(Maps.isNullOrEmpty(findByUserIdentity) == false){
				throw new DataManagerException("Failed to add Database Subcriber Profile Record, Reason: ORA-00001: unique constraint violated");
			}
		}catch(DataManagerException exp){
			throw exp;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
