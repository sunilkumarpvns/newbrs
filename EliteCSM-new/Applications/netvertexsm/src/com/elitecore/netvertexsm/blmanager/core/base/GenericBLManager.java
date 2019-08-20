package com.elitecore.netvertexsm.blmanager.core.base;

import java.util.List;

import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.GenericDataManager;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;

public class GenericBLManager extends BaseBLManager{
	private static final String MODULE = "GenericBLManager";
	
	public GenericDataManager getGenericDataManager(IDataManagerSession session) { 
		GenericDataManager genericDataManager = (GenericDataManager) DataManagerFactory 
				.getInstance().getDataManager(GenericDataManager.class, session);
		return genericDataManager;
	}
	
	public PageList getAllRecords(Class<?> instanceClass,String orderByProperty,boolean isAsc) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GenericDataManager genericDataManager = getGenericDataManager(session);
		PageList lstAllRecordsList;
		if (genericDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			lstAllRecordsList = genericDataManager.getAllRecords(instanceClass,orderByProperty,isAsc);
			session.commit();
		}
		catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}

		return lstAllRecordsList; 
	}
	
	public List<?> getAllRecords(Class<?> instanceClass, String statusProperty, String orderByProperty,boolean isAsc) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GenericDataManager genericDataManager = getGenericDataManager(session);
		List<?> lstAllRecordsList;
		
		if (genericDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			lstAllRecordsList = genericDataManager.getAllRecords(instanceClass,statusProperty,orderByProperty,isAsc);
			session.commit();
		}
		catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}

		return lstAllRecordsList; 
	}
	
	
	public void saveOrderOfData(String className, Long[] ids) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GenericDataManager genericDataManager = getGenericDataManager(session);
		if (genericDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();
			genericDataManager.saveOrderOfData(className, ids);
			session.commit();
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}	
	}
   
}
