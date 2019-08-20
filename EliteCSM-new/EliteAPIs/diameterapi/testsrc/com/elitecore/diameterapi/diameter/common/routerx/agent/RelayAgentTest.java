package com.elitecore.diameterapi.diameter.common.routerx.agent;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(Suite.class)
@SuiteClasses(value = {
		RelayAgentBasicTest.class,
		RelayAgentWithoutFailoverConfigurationTest.class,
		RelayAgentWithFailoverActionTest.class,
		RelayAgentWithDropActionTest.class,
		RelayAgentServerInitiatedTest.class,
		RelayAgentStatefullTest.class
})
// TODO session release scenario tests
public class RelayAgentTest {
	
}
