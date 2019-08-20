package com.elitecore.corenetvertex.util;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(value = JUnitParamsRunner.class)
public class ConcurrentCounterTest {

	private ConcurrentCounter concurrentCounter;

	@Test
	public void test_DefaultConstructor_should_take_default_value_for_min_max_val() {
		concurrentCounter = new ConcurrentCounter();
		
		assertEquals("Min value should be zero", 0, concurrentCounter.getMinValue());
		assertEquals("Max value should be MAX LONG value", Long.MAX_VALUE, concurrentCounter.getMaxValue());
		assertEquals("Initial counter should be zero", 0, concurrentCounter.getCounter());
	}
	
	@Test
	public void test_Constructor_should_should_set_max_min_and_counter_value() {
		concurrentCounter = new ConcurrentCounter(10,20);
		assertEquals("Min value is mismatched", 10, concurrentCounter.getMinValue());
		assertEquals("Max value is mismatched", 20, concurrentCounter.getMaxValue());
		assertEquals("initially counter should be same as min value", 10, concurrentCounter.getCounter());
	}
	
	@Test
	public void test_incrementMaxVal_should_increment_max_value() {
		concurrentCounter = new ConcurrentCounter(10,20);
		concurrentCounter.incrementMaxVal();
		assertEquals("Max value should be incremented", 21, concurrentCounter.getMaxValue());
	}
	
	@Test
	public void test_decrementMaxVal_should_decrement_max_value() {
		concurrentCounter = new ConcurrentCounter(10,20);
		concurrentCounter.decrementMaxVal();
		assertEquals("Max value should be decremented", 19, concurrentCounter.getMaxValue());
	}
	
	@Test
	public void test_setCounter_should_assign_provided_value() {
		concurrentCounter = new ConcurrentCounter(10, 20);
		concurrentCounter.setCounter(15);
		assertEquals("counter mismatched", 15, concurrentCounter.getCounter());
	}
	
	private Object[][] dataProviderFor_test_incrementCounter_should_increment_counter_value() {
		return new Object[][] {
				{
					1, 11
				},
				{
					2, 12
				},
				{
					3, 13
				},
				{
					4, 14
				},
				{
					5, 15
				},
				{
					6, 10
				},
				{
					7, 11 
				}
		}; 
	}
	
	/**
	 *Min=10, Max=15 
	 *
	 *
	 *@param incrementCount used to continuous incrementing counter
	 *@param expectedCounter, expected counter value after decrementing counter
	 */
	@Test
	@Parameters(method="dataProviderFor_test_incrementCounter_should_increment_counter_value")
	public void test_incrementCounter_should_increment_counter_value(
			int incrementCount, int expectedCounter ) {
		concurrentCounter = new ConcurrentCounter(10,15);
		
		for (int i = 1; i <= incrementCount; i++) {
			concurrentCounter.incrementCounter();
		}
		
		assertEquals("counter mismatched", expectedCounter, concurrentCounter.getCounter());
	}
	
	private Object[][] dataProviderFor_test_decrementCounter_should_decrement_counter_value() {
		return new Object[][] {
				{
					1, 14
				},
				{
					2, 13
				},
				{
					3, 12
				},
				{
					4, 11
				},
				{
					5, 10
				},
				{
					6, 15
				},
				{
					7, 14 
				}
		}; 
	}
	
	
	/**
	 *Min=10, Max=15 
	 *
	 *
	 *@param decrementCount used to continuous decrementing counter
	 *@param expectedCounter, expected counter value after decrementing counter
	 */
	@Test
	@Parameters(method="dataProviderFor_test_decrementCounter_should_decrement_counter_value")
	public void test_decrementCounter_should_decrement_counter_value(
			int decrementCount, int expectedCounter) {
		concurrentCounter = new ConcurrentCounter(10,15);
		concurrentCounter.setCounter(15);
		
		for (int i = 1; i <= decrementCount; i++) {
			concurrentCounter.decrementCounter();
		}
		
		assertEquals("counter mismatched", expectedCounter, concurrentCounter.getCounter());
	}
	
	
	/*
	 * counter created using default constructor so MINVAL=0, MAXVAL=Long.MAX_VALUE
	 * 
	 * counter.setCounter(Long.MAX_VALUE)
	 * counter.incrementCounter()
	 * 
	 * now counter should be 0
	 */
	@Test
	public void test_incrementCounter_when_MAXVALUE_reached_it_should_continue_from_min_value() {
		concurrentCounter = new ConcurrentCounter();
		concurrentCounter.setCounter(Long.MAX_VALUE);
		concurrentCounter.incrementCounter();
		
		assertEquals("counter mismatched", 0, concurrentCounter.getCounter());
	}
	

	/*
	 * counter created using default constructor so MINVAL=0, MAXVAL=Long.MAX_VALUE
	 * 
	 * counter.decrementCounter()
	 * 
	 * now counter should be LONG.MAX_VALUE
	 */
	@Test
	public void test_decrementCounter_when_min_value_reached_it_should_continue_from_max_value() {
		concurrentCounter = new ConcurrentCounter();
		concurrentCounter.decrementCounter();
		
		assertEquals("counter mismatched", Long.MAX_VALUE, concurrentCounter.getCounter());
	}
}
