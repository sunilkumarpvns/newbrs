package com.elitecore.netvertex.core.notification;

import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.netvertex.cli.NotificationServiceAllStatisticsDetailProvider;
import com.elitecore.netvertex.cli.NotificationServiceEmailStatisticsDetailProvider;
import com.elitecore.netvertex.cli.NotificationServiceSMSStatisticsDetailProvider;
import com.elitecore.netvertex.cli.NotificationServiceStatisticsDetailProvider;
import com.elitecore.netvertex.cli.StatisticsDetailProvider;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.NotificationAgent;
import com.elitecore.netvertex.core.util.DummyAsyncTaskContext;
import com.elitecore.netvertex.core.util.TaskSchedulerImpl;
import com.elitecore.netvertex.core.util.DefaultDbConnectionManagerRepository;
import com.elitecore.netvertex.service.notification.EmailSender;
import com.elitecore.netvertex.service.notification.NotificationSenderFactory;
import com.elitecore.netvertex.service.notification.NotificationService;
import com.elitecore.netvertex.service.notification.NotificationServiceContext;
import com.elitecore.netvertex.service.notification.NotificationServiceCounters;
import com.elitecore.netvertex.service.notification.NotificationServiceStatisticsProvider;
import com.elitecore.netvertex.service.notification.SMSSender;
import com.elitecore.netvertex.service.notification.conf.impl.NotificationServiceConfigurationImpl;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class NotificationServiceFactory {


    private static final String MODULE = "NOTIFICATION-SER-FACTRY";
    private static final String NOTIFICATION_AGENT_MODULE = "NOTIFICATION-AGENT";
    private static final int MAX_QUEUE_SIZE = 1000;
    private static final AsyncTaskContext TASK_CONTEXT = new DummyAsyncTaskContext();
    private NotificationServiceContext serviceContext;
    private NetVertexServerContext serverContext;
    private NotificationSenderFactory senderFactory;
    private NotificationServiceCounters notificationServiceCounters;
    private NotificationServiceStatisticsProvider notificationServiceStatisticsProvider;
    private EmailSender emailSender;
    private SMSSender smsSender;

    public NotificationServiceFactory(NetVertexServerContext serverContext, NotificationSenderFactory senderFactory) {
        this.serverContext = serverContext;
        this.senderFactory = senderFactory;
        createServiceContext(serverContext);
    }

    public NotificationService createDbService() {
        createSenders();
        createNotificationServiceCounter();
        createNotificationServiceStatisticsProvider();
        return new NotificationService(serverContext, emailSender, smsSender, notificationServiceStatisticsProvider, notificationServiceCounters);
    }

    public NotificationAgent createAgent() {

        notificationServiceCounters = createNotificationServiceCounter();

        notificationServiceStatisticsProvider = createNotificationServiceStatisticsProvider();

        LinkedBlockingQueue<Runnable> notificationQueue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);

        ThreadPoolExecutor notificationThreadPoolExecutor = new ThreadPoolExecutor(1, 1, getThreadKeepAliveTime(), TimeUnit.MILLISECONDS, notificationQueue);
        notificationThreadPoolExecutor.setThreadFactory(new EliteThreadFactory("IN-MEM-NOTI-SCH", "IN-MEM-NOTI-SCH",Thread.NORM_PRIORITY));

        ScheduledThreadPoolExecutor notificationExecutor = new ScheduledThreadPoolExecutor(1, new EliteThreadFactory("NOTIFY-AGNT-DB-OPR", "NOTIFY-AGNT-DB-OPR", Thread.NORM_PRIORITY));
        ScheduledThreadPoolExecutor historyExecutor = new ScheduledThreadPoolExecutor(1, new EliteThreadFactory("NOTIFY-AGNT-HIST-DB-OPR", "NOTIFY-AGNT-HIST-DB-OPR", Thread.NORM_PRIORITY));


        TaskScheduler notificationTaskSchedular =  new TaskSchedulerImpl(notificationExecutor, NOTIFICATION_AGENT_MODULE,TASK_CONTEXT);
        TaskScheduler historyTaskSchedular = new TaskSchedulerImpl(historyExecutor, NOTIFICATION_AGENT_MODULE,TASK_CONTEXT);


        NotificationDBOperation notificationAgentDBOperation = null;
        try {
            notificationAgentDBOperation = NotificationAgentDBOperation.create(serverContext, notificationTaskSchedular, historyTaskSchedular, new DefaultDbConnectionManagerRepository());
        }catch (InitializationFailedException e){
            getLogger().warn(MODULE, "Error while creating notification agent DB operation. Reason: " + e.getMessage()+" So considering dummy notification db operation");
            getLogger().trace(MODULE, e);

        }
        if(Objects.isNull(notificationAgentDBOperation)){
            notificationAgentDBOperation = new DummyNotificationDBOperation();
        }

        notificationThreadPoolExecutor.prestartAllCoreThreads();

        createSenders();

        NotificationAgent notificationAgent = new NotificationAgent(serverContext,
                notificationQueue,
                notificationThreadPoolExecutor,
                notificationAgentDBOperation,
                notificationExecutor,
                historyExecutor,
                notificationServiceCounters,
                emailSender,
                smsSender);
        notificationAgent.init();
        return notificationAgent;
    }

    private void createSenders() {

        if(Objects.nonNull(emailSender) || Objects.nonNull(smsSender)) {
            return;
        }

        if(serverContext.getServerConfiguration().getNotificationServiceConfiguration().isSMSNotificationEnabled() == true
                || serverContext.getServerConfiguration().getNotificationServiceConfiguration().isEmailNotificationEnabled() == true){
            emailSender = senderFactory.createEmailNotificationFactory(serviceContext, notificationServiceCounters);
            smsSender = senderFactory.createSMSNotificationFactory(serviceContext, notificationServiceCounters);
        }
    }

    private NotificationServiceStatisticsProvider createNotificationServiceStatisticsProvider() {

        if(Objects.nonNull(notificationServiceStatisticsProvider)) {
            return notificationServiceStatisticsProvider;
        }

        notificationServiceStatisticsProvider = new NotificationServiceStatisticsProvider(notificationServiceCounters, serviceContext);

        try {

            NotificationServiceStatisticsDetailProvider notificationServiceStatisticsDetailProvider = new NotificationServiceStatisticsDetailProvider(notificationServiceStatisticsProvider);
            notificationServiceStatisticsDetailProvider.registerDetailProvider(new NotificationServiceEmailStatisticsDetailProvider(notificationServiceStatisticsProvider));
            notificationServiceStatisticsDetailProvider.registerDetailProvider(new NotificationServiceSMSStatisticsDetailProvider(notificationServiceStatisticsProvider));
            notificationServiceStatisticsDetailProvider.registerDetailProvider(new NotificationServiceAllStatisticsDetailProvider(notificationServiceStatisticsProvider));
            StatisticsDetailProvider.getInstance().registerDetailProvider(notificationServiceStatisticsDetailProvider);

        } catch (RegistrationFailedException e) {
            getLogger().error(MODULE, "Failed to register detail provider. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
        return notificationServiceStatisticsProvider;
    }

    private NotificationServiceCounters createNotificationServiceCounter() {
        if(Objects.nonNull(notificationServiceCounters)) {
            return notificationServiceCounters;
        }

        NotificationServiceCounters notificationServiceCounters = new NotificationServiceCounters(serviceContext);
        notificationServiceCounters.init();
        return notificationServiceCounters;
    }

    private void createServiceContext(NetVertexServerContext serverContext) {

        if(Objects.nonNull(serviceContext)) {
            return;
        }
        serviceContext = new NotificationServiceContext() {

            @Override
            public NetVertexServerContext getServerContext() {
                return serverContext;
            }

            @Override
            public NotificationServiceConfigurationImpl getNotificationServiceConfiguration() {
                return serverContext.getServerConfiguration().getNotificationServiceConfiguration();
            }
        };
    }

    private static long getThreadKeepAliveTime() {
        return (1000 * 60 * 60);
    }

}
