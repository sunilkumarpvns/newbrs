package com.elitecore.core.serverx.sessionx.db.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.utilx.db.Transaction;
import com.elitecore.core.commons.utilx.db.TransactionException;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.commons.utilx.db.TransactionFactoryGroupImpl;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.SessionResultCode;
import com.elitecore.core.serverx.sessionx.SystemPropertiesProvider;
import com.elitecore.core.serverx.sessionx.db.SQLDialect;

/**
 * 
 * @author malav.desai
 *
 */

class SynchronousSave implements SaveOperation {
	
	private static final String MODULE = "SYNCHRONOUS-SAVE-OPERATION";
	
	private final TransactionFactoryGroupImpl transactionFactoryGroup;
	private final long highQueryResponseTime;
	private final SQLDialect dialect;
	private final SystemPropertiesProvider systemPropertiesProvider;
	
	public SynchronousSave(TransactionFactoryGroupImpl transactionFactoryGroup, 
			 SystemPropertiesProvider systemPropertiesProvider, long highQueryResponseTime, SQLDialect dialect) {
		this.transactionFactoryGroup = transactionFactoryGroup;
		this.highQueryResponseTime = highQueryResponseTime;
		this.dialect = dialect;
		this.systemPropertiesProvider = systemPropertiesProvider;
	}

	@Override
	public int execute(SessionData sessionData) {
		int saveStatus = SessionResultCode.FAILURE.code;
		TransactionFactory transactionFactory = transactionFactoryGroup.getTransactionFactory();
		if(transactionFactory == null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().error(MODULE, "Error in saving session. Reason: no alive datasource found");
			}
			return saveStatus;
		}

		Transaction newTransaction = transactionFactory.createTransaction();
		try {

			if(systemPropertiesProvider.isNoWaitEnabled()) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Set NOWAIT for save Operation");						
				}																		
				newTransaction.setNoWait();
			}

			if(systemPropertiesProvider.isBatchEnabled()) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Set BATCH for save Operation");						
				}																								
				newTransaction.setBatch();
			}

			newTransaction.begin();
			saveStatus = save(sessionData,newTransaction);
		} catch(TransactionException te) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().error(MODULE, "Error in saving session, Reason: " + te.getMessage());
			}
			LogManager.getLogger().trace(MODULE, te);


		} catch(SessionException se) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().error(MODULE, "Error in saving session, Reason: " + se.getMessage());
			}
			LogManager.getLogger().trace(MODULE, se);

		} finally {
			try {
				newTransaction.end();
			} catch (TransactionException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
					LogManager.getLogger().error(MODULE, "Error in ending transaction, Reason: " + e.getMessage());
				}
				LogManager.getLogger().trace(MODULE, e);
			}
		}
		return saveStatus;
	}
	
	private int save(SessionData sessionData,Transaction currentTransaction) throws SessionException {
		PreparedStatement ps = null;
		int result = SessionResultCode.FAILURE.code;
		try {
			String sqlQuery = dialect.getInsertQuery(sessionData);
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Executing insert statement: " + sqlQuery);
			}
			ps = currentTransaction.prepareStatement(sqlQuery);
			ps.setQueryTimeout(systemPropertiesProvider.getQueryTimeout());
			dialect.setPreparedStatementForInsert(sessionData, ps);
			long queryExecutionTime = 0;
			queryExecutionTime = System.currentTimeMillis();
			result = ps.executeUpdate();
			queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
			if(highQueryResponseTime < queryExecutionTime) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "Session Management Query execution time getting high for save operation, - Last Query execution time = " + queryExecutionTime + " milliseconds.");
				}
			}

		} catch(TransactionException transactionException) {
			throw new SessionException("Error in save operation",transactionException);
		} catch (SQLException e) {
			if(currentTransaction != null) {
				if(currentTransaction.isDBDownSQLException(e)) {
					currentTransaction.markDead();
				}
			}
			throw new SessionException("Error in save operation, Reason: " + e.getMessage(),e);
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
						LogManager.getLogger().error(MODULE, "Error in closing prepared Statement, Reason: " + e.getMessage());
					}
					LogManager.getLogger().trace(MODULE, e);
				}
			}
		}
		return result;
	}
	
}
