package com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms;

import java.util.Collection;
import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class MiscConcurrentLoginPolicyForm extends BaseWebForm{
	private String  name;
	private boolean show;
	private boolean hide;
	private boolean all; 
	private long pageNumber;
	private int multiFactor;
	private long totalPages;
	private long totalRecords;
	private String commonStatusId;
	private String action;
	private Collection concurrentLoginPolicyList;
	private String status;
	private List listConcurrentLoginPolicy;
	private String concurrentLoginPolicyId = null;
	private String auditUId;
	
	
	public String getConcurrentLoginPolicyId() {
		return concurrentLoginPolicyId;
	}
	public void setConcurrentLoginPolicyId(String concurrentLoginPolicyId) {
		this.concurrentLoginPolicyId = concurrentLoginPolicyId;
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
	public String getCommonStatusId() {
		return commonStatusId;
	}
	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}
	public Collection getConcurrentLoginPolicyList() {
		return concurrentLoginPolicyList;
	}
	public void setConcurrentLoginPolicyList(Collection concurrentLoginPolicyList) {
		this.concurrentLoginPolicyList = concurrentLoginPolicyList;
	}
	public boolean isHide() {
		return hide;
	}
	public void setHide(boolean hide) {
		this.hide = hide;
	}
	public List getListConcurrentLoginPolicy() {
		return listConcurrentLoginPolicy;
	}
	public void setListConcurrentLoginPolicy(List listConcurrentLoginPolicy) {
		this.listConcurrentLoginPolicy = listConcurrentLoginPolicy;
	}
	public int getMultiFactor() {
		return multiFactor;
	}
	public void setMultiFactor(int multiFactor) {
		this.multiFactor = multiFactor;
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
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	} 

	
}
