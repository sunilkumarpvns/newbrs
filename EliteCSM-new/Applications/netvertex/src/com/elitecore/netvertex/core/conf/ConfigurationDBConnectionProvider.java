package com.elitecore.netvertex.core.conf;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import com.elitecore.netvertex.core.NetVertexDBConnectionManager;

public class ConfigurationDBConnectionProvider implements ConnectionProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean isUnwrappableAs(Class arg0) {
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> arg0) {
		return null;
	}

	@Override
	public void closeConnection(Connection connection) throws SQLException {
		connection.close();
	}

	@Override
	public Connection getConnection() throws SQLException {
		return NetVertexDBConnectionManager.getInstance().getConnection();
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return false;
	}
	
}
