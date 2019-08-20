package com.elitecore.elitesm.web.radius.bwlist.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.radius.bwlist.data.BWListData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class MiscBWListForm extends BaseWebForm{

	private static final long serialVersionUID = 1L;
	private String attribute;
	private String action;
	private boolean all;
	private long pagetNumber;
	private long multiFactor;
	private long totalPages;
	private long totalRecords;
	private String commonStatusId;
	private List<BWListData> bwList;
	private String status;
	private String typeName;
	
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public boolean isAll() {
		return all;
	}
	public void setAll(boolean all) {
		this.all = all;
	}
	public long getPagetNumber() {
		return pagetNumber;
	}
	public void setPagetNumber(long pagetNumber) {
		this.pagetNumber = pagetNumber;
	}
	public long getMultiFactor() {
		return multiFactor;
	}
	public void setMultiFactor(long multiFactor) {
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
	public String getCommonStatusId() {
		return commonStatusId;
	}
	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}
	public List<BWListData> getBwList() {
		return bwList;
	}
	public void setBwList(List<BWListData> bwList) {
		this.bwList = bwList;
	}
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
	
}
