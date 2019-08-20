package com.elitecore.elitesm.web.externalsystem.forms;

import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class SearchESIInstanceForm extends BaseWebForm{
	
	
	private String name;
	private String esiTypeId;	
	private List esiTypeList;
	private String action;
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	
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
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getEsiTypeId() {
		return esiTypeId;
	}
	public void setEsiTypeId(String esiTypeId) {
		this.esiTypeId = esiTypeId;
	}	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public List getEsiTypeList() {
		return esiTypeList;
	}
	public void setEsiTypeList(List esiTypeList) {
		this.esiTypeList = esiTypeList;
	}
	
	
	

}
