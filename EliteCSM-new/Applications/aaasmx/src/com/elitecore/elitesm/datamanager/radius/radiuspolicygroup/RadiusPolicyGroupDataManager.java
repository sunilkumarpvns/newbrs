package com.elitecore.elitesm.datamanager.radius.radiuspolicygroup;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.radiuspolicygroup.data.RadiusPolicyGroup;

public interface RadiusPolicyGroupDataManager extends DataManager {
    
    public boolean verifyRadiusPolicyGroupName(String radiusPolicyId, String policyName) throws DataManagerException;
    public PageList search(RadiusPolicyGroup radiusPolicyGroup, int pageNo, int pageSize) throws DataManagerException;
	public RadiusPolicyGroup getRadiusPolicyGroupDataById(String radiusPolicyGroupId) throws DataManagerException;
	public RadiusPolicyGroup getRadiusPolicyGroupDataByName(String radiusPolicyGroupName) throws DataManagerException;
	@Override
	public String create(Object object) throws DataManagerException;
	public void updateRadiusPolicyGroupById(RadiusPolicyGroup radiusPolicyGroup, IStaffData staffData, String radiusPolicyGroupId) throws DataManagerException;
	public void updateRadiusPolicyGroupByName(RadiusPolicyGroup radiusPolicyGroup, IStaffData staffData, String radiusPolicyGroupName) throws DataManagerException;
	public String deleteRadiusPolicyGroupById(String radiusPolicyGroupId) throws DataManagerException;
	public String deleteRadiusPolicyGroupByName(String radiusPolicyGroupName) throws DataManagerException;
	
}
