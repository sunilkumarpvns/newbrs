package com.elitecore.netvertexsm.datamanager.systemaudit.Data;

import java.util.Date;

import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IActionData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;

public interface ISystemAuditData {
	
	public String getActionId();
	public void setActionId(String actionId);
	
	public Date getAuditDate();
	public void setAuditDate(Date auditDate);
	
	public String getRemarks(); 
	public void setRemarks(String remarks);
	
	public long getSystemAuditId();
	public void setSystemAuditId(long systemAuditId);
	
	public Long getSystemUserId();
	public void setSystemUserId(Long systemUserId);
	
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


}
