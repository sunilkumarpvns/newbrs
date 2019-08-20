package com.elitecore.nvsmx.staff;

import com.elitecore.commons.base.Collectionz;

import java.util.List;

/**
 * Used to map Ldap Attribute with Staff Attribute
 * @author dhyani.raval
 */
public class StaffLDAPProfileFieldMapping {

    private String fieldForId;
    private String fieldForName;
    private String fieldForUserName;
    private String fieldForPassword;
    private String fieldForEmailAddress;
    private String fieldForStatus;
    private String fieldForMobileNumber;
    private String fieldForRole;
    private String fieldForGroup;
    private String fieldForProfilePicture;

    private List<String> staffAttributeList = Collectionz.newArrayList();

    public void setFieldMapping(String logicalName, String fieldName) {
        StaffFields staffFields = StaffFields.fromStaffFields(logicalName);

        if (staffFields == null) {
            return;
        }

        switch (staffFields) {
            case ID:
                this.fieldForId = fieldName;
                break;
            case NAME:
                this.fieldForName = fieldName;
                break;
            case USERNAME:
                this.fieldForUserName = fieldName;
                break;
            case PASSWORD:
                this.fieldForPassword = fieldName;
                break;
            case STATUS:
                this.fieldForStatus = fieldName;
                break;
            case EMAIL_ADDRESS:
                this.fieldForEmailAddress = fieldName;
                break;
            case MOBILE_NO:
                this.fieldForMobileNumber = fieldName;
                break;
            case GROUPS:
                this.fieldForGroup = fieldName;
                break;
            case ROLE:
                this.fieldForRole = fieldName;
                break;
            case PROFILE_PICTURE:
                this.fieldForProfilePicture = fieldName;
                break;
            default:
                break;
        }
        getStaffAttributeList().add(fieldName);

    }

    public String getFieldMappingForKey(StaffFields key) {

        switch (key) {
            case ID:
                return this.fieldForId;
            case NAME:
                return this.fieldForName;
            case USERNAME:
                return this.fieldForUserName;
            case PASSWORD:
                return this.fieldForPassword;
            case STATUS:
                return this.fieldForStatus;
            case EMAIL_ADDRESS:
                return this.fieldForEmailAddress;
            case MOBILE_NO:
                return this.fieldForMobileNumber;
            case GROUPS:
                return this.fieldForGroup;
            case ROLE:
                return this.fieldForRole;
            case PROFILE_PICTURE:
                return this.fieldForProfilePicture;
            default:
                return null;
        }

    }

    public List<String> getStaffAttributeList() {
        return staffAttributeList;
    }

    public String getFieldForId() {
        return fieldForId;
    }

    public void setFieldForId(String fieldForId) {
        this.fieldForId = fieldForId;
    }

    public String getFieldForName() {
        return fieldForName;
    }

    public void setFieldForName(String fieldForName) {
        this.fieldForName = fieldForName;
    }

    public String getFieldForUserName() {
        return fieldForUserName;
    }

    public void setFieldForUserName(String fieldForUserName) {
        this.fieldForUserName = fieldForUserName;
    }

    public String getFieldForPassword() {
        return fieldForPassword;
    }

    public void setFieldForPassword(String fieldForPassword) {
        this.fieldForPassword = fieldForPassword;
    }

    public String getFieldForEmailAddress() {
        return fieldForEmailAddress;
    }

    public void setFieldForEmailAddress(String fieldForEmailAddress) {
        this.fieldForEmailAddress = fieldForEmailAddress;
    }

    public String getFieldForStatus() {
        return fieldForStatus;
    }

    public void setFieldForStatus(String fieldForStatus) {
        this.fieldForStatus = fieldForStatus;
    }

    public String getFieldForMobileNumber() {
        return fieldForMobileNumber;
    }

    public void setFieldForMobileNumber(String fieldForMobileNumber) {
        this.fieldForMobileNumber = fieldForMobileNumber;
    }

    public String getFieldForRole() {
        return fieldForRole;
    }

    public void setFieldForRole(String fieldForRole) {
        this.fieldForRole = fieldForRole;
    }

    public String getFieldForGroup() {
        return fieldForGroup;
    }

    public void setFieldForGroup(String fieldForGroup) {
        this.fieldForGroup = fieldForGroup;
    }

    public String getFieldForProfilePicture() {
        return fieldForProfilePicture;
    }

    public void setFieldForProfilePicture(String fieldForProfilePicture) {
        this.fieldForProfilePicture = fieldForProfilePicture;
    }

    public void setStaffAttributeList(List<String> staffAttributeList) {
        this.staffAttributeList = staffAttributeList;
    }
}
