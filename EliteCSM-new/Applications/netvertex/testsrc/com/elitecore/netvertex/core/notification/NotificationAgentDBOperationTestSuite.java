package com.elitecore.netvertex.core.notification;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        InstanceCreationTest.class,
        NotificationQueueOperationTest.class,
        NotificationHistoryQueueOperationTest.class,
})
public class NotificationAgentDBOperationTestSuite {
}
