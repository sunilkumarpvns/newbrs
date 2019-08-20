package com.elitecore.elitesm.datamanager.servicepolicy.auth;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyAdditionalDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyBroadcastESIRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyDigestConfRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyExternalSystemRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyMainDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyRMParamsData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicySMRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicySecDriverRelData;

public interface AuthServicePoilcyDataManager extends DataManager{
	
	public void create(AuthPolicyInstData authPolicyInstData) throws DataManagerException,DuplicateInstanceNameFoundException;
	
	public void updateAuthBasicDetail(AuthPolicyInstData instData,IStaffData staffData,String actionAlias) throws DataManagerException, DuplicateInstanceNameFoundException;
	public void updateAuthenticateParams(AuthPolicyInstData instData,IStaffData staffData,String actionAlias) throws DataManagerException;
	public void updateAuthorizationParams(AuthPolicyInstData instData,IStaffData staffData,String actionAlias) throws DataManagerException;
	public void updateDriverGroup(AuthPolicyInstData instData,IStaffData staffData,String actionAlias) throws DataManagerException;
	public void updateRMCommunication(AuthPolicyInstData instData,IStaffData staffData,String actionAlias) throws DataManagerException;
	public void updateAdditionalProc(AuthPolicyInstData instData,IStaffData staffData,String actionAlias) throws DataManagerException;
	
	public  List<AuthMethodTypeData> getAuthMethodTypeList() throws DataManagerException; 
	
	public PageList search(AuthPolicyInstData authPolicyInstData ,int pageNo, int pageSize)throws DataManagerException;
	public void delete(List<String> authPolicyIds)throws DataManagerException;
	public void updateStatus(List<String> authPolicyIds,String status)throws DataManagerException;
	public List<AuthPolicyInstData> searchActiveAuthServicePolicy() throws DataManagerException;
	public List<AuthPolicyInstData> getAuthPolicyInstDataList(AuthPolicyInstData authPolicyInstData) throws DataManagerException;
	public List<AuthPolicySMRelData> getAuthPolicySMRelData(String authPolicyId)throws DataManagerException;
	public List<AuthPolicyBroadcastESIRelData> getAuthPolicyBroadcastData(String authPolicyId) throws DataManagerException;

	public List<AuthPolicyExternalSystemRelData> getExternalSystemRelList(AuthPolicyInstData authPolicyInstData, Long externalSystemTypeId) throws DataManagerException;
	public List<AuthPolicyBroadcastESIRelData> getBroadcastingESIRelList(AuthPolicyInstData authPolicyInstData, Long externalSystemTypeId) throws DataManagerException;
	
	public List<DriverInstanceData> getMainDriverList(AuthPolicyInstData authPolicyInstData) throws DataManagerException;	
	public List<AuthMethodTypeData> getSupportedAuthMethods(AuthPolicyInstData authPolicyInstData)throws DataManagerException;
	public AuthPolicyDigestConfRelData getDigestConfigRelData(AuthPolicyInstData authPolicyInstData) throws DataManagerException;

	public AuthPolicyRMParamsData getRMParamsData(AuthPolicyInstData authPolicyInstData, long externalSystemTypeId)throws DataManagerException;
	public List<AuthPolicyMainDriverRelData> getDriverListPolicyId(AuthPolicyInstData acctPolicyInstData) throws DataManagerException ;
	public List<AuthPolicySecDriverRelData> getAuthPolicySecDriverList(AuthPolicyInstData authPolicyInstData) throws DataManagerException;
	public List<AuthPolicyAdditionalDriverRelData> getAuthPolicyAdditionalDriverList(AuthPolicyInstData authPolicyInstData) throws DataManagerException ;
	public AuthPolicyInstData  getPluginListByAuthPolicyIdForAuth(AuthPolicyInstData authPolicyInstData) throws DataManagerException;
	public void updateAuthPolicyPlugins(AuthPolicyInstData authPolicyInstData,IStaffData staffData,String actionAlias) throws DataManagerException;	
}
