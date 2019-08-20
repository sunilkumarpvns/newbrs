package com.elitecore.netvertex.core.conf;

import com.elitecore.corenetvertex.constants.NotificationRecipient;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.service.notification.Template;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.Notification;
import com.elitecore.netvertex.core.constant.NotificationConstants;
import com.elitecore.netvertex.service.notification.EmailSender;
import com.elitecore.netvertex.service.notification.NotificationServiceContext;
import com.elitecore.netvertex.service.notification.NotificationServiceCounters;
import com.elitecore.netvertex.service.notification.NotificationUtility;
import com.elitecore.netvertex.service.notification.SMSSender;
import com.elitecore.netvertex.service.notification.conf.impl.NotificationServiceConfigurationImpl;
import com.elitecore.netvertex.service.notification.data.NotificationEntity;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class NotificationUtilityTest {

    private DummyNetvertexServerContextImpl serverContext;
    @Mock private EmailSender emailSender;
    @Mock private SMSSender smsSender;
    private PCRFResponse pcrfResponse;
    private NotificationServiceCounters notificationServiceCounters;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        serverContext = spy(new DummyNetvertexServerContextImpl());
        serverContext.setServerInstanceId(UUID.randomUUID().toString());
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
        notificationServiceCounters.init();

        pcrfResponse = new PCRFResponseImpl();
    }

    @Test
    public void createNotificationEntityForParentNotificationRecepientAndValidEmailAndSMSAddress() {
        Notification notification = createNotification();
        notification.setRecipient(NotificationRecipient.PARENT);

        SPRInfo sprInfo = new SPRInfoImpl.SPRInfoBuilder()
                .withEmail(pcrfResponse.getAttribute(PCRFKeyConstants.SUB_EMAIL.val))
                .withPhone(pcrfResponse.getAttribute(PCRFKeyConstants.SUB_PHONE.val))
                .build();

        when(serverContext.getSPRInfo(pcrfResponse.getAttribute(PCRFKeyConstants.SUB_PARENTID.getVal()))).thenReturn(sprInfo);

        NotificationEntity notificationEntity = NotificationUtility.createNotificationEntity(UUID.randomUUID().toString(), notification.getPCRFResponse(), NotificationRecipient.fromValue(notification.getRecipient()),
                null, null, notification.getEmailTemplateData(), notification.getEmailSubject(), notification.getSMSTemplateData(), serverContext);

        ReflectionAssert.assertReflectionEquals(createNotificationEntity(notificationEntity, notification), notificationEntity);
    }

    @Test
    public void createNotificationEntityForParentNotificationRecepientAndNullEmailAndSMSAddress() {
        Notification notification = createNotification();
        notification.setRecipient(NotificationRecipient.PARENT);

        SPRInfo sprInfo = new SPRInfoImpl.SPRInfoBuilder()
                .withEmail(null)
                .withPhone(null)
                .build();

        when(serverContext.getSPRInfo(pcrfResponse.getAttribute(PCRFKeyConstants.SUB_PARENTID.getVal()))).thenReturn(sprInfo);

        NotificationEntity notificationEntity = NotificationUtility.createNotificationEntity(UUID.randomUUID().toString(), notification.getPCRFResponse(), NotificationRecipient.fromValue(notification.getRecipient()),
                null, null, notification.getEmailTemplateData(), notification.getEmailSubject(), notification.getSMSTemplateData(), serverContext);

        Assert.assertFalse(Objects.nonNull(notificationEntity));
    }

    @Test
    public void incNotificationServiceCounterOnNotificationExecution() {
        NotificationEntity notificationEntity = createNotificationEntity(null, createNotification());

        NotificationUtility.send(emailSender, smsSender, notificationEntity, notificationServiceCounters);

        verify(notificationServiceCounters, atLeastOnce()).incTotalNotificationProcessed();
        verify(emailSender, atLeastOnce()).sendEmail(notificationEntity);
        verify(smsSender, atLeastOnce()).sendSMS(notificationEntity);
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

    public NotificationEntity createNotificationEntity(NotificationEntity notificationEntity, Notification notification)  {
        return new NotificationEntity(
                Objects.nonNull(notificationEntity) ? notificationEntity.getNotificationId() : UUID.randomUUID().toString(),
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
