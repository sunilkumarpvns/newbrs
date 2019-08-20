package com.elitecore.elitesm.web.core.system.accessgroup.forms;

import java.sql.Timestamp;
import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ViewAccessGroupForm extends BaseWebForm{
	private String name;
	private String description;
	private String action;
	private String groupId;
	private String chkID;
	private Long IDLength;
	private String businessModelId;
	private List listBusinessModel;
	private Timestamp createDate;
	private String createdByStaffId;
	private Timestamp lastModifiedDate;
	private String lastModifiedByStaffId;
	private String c_strCheckedIDs;
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getBusinessModelId() {
		return businessModelId;
	}
	public void setBusinessModelId(String businessModelId) {
		this.businessModelId = businessModelId;
	}
	public String getChkID() {
		return chkID;
	}
	public void setChkID(String chkID) {
		this.chkID = chkID;
	}
	public Long getIDLength() {
		return IDLength;
	}
	public void setIDLength(Long length) {
		IDLength = length;
	}
	public List getListBusinessModel() {
		return listBusinessModel;
	}
	public void setListBusinessModel(List listBusinessModel) {
		this.listBusinessModel = listBusinessModel;
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
	public String getC_strCheckedIDs() {
		return c_strCheckedIDs;
	}
	public void setC_strCheckedIDs(String checkedIDs) {
		c_strCheckedIDs = checkedIDs;
	}
}
