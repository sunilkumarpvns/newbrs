package com.elitecore.netvertexsm.web.servermgr.server.form;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class MiscUserfileDatasourceForm extends BaseWebForm{
	
	private Long netserverid;
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
	public Long getNetserverid() {
		return netserverid;
	}
	public void setNetserverid(Long netserverid) {
		this.netserverid = netserverid;
	}
}
