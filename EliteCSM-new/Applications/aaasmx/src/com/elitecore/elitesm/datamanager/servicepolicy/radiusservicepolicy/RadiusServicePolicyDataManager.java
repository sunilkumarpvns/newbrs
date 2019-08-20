package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadServicePolicyData;

public interface RadiusServicePolicyDataManager extends DataManager{
	
	public PageList search(RadServicePolicyData radiusServicePolicyData ,int pageNo, int pageSize)throws DataManagerException;

	@Override
	public String create(Object object) throws DataManagerException;
	
	public String deleteById(String radiusServicePolicyIds)throws DataManagerException;

	public String deleteByName(String name) throws DataManagerException;

	public RadServicePolicyData getRadiusServPolicyInstDataById(String policyId)throws DataManagerException;

	public void updateRadiusServicePolicyById(RadServicePolicyData radServicePolicyData, IStaffData staffData, String actionAlias)throws DataManagerException;
	
	public void updateRadiusServicePolicyName(RadServicePolicyData policyData, String policyToUpdate, IStaffData staffData, String actionAlias) throws DataManagerException;

	public List<RadServicePolicyData> searchActiveRadiusServicePolicy()throws DataManagerException;
	
	/**
	 * This is used for get all Radius service policy included with Active and Inactive status .
	 * @return List of Radius service policy
	 * @throws DataManagerException
	 */
	public List<RadServicePolicyData> getRadiusServicePolicyList()throws DataManagerException;
	
	public RadServicePolicyData getRadiusServicePolicyDataByName(String policyName) throws DataManagerException;

	public void updateStatus(List<String> radiusPolicyIds, String status)throws DataManagerException;


}
