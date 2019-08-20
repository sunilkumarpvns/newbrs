package com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAcctDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAdditionalDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAuthDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAuthMethodRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASResponseAttributes;

public interface DiameterNASPolicyDataManager extends DataManager{

	@Override
	public java.lang.String create(Object object) throws DataManagerException;
	
	String deleteById(String nasPolicyIds) throws DataManagerException;
	
	String deleteByName(String nasPolicyName) throws DataManagerException;

	void updateStatus(List<String> nasPolicyIds, String showStatusId) throws DataManagerException;

	PageList search(NASPolicyInstData nasPolicyInstData, int requiredPageNo,int pageSize) throws DataManagerException;

	List<NASPolicyInstData> searchActiveNASServicePolicy() throws DataManagerException;
	
	/**
	 * This is used for get all NAS service policy included with Active and Inactive status .
	 * @return List of NAS policy
	 * @throws DataManagerException
	 */
	List<NASPolicyInstData> getNASServicePolicyList() throws DataManagerException;
	
	NASPolicyInstData getDiameterServicePolicyByPolicyId(String servicePolicyId) throws DataManagerException;
	
	NASPolicyInstData getDiameterServicePolicyByName(String servicePolicyId) throws DataManagerException;
	
	void updateNasPolicyBasicDetails(NASPolicyInstData policyData,IStaffData staffData,String actionAlias) throws DataManagerException;
	
	List<NASPolicyAuthMethodRelData>  getDiameterAuthMethodRel(String nasPolicyId) throws DataManagerException;
	
	List<NASPolicyAuthDriverRelData>  getDiameterAuthDriverRel(String nasPolicyId) throws DataManagerException;
	
	List<NASPolicyAdditionalDriverRelData>  getDiameterAdditionalDriverRel(String nasPolicyId) throws DataManagerException;
	
	List<NASPolicyAcctDriverRelData>  getDiameterAcctDriverRel(String nasPolicyId) throws DataManagerException;
	
	void  updateAuthenticationParams(NASPolicyInstData data,IStaffData staffData,String actionAlias) throws DataManagerException;
	
	void  updateAuthorizationParams(NASPolicyInstData data,IStaffData staffData,String actionAlias) throws DataManagerException;
	
	void  updateAccountingParams(NASPolicyInstData data,IStaffData staffData,String actionAlias) throws DataManagerException;

	List<NASResponseAttributes> getResponseAttributes(String nasPolicyId) throws DataManagerException;

	void updateNasResponseAttribute(NASPolicyInstData policyInstData,IStaffData staffData, String actionAlias)throws DataManagerException;

	void updateRFC4372CUIParams(NASPolicyInstData policyData,IStaffData staffData, String actionAlias)throws DataManagerException;

	void updateByName(NASPolicyInstData policyData, String policyName, IStaffData staffData, String actionAlias) throws DataManagerException;
}
