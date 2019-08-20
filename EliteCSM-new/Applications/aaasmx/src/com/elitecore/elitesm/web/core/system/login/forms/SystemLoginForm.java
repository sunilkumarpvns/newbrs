package com.elitecore.elitesm.web.core.system.login.forms;


import com.elitecore.elitesm.web.core.system.base.forms.BaseSystemForm;

public class SystemLoginForm extends BaseSystemForm {

	private static final long serialVersionUID = 1L;

	private String userId;
	private String systemUserName;
	private String password;
	private String captchaText;
    private String action;
    private String generateDm;
    
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	public String getCaptchaText() {
		return captchaText;
	}
	public void setCaptchaText(String captchaText) {
		this.captchaText = captchaText;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSystemUserName() {
		return systemUserName;
	}
	public void setSystemUserName(String userName) {
		this.systemUserName = userName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getGenerateDm() {
		return generateDm;
	}
	public void setGenerateDm(String generateDm) {
		this.generateDm = generateDm;
	}
}