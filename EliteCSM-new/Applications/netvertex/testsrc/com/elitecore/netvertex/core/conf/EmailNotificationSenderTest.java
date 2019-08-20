package com.elitecore.netvertex.core.conf;

import com.elitecore.core.notification.email.EmailConfiguration;
import com.elitecore.core.notification.email.EmailNotifier;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.corenetvertex.constants.NotificationRecipient;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.service.notification.Template;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.Notification;
import com.elitecore.netvertex.core.alerts.Alerts;
import com.elitecore.netvertex.core.constant.NotificationConstants;
import com.elitecore.netvertex.service.notification.EmailNotificationSender;
import com.elitecore.netvertex.service.notification.NotificationServiceContext;
import com.elitecore.netvertex.service.notification.NotificationServiceCounters;
import com.elitecore.netvertex.service.notification.conf.EmailAgentConfiguration;
import com.elitecore.netvertex.service.notification.conf.impl.NotificationServiceConfigurationImpl;
import com.elitecore.netvertex.service.notification.data.NotificationEntity;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class EmailNotificationSenderTest {

    private NetVertexServerContext serverContext;
    private NotificationServiceCounters notificationServiceCounters;
    private EmailNotifier emailNotifier;
    private EmailNotificationSender emailNotificationSender;
    private PCRFResponse pcrfResponse;

    @Before
    public void setUp() {
        serverContext = spy(new DummyNetvertexServerContextImpl());
        NotificationServiceContext serviceContext = new NotificationServiceContext() {

            @Override
            public NetVertexServerContext getServerContext() {
                return serverContext;
            }

            @Override
            public NotificationServiceConfigurationImpl getNotificationServiceConfiguration() {
                return serverContext.getServerConfiguration().getNotificationServiceConfiguration();
            }
        };
        notificationServiceCounters = spy(new NotificationServiceCounters(serviceContext));

        EmailAgentConfiguration emailAgentConfiguration = new EmailAgentConfiguration(null, "127.0.0.1", 1000, null, null);
        boolean isAuthRequired = Objects.nonNull(emailAgentConfiguration.getPassword()) && Objects.nonNull(emailAgentConfiguration.getUserName());
        EmailConfiguration emailConfiguration = new EmailConfiguration(emailAgentConfiguration.getMailFrom(),
                emailAgentConfiguration.getHost(),
                emailAgentConfiguration.getPort(),
                isAuthRequired, emailAgentConfiguration.getUserName(), emailAgentConfiguration.getPassword());
        emailNotifier = spy(new EmailNotifier(emailConfiguration, serviceContext.getServerContext().getTaskScheduler()));

        pcrfResponse = new PCRFResponseImpl();
        emailNotificationSender = new EmailNotificationSender(serverContext,emailNotifier, notificationServiceCounters);
    }

    @Test
    public void increaseFailureCountWhenEmailAddressNotFoundWhileSendingEmail() {
        NotificationEntity notificationEntity = createNotificationEntity(createNotification());
        notificationEntity.setToEmailAddress(null);

        emailNotificationSender.sendEmail(notificationEntity);

        verify(notificationServiceCounters, atLeastOnce()).incTotalEmailFailuresCntr();
    }

    @Test
    public void onSuccessfulEmailSentIncreaseSentCount() {
        NotificationEntity notificationEntity = createNotificationEntity(createNotification());

        when(emailNotifier.send(notificationEntity.getEmailSubject(), notificationEntity.getEmailData(), notificationEntity.getToEmailAddress())).thenReturn(true);
        emailNotificationSender.sendEmail(notificationEntity);

        verify(emailNotifier, atLeastOnce()).send(notificationEntity.getEmailSubject(), notificationEntity.getEmailData(), notificationEntity.getToEmailAddress());
        verify(notificationServiceCounters).incTotalEmailSentCntr();
    }

    @Test
    public void throwRuntimeExceptionOnSendGeneratesEmailAlert() {
        NotificationEntity notificationEntity = createNotificationEntity(createNotification());

        Exception exception = new RuntimeException("From"+ SMSNotificationSenderTest.class);
        when(emailNotifier.send(notificationEntity.getEmailSubject(), notificationEntity.getEmailData(), notificationEntity.getToEmailAddress())).thenThrow(exception);
        emailNotificationSender.sendEmail(notificationEntity);

        verify(serverContext, atLeastOnce()).generateSystemAlert(AlertSeverity.WARN, Alerts.EMAIL_SENDING_FAILED, "EMAIL-SENDER", "Email notification sending failed. Reason: " + exception.getMessage());
    }

    public Notification createNotification() {
        Template emailTemplate = new Template(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        Template smsTemplate = new Template(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        pcrfResponse.setAttribute(PCRFKeyConstants.SUB_EMAIL.val, "a@a.com");
        pcrfResponse.setAttribute(PCRFKeyConstants.SUB_PHONE.val, "1234567899");
        return new Notification(emailTemplate, smsTemplate, pcrfResponse, new Timestamp(System.currentTimeMillis()), NotificationRecipient.SELF, NotificationConstants.PENDING.toString(), NotificationConstants.PENDING.toString());
    }

    public NotificationEntity createNotificationEntity(Notification notification)  {
        String notificationId = UUID.randomUUID().toString();
        return new NotificationEntity(
                notificationId,
                notification.getEmailSubject(),
                notification.getEmailTemplateData(),
                pcrfResponse.getAttribute(PCRFKeyConstants.SUB_EMAIL.val),
                notification.getSMSTemplateData(),
                pcrfResponse.getAttribute(PCRFKeyConstants.SUB_PHONE.val),
                pcrfResponse,
                false,
                false);
    }
}
