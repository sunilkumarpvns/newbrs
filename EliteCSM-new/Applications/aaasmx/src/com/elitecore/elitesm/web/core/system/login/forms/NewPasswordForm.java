package com.elitecore.elitesm.web.core.system.login.forms;

import com.elitecore.elitesm.web.core.system.base.forms.BaseSystemForm;

public class NewPasswordForm extends BaseSystemForm{

	private static final long serialVersionUID = 1L;
	
	private String userId;
	private String newPassword;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
