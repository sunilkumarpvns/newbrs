package com.elitecore.commons.logging;

import static org.junit.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class LogLevelTest {

	@Test
	public void thereMustOnlyBeSevenLogLevels() {
		assertEquals(7, LogLevel.values().length);
	}

	@Test
	@Parameters(method = "dataFor_testLogLevelValues_ShouldHaveDefinedOrder")
	public void testLogLevelValues_ShouldHaveDefinedOrder(LogLevel logLevel, int expectedLevel){
		assertEquals(expectedLevel, logLevel.level);
	}
	
	@Test
	@Parameters(method = "dataFor_testLogLevelValues_ShouldHaveDefinedOrder")
	public void testFromLogLevel_ShouldReturnProperInstanceOfLogLevel(LogLevel expectedLogLevel, int level){
		assertEquals(expectedLogLevel, LogLevel.fromLogLevel(level));
	}
	
	public Object[][] dataFor_testLogLevelValues_ShouldHaveDefinedOrder(){
		return new Object[][]{
				{LogLevel.NONE, 0},
				{LogLevel.ERROR, 1},
				{LogLevel.WARN, 2},
				{LogLevel.INFO, 3},
				{LogLevel.DEBUG, 4},
				{LogLevel.TRACE, 5},
				{LogLevel.ALL, 6}
		};
	}
}
