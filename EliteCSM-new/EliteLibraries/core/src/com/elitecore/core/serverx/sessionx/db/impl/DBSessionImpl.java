package com.elitecore.core.serverx.sessionx.db.impl;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DBVendors;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.db.*;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.alert.Alerts;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.serverx.sessionx.*;
import com.elitecore.core.serverx.sessionx.conf.SessionConfiguration;
import com.elitecore.core.serverx.sessionx.db.SQLDialect;
import com.elitecore.core.serverx.sessionx.impl.CriteriaImpl;
import com.elitecore.core.serverx.sessionx.impl.SessionDataImpl;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESIEventListener;
import com.elitecore.core.systemx.esix.LoadBalancerType;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBSessionImpl implements SessionImplementer {
	private static final String MODULE = "DBSessionImpl";
	
	protected SessionConfiguration sessionConfiguration;
	protected SQLDialect dialect;
	protected ServerContext serverContext;
	protected AutoSessionCloser autoSessionCloser;
	protected TransactionFactoryGroupImpl transactionFactoryGroup;
	private SaveOperation saveOperation;
	private UpdateOperation updateOperation;
	private DeleteOperation deleteOperation;
	private SystemPropertiesProvider systemPropertiesProvider;
	@Nullable
	private BatchDataSink batchDataSink = null;
	
	public DBSessionImpl(ServerContext serverContext, SessionConfiguration sessionConfiguration) {
		this.sessionConfiguration = sessionConfiguration;
		this.transactionFactoryGroup = new TransactionFactoryGroupImpl(LoadBalancerType.FAIL_OVER);
		this.systemPropertiesProvider = sessionConfiguration.getSystemPropertiesProvider();
		this.serverContext = serverContext;
	}
	
	public void init() throws InitializationFailedException{
		
		List<DBDataSource> dbDataSources = sessionConfiguration.getDataSources();
					
		for(final DBDataSource dbDataSource :  dbDataSources){
			
			TransactionFactory transactionFactory = null;
			
			DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance(dbDataSource.getDataSourceName());
			
			try{
				dbConnectionManager.init(dbDataSource, serverContext.getTaskScheduler());
			} catch (DatabaseTypeNotSupportedException e) {
				throw new InitializationFailedException(e);
			} catch (DatabaseInitializationException e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Error while initializing DataSource = " + dbDataSource.getDataSourceName() + ". reason: " + e.getMessage());
				LogManager.ignoreTrace(e);
			}
			
			transactionFactory = dbConnectionManager.getTransactionFactory();
			
			
			if(transactionFactory == null){
				int statusCheckInterval = dbDataSource.getStatusCheckDuration();
				if(statusCheckInterval > 0){
					transactionFactory = new TransactionFactory(dbDataSource, serverContext.getTaskScheduler(), statusCheckInterval);	
				} else {
					transactionFactory = new TransactionFactory(dbDataSource, serverContext.getTaskScheduler());
				}
			}
			
			transactionFactory.addESIEventListener(new ESIEventListener<ESCommunicator>() {
			
				@Override
				public void alive(ESCommunicator esCommunicator) {
					serverContext.generateSystemAlert(AlertSeverity.INFO,Alerts.DATABASEUP, MODULE,"Connection is UP from DataSource : " + dbDataSource.getDataSourceName());
				}

				@Override
				public void dead(ESCommunicator esCommunicator) {
					serverContext.generateSystemAlert(AlertSeverity.CRITICAL,Alerts.DATABASEDOWN, MODULE, "Database with Data Source name "+dbDataSource.getDataSourceName()+" is Down, System marked as dead." );
				}				
			});
			
			transactionFactoryGroup.addCommunicator(transactionFactory);
		}
		
		try {
			dialect = createDialect(DBVendors.fromUrl(dbDataSources.get(0).getConnectionURL()));
		} catch (DatabaseTypeNotSupportedException e) {
			throw new InitializationFailedException(e);
		}
			
		//Initialize SQL Dialect
		dialect.init();
		
		//Start Auto Session Closer
		for(SchemaMapping mapping:sessionConfiguration.getSchemaList()){
			if(mapping.getSessionCloserListner() != null){
				if (mapping.getThreadSleepTime() > 0){
					serverContext.getTaskScheduler().scheduleIntervalBasedTask(new AutoSessionCloser(mapping));
				} else {
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Session Thread sleep time configured for schema: " + mapping.getSchemaName() + " is: "+ mapping.getThreadSleepTime() +". So Auto session closure activity will not be started");
						LogManager.getLogger().warn(MODULE, "To start Auto session closure activity configure positive value in session thread sleep time. (In Seconds)");
					}
					
				}
			}
		}
		
		createDBOperation();
	}
	
	private void createDBOperation() {
		if (sessionConfiguration.getSessionFactoryType() == SessionConfiguration.DB_SESSION_WITH_BATCH_UPDATE) {
			
			
			batchDataSink = new BatchDataSink(transactionFactoryGroup, 
					sessionConfiguration, dialect, serverContext.getTaskScheduler());
		}
		if (sessionConfiguration.isSaveBatched() == false) {
			saveOperation = new SynchronousSave(transactionFactoryGroup, sessionConfiguration.getSystemPropertiesProvider(), 
					sessionConfiguration.getHighQueryResponseTime(), dialect);
		} else {
			saveOperation = new AsynchronousSave(batchDataSink);
		}
		if (sessionConfiguration.isUpdateBatched() == false) {
			updateOperation = new SynchronousUpdate(transactionFactoryGroup, sessionConfiguration.getSystemPropertiesProvider(), 
					sessionConfiguration.getHighQueryResponseTime(), dialect);
		} else {
			updateOperation = new AsynchronousUpdate(batchDataSink);
		}
		if (sessionConfiguration.isDeleteBatched() == false) {
			deleteOperation = new SynchronousDelete(transactionFactoryGroup, sessionConfiguration.getSystemPropertiesProvider(),
					sessionConfiguration.getHighQueryResponseTime(), dialect);
		} else {
			deleteOperation = new AsynchronousDelete(batchDataSink);
		}
	}

	protected SQLDialect createDialect(DBVendors dbVendor) {
		return sessionConfiguration.getDialectFactory().newDialect(sessionConfiguration.getSchemaList(), dbVendor);
	}
	
	@Override
	public Session getSession() {
			return new SessionImpl();
	}
	
	protected List<SessionData> list(Criteria criteria,DBTransaction currentTransaction) throws SessionException{
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<SessionData> sessionList = new ArrayList<SessionData>();
		try{
			String sqlQuery = dialect.getSelectQuery(criteria);
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Executing select statement: " + sqlQuery);
			}
			ps = currentTransaction.prepareStatement(sqlQuery);
			ps.setQueryTimeout(sessionConfiguration.getSystemPropertiesProvider().getQueryTimeout());
			dialect.setPreparedStatementForSelect(criteria, ps);
			SchemaMapping schemaMapping = getSchemaMapping(criteria.getSchemaName());
			if(schemaMapping == null){
				throw new SessionException("Schema Mapping not found");
			}
			List<FieldMapping> fieldMappings;
			fieldMappings = schemaMapping.getFieldMappings();
	
			long queryExecutionTime = 0;
			queryExecutionTime = System.currentTimeMillis();
			rs = ps.executeQuery();
			
			queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
			if(sessionConfiguration.getHighQueryResponseTime() < queryExecutionTime) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "OPEN-DB Query execution time getting high for list operation, - Last Query execution time = " + queryExecutionTime + " milliseconds.");
			}
			while(rs.next()){
				SessionDataImpl session = new SessionDataImpl(criteria.getSchemaName(),rs.getString(schemaMapping.getIdFieldMapping().getColumnName()),
						rs.getTimestamp(schemaMapping.getCreationTime().getColumnName()),rs.getTimestamp(schemaMapping.getLastUpdateTime().getColumnName()));
				for(FieldMapping fieldMapping:fieldMappings){
					session.addValue(fieldMapping.getPropertyName(), rs.getString(fieldMapping.getColumnName()));
				}
				session.setSessionLoadTime(queryExecutionTime);
				sessionList.add(session);
			}
		}catch(TransactionException transactionException){
			throw new SessionException("Error in list operation",transactionException);
		} catch (SQLException e) {
			if(currentTransaction != null){
				if(currentTransaction.isDBDownSQLException(e)){
					currentTransaction.markDead();
				}
			}
			throw new SessionException("Error in list operation, Reason: " + e.getMessage(),e);
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in closing Result Set, Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
					
				}
			}
			if(ps != null){
				try {
					ps.close();
				} catch (SQLException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in closing prepared Statement, Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}
			}
		}
		return sessionList;
	}
	
	protected List<SessionData> list(String sqlQuery, String tableName, Transaction currentTransaction) throws SessionException{
		PreparedStatement ps = null;		
		ResultSet rs = null;
		ArrayList<SessionData> sessionList = new ArrayList<SessionData>();
		try{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Executing select statement: " + sqlQuery);
			}
			ps = currentTransaction.prepareStatement(sqlQuery);
			ps.setQueryTimeout(sessionConfiguration.getSystemPropertiesProvider().getQueryTimeout());
			SchemaMapping schemaMapping = getSchemaMapping(tableName);
			if(schemaMapping == null){
				throw new SessionException("Schema Mapping not found");
			}
			List<FieldMapping> fieldMappings;
			fieldMappings = schemaMapping.getFieldMappings();
			long queryExecutionTime = 0;
			queryExecutionTime = System.currentTimeMillis();
			
			rs = ps.executeQuery();
			queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
			if(sessionConfiguration.getHighQueryResponseTime() < queryExecutionTime) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "OPEN-DB Query execution time getting high for list operation, - Last Query execution time = " + queryExecutionTime + " milliseconds.");
			}
			while(rs.next()){
				SessionDataImpl session = new SessionDataImpl(tableName,rs.getString(schemaMapping.getIdFieldMapping().getColumnName()),
						rs.getTimestamp(schemaMapping.getCreationTime().getColumnName()),rs.getTimestamp(schemaMapping.getLastUpdateTime().getColumnName()));
				for(FieldMapping fieldMapping:fieldMappings){
					session.addValue(fieldMapping.getPropertyName(), rs.getString(fieldMapping.getColumnName()));
				}
				session.setSessionLoadTime(queryExecutionTime);
				sessionList.add(session);
			}
		}catch(TransactionException transactionException){
			throw new SessionException("Error in save operation",transactionException);
		} catch (SQLException e) {
			if(currentTransaction != null){
				if(currentTransaction.isDBDownSQLException(e)){
					currentTransaction.markDead();
				}
			}
			throw new SessionException("Error in list operation, Reason: " + e.getMessage(),e);
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in closing Result Set, Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
					
				}
			}
			if(ps != null){
				try {
					ps.close();
				} catch (SQLException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in closing prepared Statement, Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
					
				}
			}
		}
		return sessionList;
	}
	
	protected int count(DBTransaction currentTransaction,Criteria criteria){
		PreparedStatement ps = null;
		int result = SessionResultCode.FAILURE.code;
		ResultSet rs = null;
		String sqlQuery = dialect.getCountQuery(criteria);
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Executing count statement: " + sqlQuery);
		}


		try{

			
			ps = currentTransaction.prepareStatement(sqlQuery);
			ps.setQueryTimeout(sessionConfiguration.getSystemPropertiesProvider().getQueryTimeout());
			dialect.setPreparedStatementForCount(criteria, ps);
			long queryExecutionTime = 0;
			queryExecutionTime = System.currentTimeMillis();
			
			rs = ps.executeQuery();
			rs.next();
			result = rs.getInt(1);
			queryExecutionTime = System.currentTimeMillis()
					- queryExecutionTime;
			if (sessionConfiguration.getHighQueryResponseTime() < queryExecutionTime) {
				LogManager.getLogger().warn(MODULE,"Session Management Query execution time getting high for count operation , - Last Query execution time = " + queryExecutionTime + " milliseconds.");
			}
		} catch (TransactionException ex) {
			LogManager.getLogger().error(MODULE,
					"Error in transaction, Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		} catch (SQLException e) {
			if(currentTransaction != null){
				if(currentTransaction.isDBDownSQLException(e)){
					currentTransaction.markDead();
				}
			}
			LogManager.getLogger().error(MODULE,"Error in  count operation, Reason: "+ e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		} finally {
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(ps);
		}
		return result;
	}
	
	protected SchemaMapping getSchemaMapping(String schemaName){
		for(SchemaMapping schemaMapping:sessionConfiguration.getSchemaList()){
			if(schemaMapping.getSchemaName().equals(schemaName)){
				return schemaMapping;
			}
		}
		return null;
	}
	
	class SessionImpl implements Session {

		public int save(SessionData sessionData) {
			return saveOperation.execute(sessionData);
		}

		@Override
		public int update(SessionData sessionData) {
			return updateOperation.execute(sessionData);
		}
		
		@Override
		public int update(SessionData sessionData, Criteria criteria) {
			return updateOperation.execute(sessionData, criteria);
		}


		@Override
		public int delete(SessionData sessionData) {
			return deleteOperation.execute(sessionData);
		}

		@Override
		public int delete(Criteria criteria) {
			return deleteOperation.execute(criteria);

		}

		@Override
		public int truncate(String tableName) {
			int truncateStatus = SessionResultCode.FAILURE.code;
			if (tableName == null) {
				LogManager.getLogger().info(MODULE,
						"Unable to truncate table. Reason: table name is null");
				return truncateStatus;
			}

			TransactionFactory transactionFactory = transactionFactoryGroup.getTransactionFactory();
			if (transactionFactory == null) {
				LogManager.getLogger().error(MODULE, "Error in truncating session. Reason: no live datasource found");
				return truncateStatus;
			}

			Transaction newTransaction = transactionFactory
					.createTransaction();
			try {

				if (systemPropertiesProvider.isNoWaitEnabled()) {
					if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
						LogManager.getLogger().debug(MODULE, "Set NOWAIT for Truncate Operation");
					}
					newTransaction.setNoWait();
				}

				if (systemPropertiesProvider.isBatchEnabled()) {
					if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
						LogManager.getLogger().debug(MODULE, "Set BATCH for Truncate Operation");
					}
					newTransaction.setBatch();
				}
				newTransaction.begin();
				truncateStatus = truncate(newTransaction, tableName);
			} catch (TransactionException te) {
				LogManager.getLogger().error(MODULE,"Error in truncating session, Reason: " + te.getMessage());
				LogManager.getLogger().trace(MODULE, te);
				truncateStatus = SessionResultCode.FAILURE.code;
			} finally {
				try {
					newTransaction.end();
				} catch (TransactionException e) {
					LogManager.getLogger().error(MODULE,"Error in ending transaction, Reason: "+ e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}
			}
			return truncateStatus;
		}

		@Override
		public List<SessionData> list(Criteria criteria) {
			List<SessionData> sessionList = null;
			TransactionFactory transactionFactory = transactionFactoryGroup.getTransactionFactory();
			if(transactionFactory == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Error in retrieving session. Reason: no live datasource found");
				return null;
			}

			DBTransaction newTransaction = transactionFactory.createReadOnlyTransaction();
			try{
				newTransaction.begin();
				sessionList= DBSessionImpl.this.list(criteria,newTransaction);
			}catch(TransactionException te){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Error in retrieving session, Reason:" + te.getMessage());
				LogManager.getLogger().trace(MODULE, te);
			}catch(SessionException se){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Error in retrieving session, Reason: " + se.getMessage());
				LogManager.getLogger().trace(MODULE, se);
			}finally{
				try {
					if(newTransaction != null)
						newTransaction.end();
				} catch (TransactionException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in ending transaction, Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}
			}
			return sessionList;
		}

		@Override
		public int count(Criteria criteria) {
			int count = SessionResultCode.FAILURE.code;
			
			TransactionFactory transactionFactory = transactionFactoryGroup.getTransactionFactory();
			if (transactionFactory == null) {
				if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
					LogManager.getLogger().error(MODULE, "Error in retrieving session. Reason: no live datasource found");
				}
				return count;
			}

			DBTransaction newTransaction = transactionFactory.createReadOnlyTransaction();
			try {
				newTransaction.begin();
				count = DBSessionImpl.this.count(newTransaction, criteria);
			} catch (TransactionException te) {
				if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
					LogManager.getLogger().error(MODULE, "Error in retrieving session, Reason:" + te.getMessage());
				}
				LogManager.getLogger().trace(MODULE, te);
			} finally {
				try {
					if (newTransaction != null) {
						newTransaction.end();
					}
				} catch (TransactionException e) {
					if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
						LogManager.getLogger().error(MODULE, "Error in ending transaction, Reason: " + e.getMessage());
					}
					LogManager.getLogger().trace(MODULE, e);
				}
			}
			return count;
		}

		@Override
		public Criteria createCriteria(String schemaName) {
			return new CriteriaImpl(schemaName);
		}

		private int truncate(Transaction currentTransaction, String tableName) {
			PreparedStatement ps = null;
			int result = SessionResultCode.FAILURE.code;
			try {
				String sqlQuery = dialect.getTruncateQuery(tableName);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Executing truncate statement: " + sqlQuery);
				}

				ps = currentTransaction.prepareStatement(sqlQuery);
				ps.setQueryTimeout(sessionConfiguration.getSystemPropertiesProvider().getQueryTimeout());
				long queryExecutionTime = 0;
				queryExecutionTime = System.currentTimeMillis();

				result = ps.executeUpdate();
				if (sessionConfiguration.getSessionFactoryType() == SessionConfiguration.DB_SESSION_WITH_BATCH_UPDATE) {
					DBSessionImpl.this.batchDataSink.clearQueue();
				}
				queryExecutionTime = System.currentTimeMillis()
						- queryExecutionTime;
				if (sessionConfiguration.getHighQueryResponseTime() < queryExecutionTime) {
					LogManager.getLogger()
							.warn(MODULE,
									"Session Management Query execution time getting high for truncate operation , - Last Query execution time = "
											+ queryExecutionTime + " milliseconds.");
				}
			} catch (TransactionException ex) {
				LogManager.getLogger().error(MODULE,
						"Error in transaction, Reason: " + ex.getMessage());
				LogManager.getLogger().trace(MODULE, ex);
			} catch (SQLException e) {
				if(currentTransaction != null){
					if(currentTransaction.isDBDownSQLException(e)){
						currentTransaction.markDead();
					}
				}
				LogManager.getLogger().error(MODULE,"Error in  truncating operation, Reason: "+ e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} finally {
				if (ps != null) {
					try {
						ps.close();
					} catch (SQLException e) {
						LogManager.getLogger().error(
								MODULE,
								"Error in closing prepared Statement, Reason: "
										+ e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
					}
				} else {
					LogManager.getLogger().warn(MODULE, "PS is null");
				}
			}
			return result;
		}
	}
	
	class AutoSessionCloser extends BaseIntervalBasedTask{
		private SchemaMapping schemaMapping;

		public AutoSessionCloser(SchemaMapping schemaMapping){
			this.schemaMapping = schemaMapping;
		}

		@Override
		public void execute(AsyncTaskContext context) {
			AutoSessionCloserListner sessionCloserListner = schemaMapping.getSessionCloserListner();
			if(sessionCloserListner != null){

				TransactionFactory transactionFactory = transactionFactoryGroup.getTransactionFactory();
				if(transactionFactory == null){
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in truncating session. Reason: no live datasource found");
					return;
				}
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Searching for session eligible for closure for table : " + schemaMapping.getSchemaName());
				Transaction newTransaction = transactionFactory.createTransaction();
				try {
					
					if(systemPropertiesProvider.isNoWaitEnabled()){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
							LogManager.getLogger().debug(MODULE, "Set NOWAIT for delete Operation");						
						}																																				
						newTransaction.setNoWait();
					}

					if(systemPropertiesProvider.isBatchEnabled()){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
							LogManager.getLogger().debug(MODULE, "Set BATCH for delete Operation");						
						}																																										
						newTransaction.setBatch();
					}					
					
					newTransaction.begin();
					List<SessionData> sessionDataList = list(dialect.getSessionCloseQuery(schemaMapping.getSchemaName()), schemaMapping.getSchemaName(), newTransaction);							
					for(SessionData sessionData:sessionDataList){
						sessionCloserListner.timedoutSession(sessionData);
					}
				} catch (SessionException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in getting sessions for Auto Session Close, Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
					
				}catch (Exception ex) {
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in getting sessions for Auto Session Close, Reason: " + ex.getMessage());
					LogManager.getLogger().trace(MODULE, ex);
					
				}finally{
					try {
						newTransaction.end();
					}catch(TransactionException e){
						if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
							LogManager.getLogger().error(MODULE, "Error in ending transaction, Reason: " + e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
					}
				}
			}
		}

		@Override
		public long getInterval() {
			return this.schemaMapping.getThreadSleepTime();
		}

		@Override
		public boolean isFixedDelay() {
			return true;
		}

	}

	@Override
	public DBDataSource getActiveDataSource() {
		return transactionFactoryGroup.getActiveDataSource();
	}

	@Override
	public boolean isAlive() {
		return transactionFactoryGroup.isAlive();
	}
}
