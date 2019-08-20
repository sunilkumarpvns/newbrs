package com.elitecore.corenetvertex.spr;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Preconditions;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.ConcurrentLinkedQueue;

public class UMBatchOperation extends UMOperation {
	private static final String MODULE = "UM-BATCH-OP";
	private static final int QUEUE_SIZE = 10000;

	private final long interval = 100;

	private final ConcurrentLinkedQueue<BatchOperationData> batchOperationQueue;
	
	private final ReentrantLock sharedLock;
	private final TransactionFactory transactionFactory;
	private ScheduledExecutorService executor;
	private int batchSize;
	private int batchQueryTimeout;
	private ShutdownHook shutdownHook;
	private FailOverOperation<BatchOperationData> failOverOperation;
	private ScheduledThreadPoolExecutor failOverExecutor;
	private CSVFailOverOperation<BatchOperationData> csvfailOverOperation;
	
	public UMBatchOperation(TransactionFactory transactionFactory,
                            AlertListener alertListener, PolicyRepository policyRepository, RecordProcessor<BatchOperationData> recordProcessor,
                            int batchSize, int batchQueryTimeout, String sprName) {
	
		super(alertListener, policyRepository);
		
		this.transactionFactory = Preconditions.checkNotNull(transactionFactory, "Transaction factory cannot be null");
		this.batchSize = batchSize;
		this.batchQueryTimeout = batchQueryTimeout;
		this.sharedLock = new ReentrantLock(true);
		this.batchOperationQueue = new ConcurrentLinkedQueue<BatchOperationData>(QUEUE_SIZE);
		String threadName = sprName + "-UM-BATCH-SCH";
		this.executor = new ScheduledThreadPoolExecutor(1, new EliteThreadFactory(threadName, threadName, Thread.NORM_PRIORITY));
		this.failOverExecutor = new ScheduledThreadPoolExecutor(1, new EliteThreadFactory("UM-BATCH-FAILOVER", "UM-BATCH-FAILOVER", Thread.NORM_PRIORITY));
		csvfailOverOperation = new CSVFailOverOperation<BatchOperationData>(recordProcessor, new ScheduledThreadPoolExecutor(1, new EliteThreadFactory("UM-BATCH-FAILOVER-CSV", "UM-BATCH-FAILOVER-CSV", Thread.NORM_PRIORITY)));
		failOverOperation = new DBSingleRecordFailoverOperation<BatchOperationData>(new UMOperation(alertListener, null)
				, csvfailOverOperation, transactionFactory
				, failOverExecutor);

	}

	public UMBatchOperation(TransactionFactory transactionFactory,
			AlertListener alertListener, PolicyRepository policyRepository, RecordProcessor<BatchOperationData> recordProcessor
			, DBSingleRecordFailoverOperation<BatchOperationData> failOverOperation
			, CSVFailOverOperation<BatchOperationData> csvFailOverOperation
			, int batchSize, int batchQueryTimeout, ScheduledExecutorService batchExecutor, ConcurrentLinkedQueue<BatchOperationData> batchOperationQueue) {
	
		super(alertListener, policyRepository);
		
		this.transactionFactory = transactionFactory;
		this.batchSize = batchSize;
		this.batchQueryTimeout = batchQueryTimeout;
		this.sharedLock = new ReentrantLock(true);
		this.failOverExecutor = new ScheduledThreadPoolExecutor(1, new EliteThreadFactory("UM-BATCH-FAILOVER", "UM-BATCH-FAILOVER", Thread.NORM_PRIORITY));
		this.executor = batchExecutor;
		this.csvfailOverOperation = csvFailOverOperation;
		this.failOverOperation = failOverOperation;
		this.batchOperationQueue = batchOperationQueue;
	}
	
	@Override
	public void init() {

		try {
			shutdownHook = new ShutdownHook(executor, failOverExecutor);
			Runtime.getRuntime().addShutdownHook(shutdownHook);
			executor.scheduleWithFixedDelay(new BatchUpdateTask(), interval, interval,  TimeUnit.MILLISECONDS);
		} catch (Exception ex) {
			getLogger().error(MODULE, "Error in adding shutdown hook. Reason: " + ex.getMessage());
			getLogger().trace(ex);
		}
	}

	@Override
	public void replace(String subscriberIdentity, Collection<SubscriberUsage> usages, TransactionFactory transactionFactory)
			throws OperationFailedException {
		
		List<BatchOperationData> unProcessedRecords = null;
		
		for (SubscriberUsage usage : usages) {
			
			BatchOperationData batchData = new BatchOperationData(usage, subscriberIdentity, BatchOperationData.REPLACE);
			
			if (batchOperationQueue.add(batchData) == false) {
				
				getLogger().error(MODULE, "Set usage operation failed for user " + subscriberIdentity + ". Reason: Queue is full");
				
				if (unProcessedRecords == null) {
					unProcessedRecords = new ArrayList<UMBatchOperation.BatchOperationData>();
				}
				
				unProcessedRecords.add(batchData);
			}
		}
		
		if (Collectionz.isNullOrEmpty(unProcessedRecords) == false) {
			doFailover(unProcessedRecords);
		}
		
	}
	
	private void doFailover(List<BatchOperationData> unProcessedRecords) {
		
		getLogger().info(MODULE, "Processing non added records in batch queue. Reason: Queue was full");
		
		if (transactionFactory == null || transactionFactory.isAlive() == false) {
		
			getLogger().error(MODULE, "Unable to perform failover operation.. Reason: Datasource not available. Writing data to CSV");
			csvfailOverOperation.doFailover(unProcessedRecords);
			return;
		}
		
		failOverOperation.doFailover(unProcessedRecords);
	}

	@Override
	public void addToExisting(String subscriberIdentity, Collection<SubscriberUsage> usages, TransactionFactory transactionFactory)
			throws OperationFailedException {
		
		List<BatchOperationData> unProcessedRecords = null;
		
		for (SubscriberUsage usage : usages) {
			
			BatchOperationData batchData = new BatchOperationData(usage, subscriberIdentity, BatchOperationData.ADD_TO_EXISTING);
			
			if (batchOperationQueue.add(batchData) == false) {
				
				getLogger().error(MODULE, "Add usage operation failed for user " + subscriberIdentity + ". Reason: Queue is full");
				
				if (unProcessedRecords == null) {
					unProcessedRecords = new ArrayList<UMBatchOperation.BatchOperationData>();
				}
				
				unProcessedRecords.add(batchData);
			}
		}
		
		if (Collectionz.isNullOrEmpty(unProcessedRecords) == false) {
			doFailover(unProcessedRecords);
		}
		
	}

	@Override
	public void insert(String subscriberIdentity, Collection<SubscriberUsage> usages, TransactionFactory transactionFactory)
			throws OperationFailedException {
		
		List<BatchOperationData> unProcessedRecords = null;
		
		for (SubscriberUsage usage : usages) {
			
			BatchOperationData batchData = new BatchOperationData(usage, subscriberIdentity, BatchOperationData.INSERT);
			
			if (batchOperationQueue.add(batchData) == false) {
				
				getLogger().error(MODULE, "Insert operation failed for user " + subscriberIdentity + ". Reason: Queue is full");
				
				if (unProcessedRecords == null) {
					unProcessedRecords = new ArrayList<UMBatchOperation.BatchOperationData>();
				}
				
				unProcessedRecords.add(batchData);
			}
		}
		
		if (Collectionz.isNullOrEmpty(unProcessedRecords) == false) {
			doFailover(unProcessedRecords);
		}
	}


	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println(" -- UM DB Batch Operation -- ");
		out.println();
		out.println("Batch Query-Timeout =" + getBatchQueryTimeout() + " sec");
		out.println("Queue Size =" + QUEUE_SIZE);
		out.println("Batch Size =" + getBatchSize());
		out.println("Interval =" + interval + " sec");
		out.println();

		return stringBuffer.toString();
	}

	private int getBatchSize() {
		return batchSize;
	}

	private int getBatchQueryTimeout() {
		return batchQueryTimeout;
	}

	private class BatchUpdateTask implements Runnable {

		
		@Override
		public void run() {
			
			long currentTime = getCurrentTime();
			List<BatchOperationData> failedBatchDataFromQueue = new ArrayList<UMBatchOperation.BatchOperationData>();
			PreparedStatement psInsert = null;
			PreparedStatement psUpdate = null;
			PreparedStatement psAddExisting = null;
			PreparedStatement psUsageHistory = null;
			
			if(batchOperationQueue.isEmpty()) {
				return;
			}
			
			if (transactionFactory.isAlive() == false) {
				getLogger().error(MODULE, "Unable to perform UM Batch operation.. Reason: Datasource not available. Writing Batch data to CSV");
				csvfailOverOperation.doFailover(batchOperationQueue.stream().limit(batchSize).collect(Collectors.toList()));
				return;
			}
			
			Transaction transaction = transactionFactory.createTransaction();
			if(transaction == null) {
				getLogger().error(MODULE, "Unable to perform UM Batch operation.. Reason: Datasource not available. Writing Batch data to CSV");
				csvfailOverOperation.doFailover(batchOperationQueue.stream().limit(batchSize).collect(Collectors.toList()));
				return;
			}
			
			try {
				transaction.begin();
				psInsert = transaction.prepareStatement(getUsageInsertQuery());
				psUpdate = transaction.prepareStatement(getReplaceQuery());
				psUsageHistory = transaction.prepareStatement(usageHistoryInsertQuery);
				psAddExisting = transaction.prepareStatement(getAddToExistingUsageUpdateQuery());

				execute(psInsert, psUpdate, psUsageHistory, psAddExisting, transaction, failedBatchDataFromQueue, currentTime);
			} catch (TransactionException e) {
				if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

					alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
							"Unable execute batch for Subscriber Usage with" + transaction.getDataSourceName() +
									" database. Reason: Connection not available");
					
					//FIXME Need to discuss the failover approach --Jay
					csvfailOverOperation.doFailover(failedBatchDataFromQueue);
				} else {
					failOverOperation.doFailover(failedBatchDataFromQueue);
				}
				
				getLogger().error(MODULE, "Error while performing usage metering operation. Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
				transaction.rollback();
			} catch (SQLException e) {
				transaction.rollback();

				if (transaction.isDBDownSQLException(e)) {
					
					csvfailOverOperation.doFailover(failedBatchDataFromQueue);
					transaction.markDead();
				} else {
					failOverOperation.doFailover(failedBatchDataFromQueue);
				}
				
				getLogger().error(MODULE, "Error while performing usage metering operation. Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			} catch (Exception e) {
				getLogger().error(MODULE, "Error while performing usage metering operation. Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
				transaction.rollback();
				failOverOperation.doFailover(failedBatchDataFromQueue);
			
			} finally {
				closeStatement(psInsert, transaction);
				closeStatement(psUpdate, transaction);
				closeStatement(psAddExisting, transaction);
				closeStatement(psUsageHistory, transaction);
				closeTransaction(transaction);

				try {
					if (sharedLock.isHeldByCurrentThread()) {
						sharedLock.unlock();
					}
				}catch(Exception ex) { 
					getLogger().error(MODULE, "error while releasing usage lock. Reason:" + ex.getMessage());
					ignoreTrace(ex);
				}
				
			}
		}

		private void execute(PreparedStatement psInsert, PreparedStatement psUpdate, PreparedStatement psForUsageHistory, PreparedStatement psForAddExisting, Transaction transaction, List<BatchOperationData> processedDatas, long currentTime) throws TransactionException,
				SQLException {

			psInsert.setQueryTimeout(getBatchQueryTimeout());
			psUpdate.setQueryTimeout(getBatchQueryTimeout());
			
			int batchCnt = 0;

			while (true) {
				
				if (batchCnt >= getBatchSize()) {
					long startTime = System.currentTimeMillis();
					flush(psInsert, psUpdate, psForUsageHistory, psForAddExisting, transaction);
					processedDatas.clear();
					long endTime = System.currentTimeMillis();
					getLogger().warn(MODULE, "DB Update time(ms): " + (endTime - startTime) + " Total records : " + batchCnt);
					batchCnt = 0;
				}
				
				BatchOperationData data = batchOperationQueue.poll();
				
				if (batchCnt > 0 && data == null) {
					long startTime = System.currentTimeMillis();
					flush(psInsert, psUpdate, psForUsageHistory, psForAddExisting, transaction);
					processedDatas.clear();
					long endTime = System.currentTimeMillis();
					getLogger().warn(MODULE, "DB Update time(ms): " + (endTime - startTime) + " Total records : " + batchCnt);
					batchCnt = 0;
				}

				if (data == null) {
					break;
				}

				if (data.getOperation() == BatchOperationData.INSERT) {
					insert(psInsert, data, currentTime);
				} else if (data.getOperation() == BatchOperationData.ADD_TO_EXISTING) {
					addToExisting(psForAddExisting, data);
				} else if (data.getOperation() == BatchOperationData.REPLACE) {
					// Adding previous usage record to history table before replacing, do not change sequence
					addToUsageHistory(psForUsageHistory, data, currentTime);
					replace(psUpdate, data, currentTime);
				}
				
				processedDatas.add(data);
				batchCnt++;
			}
		}
		
		private void addToUsageHistory(PreparedStatement psForUsageHistory,
				BatchOperationData data, long currentTime) throws SQLException {
			
			SubscriberUsage usage = data.getUsage();
			psForUsageHistory.setString(1, generateId());
			psForUsageHistory.setString(2, usage.getId());
			psForUsageHistory.addBatch();
			psForUsageHistory.clearParameters();
		}

		private void addToExisting(PreparedStatement psForAddToExisting, BatchOperationData data) throws SQLException {
			
			SubscriberUsage usage = data.getUsage();
			setUsageToPSForAddToExisting(psForAddToExisting, usage);
			psForAddToExisting.addBatch();
			psForAddToExisting.clearParameters();
			
		}

		private void replace(PreparedStatement psForUpdate, BatchOperationData data, long currentTime) throws SQLException {

			SubscriberUsage usage = data.getUsage();
			setUsageToPSForReplace(psForUpdate, currentTime, usage);
			psForUpdate.addBatch();
			psForUpdate.clearParameters();
		}

		private void insert(PreparedStatement psforInsert, BatchOperationData data, long currentTime) throws SQLException {

			SubscriberUsage usage = data.getUsage();
			setUsageToPSForInsert(psforInsert, currentTime, usage);
			psforInsert.addBatch();
			psforInsert.clearParameters();
		}

		public void flush(PreparedStatement psInsert, PreparedStatement psUpdate, PreparedStatement psForUsageHistory, PreparedStatement psForAddExisting, Transaction transaction) throws SQLException, TransactionException {
			sharedLock.lock();
			
			try {
				psInsert.executeBatch();
				transaction.commit();
				
				psForAddExisting.executeBatch();
				transaction.commit();
				
				psForUsageHistory.executeBatch();
				transaction.commit();
				
				psUpdate.executeBatch();
				transaction.commit();
			} finally {
				sharedLock.unlock();
			}
			
		}

		public void closeTransaction(Transaction transaction) {
			if (transaction == null) {
				return;
			}
			try {
				transaction.end();
			} catch (Exception e) {
				getLogger().trace(MODULE, e);
			}
		}

		public void closeStatement(PreparedStatement statement, Transaction transaction) {
			if (statement == null) {
				return;
			}

			try {
				statement.close();
			} catch (SQLException e) {
				if (transaction.isDBDownSQLException(e)) {
					transaction.markDead();
				}
				if (getLogger().isLogLevel(LogLevel.ERROR)) {
					getLogger().error(MODULE, "Error in closing prepared Statement, Reason: " + e.getMessage());
				}
				getLogger().trace(MODULE, e);
			}

		}
	}

	public class BatchOperationData {
		public static final int INSERT = 0;
		public static final int ADD_TO_EXISTING = 1;
		public static final int REPLACE = 2;
		private SubscriberUsage subscriberUsage;
		private int operation;
		private String subscriberIdentiry;


		public BatchOperationData(SubscriberUsage subscriberUsage, String subscriberIdentity, int operation) {
			this.subscriberUsage = subscriberUsage;
			this.subscriberIdentiry = subscriberIdentity;
			this.operation = operation;
		}

		public SubscriberUsage getUsage() {
			return subscriberUsage;
		}

		public String getSubscriberIdentity() {
			return subscriberIdentiry;
		}

		public int getOperation() {
			return operation;
		}
	}

	private class ShutdownHook extends Thread {
		private ScheduledExecutorService executor;
		private ScheduledExecutorService failOverExecutor;

		public ShutdownHook(ScheduledExecutorService executor, ScheduledExecutorService failOverExecutor) {
			this.executor = executor;
			this.failOverExecutor = failOverExecutor;
		}

		public void run() {
			shutDownExecutor(executor);
			shutDownExecutor(failOverExecutor);
		}

		private void shutDownExecutor(ScheduledExecutorService executor) {
			
			try {
				executor.shutdown();
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Waiting for UMBatchOperation level scheduled async task executor to complete execution");
				if (executor.awaitTermination(5, TimeUnit.SECONDS) == false) {
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Shutting down UMBatchOperation level scheduled async task executor forcefully. " +
								"Reason: Async task taking more than 5 second to complete");
					executor.shutdownNow();
				}
			} catch (Exception ex) { 
				try {
					executor.shutdownNow();
				} catch (Exception e) { 
					getLogger().trace(MODULE, e);
				}
				ignoreTrace(ex);
			}
		}
	}
	
	public void stop() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Stopping UM Batch scheduler");
		}try {
			
			shutdownHook.run();
		} catch (Throwable e) {
			getLogger().error(MODULE, "Error while stopping UM Batch scheduler");
			getLogger().trace(MODULE, e);
		}
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "UM Batch scheduler stopped successfully");
		}
	}
	
}

