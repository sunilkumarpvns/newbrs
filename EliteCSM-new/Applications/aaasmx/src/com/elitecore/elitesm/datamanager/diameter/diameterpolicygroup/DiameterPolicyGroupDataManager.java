package com.elitecore.elitesm.datamanager.diameter.diameterpolicygroup;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicygroup.data.DiameterPolicyGroup;

public interface DiameterPolicyGroupDataManager extends DataManager {
    
    public PageList search(DiameterPolicyGroup diameterPolicyData, int pageNo, int pageSize) throws DataManagerException;
    @Override
    public String create(Object object) throws DataManagerException;
    public boolean verifyDiameterPolicyGroupName(String diameterPolicyId, String policyName) throws DataManagerException;
	public DiameterPolicyGroup getDiameterPolicyGroupDataById(String diameterPolicyGroupId) throws DataManagerException;
	public DiameterPolicyGroup getDiameterPolicyGroupDataByName(String diameterPolicyGroupName) throws DataManagerException;
	public void updateDiameterPolicyGroupById(DiameterPolicyGroup diameterPolicyGroup, IStaffData staffData, String diameterPolicyGroupId) throws DataManagerException;
	public void updateDiameterPolicyGroupByName(DiameterPolicyGroup diameterPolicyGroup, IStaffData staffData, String diameterPolicyGroupName) throws DataManagerException;
	public String deleteDiameterPolicyGroupById(String diameterPolicyGroupId) throws DataManagerException;
	public String deleteDiameterPolicyGroupByName(String diameterPolicyGroupName) throws DataManagerException;
}
