package com.elitecore.netvertex.core.conf;

import com.elitecore.corenetvertex.sm.notificationagents.EmailAgentData;
import com.elitecore.corenetvertex.sm.notificationagents.SMSAgentData;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.corenetvertex.sm.serverprofile.ServerProfileData;
import com.elitecore.netvertex.service.notification.conf.EmailAgentConfiguration;
import com.elitecore.netvertex.service.notification.conf.SMSAgentConfiguration;
import com.elitecore.netvertex.service.notification.conf.impl.NotificationServiceConfigurationImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(HierarchicalContextRunner.class)
public class NotificationServiceConfigurationFactoryTest {

    private NotificationServiceConfigurationFactory notificationServiceConfigurationFactory = new NotificationServiceConfigurationFactory();

    private ServerInstanceData serverInstanceData = new ServerInstanceData();
    private ServerProfileData serverProfileData = new ServerProfileData();
    private EmailAgentData emailAgentData = new EmailAgentData();
    private SMSAgentData smsAgentData = new SMSAgentData();

    @Before
    public void setUp() {
        serverProfileData.setNotificationServiceExecutionPeriod(nextInt(1, Integer.MAX_VALUE));
        serverProfileData.setMaxParallelExecution(nextInt(1, Integer.MAX_VALUE));
        serverProfileData.setBatchSize(nextInt(10, Integer.MAX_VALUE));
        emailAgentData.setEmailHost("127.0.0.1:1000");
        serverInstanceData.setEmailAgentData(emailAgentData);
        serverInstanceData.setSmsAgentData(smsAgentData);
    }

    @Test
    public void creteReturnNotificationServiceConfigurationWhichAllFields() {
        EmailAgentConfiguration emailAgentConfiguration = new EmailAgentConfiguration(null, "127.0.0.1", 1000, null, null);
        SMSAgentConfiguration smsAgentConfiguration = new SMSAgentConfiguration(null, "");
        NotificationServiceConfigurationImpl notificationServiceConfiguration = new NotificationServiceConfigurationImpl(serverProfileData.getNotificationServiceExecutionPeriod(),
                serverProfileData.getMaxParallelExecution(),
                serverProfileData.getBatchSize(), emailAgentConfiguration, smsAgentConfiguration);

        ReflectionAssert.assertReflectionEquals(notificationServiceConfiguration, notificationServiceConfigurationFactory.create(serverInstanceData, serverProfileData));
    }


    public class DefaultValuesOnInvalidConfiguration {

        private NotificationServiceConfigurationImpl notificationServiceConfiguration;

        @Before
        public void setUp() {
            serverProfileData.setNotificationServiceExecutionPeriod(0);
            serverProfileData.setMaxParallelExecution(0);
            serverProfileData.setBatchSize(nextInt(0, 10));
            serverInstanceData.setEmailAgentData(null);
            serverInstanceData.setSmsAgentData(null);
        }

        @Test
        public void skipEmailConfigurationWhenEmailAgentDataFoundNull() {
            notificationServiceConfiguration = notificationServiceConfigurationFactory.create(serverInstanceData, serverProfileData);
            assertNull(notificationServiceConfiguration.getEmailAgentConfiguration());
        }

        @Test
        public void skipSMSConfigurationWhenSMSAgentDataFoundNull() {
            notificationServiceConfiguration = notificationServiceConfigurationFactory.create(serverInstanceData, serverProfileData);
            assertNull(notificationServiceConfiguration.getEmailAgentConfiguration());
        }

        @Test
        public void skipEmailConfigurationWhenEmailAgentDataHasInvalidEmailHost() {
            EmailAgentData emailAgentData = new EmailAgentData();
            emailAgentData.setFromAddress(":x:");
            notificationServiceConfiguration = notificationServiceConfigurationFactory.create(serverInstanceData, serverProfileData);
            assertNull(notificationServiceConfiguration.getEmailAgentConfiguration());
        }

        @Test
        public void useBatchSizeAs10WhenConfiguredBatchSizeIsLessThen10() {
            notificationServiceConfiguration = notificationServiceConfigurationFactory.create(serverInstanceData, serverProfileData);
            assertEquals(10, notificationServiceConfiguration.getBatchSize());
        }

        @Test
        public void useServiceExecutionPeriodAs1WhenConfiguredServiceExecutionPeriodIsLessThen1() {
            notificationServiceConfiguration = notificationServiceConfigurationFactory.create(serverInstanceData, serverProfileData);
            assertEquals(1, notificationServiceConfiguration.getServiceExecutionPeriod());
        }

        @Test
        public void useMaxParallelExecutionAs1WhenConfiguredMaxParallelExecutionIsLessThen1() {
            notificationServiceConfiguration = notificationServiceConfigurationFactory.create(serverInstanceData, serverProfileData);
            assertEquals(1, notificationServiceConfiguration.getMaxParallelExecution());
        }
    }





}