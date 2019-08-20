package com.elitecore.netvertexsm.datamanager.systemaudit.Data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IActionData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;

public class SystemAuditData implements ISystemAuditData {
	
	private long systemAuditId;
	private String actionId;
	private String transactionId;
	private String remarks;
	private Long systemUserId;
	private String systemUserName;
	private Date auditDate;
	private String clientIP;
	
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
	
	public long getSystemAuditId() {
		return systemAuditId;
	}
	public void setSystemAuditId(long systemAuditId) {
		this.systemAuditId = systemAuditId;
	}
	public Long getSystemUserId() {
		return systemUserId;
	}
	public void setSystemUserId(Long systemUserId) {
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
		
		StringWriter out  = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
	    writer.println("------------SystemAudit Data-----------------");
        writer.println("systemAuditId          =" +systemAuditId);  
        writer.println("transactionId          =" +transactionId);  
        writer.println("remarks                =" +remarks);  
        writer.println("systemUserName         =" +systemUserName);
        writer.println("auditDate              =" +auditDate);
        writer.println("systemUserId           =" +systemUserId);  
        writer.println("actionId               =" +actionId);  
        writer.println("clientIP               =" +clientIP);  
        writer.println("---------------------------------------");
		writer.close();
		return out.toString();
	}
	
	

}
