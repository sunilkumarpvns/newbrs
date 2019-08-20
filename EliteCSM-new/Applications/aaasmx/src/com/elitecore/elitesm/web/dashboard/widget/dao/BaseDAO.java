package com.elitecore.elitesm.web.dashboard.widget.dao;

import java.sql.Connection;

import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.elitesm.blmanager.dashboard.DashboardBLManager;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.web.core.system.dashboardconfiguration.DashboardConfigData;
import com.elitecore.elitesm.web.core.system.db.datasource.DBDataSourceImpl;

public class BaseDAO {
	
	protected Connection getConnection() throws DataSourceException, DataManagerException, DatabaseInitializationException, DatabaseTypeNotSupportedException {
	
		DashboardBLManager dashboardBLManager = new DashboardBLManager();
		DashboardConfigData dashboardConfigData = new DashboardConfigData();
		dashboardConfigData=dashboardBLManager.getDashboardConfiguration();
		DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
		IDatabaseDSData databaseDsData=databaseDSBLManager.getDatabaseDSDataById(dashboardConfigData.getDatabaseId());
		
		DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance(databaseDsData.getName());
		
		//check for dbConnectionManager is Initilized or not
		if(dbConnectionManager.isInitilized() == false){
			DBDataSourceImpl dbDataSourceImpl = new  DBDataSourceImpl();
			dbDataSourceImpl.setDatasourceID(databaseDsData.getName());
			dbDataSourceImpl.setDataSourceName(databaseDsData.getName());
			dbDataSourceImpl.setConnectionURL(databaseDsData.getConnectionUrl());
			dbDataSourceImpl.setPassword(databaseDsData.getPassword());
			dbDataSourceImpl.setMinimumPoolSize(Long.valueOf(databaseDsData.getMinimumPool()).intValue());
			dbDataSourceImpl.setMaximumPoolSize(Long.valueOf(databaseDsData.getMaximumPool()).intValue());
			dbDataSourceImpl.setUsername(databaseDsData.getUserName());

			dbConnectionManager.init(dbDataSourceImpl, null);
		} 

		return dbConnectionManager.getConnection();
	}
}
