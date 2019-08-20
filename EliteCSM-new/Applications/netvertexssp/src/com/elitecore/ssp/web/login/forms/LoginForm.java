package com.elitecore.ssp.web.login.forms;

import com.elitecore.ssp.web.core.base.forms.BaseWebForm;

public class LoginForm extends BaseWebForm {
	
	
	private static final long serialVersionUID = 1L;
	
	private String userName;
	private String password;
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	

}
