package com.elitecore.netvertex.core.conf;

import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.service.notification.EmailSender;
import com.elitecore.netvertex.service.notification.NotificationService;
import com.elitecore.netvertex.service.notification.NotificationServiceCounters;
import com.elitecore.netvertex.service.notification.NotificationServiceStatisticsProvider;
import com.elitecore.netvertex.service.notification.SMSSender;
import com.elitecore.netvertex.service.notification.conf.EmailAgentConfiguration;
import com.elitecore.netvertex.service.notification.conf.SMSAgentConfiguration;
import com.elitecore.netvertex.service.notification.conf.impl.NotificationServiceConfigurationImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class NotificationServiceTest {

    @Rule public ExpectedException exception = ExpectedException.none();

    private DummyNetvertexServerContextImpl serverContext;
    private NotificationService notificationService;
    private EmailAgentConfiguration emailAgentConfiguration;
    private SMSAgentConfiguration smsAgentConfiguration;
    private DummyNetvertexServerConfiguration serverConfiguration;
    @Mock private EmailSender emailSender;
    @Mock private SMSSender smsSender;
    @Mock private NotificationServiceCounters notificationServiceCounters;
    @Mock private NotificationServiceStatisticsProvider notificationServiceStatisticsProvider;

    @Before
    public void setUp() {
        serverContext = spy(new DummyNetvertexServerContextImpl());
        serverConfiguration = serverContext.getServerConfiguration();

        emailAgentConfiguration = new EmailAgentConfiguration(null, "127.0.0.1", 1000, null, null);
        smsAgentConfiguration = new SMSAgentConfiguration("https://1.1.1.1", "");
        serverConfiguration.setNotificationServiceConfiguration(createDummyNotificationServiceConfiguration(emailAgentConfiguration, smsAgentConfiguration));

        notificationService = new NotificationService(serverContext, emailSender, smsSender, notificationServiceStatisticsProvider, notificationServiceCounters);

    }

    @Test
    public void initServiceThrowsServiceInitializationExceptionWhenEmailAndSMSNotificationIsDisabled() throws ServiceInitializationException {
        serverConfiguration.setNotificationServiceConfiguration(createDummyNotificationServiceConfiguration(null, null));

        exception.expect(ServiceInitializationException.class);
        exception.expectMessage("Email and SMS both notifications are disabled");

        notificationService.init();
    }

    @Test
    public void initServiceThrowsServiceInitializationExceptionWhenEmailNotificationEnabledAndHostNotFound() throws ServiceInitializationException {
        emailAgentConfiguration.setHost(null);
        serverConfiguration.setNotificationServiceConfiguration(createDummyNotificationServiceConfiguration(emailAgentConfiguration, smsAgentConfiguration));

        exception.expect(ServiceInitializationException.class);
        exception.expectMessage("Email host is not configured");

        notificationService.init();
    }

    @Test
    public void initServiceThrowsServiceInitializationExceptionWhenSMSNotificationEnabledAndServiceUrlNotFound() throws ServiceInitializationException {
        smsAgentConfiguration.setUrl(null);
        serverConfiguration.setNotificationServiceConfiguration(createDummyNotificationServiceConfiguration(emailAgentConfiguration, smsAgentConfiguration));

        exception.expect(ServiceInitializationException.class);
        exception.expectMessage("SMS service url is not configured");

        notificationService.init();
    }

    private NotificationServiceConfigurationImpl createDummyNotificationServiceConfiguration(EmailAgentConfiguration emailAgentConfiguration, SMSAgentConfiguration smsAgentConfiguration){
        return new NotificationServiceConfigurationImpl(nextInt(1, Integer.MAX_VALUE), nextInt(1, Integer.MAX_VALUE), nextInt(10, Integer.MAX_VALUE), emailAgentConfiguration, smsAgentConfiguration);
    }
}
