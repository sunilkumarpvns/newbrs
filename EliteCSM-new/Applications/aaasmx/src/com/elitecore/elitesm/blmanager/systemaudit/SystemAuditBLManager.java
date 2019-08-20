package com.elitecore.elitesm.blmanager.systemaudit;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.elitesm.datamanager.systemaudit.Data.ISystemAuditData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;

public class SystemAuditBLManager extends BaseBLManager{
	
	public void updateTbltSystemAudit(IStaffData staffData, String actionAlias,String transactionId) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SystemAuditDataManager systemAuditDataManager=getSystemAuditDataManager(session);
		
		if(systemAuditDataManager == null){
    		throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {
			systemAuditDataManager.updateTbltSystemAudit(staffData,actionAlias,transactionId);
		} catch (DataManagerException e) {
			rollbackSession(session);
			throw new DataManagerException("Failed to update system audit, reason :"+ e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
   public void updateTbltSystemAudit(IStaffData staffData, String actionAlias) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SystemAuditDataManager systemAuditDataManager=getSystemAuditDataManager(session);
		
		if(systemAuditDataManager == null){
    		throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {
			systemAuditDataManager.updateTbltSystemAudit(staffData,actionAlias);
		} catch (DataManagerException e) {
			rollbackSession(session);
			throw new DataManagerException("Failed to update system audit, reason :"+ e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	public List getAllAction() throws DataManagerException{
		
		List actionListInCombo = new ArrayList();
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SystemAuditDataManager systemAuditDataManager=getSystemAuditDataManager(session);
		
		if(systemAuditDataManager == null)
    		throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try{
			actionListInCombo = systemAuditDataManager.getAllAction();
		}
		catch(Exception e){
			throw new DataManagerException("Failed to retrive all action, reason :"+ e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		
		return actionListInCombo;
	}
	
	public List getAllUsers() throws DataManagerException{
		
		List usersListInCombo = new ArrayList();
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SystemAuditDataManager systemAuditDataManager=getSystemAuditDataManager(session);
		
		if(systemAuditDataManager == null)
    		throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try{
			usersListInCombo = systemAuditDataManager.getAllUsers();
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive all users, reason :"+ e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		
		return usersListInCombo;
	}
	
	public PageList getAuditDetails(ISystemAuditData systemAuditData,int pageNo, int pageSize, IStaffData staffData) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SystemAuditDataManager systemAuditDataManager=getSystemAuditDataManager(session);
		PageList auditDetails = null;
		
		if(systemAuditDataManager == null)
    		throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();
			auditDetails = systemAuditDataManager.getAuditDetails(systemAuditData, pageNo, pageSize);
			commit(session);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e.getCause());
		} finally {
			closeSession(session);
		}
		return auditDetails;
	}
	
	
	public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
        return systemAuditDataManager; 
    }

}
