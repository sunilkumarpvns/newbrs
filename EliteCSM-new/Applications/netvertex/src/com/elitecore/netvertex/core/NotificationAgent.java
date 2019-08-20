package com.elitecore.netvertex.core;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.constants.NotificationRecipient;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.service.notification.Template;
import com.elitecore.netvertex.core.constant.NotificationConstants;
import com.elitecore.netvertex.core.notification.NotificationDBOperation;
import com.elitecore.netvertex.service.notification.EmailSender;
import com.elitecore.netvertex.service.notification.NotificationServiceCounters;
import com.elitecore.netvertex.service.notification.NotificationUtility;
import com.elitecore.netvertex.service.notification.SMSSender;
import com.elitecore.netvertex.service.notification.data.NotificationEntity;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class NotificationAgent {

    private static final String MODULE = "NOTIFICATION-AGENT";

    private NetVertexServerContext serverContext;
    private ExecutorService notificationThreadPoolExecutor;
    private ShutdownHook shutdownHook;
    private EmailSender emailSender;
    private SMSSender smsSender;
    private NotificationServiceCounters notificationServiceCounters;

    private ScheduledThreadPoolExecutor notificationExecutor;
    private ScheduledThreadPoolExecutor historyExecutor;



    private LinkedBlockingQueue<Runnable> notificationQueue;

    private NotificationDBOperation notificationAgentDBOperation;
    private long waitIntervalForDbOperation = 30;
    private long waitIntervalForNotification = 5;


    public NotificationAgent(NetVertexServerContext serverContext,
                             LinkedBlockingQueue<Runnable> notificationQueue,
                             ExecutorService notificationThreadPoolExecutor,
                             NotificationDBOperation notificationAgentDBOperation,
                             ScheduledThreadPoolExecutor notificationExecutor,
                             ScheduledThreadPoolExecutor historyExecutor,
                             NotificationServiceCounters notificationServiceCounters,
                             EmailSender emailSender,
                             SMSSender smsSender) {
        this.serverContext = serverContext;
        this.notificationQueue = notificationQueue;
        this.notificationThreadPoolExecutor = notificationThreadPoolExecutor;
        this.shutdownHook = new NotificationAgent.ShutdownHook(notificationThreadPoolExecutor);
        this.notificationAgentDBOperation = notificationAgentDBOperation;
        this.notificationExecutor = notificationExecutor;
        this.historyExecutor = historyExecutor;
        this.notificationServiceCounters = notificationServiceCounters;
        this.emailSender = emailSender;
        this.smsSender = smsSender;
    }



    public void init(){
        if (serverContext.getServerConfiguration().getNotificationServiceConfiguration().isSMSNotificationEnabled()|| serverContext.getServerConfiguration().getNotificationServiceConfiguration().isEmailNotificationEnabled()) {
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        }
    }


    public boolean sendNotification(Template emailTemplate, Template smsTemplate, PCRFResponse pcrfResponse, Timestamp validityDate, NotificationRecipient recipient) {

        PCRFResponse copiedPCRFResponse;
        try {
            copiedPCRFResponse = pcrfResponse.clone();
        } catch (CloneNotSupportedException e) {
            getLogger().error(MODULE, "Error while copying pcrf response. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
            return false;
        }

        Notification notification = new Notification(emailTemplate, smsTemplate, copiedPCRFResponse, validityDate, recipient, NotificationConstants.PENDING, NotificationConstants.PENDING);

        if(getLogger().isLogLevel(LogLevel.DEBUG)) {
            getLogger().debug(MODULE, "Creating notification for user " + copiedPCRFResponse.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.getVal()));
        }

        if(serverContext.getServerConfiguration().getNotificationServiceConfiguration().isSMSNotificationEnabled() == false
                && serverContext.getServerConfiguration().getNotificationServiceConfiguration().isEmailNotificationEnabled() == false) {
            notificationAgentDBOperation.insertIntoNotificationQueue(notification);
        }else{
            notificationThreadPoolExecutor.execute(new NotificationAgent.InMemoryOperationTask(notification));
        }

        return true;
    }

    private class ShutdownHook extends Thread {
        private ExecutorService executor;

        private ShutdownHook(ExecutorService executor) {
            this.executor = executor;
        }

        @Override
        public void run() {
            shutdownAll();
        }

        private void shutdownAll() {
            shutDownExecutor(executor, waitIntervalForNotification);
            stopSender();
            moveNotificationQueueInDb();
            shutDownExecutor(historyExecutor, waitIntervalForDbOperation);
            shutDownExecutor(notificationExecutor, waitIntervalForDbOperation);
        }

        private void shutDownExecutor(ExecutorService executor, long waitInterval) {

            if (executor == null) {
                return;
            }

            if(getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Stopping worker threads");
            }

            try {

                if(getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Shutdown requested for notification executor ");
                }

                executor.shutdown();

                if(executor.awaitTermination(waitInterval, TimeUnit.SECONDS) == false) {
                    executor.shutdownNow();
                }

                if(getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Notification executor terminated");
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void stopSender(){
            if(Objects.nonNull(emailSender)){
                emailSender.stop();
            }
            if(Objects.nonNull(smsSender)){
                smsSender.stop();
            }

        }

        private void moveNotificationQueueInDb(){
            for(Runnable notification: notificationQueue){
                InMemoryOperationTask inMemoryOperationTask = (InMemoryOperationTask) notification;
                notificationAgentDBOperation.insertIntoNotificationQueue(inMemoryOperationTask.notification);
            }
        }
    }

    public class InMemoryOperationTask implements Runnable {

        private Notification notification;

        public InMemoryOperationTask(Notification notification) {
            this.notification = notification;
        }

        @Override
        public void run() {

            NotificationEntity notificationEntity = NotificationUtility.createNotificationEntity(UUID.randomUUID().toString(), notification.getPCRFResponse(), NotificationRecipient.fromValue(notification.getRecipient()),
                    null, null, notification.getEmailTemplateData(), notification.getEmailSubject(), notification.getSMSTemplateData(), serverContext);

            if(Objects.isNull(notificationEntity)){
                return;
            }
            processTargetEntity(notificationEntity, notification);
        }

        private void processTargetEntity(NotificationEntity notificationEntity,
                                        Notification notification) {
            if (getLogger().isLogLevel(LogLevel.DEBUG)) {
                getLogger().debug(MODULE, "Processing notification target entity");
            }

            Boolean[] status = NotificationUtility.send(emailSender, smsSender, notificationEntity, notificationServiceCounters);

            if ((status[0] != null && status[0] == false) || (status[1] != null && status[1] == false)) {
                if (getLogger().isLogLevel(LogLevel.DEBUG)) {
                    getLogger().debug(MODULE, "In memory Notification sending failed so doing failover");
                }

                failOver(notification, status);
            } else {
                if (getLogger().isLogLevel(LogLevel.DEBUG)) {
                    getLogger().debug(MODULE, "Insert notification into history");
                }

                setNotificationStatus(notification, status);
                notificationAgentDBOperation.insertIntoNotificationHistory(notification);
            }
        }

        private void setNotificationStatus(Notification notification, Boolean[] status) {
            if(status[1] != null) {
                notification.setSmsSendStatus(deriveStatus(status[1]));
            }else{
                notification.setSmsSendStatus(null);
            }

            if(status[0] != null) {
                notification.setEmailSendStatus(deriveStatus(status[0]));
            }else{
                notification.setEmailSendStatus(null);
            }
        }

        private String deriveStatus(Boolean status) {
            return status ? NotificationConstants.SENT : NotificationConstants.PENDING;
        }

        private void failOver(Notification notification, Boolean[] status) {

            setNotificationStatus(notification, status);

            if (getLogger().isLogLevel(LogLevel.DEBUG)) {
                getLogger().debug(MODULE, "Insert notification into DB");
            }

            notificationAgentDBOperation.insertIntoNotificationQueue(notification);
        }
    }

    public void stop(){
        shutdownHook.shutdownAll();
    }


}
