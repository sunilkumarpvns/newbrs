package com.elitecore.commons.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.PrintStream;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class ConsoleLoggerTest {
	
	private ConsoleLogger logger;
	private PrintStream spyStream;
	private PrintStream standardOut;

	@Before
	public void setUp(){
		logger = new ConsoleLogger();
		standardOut = System.out;
		spyStream = spy(standardOut);
		System.setOut(spyStream);
	}
	
	@After
	public void cleanUp(){
		System.setOut(standardOut);
	}
	
	@Test
	public void testConstructor_ShouldHaveADefaultNoArgConstructor() throws SecurityException, NoSuchMethodException{
		ConsoleLogger.class.getDeclaredConstructor(new Class<?>[]{});
	}
	
	@Test
	@Parameters(method = "dataFor_testIsLogLevel_ShouldReturnTrueForAllLogLevels")
	public void testIsLogLevel_ShouldReturnTrueForAllLogLevels(LogLevel logLevel){
		assertTrue(logger.isLogLevel(logLevel));
	}
	
	@Test
	public void testInfo_ShouldLogOnStandardConsole(){
		logger.info("any", "any");
		verify(spyStream).println(anyString());
	}
	
	@Test
	public void testDebug_ShouldLogOnStandardConsole(){
		logger.debug("any", "any");
		verify(spyStream).println(anyString());
	}
	
	@Test
	public void testError_ShouldLogOnStandardConsole(){
		logger.error("any", "any");
		verify(spyStream).println(anyString());
	}
	
	@Test
	public void testTrace_ShouldLogOnStandardConsole(){
		logger.trace("any", "any");
		verify(spyStream).println(anyString());
	}
	
	@Test
	public void testTraceWithException_ShouldLogOnStandardConsole(){
		logger.trace(new Exception());
		verify(spyStream).println(anyString());
	}
	
	@Test
	public void testTraceWithModuleAndException_ShouldLogOnStandardConsole(){
		logger.trace("", new Exception());
		verify(spyStream).println(anyString());
	}
	
	@Test
	public void testWarn_ShouldLogOnStandardConsole(){
		logger.warn("any", "any");
		verify(spyStream).println(anyString());
	}
	
	@Test
	public void testGetCurrentLogLevel_ShouldReturnAllLogLevel(){
		assertEquals(LogLevel.ALL.level, logger.getCurrentLogLevel());
	}
	
	@Test
	public void testAddThreadName_ShouldNotThrowAnyException_WhenCalled(){
		logger.addThreadName("");
	}
	
	@Test
	public void testAddThreadName_ShouldNotThrowAnyException_WhenThreadNameArgumentIsNull(){
		logger.addThreadName(null);
	}
	
	@Test
	public void testRemoveThreadName_ShouldNotThrowAnyException_WhenCalled(){
		logger.removeThreadName("");
	}
	
	@Test
	public void testRemoveThreadName_ShouldNotThrowAnyException_WhenThreadNameArgumentIsNull(){
		logger.removeThreadName(null);
	}
	
	public Object[][] dataFor_testIsLogLevel_ShouldReturnTrueForAllLogLevels(){
		return allLogLevels();
	}

	private Object[][] allLogLevels() {
		LogLevel[] logLevels = LogLevel.values();
		Object[][] data = new Object[logLevels.length][1];
		int iteration = 0;
		for(LogLevel logLevel : logLevels){
			data[iteration][0] = logLevel;
			iteration++;
		}
		return data;
	}
	
	@Test
	public void testIsErrorLogLevel_ShouldReturnFalse() {
		assertTrue(logger.isErrorLogLevel());
	}
	
	@Test
	public void testIsWarnLogLevel_ShouldReturnFalse() {
		assertTrue(logger.isWarnLogLevel());
	}
	
	@Test
	public void testIsInfoLogLevel_ShouldReturnFalse() {
		assertTrue(logger.isInfoLogLevel());
	}
	
	@Test
	public void testIsDebugLogLevel_ShouldReturnFalse() {
		assertTrue(logger.isDebugLogLevel());
	}
}
