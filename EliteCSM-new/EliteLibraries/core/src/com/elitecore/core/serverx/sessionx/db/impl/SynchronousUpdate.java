package com.elitecore.core.serverx.sessionx.db.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.utilx.db.Transaction;
import com.elitecore.core.commons.utilx.db.TransactionException;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.commons.utilx.db.TransactionFactoryGroupImpl;
import com.elitecore.core.serverx.sessionx.Criteria;
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
class SynchronousUpdate implements UpdateOperation {
	
	private static final String MODULE = "SYNCHRONOUS-UPDATE-OPERATION";
	
	private final TransactionFactoryGroupImpl transactionFactoryGroup;
	private final long highQueryResponseTime;
	private final SQLDialect dialect;
	private final SystemPropertiesProvider systemPropertiesProvider;
	
	public SynchronousUpdate(TransactionFactoryGroupImpl transactionFactoryGroup, 
			SystemPropertiesProvider systemPropertiesProvider, int highQueryResponseTime, SQLDialect dialect) {
		this.transactionFactoryGroup = transactionFactoryGroup;
		this.highQueryResponseTime = highQueryResponseTime;
		this.dialect = dialect;
		this.systemPropertiesProvider = systemPropertiesProvider;
	}

	@Override
	public int execute(SessionData sessionData) {
		int updateStatus = SessionResultCode.FAILURE.code;
		TransactionFactory transactionFactory = transactionFactoryGroup.getTransactionFactory();
		if (transactionFactory == null) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().error(MODULE, "Error in saving session. Reason: no alive datasource found");
			}
			return SessionResultCode.FAILURE.code;
		}
		Transaction newTransaction = transactionFactory.createTransaction();
		try {

			if (systemPropertiesProvider.isNoWaitEnabled()) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Set NOWAIT for Update Operation");						
				}																																				
				newTransaction.setNoWait();
			}

			if (systemPropertiesProvider.isBatchEnabled()) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Set BATCH for Update Operation");						
				}																																				
				newTransaction.setBatch();
			}			

			newTransaction.begin();
			updateStatus = update(sessionData,newTransaction);
		} catch (TransactionException te) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().error(MODULE, "Error in updating session, Reason: " + te.getMessage());
			}
			LogManager.getLogger().trace(MODULE, te);

		} catch (SessionException se) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().error(MODULE, "Error in updating session, Reason: " + se.getMessage());
			}
			LogManager.getLogger().trace(MODULE, se);

		} finally {
			try {
				newTransaction.end();
			} catch (TransactionException e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
					LogManager.getLogger().error(MODULE, "Error in ending transaction, Reason: " + e.getMessage());
				}
				LogManager.getLogger().trace(MODULE, e);

			}
		}
		return updateStatus;
	}

	@Override
	public int execute(SessionData sessionData, Criteria criteria) {
		int updateStatus = SessionResultCode.FAILURE.code;
		TransactionFactory transactionFactory = transactionFactoryGroup.getTransactionFactory();
		if (transactionFactory == null) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().error(MODULE, "Error in updating session. Reason: no alive datasource found");
			}
			return SessionResultCode.FAILURE.code;
		}

		Transaction newTransaction = transactionFactory.createTransaction();
		try {
			if (systemPropertiesProvider.isNoWaitEnabled()) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Set NOWAIT for Update Operation");						
				}																														
				newTransaction.setNoWait();
			}

			if (systemPropertiesProvider.isBatchEnabled()) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Set BATCH for Update Operation");						
				}																																				
				newTransaction.setBatch();
			}					
			newTransaction.begin();
			updateStatus = update(sessionData,criteria,newTransaction);
		} catch (TransactionException te) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().error(MODULE, "Error in updating session, Reason: " + te.getMessage());
			}
			LogManager.getLogger().trace(MODULE, te);

		} catch (SessionException se) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().error(MODULE, "Error in updating session, Reason: " + se.getMessage());
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
		return updateStatus;
	}
	
	private int update(SessionData sessionData,Transaction currentTransaction) throws SessionException {
		PreparedStatement ps = null;
		int result = SessionResultCode.FAILURE.code;
		try {
			String sqlQuery = dialect.getUpdateQuery(sessionData);
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Executing update statement: " + sqlQuery);
			}
			ps = currentTransaction.prepareStatement(sqlQuery);
			ps.setQueryTimeout(systemPropertiesProvider.getQueryTimeout());
			dialect.setPreparedStatementForUpdate(sessionData, ps);
			long queryExecutionTime = 0;
			queryExecutionTime = System.currentTimeMillis();
			
			result = ps.executeUpdate();
			queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
			if (highQueryResponseTime < queryExecutionTime) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "Session Management Query execution time getting high for update operation, - Last Query execution time = " + queryExecutionTime + " milliseconds.");
				}
			}
		} catch (TransactionException transactionException) {
			throw new SessionException("Error in update operation",transactionException);
		} catch (SQLException e) {
			if (currentTransaction != null) {
				if (currentTransaction.isDBDownSQLException(e)) {
					currentTransaction.markDead();
				}
			}
			throw new SessionException("Error in update operation, Reason: " + e.getMessage(),e);
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
						LogManager.getLogger().error(MODULE, "Error in closing prepared Statement, Reason: " + e.getMessage());
					}
					LogManager.getLogger().trace(MODULE, e);
				}
			}
		}
		return result;
	}
	
	private int update(SessionData sessionData,Criteria criteria,Transaction currentTransaction) throws SessionException {
		PreparedStatement ps = null;
		int result = SessionResultCode.FAILURE.code;
		try {
			String sqlQuery = dialect.getUpdateQuery(sessionData,criteria);
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Executing update statement: " + sqlQuery);
			}
			ps = currentTransaction.prepareStatement(sqlQuery);
			ps.setQueryTimeout(systemPropertiesProvider.getQueryTimeout());
			dialect.setPreparedStatementForUpdate(sessionData,criteria, ps);
			long queryExecutionTime = 0;
			queryExecutionTime = System.currentTimeMillis();
			
			result = ps.executeUpdate();
			queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
			if (highQueryResponseTime < queryExecutionTime) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "Session Management Query execution time getting high for " +
							"update operation, - Last Query execution time = " + queryExecutionTime + " milliseconds.");
				}
			}
		} catch (TransactionException transactionException) {
			throw new SessionException("Error in update operation",transactionException);
		} catch (SQLException e) {
			if (currentTransaction != null) {
				if (currentTransaction.isDBDownSQLException(e)) {
					currentTransaction.markDead();
				}
			}
			throw new SessionException("Error in update operation, Reason: " + e.getMessage(),e);
		} finally {
			if (ps != null) {
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
