package com.elitecore.elitesm.datamanager.core.system.accessgroup.data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class GroupData extends BaseData implements IGroupData,Serializable{
	private static final long serialVersionUID = 1L;
	private String groupId;
	private String name;
	private String groupMailId;
	private String description;
	private String systemGenerated;
	private Timestamp statusChangeDate;
	private String commonStatusId;
	private Timestamp createDate;
	private String createdByStaffId;
	private Timestamp lastModifiedDate;
	private String lastModifiedByStaffId;
//	private Set groupActionRel;
	private Set staffGroupRel;
	public String getCommonStatusId() {
		return commonStatusId;
	}
	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getGroupMailId() {
		return groupMailId;
	}
	public void setGroupMailId(String groupMailId) {
		this.groupMailId = groupMailId;
	}
	public String getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}
	public void setLastModifiedByStaffId(String lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Timestamp getStatusChangeDate() {
		return statusChangeDate;
	}
	public void setStatusChangeDate(Timestamp statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
/*	public Set getGroupActionRel() {
		return groupActionRel;
	}
	public void setGroupActionRel(Set groupActionRel) {
		this.groupActionRel = groupActionRel;
	}*/
	public Set getStaffGroupRel() {
		return staffGroupRel;
	}
	public void setStaffGroupRel(Set staffGroupRel) {
		this.staffGroupRel = staffGroupRel;
	}
}
