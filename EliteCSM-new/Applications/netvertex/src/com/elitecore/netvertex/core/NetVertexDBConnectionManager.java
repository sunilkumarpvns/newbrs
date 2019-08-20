package com.elitecore.netvertex.core;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.netvertex.EliteNetVertexServer;
import com.elitecore.netvertex.core.conf.impl.DBDataSourceImpl;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.SQLException;

public class NetVertexDBConnectionManager {
	
	//FIXME need to remove when configuration for same provided
	private static final int NETWORK_READ_TIMEOUT = 60*1000;
	private static final String MODULE = "NV-DBM";
	private static final int DEFAULT_MIN_POOL =1;
	private static final int DEFAULT_MAX_POOL =20;
	
	private static NetVertexDBConnectionManager netVertexDBConnectionManager;
	
	static {
		netVertexDBConnectionManager = new NetVertexDBConnectionManager();
	}

	private DBDataSource dataSource;

	public void init(@Nonnull DBDataSource dbDataSource, @Nonnull TaskScheduler taskScheduler) {
		String connectionUrl = dbDataSource.getConnectionURL();
		String userName = dbDataSource.getUsername();
		String password = dbDataSource.getPassword();
		int minPoolSize =dbDataSource.getMinimumPoolSize();
		minPoolSize = minPoolSize > 0 ? minPoolSize : DEFAULT_MIN_POOL;
		int maxPoolSize =dbDataSource.getMaximumPoolSize();
		maxPoolSize = maxPoolSize > 0 ? maxPoolSize : DEFAULT_MAX_POOL;
		
		dataSource = new DBDataSourceImpl(EliteNetVertexServer.DS_ID,EliteNetVertexServer.DS_NAME, connectionUrl, userName, password, minPoolSize, maxPoolSize, DBDataSourceImpl.DEFAULT_STATUS_CHECK_DURATION,NETWORK_READ_TIMEOUT, CommonConstants.QUERY_TIMEOUT_SEC);
		
		try{
			DBConnectionManager.getInstance(dataSource.getDataSourceName()).init(dataSource, taskScheduler);
			LogManager.getLogger().debug(MODULE,"Initializing database connection manager completed");
		}catch (DatabaseInitializationException e) {
			LogManager.getLogger().trace(MODULE,"Datasource initialization Failed : "+e.getMessage());
			LogManager.ignoreTrace(e);
		}catch (DatabaseTypeNotSupportedException e) {
			LogManager.getLogger().trace(MODULE,"Datasource Type is not Supported : "+e.getMessage());
			LogManager.ignoreTrace(e);
		}
	}
	
	public DBDataSource getDataSource(){
		return dataSource;
	}

	public static NetVertexDBConnectionManager getInstance(){
		return netVertexDBConnectionManager;
	}
	
	public Connection getConnection() throws DataSourceException {
		return DBConnectionManager.getInstance(dataSource.getDataSourceName()).getConnection();
	}
	
	public TransactionFactory getTransactionFactory(){
		return DBConnectionManager.getInstance(dataSource.getDataSourceName()).getTransactionFactory();
	}
	
	public boolean isDBDownSQLException(SQLException ex){
		return DBConnectionManager.getInstance(dataSource.getDataSourceName()).isDBDownSQLException(ex);
	}
}
