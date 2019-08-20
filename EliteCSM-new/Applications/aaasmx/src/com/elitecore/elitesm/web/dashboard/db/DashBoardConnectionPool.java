package com.elitecore.elitesm.web.dashboard.db;

import java.sql.Connection;

import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.web.core.system.db.datasource.DBDataSourceImpl;
import com.elitecore.elitesm.ws.logger.Logger;

public class DashBoardConnectionPool {
	private final static String KEY = "DashBoard"; 
	private static final String MODULE = DashBoardConnectionPool.class.getSimpleName(); 
	
	public void init(IDatabaseDSData dbConfiguration) {
		try {
			DBConnectionManager dbConnectionManager =  DBConnectionManager.getInstance(KEY);
			
			if(dbConnectionManager != null) {
				
				try {
					if(dbConnectionManager.isInitilized() == false){
						DBDataSourceImpl dbDataSourceImpl = new  DBDataSourceImpl();
						dbDataSourceImpl.setDatasourceID("DashBoard");
						dbDataSourceImpl.setDataSourceName("DashBoard");
						dbDataSourceImpl.setConnectionURL(dbConfiguration.getConnectionUrl());
						dbDataSourceImpl.setPassword(dbConfiguration.getPassword());
						dbDataSourceImpl.setMinimumPoolSize(Long.valueOf(dbConfiguration.getMinimumPool()).intValue());
						dbDataSourceImpl.setMaximumPoolSize(Long.valueOf(dbConfiguration.getMaximumPool()).intValue());
						dbDataSourceImpl.setUsername(dbConfiguration.getUserName());
						
						dbConnectionManager.init(dbDataSourceImpl, null);
					} else{
						dbConnectionManager.reInit();
					}
					
				} catch (DatabaseInitializationException e) {
					Logger.logError(MODULE, "Datasource initialization failed, reason:"+e.getMessage());
					Logger.logTrace(MODULE, e);
				} catch (DatabaseTypeNotSupportedException e) {
					Logger.logError(MODULE, "Datasource Type is not Supported, reason:"+e.getMessage());
					Logger.logTrace(MODULE, e);
				} catch (Exception e) {
					Logger.logError(MODULE, "Error while initializining data source, reason:"+e.getMessage());
					Logger.logTrace(MODULE, e);
				}
			}
		} catch (Exception e) {
			Logger.logError(MODULE, "Error while init dashboard Connection pool, reason:"+e.getMessage());
			Logger.logTrace(MODULE, e);
		}
	}


	public Connection getConnection() throws DataSourceException {
		return DBConnectionManager.getInstance(KEY).getConnection();
	}
	
		
}
