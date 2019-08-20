package com.elitecore.elitesm.blmanager.core.system.databaseproperties;

import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.databaseproperties.DatabasePropertiesDataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;

public class DatabasePropertiesBLManager extends BaseBLManager {

	private DatabasePropertiesDataManager getDatabasePropertiesDataManager(IDataManagerSession session) {
		DatabasePropertiesDataManager  dataManager = (DatabasePropertiesDataManager) DataManagerFactory.getInstance().getDataManager(DatabasePropertiesDataManager.class, session);
		return dataManager;
	}
	
	public void doAudit(String jsonString , IStaffData staffData) throws DataManagerException {
		IDataManagerSession session = null;
		try { 
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			DatabasePropertiesDataManager dataManager = getDatabasePropertiesDataManager(session);

			if (dataManager == null) {
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
			session.beginTransaction();
			dataManager.doAudit(jsonString, staffData);
			commit(session);
		} catch(DataManagerException de){
			rollbackSession(session);
			throw de;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}		
	}
	
}
