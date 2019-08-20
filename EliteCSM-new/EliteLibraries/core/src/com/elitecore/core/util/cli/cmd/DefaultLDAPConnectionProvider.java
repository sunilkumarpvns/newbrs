package com.elitecore.core.util.cli.cmd;

import com.elitecore.core.commons.utilx.ldap.LDAPConnectionManager;

/**
 * Represents system default connection manager that creates connection to remote LDAP systems.
 * 
 * @author Chetan.Sankhala
 *
 */
public class DefaultLDAPConnectionProvider implements LDAPConnectionManagerProvider {

	@Override
	public LDAPConnectionManager getConnectionManager(String dsName) {
		return LDAPConnectionManager.getInstance(dsName);
	}
}
