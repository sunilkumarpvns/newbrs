package com.elitecore.aaa.radius.plugins.quotamgmt;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	VolumeBasedThresholdTest.class,
	TimeBasedThresholdTest.class,
	VolumeAndTimeBasedThresholdTest.class,
	PostThresholdActionsTest.class,
	AttributesToAddInPacketTest.class
})

public class QuotaManagementPluginTest {
	
}
