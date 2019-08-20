package com.elitecore.nvsmx.commons.model.login;

import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.pkg.systemparameter.PasswordPolicyConfigData;

import java.util.ArrayList;
import java.util.List;

/**
 * This will be used to manage change password bean
 * Created by dhyani on 22/12/16.
 */
public class ChangePasswordData {

    public String oldPassword;
    public String newPassword;
    public String userId;
    public List<StaffData> staffList;
    public PasswordPolicyConfigData passwordPolicyConfigData;

    public ChangePasswordData() {
        staffList = new ArrayList<StaffData>();
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<StaffData> getStaffList() {
        return staffList;
    }

    public void setStaffList(List<StaffData> staffList) {
        this.staffList = staffList;
    }

    public PasswordPolicyConfigData getPasswordPolicyConfigData() {
        return passwordPolicyConfigData;
    }

    public void setPasswordPolicyConfigData(PasswordPolicyConfigData passwordPolicyConfigData) {
        this.passwordPolicyConfigData = passwordPolicyConfigData;
    }
}
