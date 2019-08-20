package com.elitecore.core.commons.utilx.ldap;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPConstraints;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPv2;
import netscape.ldap.util.ConnectionPool;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.utilx.ldap.data.LDAPDataSource;

public class LDAPConnectionManager {
	
	private static final String MODULE = "LDAP CONN MNGR";
	private static final String DEAD = "DEAD";
	private static final String ALIVE = "ALIVE";
	private static final String NOT_INITIALIZED = "NOT_INITIALIZED";
	private static Map<String,LDAPConnectionManager> connectionManagers = new HashMap<String, LDAPConnectionManager>();

	private LDAPConstraints constraints ; 
	private volatile ConnectionPool connectionPool = null;
	private int version;
	private String lastResolvedIp;
	
	private LDAPConnectionManager(){
		
	}
	public static LDAPConnectionManager getInstance(String dataSourceKey ){
		LDAPConnectionManager managerInstance = connectionManagers.get(dataSourceKey);
		if (managerInstance == null) {
			synchronized (LDAPConnectionManager.class) {
				//Following check is required, do not remove it.
				managerInstance = connectionManagers.get(dataSourceKey);
				if (managerInstance == null) {
					managerInstance = new LDAPConnectionManager();
					connectionManagers.put(dataSourceKey, managerInstance);
				}
			}
		}
		return managerInstance;		
	}
	/**
	 * This Method will return the Connection of LDAP from the LDAPConnectionPool.
	 * If connection Pool size is Exceeds the Mx. size it returns NULL.
	 * @param strServerName - String specify which server's Connection you want
	 * @return ldapConnection [LDAPConnection instance]
	 * @throws LDAPException 
	 */
	public LDAPConnection getConnection() throws LDAPException{
		LDAPConnection ldapConnection 	= 	null;			
		if(connectionPool != null){
			ldapConnection = connectionPool.getConnection(0);
			if(ldapConnection != null){
				ldapConnection.setOption(LDAPv2.PROTOCOL_VERSION, version);
				if(constraints != null){
				ldapConnection.setConstraints(constraints);
		}			
			}
		}
		return ldapConnection;
	}	
	
	public void closeConnection(LDAPConnection ldapConnection){	
		if(connectionPool != null)
			connectionPool.close(ldapConnection);
	}
				
	public void createLDAPConnectionPool(LDAPDataSource dataSource)throws DriverInitializationFailedException,LDAPException{	
		createLDAPConnectionPool(dataSource,false);
		
	}
	
	public void createLDAPConnectionPool(LDAPDataSource dataSource,boolean checkForAllIp)throws DriverInitializationFailedException,LDAPException{
		
		if( dataSource.getIpAddress() == null || dataSource.getPort() == 0  || dataSource.getTimeout() == 0 || dataSource.getAdministrator() == null || dataSource.getPlainTextPassword() == null){
			throw new DriverInitializationFailedException("One or some of the mandatory fields are not set.");
		}	
		
		int timeOut = dataSource.getTimeout();
		version = dataSource.getVersion();
		
		if(constraints == null){
			constraints = new LDAPConstraints();
		}
		
		if(timeOut > 0)
			constraints.setTimeLimit(timeOut);
		
		int connectTimeout = 1;
		if(timeOut > 1000 )
			connectTimeout =(int) Math.ceil(timeOut/1000d);
		
		closeConnectionPool(dataSource.getDataSourceName());
		
		if(!checkForAllIp){
			if(connectionPool==null){
				if(lastResolvedIp!=null){
					connectionPool = new ConnectionPool(dataSource.getMinPoolSize(),dataSource.getMaxPoolSize(),lastResolvedIp, dataSource.getPort(), dataSource.getAdministrator(), dataSource.getPlainTextPassword(),connectTimeout);
				}else {
					connectionPool = new ConnectionPool(dataSource.getMinPoolSize(),dataSource.getMaxPoolSize(),dataSource.getStrIpAddress(), dataSource.getPort(), dataSource.getAdministrator(), dataSource.getPlainTextPassword(),connectTimeout);
				}
			}	
			connect(dataSource.getDataSourceName());
		}else {
			InetAddress[] inetAddressList =null;
			try {
				inetAddressList = InetAddress.getAllByName(dataSource.getStrIpAddress());
			} catch (UnknownHostException e) {
				LogManager.ignoreTrace(e);
				throw new LDAPException("Failed to initialize LdapDatasource: "+dataSource.getDataSourceName()+", Reason: "+e.getMessage());
			}
			boolean isInitializedSuccessfully = false;
			if(inetAddressList!=null && inetAddressList.length>0){
				for(int i=0;i<inetAddressList.length;i++){
					try{
						connectionPool = new ConnectionPool(dataSource.getMinPoolSize(),dataSource.getMaxPoolSize(),inetAddressList[i].getHostAddress(), dataSource.getPort(), dataSource.getAdministrator(), dataSource.getPlainTextPassword(),connectTimeout);
						connect(dataSource.getDataSourceName());
						this.lastResolvedIp = inetAddressList[i].getHostAddress();
						isInitializedSuccessfully = true;
						break;
					}catch (LDAPException e) {
						LogManager.getLogger().debug(MODULE, "Failed to initialize LdapDatasource: "+dataSource.getDataSourceName()+",using IP: "+inetAddressList[i].getHostAddress());
						LogManager.ignoreTrace(e);
					}
				}
				if(!isInitializedSuccessfully){
					throw new LDAPException("Failed to initialize LdapDatasource: "+dataSource.getDataSourceName()+",using Host: "+dataSource.getIpAddress());
				}
			}else {
				throw new LDAPException("Failed to initialize LdapDatasource: "+dataSource.getDataSourceName()+", Reason: no ip resolved from host: "+dataSource.getIpAddress());
			}
		}
		return;
	}
	
	private void closeConnectionPool(String datasourceName) {
		if(connectionPool!=null){
			ConnectionPool pool = connectionPool;
			/* Avoid pool being destroyed from being utilized by other methods */
			connectionPool = null;
			try{
				pool.destroy();
			}catch (Exception e) {
				LogManager.getLogger().error(MODULE, "Error in closing ConnectionPool for LDAP Datasource: "+datasourceName+", Reason: "+e.getMessage());
				LogManager.ignoreTrace(e);
			}
		}
	}
	
	private void connect(String datasourceName) throws LDAPException {
		/*
		 * Varifying connection pool
		 */		
		LDAPConnection tempConnection = getConnection();
		if (tempConnection != null){
			try{
				tempConnection.reconnect();
			}finally{
				if (connectionPool != null) {
					connectionPool.close(tempConnection);
				}
			}
		}else {
			connectionPool = null;
			throw new LDAPException("Failed LDAP connection pool verification for " + datasourceName);
		}
	}

	public boolean isInitialize() {
	    return connectionPool != null;
	}

	private boolean isAlive() {

		try {
			LDAPConnection connection = getConnection();

			if (connection == null) {
				return false;
			}

			try {
				connection.disconnect();
			} finally {
				closeConnection(connection);
			}
			return true;
		} catch (LDAPException e) {
			LogManager.ignoreTrace(e);
			return false;
		}
	}

	public String getDSStatus() {

		if (isInitialize() == false) {
			return NOT_INITIALIZED;
		}

		if (isAlive()) {
			return ALIVE;
		}

		return DEAD;
	}
}
