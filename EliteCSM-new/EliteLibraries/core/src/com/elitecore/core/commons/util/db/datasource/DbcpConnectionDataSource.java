package com.elitecore.core.commons.util.db.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.dbcp2.BasicDataSource;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.core.commons.util.db.DBVendors;
import com.elitecore.core.commons.util.db.DataSourceException;

/**
 * 
 * @author Malav Desai
 *
 */
public class DbcpConnectionDataSource implements IConnectionDataSource {
	
	private static final boolean DEFAULT_AUTO_COMMIT = false;
	private static final int REMOVE_ABANDOAND_TIMEOUT_IN_SECS = 60;
	private static final int MAX_OPEN_PREPARED_STATMENTS = 500;
	private static final int MIN_EVICTABLE_TIMES_IN_MS = 60 * 60 * 1000;
	private static final int TIME_BETWEEN_EVICTION_RUNS_IN_MS = 60 * 60 * 1000;
	private static final int VALIDATION_QUERY_TIMEOUT_IN_SECS = 1;
	private static final int MAX_WAIT_TIME_IN_MILLI_SECS = 1000;
	private final String url;
	private final String username;
	private final String password;
	private final int minPoolSize;
	private final int maxPoolSize;
	private final Map<String, String> additionalProperites;
	private final DBVendors vendor;
	
	private BasicDataSource dataSource; 

	public DbcpConnectionDataSource(String url, String username,
			String password, int minPoolSize, int maxPoolSize,
			Map<String, String> properties, DBVendors vendor) {
		
		this.url = url;
		this.username = username;
		this.password = password;
		this.minPoolSize = minPoolSize;
		this.maxPoolSize = maxPoolSize;
		this.additionalProperites = properties;
		this.vendor = vendor;
	}

	@Override
	public void init() throws DataSourceException {
		
		if (dataSource != null && dataSource.isClosed() == false) {
			try {
				dataSource.close();
			} catch (SQLException e) {
				throw DataSourceException.newException(e, vendor);
			}
		}
		
		dataSource = new BasicDataSource();
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setUrl(url);
		dataSource.setDriverClassName(vendor.driverClassName);
		dataSource.setDefaultAutoCommit(DEFAULT_AUTO_COMMIT);
		
		dataSource.setInitialSize(minPoolSize);
		dataSource.setMaxTotal(maxPoolSize);
		dataSource.setMaxIdle(minPoolSize);
		dataSource.setMinIdle(0);
		dataSource.setMaxWaitMillis(MAX_WAIT_TIME_IN_MILLI_SECS);
        
		dataSource.setValidationQuery(vendor.validationQuery);
		dataSource.setValidationQueryTimeout(VALIDATION_QUERY_TIMEOUT_IN_SECS);
		dataSource.setTestOnBorrow(false);
		dataSource.setTestOnReturn(false);
		dataSource.setTestWhileIdle(true);
		dataSource.setTimeBetweenEvictionRunsMillis(TIME_BETWEEN_EVICTION_RUNS_IN_MS);
		dataSource.setNumTestsPerEvictionRun(minPoolSize);
		dataSource.setMinEvictableIdleTimeMillis(MIN_EVICTABLE_TIMES_IN_MS);
        
		dataSource.setPoolPreparedStatements(true);
		dataSource.setMaxOpenPreparedStatements(MAX_OPEN_PREPARED_STATMENTS);
		
		dataSource.setRemoveAbandonedOnBorrow(true);
		dataSource.setRemoveAbandonedTimeout(REMOVE_ABANDOAND_TIMEOUT_IN_SECS);
		dataSource.setLogAbandoned(true);
		
		if(this.additionalProperites != null){
			for (Entry<String, String> property : additionalProperites.entrySet()) {
					dataSource.addConnectionProperty(property.getKey(), String.valueOf(property.getValue()));
			}
		}
		
		Connection connection = null;
		try{
			connection = getConnection();
		} finally {
			DBUtility.closeQuietly(connection);
		}
	}

	@Override
	public Connection getConnection() throws DataSourceException {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw DataSourceException.newException(e, vendor);
		}
	}
	
	@Override
	public int getPoolSize() {
		return maxPoolSize;
	}

	@Override
	public int getNumberOfActiveConnections() {
		return dataSource.getNumActive();
	}

	@Override
	public void close() throws SQLException {
		dataSource.close();
	}

	@Override
	public DBVendors getVendor() {
		return vendor;
	}

	@Override
	public boolean isDBDownSQLException(SQLException ex) {
		return vendor.isDBDownSQLException(ex);
	}
	
}
