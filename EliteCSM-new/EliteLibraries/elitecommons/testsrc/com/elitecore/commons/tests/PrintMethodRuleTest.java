package com.elitecore.commons.tests;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.logging.StringCollectionLogger;

/**
 * 
 * @author narendra.pathai
 *
 */
public class PrintMethodRuleTest {

	private StringCollectionLogger logger = new StringCollectionLogger();
	
	@Before
	public void setUp() {
		LogManager.setDefaultLogger(logger);
	}
	
	
	@Test
	public void printsTheNameOfTestMethodWhenItsExecutionStarts() {
		JUnitCore.runClasses(ClassWithPrintMethodRule.class);
		
		assertTrue(logger.contents().contains("Starting execution of method: someTest"));
	}
	
	@Test
	public void printsTheNameOfMethodWhenItsExecutionCompletes() {
		JUnitCore.runClasses(ClassWithPrintMethodRule.class);
		
		assertTrue(logger.contents().contains("Ending execution of method: someTest"));
	}
	
	@Test
	public void printsTheNameOfTestMethodThrowingCheckedExceptionWhenItsExecutionStarts() {
		JUnitCore.runClasses(ClassWithPrintMethodRule.class);
		
		assertTrue(logger.contents().contains("Starting execution of method: someTestThrowingCheckedException"));
	}
	
	@Test
	public void printsTheNameOfMethodThrowingCheckedExceptionWhenItsExecutionCompletes() {
		JUnitCore.runClasses(ClassWithPrintMethodRule.class);
		
		assertTrue(logger.contents().contains("Ending execution of method: someTestThrowingCheckedException"));
	}
	
	@Test
	public void printsTheNameOfTestMethodThrowingRuntimeExceptionWhenItsExecutionStarts() {
		JUnitCore.runClasses(ClassWithPrintMethodRule.class);
		
		assertTrue(logger.contents().contains("Starting execution of method: someTestThrowingRuntimeException"));
	}
	
	@Test
	public void printsTheNameOfMethodThrowingRuntimeExceptionWhenItsExecutionCompletes() {
		JUnitCore.runClasses(ClassWithPrintMethodRule.class);
		
		assertTrue(logger.contents().contains("Ending execution of method: someTestThrowingRuntimeException"));
	}
	
	@Test
	public void printsTheNameOfAFailingTestMethodWhenItsExecutionStarts() {
		JUnitCore.runClasses(ClassWithPrintMethodRule.class);
		
		assertTrue(logger.contents().contains("Starting execution of method: aFailingTest"));
	}
	
	@Test
	public void printsTheNameOfAFailingTestMethodWhenItsExecutionCompletes() {
		JUnitCore.runClasses(ClassWithPrintMethodRule.class);
		
		assertTrue(logger.contents().contains("Ending execution of method: aFailingTest"));
	}
	
	public static class ClassWithPrintMethodRule {
		
		@Rule public PrintMethodRule printMethod = new PrintMethodRule();
		
		@Test
		public void someTest() {
			System.out.println("Executing method someTest");
		}
		
		@Test
		public void someTestThrowingCheckedException() throws Exception {
			throw new Exception();
		}
		
		@Test
		public void someTestThrowingRuntimeException() throws Exception {
			throw new Exception();
		}
		
		@Test
		public void aFailingTest() {
			assertTrue(false);
		}
	}
}
