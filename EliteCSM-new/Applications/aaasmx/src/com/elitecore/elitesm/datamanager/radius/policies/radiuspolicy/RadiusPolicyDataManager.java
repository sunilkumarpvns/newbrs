package com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy;

import java.sql.Timestamp;
import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.IRadiusPolicyData;

public interface RadiusPolicyDataManager extends DataManager {

    /**
     * This method returns all the radius policies matching the give criteria.
     * All the not null @see java.lang.String fields will be compared with
     * contains operator.
     * 
     * @param radiusPolicyData
     * @return
     */
    public PageList search(IRadiusPolicyData radiusPolicyData, int pageNo, int pageSize) throws DataManagerException;
    

    /**
     * This method creates a new Radius Policy from the RadiusPolicyData supplied to it.
     * Object of IRadiusPolicyData must be supplied to it.
     * @param radiusPolicyData
     * @return
     */
    @Override
    String create(Object object) throws DataManagerException;
    /**
     * This method updates the Status of Radius Policy.
     * Object of Radius Policy Id must be supplied to it along with required Status.
     * @param radiusPolicyData
     * @return
     */
    public String updateStatus(String radiusPolicyId,String commonStatus, Timestamp statusChangeDate) throws DataManagerException;
    
    /**
     * This method updates Basic Details of Radius Policy.
     * IRadiusPolicyData object must be supplied with correct Basic Details.
     * @param radiusPolicyData
     * @return
     */
    public void updateByName(IRadiusPolicyData radiusPolicyData, IStaffData staffData,String name) throws DataManagerException;
    public void updateById(IRadiusPolicyData radiusPolicyData, IStaffData staffData, String radiusPolicyId) throws DataManagerException;

    /**
     * This method updates Basic Details of Radius Policy.
     * IRadiusPolicyData object must be supplied with correct Basic Details.
     * @param radiusPolicyData
     * @return
     */
    public void updateRadiusPolicyParamByItems(List lstRadiusPolicyParamDetails, String radiusPolicyId, String parameterUsage) throws DataManagerException;




    /**
     * This method deletes Radius Policy and all its Detail Entries.
     * Radius Policy Id must be supplied to it.
     * @param String
     * @return
     */
    public String deleteById(String radiusPolicyId) throws DataManagerException;
    public String deleteByName(String radiusPolicyName) throws DataManagerException;

    /**
     * This method Duplicate Radius Policy Name while Updating Radius Policy basic details.
     * Radius Policy ID and Radius Policy Name must be supplied to it.
     * @param String
     * @param String      
     * @return
     */
    public boolean verifyRadiusPolicyName(String radiusPolicyId, String policyName) throws DataManagerException;

    /**
     * This method updates the check item expression.
     * Radius Policy ID and CheckItem Expression must be supplied to it.
     * @param String
     * @param String      
     * @return
     */
    public void updateCheckItem (String radiusPolicyId, String checkItem) throws DataManagerException;
    /**
     * This method updates the reject item expression.
     * Radius Policy ID and RejectItem Expression must be supplied to it.
     * @param String
     * @param String      
     * @return
     */
    public void updateRejectItem (String radiusPolicyId, String rejectItem) throws DataManagerException;
    /**
     * This method updates the reply item expression.
     * Radius Policy ID and ReplyItem Expression must be supplied to it.
     * @param String
     * @param String      
     * @return
     */
    public void updateReplyItem (String radiusPolicyId, String replyItem) throws DataManagerException;
    
    public IRadiusPolicyData getRadiusPolicyDataByName(String radiusPolicyName) throws DataManagerException;

	public IRadiusPolicyData getRadiusPolicyDataById(String searchVal) throws DataManagerException;
    
}
