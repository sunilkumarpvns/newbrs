package com.elitecore.netvertex.core.conf;

import com.elitecore.core.notification.sms.SMSCommunicatorGroup;
import com.elitecore.core.notification.sms.SMSCommunicatorGroupImpl;
import com.elitecore.core.notification.sms.SMSNotifier;
import com.elitecore.core.notification.sms.http.SMSHTTPConfiguration;
import com.elitecore.core.notification.sms.http.SMSHTTPNotifier;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.corenetvertex.constants.NotificationRecipient;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.service.notification.Template;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.Notification;
import com.elitecore.netvertex.core.alerts.Alerts;
import com.elitecore.netvertex.core.constant.NotificationConstants;
import com.elitecore.netvertex.service.notification.NotificationServiceContext;
import com.elitecore.netvertex.service.notification.NotificationServiceCounters;
import com.elitecore.netvertex.service.notification.SMSNotificationSender;
import com.elitecore.netvertex.service.notification.conf.EmailAgentConfiguration;
import com.elitecore.netvertex.service.notification.conf.SMSAgentConfiguration;
import com.elitecore.netvertex.service.notification.conf.impl.NotificationServiceConfigurationImpl;
import com.elitecore.netvertex.service.notification.data.NotificationEntity;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;
import java.util.UUID;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class SMSNotificationSenderTest {

    private DummyNetvertexServerContextImpl serverContext;
    private NotificationServiceCounters notificationServiceCounters;
    private SMSNotifier smsNotifier;
    private SMSNotificationSender smsNotificationSender;
    private PCRFResponse pcrfResponse;
    private SMSCommunicatorGroup smsCommunicatorGroup;

    @Before
    public void setUp() {
        serverContext = spy(new DummyNetvertexServerContextImpl());

        SMSAgentConfiguration smsAgentConfiguration = new SMSAgentConfiguration("https://1.1.1.1", "");
        serverContext.getServerConfiguration().setNotificationServiceConfiguration(createDummyNotificationServiceConfiguration(null, smsAgentConfiguration));

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

        SMSHTTPConfiguration smsHTTPConf = new SMSHTTPConfiguration(smsAgentConfiguration.getUrl(), null,
                null, smsAgentConfiguration.getUrl(), null, serviceContext.getNotificationServiceConfiguration().isSMSNotificationEnabled(),
                null, null, null, null);
        smsNotifier = new SMSHTTPNotifier(smsHTTPConf, serviceContext.getServerContext().getTaskScheduler());

        pcrfResponse = new PCRFResponseImpl();

        smsCommunicatorGroup = spy(new SMSCommunicatorGroupImpl());
        smsNotificationSender = new SMSNotificationSender(serverContext, smsNotifier, smsCommunicatorGroup,  notificationServiceCounters);
    }

    @Test
    public void increaseFailureCountWhenSMSAddressNotFoundWhileSendingSMS() {
        NotificationEntity notificationEntity = createNotificationEntity(createNotification());
        notificationEntity.setToSMSAddress(null);

        smsNotificationSender.sendSMS(notificationEntity);

        verify(notificationServiceCounters, atLeastOnce()).incTotalSMSFailuresCntr();
    }

    @Test
    public void onSuccessfulSMSSentIncreaseSentCount() {
        NotificationEntity notificationEntity = createNotificationEntity(createNotification());

        when(smsCommunicatorGroup.send(notificationEntity.getSMSData(), notificationEntity.getToSMSAddress())).thenReturn(true);
        smsNotificationSender.sendSMS(notificationEntity);

        verify(smsCommunicatorGroup, atLeastOnce()).send(notificationEntity.getSMSData(), notificationEntity.getToSMSAddress());
        verify(notificationServiceCounters).incTotalSMSProcessed();
    }

    @Test
    public void throwRuntimeExceptionOnSendGeneratesSMSAlert() {
        NotificationEntity notificationEntity = createNotificationEntity(createNotification());

        Exception exception = new RuntimeException("From"+ SMSNotificationSenderTest.class);
        when(smsCommunicatorGroup.send(notificationEntity.getSMSData(), notificationEntity.getToSMSAddress())).thenThrow(exception);
        smsNotificationSender.sendSMS(notificationEntity);

        verify(serverContext, atLeastOnce()).generateSystemAlert(AlertSeverity.WARN, Alerts.SMS_SENDING_FAILED, "SMS-SENDER", "SMS notification sending failed Reason: " + exception.getMessage());
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

    private NotificationServiceConfigurationImpl createDummyNotificationServiceConfiguration(EmailAgentConfiguration emailAgentConfiguration, SMSAgentConfiguration smsAgentConfiguration){
        return new NotificationServiceConfigurationImpl(nextInt(1, Integer.MAX_VALUE), nextInt(1, Integer.MAX_VALUE), nextInt(10, Integer.MAX_VALUE), emailAgentConfiguration, smsAgentConfiguration);
    }
}
