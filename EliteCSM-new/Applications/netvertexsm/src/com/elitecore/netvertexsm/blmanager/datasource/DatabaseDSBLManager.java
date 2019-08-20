package com.elitecore.netvertexsm.blmanager.datasource;


import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.EliteGenericValidator;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.datasource.database.DatabaseDSDataManager;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;

public class DatabaseDSBLManager extends BaseBLManager {

	private static final String MODULE = "DATABASE_DS_BLMANAGER";


	public DatabaseDSDataManager getDatabaseDSDataManager(IDataManagerSession session) {
		DatabaseDSDataManager databaseDSDataManager = (DatabaseDSDataManager) DataManagerFactory.getInstance().getDataManager(DatabaseDSDataManager.class, session);
		return databaseDSDataManager; 
	}
	public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
		return systemAuditDataManager; 
	}
	public void create(IDatabaseDSData databaseDSData,IStaffData staffData, String actionAlias) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if (databaseDSDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			databaseDSDataManager.create(databaseDSData);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();
		}catch(DuplicateInstanceNameFoundException exp){
			session.rollback();
			throw new DuplicateInstanceNameFoundException("Duplicate User Name. : "+exp.getMessage(),exp);
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
	}
	
	public List<DatabaseDSData> getDatabaseDSList() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSDataManager(session);

		List<DatabaseDSData> lstDatabasedsList = null;

		if (databaseDSDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();

			lstDatabasedsList = databaseDSDataManager.getAllList();

			session.commit();
		}
		catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}
		return lstDatabasedsList; 
	}
	public void updateStatusValidate(List lstDatabaseDSIds,String commonStatusId)throws DataValidationException{

		// commonStatusId
		if(EliteGenericValidator.isBlankOrNull(commonStatusId)){
			throw (new DataValidationException("Invalid Database Datasource  commonStatusId",(MODULE+"."+"commonStatusId").toLowerCase()));
		}
	}	
	public void updateStatus(List<String> lstDatabaseDSIds, String commonStatusId,
			IStaffData staffData, String actionAlias) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		Date currentDate = new Date();

		if(databaseDSDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

		try{
			updateStatusValidate(lstDatabaseDSIds,commonStatusId);
			session.beginTransaction();

			if(lstDatabaseDSIds != null){
				for(int i=0;i<lstDatabaseDSIds.size();i++){
					if(lstDatabaseDSIds.get(i) != null){

						String transactionId = lstDatabaseDSIds.get(i).toString();
						long datasourceId= Long.parseLong(transactionId);
						databaseDSDataManager.updateStatus(datasourceId,commonStatusId, new Timestamp(currentDate.getTime()));

						//systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias,transactionId);
					}
				}

				session.commit();
			}else{
				throw new DataManagerException("Data Manager implementation not found for ");    		
			}
		}catch(Exception exp){
			session.rollback();
			throw new DataManagerException("Action failed :"+exp.getMessage());
		}finally{
			session.close();
		}
	}


	public void delete(List lstDatabaseDSIds,IStaffData staffData, String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if(databaseDSDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

		try{
			session.beginTransaction();
			if(lstDatabaseDSIds != null){
				for(int i=0;i<lstDatabaseDSIds.size();i++){
					if(lstDatabaseDSIds.get(i) != null){
						String transactionId = lstDatabaseDSIds.get(i).toString();
						long dasourceId = Long.parseLong(transactionId);
						databaseDSDataManager.delete(dasourceId);				    	
						systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);					}
				}
			}else{
				throw new DataManagerException("Data Manager implementation not found for ");    		
			}
			session.commit();
		
		}catch(ConstraintViolationException exp){
			try{
				session.rollback();
			}catch(Exception e){

			}
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
		}catch(Exception exp){
			session.rollback();
			throw new DataManagerException("Action failed :"+exp.getMessage(),exp);
		}finally{
			session.close();
			
		}
	}
	public PageList search(IDatabaseDSData databaseDSData,int pageNo, Integer pageSize, IStaffData staffData,String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSDataManager(session);
		
		PageList lstDatabaseDsList;

		if (databaseDSDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			lstDatabaseDsList = databaseDSDataManager.search(databaseDSData, pageNo, pageSize);			
		}
		catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
		return lstDatabaseDsList; 
	}
	
	public IDatabaseDSData getDatabaseDS(IDatabaseDSData databaseDSData,IStaffData staffData, String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSDataManager(session);
		if (databaseDSDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			List lstDatabaseDS = databaseDSDataManager.getList(databaseDSData);
			if(lstDatabaseDS != null && lstDatabaseDS.size() >= 1){
				databaseDSData = (IDatabaseDSData)lstDatabaseDS.get(0);
			}
			
		}
		catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}
		return databaseDSData;
	}

	public IDatabaseDSData getDatabaseDS(IDatabaseDSData databaseDSData) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSDataManager(session);

		if (databaseDSDataManager == null )
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{

			List lstDatabaseDS = databaseDSDataManager.getList(databaseDSData);

			if(lstDatabaseDS != null && lstDatabaseDS.size() >= 1){
				databaseDSData = (IDatabaseDSData)lstDatabaseDS.get(0);

			}

			session.commit();

		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}

		return databaseDSData;

	}
	
	public DatabaseDSData getDatabaseDS(Long databaseDSID) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSDataManager(session);
		DatabaseDSData databaseDSData = null;
		
		if (databaseDSDataManager == null )
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			databaseDSData = databaseDSDataManager.getDatabaseDS(databaseDSID);
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}
		
		return databaseDSData;
	}
	
	public void updateDatabaseDSDetail(IDatabaseDSData databaseDSData,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateInstanceNameFoundException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if(databaseDSDataManager == null || systemAuditDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}

		try{

			session.beginTransaction();

			databaseDSDataManager.updateDatabaseDSDetail(databaseDSData);
			String transactionId = Long.toString(staffData.getStaffId());
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias,transactionId);

			session.commit();
		}catch(DuplicateInstanceNameFoundException e){
			session.rollback();
			throw new DuplicateInstanceNameFoundException("Action failed :"+e.getMessage());	
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action failed :"+exp.getMessage());
		}catch(Exception e){
		    session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}

	}

	public Set<String> getDataTypeList(Long databaseId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSDataManager(session);

		try{

	
			Set<String> datatypes = databaseDSDataManager.getDataTypeList(databaseId);
			return datatypes;
		}catch(DuplicateInstanceNameFoundException e){
			session.rollback();
			throw new DuplicateInstanceNameFoundException("Action failed :"+e.getMessage());	
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action failed :"+exp.getMessage());
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}
	}
	
	public Connection getConnection(Long databaseId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSDataManager(session);

		try{
			IDatabaseDSData databaseDSData = databaseDSDataManager.getDatabaseDSData(databaseId);
			return databaseDSDataManager.getConnection(databaseDSData);
		}catch(DataManagerException exp){
			throw new DataManagerException("Action failed :"+exp.getMessage());
		}catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
	}
	
	public Connection getDBConnection(Long databaseId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSDataManager(session);

		try{
			return databaseDSDataManager.getDBConnection(databaseId);
		}catch(DataManagerException exp){
			throw new DataManagerException("Action failed :"+exp.getMessage());
		}catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
	}
}