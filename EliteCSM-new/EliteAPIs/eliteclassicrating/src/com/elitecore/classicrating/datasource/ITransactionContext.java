/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 1, 2008
 *	@author baiju
 *      Last Modified October 1, 2008
 */

/*
 * ITransactionContext.java
 * 
 * This interface is used to provide an ITransactionContext instance with the methods to 
 * manipulate the transaction context
 * 
 */
package com.elitecore.classicrating.datasource;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public interface ITransactionContext {

	public void setRollbackOnly();

	public boolean isRollbackOnly();

	public PreparedStatement prepareStatement(String query) throws SQLException;

	public PreparedStatement prepareStatement(String query, String key) throws SQLException;

	public Statement createStatement() throws SQLException;

	public void closePreparedStatement(PreparedStatement pStmt) throws SQLException;

	public void closePreparedStatement(PreparedStatement pStmt, String key) throws SQLException;

	public void closeStatement(Statement stmt) throws SQLException;

}
