package com.elitecore.elitesm.datamanager.systemaudit.Data;

import java.util.Date;

import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IActionData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;

public interface ISystemAuditData {
	
	public String getActionId();
	public void setActionId(String actionId);
	
	public Date getAuditDate();
	public void setAuditDate(Date auditDate);
	
	public String getRemarks(); 
	public void setRemarks(String remarks);
	
	public String getSystemAuditId();
	public void setSystemAuditId(String systemAuditId);
	
	public String getSystemUserId();
	public void setSystemUserId(String systemUserId);
	
	public String getSystemUserName();
	public void setSystemUserName(String systemUserName);
	
	public String getTransactionId();
	public void setTransactionId(String transactionId);
	
	public IActionData getActionData();
	public void setActionData(IActionData actionData);
	
	public String getClientIP();
	public void setClientIP(String clientIP);
	
	public IStaffData getStaffData();
	public void setStaffData(IStaffData staffData);
	public String toString();

	public String getAuditId();
	public void setAuditId(String auditId);
	public String getAuditName();
	public void setAuditName(String auditName);

	public Date getAuditDateFrom();
	public void setAuditDateFrom(Date auditDateFrom);
}
