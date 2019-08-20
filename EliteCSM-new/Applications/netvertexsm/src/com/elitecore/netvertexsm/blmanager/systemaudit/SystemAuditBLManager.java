package com.elitecore.netvertexsm.blmanager.systemaudit;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.netvertexsm.datamanager.systemaudit.Data.ISystemAuditData;

public class SystemAuditBLManager extends BaseBLManager{
	
	public void updateTbltSystemAudit(IStaffData staffData, String actionAlias,String transactionId) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SystemAuditDataManager systemAuditDataManager=getSystemAuditDataManager(session);
		try	{
		if(systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			systemAuditDataManager.updateTbltSystemAudit(staffData,actionAlias,transactionId);
		}finally{
			session.close();
		}
	}
   public void updateTbltSystemAudit(IStaffData staffData, String actionAlias) throws DataManagerException{
	   	
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SystemAuditDataManager systemAuditDataManager=getSystemAuditDataManager(session);
		try{
		if(systemAuditDataManager == null)
    		throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
    		
		systemAuditDataManager.updateTbltSystemAudit(staffData,actionAlias);
		}finally{
    	session.close();
		}
	}
	
	public List getAllAction() throws DataManagerException{
		
		List actionListInCombo = new ArrayList();
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SystemAuditDataManager systemAuditDataManager=getSystemAuditDataManager(session);
		try{
		if(systemAuditDataManager == null)
    		throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
			actionListInCombo = systemAuditDataManager.getAllAction();
			
		}finally{
			session.close();
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
			
		}finally{
			session.close();
		}
		
		return usersListInCombo;
	}
	
	public PageList getAuditDetails(ISystemAuditData systemAuditData,int pageNo, int pageSize) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SystemAuditDataManager systemAuditDataManager=getSystemAuditDataManager(session);
		PageList auditDetails = null;
		
		try{
		if(systemAuditDataManager == null)
    		throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			
			auditDetails = systemAuditDataManager.getAuditDetails(systemAuditData, pageNo, pageSize);
		}finally{
			session.close();
		}
		return auditDetails;
	}
	
	
	public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
        return systemAuditDataManager; 
    }

}