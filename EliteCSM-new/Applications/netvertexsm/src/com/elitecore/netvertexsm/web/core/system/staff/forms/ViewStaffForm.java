package com.elitecore.netvertexsm.web.core.system.staff.forms;

import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;

public class ViewStaffForm extends BaseWebForm{
	private long staffId;
	private String name;
	private String userName;
	private String emailAddress;
	private String phone;
	private String mobile;
	private String action;
	
	private static java.text.DateFormat formatter =  new java.text.SimpleDateFormat(ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT));
	
	public static java.text.DateFormat getFormatter() {
		return formatter;
	}
	public static void setFormatter(java.text.DateFormat formatter) {
		ViewStaffForm.formatter = formatter;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public long getStaffId() {
		return staffId;
	}
	public void setStaffId(long staffId) {
		this.staffId = staffId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
