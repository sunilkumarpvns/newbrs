package com.elitecore.aaa.core.conf;

import java.util.Map;

import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.utilx.ldap.data.LDAPDataSource;

public interface LDAPDSConfiguration {
	public LDAPDataSource getDatasurce(String dsID);
	public Map<String,LDAPDataSource> getDatasourceMap();
	public Map<String, LDAPDataSource> getDatasourceNameMap();
	public LDAPDataSource getDatasourceByName(String dsName);
	public void reloadConfiguration() throws LoadConfigurationException;
}
