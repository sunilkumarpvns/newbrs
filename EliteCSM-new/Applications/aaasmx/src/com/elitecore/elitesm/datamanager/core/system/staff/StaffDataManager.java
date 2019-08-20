package com.elitecore.elitesm.datamanager.core.system.staff;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffGroupRelData;
import com.elitecore.elitesm.datamanager.core.util.PageList;

public interface StaffDataManager extends DataManager{
	/**
     * This method returns all the available Staff Personnels.
     * @return List
     */
    public List getList() throws DataManagerException;
    
    /**
     * This method returns all the Staff Personnels matching the given criteria.
     * All the not null fields will be compared for equality.
     * @param  staffData
     * @return List
     */
    public List getList(IStaffData staffData) throws DataManagerException;
    

    /**
     * This method returns all the Staff personnels matching the give criteria with Paging.
     * All the not null @see java.lang.String fields will be compared with
     * contains operator.
     * 
     * @param  staffData, pageNo, pageSize
     * @return List
     */
    public PageList search(IStaffData staffData, int pageNo, int pageSize) throws DataManagerException;
   
    /** 
     * This method creates a new User.
     * Object of IStaffData must be supplied to it.
     * @param  staffData
     * @return
     */
    public void create(IStaffData staffData) throws DataManagerException;

    /**
     * This method updates the Status of User.
     * Object of Staff Id must be supplied to it along with required Status.
     * @param  userId, commonStatus
     * @return
     */
    public String updateStatus(String userId,String commonStatus,Timestamp statusChangeDate) throws DataManagerException;

    /**
     * This method changes User Password.
     * Object of Staff Id must be supplied to it along with required Status.
     * @param  userId, oldPassword, newPassword
     * @return
     */
    public void changePassword(String staffId, String oldPassword, String newPassword,Timestamp statusChangeDate) throws DataManagerException;
    
    /**
     * This method delete a User.
     * Staff Id must be supplied to it.
     * @param  staffId
     * @throws DataManagerException
     */
    public String delete(String staffId) throws DataManagerException;
    
//    public void deleteStaffGroupRel(String staffId) throws DataManagerException;
    
//    public void update(IStaffData staffData,String staffId) throws DataManagerException;
    
    /**
     * This method updateBasicDetails of a User.
     * Object of StaffData must be supplied with the statusChangeDate.
     * @param  staffData,statusChangeDate.
     * @throws DataManagerException
     */
    public void updateBasicDetail(IStaffData staffData,Timestamp statusChangeDate,String actionAlias)throws DataManagerException ;
    
    /**
     * This method updateStaffAccessGroup of a User.
     * Object of StaffData must be supplied.
     * @param staffData
     * @throws DataManagerException
     */
    public void updateStaffAccessGroup(IStaffData staffData,String actionAlias,List<StaffGroupRelData> staffGroupRelList) throws DataManagerException;
    
//    public void deleteStaffAccessGroup(IStaffData staffData) throws DataManagerException;
    /**
     * This method deleteStaffAccessGroup of a User.
     * StaffId must be supplied.
     * @param  staffId
     * @throws DataManagerException
     */
    public List<StaffGroupRelData> deleteStaffAccessGroup(String staffId) throws DataManagerException;
    
    /**
     * This method changeUserName of a User.
     * Object of StaffData must bw specified with the statusChangeDate.
     * @param staffData
     * @param statusChageDate
     * @throws DataManagerException
     */
    public void changeUserName(IStaffData staffData,Timestamp statusChageDate,String actionAlias) throws DataManagerException, DuplicateParameterFoundExcpetion;
    
    /**
     * This method returns the list of StaffGroupRelData
     * StaffId must be supplied. 
     * @param  staffId
     * @return List
     * @throws DataManagerException
     */
    public List getStaffGroupRelList(String staffId) throws DataManagerException;

    /**
     * This method returns the list of IStaffData
     * userName must be supplied. 
     * @param  userName
     * @return List
     * @throws DataManagerException
     */
    public List<IStaffData> getStaffDatas (String userName) throws DataManagerException;

	public void updateLoginInfo(IStaffData staffData)  throws DataManagerException;
	
	public void checkPermission(String userName, String actionAlias) throws ActionNotPermitedException, DataManagerException;

	public Date getLastChangedPwdDate(String userName) throws DataManagerException;

	public void doAuditingJson(String string, IStaffData staffData, String actionAlias)throws DataManagerException;

	public boolean isValidUser(String systemUserName, String password) throws DataManagerException;
	
	public IStaffData getStaffData(String staffId) throws DataManagerException;

}
