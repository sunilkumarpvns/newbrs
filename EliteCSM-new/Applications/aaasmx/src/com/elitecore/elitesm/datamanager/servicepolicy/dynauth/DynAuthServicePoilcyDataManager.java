package com.elitecore.elitesm.datamanager.servicepolicy.dynauth;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynaAuthPolicyESIRelData;

public interface DynAuthServicePoilcyDataManager extends DataManager{
	
	@Override
	public String create(Object object) throws DataManagerException;
	
	public void updateById(DynAuthPolicyInstData policyData, IStaffData staffData) throws DataManagerException;
	
	public void updateByName(DynAuthPolicyInstData policyData, IStaffData staffData, String policyName) throws DataManagerException;
	
	public PageList search(DynAuthPolicyInstData dynAuthPolicyInstData ,int pageNo, int pageSize)throws DataManagerException;
	
	public String deleteById(String dynauthPolicyIds)throws DataManagerException;
	
	public String deleteByName(String name)throws DataManagerException;
	
	public void updateStatus(List<String> dynauthPolicyIds,String status)throws DataManagerException;
	
	public List<DynAuthPolicyInstData> searchActiveDynAuthServicePolicy() throws DataManagerException;
	
	public DynAuthPolicyInstData getDynAuthPolicyInstDataById(String dynAuthPolicyId) throws DataManagerException;

	public List<DynaAuthPolicyESIRelData> getNASClients(String dynAuthPolicyId) throws DataManagerException;

	public DynAuthPolicyInstData getDynauthPolicyByName(String name) throws DataManagerException;
}
