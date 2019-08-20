package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	NPlusMRedundancyGroupTest.class,
	ActivePassiveRedundancyGroupTest.class,
	CorrelatedRadiusCommunicatorTest.class,
	ActivePassiveRedundancyGroupForCorrelatedRADIUSTest.class,
	NPlusMRedundancyGroupForCorrelatedRADIUSTest.class})
public class RadiusESIGroupTest {

}
