package com.elitecore.commons.logging;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import junitparams.JUnitParamsRunner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.commons.logging.LogManager.LogKeyResolver;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class LogManagerTest {

	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testSetLogger_ShouldThrowNPE_WhenLogKeyIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("key for logger is null");
		LogManager.setLogger(null, new NullLogger());
	}
	
	@Test
	public void testSetLogger_ShouldThrowNPE_WhenLoggerIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("logger is null");
		LogManager.setLogger("KEY", null);
	}
	
	@Test
	public void testSetLogger_ShouldMapTheLoggerInstanceToTheKey(){
		ILogger logger = new NullLogger();
		LogManager.setLogger("KEY", logger);
		assertSame(logger, LogManager.get("KEY"));
	}
	
	@Test
	public void testSetDefaultLogger_ShouldThrowNPE_WhenLoggerIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("defaultLogger is null");
		LogManager.setDefaultLogger(null);
	}
	
	@Test
	public void testSetDefaultLogger_ShouldSetPassedLoggerInstanceAsDefaultLogger(){
		clearLogCache();
		ILogger defaultLogger = new NullLogger();
		LogManager.setDefaultLogger(defaultLogger);
		
		assertSame(defaultLogger, LogManager.getLogger());
	}
	
	@Test
	public void testSetLogKeyResolver_ShouldThrowNPE_WhenResolverIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("logKeyResolver is null");
		LogManager.setLogKeyResolver(null);
	}
	
	@Test
	public void testGetLogger_ShouldReturnDefaultLogger_WhenNoOtherLogKeyIsRegistered(){
		assertNotNull(LogManager.getLogger());
		assertSame(LogManager.get(LogManager.DEFAULT_LOGGER_KEY), LogManager.getLogger());
	}
	
	@Test
	public void testGetLogger_ShouldUseTheRegisteredLogKeyReslolverForResolvingKey_IfNotYetMapped(){
		clearLogCache();
		LogKeyResolver resolver = spy(new ThreadNameBasedReslover(3));
		LogManager.setLogKeyResolver(resolver);
		LogManager.getLogger();
		
		verify(resolver).resloveKey();
	}
	
	@Test
	public void testGetLogger_ShouldUseKeyResolverOnceDuringInitialLoggerAssignment_WhenTheLoggerIsNotMapped(){
		//making sure that no key is mapped
		clearLogCache();
		LogKeyResolver resolver = spy(new ThreadNameBasedReslover(3));
		LogManager.setLogKeyResolver(resolver);
		
		LogManager.getLogger();
		LogManager.getLogger();
		
		//there will be only one invocation on resolver
		verify(resolver, times(1)).resloveKey();
	}

	private void clearLogCache() {
		LogManager.loggerLocal.remove();
	}
	
	@Test
	public void testGetLogger_TwoSubsequentCallsToGetLogger_ShouldReturnSameInstance(){
		assertSame(LogManager.getLogger(), LogManager.getLogger());
	}
	
	@Test
	public void testIgnoreTrace_ShouldNotLog_AnyThrowable() {
		ILogger logger = spy (ILogger.class);
		LogManager.setDefaultLogger(logger);
		
		LogManager.ignoreTrace(new Throwable());
		
		verifyZeroInteractions(logger);
	}
}
