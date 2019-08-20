package com.elitecore.elitesm.blmanager.core.system.systemparameter;

import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.PasswordPolicyDataManager;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;

public class PasswordSelectionPolicyBLManager extends BaseBLManager {
	
    public PasswordPolicyDataManager getSystemParameterDataManager(IDataManagerSession session){
    	return (PasswordPolicyDataManager)DataManagerFactory.getInstance().getDataManager(PasswordPolicyDataManager.class,session);
    }
   
    public void updatePolicyDetail(PasswordPolicyConfigData passwordPolicyConfigData, IStaffData staffData) throws DataManagerException {
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	PasswordPolicyDataManager passwordPolicyDataManager = getSystemParameterDataManager(session);
    	
    	if (passwordPolicyDataManager == null)
    		throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
    	
    	try{
    		session.beginTransaction();
    		PasswordPolicyConfigData passwordSelectionPolicy = getPasswordSelectionPolicy();
    		passwordPolicyDataManager.updatePolicyDetail(passwordSelectionPolicy,passwordPolicyConfigData, staffData);
    		
    		session.commit();
    	}catch(DataManagerException exp){
    		exp.printStackTrace();
    		session.rollback();
    		throw new DataManagerException("Action Failed : "+exp.getMessage());
    	} finally {
    		closeSession(session);
    	}
    }
 
   public PasswordPolicyConfigData getPasswordSelectionPolicy() throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	PasswordPolicyDataManager passwordPolicyDataManager = getSystemParameterDataManager(session);
    	
    	if (passwordPolicyDataManager == null)
    		throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
    	try{
	    	PasswordPolicyConfigData passwordPolicyConfigData=null;
	    	passwordPolicyConfigData = passwordPolicyDataManager.getPasswordSelectionPolicy();
	    	
	    	return passwordPolicyConfigData;
    	}catch(DataManagerException exp){
    		exp.printStackTrace();
    		session.rollback();
    		throw new DataManagerException("Action Failed : "+exp.getMessage());
    	} finally {
    		closeSession(session);
    	}
    } 
}
