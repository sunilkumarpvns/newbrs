package com.elitecore.elitesm.web.core.system.staff.forms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class StaffAuditForm extends BaseWebForm {
	
	private static final long serialVersionUID = 1L;
	private Date auditDate;
	private Date auditDateFrom;
	private String actionId;
	private String userId;
	private String actionName;
	private String name;
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	private String strAuditDateFrom;
	
	List auditDetailList = new ArrayList();
	List actionListInCombo = new ArrayList();
	List usersListInCombo = new ArrayList();
	
	/*private static java.text.DateFormat formatter = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss");*/
	private static SimpleDateFormat auditDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	//String auditDate = auditDateFormat.format(systemAuditData.getAuditDate());
	
	public String getStrAuditDate(){
		if(auditDate != null)
			return auditDateFormat.format(auditDate);
		else 
			return "";
	}
	public void setStrAuditDate(String strAuditDate){
		try{
			if(!strAuditDate.equalsIgnoreCase("")){
				this.auditDate=auditDateFormat.parse(strAuditDate);
			}
		}catch(ParseException e){
			e.printStackTrace();
		}
	}
	
	public String getStrAuditDateFrom() {
		if(auditDateFrom != null)
			return auditDateFormat.format(auditDateFrom);
		else 
			return "";
	}
	public void setStrAuditDateFrom(String strAuditDateFrom) {
		try{
			if(!strAuditDateFrom.equalsIgnoreCase("")){
				this.auditDateFrom=auditDateFormat.parse(strAuditDateFrom);
			}
		}catch(ParseException e){
			e.printStackTrace();
		}
	}
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getAuditDateFrom() {
		return auditDateFrom;
	}
	public void setAuditDateFrom(Date auditDateFrom) {
		this.auditDateFrom = auditDateFrom;
	}
}
