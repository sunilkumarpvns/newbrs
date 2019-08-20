package com.elitecore.elitesm.web.core.system.staff.forms;

import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class SearchStaffForm extends BaseWebForm {
	
	private String staffId;
	private String name;
	private String username;
	private java.sql.Timestamp createDate;
	private java.sql.Timestamp birthDate;
	private java.sql.Timestamp lastUpdated;
	private String commonStatusId;
	private String createdByStaffId;
	private String systemGenerated;
	private String status;
	private String action;
	private long pageNumber;
	private int multiFactor;
	private long totalPages;
	private long totalRecords;
	private List listSearchStaff;
	
	public java.sql.Timestamp getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(java.sql.Timestamp birthDate) {
		this.birthDate = birthDate;
	}
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
	public java.sql.Timestamp getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(java.sql.Timestamp lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStaffId() {
		return staffId;
	}
	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getMultiFactor() {
		return multiFactor;
	}
	public void setMultiFactor(int multiFactor) {
		this.multiFactor = multiFactor;
	}
	public long getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
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
	public List getListSearchStaff() {
		return listSearchStaff;
	}
	public void setListSearchStaff(List listSearchStaff) {
		this.listSearchStaff = listSearchStaff;
	}
}
