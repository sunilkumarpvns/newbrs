/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   WsconfigBLManager.java                 		
 * ModualName wsconfig    			      		
 * Created on 23 December, 2010
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.blmanager.wsconfig;

import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.elitesm.datamanager.wsconfig.WebServiceConfigDataManager;
import com.elitecore.elitesm.datamanager.wsconfig.data.IWSConfigData;

public class WebServiceConfigBLManager extends BaseBLManager {
	
	public WebServiceConfigDataManager getWebServiceConfigDataManager(IDataManagerSession session) { 
		return (WebServiceConfigDataManager) DataManagerFactory.getInstance().getDataManager(WebServiceConfigDataManager.class, session);
	}
	@Override
	public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
        return (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session); 
    }
	
	public IWSConfigData getSubscriberConfiguration() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		WebServiceConfigDataManager wsconfigDataManager = getWebServiceConfigDataManager(session);
		
		if(wsconfigDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

		try {
			return wsconfigDataManager.getSubscriberConfiguration();
		} catch (DataManagerException e) {
			throw new DataManagerException(e.getMessage(),e.getCause());
		} finally {
			closeSession(session);
		}
	}
	
	public void updateSubscriberConfiguration(IWSConfigData subscriberDBConfigData, IStaffData staffData) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		WebServiceConfigDataManager wsconfigDataManager = getWebServiceConfigDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		

		if(wsconfigDataManager == null || systemAuditDataManager == null )
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
			
		try{
			session.beginTransaction();	
			wsconfigDataManager.updateSubscriberConfiguration(subscriberDBConfigData, staffData);
			//systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias,transactionId);

			session.commit();
			session.close();
		}
		catch(Exception e){
        	e.printStackTrace();
        	session.rollback();
        	session.close();
        	
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally {
			closeSession(session);
		}
	}
	
	/*
	 * Session Mgmt
	 */
	
	
	public IWSConfigData getSessionMgmtConfiguration() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		WebServiceConfigDataManager wsconfigDataManager = getWebServiceConfigDataManager(session);
		
		if(wsconfigDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

		try {
			return wsconfigDataManager.getSessionMgmtConfiguration();
		} catch (DataManagerException e) {
			throw new DataManagerException(e.getMessage(),e.getCause());
		} finally {
			closeSession(session);
		}
	}
	
	public void updateSessionMgmtConfiguration(IWSConfigData sessionMgmtConfigData, IStaffData staffData) throws DataManagerException{
   	
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		WebServiceConfigDataManager wsconfigDataManager = getWebServiceConfigDataManager(session);

		if(wsconfigDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
			
		try{
			session.beginTransaction();	
			wsconfigDataManager.updateSessionMgmtConfiguration(sessionMgmtConfigData, staffData);
			
			session.commit();
			session.close();
		}
		catch(Exception e){
        	e.printStackTrace();
        	session.rollback();
        	session.close();
        	
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally {
			closeSession(session);
		}
		
	}
	
}
