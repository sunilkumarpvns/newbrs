package com.elitecore.elitesm.web.core.system.staff.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateStaffUserNameForm extends BaseWebForm{
	private String staffId;
	private String username;
	private String action;
	private String reason;
	private String auditUId;
	private String name;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getStaffId() {
		return staffId;
	}
	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
