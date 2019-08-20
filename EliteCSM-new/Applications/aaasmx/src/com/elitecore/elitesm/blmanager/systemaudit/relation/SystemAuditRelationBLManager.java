package com.elitecore.elitesm.blmanager.systemaudit.relation;

import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.systemaudit.Data.SystemAuditRelationData;
import com.elitecore.elitesm.datamanager.systemaudit.relation.SystemAuditRelationDataManager;

/**
 * @author Tejas.P.Shah
**/

public class SystemAuditRelationBLManager extends BaseBLManager {

	public SystemAuditRelationData getSystemAuditId(String moduleName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SystemAuditRelationDataManager systemAuditDataManager=getSystemAuditRelationDataManager(session);
		
		if(systemAuditDataManager ==  null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			return systemAuditDataManager.getSystemAuditId(moduleName);
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
		
	}
	
	public void create(SystemAuditRelationData data) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SystemAuditRelationDataManager systemAuditDataManager=getSystemAuditRelationDataManager(session);
		
		try{
			session.beginTransaction();
			
			if(systemAuditDataManager ==  null) {
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
			
			systemAuditDataManager.create(data);
			commit(session);
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}
	
	
	private SystemAuditRelationDataManager getSystemAuditRelationDataManager(IDataManagerSession session) {
		SystemAuditRelationDataManager systemAuditDataManager = (SystemAuditRelationDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditRelationDataManager.class, session);
      return systemAuditDataManager;
	}
}
