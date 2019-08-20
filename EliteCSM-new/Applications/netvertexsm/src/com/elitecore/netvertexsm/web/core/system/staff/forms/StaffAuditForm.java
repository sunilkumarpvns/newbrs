package com.elitecore.netvertexsm.web.core.system.staff.forms;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class StaffAuditForm extends BaseWebForm {
	
	private Date auditDate;
	private String actionId;
	private String userId;
	private String actionName;
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	List auditDetailList = new ArrayList();
	List actionListInCombo = new ArrayList();
	List usersListInCombo = new ArrayList();
	
	private static java.text.DateFormat formatter = new java.text.SimpleDateFormat("MM-dd-yyyy");
	
	public String getStrAuditDate(){
		if(auditDate != null)
			return formatter.format(auditDate);
		else 
			return "";
	}
	public void setStrAuditDate(String strAuditDate){
		try{
			if(!strAuditDate.equalsIgnoreCase("")){
				this.auditDate=formatter.parse(strAuditDate);
			}
		}catch(ParseException e){
			e.printStackTrace();
		}
	}
	/*public Timestamp getAuditDate() {
		return auditDate;
	}
	public void setAuditDate(Timestamp auditDate) {
		this.auditDate = auditDate;
	}*/
	public String getActionId() {
		return actionId;
	}
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public List getActionListInCombo() {
		return actionListInCombo;
	}
	public void setActionListInCombo(List actionListInCombo) {
		this.actionListInCombo = actionListInCombo;
	}
	public List getUsersListInCombo() {
		return usersListInCombo;
	}
	public void setUsersListInCombo(List usersListInCombo) {
		this.usersListInCombo = usersListInCombo;
	}
	public Date getAuditDate() {
		return auditDate;
	}
	public void setAuditDate(Date auditDate) {
		this.auditDate = auditDate;
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
	public List getAuditDetailList() {
		return auditDetailList;
	}
	public void setAuditDetailList(List auditDetailList) {
		this.auditDetailList = auditDetailList;
	}
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	

}
