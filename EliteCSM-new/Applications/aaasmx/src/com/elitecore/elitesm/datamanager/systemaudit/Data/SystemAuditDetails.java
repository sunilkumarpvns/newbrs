package com.elitecore.elitesm.datamanager.systemaudit.Data;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;


public class SystemAuditDetails extends BaseData implements ISystemAuditDetails {
	
	private String auditDetailId;
	private String systemAuditId;
	private byte[] history;
	
	public String getAuditDetailId() {
		return auditDetailId;
	}
	public void setAuditDetailId(String auditDetailId) {
		this.auditDetailId = auditDetailId;
	}
	public String getSystemAuditId() {
		return systemAuditId;
	}
	public void setSystemAuditId(String systemAuditId) {
		this.systemAuditId = systemAuditId;
	}
	public byte[] getHistory() {
		return history;
	}
	public void setHistory(byte[] history) {
		this.history = history;
	}
}
