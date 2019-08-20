package com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data;

import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatasourceSchemaData;

public interface IDBAuthDriverData {

	public String getDbAuthId() ;
	public void setDbAuthId(String dbAuthId) ;
	
	public String getDatabaseId() ;
	public void setDatabaseId(String databaseId) ;
	
	/*public long getDbScanTime() ;
	public void setDbScanTime(long dbScanTime) ;*/
	
	public String getTableName() ;	
	public void setTableName(String tableName) ;
	
	public Long getDbQueryTimeout() ;
	public void setDbQueryTimeout(Long dbQueryTimeout) ;
	
	public Long getMaxQueryTimeoutCount() ;
	public void setMaxQueryTimeoutCount(Long maxQueryTimeoutCount) ;
	
	public String getDriverInstanceId() ;
	public void setDriverInstanceId(String driverInstanceId) ;
	
	public List<DBAuthFieldMapData> getDbFieldMapList() ;
	public void setDbFieldMapList(List<DBAuthFieldMapData> dbFeildMapList); 
	
	public String getProfileLookupColumn();
	public void setProfileLookupColumn(String profileLookupColumn);
	
	public Set<IDatasourceSchemaData> getDatasourceSchemaSet();
	public void setDatasourceSchemaSet(Set<IDatasourceSchemaData> datasourceSchemaSet);
	
}
