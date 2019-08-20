package com.elitecore.netvertexsm.web.servicepolicy.pcrfservicepolicy.form;

import java.util.List;

import org.apache.struts.action.ActionForm;

import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicyData;

public class SearchPCRFServicePolicyForm extends ActionForm {
	private String name;
	private long pageNumber;		
	private long totalPages;
	private long totalRecords;
	private String action;
	
	private List<PCRFServicePolicyData> listSearchPcrfPolicy;
	
	
	public List<PCRFServicePolicyData> getListSearchPcrfPolicy() {
		return listSearchPcrfPolicy;
	}
	public void setListSearchPcrfPolicy(
			List<PCRFServicePolicyData> listSearchPcrfPolicy) {
		this.listSearchPcrfPolicy = listSearchPcrfPolicy;
	}
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
}
