package com.elitecore.elitesm.datamanager.radius.policies.accesspolicy;

import java.sql.Timestamp;
import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.IAccessPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.IWeekDayData;

public interface AccessPolicyDataManager extends DataManager{
	 /**
     * This method returns all the available RadiusPolicies.
     * @return
     */
    public List getList() throws DataManagerException;
    
    /**
     * This method returns all the RadiusPolicies matching the given criteria.
     * All the not null fields will be compared for equality.
     * @param radiusPolicyData
     * @return
     */
    public List getList(IAccessPolicyData accessPolicyData) throws DataManagerException;
    
    /**
     * This method returns all the radius policies matching the give criteria.
     * All the not null @see java.lang.String fields will be compared with
     * contains operator.
     * 
     * @param radiusPolicyData
     * @return
     */
    public PageList search(IAccessPolicyData accessPolicyData, int pageNo, int pageSize) throws DataManagerException;
    
    /**
     * This method updates the Status of Radius Policy.
     * Object of Radius Policy Id must be supplied to it along with required Status.
     * @param radiusPolicyData
     * @return
     */
    public List updateStatus(String accessPolicyId,String commonStatus,Timestamp statusChangeDate) throws DataManagerException;
    
    public List getWeekDay(IWeekDayData weekDayData) throws DataManagerException;
    
    public List getStartWeekDayList() throws DataManagerException;
    
    public boolean verifyAccessPolicyName(String policyName) throws DataManagerException;

    public boolean verifyAccessPolicyName(String accessPolicyId, String policyName) throws DataManagerException;

	public IAccessPolicyData getAccessPolicyById(String accessPolicyId) throws DataManagerException;

	public IAccessPolicyData getAccessPolicyByName(String accessPolicyName) throws DataManagerException;

	@Override
	public String create(Object object) throws DataManagerException;
	
	public void updateAccessPolicyById(IAccessPolicyData accessPolicy, String accessPolicyId) throws DataManagerException;

	public void updateAccessPolicyByName(IAccessPolicyData accessPolicy, String accessPolicyName) throws DataManagerException;

	public String deleteAccessPolicyById(String accessPolicyId) throws DataManagerException;
	
	public String deleteAccessPolicyByName(String accessPolicyName) throws DataManagerException;
	
}
