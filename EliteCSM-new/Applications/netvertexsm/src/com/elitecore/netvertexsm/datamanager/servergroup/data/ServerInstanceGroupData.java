package com.elitecore.netvertexsm.datamanager.servergroup.data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.netvertexsm.web.servergroup.form.ServerInstanceGroupRelationForm;

/**
 * Created by aditya on 11/5/16.
 */
@Entity
@Table(name="TBLM_SERVER_INSTANCE_GROUP")
public class ServerInstanceGroupData implements Serializable{

    private static final long serialVersionUID = 1L;

    private String id;
    private String Name;
    private String accessGroups;
    private Integer orderNo;
    transient private Timestamp createdDate;
    transient private StaffData createdByStaff;
    transient private Timestamp modifiedDate;
    private List<ServerInstanceGroupRelationForm> serverInstanceGroupRelationForms  = Collectionz.newArrayList();
    private Boolean sessionSynchronization = false;

    transient private StaffData modifiedByStaff;
    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "NAME")
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Column(name="ACCESS_GROUPS")
    public String getAccessGroups() {
        return accessGroups;
    }

    public void setAccessGroups(String accessGroups) {
        this.accessGroups = accessGroups;
    }

    @Column(name="CREATED_DATE", updatable = false)
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    @ManyToOne
	@JoinColumn(name = "CREATED_BY_STAFF_ID", updatable = false)
    public StaffData getCreatedByStaff() {
        return createdByStaff;
    }


    public void setCreatedByStaff(StaffData createdByStaff) {
        this.createdByStaff = createdByStaff;
    }

    @Column(name="MODIFIED_DATE")
    public Timestamp getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @ManyToOne
	@JoinColumn(name = "MODIFIED_BY_STAFF_ID")
    public StaffData getModifiedByStaff() {
        return modifiedByStaff;
    }

    public void setModifiedByStaff(StaffData modifiedByStaff) {
        this.modifiedByStaff = modifiedByStaff;
    }

    @Transient
	public List<ServerInstanceGroupRelationForm> getServerInstanceGroupRelationForms() {
		return serverInstanceGroupRelationForms;
	}

	public void setServerInstanceGroupRelationForms(
			List<ServerInstanceGroupRelationForm> serverInstanceGroupRelationForms) {
		this.serverInstanceGroupRelationForms = serverInstanceGroupRelationForms;
	}

	@Column(name="ORDER_NO")
	public Integer getOrderNo() {
		return orderNo;
	}

    @Column(name="SESSION_SYNCHRONIZATION")
    public Boolean getSessionSynchronization() {
        return sessionSynchronization;
    }

    public void setSessionSynchronization(Boolean sessionSynchronization) {
        if(sessionSynchronization == null) {
            sessionSynchronization = false;
        }
        this.sessionSynchronization = sessionSynchronization;
    }

    public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}




}
