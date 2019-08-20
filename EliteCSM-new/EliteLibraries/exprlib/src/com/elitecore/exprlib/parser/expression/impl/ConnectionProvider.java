package com.elitecore.exprlib.parser.expression.impl;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionProvider {
	public Connection getConnection(String dbSourceName) throws SQLException ;
}
