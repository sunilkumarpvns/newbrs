package com.elitecore.netvertexsm.web.core.system.staff.forms;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class UpdateStaffUserNameForm extends BaseWebForm{
	private long staffId;
	private String userName;
	private String action;
	private String reason;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getuserName() {
		return userName;
	}
	public void setuserName(String userName) {
		this.userName = userName;
	}
	public long getStaffId() {
		return staffId;
	}
	public void setStaffId(long staffId) {
		this.staffId = staffId;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
}
