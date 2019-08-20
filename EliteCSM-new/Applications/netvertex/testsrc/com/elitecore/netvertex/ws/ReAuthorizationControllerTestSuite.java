package com.elitecore.netvertex.ws;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ReAuthorizationBySubscriberIdentity.class,
        ReAuthorizationByCoreSessionId.class,
        ReAuthorizationByIPv4.class,
        ReAuthorizationByIPv6.class
})
public class ReAuthorizationControllerTestSuite {
}
