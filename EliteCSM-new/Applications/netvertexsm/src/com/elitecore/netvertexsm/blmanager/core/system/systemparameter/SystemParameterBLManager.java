package com.elitecore.netvertexsm.blmanager.core.system.systemparameter;

import java.util.List;
import java.util.Set;

import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.SystemParameterDataManager;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.SystemParameterValuePoolData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;

public class SystemParameterBLManager extends BaseBLManager {
	private static final String MODULE = "SYSTEM PARAMETER";
	
	/**
     * @return Returns Data Manager instance for systemParameterData.
     */
    public SystemParameterDataManager getSystemParameterDataManager(IDataManagerSession session){
    	SystemParameterDataManager systemParameterDataManager = (SystemParameterDataManager)DataManagerFactory.getInstance().getDataManager(SystemParameterDataManager.class,session);
    	return systemParameterDataManager;
    }
    
    /**
	 * 
	 * @param systemParameterData
	 * @ Update SystemParameter
	 * @throws DataManagerException
	 */
    public void updateBasicDetail(List lstValueData,IStaffData staffData, String actionAlias) throws DataManagerException {
    	
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	SystemParameterDataManager systemParameterDataManager = getSystemParameterDataManager(session);
    	SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
    	
    	if (systemParameterDataManager == null || systemAuditDataManager == null)
    		throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
    	
    	try{
    		session.beginTransaction();
    		
    		systemParameterDataManager.updateBasicDetail(lstValueData);
    		String transactionId = "";
            systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias,transactionId);
    		session.commit();
    	}catch(DataManagerException exp){
    		session.rollback();
    		throw new DataManagerException("Action Failed : "+exp.getMessage());
    	}finally{
    		session.close();
    	}
    }
  
    /**
	 * 
	 * @param systemParameterData
	 * @return ParameterList
	 * @throws DataManagerException
	 */
   public List getList() throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	SystemParameterDataManager systemParameterDataManager = getSystemParameterDataManager(session);
    	
    	List lstParameterList;
    	try{
    	if(systemParameterDataManager == null) 
    		throw new DataManagerException("Data Manager Implementation not found for "+ getClass().getName());
    	   	lstParameterList = systemParameterDataManager.getList();
    	}finally{
    	 session.close();
    	}
    	return lstParameterList;
    } 
    
    public List getList(IStaffData staffData, String actionAlias) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	SystemParameterDataManager systemParameterDataManager = getSystemParameterDataManager(session);
    	
    	List lstParameterList;
    	if(systemParameterDataManager == null) 
    		throw new DataManagerException("Data Manager Implementation not found for "+ getClass().getName());
    	
    	try{
    	
    	lstParameterList = systemParameterDataManager.getList();
    	}
    	catch(Exception e){
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }
    	return lstParameterList;
    } 
    
    public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
        return systemAuditDataManager; 
    }

	public List<SystemParameterValuePoolData> getSystemParameterValuePoolForParameter(long parameterId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	SystemParameterDataManager systemParameterDataManager = getSystemParameterDataManager(session);
    	List lstParameterList;
    	if(systemParameterDataManager == null) 
    		throw new DataManagerException("Data Manager Implementation not found for "+ getClass().getName());
    	
    	try{
    		lstParameterList = systemParameterDataManager.getSystemParameterValuePoolList(parameterId);
    	}
    	catch(Exception e){
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }
    	return lstParameterList;
    	
	}
    
    /*public List getList(ISystemParameterData systemParameterData) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	SystemParameterDataManager systemParameterDataManager = getSystemParameterDataManager(session);
    	List lstSystemParameter = systemParameterDataManager.getList(systemParameterData);
    	
    	if(lstSystemParameter != null && lstSystemParameter.size() >= 1){
    		systemParameterData =(ISystemParameterData) lstSystemParameter.get(0);
    	}
    	session.close();
    	return lstSystemParameter ;
    }   
    */
    /**
	 * 
	 * @param systemParameterData
	 * @ Create SystemParameter
	 * @throws DataManagerException
	 */
	/*public void create(ISystemParameterData systemParameterData) throws DataManagerException {
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        SystemParameterDataManager systemParameterDataManager = getSystemParameterDataManager(session);
       
        if (systemParameterDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
       
        try{
        	session.beginTransaction();
        	System.out.println("herer in Before create in blmanager");
        	systemParameterDataManager.create(systemParameterData);
        	System.out.println("herer in After create in blmanager");
        	session.commit();
        	session.close();
        }catch(DataManagerException exp){
        	session.rollback();
        	session.close();
        	throw new DataManagerException("Create Action failed : "+exp.getMessage());
        }
    }*/
	
	/**
	 * 
	 * @param systemParameterData
	 * @param Search
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws DataManagerException
	 */
	/*public PageList search(ISystemParameterData systemParameterData, int pageNo, int pageSize) throws DataManagerException {
        
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        SystemParameterDataManager systemParameterDataManager = getSystemParameterDataManager(session);
        PageList lstsystemParameterList;
        
        if (systemParameterDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        lstsystemParameterList = systemParameterDataManager.search(systemParameterData, pageNo, pageSize); 
        
        session.close();
        
        return lstsystemParameterList;
        
    }*/
	
	/*public ISystemParameterData getSystemParameter(ISystemParameterData systemParameterData) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SystemParameterDataManager systemParameterDataManager = getSystemParameterDataManager(session);
		List lstSystemParameter = systemParameterDataManager.getList(systemParameterData);
		
		if(lstSystemParameter != null && lstSystemParameter.size() >= 1){
			systemParameterData =(ISystemParameterData) lstSystemParameter.get(0);
		}
		session.close();
		return systemParameterData ;
	}*/
			
	/*public void delete(List lstPrameterIds) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SystemParameterDataManager systemParameterDataManager = getSystemParameterDataManager(session);
		if(systemParameterDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		
		try{
			session.beginTransaction();
			if(lstPrameterIds != null){
				for(int i=0;i<lstPrameterIds.size();i++){
					if(lstPrameterIds.get(i) != null)
						systemParameterDataManager.delete(lstPrameterIds.get(i).toString());
				}
				session.commit();
				session.close();
			}else{
				session.rollback();
				session.close();
				throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
			}
		}catch(DataManagerException exp){
			exp.printStackTrace();
			session.rollback();
			session.close();
			throw new DataManagerException("Delete operation failed : "+exp.getMessage());
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			session.close();
			throw new DataManagerException("Delete operation failed : "+e.getMessage());
			
		}
	}*/
    
}
