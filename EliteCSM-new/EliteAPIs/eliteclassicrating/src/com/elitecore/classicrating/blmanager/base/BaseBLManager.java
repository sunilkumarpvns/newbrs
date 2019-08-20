/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on Oct 1, 2008
 *	@author baiju
 *  Last Modified Oct 1, 2008
 */

/*
 * BaseBLManager.java
 * 
 * This class is an abstract class that provides
 * basic methods for starting transaction, ending transaction
 * resetting transaction and also to set the transaction isolation level
 * 
 */

package com.elitecore.classicrating.blmanager.base;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.elitecore.classicrating.base.IRatingAppContext;
import com.elitecore.classicrating.commons.util.logger.Log4jLogger;
import com.elitecore.classicrating.datasource.IRatingDBConnection;
import com.elitecore.classicrating.datasource.IRatingDataSource;
import com.elitecore.classicrating.datasource.ITransactionContext;

public abstract class BaseBLManager {

	private IRatingDataSource ratingDataSource;

	private IRatingDBConnection dbConnection;

	private boolean isTransactionStarted;

	private boolean isExternalTransaction;

	private IRatingAppContext ratingAppContext;

	private ITransactionContext transactionContext;

	private static final String MODULE = "BASE_BL";

	protected Log4jLogger Logger;
	
	public BaseBLManager(IRatingAppContext ratingAppContext) {
		this.ratingAppContext = ratingAppContext;
		isExternalTransaction = false;
		Logger = Log4jLogger.getInstance();
	}

	public BaseBLManager(IRatingAppContext ratingAppContext, ITransactionContext transactionContext) {
		this.ratingAppContext = ratingAppContext;
		this.transactionContext = transactionContext;
		isExternalTransaction = true;
		Logger = Log4jLogger.getInstance();		
	}

	protected final synchronized void startTransaction() throws SQLException {
		
		//Logger.logInfo(MODULE, "Starting transaction..");
		if (!isExternalTransaction) {
			if (!isTransactionStarted) {
				this.ratingDataSource = ratingAppContext.getDataSourceProvider().getRatingDataSource();
				dbConnection = ratingDataSource.getConnection();
				dbConnection.setAutoCommit(false);
				isTransactionStarted = true;
				
				//Logger.logInfo(MODULE, "Creating transaction context");

				transactionContext = new ITransactionContext() {

					private boolean isRollbackOnly;

					public Statement createStatement() throws SQLException {
						return dbConnection.createStatement();
					}

					public PreparedStatement prepareStatement(String strQuery) throws SQLException {
						return dbConnection.prepareStatement(strQuery);
					}

					public void setRollbackOnly() {
						isRollbackOnly = true;
					}

					public boolean isRollbackOnly() {
						return isRollbackOnly;
					}

					public void closePreparedStatement(PreparedStatement pStmt) throws SQLException {
						dbConnection.closePreparedStatement(pStmt);

					}

					public void closePreparedStatement(PreparedStatement pStmt, String key) throws SQLException {
						dbConnection.closePreparedStatement(pStmt, key);

					}

					public void closeStatement(Statement stmt) throws SQLException {
						dbConnection.closeStatement(stmt);

					}

					public PreparedStatement prepareStatement(String query, String key) throws SQLException {
						return dbConnection.prepareStatement(query, key);
					}
				};

			}
		} else {
			Logger.trace(MODULE, "External Transaction exists");
		}
	}

	public final ITransactionContext getTransactionContext() {
		return transactionContext;
	}

	protected final synchronized void endTransaction() throws SQLException {
		
		//Logger.logInfo(MODULE, "Ending transaction..");
		if (!isExternalTransaction) {
			if (isTransactionStarted) {
				if (!transactionContext.isRollbackOnly()) {
					dbConnection.commit();
				} else {
					dbConnection.rollback();
				}
				dbConnection.close();
				isTransactionStarted = !isTransactionStarted;
			}
		}
	}

	public final synchronized void resetTransaction() throws SQLException {
		if (!isExternalTransaction) {
			if (isTransactionStarted) {
				dbConnection.rollback();
				dbConnection.close();
				isTransactionStarted = !isTransactionStarted;
			}
		}
		isExternalTransaction = false;
	}

	protected final void setTransactionIsolation(int level) throws SQLException {
		if (isTransactionStarted) {
			dbConnection.setTransactionIsolation(level);
		}
	}

	public final IRatingAppContext getRatingAppContext() {
		return this.ratingAppContext;
	}
	
}
