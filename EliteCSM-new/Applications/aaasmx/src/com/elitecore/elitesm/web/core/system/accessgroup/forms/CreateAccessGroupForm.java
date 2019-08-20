package com.elitecore.elitesm.web.core.system.accessgroup.forms;

import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;


public class CreateAccessGroupForm extends BaseWebForm{
	private String name;
	private String status;
	private List listBusinessModel;
	private String chkID;
	private String c_strCheckedIDs;
	private Long IDLength;
	private String businessModelId;
	private String action;
	private String accessGroupName;
	private String description;
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getBusinessModelId() {
		return businessModelId;
	}
	public void setBusinessModelId(String businessModelId) {
		this.businessModelId = businessModelId;
	}
	public String getC_strCheckedIDs() {
		return c_strCheckedIDs;
	}
	public void setC_strCheckedIDs(String checkedIDs) {
		c_strCheckedIDs = checkedIDs;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAccessGroupName() {
		return accessGroupName;
	}
	public void setAccessGroupName(String accessGroupName) {
		this.accessGroupName = accessGroupName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
