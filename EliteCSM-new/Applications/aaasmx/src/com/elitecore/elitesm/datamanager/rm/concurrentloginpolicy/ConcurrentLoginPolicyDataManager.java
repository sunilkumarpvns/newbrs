package com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy;
import java.sql.Timestamp;
import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.ConcurrentLoginPolicyData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.IConcurrentLoginPolicyData;
public interface ConcurrentLoginPolicyDataManager extends DataManager{
	
	/**
     * This method updates the Status of concurrent login policy.
     * Object of concurrent login Id must be supplied to it along with required Status.
     * @param concurrent Login Data
     * @return
     */
    public List updateStatus(IConcurrentLoginPolicyData concurrentLoginPolicyData,String concurrentLoginPolicyId,String commonStatus,Timestamp statusChangeDate,IStaffData staffData,String actionAlias) throws DataManagerException;
    
    public PageList searchConcurrentLoginPolicy(IConcurrentLoginPolicyData concurrentLoginPolicyData, int pageNo, int pageSize) throws DataManagerException;
    
    public void updateBasicDetail(IConcurrentLoginPolicyData concurrentLoginPolicyData, IStaffData staffData, String actionAlias)throws DataManagerException ;
    
    public void updateAttributeDetail(IConcurrentLoginPolicyData concurrentLoginPolicyData, String concurrentLoginId,IStaffData staffData,String actionAlias) throws DataManagerException;
    
	public ConcurrentLoginPolicyData getConcurrentLoginPolicyById(String concurrentLoginPolicyId) throws DataManagerException;

	public ConcurrentLoginPolicyData getConcurrentLoginPolicyByName(String concurrentLoginPolicyName) throws DataManagerException;

	@Override
	public String create(Object object) throws DataManagerException;
	
	public void updateConcurrentLoginPolicyByName(ConcurrentLoginPolicyData concurrentLoginPolicy, IStaffData staffData, String concurrentLoginPolicyName) throws DataManagerException;

	public String deleteConcurrentLoginPolicyById(String concurrentLoginPolicyId) throws DataManagerException;

	public String deleteConcurrentLoginPolicyByName(String concurrentLoginPolicyName) throws DataManagerException;

	void updateConcurrentLoginPolicyById(ConcurrentLoginPolicyData concurrentLoginPolicy, IStaffData staffData,
			String value) throws DataManagerException;
	
}
