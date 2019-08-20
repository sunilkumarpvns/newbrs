package com.elitecore.netvertex.core.conf;

import java.util.Map;

import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.corenetvertex.core.db.DBDataSource;
import com.elitecore.netvertex.core.conf.impl.DBDataSourceImpl;

/**
 * This interface provide the DB configuration information
 * @author Harsh Patel
 *
 */

public interface DatabaseDSConfiguration {
	
		/**
		 * This method return the datasource 
		 * @param dsID
		 * @return DBDataSource
		 */
		public DBDataSource getDatasource(String dsID);
		
		/**
		 * This method returns map which maps the id with datasource
		 * @return Map
		 */
		public Map<String,DBDataSourceImpl> getDatasourceMap();
		
		/**
		 * This method reloads the configuration
		 * @throws LoadConfigurationException
		 */
		public void reloadConfiguration() throws LoadConfigurationException;
		
		/**
		 * This method returns the datasource
		 * @param datasourceName
		 * @return DBDatasource
		 */
		public DBDataSource getDatasourceByName(String datasourceName); 
		
		/**
		 * This method returns map which maps the name with datasource
		 * @return Map
		 */
		public Map<String, DBDataSourceImpl> getDatasourceNameMap();
	}
