package com.elite.auth.ldap;


import java.util.HashMap;

import org.apache.log4j.Logger;

import netscape.ldap.LDAPException;
import netscape.ldap.util.ConnectionPool;

import com.elite.config.LDAPConfig;
import com.elite.context.Context_Manager;
import com.elite.context.WSCContext;
import com.elite.exception.CommunicationException;
import com.elite.exception.ContextManagerNotInitialisedException;
import com.opensymphony.xwork2.ActionContext;

public class LDAPConnectionManager {
	private static LDAPConnectionManager ldapconnectionmanager = new LDAPConnectionManager();
	private HashMap ldapConnectionpoolMap = new HashMap();
	public static ConnectionPool getInstance(String dataSourceKey)throws Exception
	{
		ConnectionPool ldapConnectionpoolInstance = (ConnectionPool)ldapconnectionmanager.ldapConnectionpoolMap.get(dataSourceKey);
		if (ldapConnectionpoolInstance == null) {
			
			synchronized (LDAPConnectionManager.class) {
				//Following check is required, do not remove it.
				ldapConnectionpoolInstance = (ConnectionPool)ldapconnectionmanager.ldapConnectionpoolMap.get(dataSourceKey);
				if (ldapConnectionpoolInstance == null) {
					ldapConnectionpoolInstance = new LDAPConnectionManager().createLDAPConnectionPool();
					ldapconnectionmanager.ldapConnectionpoolMap.put(dataSourceKey, ldapConnectionpoolInstance);
				}
			}
		}
		return ldapConnectionpoolInstance;
	}
	
	public ConnectionPool createLDAPConnectionPool() throws CommunicationException {
		ConnectionPool connectionPool = null;
		try 
		{
			LDAPConfig ldap_config = (LDAPConfig)((WSCContext)ActionContext.getContext().getApplication().get("wsc_context")).getAttribute("ldap");
			ldap_config.validate();
			connectionPool = new ConnectionPool(ldap_config.getMin_con(),ldap_config.getMax_con(),ldap_config.getHost_ip(), ldap_config.getPort(), ldap_config.getAdministrator(), ldap_config.getPassword());
		
		}catch(LDAPException e){
			throw new CommunicationException("LDAP Exceptin in loading connection pool, reason: " + e.getMessage());
		}catch(Exception exp){
			throw new CommunicationException("LDAP Exception in loading connection pool, reason: " + exp.getMessage());
		}
		return connectionPool;
   }

	
	
}
