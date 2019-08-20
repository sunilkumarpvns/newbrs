package com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.data.TGPPAAAPolicyData;

public interface TGPPAAAPolicyDataManager extends DataManager {
	
	public PageList searchTGPPAAAPolicy(TGPPAAAPolicyData tgppAAAPolicyData, int requiredPageNo, Integer pageSize)throws DataManagerException;

	public String deleteTGPPAAAPolicyById(String id)throws DataManagerException;
	
	public String deleteTGPPAAAPolicyByName(String name) throws DataManagerException;

	@Override
	public String create(Object object) throws DataManagerException;
	
	public TGPPAAAPolicyData getTGPPAAAPolicyData(String tgppAAAPolicyID)throws DataManagerException;
	
	public TGPPAAAPolicyData getTGPPAAAPolicyDataByName(String policyName)throws DataManagerException;

	public void updateStatus(List<String> tgppAAAPolicyIds, String status)throws DataManagerException;
	public List<TGPPAAAPolicyData> searchActiveTGPPAAAServicePolicy() throws DataManagerException;
	
	/**
	 * This is used for get all TGPP service policy included with Active and Inactive status .
	 * @return List of TGPP policy
	 * @throws DataManagerException
	 */
	public List<TGPPAAAPolicyData> getTGPPAAAServicePolicyList() throws DataManagerException;

	public String updateTgppAAAPolicyById(TGPPAAAPolicyData tgppAAAPolicyData, IStaffData staffData, boolean auditEnable) throws DataManagerException;

	public String updateTgppAAAPolicyByName(TGPPAAAPolicyData tgppAAAPolicyData, String policyToUpdate, IStaffData staffData, boolean auditEnable) throws DataManagerException;
}