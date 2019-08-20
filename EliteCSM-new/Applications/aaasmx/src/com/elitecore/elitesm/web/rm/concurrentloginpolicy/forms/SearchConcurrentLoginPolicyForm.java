package com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms;

import java.util.Collection;
import java.util.List;

import com.elitecore.elitesm.web.radius.base.forms.BaseConcurrentLoginPolicyForm;

public class SearchConcurrentLoginPolicyForm  extends BaseConcurrentLoginPolicyForm {

	private String toggleAll = null;
	private String c_bSelected = null;
	private String concurrentLoginPolicyId = null;
//	private List stListConcurrentLoginPolicy = null;
	
	private String  name;
	private boolean show;
	private boolean hide;
	private boolean all; 
	private long pageNumber;
//	private int multiFactor;
	private long totalPages;
	private long totalRecords;
	private String commonStatusId;
	private String action;
	private Collection concurrentLoginPolicyList;
	private String status;
	private boolean individual;
	private boolean group;
	private boolean bPolicyTypeAll;
	private String auditUId;
	private Long concurrentLoginId;
	
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
	public String getC_bSelected() {
		return c_bSelected;
	}
	public void setC_bSelected(String selected) {
		c_bSelected = selected;
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
	public String getToggleAll() {
		return toggleAll;
	}
	public void setToggleAll(String toggleAll) {
		this.toggleAll = toggleAll;
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
	public String getConcurrentLoginPolicyId() {
		return concurrentLoginPolicyId;
	}
	public void setConcurrentLoginPolicyId(String concurrentLoginPolicyId) {
		this.concurrentLoginPolicyId = concurrentLoginPolicyId;
	}
	public boolean isBAll() {
		return bPolicyTypeAll;
	}
	public void setBAll(boolean all) {
		bPolicyTypeAll = all;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	
//	public List getConcurrentLoginPolicyList(){
//        return stListConcurrentLoginPolicy;
//    }
//
//	public void setConcurrentLoginPolicyList(List stListConcurrentLoginPolicy){
//    	this.stListConcurrentLoginPolicy = stListConcurrentLoginPolicy;
//    }
	
	

}
