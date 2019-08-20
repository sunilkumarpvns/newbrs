package com.elitecore.netvertex.service.pcrf.preprocessors;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses(
        {
                OperatorNetworkInfoOrchestratorTest.class,
                SubscriberNetworkInfoOrchestratorTest.class,
                CallTypeStatusOrchestratorTest.class,
        })
public class PCRFRequestHandlerPreProcessorTestSuite {
}
