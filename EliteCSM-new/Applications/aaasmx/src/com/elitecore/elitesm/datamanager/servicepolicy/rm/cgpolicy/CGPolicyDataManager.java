package com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyData;

public interface CGPolicyDataManager extends DataManager{
	@Override
	public String create(Object object) throws DataManagerException;
	public String deleteById(String policyIds)throws DataManagerException;
	public String deleteByName(String policyIds)throws DataManagerException;
	public PageList searchCGPolicy(CGPolicyData cgPolicyData,int pageNo,int pageSize) throws DataManagerException;
	public String updateById(CGPolicyData policyData,IStaffData staffData, String policyToUpdate)throws DataManagerException;
	public String updateByName(CGPolicyData policyData, IStaffData staffData, String policyToUpdate) throws DataManagerException;
	public CGPolicyData getCGPolicyDataById(String cgPolicyId) throws DataManagerException;
	public CGPolicyData getCGPolicyDataByName(String policyName) throws DataManagerException;
	public void changePolicyOrder(String[] order) throws DataManagerException;
	public List<CGPolicyData> getCGPolicies() throws DataManagerException;
	public void updateStatus(List<String> cgPolicyIds,String status)throws DataManagerException;
}
