package com.elitecore.aaa.radius.drivers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.elitecore.aaa.radius.drivers.conf.impl.RadDBAuthDriverBasicTest;
import com.elitecore.aaa.radius.drivers.conf.impl.RadDBAuthDriverGetAccountDataTest;

/**
 * @author narendra.pathai
 */
@RunWith(Suite.class)
@SuiteClasses({
	RadDBAuthDriverBasicTest.class,
	RadDBAuthDriverGetAccountDataTest.class,
	RadDBAuthDriverSaveAccountDataTest.class
})
public class RadDBAuthDriverTest {

}
