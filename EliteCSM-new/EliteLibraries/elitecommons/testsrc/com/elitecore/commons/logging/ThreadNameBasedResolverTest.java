package com.elitecore.commons.logging;

import static org.junit.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.commons.logging.ThreadNameBasedReslover.ThreadNameResolver;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class ThreadNameBasedResolverTest {

	@Test(expected = NoSuchMethodException.class)
	public void testConstructor_ShouldNotHaveANoArgConstructor() throws SecurityException, NoSuchMethodException{
		ThreadNameBasedReslover.class.getDeclaredConstructor(new Class<?>[]{});
	}
	
	@Test
	public void testConstructor_ShouldHaveConstructorAcceptingKeyLengthForName() throws SecurityException, NoSuchMethodException{
		ThreadNameBasedReslover.class.getDeclaredConstructor(new Class<?>[]{int.class});
	}
	
	@Test(expected = IllegalArgumentException.class)
	@Parameters({"-2", "-1", "0"})
	public void testConstructor_ShouldThrowIllegalArgumentException_WhenKeyLengthIsLessThanOrEqualToZero(int keyLength){
		new ThreadNameBasedReslover(keyLength);
	}
	
	@Test
	@Parameters(method = "dataFor_testResolveKey_ShouldReturnDefaultLogKey_WhenThreadNameSizeIsLessThanKeyLength")
	public void testResolveKey_ShouldReturnDefaultLogKey_WhenThreadNameSizeIsLessThanKeyLength(int keyLength, String threadName, String expectedKey){
		ThreadNameBasedReslover reslover = new ThreadNameBasedReslover(keyLength);
		reslover.nameResolver = new ThreadNameResolverStub(threadName);
		
		assertEquals(expectedKey, reslover.resloveKey());
	}
	
	public Object[][] dataFor_testResolveKey_ShouldReturnDefaultLogKey_WhenThreadNameSizeIsLessThanKeyLength(){
		return new Object[][]{
				{1, "", LogManager.DEFAULT_LOGGER_KEY},
				{2, "a",LogManager.DEFAULT_LOGGER_KEY},
				{3,	"aa", LogManager.DEFAULT_LOGGER_KEY}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testResolveKey_ShouldRetrunExtractedKeyFromName_WhenThreadNameLengthIsGreaterOrEqualsToKeyLength")
	public void testResolveKey_ShouldRetrunExtractedKeyFromName_WhenThreadNameLengthIsGreaterOrEqualsToKeyLength(int keyLength, String threadName, String expectedKey){
		ThreadNameBasedReslover reslover = new ThreadNameBasedReslover(keyLength);
		reslover.nameResolver = new ThreadNameResolverStub(threadName);
		
		assertEquals(expectedKey, reslover.resloveKey());
	}
	
	public Object[][] dataFor_testResolveKey_ShouldRetrunExtractedKeyFromName_WhenThreadNameLengthIsGreaterOrEqualsToKeyLength(){
		return new Object[][]{
				{1, " ", " "},
				{1, "\t", "\t"},
				{1, "a", "a"},
				{1, "z", "z"},
				{2, "aa","aa"},
				{2, "aaa","aa"},
				{2, "aaazz","aa"},
				{3,	"aazzz", "aaz"},
				{3,	"abzzzz", "abz"}
		};
	}
	
	@Test
	public void testResolveKey_ShouldUseCurrentThreadNameForExtractingTheKey(){
		String currentThreadName = Thread.currentThread().getName();
		String expectedKey = currentThreadName.substring(0, 4);
		ThreadNameBasedReslover reslover = new ThreadNameBasedReslover(4);
		
		assertEquals(expectedKey, reslover.resloveKey());
	}
	
	private static class ThreadNameResolverStub extends ThreadNameResolver{
		private final String threadName;

		public ThreadNameResolverStub(String threadName) {
			this.threadName = threadName;
		}
		
		@Override
		public String resloveName() {
			return threadName;
		}
	}
}
