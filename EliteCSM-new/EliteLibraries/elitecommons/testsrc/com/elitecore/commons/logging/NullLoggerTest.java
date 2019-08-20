package com.elitecore.commons.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class NullLoggerTest {

	private NullLogger logger;

	@Before
	public void setUp(){
		logger = new NullLogger();
	}
	
	@Test
	public void testConstructor_ShouldHaveADefaultNoArgConstructor() throws SecurityException, NoSuchMethodException{
		NullLogger.class.getDeclaredConstructor(new Class<?>[]{});
	}
	
	@Test
	public void testInfo_ShouldNotThrowAnyExceptionIfArgumentsAreNull(){
		logger.info(null, null);
	}
	
	@Test
	public void testDebug_ShouldNotThrowAnyExceptionIfArgumentsAreNull(){
		logger.debug(null, null);
	}
	
	@Test
	public void testWarn_ShouldNotThrowAnyExceptionIfArgumentsAreNull(){
		logger.warn(null, null);
	}
	
	@Test
	public void testError_ShouldNotThrowAnyExceptionIfArgumentsAreNull(){
		logger.error(null, null);
	}
	
	@Test
	public void testTrace_ShouldNotThrowAnyExceptionIfArgumentsAreNull(){
		logger.trace(null, (String)null);
	}
	
	@Test
	public void testTraceWithThrowable_ShouldNotThrowAnyExceptionIfArgumentsAreNull(){
		logger.trace(null, (Throwable)null);
	}
	
	@Test
	@Parameters(method = "dataFor_testIsLogLevel_ShouldReturnFalseForAllLogLevels")
	public void testIsLogLevel_ShouldReturnFalseForAllLogLevels(LogLevel logLevel){
		assertFalse(logger.isLogLevel(logLevel));
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
	
	@Test
	public void testTraceWithThrowable_ShouldNotThrowAnyException_WhenThrowableIsNull(){
		logger.trace(null);
	}
	
	@Test
	public void testGetCurrentLogLevel_ShouldReturnNoneLogLevel(){
		assertEquals(LogLevel.NONE.level, logger.getCurrentLogLevel());
	}
	
	public Object[][] dataFor_testIsLogLevel_ShouldReturnFalseForAllLogLevels(){
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
		assertFalse(logger.isErrorLogLevel());
	}
	
	@Test
	public void testIsWarnLogLevel_ShouldReturnFalse() {
		assertFalse(logger.isWarnLogLevel());
	}
	
	@Test
	public void testIsInfoLogLevel_ShouldReturnFalse() {
		assertFalse(logger.isInfoLogLevel());
	}
	
	@Test
	public void testIsDebugLogLevel_ShouldReturnFalse() {
		assertFalse(logger.isDebugLogLevel());
	}
}
