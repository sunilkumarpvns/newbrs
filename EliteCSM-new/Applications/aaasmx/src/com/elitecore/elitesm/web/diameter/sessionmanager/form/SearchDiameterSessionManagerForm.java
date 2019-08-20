package com.elitecore.elitesm.web.diameter.sessionmanager.form;

import java.util.List;

import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class SearchDiameterSessionManagerForm extends BaseWebForm{
	/**
	 * @author nayana.rathod
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String action;
	private String status;
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	private List policyList;
	private List<DiameterSessionManagerData> listSessionManager;
	private List<IDatabaseDSData> databaseDSDataList;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
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
	public List getPolicyList() {
		return policyList;
	}
	public void setPolicyList(List policyList) {
		this.policyList = policyList;
	}
	
	@Override
	public String toString() {
		return "SearchDiameterSessionManagerForm [name=" + name + ", action="
				+ action + ", status=" + status + ", pageNumber=" + pageNumber
				+ ", totalPages=" + totalPages + ", totalRecords="
				+ totalRecords + ", policyList=" + policyList + "]";
	}
	public List<DiameterSessionManagerData> getListSessionManager() {
		return listSessionManager;
	}
	public void setListSessionManager(List<DiameterSessionManagerData> listSessionManager) {
		this.listSessionManager = listSessionManager;
	}
	public List<IDatabaseDSData> getDatabaseDSDataList() {
		return databaseDSDataList;
	}
	public void setDatabaseDSDataList(List<IDatabaseDSData> databaseDSDataList) {
		this.databaseDSDataList = databaseDSDataList;
	}
}
