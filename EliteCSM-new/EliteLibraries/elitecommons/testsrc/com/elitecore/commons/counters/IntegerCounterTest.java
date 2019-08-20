package com.elitecore.commons.counters;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class IntegerCounterTest {

	IntegerCounter counter;

	public class withNoParameterConstructor {

		@Before
		public void setup() {
			counter = new IntegerCounter();
		}

		@Test
		public void nextMethodReturnsZeroOnFirstTimeCall() {
			int next = counter.next();
			assertEquals(0, next);
		}

		@Test
		public void nextMethodReturnsOneOnSecondTimeCall() {
			int next = counter.next();
			next = counter.next();
			assertEquals(1, next);
		}

		@Test
		public void returnsCircularValuesFromZeroToIntegerMaxValue() {
			for (int i=0; i<Integer.MAX_VALUE; i++) {
				int next = counter.next();
				assertEquals(i, next);
			}
			for (int i=0; i<100; i++) {
				int next = counter.next();
				assertEquals(i, next);
			}
		}

	}

	public class withModulasAsTen {

		@Before
		public void setup() {
			counter = new IntegerCounter(10);
		}

		@Test
		public void nextMethodReturnsZeroOnFirstTimeCall() {
			int next = counter.next();
			assertEquals(0, next);
		}

		@Test
		public void nextMethodReturnsOneOnSecondTimeCall() {
			int next = counter.next();
			next = counter.next();
			assertEquals(1, next);
		}

		@Test
		public void returnsCircularValuesFromZeroToNine() {
			for (int i=0; i<10; i++) {
				int next = counter.next();
				assertEquals(i, next);
			}
			for (int i=0; i<10; i++) {
				int next = counter.next();
				assertEquals(i, next);
			}
		}

	}
	
	public class withModulasAsTenAndInitialValueAsFive {

		@Before
		public void setup() {
			counter = new IntegerCounter(5, 10);
		}

		@Test
		public void nextMethodReturnsSixOnFirstTimeCall() {
			int next = counter.next();
			assertEquals(6, next);
		}

		@Test
		public void nextMethodReturnssevenOnSecondTimeCall() {
			int next = counter.next();
			next = counter.next();
			assertEquals(7, next);
		}

		@Test
		public void returnsSixToNineAndThanOnwardsCircularValuesFromZeroToNine() {
			for (int i=6; i<10; i++) {
				int next = counter.next();
				assertEquals(i, next);
			}
			for (int i=0; i<10; i++) {
				int next = counter.next();
				assertEquals(i, next);
			}
		}

	}
	
	public class withModulasAsTenAndInitialValueAsMinusFive {

		@Before
		public void setup() {
			counter = new IntegerCounter(-5, 10);
		}

		@Test
		public void nextMethodReturnsSixOnFirstTimeCall() {
			int next = counter.next();
			assertEquals(-4, next);
		}

		@Test
		public void nextMethodReturnssevenOnSecondTimeCall() {
			int next = counter.next();
			next = counter.next();
			assertEquals(-3, next);
		}

		@Test
		public void returnsSixToNineAndThanOnwardsCircularValuesFromZeroToNine() {
			for (int i=-4; i<10; i++) {
				int next = counter.next();
				assertEquals(i, next);
			}
			for (int i=0; i<10; i++) {
				int next = counter.next();
				assertEquals(i, next);
			}
		}

	}
	
	public class withModulasAsTenAndInitialValueAsFifteen {

		@Before
		public void setup() {
			counter = new IntegerCounter(15, 10);
		}

		@Test
		public void nextMethodReturnsSixOnFirstTimeCall() {
			int next = counter.next();
			assertEquals(6, next);
		}

		@Test
		public void nextMethodReturnssevenOnSecondTimeCall() {
			int next = counter.next();
			next = counter.next();
			assertEquals(7, next);
		}

		@Test
		public void returnsSixToNineAndThanOnwardsCircularValuesFromZeroToNine() {
			for (int i=6; i<10; i++) {
				int next = counter.next();
				assertEquals(i, next);
			}
			for (int i=0; i<10; i++) {
				int next = counter.next();
				assertEquals(i, next);
			}
		}

	}
	
	public class withModulasAsMinusTenAndInitialValueAsFive {

		@Before
		public void setup() {
			counter = new IntegerCounter(5, -10);
		}

		@Test
		public void nextMethodReturnsSixOnFirstTimeCall() {
			int next = counter.next();
			assertEquals(6, next);
		}

		@Test
		public void nextMethodReturnssevenOnSecondTimeCall() {
			int next = counter.next();
			next = counter.next();
			assertEquals(7, next);
		}

		@Test
		public void returnsSixToNineAndThanOnwardsCircularValuesFromZeroToNine() {
			for (int i=6; i<10; i++) {
				int next = counter.next();
				assertEquals(i, next);
			}
			for (int i=0; i<10; i++) {
				int next = counter.next();
				assertEquals(i, next);
			}
		}

	}
	
	public class withModulasAsMinusTenAndInitialValueAsMinusFive {

		@Before
		public void setup() {
			counter = new IntegerCounter(-5, -10);
		}

		@Test
		public void nextMethodReturnsSixOnFirstTimeCall() {
			int next = counter.next();
			assertEquals(-4, next);
		}

		@Test
		public void nextMethodReturnssevenOnSecondTimeCall() {
			int next = counter.next();
			next = counter.next();
			assertEquals(-3, next);
		}

		@Test
		public void returnsSixToNineAndThanOnwardsCircularValuesFromZeroToNine() {
			for (int i=-4; i<10; i++) {
				int next = counter.next();
				assertEquals(i, next);
			}
			for (int i=0; i<10; i++) {
				int next = counter.next();
				assertEquals(i, next);
			}
		}

	}
	
	@Test
	public void withModuleIntegerMaxValueAndInitialIntegerMaxMinusOneNextMethodWillReturnZeroOnFirstCall() {
		counter = new IntegerCounter(Integer.MAX_VALUE-1, Integer.MAX_VALUE);
		long next = counter.next();
		assertEquals(0, next);
	}

}
