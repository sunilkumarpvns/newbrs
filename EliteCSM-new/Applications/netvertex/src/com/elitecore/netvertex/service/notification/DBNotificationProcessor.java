package com.elitecore.netvertex.service.notification;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.constants.BaseConfConstant;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.utilx.db.Transaction;
import com.elitecore.core.commons.utilx.db.TransactionErrorCode;
import com.elitecore.core.commons.utilx.db.TransactionException;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.servicex.EliteScheduledService;
import com.elitecore.corenetvertex.constants.NotificationRecipient;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.alerts.Alerts;
import com.elitecore.netvertex.core.constant.NotificationConstants;
import com.elitecore.netvertex.service.notification.data.NotificationEntity;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class DBNotificationProcessor extends EliteScheduledService<NotificationEntity> {

    private static final String MODULE = "DB-NOTIFICATION-PROCESSOR";

    private NetVertexServerContext serverContext;
    private EmailSender emailSender;
    private SMSSender smsSender;
    private NotificationServiceCounters notificationServiceCounters;

    private int dbQueryTimeout = BaseConfConstant.QUERY_TIMEOUT_SEC; // seconds

    private static final String SERVICE_ID = "NOTIFICATION";
    private static final String SERVICE_NAME = "Notification Service";
    private static final String QUERY_FOR_RESERVE_NOTIFICATION = "UPDATE TBLT_NOTIFICATION_QUEUE SET EMAIL_STATUS = ? , SMS_STATUS = ? , SERVER_INSTANCE_ID = ?  WHERE EMAIL_STATUS = ? AND SMS_STATUS = ?";
    private static final String QUERY_FOR_CREATE_NOTIFICATION_ENTITY = "SELECT * FROM TBLT_NOTIFICATION_QUEUE WHERE EMAIL_STATUS = ? AND SMS_STATUS = ? AND SERVER_INSTANCE_ID = ? ";
    private static final String QUERY_FOR_UPDATE_NOTIFICATION_ENTITY = "UPDATE TBLT_NOTIFICATION_QUEUE SET EMAIL_STATUS=?, SMS_STATUS=?, TIMESTAMP=CURRENT_TIMESTAMP WHERE ID=?";

    public DBNotificationProcessor(NetVertexServerContext serverContext,
                                   EmailSender emailSender,
                                   SMSSender smsSender, NotificationServiceCounters notificationServiceCounters) {
        super(serverContext, serverContext.getServerConfiguration().getNotificationServiceConfiguration().getEliteSchuduledServiceConf());
        this.serverContext = serverContext;
        this.emailSender = emailSender;
        this.smsSender = smsSender;
        this.notificationServiceCounters = notificationServiceCounters;
    }

    @Override
    protected void preScheduleExecution() {
        if(serverContext.isPrimaryServer()){
            if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                LogManager.getLogger().debug(MODULE,"Skipping pre schedule execution. Reason: DB notification should be processed on secondory server");
            return;
        }
    }

    @Override
    protected void doInitScheduleExecution() {
        if(serverContext.isPrimaryServer()){
            if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                LogManager.getLogger().debug(MODULE,"Skipping init schedule execution. Reason: DB notification should be processed on secondory server");
            return;
        }
        reserveNotification();
    }

    @Override
    protected List<NotificationEntity> getMoreTargetEntitiesForSession() {

        LogManager.getLogger().trace(MODULE, "Getting target entities for session");
        List<NotificationEntity> notificationEntityList = null;
        notificationEntityList = getNotificationsToBeProcessed();
        if (notificationEntityList != null) {
            LogManager.getLogger().trace(MODULE, "No target entities found for session " + notificationEntityList.size());
        }
        return notificationEntityList;
    }

    @Override
    protected void doProcessTargetEntity(NotificationEntity notificationEntity) {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Start processing target entity");
        }

        PreparedStatement psForUpdateStatus = null;
        Transaction transaction = null;

        try {

            TransactionFactory transactionFactory = DBConnectionManager.getInstance(serverContext.getServerConfiguration().getNetvertexServerGroupConfiguration().getNotificationDS().getDataSourceName()).getTransactionFactory();
            if (transactionFactory.isAlive() == false) {
                getLogger().warn(MODULE, "Unable to send Notification for Notification Id: "
                        + notificationEntity.getNotificationId() + ". Reason: Server DB is down");
                return;
            }

            Boolean[] status = NotificationUtility.send(emailSender, smsSender, notificationEntity, notificationServiceCounters);

            transaction = transactionFactory.createTransaction();
            transaction.begin();
            psForUpdateStatus = transaction.prepareStatement(QUERY_FOR_UPDATE_NOTIFICATION_ENTITY);
            psForUpdateStatus.setString(1, status[0] ? NotificationConstants.SENT : NotificationConstants.FAILED);
            psForUpdateStatus.setString(2, status[1] ? NotificationConstants.SENT : NotificationConstants.FAILED);
            psForUpdateStatus.setString(3, notificationEntity.getNotificationId());
            psForUpdateStatus.setQueryTimeout(dbQueryTimeout);

            long queryExecutionTime = System.currentTimeMillis();
            psForUpdateStatus.executeUpdate();
            queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                serverContext.generateSystemAlert("", Alerts.DB_HIGH_QUERY_RESPONSE_TIME, MODULE, "DB Query execution time is high while updating notification status. "
                        + "Last Query execution time: " + queryExecutionTime + " ms");

                if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
                    LogManager.getLogger().warn(MODULE, "DB Query execution time is high while fetching notifications. "
                            + "Last Query execution time: " + queryExecutionTime + " ms");
                }
            }
        } catch (TransactionException ex) {
            if (ex.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {
                serverContext.generateSystemAlert("", Alerts.DB_NO_CONNECTION, MODULE, "Unable to get Notifications from Server Database. Reason: Connection not available");
            }

            if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
                LogManager.getLogger().error(MODULE, "No connection available while fetching Notifications details from server database");
            }
            LogManager.getLogger().trace(MODULE, ex);
            if (transaction != null) {
                transaction.rollback();
            }
        } catch (SQLException sqlEx) {
            LogManager.getLogger().error(MODULE, "SQL Error while processing target entity. reason : " + sqlEx.getMessage());
            LogManager.getLogger().trace(MODULE, sqlEx);
            if (transaction != null) {
                transaction.rollback();
                if (transaction.isDBDownSQLException(sqlEx)) {
                    transaction.markDead();
                }
            }

        } catch (Exception e) {
            LogManager.getLogger().trace(MODULE, e);
            LogManager.getLogger().error(MODULE, "Error while processing target entity. reason : " + e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {

            DBUtility.closeQuietly(psForUpdateStatus);

            if (transaction != null) {
                try {
                    transaction.end();
                } catch (TransactionException e) {
                    LogManager.getLogger().trace(MODULE, e);
                }
            }
        }
    }

    @Override
    protected void postScheduleExecution() {
        //IGNORED
    }

    @Override
    public String getServiceIdentifier() {
        return SERVICE_ID;
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    public List<NotificationEntity> getNotificationsToBeProcessed() {

        List<NotificationEntity> notificationEntityList = new ArrayList<>();
        PreparedStatement psForNQ = null;
        ResultSet rsForNQ = null;
        PCRFResponse pcrfResponse = null;

        Transaction transaction = null;
        TransactionFactory transactionFactory = DBConnectionManager.getInstance(serverContext.getServerConfiguration().getNetvertexServerGroupConfiguration().getNotificationDS().getDataSourceName()).getTransactionFactory();

        if (transactionFactory.isAlive() == false) {
            LogManager.getLogger().warn(MODULE, "Unable to feaching Notification into Server Database. Reason: Server DB is down");
            return notificationEntityList;
        }

        transaction = transactionFactory.createTransaction();

        try {

            transaction.begin();

            psForNQ = transaction.prepareStatement(QUERY_FOR_CREATE_NOTIFICATION_ENTITY);
            psForNQ.setString(1, NotificationConstants.RESERVE);
            psForNQ.setString(2, NotificationConstants.RESERVE);
            psForNQ.setString(3, serverContext.getServerInstanceId());
            psForNQ.setQueryTimeout(dbQueryTimeout);

            long queryExecutionTime = System.currentTimeMillis();
            rsForNQ = psForNQ.executeQuery();
            queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                serverContext.generateSystemAlert("", Alerts.DB_HIGH_QUERY_RESPONSE_TIME, MODULE, "DB Query execution time is high while fetching notifications. "
                        + "Last Query execution time: " + queryExecutionTime + " ms");

                if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
                    LogManager.getLogger().warn(MODULE, "DB Query execution time is high while fetching notifications. "
                            + "Last Query execution time: " + queryExecutionTime + " ms");
                }
            }

            while (rsForNQ.next()) {

                notificationEntityList.add(NotificationUtility.createNotificationEntity(rsForNQ.getString("ID"), getPcrfResponse(rsForNQ, pcrfResponse),
                        NotificationRecipient.fromValue(rsForNQ.getInt("NOTIFICATION_RECIPIENT")),rsForNQ.getString("TO_EMAIL_ADDRESS"),
                        rsForNQ.getString("TO_SMS_ADDRESS"), rsForNQ.getString("EMAIL_DATA"), rsForNQ.getString("EMAIL_SUBJECT"),
                        rsForNQ.getString("SMS_DATA"), serverContext));
            }
        } catch (TransactionException e) {
            if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {
                serverContext.generateSystemAlert("", Alerts.DB_NO_CONNECTION, MODULE, "Unable to feaching Notification into Server Database. Reason: Connection not available");
            }
            getLogger().error(MODULE, "No connection available while feaching Notifications from server database");
            getLogger().trace(MODULE, e);


        } catch (SQLException e) {
            if (transaction != null) {
                if (transaction.isDBDownSQLException(e)) {
                    transaction.markDead();
                }
            }
            getLogger().error(MODULE, "SQL Error while fetching target entity. Reason : " + e.getMessage());
            getLogger().trace(MODULE, e);
        } catch (Exception e) {
            getLogger().trace(MODULE, e);
            getLogger().error(MODULE, "Error while fetching target entity. Reason : " + e.getMessage());
        } finally {
            DBUtility.closeQuietly(rsForNQ);
            DBUtility.closeQuietly(psForNQ);

            if (transaction != null) {
                try {
                    transaction.end();
                } catch (TransactionException e) {
                    LogManager.getLogger().trace(MODULE, e);
                }
            }
        }
        return notificationEntityList;
    }

    private PCRFResponse getPcrfResponse(ResultSet rsForNQ, PCRFResponse pcrfResponse) throws ClassNotFoundException, SQLException {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(rsForNQ.getBytes("PCRF_RESPONSE")))) {
            pcrfResponse = (PCRFResponse) objectInputStream.readObject();
        }catch (IOException e){
            getLogger().error(MODULE, "Could not initialize object input stream");
            getLogger().trace(MODULE, e);
        }
        return pcrfResponse;
    }

    public void reserveNotification(){
        PreparedStatement psForReservation = null;
        ResultSet rsForNQ = null;
        Transaction transaction = null;

        try{
            TransactionFactory transactionFactory = DBConnectionManager.getInstance(serverContext.getServerConfiguration().getNetvertexServerGroupConfiguration().getNotificationDS().getDataSourceName()).getTransactionFactory();

            if(transactionFactory.isAlive() == false){
                LogManager.getLogger().warn(MODULE, "Unable to reserve Notifications in Server Database. Reason: Server DB is down");
                return;
            }

            transaction = transactionFactory.createTransaction();

            transaction.begin();

            psForReservation = transaction.prepareStatement(QUERY_FOR_RESERVE_NOTIFICATION);

            psForReservation.setString(1,NotificationConstants.RESERVE);
            psForReservation.setString(2,NotificationConstants.RESERVE);
            psForReservation.setString(3,serverContext.getServerInstanceId());
            psForReservation.setString(4,NotificationConstants.PENDING);
            psForReservation.setString(5,NotificationConstants.PENDING);
            psForReservation.setQueryTimeout(dbQueryTimeout);

            long queryExecutionTime = System.currentTimeMillis();
            psForReservation.executeUpdate();
            queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
            if(queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS){

                serverContext.generateSystemAlert("",Alerts.DB_HIGH_QUERY_RESPONSE_TIME, MODULE, "DB Query execution time is high while reserving notifications records. "
                        + "Last Query execution time: " + queryExecutionTime + " ms");

                if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
                    LogManager.getLogger().warn(MODULE, "DB Query execution time is high while reserving notifications. "
                            + "Last Query execution time: " + queryExecutionTime + " ms");
                }
            }
        }catch(TransactionException ex){
            if(ex.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND){
                serverContext.generateSystemAlert("", Alerts.DB_NO_CONNECTION, MODULE, "Unable to reserve Notifications in Server Database. Reason: Connection not available");
            }
            if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
                LogManager.getLogger().error(MODULE, "No connection available while reserving Notifications from server database");

            LogManager.getLogger().trace(ex);
            if(transaction != null){
                transaction.rollback();
            }

        }  catch (SQLException sqlEx) {
            getLogger().error(MODULE,"SQL Error while fetching data from notification queue. Reason : " + sqlEx.getMessage());
            getLogger().trace(MODULE, sqlEx);
            if(transaction != null){
                transaction.rollback();
            }
            if(transaction != null){
                if(transaction.isDBDownSQLException(sqlEx)){
                    transaction.markDead();
                }
            }
        } catch (Exception e) {
            getLogger().trace(MODULE, e);
            getLogger().error(MODULE, "Error while fetching  data from notification queue. Reason : " + e.getMessage());
            if(transaction != null){
                transaction.rollback();
            }
        } finally{

            DBUtility.closeQuietly(rsForNQ);
            DBUtility.closeQuietly(psForReservation);

            if(transaction != null){
                try {
                    transaction.end();
                } catch (TransactionException e) {
                    LogManager.getLogger().trace(MODULE, e);
                }
            }
        }
    }
}
