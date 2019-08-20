package com.elitecore.netvertexsm.web.servermgr.alert.forms;

import java.util.List;

import org.apache.struts.action.ActionForm;

import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerTypeData;

public class SearchAlertListenerForm extends ActionForm{
	private static final long serialVersionUID = 1L;
	private String name;
	private String typeId;
	private List<AlertListenerTypeData> typeList;
	private String action;

	private List<AlertListenerData> lstAlertListener;

	private long pageNumber;
	private long totalPages;
	private long totalRecords;


	public List<AlertListenerData> getLstAlertListener() {
		return lstAlertListener;
	}
	public void setLstAlertListener(List<AlertListenerData> lstAlertListener) {
		this.lstAlertListener = lstAlertListener;
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
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List<AlertListenerTypeData> getTypeList() {
		return typeList;
	}
	public void setTypeList(List<AlertListenerTypeData> typeList) {
		this.typeList = typeList;
	}
}
