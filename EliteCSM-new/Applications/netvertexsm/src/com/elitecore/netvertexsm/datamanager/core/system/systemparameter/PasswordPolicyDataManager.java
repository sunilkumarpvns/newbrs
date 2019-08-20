package com.elitecore.netvertexsm.datamanager.core.system.systemparameter;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData;

public interface PasswordPolicyDataManager extends DataManager {
    
    public void updatePolicyDetail(PasswordPolicyConfigData passwordPolicyConfigData) throws DataManagerException;
    public PasswordPolicyConfigData getPasswordSelectionPolicy() throws DataManagerException;	
    public PasswordPolicyConfigData viewPasswordSelectionPolicy() throws DataManagerException;
   
}
