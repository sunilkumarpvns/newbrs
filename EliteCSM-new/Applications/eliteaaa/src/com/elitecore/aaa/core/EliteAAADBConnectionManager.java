package com.elitecore.aaa.core;

import java.sql.Connection;

import com.elitecore.aaa.core.config.DBDataSourceImpl;
import com.elitecore.aaa.core.config.DataSourceDetail;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.EliteDBConnectionProperty;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.systemx.esix.TaskScheduler;

public class EliteAAADBConnectionManager {

	private static final String MODULE = "AAADB-CONN-MGR";
	private static final int DEFAULT_MIN_POOL_SIZE = 1;
	private static final int DEFAUL_MAX_POOL_SIZE = 5;
	private static final int DEFAULT_STATUS_CHECK_DURATION = 5;
	public static final String ELITE_AAADB_CACHE = "ELITE_AAADB_CACHE";
	
	private static EliteAAADBConnectionManager eliteAAADBConnectionManager;
	private DBConnectionManager connectionManager;

	private DataSourceDetail datasourceDetail;
	private TaskScheduler taskScheduler;

	private EliteAAADBConnectionManager() {
		connectionManager = DBConnectionManager.getInstance(ELITE_AAADB_CACHE); 
	}

	public void init(DataSourceDetail datasourceDetail, TaskScheduler taskScheduler) throws DatabaseInitializationException, DatabaseTypeNotSupportedException {
		this.datasourceDetail = datasourceDetail;
		this.taskScheduler = taskScheduler;
		initDataSource();
	}

	private void initDataSource() throws DatabaseInitializationException, DatabaseTypeNotSupportedException {
		DBDataSource dataSource = new DBDataSourceImpl(ELITE_AAADB_CACHE, ELITE_AAADB_CACHE, datasourceDetail.getConnectionUrlForAAADB(), datasourceDetail.getUsernameForAAADB(), datasourceDetail.getPassword(), DEFAULT_MIN_POOL_SIZE, DEFAUL_MAX_POOL_SIZE, DEFAULT_STATUS_CHECK_DURATION,EliteDBConnectionProperty.NETWORK_READ_TIMEOUT.defaultValue);
		connectionManager.init(dataSource, taskScheduler);
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "AAA database datasource initialized successfully using connection-url: " + datasourceDetail.getConnectionUrlForAAADB());
		}
	}

	static {
		eliteAAADBConnectionManager = new EliteAAADBConnectionManager();
	}
	public static EliteAAADBConnectionManager getInstance(){
		return eliteAAADBConnectionManager;
	}

	public Connection getConnection() throws DataSourceException{
		return connectionManager.getConnection();
	}
	
}