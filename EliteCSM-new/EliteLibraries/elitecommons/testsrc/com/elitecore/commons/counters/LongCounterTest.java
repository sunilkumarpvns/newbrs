package com.elitecore.commons.counters;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class LongCounterTest {
	
	LongCounter counter;

	public class withNoParameterConstructor {

		@Before
		public void setup() {
			counter = new LongCounter();
		}

		@Test
		public void nextMethodReturnsZeroOnFirstTimeCall() {
			long next = counter.next();
			assertEquals(0, next);
		}

		@Test
		public void nextMethodReturnsOneOnSecondTimeCall() {
			long next = counter.next();
			next = counter.next();
			assertEquals(1, next);
		}

//		@Test
//		public void returnsCircularValuesFromZeroToIntegerMaxValue() {
//			Instant last = Instant.now();
//			for (long i=0; i<Long.MAX_VALUE; i++) {
//				long next = counter.next();
//				assertEquals(i, next);
//				if(i%1000000000l == 0) {
//					Instant now = Instant.now();
//					System.err.println(Duration.between(last, now) + "    " + i);
//					last = now;
//				}
//			}
//			for (int i=0; i<100; i++) {
//				long next = counter.next();
//				assertEquals(i, next);
//			}
//		}

	}

	public class withModulasAsTen {

		@Before
		public void setup() {
			counter = new LongCounter(10);
		}

		@Test
		public void nextMethodReturnsZeroOnFirstTimeCall() {
			long next = counter.next();
			assertEquals(0, next);
		}

		@Test
		public void nextMethodReturnsOneOnSecondTimeCall() {
			long next = counter.next();
			next = counter.next();
			assertEquals(1, next);
		}

		@Test
		public void returnsCircularValuesFromZeroToNine() {
			for (int i=0; i<10; i++) {
				long next = counter.next();
				assertEquals(i, next);
			}
			for (int i=0; i<10; i++) {
				long next = counter.next();
				assertEquals(i, next);
			}
		}

	}
	
	public class withModulasAsTenAndInitialValueAsFive {

		@Before
		public void setup() {
			counter = new LongCounter(5, 10);
		}

		@Test
		public void nextMethodReturnsSixOnFirstTimeCall() {
			long next = counter.next();
			assertEquals(6, next);
		}

		@Test
		public void nextMethodReturnssevenOnSecondTimeCall() {
			long next = counter.next();
			next = counter.next();
			assertEquals(7, next);
		}

		@Test
		public void returnsSixToNineAndThanOnwardsCircularValuesFromZeroToNine() {
			for (long i=6; i<10; i++) {
				long next = counter.next();
				assertEquals(i, next);
			}
			for (long i=0; i<10; i++) {
				long next = counter.next();
				assertEquals(i, next);
			}
		}

	}
	
	public class withModulasAsTenAndInitialValueAsMinusFive {

		@Before
		public void setup() {
			counter = new LongCounter(-5, 10);
		}

		@Test
		public void nextMethodReturnsSixOnFirstTimeCall() {
			long next = counter.next();
			assertEquals(-4, next);
		}

		@Test
		public void nextMethodReturnssevenOnSecondTimeCall() {
			long next = counter.next();
			next = counter.next();
			assertEquals(-3, next);
		}

		@Test
		public void returnsSixToNineAndThanOnwardsCircularValuesFromZeroToNine() {
			for (long i=-4; i<10; i++) {
				long next = counter.next();
				assertEquals(i, next);
			}
			for (int i=0; i<10; i++) {
				long next = counter.next();
				assertEquals(i, next);
			}
		}

	}
	
	public class withModulasAsTenAndInitialValueAsFifteen {

		@Before
		public void setup() {
			counter = new LongCounter(15, 10);
		}

		@Test
		public void nextMethodReturnsSixOnFirstTimeCall() {
			long next = counter.next();
			assertEquals(6, next);
		}

		@Test
		public void nextMethodReturnssevenOnSecondTimeCall() {
			long next = counter.next();
			next = counter.next();
			assertEquals(7, next);
		}

		@Test
		public void returnsSixToNineAndThanOnwardsCircularValuesFromZeroToNine() {
			for (long i=6; i<10; i++) {
				long next = counter.next();
				assertEquals(i, next);
			}
			for (long i=0; i<10; i++) {
				long next = counter.next();
				assertEquals(i, next);
			}
		}

	}
	
	public class withModulasAsMinusTenAndInitialValueAsFive {

		@Before
		public void setup() {
			counter = new LongCounter(5, -10);
		}

		@Test
		public void nextMethodReturnsSixOnFirstTimeCall() {
			long next = counter.next();
			assertEquals(6, next);
		}

		@Test
		public void nextMethodReturnssevenOnSecondTimeCall() {
			long next = counter.next();
			next = counter.next();
			assertEquals(7, next);
		}

		@Test
		public void returnsSixToNineAndThanOnwardsCircularValuesFromZeroToNine() {
			for (int i=6; i<10; i++) {
				long next = counter.next();
				assertEquals(i, next);
			}
			for (int i=0; i<10; i++) {
				long next = counter.next();
				assertEquals(i, next);
			}
		}

	}
	
	public class withModulasAsMinusTenAndInitialValueAsMinusFive {

		@Before
		public void setup() {
			counter = new LongCounter(-5, -10);
		}

		@Test
		public void nextMethodReturnsSixOnFirstTimeCall() {
			long next = counter.next();
			assertEquals(-4, next);
		}

		@Test
		public void nextMethodReturnssevenOnSecondTimeCall() {
			long next = counter.next();
			next = counter.next();
			assertEquals(-3, next);
		}

		@Test
		public void returnsSixToNineAndThanOnwardsCircularValuesFromZeroToNine() {
			for (int i=-4; i<10; i++) {
				long next = counter.next();
				assertEquals(i, next);
			}
			for (int i=0; i<10; i++) {
				long next = counter.next();
				assertEquals(i, next);
			}
		}

	}
	
	@Test
	public void withModuleLongMaxValueAndInitialLongMaxMinusOneNextMethodWillReturnZeroOnFirstCall() {
		counter = new LongCounter(Long.MAX_VALUE-1, Long.MAX_VALUE);
		long next = counter.next();
		assertEquals(0, next);
	}

}
