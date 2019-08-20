package com.elitecore.corenetvertex.sm.audit;

import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import org.codehaus.jackson.annotate.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity(name="com.elitecore.corenetvertex.sm.audit.AuditData")
@Table(name="TBLM_AUDIT")
public class AuditData implements Serializable{
    private static final long serialVersionUID = 1L;
    private String id;
    private AuditActions actionId;
    private String resourceId;
    private String resourceClass;
    private String actualResourceId;
    private String actualResourceClass;
    private String auditableResourceName;
    private Timestamp auditDate;
    private StaffData staffData;
    private String clientIp;
    private byte[] difference;
    private String hierarchy;
    private String message;


    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "ACTION_ID")
    public AuditActions getActionId() {
        return actionId;
    }

    public void setActionId(AuditActions actionId) {
        this.actionId = actionId;
    }


    @Column(name = "AUDIT_DATE")
    public Timestamp getAuditDate() {
        return auditDate;
    }


    public void setAuditDate(Timestamp auditDate) {
        this.auditDate = auditDate;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "STAFF_ID")
    @XmlTransient
    @JsonIgnore
    public StaffData getStaffData() {
        return staffData;
    }

    public void setStaffData(StaffData staffData) {
        this.staffData = staffData;
    }

    @Column(name = "CLIENT_IP")
    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    @Column(name = "DIFFERENCE")
    public byte[] getDifference() {
        return difference;
    }

    public void setDifference(byte[] difference) {
        this.difference = difference;
    }

    @Column(name = "HIERARCHY")
    public String getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(String hierarchy) {
        this.hierarchy = hierarchy;
    }

    @Column(name = "MESSAGE")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Column(name="ACTUAL_RESOURCE_CLASS")
    public String getActualResourceClass() {
        return actualResourceClass;
    }

    public void setActualResourceClass(String actualResourceClass) {
        this.actualResourceClass = actualResourceClass;
    }

    @Column(name="AUDITABLE_RESOURCE_NAME")
    public String getAuditableResourceName() {
        return auditableResourceName;
    }

    public void setAuditableResourceName(String auditableResourceName) {
        this.auditableResourceName = auditableResourceName;
    }

    @Column(name="RESOURCE_CLASS")
    public String getResourceClass() {
        return resourceClass;
    }

    public void setResourceClass(String resourceDataClass) {
        this.resourceClass = resourceDataClass;
    }

    @Column(name="RESOURCE_ID")
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Column(name="ACTUAL_RESOURCE_ID")
    public String getActualResourceId() {
        return actualResourceId;
    }

    public void setActualResourceId(String actualResourceId) {
        this.actualResourceId = actualResourceId;
    }
}
