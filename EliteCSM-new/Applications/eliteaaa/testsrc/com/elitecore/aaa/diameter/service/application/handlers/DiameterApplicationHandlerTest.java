package com.elitecore.aaa.diameter.service.application.handlers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	DiaToDiaProxyCommunicationHandlerTest.class,
	DiaToDiaProxyHandlerTest.class,
	DiaToDiaBroadcastHandlerTest.class
})
public class DiameterApplicationHandlerTest {

}
