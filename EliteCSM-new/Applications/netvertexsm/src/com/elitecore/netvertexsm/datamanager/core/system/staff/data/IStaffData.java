package com.elitecore.netvertexsm.datamanager.core.system.staff.data;

import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public interface IStaffData {
	
	public String getCommonStatusId();
	public void setCommonStatusId(String commonStatusId);
	public java.sql.Timestamp getCreateDate();
	public void setCreateDate(java.sql.Timestamp createDate);
	public String getCreatedByStaffId();
	public void setCreatedByStaffId(String createdByStaffId);
	public String getEmailAddress();
	public void setEmailAddress(String emailAddress);
	public java.sql.Timestamp getLastLoginTime();
	public void setLastLoginTime(java.sql.Timestamp lastLoginTime);
	public String getLastModifiedByStaffId();
	public void setLastModifiedByStaffId(String lastModifiedByStaffId);
	public java.sql.Timestamp getLastModifiedDate();
	public void setLastModifiedDate(java.sql.Timestamp lastModifiedDate);
	public List getStaffDetail();
	public void setStaffDetail(List lstStaffDetail);
	public String getMobile();
	public void setMobile(String mobile);
	public String getName();
	public void setName(String name);
	public long getPageNumber();
	public void setPageNumber(long pageNumber);
	public String getPassword();
	public void setPassword(String password);
	public String getPhone();
	public void setPhone(String phone);
	public long getStaffId();
	public void setStaffId(long staffId);
	public java.sql.Timestamp getStatusChangeDate();
	public void setStatusChangeDate(java.sql.Timestamp statusChangeDate);
	public String getSystemGenerated();
	public void setSystemGenerated(String systemGenerated);
	public long getTotalPages();
	public void setTotalPages(long totalPages);
	public long getTotalRecords();
	public void setTotalRecords(long totalRecords);
	public String getUserName();
	public void setUserName(String userName);
	public String getReason();
	public void setReason(String reason);
	public String getStatus();
	public void setStatus(String status);
	/*public Set getStaffRoleRel();
	public void setStaffRoleRel(Set staffGroupRel);*/
	public Long getLastLoginDuration();
	public void setLastLoginDuration(Long loginDuration);
	public Timestamp getPasswordChangeDate();
	public void setPasswordChangeDate(Timestamp passwordChangeDate);
	public Long getPasswordValidityPeriod();
	public void setPasswordValidityPeriod(Long passwordValidityPeriod);
	public String toString();
	public byte[] getProfilePicture();
	public void setProfilePicture(byte[] profilePicture);
	 public List<StaffGroupRoleRelData> getStaffGroupRoleRelationList();
	 
	 public void addRolesWithGroup(RoleData roleData,GroupData groupData);
	 public void setStaffGroupRoleRelationList(List<StaffGroupRoleRelData> staffGroupRoleRelationSet);
	 public Set<GroupData> getGroupDatas();
	 public void setGroupDatas(Set<GroupData> groupDatas);
	 public void setRecentPasswords(String recentPasswords);
	 public String getRecentPasswords();


}

