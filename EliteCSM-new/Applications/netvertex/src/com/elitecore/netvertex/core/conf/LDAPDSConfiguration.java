package com.elitecore.netvertex.core.conf;

import java.util.Map;

import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.corenetvertex.core.ldap.LDAPDataSource;

/**
 * This interface provide the LDAP configuration information
 * @author Harsh Patel
 *
 */
public interface LDAPDSConfiguration {

	/**
	 * This method return the datasource 
	 * @param dsID
	 * @return LDAPDataSource
	 */
	public LDAPDataSource getDatasourceById(String dsID);
	
	/**
	 * This method return the datasource 
	 * @param dsName
	 * @return LDAPDataSource
	 */
	public LDAPDataSource getDatasourceByName(String dsName);
	/**
	 * This method returns map which maps the id with datasource
	 * @return Map
	 */
	public Map<String, LDAPDataSource> getDatasourceMap();
	
	/**
	 * This method returns map which maps the name with datasource
	 * @return Map
	 */
	public Map<String, LDAPDataSource> getDatasourceNameMap();
	
	/**
	 * This method reloads the configuration
	 * @throws LoadConfigurationException
	 */
	public void reloadConfiguration() throws LoadConfigurationException;
}
