package com.elitecore.nvsmx.staff;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.sm.acl.AuthenticationMode;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.RoleData;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.sm.acl.StaffGroupRoleRelData;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.util.PasswordUtility;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;

import java.sql.Timestamp;
import java.util.List;

import static com.elitecore.commons.base.Strings.isNullOrBlank;
import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Parameters to mapped with LDAP attributes and build Staff LDAP Profile to StaffData
 * @author dhyani.raval
 */
public class StaffLDAPProfile {

    private static final String MODULE = "STAFF-LDAP-PROFILE";
    private String id;
    private String name;
    private String userName;
    private String password;
    private String emailAddress;
    private String phone;
    private String mobile;
    private String group;
    private String role;
    private String status;
    private String profilePictureId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilePictureId() {
        return profilePictureId;
    }

    public void setProfilePictureId(String profilePictureId) {
        this.profilePictureId = profilePictureId;
    }

    public static StaffData buildStaffData(StaffLDAPProfile staffLDAPProfile) throws Exception {
        StaffData staffData = new StaffData();

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Method  called buildStaffData()");
        }

        staffData.setName(staffLDAPProfile.getName());
        staffData.setUserName(staffLDAPProfile.getUserName());
        staffData.setEmailAddress(staffLDAPProfile.getEmailAddress());
        staffData.setId(staffLDAPProfile.getId());

        if(isNullOrBlank(staffLDAPProfile.getPassword()) == false) {
            try {
                staffData.setPassword(PasswordUtility.getEncryptedPassword(staffLDAPProfile.getPassword()));
            } catch (NoSuchEncryptionException | EncryptionFailedException e) {
                getLogger().error(MODULE, "Error while encrypting password. Reason: " + e.getMessage());
                getLogger().trace(MODULE, e);
                throw new Exception(e);
            }
        }


        try {
            if(staffLDAPProfile.getProfilePictureId() !=null) {
                //TODO NEED TO DISCUSS
            }

        } catch (Exception e) {
            getLogger().error(MODULE, "Error while saving profile picture. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
            throw new Exception(e);
        }

        StaffGroupRoleRelData staffGroupRoleRelData = new StaffGroupRoleRelData();
        if(isNullOrBlank(staffLDAPProfile.getGroup())) {
            //SET DEFAULT GROUP IF GROUP IS NOT MAPPED IN LDAP AUTHENTICATION
            staffLDAPProfile.setGroup(CommonConstants.DEFAULT_GROUP_ID);
        }

        staffGroupRoleRelData.setGroupData(CRUDOperationUtil.get(GroupData.class, staffLDAPProfile.getGroup()));
        if(isNullOrBlank(staffLDAPProfile.getRole())) {
            //SET DEFAULT ROLE IF ROLE IS NOT MAPPED IN LDAP AUTHENTICATION
            staffLDAPProfile.setRole(NVSMXCommonConstants.DEFAULT_ROLE_ID);
        }
        staffGroupRoleRelData.setRoleData(CRUDOperationUtil.get(RoleData.class, staffLDAPProfile.getRole()));

        staffGroupRoleRelData.setStaffData(staffData);

        List<StaffGroupRoleRelData> staffGroupRoleRelDataList = Collectionz.newArrayList();
        staffGroupRoleRelDataList.add(staffGroupRoleRelData);
        staffData.setStaffGroupRoleRelDataList(staffGroupRoleRelDataList);
        staffData.setCreatedByStaff(CRUDOperationUtil.get(StaffData.class, NVSMXCommonConstants.ADMIN_STAFF_ID)); //WILL BE ADMIN ALWAYS
        staffData.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        staffData.setAuthenticationMode(AuthenticationMode.LDAP.name());
        return staffData;
    }
}
