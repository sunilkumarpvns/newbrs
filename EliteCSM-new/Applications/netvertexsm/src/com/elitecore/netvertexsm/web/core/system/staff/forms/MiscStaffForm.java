package com.elitecore.netvertexsm.web.core.system.staff.forms;

import java.util.List;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;



public class MiscStaffForm extends BaseWebForm{
	private String staffId;
	private String userName;
	private String name;
	private String action;
	private boolean active;
	private boolean inactive;
	private boolean all;
	private long pagetNumber;
	private long multiFactor;
	private long totalPages;
	private long totalRecords;
	private String commonStatusId;
	private List listStaff;
	private String status;
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public boolean isAll() {
		return all;
	}
	public void setAll(boolean all) {
		this.all = all;
	}
	public String getCommonStatusId() {
		return commonStatusId;
	}
	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}
	public boolean isInactive() {
		return inactive;
	}
	public void setInactive(boolean inactive) {
		this.inactive = inactive;
	}
	public List getListStaff() {
		return listStaff;
	}
	public void setListStaff(List listStaff) {
		this.listStaff = listStaff;
	}
	public long getMultiFactor() {
		return multiFactor;
	}
	public void setMultiFactor(long multiFactor) {
		this.multiFactor = multiFactor;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getPagetNumber() {
		return pagetNumber;
	}
	public void setPagetNumber(long pagetNumber) {
		this.pagetNumber = pagetNumber;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public String getStaffId() {
		return staffId;
	}
	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}
}
