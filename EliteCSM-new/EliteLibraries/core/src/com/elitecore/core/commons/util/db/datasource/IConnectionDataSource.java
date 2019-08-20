package com.elitecore.core.commons.util.db.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import com.elitecore.core.commons.util.db.DBVendors;
import com.elitecore.core.commons.util.db.DataSourceException;

public interface IConnectionDataSource {
	
	public void init() throws DataSourceException;
	
	/**
	 * 
	 * @return
	 * @throws DataSourceException
	 */
	public DBVendors getVendor();
	public Connection getConnection() throws DataSourceException;
	
	public int getPoolSize();
	public int getNumberOfActiveConnections();
	public void close() throws SQLException;
	public boolean isDBDownSQLException(SQLException ex);
}
