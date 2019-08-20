package com.elitecore.netvertexsm.web.servermgr.alert.forms;

import java.util.List;

import org.apache.struts.action.ActionForm;

public class MiscAlertListenerForm extends ActionForm{
	private static final long serialVersionUID = 1L;
	private String  name;
	private String typeId;
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	private String action;
	private List alertListenerLst;
		
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
	public List getAlertListenerLst() {
		return alertListenerLst;
	}
	public void setAlertListenerLst(List alertListenerLst) {
		this.alertListenerLst = alertListenerLst;
	} 
	
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
}
