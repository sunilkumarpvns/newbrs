package com.elitecore.elitesm.web.radius.policies.accesspolicy.forms;

import java.util.Collection;
import java.util.List;

import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;

public class MiscAccessPolicyForm extends BaseDictionaryForm{
	private String accessPolicyId;
	private String name;
	private boolean show;
	private boolean hide;
	private boolean all;
	private long pageNumber;
	private int multiFactor;
	private long totalPages;
	private long totalRecords;
	private String commonStatusId;
	private String action;
	private Collection accessPolicyList;
	private String status;
	private List listAccessPolicy;
	
	public Collection getAccessPolicyList() {
		return accessPolicyList;
	}
	public void setAccessPolicyList(Collection accessPolicyList) {
		this.accessPolicyList = accessPolicyList;
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
	public boolean isHide() {
		return hide;
	}
	public void setHide(boolean hide) {
		this.hide = hide;
	}
	public List getListAccessPolicy() {
		return listAccessPolicy;
	}
	public void setListAccessPolicy(List listAccessPolicy) {
		this.listAccessPolicy = listAccessPolicy;
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
	public String getAccessPolicyId() {
		return accessPolicyId;
	}
	public void setAccessPolicyId(String accessPolicyId) {
		this.accessPolicyId = accessPolicyId;
	}
}
