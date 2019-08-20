package com.elitecore.elitesm.blmanager.diameter.prioritytable;

import java.util.Collections;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.diameter.prioritytable.PriorityTableConfDataManager;
import com.elitecore.elitesm.datamanager.diameter.prioritytable.data.PriorityTableData;

public class PriorityTableConfBLManager extends BaseBLManager{
	
	public PriorityTableConfDataManager getPriorityTableConfDataManager(IDataManagerSession session){
		PriorityTableConfDataManager priorityTableConfDataManager = (PriorityTableConfDataManager) DataManagerFactory.getInstance().getDataManager(PriorityTableConfDataManager.class, session);
		return priorityTableConfDataManager;
	}
	
	
	public void updatePriorityTable(List<PriorityTableData> lstPriorityTableData, IStaffData staffData) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PriorityTableConfDataManager priorityTableConfDataManager = getPriorityTableConfDataManager(session);
		
		if(priorityTableConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();
			priorityTableConfDataManager.updatePriorityTable(lstPriorityTableData, staffData);
			
			commit(session);
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	
	public List<PriorityTableData> getPriorityTableList() throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PriorityTableConfDataManager priorityTableConfDataManager = getPriorityTableConfDataManager(session);
		
		if(priorityTableConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			List<PriorityTableData> priorityTables = priorityTableConfDataManager.getPriorityTableList();
			
			if(Collectionz.isNullOrEmpty(priorityTables) == false){
				return priorityTables;
			}
			
			return Collections.emptyList();
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	
}
