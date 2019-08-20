package com.elitecore.netvertexsm.datamanager.servicepolicy;


import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFPolicyCDRDriverRelData;
import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicyData;
import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicySyGatewayRelData;

public interface ServicePolicyDataManager extends DataManager {
	 public void create(PCRFServicePolicyData pcrfServicePolicyData) throws DataManagerException,DuplicateParameterFoundExcpetion;
	 public PageList search(PCRFServicePolicyData pcrfServicePolicyData, int pageNo, int pageSize) throws DataManagerException;
	 public List searchServiceServicePolicy() throws DataManagerException;
	 public void changeServicePolicyOrder(String[] order) throws DataManagerException;
	 
	 public void update(PCRFServicePolicyData pcrfServicePolicyData,long pcrfPolicyId) throws DataManagerException;
	 public void delete(List ldapSPInterfaceIds) throws DataManagerException;
	 public void updateStatus(List<String> pcrfPolicyIds,String status)throws DataManagerException;
	 
	 public PCRFServicePolicyData getPCRFServicePolicyData(PCRFServicePolicyData pcrfPolicyData) throws DataManagerException;
	 public List<PCRFServicePolicyData> getPCRFServicePolicyList() throws DataManagerException ;
	 public void update(PCRFServicePolicyData pcrfPolicyData) throws DataManagerException;
	 public List<PCRFPolicyCDRDriverRelData> getPCRFPolicyCDRDriverList(long pcrfID) throws DataManagerException;	 
	 //public void delete(PCRFServicePolicyData pcrfPolicyData)throws DataManagerException;
	 public void delete(List<Long> policyIdList,String actionAlias) throws DataManagerException;
	 public DriverInstanceData getDriverInstanceData(long driverInstanceId) throws DataManagerException;
	 public List<PCRFServicePolicySyGatewayRelData> getPCRFPolicySyGatewayList(long pcrfID) throws DataManagerException;
}
