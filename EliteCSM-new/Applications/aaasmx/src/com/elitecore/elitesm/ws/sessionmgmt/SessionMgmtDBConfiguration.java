package com.elitecore.elitesm.ws.sessionmgmt;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.elitesm.ws.BaseDBConfiguration;

public class SessionMgmtDBConfiguration extends BaseDBConfiguration {

	private Map<String,String> attributeFieldMap = new HashMap<String,String>();
	private static DBConnectionManager dbConnectionManager;
	private final static String cache = "ELITE_SESSION_DB_CACHE";
	
	public SessionMgmtDBConfiguration(String driverClass,String connectionUrl,String userName, String password){
		super( driverClass, connectionUrl, userName,  password,"TBLMCONCURRENTUSERS");
	}

	public Map<String, String> getAttributeFieldMap() {
		return attributeFieldMap;
	}
	
	@Override
	public DBConnectionManager getDbConnectionManager() {
		return SessionMgmtDBConfiguration.dbConnectionManager;
	}

	@Override
	public void setDbConnectionManager(DBConnectionManager dbConnectionManager) {
		SessionMgmtDBConfiguration.dbConnectionManager = dbConnectionManager;
		
	}

	@Override
	public String getCache() {
		return SessionMgmtDBConfiguration.cache;
	}

	public String toString() {
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------SessionManagementDBConfiguration-----------------");
		writer.println("connectionUrl        =" +getConnectionUrl());                                     
		writer.println("userName             =" +getUserName());                                     
		writer.println("password             =" +getPassword());                                     
		writer.println("tableName            =" +getTableName());
		writer.println("driverClass          =" +getDriverClass());
		writer.println("cache                =" +getCache());
		writer.println("-------------------------------------------------------------");
		writer.close();
		return out.toString();
	}
}

