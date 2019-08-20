package com.elitecore.elitesm.web.core.system.staff.forms;

import java.sql.Timestamp;
import java.text.ParseException;

import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;

public class ViewStaffForm extends BaseWebForm{
	private String staffId;
	private String name;
	private String username;
	private Timestamp birthDate;
	private String emailAddress;
	private String address1;
	private String address2;
	private String phone;
	private String mobile;
	private String action;
	
	private static java.text.DateFormat formatter =  new java.text.SimpleDateFormat(ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT));
	
	public String getStrStatusChangeDate(){
		if(birthDate != null)
			return formatter.format(birthDate);
		else 
			return "";
	}
	public void setStrStatusChangeDate(String strStatusChangeDate){
		try{
			this.birthDate=new Timestamp(formatter.parse(strStatusChangeDate).getTime());
		}catch(ParseException e){
			e.printStackTrace();
		}
	}
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
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public Timestamp getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Timestamp birthDate) {
		this.birthDate = birthDate;
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
	public String getStaffId() {
		return staffId;
	}
	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}
