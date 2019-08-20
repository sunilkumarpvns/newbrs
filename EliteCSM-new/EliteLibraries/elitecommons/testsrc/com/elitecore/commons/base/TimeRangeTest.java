package com.elitecore.commons.base;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class TimeRangeTest {

	private static final String PATTERN = "dd/MM/yyyy hh:mm:ss";
	
	private TimeRange timeRange;
	
	private String startDateValue = "10/08/1991 00:00:00";
	private String endDateValue = "12/08/1991 00:00:00";
	private String checkingDate;
	
	public class OpenRangeContainsReturns {
		
		public class EndIsFinite {
			
			@Before
			public void setUp() throws ParseException {
				timeRange = TimeRange.open(PATTERN, startDateValue, endDateValue);
			}
			
			@Test
			public void trueGivenDateIsBetweenStartDateAndEndDate() throws ParseException {
				checkingDate = "11/08/1991 00:00:00";
				
				assertTrue(timeRange.contains(checkingDate));
			}
			
			@Test
			public void falseGivenDateIsSameAsStartDate() throws ParseException {
				checkingDate = "10/08/1991 00:00:00";
				
				assertFalse(timeRange.contains(checkingDate));
			}
			
			@Test
			public void falseGivenDateIsSameAsEndDate() throws ParseException {
				checkingDate="12/08/1991 00:00:00";
				
				assertFalse(timeRange.contains(checkingDate));
			}
			
			@Test
			public void falseGivenDateIsAfterEndDate() throws ParseException {
				checkingDate="13/08/1991 00:00:00";
				
				
				assertFalse(timeRange.contains(checkingDate));
			}
			
			
			@Test
			public void falseGivenDateIsBeforeStartDate() throws ParseException {
				checkingDate="09/08/1991 00:00:00";
				
				assertFalse(timeRange.contains(checkingDate));
			}
		}
		
		public class EndIsInfinite {

			@Before
			public void setUp() throws ParseException {
				timeRange = TimeRange.open(PATTERN, startDateValue);
			}
			
			@Test
			public void falseGivenDateIsSameAsStartDate() throws ParseException {
				checkingDate = "10/08/1991 00:00:00";
				
				assertFalse(timeRange.contains(checkingDate));
			}
			
			@Test
			public void trueGivenDateIsAfterStartDate() throws ParseException {
				checkingDate = "11/08/1991 00:00:00";
				
				assertTrue(timeRange.contains(checkingDate));
			}
			
			@Test
			public void falseGivenDateIsBeforeStartDate() throws ParseException {
				checkingDate = "09/08/1991 00:00:00";
				
				assertFalse(timeRange.contains(checkingDate));
			}
		}
		
		public class StartIsInfinite {

			@Before
			public void setUp() throws ParseException {
				timeRange = TimeRange.lessThan(PATTERN, endDateValue);
			}
			
			@Test
			public void falseGivenDateIsSameAsEndDate() throws ParseException {
				checkingDate = "12/08/1991 00:00:00";
				
				assertFalse(timeRange.contains(checkingDate));
			}
			
			@Test
			public void trueGivenDateIsBeforeEndDate() throws ParseException {
				checkingDate = "11/08/1991 00:00:00";
				
				assertTrue(timeRange.contains(checkingDate));
			}
			
			@Test
			public void falseGivenDateIsAfterEndDate() throws ParseException {
				checkingDate = "13/08/1991 00:00:00";
				
				assertFalse(timeRange.contains(checkingDate));
			}
		}
	}
	
	public class StartEndAreInfinite {
		
		@Before
		public void setup() {
			timeRange = TimeRange.open();
		}
		
		@Test
		public void alwaysReturnsTrue() throws ParseException {
			checkingDate = "09/08/1991 00:00:00";
			assertTrue(timeRange.contains(checkingDate));
			
			checkingDate = "09/08/2031 00:00:00";
			assertTrue(timeRange.contains(checkingDate));
		}
	}
	
	public class ClosedRangeContainsReturns {
		
		public class EndIsFinite {
			
			@Before
			public void setup() throws ParseException {
				timeRange = TimeRange.closed(PATTERN ,startDateValue, endDateValue);
			}
			
			@Test
			public void trueGivenDateIsBetweenStartDateAndEndDate() throws ParseException {
				checkingDate = "11/08/1991 00:00:00";
				
				assertTrue(timeRange.contains(checkingDate));
			}
			
			@Test
			public void trueGivenDateIsSameAsStartDate() throws ParseException {
				checkingDate = "10/08/1991 00:00:00";
				
				assertTrue(timeRange.contains(checkingDate));
			}
			
			@Test
			public void trueGivenDateIsSameAsEndDate() throws ParseException {
				checkingDate = "12/08/1991 00:00:00";
				
				assertTrue(timeRange.contains(checkingDate));
			}
			
			@Test
			public void falseGivenDateIsAfterEndDate() throws ParseException {
				checkingDate = "13/08/1991 00:00:00";
				
				assertFalse(timeRange.contains(checkingDate));
			}
			
			@Test
			public void falseGivenDateIsBeforeStartDate() throws ParseException {
				checkingDate="09/08/1991 00:00:00";
				
				assertFalse(timeRange.contains(checkingDate));
			}
		}
		
		public class EndIsInfinite {

			@Before
			public void setUp() throws ParseException {
				timeRange = TimeRange.closed(PATTERN ,startDateValue);
			}
			
			@Test
			public void trueGivenDateIsSameAsStartDate() throws ParseException {
				checkingDate = "10/08/1991 00:00:00";
				
				assertTrue(timeRange.contains(checkingDate));
			}
			
			@Test
			public void trueGivenDateIsAfterStartDate() throws ParseException {
				checkingDate = "11/08/1991 00:00:00";
				
				assertTrue(timeRange.contains(checkingDate));
			}
			
			@Test
			public void falseGivenDateIsBeforeStartDate() throws ParseException {
				checkingDate = "09/08/1991 00:00:00";
				
				assertFalse(timeRange.contains(checkingDate));
			}
		}
		
		public class StartIsInfinite {
			@Before
			public void setUp() throws ParseException {
				timeRange = TimeRange.atMost(PATTERN ,endDateValue);
			}
			
			@Test
			public void trueGivenDateIsSameAsEndDate() throws ParseException {
				checkingDate = "12/08/1991 00:00:00";
				
				assertTrue(timeRange.contains(checkingDate));
			}
			
			@Test
			public void trueGivenDateIsBeforeEndDate() throws ParseException {
				checkingDate = "11/08/1991 00:00:00";
				
				assertTrue(timeRange.contains(checkingDate));
			}
			
			@Test
			public void falseGivenDateIsAfterEndDate() throws ParseException {
				checkingDate = "13/08/1991 00:00:00";
				
				assertFalse(timeRange.contains(checkingDate));
			}
		}
	}
}
