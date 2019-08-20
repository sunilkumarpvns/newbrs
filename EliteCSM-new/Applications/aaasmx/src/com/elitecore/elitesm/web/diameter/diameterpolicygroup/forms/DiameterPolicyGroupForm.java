package com.elitecore.elitesm.web.diameter.diameterpolicygroup.forms;

import java.util.Collection;
import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

/**
 * author : nayana.rathod
 */

public class DiameterPolicyGroupForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private String action;
	private String policyId;
	private String policyname;
	private String expression;
	private String auditUId;
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	private Collection diameterPolicyGroupList;
	private List listDiameterPolicyGroup;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getPolicyId() {
		return policyId;
	}
	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}
	public String getPolicyname() {
		return policyname;
	}
	public void setPolicyname(String policyname) {
		this.policyname = policyname;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
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
	public Collection getDiameterPolicyGroupList() {
		return diameterPolicyGroupList;
	}
	public void setDiameterPolicyGroupList(Collection diameterPolicyGroupList) {
		this.diameterPolicyGroupList = diameterPolicyGroupList;
	}
	public List getListDiameterPolicyGroup() {
		return listDiameterPolicyGroup;
	}
	public void setListDiameterPolicyGroup(List listDiameterPolicyGroup) {
		this.listDiameterPolicyGroup = listDiameterPolicyGroup;
	} 
}
