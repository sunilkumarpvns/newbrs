package com.elitecore.corenetvertex.sm.acl;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.StaffACLChecker;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Entity(name = "com.elitecore.corenetvertex.sm.acl.StaffData")
@Table(name = "TBLM_STAFF")
public class StaffData extends ResourceData implements Serializable {


    private static final long serialVersionUID = -6245762531052746368L;
    private String name;
    private String userName;
    private transient  String password;
    private transient String confirmNewPassword;
    private String emailAddress;
    private String phone;
    private Timestamp lastLoginTime;
    private String mobile;
    private Long lastLoginDuration;
    private Timestamp passwordChangeDate;
    private Long passwordValidityPeriod;
    private Timestamp passwordExpiryDate;
    private String recentPasswords;
    private transient List<StaffGroupRoleRelData> staffGroupRoleRelDataList;

    private transient Set<GroupData> groupDatas;
    private transient Set<RoleData> roleDatas;
    private transient Map<String,RoleData> groupIdRoleDataMap;
    @Transient
    private transient StaffACLChecker aclChecker;

    private transient String profilePictureId;
    private String authenticationMode;

    public StaffData() {
        staffGroupRoleRelDataList = new ArrayList<>();
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    @Column(name = "USER_NAME")
    public String getUserName() {
        return userName;
    }


    @Column(name = "PASSWORD")
    @JsonIgnore
    public String getPassword() {
        return password;
    }


    @Column(name = "EMAIL_ADDRESS")
    public String getEmailAddress() {
        return emailAddress;
    }

    @Column(name = "PHONE")
    public String getPhone() {
        return phone;
    }

    @Column(name = "LAST_LOGIN_TIME")
    @JsonIgnore
    public Timestamp getLastLoginTime() {
        return lastLoginTime;
    }

    @Column(name = "MOBILE")
    public String getMobile() {
        return mobile;
    }


    @Column(name = "LAST_LOGIN_DURATION")
    @JsonIgnore
    public Long getLastLoginDuration() {
        return lastLoginDuration;
    }

    @Column(name = "PASSWORD_CHANGE_DATE")
    @JsonIgnore
    public Timestamp getPasswordChangeDate() {
        return passwordChangeDate;
    }

    @Column(name = "PASSWORD_VALIDITY_PERIOD")
    @JsonIgnore
    public Long getPasswordValidityPeriod() {
        return passwordValidityPeriod;
    }

    @Column(name = "RECENT_PASSWORDS")
    @JsonIgnore
    public String getRecentPasswords() {
        return recentPasswords;
    }

    @Transient
    public Timestamp getPasswordExpiryDate() {
        if (this.passwordValidityPeriod == null) {
            return null;
        }

        if (passwordChangeDate == null) {
            return new Timestamp(System.currentTimeMillis());
        }

        long passwordValidityPeriodInMs = new Timestamp(TimeUnit.DAY.toSeconds(passwordValidityPeriod) * 1000).getTime();
        passwordExpiryDate = new Timestamp(passwordChangeDate.getTime() + passwordValidityPeriodInMs);
        return this.passwordExpiryDate;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }


    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLastLoginTime(Timestamp lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setLastLoginDuration(Long lastLoginDuration) {
        this.lastLoginDuration = lastLoginDuration;
    }

    public void setPasswordChangeDate(Timestamp passwordChangeDate) {
        this.passwordChangeDate = passwordChangeDate;
    }

    public void setPasswordValidityPeriod(Long passwordValidityPeriod) {
        this.passwordValidityPeriod = passwordValidityPeriod;
    }

    public void setPasswordExpiryDate(Timestamp passwordExpiryDate) {
        //password expiry date doesn't set externally it is calculated based on validity period
    }


    @Override
    @Column(name = "STATUS")
    @XmlElement(name = "status")
    public String getStatus() {
        return super.getStatus();
    }

    @Override
    public void setStatus(String status) {
        super.setStatus(status);
    }

    public void setRecentPasswords(String recentPasswords) {
        this.recentPasswords = recentPasswords;
    }


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "staffData", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @XmlElement
    public List<StaffGroupRoleRelData> getStaffGroupRoleRelDataList() {
        return staffGroupRoleRelDataList;
    }

    public void setStaffGroupRoleRelDataList(List<StaffGroupRoleRelData> staffGroupRoleRelDataList) {
        this.staffGroupRoleRelDataList = staffGroupRoleRelDataList;
    }

    @Transient
    @JsonIgnore
    public Set<GroupData> getGroupDatas() {

        if (Collectionz.isNullOrEmpty(groupDatas)) {
            groupDatas = new HashSet<>();
            if (Collectionz.isNullOrEmpty(staffGroupRoleRelDataList) == false) {
                staffGroupRoleRelDataList.forEach(staffGroupRoleRelData -> groupDatas.add(staffGroupRoleRelData.getGroupData()));
            }
        }
        return groupDatas;
    }


    @Transient
    @JsonIgnore
    public Set<RoleData> getRoleDatas() {
        if (Collectionz.isNullOrEmpty(roleDatas)) {
            roleDatas = new HashSet<>();
            if (Collectionz.isNullOrEmpty(staffGroupRoleRelDataList) == false) {
                staffGroupRoleRelDataList.forEach(staffGroupRoleRelData -> roleDatas.add(staffGroupRoleRelData.getRoleData()));
            }
        }
        return roleDatas;
    }


    @Transient
    @JsonIgnore
    public Map<String, RoleData> getGroupIdRoleDataMap() {
        if(Maps.isNullOrEmpty(groupIdRoleDataMap)){
            groupIdRoleDataMap = new HashMap<>();
            if(Collectionz.isNullOrEmpty(staffGroupRoleRelDataList) == false){
                staffGroupRoleRelDataList.forEach(staffGroupRoleRelData -> groupIdRoleDataMap.put(staffGroupRoleRelData.getGroupData().getId(),staffGroupRoleRelData.getRoleData()));
            }
        }
        return groupIdRoleDataMap;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((userName == null) ? 0 : userName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StaffData other = (StaffData) obj;
        if (userName == null) {
            if (other.userName != null)
                return false;
        } else if (!userName.equals(other.userName))
            return false;
        return true;
    }

    @Transient
    public void setACLChecker(StaffACLChecker aclChecker) {
        this.aclChecker = aclChecker;
    }

    @Transient
    public boolean isAccessibleAction(List<String> groupIds, ACLModules requestedACLModule, ACLAction requestedACLAction) {

        if (userName != null && CommonConstants.ADMIN_USER_NAME.equalsIgnoreCase(userName) == true) {
            return true;
        }
        //check for staff role if it is Admin role or read only role
        return aclChecker.isAccessibleAction(groupIds, requestedACLModule, requestedACLAction);
    }

    public String toString() {


        ToStringBuilder toStringBuilder = new ToStringBuilder(this,
                ToStringStyle.CUSTOM_TO_STRING_STYLE).append("Name", name)
                .append("User Name", userName)
                .append("Email Address ", emailAddress);

        return toStringBuilder.toString();
    }


    @Override
    @Transient
    public String getResourceName() {
        return getUserName();
    }


    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Name", name);
        jsonObject.addProperty("User Name", userName);
        jsonObject.addProperty("Status", getStatus());
        jsonObject.addProperty("Email Address", emailAddress);
        jsonObject.addProperty("Phone No", phone);
        jsonObject.addProperty("Mobile No", mobile);

        if( staffGroupRoleRelDataList != null ) {
            JsonArray staffGroupRollRelDatJsonArray = new JsonArray();
            for(StaffGroupRoleRelData staffGroupRoleRelData : staffGroupRoleRelDataList){
                JsonObject roleGroupJsonObject = new JsonObject();
                roleGroupJsonObject.addProperty("Role",staffGroupRoleRelData.getRoleData().getName());
                roleGroupJsonObject.addProperty("Group Name",staffGroupRoleRelData.getGroupData().getName());
                staffGroupRollRelDatJsonArray.add(roleGroupJsonObject);
            }
            jsonObject.add("Role Group Relations", staffGroupRollRelDatJsonArray);
        }

        return jsonObject;
    }


    @Transient
    @XmlTransient
    @JsonIgnore
    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }


    @Transient
    @JsonIgnore
    @XmlTransient
    public boolean isActive() {
        return PkgStatus.ACTIVE.name().equals(getStatus());

    }

    @Column(name="PROFILE_PICTURE_ID")
    @JsonIgnore
    public String getProfilePictureId() {
        return profilePictureId;
    }

    public void setProfilePictureId(String profilePictureId) {
        this.profilePictureId = profilePictureId;
    }

    @Override
    @JsonIgnore
    @Transient
    public String getGroupNames() {
        return super.getGroupNames();
    }

    @Column(name = "AUTHENTICATION_MODE")
    public String getAuthenticationMode() {
        return authenticationMode;
    }

    public void setAuthenticationMode(String authenticationMode) {
        this.authenticationMode = authenticationMode;
    }
}