package com.elitecore.elitesm.datamanager.core.system.staff.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.core.base.data.CommonStatusData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;

public class StaffData extends BaseData implements IStaffData,Differentiable,UserDetails{
	private String staffId;
	private String name;
	private String username;
	private String reason;
	private java.sql.Timestamp createDate;
	private String password;
	private java.sql.Timestamp birthDate;
	private java.sql.Timestamp lastModifiedDate;
	private java.sql.Timestamp lastChangePasswordDate;
	private String emailAddress;
	private String address1;              
	private String address2;              
	private String zip;                   
	private String city;                  
	private String state;                 
	private String country;
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
	private Set<StaffGroupRelData> staffGroupRel; 
	private CommonStatusData commonStatus;
	private String auditId;
	private String auditName;
	private String auditUId;
	private static java.text.DateFormat formatter = new java.text.SimpleDateFormat(ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT));
	private String historicalPassword;

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
	public java.sql.Timestamp getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(java.sql.Timestamp birthDate) {
		this.birthDate = birthDate;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCommonStatusId() {
		return commonStatusId;
	}
	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
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
	public String getStaffId() {
		return staffId;
	}
	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
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
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
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
	public Set<StaffGroupRelData> getStaffGroupRel() {
		return staffGroupRel;
	}
	public void setStaffGroupRel(Set<StaffGroupRelData> staffGroupRel) {
		this.staffGroupRel = staffGroupRel;
	}
	public CommonStatusData getCommonStatus() {
		return commonStatus;
	}
	public void setCommonStatus(CommonStatusData commonStatus) {
		this.commonStatus = commonStatus;
	}

	public String getBasicDetail() {
		return      " Name : " + name
				+ "\n Email Address : " + emailAddress
				+ "\n Address1 : " + address1 + "\n Address2 : " + (address2 != null ? address2 :"")
				+ "\n City : " + city + "\n State : " + state+ "\n Country : " + country+"\n Zip : " + zip 
				+ "\n Phone : " + ( phone != null ? phone : "") + "\n Mobile : " + (mobile != null ? mobile : "");
	}

	public String toString(){

		StringWriter out  = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------Staff Data-----------------");
		writer.println("name          =" +name);  
		writer.println("userName      =" +username);  
		writer.println("status        =" +name);  
		writer.println("lastLoginTime =" +lastLoginTime);  
		writer.println("statusChangeDate  =" +statusChangeDate);  
		writer.println("---------------------------------------");
		writer.close();
		return out.toString();
	}
	public java.sql.Timestamp getLastChangePasswordDate() {
		return lastChangePasswordDate;
	}
	public void setLastChangePasswordDate(java.sql.Timestamp lastChangePasswordDate) {
		this.lastChangePasswordDate = lastChangePasswordDate;
	}
	public String getAuditId() {
		return auditId;
	}
	public void setAuditId(String auditId) {
		this.auditId = auditId;
	}
	public String getAuditName() {
		return auditName;
	}
	public void setAuditName(String auditName) {
		this.auditName = auditName;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	@Override
	public JSONObject toJson() {
		JSONObject object=new JSONObject();
		object.put("Name", name);
		object.put("User Name", username);
		object.put("Reason", reason);
		object.put("Password", password);
		if(birthDate != null){
			object.put("Birth Date",birthDate.toString());
		}
		object.put("Email Address", emailAddress);
		object.put("Address1", address1);
		object.put("Address2", address2);
		object.put("Pin Code", zip);
		object.put("City", city);
		object.put("State", state);
		object.put("Country", country);
		object.put("Phone", phone);
		object.put("Mobile", mobile);

		object.put("Status", status);
		if(staffGroupRel != null){
			JSONArray array=new JSONArray();
			for(StaffGroupRelData element:staffGroupRel){
				array.add(element.toJson());
			}
			object.put("Assigned Access Groups", array);
		}
		if(commonStatus != null){
			object.put("Common Status", commonStatus.getShowHide());
		}

		object.put("Historical Password", historicalPassword);
		return object;
	}
	public String getHistoricalPassword() {
		return historicalPassword;
	}
	public void setHistoricalPassword(String historicalPassword) {
		this.historicalPassword = historicalPassword;
	}

	@Override
	public String getUsername() {
		return username;
	}
	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return null;
	}
	@Override
	public boolean isAccountNonExpired() {
		return false;
	}
	@Override
	public boolean isAccountNonLocked() {
		return false;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}
	@Override
	public boolean isEnabled() {
		return false;
	}
}
	
