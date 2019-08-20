package com.elitecore.netvertexsm.datamanager.core.system.staff.data;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;



public class StaffData implements IStaffData{
	
		private long staffId;
		private String name;
		private String userName;
		private String reason;
		private java.sql.Timestamp createDate;
		private String password;
		private java.sql.Timestamp lastModifiedDate;
		private String emailAddress;
		private String phone;
		private java.sql.Timestamp lastLoginTime;
		private String mobile;     
		private java.sql.Timestamp statusChangeDate;
		private String commonStatusId; 
		private String createdByStaffId;   
		private String lastModifiedByStaffId; 
		private String systemGenerated;
		private List lstStaffDetail;
		private long pageNumber;
		private long totalPages;
		private long totalRecords;
		private String status;
		private Set staffRoleRel; 
		private Long lastLoginDuration;
		private java.sql.Timestamp passwordChangeDate;
		private Long passwordValidityPeriod;
		private byte[] profilePicture;
		private List<StaffGroupRoleRelData> staffGroupRoleRelationList = Collectionz.newArrayList();
		private Set<GroupData> groupDatas;
		private String recentPasswords;

	
	
	public String getCommonStatusId() {
		return commonStatusId;
	}
	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}
	public java.sql.Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(java.sql.Timestamp createDate) {
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
	public java.sql.Timestamp getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(java.sql.Timestamp lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public String getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}
	public void setLastModifiedByStaffId(String lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}
	public java.sql.Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(java.sql.Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public List getStaffDetail() {
		return lstStaffDetail;
	}
	public void setStaffDetail(List lstStaffDetail) {
		this.lstStaffDetail = lstStaffDetail;
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
	public long getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
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
	public long getStaffId() {
		return staffId;
	}
	public void setStaffId(long staffId) {
		this.staffId = staffId;
	}
	public java.sql.Timestamp getStatusChangeDate() {
		return statusChangeDate;
	}
	public void setStatusChangeDate(java.sql.Timestamp statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
	public long getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
	}
	public long getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Set getStaffRoleRel() {
		return staffRoleRel;
	}
	public void setStaffRoleRel(Set staffRoleRel) {
		this.staffRoleRel = staffRoleRel;
	}
	
	public String toString(){
		
		StringWriter out  = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
	    writer.println("------------Staff Data-----------------");
        writer.println("name          =" +name);  
        writer.println("userName      =" +userName);  
        writer.println("status        =" +name);  
        writer.println("lastLoginTime =" +lastLoginTime);  
        writer.println("statusChangeDate  =" +statusChangeDate);  
        writer.println("---------------------------------------");
		writer.close();
		return out.toString();
	}
	@Override
	public Long getLastLoginDuration() {
		return lastLoginDuration;
	}
	@Override
	public void setLastLoginDuration(Long lastLoginDuration) {
		this.lastLoginDuration = lastLoginDuration;
	}
	@Override
	public Timestamp getPasswordChangeDate() {
		return passwordChangeDate;
	}
	@Override
	public void setPasswordChangeDate(Timestamp passwordChangeDate) {
		this.passwordChangeDate = passwordChangeDate;
	}
	@Override
	public Long getPasswordValidityPeriod() {
		return passwordValidityPeriod;
	}
	@Override
	public void setPasswordValidityPeriod(Long passwordValidityPeriod) {
		this.passwordValidityPeriod=passwordValidityPeriod;
	}
	@Override
	public byte[] getProfilePicture() {
		return profilePicture;
	}
	@Override
	public void setProfilePicture(byte[] profilePicture) {
		this.profilePicture = profilePicture;
	}

	public void addRolesWithGroup(RoleData roleData,GroupData groupData) {
        StaffGroupRoleRelData relationData = new StaffGroupRoleRelData();
        relationData.setRoleData(roleData);
        relationData.setGroupData(groupData);
        relationData.setStaffData(this);
        this.getStaffGroupRoleRelationList().add(relationData);        
    }
	
    public List<StaffGroupRoleRelData> getStaffGroupRoleRelationList() {
		return staffGroupRoleRelationList;
	}
	public void setStaffGroupRoleRelationList(
			List<StaffGroupRoleRelData> staffGroupRoleRelationList) {
		this.staffGroupRoleRelationList = staffGroupRoleRelationList;
	}
	public Set<GroupData> getGroupDatas() {
	    return groupDatas;
	}
	public void setGroupDatas(Set<GroupData> groupDatas) {
	    this.groupDatas = groupDatas;
	}
	@Override
	public void setRecentPasswords(String recentPasswords) {
	    this.recentPasswords = recentPasswords;	    
	}
	
	@Override
	public String getRecentPasswords() {
	    return this.recentPasswords;
	}

	
}
	
