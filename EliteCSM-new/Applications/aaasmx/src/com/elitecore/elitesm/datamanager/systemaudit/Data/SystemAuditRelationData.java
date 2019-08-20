package com.elitecore.elitesm.datamanager.systemaudit.Data;

import java.io.Serializable;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

/**
 * Provides relational data with system audit with store module name and audit id
 * which helps to communicate with system audit table 
 * @author Tejas.P.Shah
 *
 */
public class SystemAuditRelationData extends BaseData implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String moduleName;
	private String auditId;
	
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getAuditId() {
		return auditId;
	}
	public void setAuditId(String auditId) {
		this.auditId = auditId;
	}
}
