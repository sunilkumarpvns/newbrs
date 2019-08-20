/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on Oct 1, 2008
 *	@author baiju
 *      Last Modified Oct 1, 2008
 */

/*
 * IRatingDBConnection.java
 * 
 * This interface declares a method that returns an instance of connection with its related methods
 * to create statements, preparedstatements . 
 * 
 */

package com.elitecore.classicrating.datasource;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public interface IRatingDBConnection {

	public PreparedStatement prepareStatement(String query) throws SQLException;

	public PreparedStatement prepareStatement(String query, String key) throws SQLException;

	public Statement createStatement() throws SQLException;

	public void closePreparedStatement(PreparedStatement pStmt) throws SQLException;

	public void closePreparedStatement(PreparedStatement pStmt, String key) throws SQLException;

	public void closeStatement(Statement stmt) throws SQLException;

	public void close() throws SQLException;

	public void setAutoCommit(boolean autoCommit) throws SQLException;

	public void commit() throws SQLException;

	public void rollback() throws SQLException;

	public void setTransactionIsolation(int level) throws SQLException;
}
