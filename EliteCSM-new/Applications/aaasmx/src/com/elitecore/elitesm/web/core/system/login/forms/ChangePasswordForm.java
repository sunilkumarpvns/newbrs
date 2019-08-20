package com.elitecore.elitesm.web.core.system.login.forms;


import com.elitecore.elitesm.web.core.system.base.forms.BaseSystemForm;

public class ChangePasswordForm extends BaseSystemForm {

	private static final long serialVersionUID = 1L;

	private String userId;
	private String userName;
	private String password;	
	private String oldPassword;
	private String newPassword;
	private String newConfirmPassword;
	private String action;
	private Long auditUId;
	private String name;
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getNewConfirmPassword() {
		return newConfirmPassword;
	}
	public void setNewConfirmPassword(String newConfirmPassword) {
		this.newConfirmPassword = newConfirmPassword;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Long getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(Long auditUId) {
		this.auditUId = auditUId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}