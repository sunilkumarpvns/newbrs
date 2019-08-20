package com.elitecore.commons.threads;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.Thread.UncaughtExceptionHandler;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.commons.threads.EliteThreadFactory.EliteThread;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class EliteThreadFactoryTest {

	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testConstructor_ShouldThrowNPE_WhenThreadKeyIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("threadKey is null");
		
		new EliteThreadFactory(null, "", -1);
	}
	
	@Test
	public void testConstructor_ShouldThrowNPE_WhenThreadNamePrefixIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("threadNamePrefix is null");
		
		new EliteThreadFactory("", null,  -1);
	}
	
	@Test
	public void testConstructor_ShouldThrowNPE_WhenUncaughtExceptionHandlerIsNull() {
		exception.expect(NullPointerException.class);
		exception.expectMessage("uncaughtExceptionHandler is null");
		
		new EliteThreadFactory("", "",  -1, null);
	}
	
	@Test
	@Parameters({"-2", "-1", "0" , "11", "12" , "13"})
	public void testConstructor_ShouldUseNormalThreadPriority_WhenThreadPriorityArgumentIsOutOfRange(int threadPriority){
		//thread priority falls in the range of 1-10
		EliteThreadFactory factory = createFactory(threadPriority);
		
		Thread thread = factory.newThread(null);
		assertEquals(Thread.NORM_PRIORITY, thread.getPriority());
	}

	private EliteThreadFactory createFactory(int threadPriority) {
		EliteThreadFactory factory = new EliteThreadFactory("KEY", "PREFIX",threadPriority);
		return factory;
	}
	
	@Test
	@Parameters({"1", "2", "5" , "8", "9" , "10"})
	public void testConstructor_ShouldUseGivenThreadPriority_WhenThreadPriorityFallsInRange(int threadPriority){
		EliteThreadFactory factory = createFactory(threadPriority);
		
		Thread thread = factory.newThread(null);
		assertEquals(threadPriority, thread.getPriority());
	}
	
	@Test
	public void testNewThread_ShouldReturnAnInstanceOfCustomThread(){
		EliteThreadFactory factory = createFactory(Thread.NORM_PRIORITY);
		Thread t = factory.newThread(null);
		
		assertEquals(EliteThread.class, t.getClass());
	}
	
	@Test
	public void testNewThread_ShouldNotThrowAnyException_WhenRunnableIsNull(){
		EliteThreadFactory factory = createFactory(Thread.NORM_PRIORITY);
		factory.newThread(null);
	}
	
	@Test
	public void testNewThread_ShouldPrefixTheThreadNamePrefixPassedInNameOfThreadsItCreates(){
		EliteThreadFactory factory = createFactory(Thread.NORM_PRIORITY);
		Thread t = factory.newThread(null);
		
		assertTrue(t.getName().startsWith("PREFIX"));
	}
	
	@Test
	public void testNewThread_ShouldSuffixThreadNameWithTheCounterOfNumberOfThreadsCreatedByFactory(){
		EliteThreadFactory factory = createFactory(Thread.NORM_PRIORITY);

		Thread t = factory.newThread(null);
		assertTrue(t.getName().endsWith("1"));
		
		t = factory.newThread(null);
		assertTrue(t.getName().endsWith("2"));
	}
	
	@Test
	public void givenUncaughtExceptionHandlerIsProvided_ShouldSetUncaughtExceptionHandlerInAllThreadItCreates() {
		UncaughtExceptionHandler handler = new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				
			}
		};
		
		EliteThreadFactory factory = new EliteThreadFactory("KEY", "PREFIX", Thread.NORM_PRIORITY, handler);
		Thread t = factory.newThread(null);
		
		assertSame(handler, t.getUncaughtExceptionHandler());
	}
}
