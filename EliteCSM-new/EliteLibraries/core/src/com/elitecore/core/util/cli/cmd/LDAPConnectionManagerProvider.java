package com.elitecore.core.util.cli.cmd;

import com.elitecore.core.commons.utilx.ldap.LDAPConnectionManager;

public interface LDAPConnectionManagerProvider {
	LDAPConnectionManager getConnectionManager(String dsName);
}
