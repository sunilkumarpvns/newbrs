package com.elitecore.diameterapi.diameter.common.routerx.agent;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = {
		ProxyAgentBasicTest.class,
		ProxyAgentWithoutFailoverConfigurationTest.class,
		ProxyAgentWithFailoverActionTest.class,
		ProxyAgentWithDropActionTest.class,
		ProxyAgentServerInitiatedTest.class,
		ProxyAgentStatefullTest.class
})
public class NewProxyAgentTest {

}
