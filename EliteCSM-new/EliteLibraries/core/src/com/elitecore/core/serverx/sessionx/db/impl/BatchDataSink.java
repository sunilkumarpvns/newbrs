package com.elitecore.core.serverx.sessionx.db.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.utilx.db.Transaction;
import com.elitecore.core.commons.utilx.db.TransactionException;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.commons.utilx.db.TransactionFactoryGroupImpl;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.SystemPropertiesProvider;
import com.elitecore.core.serverx.sessionx.conf.SessionConfiguration;
import com.elitecore.core.serverx.sessionx.db.SQLDialect;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.util.queue.ConcurrentLinkedQueue;

/**
 * 
 * @author malav.desai
 *
 */
class BatchDataSink {
	private static final String MODULE = "BATCH-DATA-SINK";
	private final TransactionFactoryGroupImpl transactionFactoryGroup;
	private final SQLDialect dialect;
	private final ConcurrentLinkedQueue<BatchUpdateData> requestQueue;
	private final HashSet<String> requestsInBatchProcess;
	private final Object requestInBatchLock;
	 
	private long batchUpdateInterval = 10;
	private final int dbQueryTimeout;
	private final long maxBatchSize;
	private final SystemPropertiesProvider systemPropertiesProvider;
	
	public BatchDataSink(TransactionFactoryGroupImpl transactionFactoryGroup, 
			SessionConfiguration sessionConfiguration, SQLDialect dialect,
			@Nonnull TaskScheduler taskScheduler) {
		this.transactionFactoryGroup = transactionFactoryGroup;
		this.dialect = dialect;
		this.batchUpdateInterval = sessionConfiguration.getBatchUpdateInterval();
		this.dbQueryTimeout = sessionConfiguration.getSystemPropertiesProvider().getBatchQueryTimeout();
		this.requestQueue = new ConcurrentLinkedQueue<BatchUpdateData>(10000);
		this.requestsInBatchProcess = new HashSet<String>(5000);
		this.requestInBatchLock = new Object();
		this.maxBatchSize = sessionConfiguration.getMaxBatchSize();
		this.systemPropertiesProvider = sessionConfiguration.getSystemPropertiesProvider();
		
		taskScheduler.scheduleIntervalBasedTask(new BatchExecutionTask());
	}
	
	static class BatchUpdateData {
		public static final  int INSERT = 1;
		public static final  int UPDATE = 2;
		public static final  int DELETE = 3;		
		private SessionData sessionData;
		private Criteria criteria;
		private int operationType;
		
		public BatchUpdateData(SessionData sessionData, int operationType) {
			this.sessionData = sessionData;
			this.operationType = operationType;
		}

		public BatchUpdateData(Criteria criteria, int operationType) {
			this.criteria = criteria;
			this.operationType = operationType;
		}

		public BatchUpdateData(SessionData sessionData,Criteria criteria, int operationType) {
			this (criteria, operationType);
			this.sessionData = sessionData;
		}

		public SessionData getSessionData() {
			return sessionData;
		}
		
		public Criteria getCriteria() {
			return criteria;
		}

		public int getOperation() {
			return operationType;
		}
	}
	
	public boolean addBatchData(BatchUpdateData batchUpdateData) {
		if (transactionFactoryGroup.isAlive() == false) {
			return false;
		}
		return requestQueue.add(batchUpdateData);
	}
	
	private class BatchExecutionTask extends BaseIntervalBasedTask {

		@Override
		public long getInterval() {
			return batchUpdateInterval;
		}
		@Override
		public void execute(AsyncTaskContext context) {
			executeBatchOperation();			
		}
		
		@Override
		public boolean isFixedDelay() {
			return true;
		}
		
		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.MILLISECONDS;
		}
	}

	public void executeBatchOperation(){

		if (this.requestQueue.isEmpty()) {
			return;
		}

		Transaction transaction = null;
		
		Map<String,PreparedStatement> insertPrepaidStatementMap = new LinkedHashMap<String, PreparedStatement>();
		Map<String,PreparedStatement> updatePrepaidStatementMap = new LinkedHashMap<String, PreparedStatement>();
		Map<String,PreparedStatement> deletePrepaidStatementMap = new LinkedHashMap<String, PreparedStatement>();
		
		
		PreparedStatement psForInsert = null;
		PreparedStatement psForUpdate = null;
		PreparedStatement psForDelete = null;
		
		try{
			TransactionFactory transactionFactory = transactionFactoryGroup.getTransactionFactory();
			if(transactionFactory == null){
				throw new SessionException("No alive DataSource found");
			}
			transaction = transactionFactory.createTransaction();
			
			if(systemPropertiesProvider.isNoWaitEnabled()){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Set NOWAIT for Batch-Insert Operation");
				}
				transaction.setNoWait();
			}

			if(systemPropertiesProvider.isBatchEnabled()){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Set BATCH for Batch-Insert Operation");
				}				
				transaction.setBatch();
			}					
			
			transaction.begin();
			int iBatchSize = 0;
			while(true){
				
				BatchUpdateData batchUpdateData = this.requestQueue.poll();
				if(iBatchSize > 0 && (iBatchSize >= maxBatchSize || batchUpdateData == null)){
					if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
						LogManager.getLogger().debug(MODULE, "Executing batch operations");
					}
	  	    		long queryExecutionTime = 0;
	  	    			queryExecutionTime = System.currentTimeMillis();
	  	    			
	  	    			/*Execute the Batch Update Operation for Insert Query*/
	  	    			for(Entry<String, PreparedStatement> entry : insertPrepaidStatementMap.entrySet()){
	  	    				psForInsert = entry.getValue();
		  	    			if(psForInsert != null){
		  	    				psForInsert.setQueryTimeout(dbQueryTimeout);
		  	    				psForInsert.executeBatch();
		  	    				transaction.commit();
		  	    			}	  	    				
	  	    			}

	  	    			/*Execute the Batch Update Operation for Update Query*/
	  	    			for(Entry<String, PreparedStatement> entry : updatePrepaidStatementMap.entrySet()){
	  	    				psForUpdate = entry.getValue();
		  	    			if(psForUpdate != null){
		  	    				psForUpdate.setQueryTimeout(dbQueryTimeout);
		  	    				psForUpdate.executeBatch();
		  	    				transaction.commit();
		  	    			}
	  	    			}

	  	    			/*Execute the Batch Update Operation for Delete Query*/
	  	    			for(Entry<String, PreparedStatement> entry : deletePrepaidStatementMap.entrySet()){
	  	    				psForDelete  = entry.getValue();
		  	    			if(psForDelete != null){
		  	    				psForDelete.setQueryTimeout(dbQueryTimeout);
		  	    				psForDelete.executeBatch();
		  	    				transaction.commit();
		  	    			}		  	    			
	  	    			}
	  	    		
	      	    		queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
		      	    	LogManager.getLogger().warn(MODULE,"DB Update time(ms): " + queryExecutionTime + " Total records : "+ iBatchSize);
			      	    synchronized (requestInBatchLock) {
							requestsInBatchProcess.clear();
						}
	      	    		iBatchSize=0;
	      	    	}
					if(batchUpdateData == null){
						break;
					}
				
				
					synchronized (requestInBatchLock) {
						if(batchUpdateData.getSessionData() != null)
							this.requestsInBatchProcess.add(batchUpdateData.getSessionData().getSessionId());
					}
					
					if(batchUpdateData.getOperation() == BatchUpdateData.INSERT){
						String sqlQuery = dialect.getInsertQuery(batchUpdateData.getSessionData());
						psForInsert = insertPrepaidStatementMap.get(sqlQuery);
						try{
							if(psForInsert == null){
								psForInsert = transaction.prepareStatement(sqlQuery);
								insertPrepaidStatementMap.put(sqlQuery, psForInsert);
							}
							BatchDataSink.this.save(batchUpdateData.getSessionData(), psForInsert, transaction);
						}catch(SessionException sessionException){
							throw new SessionException("Error in creating Prepaid Statement for Insert Operation",sessionException);
						}
					}else if(batchUpdateData.getOperation() == BatchUpdateData.DELETE){
						if(batchUpdateData.getSessionData() != null){
							String sqlQuery = dialect.getDeleteQuery(batchUpdateData.getSessionData());
							try{
								psForDelete = deletePrepaidStatementMap.get(sqlQuery);
								if(psForDelete == null){
									psForDelete = transaction.prepareStatement(sqlQuery);
									deletePrepaidStatementMap.put(sqlQuery, psForDelete);
								}
							}catch(TransactionException sessionException){
								throw new SessionException("Error in creating Prepaid Statement for Delete Operation",sessionException);
							}
							BatchDataSink.this.delete(batchUpdateData.getSessionData(), psForDelete, transaction);
						}else if(batchUpdateData.getCriteria() != null){
							String sqlDelQuery  = dialect.getDeleteQuery(batchUpdateData.getCriteria());
							try{
								psForDelete = deletePrepaidStatementMap.get(sqlDelQuery);
								if(psForDelete == null){
									psForDelete = transaction.prepareStatement(sqlDelQuery);
									deletePrepaidStatementMap.put(sqlDelQuery, psForDelete);
								}
								BatchDataSink.this.delete(batchUpdateData.getCriteria(), psForDelete,transaction);
							}catch(SessionException sessionException){
								throw new SessionException("Error in creating Prepaid Statement for Delete Operation",sessionException);
							}
						}
						
					}else if(batchUpdateData.getOperation() == BatchUpdateData.UPDATE){
						
						if(batchUpdateData.getCriteria() == null && batchUpdateData.getSessionData() != null){
							String sqlQuery = dialect.getUpdateQuery(batchUpdateData.getSessionData());
							try{
								psForUpdate = updatePrepaidStatementMap.get(sqlQuery);
								if(psForUpdate == null){
									psForUpdate = transaction.prepareStatement(sqlQuery);
									updatePrepaidStatementMap.put(sqlQuery, psForUpdate);
								}
								BatchDataSink.this.update(batchUpdateData.getSessionData(), psForUpdate, transaction);
							}catch(SessionException sessionException){
								throw new SessionException("Error in creating Prepaid Statement for Update Operation",sessionException);
							}

						}else if(batchUpdateData.getCriteria() != null && batchUpdateData.getSessionData() != null){
							String sqlQuery = dialect.getUpdateQuery(batchUpdateData.getSessionData(),batchUpdateData.getCriteria());
							try{
								psForUpdate = updatePrepaidStatementMap.get(sqlQuery);
								if(psForUpdate == null){
									psForUpdate = transaction.prepareStatement(sqlQuery);
									updatePrepaidStatementMap.put(sqlQuery, psForUpdate);
								}
								BatchDataSink.this.update(batchUpdateData.getSessionData(),batchUpdateData.getCriteria(), psForUpdate, transaction);
							}catch(SessionException sessionException){
								throw new SessionException("Error in creating Prepaid Statement for Update Operation",sessionException);
							}

						}
					}
	      	    	iBatchSize++;
				}
			} catch (SQLException e) {
				if(transaction != null){
					transaction.rollback();
					if(transaction.isDBDownSQLException(e))
						transaction.markDead();
				}
				
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Error in Batch operation, Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
				
			} catch (SessionException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Error in Batch operation, Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
				if(transaction != null){
					transaction.rollback();
				}
				
			} catch (Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Error in Batch operation, Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
				if(transaction != null){
					transaction.rollback();
				}
			}finally{
				for(Entry<String, PreparedStatement> entry : insertPrepaidStatementMap.entrySet()){
					psForInsert = entry.getValue();
				if(psForInsert != null){
					try {
						psForInsert.close();
					} catch (SQLException e) {
						if(transaction != null){
							if(transaction.isDBDownSQLException(e)){
								transaction.markDead();
							}
						}
						if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
							LogManager.getLogger().error(MODULE, "Error in closing prepared Statement, Reason: " + e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
					}
				}
				}
				for(Entry<String, PreparedStatement> entry : updatePrepaidStatementMap.entrySet()){
					psForUpdate = entry.getValue();
				if(psForUpdate != null){
					try {
						psForUpdate.close();
					} catch (SQLException e) {
						if(transaction != null){
							if(transaction.isDBDownSQLException(e)){
								transaction.markDead();
							}
						}
						if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
							LogManager.getLogger().error(MODULE, "Error in closing prepared Statement, Reason: " + e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
					}
				}
				}
				
				for(Entry<String, PreparedStatement> entry : deletePrepaidStatementMap.entrySet()){
					psForDelete = entry.getValue();
				if(psForDelete != null){
					try {
						psForDelete.close();
					} catch (SQLException e) {
						if(transaction != null){
							if(transaction.isDBDownSQLException(e)){
								transaction.markDead();
							}
						}
						if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
							LogManager.getLogger().error(MODULE, "Error in closing prepared Statement, Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
					}
				}
				}
				
				if(transaction != null){
					try {
						transaction.end();
					} catch (TransactionException e) {
						if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
							LogManager.getLogger().error(MODULE, "Error in ending Transaction, Reason: " + e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
						
					}
				}
			}
	}
	
	private boolean save(SessionData sessionData,PreparedStatement psForInsert, Transaction currentTransaction) throws SessionException{
		try{			
			dialect.setPreparedStatementForInsert(sessionData, psForInsert);
			psForInsert.addBatch();
			psForInsert.clearParameters();
		}catch(SessionException sessionException){
			throw new SessionException("Error in save operation",sessionException);
		} catch (SQLException e) {
			if(currentTransaction != null){
				if(currentTransaction.isDBDownSQLException(e)){
					currentTransaction.markDead();
				}
			}
			throw new SessionException("Error in save operation, Reason: " + e.getMessage(),e);
		}		
		return true;
	}
	
	private boolean update(SessionData sessionData,PreparedStatement psForUpdate, Transaction currentTransaction) throws SessionException{
		try{			
			dialect.setPreparedStatementForUpdate(sessionData, psForUpdate);
			psForUpdate.addBatch();
			psForUpdate.clearParameters();
		}catch(SessionException sessionException){
			throw new SessionException("Error in save operation",sessionException);
		} catch (SQLException e) {
			if(currentTransaction != null){
				if(currentTransaction.isDBDownSQLException(e)){
					currentTransaction.markDead();
				}
			}
			throw new SessionException("Error in save operation, Reason: " + e.getMessage(),e);
		}		
		return true;
	}
	
	private int update(SessionData sessionData,Criteria criteria,PreparedStatement psForUpdate, Transaction currentTransaction) throws SessionException{		
		try{
			dialect.setPreparedStatementForUpdate(sessionData,criteria, psForUpdate);
			psForUpdate.addBatch();
			psForUpdate.clearParameters();
		}catch(SessionException sessionException){
			throw new SessionException("Error in delete operation",sessionException);
		} catch (SQLException e) {
			if(currentTransaction != null){
				if(currentTransaction.isDBDownSQLException(e)){
					currentTransaction.markDead();
				}
			}
			throw new SessionException("Error in delete operation, Reason: " + e.getMessage(),e);
		}
		return 1;
	}

	private boolean delete(SessionData sessionData,PreparedStatement psForDelete, Transaction currentTransaction) throws SessionException{
		try{			
			dialect.setPreparedStatementForDelete(sessionData, psForDelete);
			psForDelete.addBatch();
			psForDelete.clearParameters();
		}catch(SessionException sessionException){
			throw new SessionException("Error in save operation",sessionException);
		} catch (SQLException e) {
			if(currentTransaction != null){
				if(currentTransaction.isDBDownSQLException(e)){
					currentTransaction.markDead();
				}
			}
			throw new SessionException("Error in save operation, Reason: " + e.getMessage(),e);
		}		
		return true;
	}
	
	private int delete(Criteria criteria,PreparedStatement psForDelete, Transaction currentTransaction) throws SessionException{		
		try{
			dialect.setPreparedStatementForDelete(criteria, psForDelete);
			psForDelete.addBatch();
			psForDelete.clearParameters();
		}catch(SessionException sessionException){
			throw new SessionException("Error in delete operation",sessionException);
		} catch (SQLException e) {
			if(currentTransaction != null && currentTransaction.isDBDownSQLException(e))
				currentTransaction.markDead();
			throw new SessionException("Error in delete operation, Reason: " + e.getMessage(),e);
		}
		return 1;
	}
	
	public void clearQueue() {
		this.requestQueue.clear();
	}
	
}
