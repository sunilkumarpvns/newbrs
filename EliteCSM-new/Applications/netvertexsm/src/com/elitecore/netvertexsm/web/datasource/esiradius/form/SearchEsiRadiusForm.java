package com.elitecore.netvertexsm.web.datasource.esiradius.form;

import java.util.List;

import org.apache.struts.action.ActionForm;

public class SearchEsiRadiusForm extends ActionForm {	
	private String name;
	private String status;	
	private long pageNumber;		
	private long totalPages;
	private long totalRecords;
	private List listSearchPackage;	
	private String action;
	
	private List listSearchEsiRadius;
	
	public List getListSearchEsiRadius() {
		return listSearchEsiRadius;
	}
	public void setListSearchEsiRadius(List listSearchEsiRadius) {
		this.listSearchEsiRadius = listSearchEsiRadius;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public List getListSearchPackage() {
		return listSearchPackage;
	}
	public void setListSearchPackage(List listSearchPackage) {
		this.listSearchPackage = listSearchPackage;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
}
