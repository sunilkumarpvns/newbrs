package com.elitecore.commons.threads;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadLogKeyResolver.ThreadResolver;

/**
 * 
 * @author narendra.pathai
 *
 */
public class EliteThreadLogKeyResolverTest {

	private EliteThreadLogKeyResolver resolver;

	@Before
	public void setUp(){
		resolver = new EliteThreadLogKeyResolver();
	}
	
	@Test
	public void testResolveKey_ShouldReturnDefaultLogKey_WhenThreadIsNotAnEliteThread(){
		//as current thread is not a elite thread it should return default key
		String key = resolver.resloveKey();
		
		assertEquals(LogManager.DEFAULT_LOGGER_KEY, key);
	}
	
	@Test
	public void testResolveKey_ShouldUseKeyOfEliteThread_WhenCurrentThreadIsInstanceOfEliteThread(){
		//overriding thread resolver to set custom stub
		resolver.threadResolver = new ThreadResolverStub();
		
		assertEquals("KEY", resolver.resloveKey());
	}
	
	class ThreadResolverStub extends ThreadResolver{
		@Override
		public Thread resolveThread() {
			EliteThreadFactory factory = new EliteThreadFactory("KEY", "PREFIX", 1);
			return factory.newThread(null);
		}
	}
}
