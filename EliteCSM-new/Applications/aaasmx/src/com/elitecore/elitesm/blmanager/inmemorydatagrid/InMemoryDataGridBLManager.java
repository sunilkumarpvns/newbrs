package com.elitecore.elitesm.blmanager.inmemorydatagrid;

import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.inmemorydatagrid.InMemoryDataGridDataManager;
import com.elitecore.elitesm.datamanager.inmemorydatagrid.data.InMemoryDataGridData;

public class InMemoryDataGridBLManager extends BaseBLManager{

	public InMemoryDataGridData search() throws Exception {

		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		
		InMemoryDataGridDataManager inMemoryDataGridDataManager = getInMemoryDataGridDataManager(session);
		
		if(inMemoryDataGridDataManager==null){
			throw new DataManagerException ("Data Manager implementation not found for "+getClass().getName());
		}
		
		try{
			
			InMemoryDataGridData inMemoryDataGridData = new InMemoryDataGridData();
			
			session.beginTransaction();
			
			inMemoryDataGridData=inMemoryDataGridDataManager.search();
			
			return inMemoryDataGridData;
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action failed :"+exp.getMessage(),exp);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
	}
	
	public void update(InMemoryDataGridData inMemoryDataGridData, IStaffData staffData) throws Exception{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		InMemoryDataGridDataManager inMemoryDataGridDataManager = getInMemoryDataGridDataManager(session);
		
		if(inMemoryDataGridDataManager==null){
			throw new DataManagerException ("Data Manager implementation not found for "+getClass().getName());
		}
		
		try{
			session.beginTransaction();

			inMemoryDataGridDataManager.update(inMemoryDataGridData, staffData);
		
			session.commit();
		
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action failed :"+exp.getMessage(),exp);
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			closeSession(session);
		}
	}

	public InMemoryDataGridDataManager getInMemoryDataGridDataManager(IDataManagerSession session)throws Exception{
		InMemoryDataGridDataManager dashboardDatamanager=(InMemoryDataGridDataManager)DataManagerFactory.getInstance().getDataManager(InMemoryDataGridDataManager.class, session);
		return dashboardDatamanager;
	}
	
	public InMemoryDataGridData getInMemoryDatagridConfiguration() throws Exception{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		InMemoryDataGridDataManager inMemoryDataGridDataManager = getInMemoryDataGridDataManager(session);

		InMemoryDataGridData inMemoryDataGridConfiguration = null;

		if(inMemoryDataGridDataManager==null){
			throw new DataManagerException ("Data Manager implementation not found for "+getClass().getName());
		}

		try{
			inMemoryDataGridConfiguration = inMemoryDataGridDataManager.getInMemoryDataGridConfiguration();
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			closeSession(session);
		}
		return inMemoryDataGridConfiguration;
	}
}
