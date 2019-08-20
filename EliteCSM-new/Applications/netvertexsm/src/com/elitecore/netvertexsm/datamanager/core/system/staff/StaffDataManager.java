package com.elitecore.netvertexsm.datamanager.core.system.staff;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.elitecore.corenetvertex.sm.acl.StaffProfilePictureData;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffGroupRoleRelData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;

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
    public PageList search(IStaffData staffData, int pageNo, int pageSize,List lstHiddenUser) throws DataManagerException;
    

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
    public void updateStatus(long userId,String commonStatus,Timestamp statusChangeDate) throws DataManagerException;

    /**
     * This method changes User Password.
     * Object of Staff Id must be supplied to it along with required Status.
     * @param  staffId, oldPassword, newPassword
     * @return
     */
    public void changePassword(long staffId, String oldPassword, String newPassword,Timestamp statusChangeDate,String newRecentPasswords) throws DataManagerException;
    
    /**
     * This method delete a User.
     * Staff Id must be supplied to it.
     * @param  staffId
     * @throws DataManagerException
     */
    public void delete(long staffId) throws DataManagerException;
    
//    public void deleteStaffGroupRel(String staffId) throws DataManagerException;
    
//    public void update(IStaffData staffData,String staffId) throws DataManagerException;
    
    /**
     * This method updateBasicDetails of a User.
     * Object of StaffData must be supplied with the statusChangeDate.
     * @param  staffData,statusChangeDate.
     * @throws DataManagerException
     */
    public void updateBasicDetail(IStaffData staffData,Timestamp statusChangeDate)throws DataManagerException ;
    
    /**
     * This method updateStaffAccessGroup of a User.
     * Object of StaffData must be supplied.
     * @param staffData
     * @throws DataManagerException
     */
    public void updateStaffGroupRoleRelation(IStaffData staffData) throws DataManagerException;
    
//    public void deleteStaffAccessGroup(IStaffData staffData) throws DataManagerException;
    /**
     * This method deleteStaffAccessGroup of a User.
     * StaffId must be supplied.
     * @param  staffData
     * @throws DataManagerException
     */
    public void deleteStaffGroupRoleRelation(StaffData staffData) throws DataManagerException;
    
    /**
     * This method changeUserName of a User.
     * Object of StaffData must bw specified with the statusChangeDate.
     * @param staffData
     * @param statusChageDate
     * @throws DataManagerException
     */
    public void changeUserName(IStaffData staffData,Timestamp statusChageDate) throws DataManagerException, DuplicateParameterFoundExcpetion;
    
    /**
     * This method returns the list of StaffRoleRelData
     * StaffId must be supplied. 
     * @param  staffId
     * @return List
     * @throws DataManagerException
     */
   /* public List getStaffRoleRelList(long staffId) throws DataManagerException;*/

    /**
     * This method returns the list of IStaffData
     * userName must be supplied. 
     * @param  userName
     * @return List
     * @throws DataManagerException
     */
    public List<IStaffData> getStaffData (String userName) throws DataManagerException;
    
    
     /**
     * This method updates the last login detail of the logged in staff member 
     * @param staffData
     * @throws DataManagerException
     */
    public void updateLoginInfo(IStaffData staffData)  throws DataManagerException;
    
    /**
     * @author aneri.chavda
     * This method updates the last login details of the staff member at log-out time. 
     * @param staffData
     * @throws DataManagerException
     * @return 
     */
    public void updateLogoutInfo(IStaffData staffData)  throws DataManagerException;
    
    /**
     * This method returns the list of StaffGroupRoleRelData
     * StaffId must be specified. 
     * @param  staffId
     * @return List
     * @throws DataManagerException
     */
    public List<StaffGroupRoleRelData> getStaffGroupRoleRelList(long staffId) throws DataManagerException;
    
    public Date getLastChangedPwdDate(String userName) throws DataManagerException;

    void create(StaffProfilePictureData staffProfilePicture) throws DataManagerException;

    void update(StaffProfilePictureData staffProfilePicture) throws DataManagerException;

    StaffProfilePictureData getStaffProfilePicture(Long staffId) throws DataManagerException;

    public void resetPassword(long staffId, String newPassword,Timestamp statusChangeDate) throws DataManagerException;
}
