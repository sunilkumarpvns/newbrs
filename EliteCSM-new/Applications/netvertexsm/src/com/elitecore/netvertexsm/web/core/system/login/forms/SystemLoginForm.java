package com.elitecore.netvertexsm.web.core.system.login.forms;


import com.elitecore.netvertexsm.web.core.system.base.forms.BaseSystemForm;

public class SystemLoginForm extends BaseSystemForm {

	private static final long serialVersionUID = 1L;

	private String userId;
	private String userName;
	private String password;
	
    
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
}