package com.elitecore.elitesm.web.radius.bwlist.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.radius.bwlist.data.BWListData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class SearchBWListForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;
	private String attribute;
	private String attributeValue;
	private String typeName;
	private String status;
	private String action;
	private long pageNumber;
	private long pageNumberForType;
	private int multiFactor;
	private long totalPages;
	private long totalRecords;
	private List<BWListData> bwList;
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
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
	public int getMultiFactor() {
		return multiFactor;
	}
	public void setMultiFactor(int multiFactor) {
		this.multiFactor = multiFactor;
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
	public List<BWListData> getBwList() {
		return bwList;
	}
	public void setBwList(List<BWListData> bwList) {
		this.bwList = bwList;
	}
	public String getAttributeValue() {
		return attributeValue;
	}
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public long getPageNumberForType() {
		return pageNumberForType;
	}
	public void setPageNumberForType(long pageNumberForType) {
		this.pageNumberForType = pageNumberForType;
	}
	
	
}
