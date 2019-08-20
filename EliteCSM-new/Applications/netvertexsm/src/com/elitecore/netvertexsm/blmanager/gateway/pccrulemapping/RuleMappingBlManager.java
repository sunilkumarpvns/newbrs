package com.elitecore.netvertexsm.blmanager.gateway.pccrulemapping;

import java.util.List;

import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping.RuleMappingDataManager;
import com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping.data.RuleMappingData;

public class RuleMappingBlManager extends BaseBLManager {

	
	public PageList search(RuleMappingData ruleMappingData, int requiredPageNo,	Integer pageSize, IStaffData staffData, String actionAlias) throws DataManagerException {
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        RuleMappingDataManager routingTableDataDataManager = getDataManager(session);
        PageList ruleMappingDataList;
        
        if(routingTableDataDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	ruleMappingDataList = routingTableDataDataManager.search(ruleMappingData, requiredPageNo, pageSize);
        }catch(Exception e){
        	throw new DataManagerException("Action failed :"+e.getMessage(),e);
        }finally{
        	session.close();
        }
        return ruleMappingDataList;
   }


	public void create(RuleMappingData ruleMappingData,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		RuleMappingDataManager routingTableDataManager = getDataManager(session);
      
        if (ruleMappingData == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	session.beginTransaction();        	
        	routingTableDataManager.create(ruleMappingData);
        	session.commit();
        }catch(DuplicateParameterFoundExcpetion exp){
        	session.rollback();
        	throw new DuplicateParameterFoundExcpetion("Duplicate Routing Id Detail. : "+exp.getMessage(),exp);
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
        	session.close();
        }
    }
	
	
	public List<RuleMappingData> getRuleMappingDataList() throws DataManagerException,DuplicateParameterFoundExcpetion{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		RuleMappingDataManager routingTableDataManager = getDataManager(session);
        List<RuleMappingData> routingTableDataDataList = null;
        if (routingTableDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	session.beginTransaction();        	
        	routingTableDataDataList = routingTableDataManager.getRoutingTableDataList();
        	return routingTableDataDataList;
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
        	session.close();
        }
	}

	
	public void delete(Long[] ruleMappingIDs, IStaffData staffData,String deleteActionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		RuleMappingDataManager routingTableDataManager = getDataManager(session);
        if (routingTableDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	session.beginTransaction();        	
        	routingTableDataManager.delete(ruleMappingIDs);
        	session.commit();
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
        	session.close();
        }
	}
	public RuleMappingData getRuleMappingData(long ruleMappingId)throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		RuleMappingDataManager routingTableDataManager = getDataManager(session);
		RuleMappingData ruleMappingData = null;
        if (routingTableDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	session.beginTransaction();        	
        	ruleMappingData =routingTableDataManager.getRuleMappingData(ruleMappingId);
        	return ruleMappingData;
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
        	session.close();
        }
	}
	
	public void update(RuleMappingData routingTableDataData, IStaffData staffData,
			String updateActionAlias)throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		RuleMappingDataManager routingTableDataManager = getDataManager(session);
        
        if (routingTableDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	session.beginTransaction();        	
        	routingTableDataManager.update(routingTableDataData);
        	session.commit();
        }catch(DuplicateParameterFoundExcpetion exp){
        	session.rollback();
        	throw new DuplicateParameterFoundExcpetion("Duplicate MCC-MNC Codes Detail. : "+exp.getMessage(),exp);
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
        	session.close();
        }
	}
		
	
		

	public void changeRoutingEntryOrder(String[] routingEntryIds,IStaffData staffData,
			String changeOrderActionAlias) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		RuleMappingDataManager routingTableDataManager = getDataManager(session);
		
		if (routingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();        	
			//routingTableDataManager.changeRoutingEntryOrder(routingEntryIds);
			session.commit();
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
		}finally{
			session.close();
		}
	}
	
		private RuleMappingDataManager getDataManager(IDataManagerSession session){
			RuleMappingDataManager routingTableDataDatamanager = (RuleMappingDataManager)DataManagerFactory.getInstance().getDataManager(RuleMappingDataManager.class, session);
            return routingTableDataDatamanager;
			
		}
	}