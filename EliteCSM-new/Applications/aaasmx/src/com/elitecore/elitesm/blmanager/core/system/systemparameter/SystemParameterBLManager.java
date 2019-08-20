package com.elitecore.elitesm.blmanager.core.system.systemparameter;

import java.util.List;

import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.SystemParameterDataManager;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.ISystemParameterData;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.SystemParameterData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;

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
	 * @param staffData 
     * @param systemParameterData
	 * @ Update SystemParameter
	 * @throws DataManagerException
	 */
    public void updateBasicDetail(List lstValueData, IStaffData staffData) throws DataManagerException {
    	
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	SystemParameterDataManager systemParameterDataManager = getSystemParameterDataManager(session);
    	
    	if (systemParameterDataManager == null)
    		throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
    	
    	try{
    		session.beginTransaction();
    		List<ISystemParameterData> systemParameterList = getList();
    		systemParameterDataManager.updateBasicDetail(systemParameterList, lstValueData, staffData);
    		
    		session.commit();
    	}catch(DataManagerException exp){
    		exp.printStackTrace();
    		session.rollback();
    		throw new DataManagerException("Action Failed : "+exp.getMessage());
    	} finally {
    		closeSession(session);
    	}
    }
  
    /**
	 * 
	 * @param systemParameterData
	 * @return ParameterList
	 * @throws DataManagerException
	 */
   public List<ISystemParameterData> getList() throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	SystemParameterDataManager systemParameterDataManager = getSystemParameterDataManager(session);
    	
    	try {
    		List<ISystemParameterData> lstParameterList;
        	if(systemParameterDataManager == null) {
        		throw new DataManagerException("Data Manager Implementation not found for "+ getClass().getName());
        	}
        	
        	lstParameterList = systemParameterDataManager.getList();
        	
        	for(int i=0;i<lstParameterList.size();i++){
        		System.out.println("In data Manager :"+lstParameterList.get(i).getParameterId());
        	}
        	
        	return lstParameterList;
    	}catch(DataManagerException exp){
    		if(ConfigManager.isInitCompleted()){
    			Logger.logError(MODULE,  "Failed to retrive System parameter list");
    			throw exp;
    		}else{
    			throw exp;
    		}
    	} finally {
    		closeSession(session);
    	}
    } 
    
    public List<SystemParameterData> getParameterList() throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	SystemParameterDataManager systemParameterDataManager = getSystemParameterDataManager(session);
    	SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
    	
    	List<SystemParameterData> lstParameterList;
    	if(systemParameterDataManager == null || systemAuditDataManager == null) 
    		throw new DataManagerException("Data Manager Implementation not found for "+ getClass().getName());
    	
    	try{
    	
    	session.beginTransaction();	
    	
    	lstParameterList = systemParameterDataManager.getList();
    	
    	session.commit();
    	}
    	catch(Exception e){
        	e.printStackTrace();
        	session.rollback();
        	
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	closeSession(session);
        }
    	return lstParameterList;
    } 
    
    public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
        return systemAuditDataManager; 
    }
    
    public void updateCaseSensitivity(String policyCaseSensitivity, String subscriberCaseSensitivity, IStaffData staffData) throws DataManagerException {

    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	SystemParameterDataManager systemParameterDataManager = getSystemParameterDataManager(session);

    	if(systemParameterDataManager == null) {
    		throw new DataManagerException("Data Manager Implementation not found for "+ getClass().getName());
    	}

    	try{
    		session.beginTransaction();	

    		systemParameterDataManager.updateCaseSesitivity(policyCaseSensitivity,subscriberCaseSensitivity);

    		commit(session);
    	}catch (DataManagerException de) {
    		rollbackSession(session);
    		throw de;
    	} catch (Exception e) {
    		e.printStackTrace();
    		rollbackSession(session);
    		throw new DataManagerException(e.getMessage(),e);
    	} finally {
    		closeSession(session);
    	}
    }
}
