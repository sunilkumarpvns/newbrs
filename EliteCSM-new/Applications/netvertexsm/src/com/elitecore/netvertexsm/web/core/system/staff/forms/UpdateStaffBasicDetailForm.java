package com.elitecore.netvertexsm.web.core.system.staff.forms;

import org.apache.struts.upload.FormFile;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class UpdateStaffBasicDetailForm extends BaseWebForm{
	private long staffId;
	private String name;
	private String userName;
	private String phone;
	private String mobile;
	private String emailAddress;
	private String action;
	private String profilePicture;
	private FormFile profilePictureFile;
	
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
	public String getProfilePicture() {
		return profilePicture;
	}
	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}
	public FormFile getProfilePictureFile() {
		return profilePictureFile;
	}
	public void setProfilePictureFile(FormFile profilePictureFile) {
		this.profilePictureFile = profilePictureFile;
	}
}
