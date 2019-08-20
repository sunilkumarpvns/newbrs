package com.elitecore.netvertex.service.notification;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceDescription;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.servicex.base.BaseEliteService;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.alerts.Alerts;
import com.elitecore.netvertex.core.conf.impl.DBDataSourceImpl;
import com.elitecore.netvertex.service.notification.conf.impl.NotificationServiceConfigurationImpl;

public class NotificationService extends BaseEliteService {

    private static final String MODULE = "NOTIFICATION-SERVICE";
    private static final String SERVICE_ID = "NOTIFICATION";
    private static final String SERVICE_NAME = "Notification Service";

    private NetVertexServerContext serverContext = null;
    private NotificationServiceStatisticsProvider notificationServiceStatisticsProvider;
    private DBNotificationProcessor dbNotificationProcessor;

    public NotificationService(NetVertexServerContext serverContext, EmailSender emailSender, SMSSender smsSender,
                               NotificationServiceStatisticsProvider notificationServiceStatisticsProvider, NotificationServiceCounters notificationServiceCounters) {
        super(serverContext);
        this.serverContext = serverContext;
        this.notificationServiceStatisticsProvider = notificationServiceStatisticsProvider;

        dbNotificationProcessor = new DBNotificationProcessor(serverContext, emailSender, smsSender, notificationServiceCounters);
    }

    @Override
    protected ServiceContext getServiceContext() {
        return null;
    }

    @Override
    public void readConfiguration() throws LoadConfigurationException {
        //IGNORED
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    protected void initService() throws ServiceInitializationException {
        LogManager.getLogger().info(MODULE, "Initializing the Notification Service");

        NotificationServiceConfigurationImpl serviceConfiguration = serverContext.getServerConfiguration().getNotificationServiceConfiguration();

        try {
			/*
			 * email and sms both notifications are disabled then no need to
			 * start notification service
			 */
            if (serviceConfiguration.isEmailNotificationEnabled() == false
                    && serviceConfiguration.isSMSNotificationEnabled() == false) {
                throw new ServiceInitializationException("Email and SMS both notifications are disabled",
                        ServiceRemarks.INVALID_CONFIGURATION);
            }

			/*
			 * email notification enabled but email host not provided then no
			 * need to start notification service
			 */
            if (serviceConfiguration.isEmailNotificationEnabled()
                    && Strings.isNullOrBlank(serviceConfiguration.getEmailAgentConfiguration().getHost())) {
                throw new ServiceInitializationException("Email host is not configured",
                        ServiceRemarks.INVALID_CONFIGURATION);
            }

			/*
			 * IF SMS notification enabled THEN
			 *
			 *  IF HTTP protocol THEN
			 *  	service url is compulsory
			 * 	IF SMPP protocol THEN
			 *  	connection url is compulsory
			 */
            if (serviceConfiguration.isSMSNotificationEnabled()
                    && Strings.isNullOrBlank(serviceConfiguration.getSmsAgentConfiguration().getUrl())) {

                throw new ServiceInitializationException("SMS service url is not configured",
                        ServiceRemarks.INVALID_CONFIGURATION);
            }


            DBDataSourceImpl notificationDs = (DBDataSourceImpl) serverContext.getServerConfiguration().getNetvertexServerGroupConfiguration().getNotificationDS();

            if (notificationDs == null) {
                LogManager.getLogger().warn(MODULE, "Notification DB datasource is not configured");
                return;
            }

            DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance(notificationDs.getDataSourceName());

            if (dbConnectionManager.isInitilized() == false) {

                initializeDBConnectionManager(notificationDs, dbConnectionManager);
            }


        } catch (ServiceInitializationException e) {
            getServerContext().generateSystemAlert("", Alerts.NOTIFICATION_STARTUP_FAILED, MODULE, "Notification Service initialization failed. Reason: " + e.getMessage());
            throw e;
        }

        try {
            dbNotificationProcessor.init().start();

            LogManager.getLogger().info(MODULE, "Notification service initialization completed");
        } catch (NoClassDefFoundError ex) {
            getServerContext().generateSystemAlert("", Alerts.NOTIFICATION_STARTUP_FAILED, MODULE, "Notification Service initialization failed. Reason: " + ex.getMessage());

            throw new ServiceInitializationException(ex.getMessage(), ServiceRemarks.MISSING_JAR_FILE, ex);
        } catch (Exception e) {
            getServerContext().generateSystemAlert("", Alerts.NOTIFICATION_STARTUP_FAILED, MODULE, "Notification Service initialization failed. Reason: " + e.getMessage());

            throw new ServiceInitializationException(e.getMessage(), ServiceRemarks.UNKNOWN_PROBLEM, e);
        }
    }

    private void initializeDBConnectionManager(DBDataSourceImpl notificationDs, DBConnectionManager dbConnectionManager) throws ServiceInitializationException {
        try {
            dbConnectionManager.init(notificationDs, serverContext.getTaskScheduler());
        } catch (DatabaseInitializationException | DatabaseTypeNotSupportedException ex) {
            LogManager.ignoreTrace(ex);
            LogManager.getLogger().warn(MODULE, "Error while initializing notification DB datasource "+notificationDs.getDataSourceName()+". Reason : " + ex.getMessage());
            return;
        }
    }

    @Override
    protected boolean startService() {

        return true;
    }

    @Override
    public boolean stopService() {
        dbNotificationProcessor.stopService();

        return true;
    }

    @Override
    protected void shutdownService() {
        //IGNORED
    }

    @Override
    public String getServiceIdentifier() {
        return SERVICE_ID;
    }

    public NotificationServiceStatisticsProvider getNotificationServiceStatisticsProvider() {
        return notificationServiceStatisticsProvider;
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public ServiceDescription getDescription() {
        return new ServiceDescription(getServiceIdentifier(), getStatus(),
                "N.A.", getStartDate(), getRemarks());
    }


}