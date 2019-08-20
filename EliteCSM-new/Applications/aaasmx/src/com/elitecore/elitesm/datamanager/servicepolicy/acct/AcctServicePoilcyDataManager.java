package com.elitecore.elitesm.datamanager.servicepolicy.acct;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateEntityFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.acct.data.AcctPolicyAdditionalDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.acct.data.AcctPolicyBroadcastESIRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.acct.data.AcctPolicyExternalSystemRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.acct.data.AcctPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.acct.data.AcctPolicyMainDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.acct.data.AcctPolicyRMParamsData;
import com.elitecore.elitesm.datamanager.servicepolicy.acct.data.AcctPolicySMRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyBroadcastESIRelData;

public interface AcctServicePoilcyDataManager extends DataManager{
	
	public void create(AcctPolicyInstData acctPolicyInstData) throws DataManagerException,DuplicateEntityFoundException;
	public PageList search(AcctPolicyInstData acctPolicyInstData ,int pageNo, int pageSize)throws DataManagerException;
	public void delete(List<String> authPolicyIds)throws DataManagerException;
	public void updateStatus(List<String> authPolicyIds,String status)throws DataManagerException;
	public List<AcctPolicyInstData> searchActiveAcctServicePolicy()throws DataManagerException;
	public AcctPolicyInstData getAcctPolicyData(AcctPolicyInstData acctPolicyInstData) throws DataManagerException;
	public void updateAcctServicePolicy(AcctPolicyInstData acctPolicyInstData,IStaffData staffData,String actionAlias) throws DataManagerException;
	public List<DriverInstanceData> getMainDriverList(AcctPolicyInstData acctPolicyInstData) throws DataManagerException;	
	public void updateAcctServicePolicyExternalSystemList(AcctPolicyInstData acctPolicyInstData,IStaffData staffData,String actionAlias) throws DataManagerException;
	public AcctPolicySMRelData getAcctPolicySMRelData(Long acctPolicyId) throws DataManagerException;

	public void updateAcctServicePolicyDriverGroup(AcctPolicyInstData acctPolicyInstData) throws DataManagerException;
	public void updateAcctServicePolicyProxyServerList(AcctPolicyInstData policyInstData) throws DataManagerException;
	public void updateAdditionalProc(AcctPolicyInstData policyInstData,IStaffData staffData,String actionAlias) throws DataManagerException;
	public void updateAcctAccountingMethod(AcctPolicyInstData acctPolicyInstData,IStaffData staffData,String actionAlias) throws DataManagerException;
	public List<AcctPolicyBroadcastESIRelData> getAcctPolicyBroadcastData(Long acctPolicyId) throws DataManagerException;

	public void deleteAcctServicePolicyProxyServerList(AcctPolicyInstData acctPolicyInstData) throws DataManagerException;
	public void deleteMainDriverList(AcctPolicyInstData policyInstData) throws DataManagerException;
	
	public List<AcctPolicyExternalSystemRelData> getExternalSystemRelList(AcctPolicyInstData acctPolicyInstData, long externalSystemTypeId) throws DataManagerException;
	public List<AcctPolicyBroadcastESIRelData> getBroadcastingESIRelList(AcctPolicyInstData acctPolicyInstData, long externalSystemTypeId) throws DataManagerException;
	public AcctPolicyRMParamsData getRMParamsData(AcctPolicyInstData acctPolicyInstData, long externalSystemTypeId)throws DataManagerException;
	public List<AcctPolicyMainDriverRelData> getDriverListByPolicyId(AcctPolicyInstData acctPolicyInstData) throws DataManagerException;
	public List<AcctPolicyAdditionalDriverRelData> getAdditionalDriverRelList(AcctPolicyInstData acctPolicyInstData) throws DataManagerException;
	public List<DriverInstanceData> getAdditionalDriverList(AcctPolicyInstData acctPolicyInstData) throws DataManagerException;
	
	public AcctPolicyInstData getPluginListByAcctPolicyIdForAcct(AcctPolicyInstData acctInstPolicyInstData) throws DataManagerException;
	public void updateAcctPolicyPlugins(AcctPolicyInstData acctPolicyInstData,IStaffData staffData,String actionAlias)throws DataManagerException;
	
}
