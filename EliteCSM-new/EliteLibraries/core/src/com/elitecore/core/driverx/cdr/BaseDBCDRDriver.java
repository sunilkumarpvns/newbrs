package com.elitecore.core.driverx.cdr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.alert.Events;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.util.constants.DataTypeConstant;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DBVendors;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.db.Transaction;
import com.elitecore.core.commons.utilx.db.TransactionErrorCode;
import com.elitecore.core.commons.utilx.db.TransactionException;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.commons.utilx.db.TransactionFactoryGroupImpl;
import com.elitecore.core.driverx.cdr.data.DBFieldMapping;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESCommunicatorImpl;
import com.elitecore.core.systemx.esix.ESIEventListener;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.util.queue.ConcurrentLinkedQueue;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

public abstract class BaseDBCDRDriver<T> extends ESCommunicatorImpl implements CDRDriver<T> {

	private static final String MODULE = "BASE-DBCDRD";
	
	private static final int QUERY_TIMEOUT_ERRORCODE = 1013;
	private static final int DATABASE_UNIQUE_CONSTRAINTS = 1;
	
	private int totalQueryTimeoutCount;

	private String insertQuery;
	private String updateQuery;
	
	private TransactionFactory transactionFactory;
	private DBDataSource dataSource;
	private DBCDRBuilder psBuilder;
	private TaskScheduler taskScheduler;
	private TransactionFactoryGroupImpl transactionFactoryGroup;
	
	public BaseDBCDRDriver(TaskScheduler taskScheduler) {
		super(taskScheduler);
		this.taskScheduler = taskScheduler;
	}
	
	public void init() throws DriverInitializationFailedException {
		try {
			super.init();
			
			if(getDBFieldMappings() == null || getDBFieldMappings().isEmpty())
				throw new DriverInitializationFailedException("Error while initialising DB CDR driver. Reason: No DB Field Mapping Configured");

			dataSource = getDataSource();
			if(dataSource == null) 
				throw new DriverInitializationFailedException("Error while initialising DB CDR driver. Reason: Datasource not found");
				
			
			DBConnectionManager connectionManager = DBConnectionManager.getInstance(dataSource.getDataSourceName());
			initConnectionManager(connectionManager);

			transactionFactory = connectionManager.getTransactionFactory();
			transactionFactory.addESIEventListener(new ESIEventListener<ESCommunicator>() {
				
				@Override
				public void dead(ESCommunicator esCommunicator) {
					BaseDBCDRDriver.this.markDead();
				}
				
				@Override
				public void alive(ESCommunicator esCommunicator) {
					BaseDBCDRDriver.this.markAlive();
				}
			});
			
			
			transactionFactoryGroup=new TransactionFactoryGroupImpl();
			transactionFactoryGroup.addCommunicator(transactionFactory);
			
			//	build insert and update query
			insertQuery = buildInsertQuery(connectionManager.getVendor());
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Generated DB CDR insert query:\n" + insertQuery);
			
			updateQuery = buildUpdateQuery();
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Generated DB CDR update query:\n" + updateQuery);
			
		if(psBuilder==null){	
			if(isStoreAllCDRs()){
				if(isBatchUpdate()){
					psBuilder=new DBCDRBatchBuilder(transactionFactoryGroup);
				}else{
					psBuilder=new DBCDRBuilder(transactionFactoryGroup);
				}
			}else{
				if(isBatchUpdate()){
					LogManager.getLogger().warn(MODULE, "Batch Operation for DB CDR will be disabled. Reason: Store All CDR is disabled");
				}
				psBuilder=new DBCDRBuilder(transactionFactoryGroup);
			}
		}
			psBuilder.init();
			
		} catch (InitializationFailedException e) {
			throw new DriverInitializationFailedException("Error while initialising DB CDR driver. Reason: " + e.getMessage(), e);
		}
	}

	private void initConnectionManager(DBConnectionManager connectionManager) throws InitializationFailedException {
		try {
            connectionManager.init(dataSource, taskScheduler);
        } catch (DatabaseTypeNotSupportedException e) {
            throw new InitializationFailedException(e);
        } catch (DatabaseInitializationException e) {
            if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
                LogManager.getLogger().warn(MODULE, "Error initializing DataSource = " + dataSource.getDataSourceName() + ". Reason: " + e.getMessage());

            LogManager.ignoreTrace(e);
        }
	}


	/**
	 * Builds insert query for database CDR. This query will use in inserting CDR of 
	 * quota reporting, usage metering and other parameters
	 * @return insert query
	 */
	private String buildInsertQuery(DBVendors dbVendor) {
		StringBuilder insertQuery = new StringBuilder(100);
		insertQuery.append("INSERT INTO ").append(getTableName()).append(" (");
		
		if (Strings.isNullOrBlank(getIdentityField()) == false) {
			insertQuery.append(getIdentityField()).append(", ");
		}

		StringBuilder strValue = new StringBuilder(" VALUES ( ");
		
		
		if (Strings.isNullOrBlank(getSequenceName()) == false) {
				strValue.append(dbVendor.getVendorSpecificSequenceSyntax(getSequenceName().trim())
						+ ", ");
		}
		
		for(DBFieldMapping dbField : getDBFieldMappings()) {
			insertQuery.append(dbField.getDBField()).append(", ");
			strValue.append("?").append(", ");
		}
		
		insertQuery.append(getCreateDateField()).append(", ").append(getTimestampField()).append(") ");
		strValue.append("?").append(", ").append("?").append(")");
		insertQuery.append(strValue);
		
		return insertQuery.toString();
	}
	
	/**
	 * Builds update query for database CDR. This query will use if "All CDR store" option is disabled.
	 * This query will use in updating CDR of quota reporting, usage metering and other parameters
	 * @return update query
	 */
	private String buildUpdateQuery() {
		StringBuilder updateQuery = new StringBuilder(100);
		updateQuery.append("UPDATE ").append(getTableName()).append(" SET ");
			
		for(DBFieldMapping dbField : getDBFieldMappings())
			updateQuery.append(dbField.getDBField()).append(" = ?, ");
				
		updateQuery.append(getLastModifiedDateField()).append(" = ?, ").append(getTimestampField()).append(" = ?");
		updateQuery.append(" WHERE ").append(getSessionIdField()).append(" = ?");
		
		return updateQuery.toString();
	}
	
	public final void registerDBCDRBuilder(DBCDRBuilder psBuilder) {
		if(psBuilder != null){
			this.psBuilder = psBuilder;
	}
	}
	
	@Override
	public void handleRequest(T request) throws DriverProcessFailedException {
		
		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Processing request for DB CDR");
		try {
			if (isStoreAllCDRs())
				psBuilder.insertCDR(request);
			else {
				psBuilder.updateCDR(request);
			}
		} catch (TransactionException te) {
			if (te.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {
				notifyAlertListeners(Events.DB_CONNECTION_NOT_AVAILABLE, "Unable to process request for Secondary CDR driver: "
						+ getDriverName() + ". Reason: Connection not available");
			}

			throw new DriverProcessFailedException("Error while processing for DB CDR Driver request. Reason: " + te.getMessage(), te);
		}
	}
	
	
	public class DBCDRBuilder {

		protected TransactionFactoryGroupImpl transactionFactoryGroup;
		
		
		public DBCDRBuilder(TransactionFactoryGroupImpl transactionFactoryGroup){
			this.transactionFactoryGroup=transactionFactoryGroup;
	}
	
	public void init() throws InitializationFailedException {
		//in Current class not used. but need to in DB Batch
	}

		public void insertCDR(T request) throws TransactionException {
			PreparedStatement preparedStatement = null;
			Transaction transaction=null;
			
			try {
				int index = 0;
				TransactionFactory transactionFactory=transactionFactoryGroup.getTransactionFactory();
				if(transactionFactory==null){
					LogManager.getLogger().error(MODULE, "Error in processing CDR . Reason: Could not create Transaction Factory");
					return;
				}
				transaction=transactionFactory.createTransaction();
				if(transaction==null){
					LogManager.getLogger().error(MODULE, "Error in processing CDR . Reason: Could not create Transaction");
					return;
				}
				transaction.begin();
				preparedStatement = transaction.prepareStatement(getInsertQuery());
				if(preparedStatement==null){
					LogManager.getLogger().error(MODULE, "Error in processing CDR . Reason: Could not create Prepared Statement");
					return;
				}
				for(DBFieldMapping mapping : getDBFieldMappings()) { 
					setValue(++index, mapping.getDataType(), getParameterValue(request, mapping.getKey(), mapping.getDefaultValue()), preparedStatement);
				}
				
				Timestamp timestamp = new Timestamp(new Date().getTime());
				preparedStatement.setTimestamp(++index, timestamp); // for create date field
				preparedStatement.setTimestamp(++index, timestamp); // for timestamp field
				executeQuery(preparedStatement);
			} catch (SQLException e) {
				if(transaction!=null){
					transaction.rollback();
					if(transaction.isDBDownSQLException(e)){
						transaction.markDead();
					}
				}
					
				throw new TransactionException("Error while processing CDR. Reason: " + e.getMessage(), e);
			} catch (TransactionException e) {
				if(transaction!=null) {
					transaction.rollback();
				}
				throw new TransactionException("Error while processing CDR. Reason: " + e.getMessage(), e);
			} finally {
				DBUtility.closeQuietly(preparedStatement);
				try {
					if(Objects.nonNull(transaction)){
						transaction.end();
					}
				} catch (TransactionException e) { 
					getLogger().trace(e);
				}
			}
		}


		public void updateCDR(T request) throws TransactionException {
			PreparedStatement preparedStatement = null;
			Transaction transaction=null;
			
			try {
				int index = 0;
				TransactionFactory transactionFactory=transactionFactoryGroup.getTransactionFactory();
				if(transactionFactory==null){
					LogManager.getLogger().error(MODULE, "Error in processing CDR . Reason: Could not create Transaction Factory");
					return;
				}
				transaction=transactionFactory.createTransaction();
				if(transaction==null){
					LogManager.getLogger().error(MODULE, "Error in processing CDR . Reason: Could not create Transaction");
					return;
				}
				transaction.begin();
				preparedStatement = transaction.prepareStatement(getUpdateQuery());
				if(preparedStatement==null){
					LogManager.getLogger().error(MODULE, "Error in processing CDR . Reason: Could not create Prepared Statement");
					return;
				}
				for(DBFieldMapping mapping : getDBFieldMappings()) { 
					setValue(++index, mapping.getDataType(), getParameterValue(request, mapping.getKey(), mapping.getDefaultValue()), preparedStatement);
				}
				
				Timestamp timestamp = new Timestamp(new Date().getTime());
				preparedStatement.setTimestamp(++index, timestamp); // for last modified date field
				preparedStatement.setTimestamp(++index, timestamp); // for timestamp field
				preparedStatement.setString(++index, getIdentityFieldValue(request));
				if(executeQuery(preparedStatement) == 0) 
					insertCDR(request);
			} catch (SQLException e) {
				if(transaction!=null){
					transaction.rollback();
					if(transaction.isDBDownSQLException(e)){
						transaction.markDead();
					}
				}
					
				throw new TransactionException("Error while processing CDR. Reason: " + e.getMessage(), e);
			} catch (TransactionException e) {
				if(transaction!=null) {
					transaction.rollback();
				}
				throw new TransactionException("Error while processing CDR. Reason: " + e.getMessage(), e);
			}finally{
		       DBUtility.closeQuietly(preparedStatement);
				try {
					if(Objects.nonNull(transaction)){
						transaction.end();
					}
				} catch (Exception e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in ending transaction, Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}
			}
		}
}


	private  class DBCDRBatchBuilder extends DBCDRBuilder {

		private static final int QUEUE_SIZE = 10000;
		
		private long interval;		//default 1 sec
		private int queryTimeout;   //default 10sec
		
		private ConcurrentLinkedQueue<T> batchOperationQueue;
		
		
		public DBCDRBatchBuilder(TransactionFactoryGroupImpl transactionFactoryGroup){
			super(transactionFactoryGroup);			
			interval=getBatchUpdateInterval();
			queryTimeout=getBatchQueryTimeOut();
			batchOperationQueue= new ConcurrentLinkedQueue<>(QUEUE_SIZE);
		}

		@Override
		public void init() throws InitializationFailedException{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Initializing DBCDR Batch opeation");
			taskScheduler.scheduleIntervalBasedTask(new BatchUpdateTask());
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "DBCDR Batch operation initialized successfully");
		}

		@Override
		public void insertCDR(T request){
			if(!batchOperationQueue.offer(request)){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Cannot add element to queue, reason : queue is full");
			}
		}
		
		
		private class BatchUpdateTask extends BaseIntervalBasedTask {

			@Override
			public long getInterval() {
				return interval;
			}
			
			@Override
			public boolean isFixedDelay() {
				return true;
			}

			@Override
			public void execute(AsyncTaskContext context) {								
				
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Performing batch operation for CDR");
					
				if(batchOperationQueue.size()<=0){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE,"Skipping batch Operation for CDR. Reason: No request found in queue");
					}
					return;
				}
				PreparedStatement psInsert = null;
				Transaction transaction=null;
				try{
					
					TransactionFactory transactionFactory = transactionFactoryGroup.getTransactionFactory();
					if(transactionFactory == null) {
						LogManager.getLogger().error(MODULE, "Error in DBCDR batch operation. Reason: Could not create transactionFactory");
						return;
					}
					
					transaction=transactionFactory.createTransaction();
					if(transaction==null){
						LogManager.getLogger().error(MODULE, "Error in DBCDR batch operation. Reason: Could not create transaction");
						return;
					}
					transaction.begin();
					psInsert = transaction.prepareStatement(insertQuery);
					if(psInsert==null){
						LogManager.getLogger().error(MODULE, "Error in DBCDR batch operation. Reason: Could not create transaction");
						return;
					}
					psInsert.setQueryTimeout(queryTimeout);
					int batchCnt;
					batchCnt = 0;
					int index = 0;
					while(true) {
						T request = batchOperationQueue.poll();
						if(batchCnt > 0 && (batchCnt >= getBatchSize() || request == null)){
							long startTime = System.currentTimeMillis();
							psInsert.executeBatch();
							transaction.commit();
							long endTime = System.currentTimeMillis();
							LogManager.getLogger().warn(MODULE,"DB Update time(ms): " + (endTime - startTime) + " Total records : "+ batchCnt);
							batchCnt = 0;
						}

						if(request == null) {
							break;
						}
		                     index=0;				
							for(DBFieldMapping mapping : getDBFieldMappings()) { 
								setValue(++index, mapping.getDataType(), getParameterValue(request, mapping.getKey(), mapping.getDefaultValue()), psInsert);
							}
		
							Timestamp timestamp = new Timestamp(new Date().getTime());
							psInsert.setTimestamp(++index, timestamp); // for create date field
							psInsert.setTimestamp(++index, timestamp);// for timestamp field
							psInsert.addBatch();
							psInsert.clearParameters();
							batchCnt++;
	}
				}catch (TransactionException e) {
					if(transaction!=null)					
						transaction.rollback();
					LogManager.getLogger().error(MODULE, "Error while performing batch operation for CDR. Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}catch(SQLException e){
					if(transaction!=null){
						transaction.rollback();
						if(transaction.isDBDownSQLException(e))
							transaction.markDead();
					}
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in Batch operation for CDR, Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}catch (Exception e){
					if(transaction!=null)
						transaction.rollback();
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in Batch operation for CDR, Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				} finally {
					DBUtility.closeQuietly(psInsert);
					try {
						if(Objects.nonNull(transaction)){
							transaction.end();
						}
					} catch (Exception e) {
						if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
							LogManager.getLogger().error(MODULE, "Error in ending transaction, Reason: " + e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
					}
				}
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Batch operation for DBCDR completed");
			}
	
		}
		
		
	}

	
	public final void setValue(int index, int dataType, String value, PreparedStatement preparedStatement) throws SQLException {
		if(DataTypeConstant.TIME_STAMP_DATA_TYPE == dataType) {
			try {
				preparedStatement.setTimestamp(index, toTimestamp(value));
			} catch (NumberFormatException | ParseException e) { //NOSONAR
				LogManager.getLogger().warn(MODULE, "Error while processing DB CDR. Reason: Invalid number format " + e.getMessage());
				preparedStatement.setTimestamp(index, null);
			}
		} else { 
			preparedStatement.setString(index, value);
		}
	}

	protected Timestamp toTimestamp(String value) throws ParseException {
		return new Timestamp(Long.parseLong(value));
	}
	
	public final int executeQuery(PreparedStatement preparedStatement) throws TransactionException {
		int result;
		try {
			preparedStatement.setQueryTimeout(getQueryTimeout());
			long queryExecutionTime = 0;
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) 
				queryExecutionTime = System.currentTimeMillis();
			result = preparedStatement.executeUpdate();
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
				if(queryExecutionTime > 10) {
					LogManager.getLogger().warn(MODULE, "OPEN-DB Query execution time getting high, - Last Query execution time = " + queryExecutionTime + " milliseconds.");
				}
			}
			totalQueryTimeoutCount = 0;
			return result;
		} catch (SQLException e) {
			int errorCode = e.getErrorCode();
			LogManager.getLogger().warn(MODULE, "SQL Error code: " + errorCode);
			if(errorCode == QUERY_TIMEOUT_ERRORCODE) {				
				totalQueryTimeoutCount++;
				if(totalQueryTimeoutCount > getMaxQueryTimeoutCount()) {
					LogManager.getLogger().debug(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts,so System marked as DEAD");
					transactionFactory.markDead();
				}
				throw new TransactionException(e);
			} else if(errorCode == DATABASE_UNIQUE_CONSTRAINTS) {
				throw new TransactionException(e);
			} else if(DBConnectionManager.getInstance(dataSource.getDataSourceName()).isDBDownSQLException(e)) {
				transactionFactory.markDead();
				LogManager.getLogger().warn(MODULE,"Database with Data Source name " + dataSource.getDataSourceName() + " is Down, System marked as dead. Reason: " + e.getMessage());
				throw new TransactionException(e);
			} else {
				LogManager.getLogger().warn(MODULE, "UnKnown DB error. Reason: " + e.getMessage());
				throw new TransactionException(e);
			}
		}
	}
	
	public String getInsertQuery() {
		return insertQuery;
	}
	
	public String getUpdateQuery() {
		return updateQuery;
	}
	
	@Override
	public void scan() {
		
	}

	@Override
	protected int getStatusCheckDuration() {
		return ESCommunicator.NO_SCANNER_THREAD;
	}
	
	protected TransactionFactoryGroupImpl getTransactionFactoryGroup() {
		return transactionFactoryGroup;
	}

	@Override
	public TaskScheduler getTaskScheduler() {
		return taskScheduler;
	}
	
	public abstract boolean isStoreAllCDRs();
	public abstract String getTableName();
	public abstract DBDataSource getDataSource();
	
	public abstract String getIdentityFieldValue(T request);
	public abstract String getIdentityField();
	public abstract String getSequenceName();
	public abstract String getSessionIdField();
	public abstract String getCreateDateField();
	public abstract String getLastModifiedDateField();
	public abstract String getTimestampField();
	
	public abstract int getQueryTimeout();
	public abstract int getMaxQueryTimeoutCount();
	public abstract List<DBFieldMapping> getDBFieldMappings();
	public abstract String getParameterValue(T request, String key, String defaultValue);
	public abstract boolean isBatchUpdate();
	public abstract int getBatchSize();
	public abstract int getBatchUpdateInterval();
	public abstract int getBatchQueryTimeOut();
	
}
