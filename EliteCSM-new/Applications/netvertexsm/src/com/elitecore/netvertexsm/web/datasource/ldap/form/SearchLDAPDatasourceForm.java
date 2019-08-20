package com.elitecore.netvertexsm.web.datasource.ldap.form;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class SearchLDAPDatasourceForm extends BaseWebForm{
	
	private String name;
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	private String action;
	

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
