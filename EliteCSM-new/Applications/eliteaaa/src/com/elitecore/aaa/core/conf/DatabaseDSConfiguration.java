/*
 *  EliteAAA Server
 *
 *  Elitecore Technologies Ltd., 904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 3rd August 2010 by Ezhava Baiju Dhanpal
 *  
 */


package com.elitecore.aaa.core.conf;

import java.util.Map;

import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;

/**
 * 
 * @author Elitecore Technologies Ltd.
 *
 */
public interface DatabaseDSConfiguration {
	public DBDataSource getDataSource(String dsID);
	public DBDataSource getDataSourceByName(String dsName);
	/**
	 * returns the datasource map which maps id with datasource
	 * @return Map
	 */
	public Map<String,DBDataSource> getDatasourceMap();
	/**
	 * 
	 * This method returns datasource map which maps name with the datsource
	 * @return Map
	 */
	public Map<String, DBDataSource> getDatasourceNameMap();
	public void reloadConfiguration() throws LoadConfigurationException;
}
