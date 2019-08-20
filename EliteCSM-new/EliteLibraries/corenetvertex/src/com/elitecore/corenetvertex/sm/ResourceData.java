package com.elitecore.corenetvertex.sm;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.codehaus.jackson.annotate.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;


@MappedSuperclass()
public abstract class ResourceData implements Serializable{

    private String id;
    private transient Timestamp createdDate;
    private transient StaffData createdByStaff;
    private transient Timestamp modifiedDate;
    private transient StaffData modifiedByStaff;

    @SerializedName(FieldValueConstants.STATUS)private String status = PkgStatus.ACTIVE.name();
    @Transient
    @SerializedName(FieldValueConstants.GROUPS)private String groups;
    private transient String groupNames;
    private List<String> groupList;

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "CREATED_DATE", updatable = false)
    @XmlTransient
    @JsonIgnore
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    @ManyToOne
    @JoinColumn(name = "CREATED_BY", updatable = false)
    @XmlTransient
    @JsonIgnore
    public StaffData getCreatedByStaff() {
        return createdByStaff;
    }

    public void setCreatedByStaff(StaffData createdByStaff) {
        this.createdByStaff = createdByStaff;
    }

    @Column(name = "MODIFIED_DATE")
    @XmlTransient
    @JsonIgnore
    public Timestamp getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @ManyToOne
    @JoinColumn(name = "MODIFIED_BY")
    @XmlTransient
    @JsonIgnore
    public StaffData getModifiedByStaff() {
        return modifiedByStaff;
    }

    public void setModifiedByStaff(StaffData modifiedByStaff) {
        this.modifiedByStaff = modifiedByStaff;
    }

    @JsonIgnore
    @Transient
    @XmlTransient
    public String getAuditableId() {
        return id;
    }


    @Transient
    @XmlTransient
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Transient
    @XmlTransient
    @JsonIgnore
    public ResourceData getAuditableResource() {
        return this;
    }

    @Transient
    public String getGroups() {
        return groups;
    }

    public void setGroups(String groupNames) {
        this.groups = groupNames;
    }

    public void setModifiedDateAndStaff(StaffData staffData){
        setModifiedByStaff(staffData);
        setModifiedDate(new Timestamp(System.currentTimeMillis()));
    }

    public void setCreatedDateAndStaff(StaffData staffData){
        setCreatedByStaff(staffData);
        setCreatedDate(new Timestamp(System.currentTimeMillis()));
    }

    public JsonObject toJson(){
        return null;
    }


    @Transient
    @JsonProperty("groupNames")
    public String getGroupNames(){
        return groupNames;
    }

    public void setGroupNames(String groupNames){
        this.groupNames=groupNames;
    }

    @Transient
    @XmlTransient
    @JsonIgnore
    public List<String> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<String> groupList) {
        this.groupList = groupList;
    }

    @Transient
    @XmlTransient
    @JsonIgnore
    public abstract  String getResourceName();

    public void setResourceName(String name){
     //to set resource name of importing entity
    }



    @Transient
    @XmlTransient
    @JsonIgnore
    public  String getHierarchy(){ return getId() + "<br>" + getResourceName(); }

}
