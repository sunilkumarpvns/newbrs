package com.elitecore.netvertexsm.web.servermgr.spinterface.form;

import org.apache.struts.action.ActionForm;

public class MiscSPInterfaceForm extends ActionForm {
	public String action;
	public int totalPages;
	public int totalRecords;
	public int pageNumber;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public int getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
}
