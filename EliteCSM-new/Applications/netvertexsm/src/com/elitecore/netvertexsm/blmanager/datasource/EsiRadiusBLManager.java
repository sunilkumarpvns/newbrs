package com.elitecore.netvertexsm.blmanager.datasource;


import java.util.List;

import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.netvertexsm.datamanager.datasource.esiradius.EsiRadiusDataManager;
import com.elitecore.netvertexsm.datamanager.datasource.esiradius.data.IEsiRadiusData;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;

public class EsiRadiusBLManager extends BaseBLManager {

	private static final String MODULE = "ESI_RADIUS_BLMANAGER";


	public EsiRadiusDataManager getEsiRadiusDataManager(IDataManagerSession session) {
		EsiRadiusDataManager esiRadiusDataManager = (EsiRadiusDataManager) DataManagerFactory.getInstance().getDataManager(EsiRadiusDataManager.class, session);
		return esiRadiusDataManager; 
	}
	
	public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
		return systemAuditDataManager; 
	}
	
	public void create(IEsiRadiusData esiRadiusData, String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		EsiRadiusDataManager databaseDSDataManager = getEsiRadiusDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if (databaseDSDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			databaseDSDataManager.create(esiRadiusData);
			//	systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();
		}catch(DuplicateInstanceNameFoundException exp){
			session.rollback();
			throw new DuplicateInstanceNameFoundException("Duplicate User Name. : "+exp.getMessage());
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action failed : "+exp.getMessage());
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}
		
	}
	public List<DatabaseDSData> getDatabaseDSList() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		EsiRadiusDataManager databaseDSDataManager = getEsiRadiusDataManager(session);

		List<DatabaseDSData> lstDatabasedsList = null;

		if (databaseDSDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();

			lstDatabasedsList = databaseDSDataManager.getEsiRadiusList();
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
	
	
	public PageList search(IEsiRadiusData esiRadiusData,int pageNo, Integer pageSize, String actionAlias) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		EsiRadiusDataManager esiRadiusDataManager = getEsiRadiusDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		PageList lstEsiRadiusList;

		if (esiRadiusDataManager == null || systemAuditDataManager==null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			lstEsiRadiusList = esiRadiusDataManager.search(esiRadiusData, pageNo, pageSize);
			
			//String transactionId = "";
			//  systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias,transactionId);
			session.commit();
		}
		catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}
		return lstEsiRadiusList; 
	}
}