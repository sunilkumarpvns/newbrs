package com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CCPolicyPluginConfig;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CreditControlPolicyData;

public interface CreditControlPolicyDataManager extends DataManager{
	
	@Override
	public String create(Object object) throws DataManagerException;
	
	public CreditControlPolicyData getCcPolicyDataByPolicyId(String policyId)throws DataManagerException;
	
	public PageList searchCreditControlPolicy(CreditControlPolicyData creditControlPolicyData,int pageNo,int pageSize) throws DataManagerException;
	
	public String deleteId(String policyIds)throws DataManagerException;
	
	public String deleteByName(String policyNames)throws DataManagerException;

	public String updateById(CreditControlPolicyData policyData,IStaffData staffData)throws DataManagerException;
	
	public String updateByName(CreditControlPolicyData policyData,IStaffData staffData, String policyToUpdate)throws DataManagerException;

	public void changePolicyOrder(String[] order)throws DataManagerException;
	
	public List<CreditControlPolicyData> getCreditControlPolicies() throws DataManagerException;
	
	public CreditControlPolicyData getCcPolicyDataByPolicyName(String policyName) throws DataManagerException;
	/**
	 * This is used for get all CC service policy included with Active and Inactive status .
	 * @return List of CC policy
	 * @throws DataManagerException
	 */
	public List<CreditControlPolicyData> getCreditControlPolicyList() throws DataManagerException;
	
	public void updateStatus(List<String> creditControlPolicyIds,String status)throws DataManagerException;
	
	public List<CCPolicyPluginConfig> getPolicyPluginConfigList(String policyId) throws DataManagerException;
}