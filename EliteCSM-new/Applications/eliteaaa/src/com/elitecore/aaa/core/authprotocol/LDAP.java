package com.elitecore.aaa.core.authprotocol;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;

import com.elitecore.aaa.core.authprotocol.exception.InvalidPasswordException;
import com.elitecore.aaa.core.conf.LDAPAuthDriverConfiguration;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.utilx.ldap.LDAPConnectionManager;

/**
 * 
 * @author narendra.pathai
 *
 */
public class LDAP {

	private static final String MODULE = "LDAP";

	public static void verifyPassword(DriverConfiguration ldapDriverConfiguration, String dn, String decodedPassword) throws InvalidPasswordException{
		if(ldapDriverConfiguration == null){
			throw new NullPointerException("LDAP Driver configuration not found");
		}
		
		LDAPAuthDriverConfiguration ldapAuthDriverConfiguration = (LDAPAuthDriverConfiguration) ldapDriverConfiguration;
		String ldapDatasourceName = ldapAuthDriverConfiguration.getDSName();

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Performing LDAP bind authentication for DN: " + dn);
		}
		LDAPConnection ldapConnection = null;
		try{
			ldapConnection = LDAPConnectionManager.getInstance(ldapDatasourceName).getConnection();
			ldapConnection.bind(dn, decodedPassword);
		}catch (LDAPException e) {
			LogManager.getLogger().trace(e);
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "LDAP bind password verification failed for DN: " + dn + " due to reason: " + e.errorCodeToString());
			}
			throw new InvalidPasswordException("Invalid Password.");
		}finally{
			if(ldapConnection != null){
				/*
				 * On closing this connection the API does not send an UNBIND to LDAP but instead it rebinds
				 * on the same connection using the ADMIN credentials.
				 */
				LDAPConnectionManager.getInstance(ldapDatasourceName).closeConnection(ldapConnection);
			}
		}
	}
}
