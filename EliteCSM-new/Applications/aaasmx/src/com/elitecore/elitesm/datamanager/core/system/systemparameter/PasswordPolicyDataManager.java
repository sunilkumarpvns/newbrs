package com.elitecore.elitesm.datamanager.core.system.systemparameter;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData;

public interface PasswordPolicyDataManager extends DataManager {
    
    public void updatePolicyDetail(PasswordPolicyConfigData oldPasswordPolicyConfigData, PasswordPolicyConfigData newPasswordPolicyConfigData, IStaffData staffData) throws DataManagerException;
    public PasswordPolicyConfigData getPasswordSelectionPolicy() throws DataManagerException;	
   
}
