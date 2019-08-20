package com.elitecore.nvsmx.staff;

import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;

/**
 * Used to display staff attributes in LDAP authentication field mapping
 * @author dhyani.raval
 */
public enum StaffFields {

    ID("Id","ID") {
        @Override
        public void setStringValue(StaffLDAPProfile staffLDAPProfile, @Nullable String val) throws OperationFailedException {
            staffLDAPProfile.setId(val);

        }

        @Override
        public String getStringValue(StaffLDAPProfile staffLDAPProfile) {
            return staffLDAPProfile.getId();
        }

    },
    NAME("Name","NAME") {
        @Override
        public void setStringValue(StaffLDAPProfile staffLDAPProfile, @Nullable String val) throws OperationFailedException {
            staffLDAPProfile.setName(val);

        }

        @Override
        public String getStringValue(StaffLDAPProfile staffLDAPProfile) {
            return staffLDAPProfile.getName();
        }

    },
    USERNAME("User Name","USER_NAME") {
        @Override
        public void setStringValue(StaffLDAPProfile staffLDAPProfile, @Nullable String val) throws OperationFailedException {
            staffLDAPProfile.setUserName(val);
        }

        @Override
        public String getStringValue(StaffLDAPProfile staffLDAPProfile) {
            return staffLDAPProfile.getUserName();
        }


    },
    PASSWORD("Password","PASSWORD") {
        @Override
        public void setStringValue(StaffLDAPProfile staffLDAPProfile, @Nullable String val) throws OperationFailedException {
            staffLDAPProfile.setPassword(val);
        }

        @Override
        public String getStringValue(StaffLDAPProfile staffLDAPProfile) {
            return staffLDAPProfile.getPassword();
        }

    },
    EMAIL_ADDRESS("Email Address","EMAIL_ADDRESS") {
        @Override
        public void setStringValue(StaffLDAPProfile staffLDAPProfile, @Nullable String val) throws OperationFailedException {
            staffLDAPProfile.setEmailAddress(val);
        }

        @Override
        public String getStringValue(StaffLDAPProfile staffLDAPProfile) {
            return staffLDAPProfile.getEmailAddress();
        }

    },
    STATUS("Status","STATUS") {
        @Override
        public void setStringValue(StaffLDAPProfile staffLDAPProfile, @Nullable String val) throws OperationFailedException {
            staffLDAPProfile.setStatus(val);
        }

        @Override
        public String getStringValue(StaffLDAPProfile staffLDAPProfile) {
            return staffLDAPProfile.getStatus();
        }

    },
    MOBILE_NO("Mobile No","MOBILE") {
        @Override
        public void setStringValue(StaffLDAPProfile staffLDAPProfile, @Nullable String val) throws OperationFailedException {
            staffLDAPProfile.setMobile(val);
        }

        @Override
        public String getStringValue(StaffLDAPProfile staffLDAPProfile) {
            return staffLDAPProfile.getMobile();
        }

    },
    PHONE("Phone","PHONE") {
        @Override
        public void setStringValue(StaffLDAPProfile staffLDAPProfile, @Nullable String val) throws OperationFailedException {
            staffLDAPProfile.setPhone(val);
        }

        @Override
        public String getStringValue(StaffLDAPProfile staffLDAPProfile) {
            return staffLDAPProfile.getPhone();
        }

    },
    GROUPS("Groups","GROUP_ID") {
        @Override
        public void setStringValue(StaffLDAPProfile staffLDAPProfile, @Nullable String val) throws OperationFailedException {
            staffLDAPProfile.setGroup(val);
        }

        @Override
        public String getStringValue(StaffLDAPProfile staffLDAPProfile) {
            return staffLDAPProfile.getGroup();
        }

    },
    ROLE("Role","ROLE_ID") {
        @Override
        public void setStringValue(StaffLDAPProfile staffLDAPProfile, @Nullable String val) throws OperationFailedException {
            staffLDAPProfile.setRole(val);
        }

        @Override
        public String getStringValue(StaffLDAPProfile staffLDAPProfile) {
            return staffLDAPProfile.getRole();
        }

    },
    PROFILE_PICTURE("Profile Picture","PROFILE_PICTURE_ID") {
        @Override
        public void setStringValue(StaffLDAPProfile staffLDAPProfile, @Nullable String val) throws OperationFailedException {
            staffLDAPProfile.setProfilePictureId(val);
        }

        @Override
        public String getStringValue(StaffLDAPProfile staffLDAPProfile) {
            return staffLDAPProfile.getProfilePictureId();
        }
    },
    ;

    private static LinkedHashMap<String, StaffFields> dataFieldMap;
    private String displayName;
    private String columnName;
    StaffFields(String displayName,String columnName) {
        this.displayName = displayName;
        this.columnName = columnName;
    }


    static {
        dataFieldMap = new LinkedHashMap<>(1, 1);
        for (StaffFields staffFields : values()) {
            dataFieldMap.put(staffFields.name(), staffFields);
        }
    }


    public String getDisplayName() {
        return displayName;
    }

    public String getColumnName() {
        return columnName;
    }

    public static StaffFields fromStaffFields(String name) {
        return dataFieldMap.get(name);
    }

    public abstract void setStringValue(StaffLDAPProfile staffLDAPProfile, @Nullable String val) throws OperationFailedException;

    public abstract String getStringValue(StaffLDAPProfile staffLDAPProfile);
}
