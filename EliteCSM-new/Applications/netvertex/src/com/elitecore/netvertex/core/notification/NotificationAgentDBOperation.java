package com.elitecore.netvertex.core.notification;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Stopwatch;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.db.Transaction;
import com.elitecore.core.commons.utilx.db.TransactionErrorCode;
import com.elitecore.core.commons.utilx.db.TransactionException;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.util.ConcurrentLinkedQueue;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.Notification;
import com.elitecore.netvertex.core.alerts.Alerts;
import com.elitecore.netvertex.core.util.DbConnectionManagerRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class NotificationAgentDBOperation implements NotificationDBOperation {

    private static final String MODULE = "NOTIFICATION-AGENT-DB-OPR";
    private static final String INSERT_QUERY = "INSERT INTO TBLT_NOTIFICATION_QUEUE " +
            "(ID, EMAIL_STATUS , SMS_STATUS , EMAIL_SUBJECT, EMAIL_DATA, SMS_DATA, PCRF_RESPONSE, SOURCE_ID, VALIDITY_DATE, NOTIFICATION_RECIPIENT, SUBSCRIBER_IDENTITY, TIMESTAMP) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
    private static final String INSERT_QUERY_FOR_HISTORY_DATA = "INSERT INTO TBLT_NOTIFICATION_HISTORY " +
            "(ID, EMAIL_STATUS , SMS_STATUS , EMAIL_SUBJECT, EMAIL_DATA, SMS_DATA, PCRF_RESPONSE, SOURCE_ID, VALIDITY_DATE, NOTIFICATION_RECIPIENT, SUBSCRIBER_IDENTITY, TIMESTAMP) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

    private static final int DEFAULT_QUEUE_SIZE = 10000;
    private static final long INTERVAL = 100;

    private NetVertexServerContext serverContext;

    private final ConcurrentLinkedQueue<Notification> batchOperationQueue;
    private final ConcurrentLinkedQueue<Notification> batchHistoryOperationQueue;
    private TaskScheduler notificationExecutor;
    private TaskScheduler historyExecutor;

    private  DBConnectionManager dbConnectionManager;
    private int queueSize ;



    public NotificationAgentDBOperation(NetVertexServerContext serverContext,
                                        TaskScheduler notificationExecutor,
                                        TaskScheduler historyExecutor,
                                        DBConnectionManager dbConnectionManager,int queueSize){
        this.serverContext = serverContext;
        this.batchOperationQueue = new ConcurrentLinkedQueue<>(queueSize);
        this.batchHistoryOperationQueue = new ConcurrentLinkedQueue<>(queueSize);
        this.notificationExecutor = notificationExecutor;
        this.historyExecutor = historyExecutor;
        this.dbConnectionManager = dbConnectionManager;
        this.historyExecutor.scheduleIntervalBasedTask(new BatchUpdateHistoryTask());
        this.notificationExecutor.scheduleIntervalBasedTask(new BatchUpdateTask());
        this.queueSize = queueSize;
    }


    public static NotificationAgentDBOperation create(NetVertexServerContext serverContext, TaskScheduler notificationExecutor, TaskScheduler historyExecutor,DbConnectionManagerRepository dbConnectionManagerRepository) throws InitializationFailedException {
        return create(serverContext,notificationExecutor,historyExecutor,dbConnectionManagerRepository,DEFAULT_QUEUE_SIZE);

    }


    public static NotificationAgentDBOperation create(NetVertexServerContext serverContext, TaskScheduler notificationExecutor, TaskScheduler historyExecutor,DbConnectionManagerRepository dbConnectionManagerRepository,int queueSize) throws InitializationFailedException {

        DBDataSource notificationDS =  serverContext.getServerConfiguration().getNetvertexServerGroupConfiguration().getNotificationDS();

        if (notificationDS == null) {
            getLogger().warn(MODULE, "Notification DB datasource is not configured");
            throw new InitializationFailedException("Notification DB datasource is not configured");
        }

        DBConnectionManager dbConnectionManager = dbConnectionManagerRepository.getDbConnectionManager(notificationDS.getDataSourceName());

        if (dbConnectionManager.isInitilized() == false) {
            try {
                dbConnectionManager.init(notificationDS, serverContext.getTaskScheduler());
            } catch (DatabaseInitializationException ex) {
                getLogger().warn(MODULE, "Error while initializing notification DB datasource " + notificationDS.getDataSourceName() + ". Reason : " + ex.getMessage());
                getLogger().trace(MODULE, ex);
            } catch (DatabaseTypeNotSupportedException ex) {
                throw new InitializationFailedException(ex);
            }
        }

        return new NotificationAgentDBOperation(serverContext, notificationExecutor, historyExecutor, dbConnectionManager,queueSize);
    }


    private class BatchUpdateHistoryTask extends BaseIntervalBasedTask {


        @Override
        public long getInterval() {
            return INTERVAL;
        }

        @Override
        public void execute(AsyncTaskContext context) {
            performOperation(INSERT_QUERY_FOR_HISTORY_DATA, batchHistoryOperationQueue);
        }

        @Override
        public boolean isFixedDelay() {
            return true;
        }

        @Override
        public long getInitialDelay() {
            return INTERVAL;
        }

        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.MILLISECONDS;
        }
    }

    private class BatchUpdateTask extends BaseIntervalBasedTask {

        @Override
        public long getInterval() {
            return INTERVAL;
        }

        @Override
        public void execute(AsyncTaskContext context) {
            performOperation(INSERT_QUERY, batchOperationQueue);
        }

        @Override
        public boolean isFixedDelay() {
            return true;
        }

        @Override
        public long getInitialDelay() {
            return INTERVAL;
        }

        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.MILLISECONDS;
        }
    }




    private void performOperation(String query, ConcurrentLinkedQueue<Notification> notifications) {


        Transaction transaction = null;

        PreparedStatement preparedStatement = null;

        try {
            TransactionFactory transactionFactory = dbConnectionManager.getTransactionFactory();

            if (transactionFactory.isAlive() == false) {
                LogManager.getLogger().warn(MODULE, "Skipping processing of notification agent task. Reason: Server DB is dead");
                return;
            }

            transaction = transactionFactory.createTransaction();
            transaction.begin();
            preparedStatement = transaction.prepareStatement(query);

            /*
             *
             * If batch is full, then commit
             * else if no more notifications, then commit,
             * else add notification to batch
             */
            int batchCnt = 0;
            while (true) {
                if (isBatchFull(batchCnt)) {
                    commitBatch(transaction, preparedStatement, batchCnt);
                    batchCnt = 0;
                }

                Notification notification = notifications.poll();
                if (batchCnt > 0 && notification == null) {
                    commitBatch(transaction, preparedStatement, batchCnt);
                    batchCnt = 0;
                }

                if (notification == null) {
                    break;
                }
                prepareInsertStatement(preparedStatement, notification);
                batchCnt++;
            }
        } catch (TransactionException ex) {
            if (ex.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {
                serverContext.generateSystemAlert("", Alerts.DB_NO_CONNECTION, MODULE, "Unable to insert Notification into Server Database. Reason: Connection not available");
            }
            if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
                LogManager.getLogger().error(MODULE, "DB Error while inserting notification to server database");
            LogManager.getLogger().trace(ex);
            if (transaction != null) {
                transaction.rollback();
            }
        } catch (SQLException sqlEx) {
            LogManager.getLogger().error(MODULE, "SQL Error while inserting data to DB from notification queue. Reason: " + sqlEx.getMessage());
            LogManager.getLogger().trace(MODULE, sqlEx);

            if (transaction != null) {
                transaction.rollback();
                if (transaction.isDBDownSQLException(sqlEx)) {
                    transaction.markDead();
                }
            }

        } catch (Exception e) {
            LogManager.getLogger().trace(MODULE, e);
            LogManager.getLogger().error(MODULE, "Error while inserting data to DB from notification queue. Reason: " + e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            DBUtility.closeQuietly(preparedStatement);
            closeQuitely(transaction);
        }
    }

    private boolean isBatchFull(int batchCnt) {
        return batchCnt >= queueSize;
    }

    void commitBatch(Transaction transaction, PreparedStatement preparedStatement, int batchCnt) throws SQLException, TransactionException {
        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();
        preparedStatement.executeBatch();
        transaction.commit();
        stopwatch.stop();
        getLogger().warn(MODULE, "DB Update time(ms): " + (stopwatch.elapsedTime(TimeUnit.MILLISECONDS)) + " Total records : " + batchCnt);
    }

    @Override
    public void insertIntoNotificationQueue(Notification notification) {
        if (batchOperationQueue.add(notification) == false) {
            LogManager.getLogger().warn(MODULE, "Notification insertion failed" +
                    (Strings.isNullOrEmpty(notification.getEmailTemplateData()) ? notification.getSMSTemplateData() : notification.getEmailTemplateData()));
        }
    }

    @Override
    public void insertIntoNotificationHistory(Notification notification) {
        if (batchHistoryOperationQueue.add(notification) == false) {
            LogManager.getLogger().warn(MODULE, "Notification history insertion failed" +
                    (Strings.isNullOrEmpty(notification.getEmailTemplateData()) ? notification.getSMSTemplateData() : notification.getEmailTemplateData()));
        }
    }

    private void prepareInsertStatement(PreparedStatement preparedStatement, Notification notification) throws SQLException {

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1000);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(notification.getPCRFResponse());
            preparedStatement.setString(1, getNextNotificationId());
            preparedStatement.setString(2, notification.getEmailSendStatus());
            preparedStatement.setString(3, notification.getSmsSendStatus());
            preparedStatement.setString(4, notification.getEmailSubject());
            preparedStatement.setString(5, notification.getEmailTemplateData());
            preparedStatement.setString(6, notification.getSMSTemplateData());
            preparedStatement.setBytes(7, byteArrayOutputStream.toByteArray());
            preparedStatement.setString(8, MODULE);
            preparedStatement.setTimestamp(9, notification.getValidityDate());
            preparedStatement.setInt(10, notification.getRecipient());
            preparedStatement.setString(11, notification.getPCRFResponse().getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.getVal()));
            preparedStatement.addBatch();
            preparedStatement.clearParameters();

        } catch (IOException e) {
            getLogger().error(MODULE, "Error while inserting data to DB from notification queue for user "
                    + notification.getPCRFResponse().getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.getVal()) + ". Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
    }

    private String getNextNotificationId() {
        return UUID.randomUUID().toString();
    }

    private void closeQuitely(Transaction transaction) {
        if (transaction != null) {
            try {
                transaction.end();
            } catch (TransactionException e) {
                getLogger().error(MODULE, e.getMessage());
                getLogger().trace(MODULE, e);
            }
        }
    }

}


