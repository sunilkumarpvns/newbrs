package com.elitecore.elitesm.web.radius.clientprofile.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;




public class MiscClientProfileForm extends BaseWebForm{
	
	private String action;
	private String clientProfileName;
	private long clientTypeId;
	private long vendorInstanceId;
	
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getClientProfileName() {
		return clientProfileName;
	}
	public void setClientProfileName(String clientProfileName) {
		this.clientProfileName = clientProfileName;
	}
	public long getClientTypeId() {
		return clientTypeId;
	}
	public void setClientTypeId(long clientTypeId) {
		this.clientTypeId = clientTypeId;
	}
	public long getVendorInstanceId() {
		return vendorInstanceId;
	}
	public void setVendorInstanceId(long vendorInstanceId) {
		this.vendorInstanceId = vendorInstanceId;
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
	
	
	
}
