package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.db.Transaction;
import com.elitecore.core.commons.utilx.db.TransactionException;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.commons.utilx.db.TransactionFactoryGroupImpl;
import com.elitecore.core.driverx.cdr.BaseDBCDRDriver;
import com.elitecore.core.driverx.cdr.data.DBFieldMapping;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.util.queue.ConcurrentLinkedQueue;
import com.elitecore.corenetvertex.constants.DriverTypes;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.netvertex.core.driver.cdr.conf.impl.DBCDRDriverConfigurationImpl;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.usagemetering.ServiceUnit;
import com.elitecore.netvertex.usagemetering.UsageMonitoringInfo;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class NVDBCDRDriver extends BaseDBCDRDriver<ValueProviderExtImpl> {

	private static final String MODULE = "NV-DBCDRD";

	private static final String REPORTING_TYPE_UM = "UM";

	private DBDataSource dataSource;
	private DBCDRDriverConfigurationImpl dbCDRDriverConf;
	

	public NVDBCDRDriver(DBDataSource dataSource,DBCDRDriverConfigurationImpl dbCDRDriverConf, TaskScheduler schedular) {
		super(schedular);

		this.dbCDRDriverConf = dbCDRDriverConf;
		this.dataSource = dataSource;
	}
	
	@Override
	public void init() throws DriverInitializationFailedException {
		LogManager.getLogger().info(MODULE, "Initializing DB CDR driver " + dbCDRDriverConf.getDriverName());
		try {

			// initializing CDR Builder
			if(REPORTING_TYPE_UM.equals(dbCDRDriverConf.getReportingType())){
				if(dbCDRDriverConf.isStoreAllCDRs()){ 
					if(dbCDRDriverConf.isBatchUpdate()){
						registerDBCDRBuilder(new UMDBCDRBatchBuilder(getTransactionFactoryGroup()));
					}else{
						registerDBCDRBuilder(new UMDBCDRBuilder(getTransactionFactoryGroup()));	
					}
				}else{
					if(dbCDRDriverConf.isBatchUpdate()){
						LogManager.getLogger().warn(MODULE, "Batch Operation for Usage Monitoring Reporting will be disabled. Reason: Store All CDR is disabled");
					}
					registerDBCDRBuilder(new UMDBCDRBuilder(getTransactionFactoryGroup()));
				}
			}
			

			super.init();
		} catch (InitializationFailedException e) {
			throw new DriverInitializationFailedException("Error while initializing DB CDR driver " + dbCDRDriverConf.getDriverName() + ". Reason: "
					+ e.getMessage(), e);
		}
	}
		
		
	private class UMDBCDRBuilder extends DBCDRBuilder {
		
		public UMDBCDRBuilder(TransactionFactoryGroupImpl transactionFactoryGroup) {
			super(transactionFactoryGroup);
			
		}
		@Override
		public void insertCDR(ValueProviderExtImpl valueProvider) throws TransactionException {
			PCRFResponse response = valueProvider.getResponse();
			
			String subscriberID = response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
			
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Insert CDR operation started in CDR driver: " + getName() + " for subscriber ID: " + subscriberID);
			}
			
			PreparedStatement preparedStatement = null;
			Transaction transaction =null;
			try {
				int index;
				TransactionFactory transactionFactory=getTransactionFactoryGroup().getTransactionFactory();
				if(transactionFactory==null){
					LogManager.getLogger().error(MODULE, "Error in Usage Metering CDR operation. Reason: Could not create Transaction Factory");
					return;
				}
				transaction=transactionFactory.createTransaction();
				if(transaction==null){
					LogManager.getLogger().error(MODULE, "Error in Usage Metering CDR operation. Reason: Could not create Transaction");
					return;
				}
				transaction.begin();
				preparedStatement = transaction.prepareStatement(getInsertQuery());
				if(preparedStatement==null){
					LogManager.getLogger().error(MODULE, "Error in Usage Metering CDR operation. Reason: Could not create Prepared Statement");
					return;
				}
				List<UsageMonitoringInfo> reportedUnits = response.getReportedUsageInfoList();
				if(reportedUnits == null) {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Skipping Usage Metering CDR for this request for session-Id "+
					                      response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val)+" for request Type "+response.getAttribute(PCRFKeyConstants.REQUEST_TYPE.val) +" Reason: Usage Monitoring info not found");
					return;
				}

				long currentTime = new Date().getTime();
				Timestamp timestamp = new Timestamp(currentTime);
				for (UsageMonitoringInfo monitoringInfo : reportedUnits) {
					index = 0;
					ServiceUnit serviceUnit = monitoringInfo.getUsedServiceUnit();
					for(DBFieldMapping mapping : getDBFieldMappings())
						setValue(++index, mapping.getDataType(), getValue(valueProvider, serviceUnit, mapping.getKey(), monitoringInfo.getMonitoringKey(), mapping.getDefaultValue()), preparedStatement);
							
					preparedStatement.setTimestamp(++index, timestamp); // for create date field
					preparedStatement.setTimestamp(++index, timestamp); // for timestamp field
					long queryExecutionTime = System.currentTimeMillis();
					executeQuery(preparedStatement);
					queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
					if(queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS){
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
							LogManager.getLogger().warn(MODULE, "DB Query execution time is getting high for Usage Metering CDR, Last Query execution time = " + queryExecutionTime + " milliseconds.");
				}
					}

				}
				
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Insert CDR operation completed in CDR driver: " + getName() + " for subscriber ID: " + subscriberID);
				}
			} catch (SQLException e) {
				if(transaction!=null){
					transaction.rollback();
					if(transaction.isDBDownSQLException(e))
						transaction.markDead();
				}
				throw new TransactionException("Error while processing CDR for usage metering. Reason: " + e.getMessage(), e);
			} catch (TransactionException e) {
				if(transaction!=null){
				transaction.rollback();
				}
				throw new TransactionException("Error while processing CDR for usage metering. Reason: " + e.getMessage(), e);
			}finally{
				DBUtility.closeQuietly(preparedStatement);
				try {
					if(transaction!=null){
					transaction.end();
					}
				} catch (TransactionException e) {
					LogManager.ignoreTrace(e);
					//IGNORED
			}
		}
		}
		
		@Override
		public void updateCDR(ValueProviderExtImpl valueProvider) throws TransactionException {
			
			PCRFResponse response = valueProvider.getResponse();
			
			String subscriberID = response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
					
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Update CDR operation started in CDR driver: " + getName() + " for subscriber ID: " + subscriberID);
			}
			
			PreparedStatement preparedStatement = null;
			Transaction transaction =null;
			try {
				int index;
				TransactionFactory transactionFactory=getTransactionFactoryGroup().getTransactionFactory();
				if(transactionFactory==null){
					LogManager.getLogger().error(MODULE, "Error in Usage Metering CDR operation. Reason: Could not create Transaction Factory");
					return;
				}
				transaction=transactionFactory.createTransaction();
				if(transaction==null){
					LogManager.getLogger().error(MODULE, "Error in Usage Metering CDR operation. Reason: Could not create Transaction");
					return;
				}
				transaction.begin();
				preparedStatement = transaction.prepareStatement(getUpdateQuery());
				if(preparedStatement==null){
					LogManager.getLogger().error(MODULE, "Error in Usage Metering CDR operation. Reason: Could not create Prepared Statement");
					return;
				}
				List<UsageMonitoringInfo> reportedUnits = response.getReportedUsageInfoList();
				if(reportedUnits == null) {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Skipping Usage Metering CDR for this response for session-Id "+
			                      response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val)+" for response Type "+response.getAttribute(PCRFKeyConstants.REQUEST_TYPE.val) +" Reason: Usage Monitoring info not found");
					return; 
				}
				long currentTime = new Date().getTime();
				Timestamp timestamp = new Timestamp(currentTime);
				for (UsageMonitoringInfo monitoringInfo : reportedUnits ) {
					index = 0;
					ServiceUnit serviceUnit = monitoringInfo.getUsedServiceUnit();
					for(DBFieldMapping mapping : getDBFieldMappings())
						setValue(++index, mapping.getDataType(), getValue(valueProvider, serviceUnit, mapping.getKey(), monitoringInfo.getMonitoringKey(), mapping.getDefaultValue()), preparedStatement);
					
					preparedStatement.setTimestamp(++index, timestamp); // for last modified date field
					preparedStatement.setTimestamp(++index, timestamp); // for timestamp field
					preparedStatement.setString(++index, response.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal())+":"+monitoringInfo.getMonitoringKey());
					
					if(executeQuery(preparedStatement) == 0) 
						insertCDR(valueProvider);
				}
				
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Update CDR operation completed in CDR driver: " + getName() + " for subscriber ID: " + subscriberID);
				}
				
			} catch (SQLException e) {
				if(transaction!=null){
				transaction.rollback();
					if(transaction.isDBDownSQLException(e))
						transaction.markDead();
				}
				throw new TransactionException("Error while processing CDR for usage metering. Reason: " + e.getMessage(), e);
			} catch (TransactionException e) {
				if(transaction!=null){
				transaction.rollback();
				}
				throw new TransactionException("Error while processing CDR for usage metering. Reason: " + e.getMessage(), e);
			} finally{
				try {
					if(transaction!=null){
					transaction.end();
					}
				} catch (TransactionException e) {
					LogManager.ignoreTrace(e);
					//IGNORED
				}
				DBUtility.closeQuietly(preparedStatement);
			}
		}
	}

	private  class UMDBCDRBatchBuilder extends UMDBCDRBuilder {

		private static final int QUEUE_SIZE = 10000;				
		private long interval;		
		private int queryTimeout;
		
		private ConcurrentLinkedQueue<ValueProviderExtImpl> batchOperationQueue;
		public UMDBCDRBatchBuilder(TransactionFactoryGroupImpl transactionFactoryGroup){
			super(transactionFactoryGroup);			
			interval=getBatchUpdateInterval();
			queryTimeout=getBatchQueryTimeOut();
			batchOperationQueue=new ConcurrentLinkedQueue<>(QUEUE_SIZE);
		}
		@Override
		public void init() throws InitializationFailedException{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Initializing CDR Batch opeation for Usage Metering");
			getTaskScheduler().scheduleIntervalBasedTask(new BatchUpdateTask());
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "CDR Batch operation for Usage Metring initialized successfully");
		}
		
		@Override
		public void insertCDR(ValueProviderExtImpl valueProvider){
			PCRFRequest pcrfRequest= valueProvider.getRequest();
			List<UsageMonitoringInfo> reportedUnits = pcrfRequest.getReportedUsageInfoList();
			if(reportedUnits == null) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Skipping Usage Metering CDR for this request for session-Id "+
		                      pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val)+" for request Type "+pcrfRequest.getAttribute(PCRFKeyConstants.REQUEST_TYPE.val) +" Reason: Usage Monitoring info not found");
				}					
			return;	
			}
			if(!batchOperationQueue.offer(valueProvider)){
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
					LogManager.getLogger().debug(MODULE, "Performing batch operation for Usage Metering CDR");
					
				
				if(batchOperationQueue.size()<=0){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE,"Skipping batch Operation for Usage Metering CDR. Reason: No request found in queue");
					}
					return;
				}	
				PreparedStatement psInsert = null;
				Transaction transaction=null;
				try{
					TransactionFactory transactionFactory = getTransactionFactoryGroup().getTransactionFactory();
					if(transactionFactory == null) {
						LogManager.getLogger().error(MODULE, "Error in Usage Metering CDR batch operation. Reason: Could not create transactionFactory");
						return;
					}
					transaction=transactionFactory.createTransaction();
					if(transaction==null){
						LogManager.getLogger().error(MODULE, "Error in Usage Metering CDR batch operation. Reason: Could not create transaction");
						return;
					}
					transaction.begin();
					psInsert = transaction.prepareStatement(getInsertQuery());
					if(psInsert==null){
						LogManager.getLogger().error(MODULE, "Error in Usage Metering CDR batch operation. Reason: Could not create Prepared Statement");
						return;
					}
					
					
					psInsert.setQueryTimeout(queryTimeout);
			
					int batchCnt;
					batchCnt = 0;
					int index = 0;
					while(true) {
						ValueProviderExtImpl valueProvider =  batchOperationQueue.poll();
						if(batchCnt > 0 && (batchCnt >= getBatchSize() || valueProvider == null)){
							long startTime = System.currentTimeMillis();
							psInsert.executeBatch();
							transaction.commit();
							long endTime = System.currentTimeMillis();
							LogManager.getLogger().warn(MODULE,"DB Update time(ms): " + (endTime - startTime) + " Total records : "+ batchCnt);
							batchCnt = 0;
						}

						if(valueProvider == null) {
							break;
						}
						List<UsageMonitoringInfo> reportedUnits = valueProvider.getResponse().getReportedUsageInfoList();
						for (UsageMonitoringInfo monitoringInfo : reportedUnits) {
							index = 0;
							ServiceUnit serviceUnit = monitoringInfo.getUsedServiceUnit();
							for(DBFieldMapping mapping : getDBFieldMappings())
								setValue(++index, mapping.getDataType(), getValue(valueProvider, serviceUnit, mapping.getKey(), monitoringInfo.getMonitoringKey(), mapping.getDefaultValue()), psInsert);
							Timestamp timestamp = new Timestamp(new Date().getTime());
							psInsert.setTimestamp(++index, timestamp); // for create date field
							psInsert.setTimestamp(++index, timestamp);// for timestamp field
							psInsert.addBatch();
							psInsert.clearParameters();
							batchCnt++;
						}
					}
				}catch (TransactionException e) {
					if(transaction!=null)
						transaction.rollback();
					LogManager.getLogger().error(MODULE, "Error while performing batch operation for Usage Metering CDR. Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}catch (SQLException e){
					if(transaction!=null){
						transaction.rollback();
						if(transaction.isDBDownSQLException(e))
							transaction.markDead();
					}
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in Batch operation for Usage Metering CDR, Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}catch (Exception e) {
					if(transaction!=null)
						transaction.rollback();
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in Batch operation for Usage Metering CDR, Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				} finally {
					DBUtility.closeQuietly(psInsert);
					if(transaction!=null){
					try {
						transaction.end();
					} catch (Exception e) {
						if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
							LogManager.getLogger().error(MODULE, "Error in ending transaction, Reason: " + e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
					}
				}
				}
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Batch operation for Usage Metering CDR completed");
			}
		}
	}
	
	private String getValue(ValueProviderExtImpl request, ServiceUnit serviceUnit, String pcrfKey, String usageKey, String defaultValue) {

		switch(PCRFKeyConstants.fromKeyConstants(pcrfKey)) {
			case CS_SESSION_ID :
				return request.getRequest().getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal()) + ":" + usageKey;
			case CDR_USAGEKEY :
				return usageKey;
			case CDR_INPUTOCTETS :
				return String.valueOf(serviceUnit.getInputOctets());
			case CDR_OUTPUTOCTETS :
				return String.valueOf(serviceUnit.getOutputOctets());
			case CDR_TOTALOCTETS :
				return String.valueOf(serviceUnit.getTotalOctets());
			case CDR_USAGETIME :
				return String.valueOf(serviceUnit.getTime());
			default :
				return getParameterValue(request, pcrfKey, defaultValue);
		}
	}
	
	@Override
	public String getParameterValue(ValueProviderExtImpl request, String key, String defaultValue) {
		String value = ((PCRFRequest) request).getAttribute(key); 
		return value == null ? defaultValue : value;
	}
	
	@Override
	public String getDriverInstanceId() {
		return dbCDRDriverConf.getDriverInstanceId();
	}

	@Override
	public String getDriverInstanceUUID() {
		return null;
	}
	
	@Override
	public int getDriverType() {
		return DriverTypes.DB_CDR_DRIVER;
	}

	@Override
	public String getDriverName() {
		return dbCDRDriverConf.getDriverName();
	}
	
	@Override
	public String getTypeName() {
		return MODULE;
	}
	
	@Override
	public String getName() {
		return getDriverName();
	}
	
	@Override
	public String getTableName() {
		return dbCDRDriverConf.getTableName();
	}
	
	@Override
	public List<DBFieldMapping> getDBFieldMappings() {
		return dbCDRDriverConf.getDBFieldMappings();
	}

	@Override
	public boolean isStoreAllCDRs() {
		return dbCDRDriverConf.isStoreAllCDRs();
	}

	@Override
	public DBDataSource getDataSource() {
		return dataSource;
	}

	@Override
	public String getIdentityField() {
		return dbCDRDriverConf.getIdentityField();
	}

	@Override
	public String getSequenceName() {
		return dbCDRDriverConf.getSequenceName();
	}

	@Override
	public String getSessionIdField() {
		return dbCDRDriverConf.getSessionIdField();
	}

	@Override
	public String getCreateDateField() {
		return dbCDRDriverConf.getCreateDateField();
	}
	
	@Override
	public String getTimestampField() {
		return dbCDRDriverConf.getTimestampField();
	}

	@Override
	public String getLastModifiedDateField() {
		return dbCDRDriverConf.getLastModifiedDateField();
	}

	@Override
	public int getQueryTimeout() {
		return dbCDRDriverConf.getDBQueryTimeout();
	}

	@Override
	public int getMaxQueryTimeoutCount() {
		return dbCDRDriverConf.getMaxQueryTimeoutCount();
	}
	
	@Override
	public String getIdentityFieldValue(ValueProviderExtImpl valueProvider) {
		return valueProvider.getResponse().getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal());
	}

	@Override
	public boolean isBatchUpdate() {
		
		return dbCDRDriverConf.isBatchUpdate();
	}

	@Override
	public int getBatchSize() {	
		return dbCDRDriverConf.getBatchSize();
	}

	@Override
	public int getBatchUpdateInterval() {
	
		return dbCDRDriverConf.getBatchUpdateInterval();
	}

	@Override
	public int getBatchQueryTimeOut() {
		return dbCDRDriverConf.getBatchUpdateQueryTimeout();
	}
    
	
	
	
}
