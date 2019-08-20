package com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.radiuspolicytype;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.radiuspolicytype.data.IRadiusPolicyParamTypeData;

public interface RadiusPolicyTypeDataManager extends DataManager {
    
    /**
     * This method returns all the available RadiusPolicies.
     * @return
     */
    public List getList() throws DataManagerException;
    
    /** 
     * This method returns all the RadiusPolicies matching the given criteria.
     * All the not null fields will be compared for equality.
     * @param radiusPolicyData
     * @return
     */
    public IRadiusPolicyParamTypeData getRadiusPolicyParamType(String paramUsageType) throws DataManagerException;
    
}
