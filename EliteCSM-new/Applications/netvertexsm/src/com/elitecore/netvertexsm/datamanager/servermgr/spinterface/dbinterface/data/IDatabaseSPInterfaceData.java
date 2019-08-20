package com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data;

import java.util.Set;

public interface IDatabaseSPInterfaceData {
	public Long getDatabaseSprId();
	public void setDatabaseSprId(Long databaseSprId);
	public Long getDatabaseDsId();
	public void setDatabaseDsId(Long databaseDsId);
	public String getTableName();
	public void setTableName(String tableName);
	public Long getDbQueryTimeout();
	public void setDbQueryTimeout(Long dbQueryTimeout);
	public Long getMaxQueryTimeoutCnt();
	public void setMaxQueryTimeoutCnt(Long maxQueryTimeoutCnt);
	public Long getDriverInstanceId();
	public void setDriverInstanceId(Long driverInstanceId);
	public String getIdentityField();
	public void setIdentityField(String identityField);
	
	public Set<DBFieldMapData> getDbFieldMapSet();
	public void setDbFieldMapSet(Set<DBFieldMapData> dbFieldMapSet);
}
