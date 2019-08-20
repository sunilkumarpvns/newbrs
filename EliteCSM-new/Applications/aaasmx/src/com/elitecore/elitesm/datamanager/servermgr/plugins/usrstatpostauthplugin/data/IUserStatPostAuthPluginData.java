package com.elitecore.elitesm.datamanager.servermgr.plugins.usrstatpostauthplugin.data;

public interface IUserStatPostAuthPluginData {


	public String getPluginId();
	public void setPluginId(String pluginId);

	public String getPluginInstanceId();
	public void setPluginInstanceId(String pluginInstanceId);

	public byte[] getPluginData();
	public void setPluginData(byte[] pluginData);
	
	public String getDatabaseId() ;
	public void setDatabaseId(String databaseId);

	public String getDataSourceName();
	public void setDataSourceName(String dataSourceName);
	
	public String getTableName() ;	
	public void setTableName(String tableName) ;
	
	public long getDbQueryTimeout() ;
	public void setDbQueryTimeout(long dbQueryTimeout) ;
	
	public long getMaxQueryTimeoutCount() ;
	public void setMaxQueryTimeoutCount(long maxQueryTimeoutCount) ;
	
	public long getBatchUpdateInterval();
	public void setBatchUpdateInterval(long batchUpdateInterval);
	
}
