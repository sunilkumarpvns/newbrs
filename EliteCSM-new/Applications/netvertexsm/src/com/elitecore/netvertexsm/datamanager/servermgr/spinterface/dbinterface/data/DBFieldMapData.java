package com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data;

import java.io.Serializable;

public class DBFieldMapData implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Long dbFieldMapId;
	private String logicalName;
	private String dbField;
	private Long dbSpInterfaceId;
	
	public Long getDbFieldMapId() {
		return dbFieldMapId;
	}
	public void setDbFieldMapId(Long dbFieldMapId) {
		this.dbFieldMapId = dbFieldMapId;
	}
	public String getLogicalName() {
		return logicalName;
	}
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	public String getDbField() {
		return dbField;
	}
	public void setDbField(String dbField) {
		this.dbField = dbField;
	}
	public Long getDbSpInterfaceId() {
		return dbSpInterfaceId;
	}
	public void setDbSpInterfaceId(Long dbSpInterfaceId) {
		this.dbSpInterfaceId = dbSpInterfaceId;
	}
}
