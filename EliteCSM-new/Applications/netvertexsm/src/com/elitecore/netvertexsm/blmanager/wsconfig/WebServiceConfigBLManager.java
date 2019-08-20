/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   WsconfigBLManager.java                 		
 * ModualName wsconfig    			      		
 * Created on 23 December, 2010
 * Last Modified on                                     
 */                                                     
package com.elitecore.netvertexsm.blmanager.wsconfig;

import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.netvertexsm.datamanager.wsconfig.WebServiceConfigDataManager;
import com.elitecore.netvertexsm.datamanager.wsconfig.data.WSConfigData;

public class WebServiceConfigBLManager extends BaseBLManager {
	
	private static final String MODULE = "WebServiceConfigBLManager";

	
	public WebServiceConfigDataManager getWebServiceConfigDataManager(IDataManagerSession session) { 
		WebServiceConfigDataManager wsconfigDataManager = (WebServiceConfigDataManager) DataManagerFactory.getInstance().getDataManager(WebServiceConfigDataManager.class, session);
		return wsconfigDataManager;
	}
	public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
        return systemAuditDataManager; 
    }
	
	public WSConfigData getSubscriberConfiguration() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		WebServiceConfigDataManager wsconfigDataManager = getWebServiceConfigDataManager(session);
		WSConfigData data=null;
		try{
			if(wsconfigDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

			data = wsconfigDataManager.getSubscriberConfiguration();
		}finally{
			session.close();
		}
		return data;

	}
	
	public void updateSubscriberConfiguration(WSConfigData subscriberDBConfigData,String actionAlias, IStaffData staffData) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		WebServiceConfigDataManager wsconfigDataManager = getWebServiceConfigDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		if(wsconfigDataManager == null || systemAuditDataManager == null )
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
			
		try{
			session.beginTransaction();	

			wsconfigDataManager.updateSubscriberConfiguration(subscriberDBConfigData);
			
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);

			session.commit();
		}
		catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }
	}
}