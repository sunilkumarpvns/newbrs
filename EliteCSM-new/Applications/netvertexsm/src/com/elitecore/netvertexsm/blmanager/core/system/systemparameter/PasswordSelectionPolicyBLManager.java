package com.elitecore.netvertexsm.blmanager.core.system.systemparameter;

import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.PasswordPolicyDataManager;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;

public class PasswordSelectionPolicyBLManager extends BaseBLManager {
	private static final String MODULE = "PASSWORD-SELECTION-POLICY";
	
	
    public PasswordPolicyDataManager getSystemParameterDataManager(IDataManagerSession session){
    	PasswordPolicyDataManager systemParameterDataManager = (PasswordPolicyDataManager)DataManagerFactory.getInstance().getDataManager(PasswordPolicyDataManager.class,session);
    	return systemParameterDataManager;
    }
   
    public void updatePolicyDetail(PasswordPolicyConfigData passwordPolicyConfigData) throws DataManagerException {
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	PasswordPolicyDataManager passwordPolicyDataManager = getSystemParameterDataManager(session);
    	
    	if (passwordPolicyDataManager == null)
    		throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
    	
    	try{
    		session.beginTransaction();
    		
    		passwordPolicyDataManager.updatePolicyDetail(passwordPolicyConfigData);
    		
    		session.commit();
    	}catch(DataManagerException exp){
    		session.rollback();
    		throw new DataManagerException("Action Failed : "+exp.getMessage());
    	}finally{
    		session.close();
    	}
    }
 
   public PasswordPolicyConfigData getPasswordSelectionPolicy() throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	PasswordPolicyDataManager passwordPolicyDataManager = getSystemParameterDataManager(session);
    	
    	if (passwordPolicyDataManager == null)
    		throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
    	try{	    	
	    	PasswordPolicyConfigData passwordPolicyConfigData = passwordPolicyDataManager.getPasswordSelectionPolicy();
	    	return passwordPolicyConfigData;
    	}catch(DataManagerException exp){
    		throw new DataManagerException("Action Failed : "+exp.getMessage());
    	}finally{
    		session.close();
    	}
    } 
   
   public PasswordPolicyConfigData viewPasswordSelectionPolicy() throws DataManagerException{
   	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
   	PasswordPolicyDataManager passwordPolicyDataManager = getSystemParameterDataManager(session);
   	
   	if (passwordPolicyDataManager == null)
   		throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
   	try{	    	
	    	PasswordPolicyConfigData passwordPolicyConfigData = passwordPolicyDataManager.viewPasswordSelectionPolicy();
	    	return passwordPolicyConfigData;
   	}catch(DataManagerException exp){
   		throw new DataManagerException("Action Failed : "+exp.getMessage());
   	}finally{
   		session.close();
   	}
   }    
}
