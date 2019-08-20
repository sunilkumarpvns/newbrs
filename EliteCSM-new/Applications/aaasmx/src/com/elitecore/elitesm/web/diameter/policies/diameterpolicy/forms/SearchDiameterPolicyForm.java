/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   SearchDiameterpolicyForm.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.policies.diameterpolicy.forms;

import java.util.Collection;
import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class SearchDiameterPolicyForm extends BaseWebForm{

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
	private Collection diameterPolicyList;
	private String status;
	private List listDiameterPolicy;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public boolean isHide() {
		return hide;
	}
	public void setHide(boolean hide) {
		this.hide = hide;
	}
	public boolean isAll() {
		return all;
	}
	public void setAll(boolean all) {
		this.all = all;
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
	public String getCommonStatusId() {
		return commonStatusId;
	}
	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Collection getDiameterPolicyList() {
		return diameterPolicyList;
	}
	public void setDiameterPolicyList(Collection diameterPolicyList) {
		this.diameterPolicyList = diameterPolicyList;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List getListDiameterPolicy() {
		return listDiameterPolicy;
	}
	public void setListDiameterPolicy(List listDiameterPolicy) {
		this.listDiameterPolicy = listDiameterPolicy;
	} 
}
