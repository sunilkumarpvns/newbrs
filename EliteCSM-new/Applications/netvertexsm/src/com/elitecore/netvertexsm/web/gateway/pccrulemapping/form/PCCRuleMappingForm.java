package com.elitecore.netvertexsm.web.gateway.pccrulemapping.form;

import java.util.List;

import org.apache.struts.action.ActionForm;

import com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping.data.RuleMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData;


public class PCCRuleMappingForm extends ActionForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long mappingId;
	private String name;
	private String description;
	private long pageNumber;		
	private long totalPages;
	private long totalRecords;
	private String action;
	private String actionName;
	private List<PCCRuleMappingData> pccRuleMappingList;
	
	private List<RuleMappingData> ruleMappingList;
	public long getMappingId() {
		return mappingId;
	}

	public void setMappingId(long mappingId) {
		this.mappingId = mappingId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public List<RuleMappingData> getRuleMappingList() {
		return ruleMappingList;
	}

	public void setRuleMappingList(List<RuleMappingData> pccRuleMappingList) {
		this.ruleMappingList = pccRuleMappingList;
	}

	public List<PCCRuleMappingData> getPccRuleMappingList() {
		return pccRuleMappingList;
	}

	public void setPccRuleMappingList(List<PCCRuleMappingData> pccRuleMappingList) {
		this.pccRuleMappingList = pccRuleMappingList;
	}

	
	
	
}
