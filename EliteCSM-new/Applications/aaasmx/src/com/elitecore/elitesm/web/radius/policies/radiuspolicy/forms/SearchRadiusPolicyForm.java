package com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms;

import java.util.Collection;
import java.util.List;

import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;

public class SearchRadiusPolicyForm extends BaseDictionaryForm{
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
	private Collection radiusPolicyList;
	private String status;
	private List listRadiusPolicy; 
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public boolean getAll() {
		return all;
	}
	public void setAll(boolean all) {
		this.all = all;
	}
	public boolean getHide() {
		return hide;
	}
	public void setHide(boolean hide) {
		this.hide = hide;
	}
	public Collection getRadiusPolicyList() {
		return radiusPolicyList;
	}
	public void setRadiusPolicyList(Collection lstRadiusPolicy) {
		this.radiusPolicyList = lstRadiusPolicy;
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
	public boolean getShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCommonStatusId() {
		return commonStatusId;
	}
	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}
	public List getListRadiusPolicy() {
		return listRadiusPolicy;
	}
	public void setListRadiusPolicy(List listRadiusPolicy) {
		this.listRadiusPolicy = listRadiusPolicy;
	}
	
	/*public ActionErrors validate(ActionMapping mapping,HttpServletRequest request) 
	{
     		return new ActionErrors();
	}*/
}
