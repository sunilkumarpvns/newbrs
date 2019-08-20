package com.elitecore.netvertex.service.notification;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.core.notification.email.EmailConfiguration;
import com.elitecore.core.notification.email.EmailNotifier;
import com.elitecore.core.notification.sms.SMSCommunicatorGroup;
import com.elitecore.core.notification.sms.SMSCommunicatorGroupImpl;
import com.elitecore.core.notification.sms.SMSNotifier;
import com.elitecore.core.notification.sms.http.SMSHTTPConfiguration;
import com.elitecore.core.notification.sms.http.SMSHTTPNotifier;
import com.elitecore.netvertex.service.notification.conf.EmailAgentConfiguration;
import com.elitecore.netvertex.service.notification.conf.SMSAgentConfiguration;
import com.elitecore.netvertex.service.notification.conf.impl.NotificationServiceConfigurationImpl;

import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class NotificationSenderFactory {

    private static final String MODULE = "NOTIFICATION-SENDER-FACTORY";

    public EmailSender createEmailNotificationFactory(NotificationServiceContext serviceContext, NotificationServiceCounters notificationServiceCounters){

        NotificationServiceConfigurationImpl configuration = serviceContext.getNotificationServiceConfiguration();
        boolean isEmailEnabled = serviceContext.getNotificationServiceConfiguration().isEmailNotificationEnabled();
        if(isEmailEnabled) {
            try {
                EmailAgentConfiguration emailAgentConfiguration = configuration.getEmailAgentConfiguration();

                boolean isAuthRequired = Objects.nonNull(emailAgentConfiguration.getPassword()) && Objects.nonNull(emailAgentConfiguration.getUserName());
                EmailConfiguration emailConfiguration = new EmailConfiguration(emailAgentConfiguration.getMailFrom(),
                        emailAgentConfiguration.getHost(),
                        emailAgentConfiguration.getPort(),
                        isAuthRequired, emailAgentConfiguration.getUserName(), emailAgentConfiguration.getPassword());
                EmailNotifier emailNotifier = new EmailNotifier(emailConfiguration, serviceContext.getServerContext().getTaskScheduler());
                emailNotifier.init();

                return new EmailNotificationSender(serviceContext.getServerContext(), emailNotifier, notificationServiceCounters);
            } catch (Exception e) {
                getLogger().error(MODULE, "Error while initializing EmailNotifier. Reason: " + e.getMessage());
                getLogger().trace(MODULE, e);
            }
        } else {
            if(getLogger().isLogLevel(LogLevel.DEBUG))
                getLogger().debug(MODULE,"Email Notification is disable");
        }

        return null;
    }

    public SMSSender createSMSNotificationFactory(NotificationServiceContext serviceContext, NotificationServiceCounters notificationServiceCounters){

        NotificationServiceConfigurationImpl configuration = serviceContext.getNotificationServiceConfiguration();

        boolean isSMSEnabled = serviceContext.getNotificationServiceConfiguration().isSMSNotificationEnabled();
        if(isSMSEnabled) {

            try {
                SMSAgentConfiguration smsAgentConfiguration =  configuration.getSmsAgentConfiguration();

                SMSHTTPConfiguration smsHTTPConf = new SMSHTTPConfiguration(smsAgentConfiguration.getUrl(), null,
                        null, smsAgentConfiguration.getUrl(), null, configuration.isSMSNotificationEnabled(),
                        null, null, null, null);
                SMSNotifier smsNotifier = new SMSHTTPNotifier(smsHTTPConf, serviceContext.getServerContext().getTaskScheduler());
                smsNotifier.init();

                SMSCommunicatorGroup smsCommunicatorGroup = new SMSCommunicatorGroupImpl();
                if(smsNotifier != null) {
                    smsCommunicatorGroup.addCommunicator(smsNotifier);
                }

                return new SMSNotificationSender(serviceContext.getServerContext(), smsNotifier, smsCommunicatorGroup, notificationServiceCounters);
            } catch (Exception e) {
                getLogger().error(MODULE, "Error while initializing SMSNotifier. Reason: " + e.getMessage());
                getLogger().trace(MODULE, e);
            }
        } else {
            if(getLogger().isLogLevel(LogLevel.DEBUG))
                getLogger().debug(MODULE,"SMS Notification is disable");
        }
        return null;
    }
}
