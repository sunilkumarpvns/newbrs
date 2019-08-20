package com.elitecore.elitesm.datamanager.systemaudit.Data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IActionData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;

public class SystemAuditData extends BaseData implements ISystemAuditData {
	
	private String systemAuditId;
	private String actionId;
	private String transactionId;
	private String remarks;
	private String systemUserId;
	private String systemUserName;
	private Date auditDate;
	private String clientIP;
	private String auditId;
	private String auditName;
	private SystemAuditDetails systemAuditDetails;
	private Date auditDateFrom;
	
	private IActionData actionData;
    private IStaffData staffData;
	
	public String getActionId() {
		return actionId;
	}
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}
	public Date getAuditDate() {
		return auditDate;
	}
	public void setAuditDate(Date auditDate) {
		this.auditDate = auditDate;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public String getSystemAuditId() {
		return systemAuditId;
	}
	public void setSystemAuditId(String systemAuditId) {
		this.systemAuditId = systemAuditId;
	}
	public String getSystemUserId() {
		return systemUserId;
	}
	public void setSystemUserId(String systemUserId) {
		this.systemUserId = systemUserId;
	}
	public String getSystemUserName() {
		return systemUserName;
	}
	public void setSystemUserName(String systemUserName) {
		this.systemUserName = systemUserName;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public IActionData getActionData() {
		return actionData;
	}
	public void setActionData(IActionData actionData) {
		this.actionData = actionData;
	}
	public IStaffData getStaffData() {
		return staffData;
	}
	public void setStaffData(IStaffData staffData) {
		this.staffData = staffData;
	}
	public String getClientIP() {
		return clientIP;
	}
	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}
	public String toString(){
		
		/*return "Action Id is: "+this.getActionId() +"Remark is:"+ this.getRemarks()+"SystemAuditId is:"+ this.getSystemAuditId() +"SystemUserId is:"+this.getSystemUserId() +"SystemUserName is:"+ this.getSystemUserName() +"Transaction Id is:"+this.getTransactionId()+
		"StaffId is:"+this.getSystemUserId();*/
		
		StringWriter out  = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
	    writer.println("------------SystemAuditData-----------------");
        writer.println("systemAuditId          =" +systemAuditId);  
        writer.println("transactionId          =" +transactionId);  
        writer.println("remarks                =" +remarks);  
        writer.println("systemUserName         =" +systemUserName);
        writer.println("auditDate              =" +auditDate);
        writer.println("systemUserId           =" +systemUserId);  
        writer.println("clientIP               =" +clientIP);
        writer.println("actionId               =" +actionId);  
        writer.println("---------------------------------------");
		writer.close();
		return out.toString();
	}
	public String getAuditId() {
		return auditId;
	}
	public void setAuditId(String auditId) {
		this.auditId = auditId;
	}
	public String getAuditName() {
		return auditName;
	}
	public void setAuditName(String auditName) {
		this.auditName = auditName;
	}
	public SystemAuditDetails getSystemAuditDetails() {
		return systemAuditDetails;
	}
	public void setSystemAuditDetails(SystemAuditDetails systemAuditDetails) {
		this.systemAuditDetails = systemAuditDetails;
	}
	public Date getAuditDateFrom() {
		return auditDateFrom;
	}
	public void setAuditDateFrom(Date auditDateFrom) {
		this.auditDateFrom = auditDateFrom;
	}
}
