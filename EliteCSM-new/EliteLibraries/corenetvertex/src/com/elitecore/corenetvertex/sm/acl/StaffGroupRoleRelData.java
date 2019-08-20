package com.elitecore.corenetvertex.sm.acl;

import com.elitecore.commons.base.Strings;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Entity(name = "com.elitecore.corenetvertex.sm.acl.StaffGroupRoleRelData")
@Table(name = "TBLM_STAFF_GROUP_ROLE_REL")
@XmlRootElement
public class StaffGroupRoleRelData implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String id;
    private transient StaffData staffData;
    private GroupData groupData;
    private RoleData roleData;

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    @JsonIgnore
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "STAFF_ID")
    @JsonIgnore
    public StaffData getStaffData() {
        return staffData;
    }

    public void setStaffData(StaffData staffData) {
        this.staffData = staffData;
    }


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GROUP_ID")
    @JsonIgnore
    public GroupData getGroupData() {
        return groupData;
    }

    public void setGroupData(GroupData groupData) {
        this.groupData = groupData;
    }


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ROLE_ID")
    @JsonIgnore
    public RoleData getRoleData() {
        return roleData;
    }
    public void setRoleData(RoleData roleData) {
        this.roleData = roleData;
    }


    @Transient
    public String getStaffId() {
        if(this.staffData != null){
            return getStaffData().getId();
        }
        return null;
    }

    public void setStaffId(String staffId){
        if(Strings.isNullOrBlank(staffId) == false){
            StaffData staffData = new StaffData();
            staffData.setId(staffId);
            this.staffData = staffData;

        }
    }

    @Transient
    public String getGroupId() {
        if(this.groupData!=null){
            return getGroupData().getId();
        }
        return null;
    }

    public void setGroupId(String groupId) {
        if(Strings.isNullOrBlank(groupId) == false){
            GroupData groupData = new GroupData();
            groupData.setId(groupId);
            this.groupData = groupData;
        }
    }



    @Transient
    public String getRoleId() {
          if(this.roleData != null){
              return getRoleData().getId();
          }
        return null;
    }

    public void setRoleId(String roleId) {
        if(Strings.isNullOrBlank(roleId) == false){
            RoleData roleData = new RoleData();
            roleData.setId(roleId);
            this.roleData = roleData;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StaffGroupRoleRelData)) {
            return false;
        }

        StaffGroupRoleRelData that = (StaffGroupRoleRelData) o;

        if (getStaffData() != null ? !getStaffData().equals(that.getStaffData()) : that.getStaffData() != null) {
            return false;
        }
        if (getGroupData() != null ? !getGroupData().equals(that.getGroupData()) : that.getGroupData() != null) {
            return false;
        }
        return getRoleData() != null ? getRoleData().equals(that.getRoleData()) : that.getRoleData() == null;
    }

    @Override
    public int hashCode() {
        int result = getStaffData() != null ? getStaffData().hashCode() : 0;
        result = 31 * result + (getGroupData() != null ? getGroupData().hashCode() : 0);
        result = 31 * result + (getRoleData() != null ? getRoleData().hashCode() : 0);
        return result;
    }
}