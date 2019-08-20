package com.elitecore.netvertexsm.web.core.system.staff.forms;

import java.sql.Timestamp;
import java.util.List;

import org.apache.struts.upload.FormFile;

import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;


public class CreateStaffForm extends BaseWebForm{
	private String staffId;
	private String name;
	private String userName;
	private String createDate;
	private String password;
	private String newPassword;
    private Timestamp lastModifiedDate;
	private String emailAddress;
	private String phone;
	private Timestamp lastUpdated;
	private String mobile;
	private Timestamp statusChangeDate;
	private String commonStatusId;
	private String createdByStaffId;
	private String lastModifiedByStaffId;
	private String systemGenerated;
	private String action;
	private String status;
	private String available;
	private String selected;
	private String profilePicture;
	private FormFile profilePictureFile;
	private String groupDataIds;
	private List<GroupData> groupDatas;
		
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getCommonStatusId() {
		return commonStatusId;
	}
	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}
	public void setLastModifiedByStaffId(String lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public Timestamp getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Timestamp lastUpdated) {
		this.lastUpdated = lastUpdated;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
	public Timestamp getStatusChangeDate() {
		return statusChangeDate;
	}
	public void setStatusChangeDate(Timestamp statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAvailable() {
		return available;
	}
	public void setAvailable(String available) {
		this.available = available;
	}
	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
	public FormFile getProfilePictureFile() {
		return profilePictureFile;
	}
	public void setProfilePictureFile(FormFile profilePictureFile) {
		this.profilePictureFile = profilePictureFile;
	}
	public String getProfilePicture() {
		return profilePicture;
	}
	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}
	public String getGroupDataIds() {
		return groupDataIds;
	}
	public void setGroupDataIds(String groupDataIds) {
		this.groupDataIds = groupDataIds;
	}
	public List<GroupData> getGroupDatas() {
		return groupDatas;
	}
	public void setGroupDatas(List<GroupData> groupDatas) {
		this.groupDatas = groupDatas;
	}
	
	
}
