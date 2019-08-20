package com.elitecore.aaa.exprlib.function;

import java.sql.Connection;
import java.sql.SQLException;

import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.exprlib.parser.expression.impl.ConnectionProvider;

public class ConnectionProviderImpl implements ConnectionProvider {

	@Override
	public Connection getConnection(String dbSourceName) throws SQLException {
		return DBConnectionManager.getInstance(dbSourceName).getConnection();
	}
}
