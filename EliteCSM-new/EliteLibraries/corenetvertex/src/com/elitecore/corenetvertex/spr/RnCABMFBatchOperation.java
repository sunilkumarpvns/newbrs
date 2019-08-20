package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.Collectionz;
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.commons.logging.LogManager.ignoreTrace;

public class RnCABMFBatchOperation extends RnCABMFOperation{
	
    private static final String MODULE = "ABMF-RNC-BATCH-OP";
    private static final int QUEUE_SIZE = 10000;

    private static final long INTERVAL = 100;

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

    public RnCABMFBatchOperation(TransactionFactory transactionFactory,
                            AlertListener alertListener, PolicyRepository policyRepository, RecordProcessor<BatchOperationData> recordProcessor,
                            int batchSize, int batchQueryTimeout, int queryTimeOut) {

        super(alertListener, policyRepository, queryTimeOut);

        this.transactionFactory = transactionFactory;
        this.batchSize = batchSize;
        this.batchQueryTimeout = batchQueryTimeout;
        this.sharedLock = new ReentrantLock(true);
        this.batchOperationQueue = new ConcurrentLinkedQueue<>(QUEUE_SIZE);
        this.executor = new ScheduledThreadPoolExecutor(1, new EliteThreadFactory("ABMF-RNC-BATCH-SCH", "ABMF-RNC-BATCH-SCH", Thread.NORM_PRIORITY));
        this.failOverExecutor = new ScheduledThreadPoolExecutor(1, new EliteThreadFactory("ABMF-RNC-BATCH-FAILOVER", "ABMF-RNC-BATCH-FAILOVER", Thread.NORM_PRIORITY));
        csvfailOverOperation = new CSVFailOverOperation<>(recordProcessor, new ScheduledThreadPoolExecutor(1, new EliteThreadFactory("ABMF-RNC-BATCH-FAILOVER-CSV", "ABMF-RNC-BATCH-FAILOVER-CSV", Thread.NORM_PRIORITY)));

        failOverOperation = new DBSingleRecordFailoverOperation<>(new RnCABMFOperation(alertListener, policyRepository, queryTimeOut)
                , csvfailOverOperation, transactionFactory
                , failOverExecutor);

    }

    @VisibleForTesting
    RnCABMFBatchOperation(TransactionFactory transactionFactory,
                       AlertListener alertListener, PolicyRepository policyRepository,
                       DBSingleRecordFailoverOperation<BatchOperationData> failOverOperation
            , CSVFailOverOperation<BatchOperationData> csvFailOverOperation
            , ScheduledThreadPoolExecutor failOverExecutor
            , int batchSize, int batchQueryTimeout, ScheduledExecutorService batchExecutor, int queryTimeOut, ConcurrentLinkedQueue<BatchOperationData> batchOperationQueue) {
        
    	super(alertListener, policyRepository, queryTimeOut);
        
    	this.transactionFactory = transactionFactory;
        this.batchSize = batchSize;
        this.batchQueryTimeout = batchQueryTimeout;
        this.sharedLock = new ReentrantLock(true);
        this.failOverExecutor = failOverExecutor;
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
            executor.scheduleWithFixedDelay(new BatchUpdateTask(), INTERVAL, INTERVAL,  TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            getLogger().error(MODULE, "Error in adding shutdown hook. Reason: " + ex.getMessage());
            getLogger().trace(ex);
        }
    }


    @Override
    public void reportBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> rncNonMonetaryBalances, TransactionFactory transactionFactory)
            throws OperationFailedException {
    	
        List<RnCABMFBatchOperation.BatchOperationData> unProcessedRecords = null;
        
        for (RnCNonMonetaryBalance balance : rncNonMonetaryBalances) {
        	
            BatchOperationData batchData = new BatchOperationData(balance, subscriberIdentity, BatchOperation.REPORT);
            
            if (batchOperationQueue.add(batchData) == false) {
                getLogger().error(MODULE, "Report rnc balance operation failed for user " + subscriberIdentity + ". Reason: Queue is full");
                if (unProcessedRecords == null) {
                    unProcessedRecords = new ArrayList<>();
                }

                unProcessedRecords.add(batchData);
            }

        }

        if (Collectionz.isNullOrEmpty(unProcessedRecords) == false) {
            doFailover(unProcessedRecords);
        }

    }

    @Override
    public void refundBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> rncNonMonetaryBalances, TransactionFactory transactionFactory)
            throws OperationFailedException {

        List<RnCABMFBatchOperation.BatchOperationData> unProcessedRecords = null;

        for (RnCNonMonetaryBalance balance : rncNonMonetaryBalances) {

            BatchOperationData batchData = new BatchOperationData(balance, subscriberIdentity, BatchOperation.REFUND);

            if (batchOperationQueue.add(batchData) == false) {
                getLogger().error(MODULE, "Refund rnc balance operation failed for user " + subscriberIdentity + ". Reason: Queue is full");
                if (unProcessedRecords == null) {
                    unProcessedRecords = new ArrayList<>();
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
    public void reserveBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> rncNonMonetaryBalances, TransactionFactory transactionFactory)
            throws OperationFailedException {
    	
        List<RnCABMFBatchOperation.BatchOperationData> unProcessedRecords = null;
        
        for (RnCNonMonetaryBalance balance : rncNonMonetaryBalances) {
        	
            BatchOperationData batchData = new BatchOperationData(balance, subscriberIdentity, BatchOperation.RESERVE);
            
            if (batchOperationQueue.add(batchData) == false) {
                getLogger().error(MODULE, "Reserve rnc balance operation failed for user " + subscriberIdentity + ". Reason: Queue is full");
                if (unProcessedRecords == null) {
                    unProcessedRecords = new ArrayList<>();
                }

                unProcessedRecords.add(batchData);
            }
        }
        if (Collectionz.isNullOrEmpty(unProcessedRecords) == false) {
            doFailover(unProcessedRecords);
        }
    }

    @Override
    public void reportAndReserveBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> rncNonMonetaryBalances, TransactionFactory transactionFactory) {
        
    	List<RnCABMFBatchOperation.BatchOperationData> unProcessedRecords = null;
        
    	for (RnCNonMonetaryBalance balance : rncNonMonetaryBalances) {
    		
            BatchOperationData batchData = new BatchOperationData(balance, subscriberIdentity, BatchOperation.REPORT_AND_RESERVE);
            
            if (batchOperationQueue.add(batchData) == false) {
                getLogger().error(MODULE, "Report and reserve rnc balance operation failed for user " + subscriberIdentity + ". Reason: Queue is full");
                if (unProcessedRecords == null) {
                    unProcessedRecords = new ArrayList<>();
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
        out.println(" -- RnC ABMF DB Batch Operation -- ");
        out.println();
        out.println("Batch Query-Timeout =" + getBatchQueryTimeout() + " sec");
        out.println("Queue Size =" + QUEUE_SIZE);
        out.println("Batch Size =" + getBatchSize());
        out.println("Interval =" + INTERVAL + " sec");
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

            if(batchOperationQueue.isEmpty()) {
                return;
            }

            if (transactionFactory.isAlive() == false) {
                getLogger().error(MODULE, "Unable to perform ABMF Batch operation.. Reason: Datasource not available. Writing Batch data to CSV");
                csvfailOverOperation.doFailover(batchOperationQueue.stream().limit(batchSize).collect(Collectors.toList()));
                return;
            }

            Transaction transaction = transactionFactory.createTransaction();
            if(transaction == null) {
                getLogger().error(MODULE, "Unable to perform ABMF Batch operation.. Reason: Datasource not available. Writing Batch data to CSV");
                csvfailOverOperation.doFailover(batchOperationQueue.stream().limit(batchSize).collect(Collectors.toList()));
                return;
            }

            List<BatchOperationData> failedBatchDataFromQueue = new ArrayList<>();
            PreparedStatement psForReport = null;
            PreparedStatement psForRefund = null;
            PreparedStatement psForReserve = null;
            PreparedStatement psForReportAndReserve = null;




            try {
                transaction.begin();
                psForReport = transaction.prepareStatement(REPORT_RNC_BALANCE_QUERY);
                psForReserve = transaction.prepareStatement(RESERVE_RNC_BALANCE_QUERY);
                psForReportAndReserve = transaction.prepareStatement(REPORT_AND_RESERVE_RNC_BALANCE_QUERY);
                psForRefund = transaction.prepareStatement(REFUND_RNC_BALANCE_QUERY);


                execute(psForReport, psForReserve, psForReportAndReserve, psForRefund, transaction, failedBatchDataFromQueue);
                
            } catch (TransactionException e) {
            	
                if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

                    alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
                            "Unable to execute batch for Subscriber Usage with" + transaction.getDataSourceName() +
                                    " database. Reason: Connection not available");

                    csvfailOverOperation.doFailover(failedBatchDataFromQueue);
                } else {
                    failOverOperation.doFailover(failedBatchDataFromQueue);
                }

                getLogger().error(MODULE, "Error while performing ABMF operation. Reason: " + e.getMessage());
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

                getLogger().error(MODULE, "Error while performing ABMF operation. Reason: " + e.getMessage());
                getLogger().trace(MODULE, e);
            } catch (Exception e) {
                getLogger().error(MODULE, "Error while performing ABMF operation. Reason: " + e.getMessage());
                getLogger().trace(MODULE, e);
                transaction.rollback();
                failOverOperation.doFailover(failedBatchDataFromQueue);

            } finally {
            	
                closeStatement(psForReport, transaction);
                closeStatement(psForReserve, transaction);
                closeStatement(psForReportAndReserve, transaction);
                closeStatement(psForRefund, transaction);
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

        private void execute(PreparedStatement psForReport, PreparedStatement psForReserve, PreparedStatement psForReportAndReserve, PreparedStatement psForRefund, Transaction transaction, List<BatchOperationData> processedDatas)
        		throws TransactionException, SQLException {

            psForReport.setQueryTimeout(getBatchQueryTimeout());
            psForReserve.setQueryTimeout(getBatchQueryTimeout());
            psForReportAndReserve.setQueryTimeout(getBatchQueryTimeout());
            psForRefund.setQueryTimeout(getBatchQueryTimeout());

            int batchCnt = 0;

            while (true) {

                if (batchCnt >= getBatchSize()) {
                    long startTime = System.currentTimeMillis();
                    flush(psForReport, psForReserve, psForReportAndReserve, psForRefund, transaction);
                    processedDatas.clear();
                    long endTime = System.currentTimeMillis();
                    getLogger().warn(MODULE, "DB Update time(ms): " + (endTime - startTime) + " Total records : " + batchCnt);
                    batchCnt = 0;
                }


                BatchOperationData data = batchOperationQueue.poll();

                if (batchCnt > 0 && data == null) {
                    long startTime = System.currentTimeMillis();
                    flush(psForReport, psForReserve, psForReportAndReserve, psForRefund, transaction);
                    processedDatas.clear();
                    long endTime = System.currentTimeMillis();
                    getLogger().warn(MODULE, "DB Update time(ms): " + (endTime - startTime) + " Total records : " + batchCnt);
                    batchCnt = 0;
                }

                if (data == null) {
                    break;
                }

                if (data.getOperation() == BatchOperation.REPORT) {
                    reportBalance(psForReport, data);
                } else if (data.getOperation() == BatchOperation.RESERVE) {
                    reserveBalance(psForReserve, data);
                } else if (data.getOperation() == BatchOperation.REPORT_AND_RESERVE) {
                    reportAndReserveBalance(psForReportAndReserve, data);
                } else if (data.getOperation() == BatchOperation.REFUND) {
                    refundBalance(psForRefund, data);
                }

                processedDatas.add(data);
                batchCnt++;
            }
        }

        private void reportBalance(PreparedStatement psForReport, BatchOperationData data) throws SQLException {
        	
        	RnCNonMonetaryBalance rncNonMonetaryBalance = data.getRnCNonMonetaryBalance();
            setBalanceToPsForReport(psForReport, rncNonMonetaryBalance);
            psForReport.addBatch();
            psForReport.clearParameters();
        }

        private void reserveBalance(PreparedStatement psForReserve, BatchOperationData data) throws SQLException {
        	
        	RnCNonMonetaryBalance rncNonMonetaryBalance = data.getRnCNonMonetaryBalance();
            setBalanceToPsForReserve(psForReserve, rncNonMonetaryBalance);
            psForReserve.addBatch();
            psForReserve.clearParameters();
        }

        private void reportAndReserveBalance(PreparedStatement psForReportAndReserve, BatchOperationData data) throws SQLException {
        	
        	RnCNonMonetaryBalance rncNonMonetaryBalance = data.getRnCNonMonetaryBalance();
            setBalanceToPsForReportAndReserve(psForReportAndReserve, rncNonMonetaryBalance);
            psForReportAndReserve.addBatch();
            psForReportAndReserve.clearParameters();
        }

        private void refundBalance(PreparedStatement psForRefund, BatchOperationData data) throws SQLException {

            RnCNonMonetaryBalance rncNonMonetaryBalance = data.getRnCNonMonetaryBalance();
            setBalanceToPsForRefund(psForRefund, rncNonMonetaryBalance);
            psForRefund.addBatch();
            psForRefund.clearParameters();
        }

        public void flush(PreparedStatement psForReport, PreparedStatement psForReserve, PreparedStatement psForReportAndReserve, PreparedStatement psForRefund, Transaction transaction) throws SQLException, TransactionException {
            sharedLock.lock();

            try {
                psForReport.executeBatch();
                transaction.commit();

                psForReserve.executeBatch();
                transaction.commit();

                psForReportAndReserve.executeBatch();
                transaction.commit();

                psForRefund.executeBatch();
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

    public static class BatchOperationData {

        private RnCNonMonetaryBalance rncNonMonetaryBalance;
        private BatchOperation operation;
        private String subscriberIdentiry;


        public BatchOperationData(RnCNonMonetaryBalance rncNonMonetaryBalance, String subscriberIdentity, BatchOperation operation) {
            this.rncNonMonetaryBalance = rncNonMonetaryBalance;
            this.subscriberIdentiry = subscriberIdentity;
            this.operation = operation;
        }

        public RnCNonMonetaryBalance getRnCNonMonetaryBalance() {
            return rncNonMonetaryBalance;
        }

        public String getSubscriberIdentity() {
            return subscriberIdentiry;
        }

        public BatchOperation getOperation() {
            return operation;
        }


    }

    public enum  BatchOperation {

        REPORT_AND_RESERVE,
        RESERVE,
        REPORT,
        REFUND;

    }

    private class ShutdownHook extends Thread {
    	
        private ScheduledExecutorService executor;
        private ScheduledExecutorService failOverExecutor;

        public ShutdownHook(ScheduledExecutorService executor, ScheduledExecutorService failOverExecutor) {
            this.executor = executor;
            this.failOverExecutor = failOverExecutor;
        }

        @Override
        public void run() {
            shutdownAllExecutors();
        }

        private void shutdownAllExecutors() {
            shutDownExecutor(executor);
            shutDownExecutor(failOverExecutor);
        }

        private void shutDownExecutor(ScheduledExecutorService executor) {

            try {
                executor.shutdown();
                if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
                    LogManager.getLogger().info(MODULE, "Waiting for ABMFBatchOperation level scheduled async task executor to complete execution");
                if (executor.awaitTermination(5, TimeUnit.SECONDS) == false) {
                    if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
                        LogManager.getLogger().warn(MODULE, "Shutting down ABMFBatchOperation level scheduled async task executor forcefully. " +
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

    @Override
    public void stop() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Stopping ABMF Batch scheduler");
        }

        try {
            shutdownHook.shutdownAllExecutors();
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while stopping ABMF Batch scheduler");
            getLogger().trace(MODULE, e);
        }
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "ABMF Batch scheduler stopped successfully");
        }
    }
}
