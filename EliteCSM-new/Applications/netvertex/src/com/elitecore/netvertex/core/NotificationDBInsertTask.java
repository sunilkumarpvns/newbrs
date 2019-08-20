package com.elitecore.netvertex.core;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.utilx.db.Transaction;
import com.elitecore.core.commons.utilx.db.TransactionErrorCode;
import com.elitecore.core.commons.utilx.db.TransactionException;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.util.queue.ConcurrentLinkedQueue;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertex.core.alerts.Alerts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class NotificationDBInsertTask extends BaseIntervalBasedTask {
    private static final String MODULE = "NOTIFICATION-DB-INSERT-TASK";

    private NetVertexServerContext serverContext;
    private ConcurrentLinkedQueue<Notification> notificationQueue;

    private int dbQueryTimeout = CommonConstants.DEFAULT_BATCH_QUERY_TIMEOUT; // Seconds
    private static final int INTERVAL = 1;	//	Interval in Second
    private static final int BATCH_SIZE = 100;
    private static final String INSERT_QUERY = "INSERT INTO TBLT_NOTIFICATION_QUEUE " +
            "(ID, EMAIL_STATUS , SMS_STATUS , EMAIL_SUBJECT, EMAIL_DATA, SMS_DATA, PCRF_RESPONSE, SOURCE_ID, VALIDITY_DATE, NOTIFICATION_RECIPIENT, SUBSCRIBER_IDENTITY, TIMESTAMP) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

    public NotificationDBInsertTask(NetVertexServerContext serverContext, ConcurrentLinkedQueue<Notification> notificationQueue) {

        this.serverContext = serverContext;
        this.notificationQueue = notificationQueue;
    }

    @Override
    public long getInterval() {
        return INTERVAL;
    }

    @Override
    public boolean isFixedDelay() {
        return true;
    }

    @Override
    public void execute(AsyncTaskContext context) {
        if(notificationQueue.isEmpty()) {
            return;
        }

        Transaction transaction = null;

        int batchCnt;

        PreparedStatement preparedStatement = null;

        try {
            TransactionFactory transactionFactory = DBConnectionManager.getInstance(serverContext.getServerConfiguration().getNetvertexServerGroupConfiguration().getNotificationDS().getDataSourceName()).getTransactionFactory();

            if(transactionFactory.isAlive() == false){
                LogManager.getLogger().warn(MODULE, "Skiping processing of notification agent task. Reason: Server DB is dead");
                return;
            }

            transaction =  transactionFactory.createTransaction();

            transaction.begin();

            preparedStatement = transaction.prepareStatement(INSERT_QUERY);

            while(!notificationQueue.isEmpty()) {
                batchCnt = 0;
                Notification notification = null;
                while((notification = notificationQueue.poll())!= null && batchCnt < BATCH_SIZE) {
                    if (addToBatch(preparedStatement, notification)) {
                        batchCnt++;
                    }
                }

                preparedStatement.setQueryTimeout(dbQueryTimeout);
                preparedStatement.executeBatch();
            }
        } catch(TransactionException ex){
            if(ex.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND){
                serverContext.generateSystemAlert("", Alerts.DB_NO_CONNECTION, MODULE, "Unable to insert Notification into Server Database. Reason: Connection not available");
            }
            if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
                LogManager.getLogger().error(MODULE, "DB Error while inserting notification to server database");
            LogManager.getLogger().trace(ex);
            if(transaction != null){
                transaction.rollback();
            }
        }catch (SQLException sqlEx) {
            LogManager.getLogger().error(MODULE,"SQL Error while inserting data to DB from notification queue. Reason: " + sqlEx.getMessage());
            LogManager.getLogger().trace(MODULE, sqlEx);

            if(transaction != null){
                transaction.rollback();
                if(transaction.isDBDownSQLException(sqlEx)){
                    transaction.markDead();
                }
            }

        } catch (Exception e) {
            LogManager.getLogger().trace(MODULE, e);
            LogManager.getLogger().error(MODULE, "Error while inserting data to DB from notification queue. Reason: " + e.getMessage());
            if(transaction != null){
                transaction.rollback();
            }
        } finally {
            DBUtility.closeQuietly(preparedStatement);

            closeQuitely(transaction);
        }

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

    private boolean addToBatch(PreparedStatement preparedStatement, Notification notification) throws SQLException {

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

            return true;
        } catch (IOException e) {
            getLogger().error(MODULE, "Error while inserting data to DB from notification queue for user "
                    + notification.getPCRFResponse().getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.getVal()) + ". Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
            return false;
        }
    }

    private String getNextNotificationId() {
        return UUID.randomUUID().toString();
    }
}
