package com.elitecore.core.commons.util.db.datasource;


public interface DBDataSource {
	public int getStatusCheckDuration();
	public String getDatasourceID();
	public String getConnectionURL();
	public String getUsername();
	public String getPassword();
	public String getPlainTextPassword();
	public String getDataSourceName();
	public int getMinimumPoolSize();
	public int getMaximumPoolSize();
	public int getNetworkReadTimeout();
}
