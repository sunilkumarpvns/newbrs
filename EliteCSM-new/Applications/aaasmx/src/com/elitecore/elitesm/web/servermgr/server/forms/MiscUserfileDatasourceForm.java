package com.elitecore.elitesm.web.servermgr.server.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class MiscUserfileDatasourceForm extends BaseWebForm{
	
	private String netserverid;
	private String action;
	private String status;
	private String selectedFileName;
	private int currentPageNo;
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	
	
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public long getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
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
	public String getSelectedFileName() {
		return selectedFileName;
	}
	public void setSelectedFileName(String selectedFileName) {
		this.selectedFileName = selectedFileName;
	}
	public int getCurrentPageNo() {
		return currentPageNo;
	}
	public void setCurrentPageNo(int currentPageNo) {
		this.currentPageNo = currentPageNo;
	}
	public String getNetserverid() {
		return netserverid;
	}
	public void setNetserverid(String netserverid) {
		this.netserverid = netserverid;
	}
}
