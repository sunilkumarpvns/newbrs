package com.elite.auth.db;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import oracle.jdbc.driver.OracleSQLException;
import oracle.jdbc.pool.OracleDataSource;
import netscape.ldap.LDAPException;
import netscape.ldap.util.ConnectionPool;

import com.elite.config.LDAPConfig;
import com.elite.config.ORACLEConfig;
import com.elite.context.Context_Manager;
import com.elite.context.WSCContext;
import com.elite.exception.CommunicationException;
import com.elite.exception.ContextManagerNotInitialisedException;
import com.opensymphony.xwork2.ActionContext;

public class DBConnectionManager {
	private static DBConnectionManager dbconnectionmanager = new DBConnectionManager();
	private HashMap<String, OracleDataSource> dbdatasourceMap = new HashMap<String, OracleDataSource>();
	
	public static OracleDataSource getInstance(String dataSourceKey)throws Exception
	{
		OracleDataSource datasourceInstance = (OracleDataSource)dbconnectionmanager.dbdatasourceMap.get(dataSourceKey);
		if (datasourceInstance == null) {
			synchronized (DBConnectionManager.class) {
				//Following check is required, do not remove it.
				datasourceInstance = (OracleDataSource)dbconnectionmanager.dbdatasourceMap.get(dataSourceKey);
				if (datasourceInstance == null) {
					datasourceInstance = new DBConnectionManager().createLDAPConnectionPool();
					dbconnectionmanager.dbdatasourceMap.put(dataSourceKey, datasourceInstance);
				}
			}
		}
		return datasourceInstance;
	}
	
	public OracleDataSource createLDAPConnectionPool() throws CommunicationException {
		OracleDataSource datadource = null;
		try 
		{
			ORACLEConfig ora_config = (ORACLEConfig)((WSCContext)ActionContext.getContext().getApplication().get("wsc_context")).getAttribute("ora");
			Properties prop = new Properties();
			prop.put("MinLimit",ora_config.getMinpoolsize());
			prop.put("MaxLimit",ora_config.getMaxpoolsize());
			datadource = new OracleDataSource();
			datadource.setURL(ora_config.getConnectionurl());
			datadource.setUser(ora_config.getUsername());
			datadource.setPassword(ora_config.getPassword());
			datadource.setConnectionProperties(prop);
			
		}catch(OracleSQLException e){
			throw new CommunicationException("DataSource Exceptin in loading, reason: " + e.getMessage());
		}catch(Exception exp){
			throw new CommunicationException("DataSource Exceptin in loading, reason: " + exp.getMessage());
		}
		return datadource;
   }

	
	
}
