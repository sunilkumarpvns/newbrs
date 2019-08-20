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
class SynchronousDelete implements DeleteOperation {
	
	private static final String MODULE = "SYNCHRONOUS-DELETE-OPERATION";
	
	private final TransactionFactoryGroupImpl transactionFactoryGroup;
	private final long highQueryResponseTime;
	private final SQLDialect dialect;
	private final SystemPropertiesProvider systemPropertiesProvider;

	public SynchronousDelete(TransactionFactoryGroupImpl transactionFactoryGroup, 
			SystemPropertiesProvider systemPropertiesProvider, long highQueryResponseTime, SQLDialect dialect) {
		this.transactionFactoryGroup = transactionFactoryGroup;
		this.highQueryResponseTime = highQueryResponseTime;
		this.dialect = dialect;
		this.systemPropertiesProvider = systemPropertiesProvider;
	}

	@Override
	public int execute(SessionData sessionData) {
		int deleteStatus = SessionResultCode.FAILURE.code;
		TransactionFactory transactionFactory = transactionFactoryGroup.getTransactionFactory();
		if (transactionFactory == null) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().error(MODULE, "Error in deleting session. Reason: no alive datasource found");
			}
			return SessionResultCode.FAILURE.code;
		}
		Transaction newTransaction = transactionFactory.createTransaction();
		try {
			if (systemPropertiesProvider.isNoWaitEnabled()) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Set NOWAIT for delete Operation");						
				}																		
				newTransaction.setNoWait();
			}

			if (systemPropertiesProvider.isBatchEnabled()) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Set BATCH for delete Operation");						
				}												
				newTransaction.setBatch();
			}					
			newTransaction.begin();
			deleteStatus = delete(sessionData,newTransaction);
		} catch (TransactionException te) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().error(MODULE, "Error in deleting session, Reason: " + te.getMessage());
			}
			LogManager.getLogger().trace(MODULE, te);
		} catch (SessionException se) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().error(MODULE, "Error in deleting session, Reason: " + se.getMessage());
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
		return deleteStatus;
	}

	@Override
	public int execute(Criteria criteria) {
		int deleteStatus = SessionResultCode.FAILURE.code;
		TransactionFactory transactionFactory = transactionFactoryGroup.getTransactionFactory();
		if (transactionFactory == null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().error(MODULE, "Error in deleting session. Reason: no alive datasource found");
			}
			return SessionResultCode.FAILURE.code;
		}
		Transaction newTransaction = transactionFactory.createTransaction();
		try {

			if (systemPropertiesProvider.isNoWaitEnabled()) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Set NOWAIT for delete Operation");						
				}												
				newTransaction.setNoWait();
			}

			if (systemPropertiesProvider.isBatchEnabled()) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Set BATCH for delete Operation");						
				}																		
				newTransaction.setBatch();
			}

			newTransaction.begin();
			deleteStatus = delete(criteria,newTransaction);
		} catch (TransactionException te) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().error(MODULE, "Error in deleting session, Reason: " + te.getMessage());
			}
			LogManager.getLogger().trace(MODULE, te);
			deleteStatus = SessionResultCode.FAILURE.code;
		} catch (SessionException se) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().error(MODULE, "Error in deleting session, Reason: " + se.getMessage());
			}
			LogManager.getLogger().trace(MODULE, se);
			deleteStatus = SessionResultCode.FAILURE.code;
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
		return deleteStatus;
	}
	
	private int delete(SessionData sessionData,Transaction currentTransaction) throws SessionException {
		PreparedStatement ps = null;
		int result = SessionResultCode.FAILURE.code;
		try {
			String sqlDelQuery = dialect.getDeleteQuery(sessionData);
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Executing prepared statement: " + sqlDelQuery);
			}
			
			ps = currentTransaction.prepareStatement(sqlDelQuery);
			ps.setQueryTimeout(systemPropertiesProvider.getQueryTimeout());
			ps.setString(1, sessionData.getSessionId());
			long queryExecutionTime = 0;
			queryExecutionTime = System.currentTimeMillis();
			
			result = ps.executeUpdate();
			queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
			if (highQueryResponseTime < queryExecutionTime) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "OPEN-DB Query execution time getting high for delete operation, - Last Query execution time = " + queryExecutionTime + " milliseconds.");
				}
			}
		} catch (TransactionException transactionException) {
			throw new SessionException("Error in delete operation",transactionException);
		} catch (SQLException e) {
			if (currentTransaction != null){
				if (currentTransaction.isDBDownSQLException(e)) {
					currentTransaction.markDead();
				}
			}
			throw new SessionException("Error in delete operation, Reason: " + e.getMessage(),e);
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
			} else {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "PreparedStatement is null");
				}
			}
		}
		return result;
	}
	private int delete(Criteria criteria,Transaction currentTransaction) throws SessionException {
		PreparedStatement ps = null;
		int result = SessionResultCode.FAILURE.code;
		try {
			String sqlQuery = dialect.getDeleteQuery(criteria);
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Executing delete statement: " + sqlQuery);
			}
			ps = currentTransaction.prepareStatement(sqlQuery);
			ps.setQueryTimeout(systemPropertiesProvider.getQueryTimeout());
			dialect.setPreparedStatementForDelete(criteria, ps);
			long queryExecutionTime = 0;
			queryExecutionTime = System.currentTimeMillis();
			
			result = ps.executeUpdate();
			queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
			if(highQueryResponseTime < queryExecutionTime) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "Session Management Query execution time getting high for delete operation " +
							", - Last Query execution time = " + queryExecutionTime + " milliseconds.");
				}
			}
		} catch (TransactionException transactionException) {
			throw new SessionException("Error in delete operation",transactionException);
		} catch (SQLException e) {
			if (currentTransaction != null) {
				if (currentTransaction.isDBDownSQLException(e)) {
					currentTransaction.markDead();
				}
			}
			throw new SessionException("Error in delete operation, Reason: " + e.getMessage(),e);
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
			} else {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "PrepareStatemet is null");
				}
			}
		}
		return result;
	}

}
