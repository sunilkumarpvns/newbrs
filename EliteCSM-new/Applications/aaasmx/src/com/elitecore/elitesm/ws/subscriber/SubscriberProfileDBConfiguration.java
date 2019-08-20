package com.elitecore.elitesm.ws.subscriber;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.elitesm.ws.BaseDBConfiguration;

public class SubscriberProfileDBConfiguration extends BaseDBConfiguration {


	private String userIdentityFieldName;
	private String primaryKeyColumn;
	private String sequenceName;
	private Map<String, String> wsRequestFieldMap;
	private Map<String, String> wsResponseFieldMap;
	private Map<String, String> columns;
	private static DBConnectionManager dbConnectionManager;
	private static final String cache = "ELITE_SUBSCRIBER_DB_CACHE";

	public  SubscriberProfileDBConfiguration(String driverClass,String connectionUrl,String userName, String password,String tableName,String userIdentityFieldName,String primaryKeyColumn,String sequenceName){
		super( driverClass,connectionUrl,userName, password,tableName);
		this.userIdentityFieldName =userIdentityFieldName;
		this.primaryKeyColumn = primaryKeyColumn;
		this.sequenceName = sequenceName;
		
	}
	public String getPrimaryKeyColumn() {
		return primaryKeyColumn;
	}

	public void setPrimaryKeyColumn(String primaryKeyColumn) {
		this.primaryKeyColumn = primaryKeyColumn;
	}

	public String getSequenceName() {
		return sequenceName;
	}

	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}


	public String getUserIdentityFieldName() {
		return userIdentityFieldName;
	}

	public void setUserIdentityFieldName(String userIdentityFieldName) {
		this.userIdentityFieldName = userIdentityFieldName;
	}
	
	public Map<String, String> getWsRequestFieldMap() {
		return wsRequestFieldMap;
	}
	public void setWsRequestFieldMap(
			Map<String, String> wsRequestFieldMap) {
		this.wsRequestFieldMap = wsRequestFieldMap;
	}
	public Map<String, String> getWsResponseFieldMap() {
		return wsResponseFieldMap;
	}
	public void setWsResponseFieldMap(
			Map<String, String> wsResponseFieldMap) {
		this.wsResponseFieldMap = wsResponseFieldMap;
	}
	public Map<String, String> getColumns() {
		return columns;
	}
	public void setColumns(Map<String, String> columns) {
		this.columns = columns;
	}
	@Override
	public DBConnectionManager getDbConnectionManager() {
		return SubscriberProfileDBConfiguration.dbConnectionManager;
	}

	@Override
	public void setDbConnectionManager(DBConnectionManager dbConnectionManager) {
		SubscriberProfileDBConfiguration.dbConnectionManager = dbConnectionManager;
		
	}

	@Override
	public String getCache() {
		return SubscriberProfileDBConfiguration.cache;
	}
	

	public String toString() {
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------SubscriberProfileDBConfiguration-----------------");
		writer.println("connectionUrl        =" +getConnectionUrl());                                     
		writer.println("userName             =" +getUserName());                                     
		writer.println("password             =" +getPassword());
		writer.println("maxIdleConnectionPool=" +getMaxIdle());
		writer.println("maxConnectionPool    =" +getMaximumPool());
		writer.println("Connection Cache     =" +getCache());
		writer.println("tableName            =" +getTableName());
		writer.println("driverClass          =" +getDriverClass());
		writer.println("userIdentityFieldName=" +userIdentityFieldName);
		writer.println("-------------------------------------------------------------");
		writer.close();
		return out.toString();
	}
}

