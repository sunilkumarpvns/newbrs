package com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data;

public interface IDBFieldMapData {
	public Long getDbFieldMapId();
	public void setDbFieldMapId(Long dbFieldMapId);
	public String getLogicalName();
	public void setLogicalName(String logicalName);
	public String getDbField();
	public void setDbField(String dbField);
	public Long getDbSpInterfaceId();
	public void setDbSpInterfaceId(Long dbSpInterfaceId);
}
