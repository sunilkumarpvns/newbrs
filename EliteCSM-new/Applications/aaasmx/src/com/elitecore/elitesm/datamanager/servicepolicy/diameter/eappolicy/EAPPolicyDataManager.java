package com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyPluginConfig;

public interface EAPPolicyDataManager extends DataManager {
	
	@Override
	public String create(Object object) throws DataManagerException;
	
	public PageList searchEAPPolicy(EAPPolicyData eapPolicyData,int pageNo,int pageSize) throws DataManagerException;
	
	public String deleteById(String policyIds)throws DataManagerException;
	
	public String deleteByName(String policyIds)throws DataManagerException;

	public void updateBasicDetail(EAPPolicyData policyData,IStaffData staffData,String actionAlias)throws DataManagerException;
	
	public void updateAuthenticationDetails(EAPPolicyData policyData,IStaffData staffData,String actionAlias) throws DataManagerException;
	
	public void updateAuthorizationDetails(EAPPolicyData policyData,IStaffData staffData,String actionAlias) throws DataManagerException;
	
	public void updateDriverProfiles(EAPPolicyData policyData,IStaffData staffData,String actionAlias) throws DataManagerException;
	
	public EAPPolicyData getEAPPolicyById(String eapPolicyId) throws DataManagerException;
	
	public EAPPolicyData getEAPPolicyByName(String eapPolicyName) throws DataManagerException;
	
	public void changePolicyOrder(String[] order) throws DataManagerException;
	
	public List getEAPPolicies() throws DataManagerException;
	
	/**
	 * This is used for get all EAP service policy included with Active and Inactive status .
	 * @return List of EAP policy
	 * @throws DataManagerException
	 */
	public List getEAPPolicyList() throws DataManagerException;
	
	public void updateStatus(List<String> eapPolicyIds,String status)throws DataManagerException;

	public void updateResponseAttributes(EAPPolicyData eapPolicyData,IStaffData staffData, String actionAlias)throws DataManagerException;

	public void updateRFC4372CUIDetails(EAPPolicyData eapPolicyData, IStaffData staffData, String actionAlias)throws DataManagerException;
	
	public List<EAPPolicyPluginConfig> getPolicyPluginConfigList(String policyId) throws DataManagerException;
	
	public void updateEAPServicePolicy(EAPPolicyData policyData, String policyName, IStaffData staffData) throws DataManagerException;
}
