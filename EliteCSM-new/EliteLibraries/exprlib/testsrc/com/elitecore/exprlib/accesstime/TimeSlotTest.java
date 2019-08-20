package com.elitecore.exprlib.accesstime;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.acesstime.TimePeriod;
import com.elitecore.acesstime.TimeSlot;
import com.elitecore.acesstime.exception.InvalidTimeSlotException;

@RunWith(JUnitParamsRunner.class)
public class TimeSlotTest {
	
	private final String MOY_MSG_CONVENTION = "Month of Year must be between 1 to 12";
	private final String DOM_MSG_CONVENTION = "Day of Month must be between 1 to 31";
	private final String DOW_MSG_CONVENTION = "Day of Week must be between 1 to 7";
	private final String HOUR_MSG_CONVENTION = "Hour must be between 0 to 23";
	private final String MINUTE_MSG_CONVENTION = "Minutes must be between 0 to 59";
	private final String SECOND_MSG_CONVENTION = "Seconds must be between 0 to 59";
	
	public @Rule ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testGetTimeSlots() throws InvalidTimeSlotException{
		
		Calendar calendar=Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			TimeSlot timeSlot= TimeSlot.getTimeSlot(null, null, null, "15-16,1-2,13-14,12-13,20-22,14-17,17:00:01-21:59:59,17:00:02-22:00:02");
			
			List<TimePeriod> timePeriods = new ArrayList<TimePeriod>();
			timePeriods.add(new TimePeriod(1, 0, 0, 2, 00, 00));
			timePeriods.add(new TimePeriod(12, 0, 0, 17, 0, 0));
			timePeriods.add(new TimePeriod(17, 0, 1, 22, 0, 2));
			
			assertEquals(timePeriods, timeSlot.getTimePeriods());

		}
	
	Object[][] dataProviderForTestMOY() {
		
		return new Object[][]{
		
				{
					Calendar.JANUARY,AccessTimePolicy.NO_TIME_OUT
				},
				{
					Calendar.FEBRUARY,AccessTimePolicy.NO_TIME_OUT
				},
				{
					Calendar.MARCH,AccessTimePolicy.NO_TIME_OUT
				},
				{
					Calendar.APRIL,AccessTimePolicy.NO_TIME_OUT
				},
				{
					Calendar.MAY,AccessTimePolicy.NO_TIME_OUT
				},
				{
					Calendar.JUNE,AccessTimePolicy.NO_TIME_OUT
				},
				{
					Calendar.JULY,AccessTimePolicy.FAILURE
				},
				{
					Calendar.AUGUST,AccessTimePolicy.NO_TIME_OUT
				},
				{
					Calendar.SEPTEMBER,AccessTimePolicy.FAILURE
				},
				{
					Calendar.OCTOBER,AccessTimePolicy.FAILURE
				},
				{
					Calendar.NOVEMBER,AccessTimePolicy.NO_TIME_OUT
				},
				{
					Calendar.DECEMBER,AccessTimePolicy.FAILURE
				},
				
		};
	}
	
	@Test
	@Parameters(method="dataProviderForTestMOY")
	public void testMOY(int month, long accessTimePolicyStatus) throws InvalidTimeSlotException{
	
			Calendar calendar=Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			TimeSlot timeSlot= TimeSlot.getTimeSlot("1-5,3-6,8,11", null, null, null);
			
			calendar.set(Calendar.MONTH, month);
			assertEquals(accessTimePolicyStatus,timeSlot.getDuration(calendar));
			assertEquals(accessTimePolicyStatus,timeSlot.getTimeInMillis(calendar));

	}
	
	Object[][] dataProviderForTestDOM() {
			
			return new Object[][]{
			
					{
						21,AccessTimePolicy.NO_TIME_OUT
					},
					{
						2,AccessTimePolicy.NO_TIME_OUT
					},
					{
						28,AccessTimePolicy.NO_TIME_OUT
					},
					{
						5,AccessTimePolicy.FAILURE
					},
					{
						7,AccessTimePolicy.FAILURE
					},
					{
						11,AccessTimePolicy.FAILURE
					},
					{
						1,AccessTimePolicy.NO_TIME_OUT
					},
					{
						31,AccessTimePolicy.FAILURE
					},
					{
						30,AccessTimePolicy.NO_TIME_OUT
					}
					
			};
		}
	
	@Test
	@Parameters(method="dataProviderForTestDOM")
	public void testDOM(int date, long accessTimePolicyStatus) throws InvalidTimeSlotException{
		
			Calendar calendar=Calendar.getInstance();
			calendar.set(Calendar.MONTH, Calendar.JANUARY);
			TimeSlot timeSlot= TimeSlot.getTimeSlot(null, "20-30,1,2,3,4", null, null);
			
			calendar.set(Calendar.DATE, date);
			assertEquals(accessTimePolicyStatus,timeSlot.getDuration(calendar));
			assertEquals(accessTimePolicyStatus,timeSlot.getTimeInMillis(calendar));
		
	}
	
	Object[][] dataProviderForTestDOW() {
		
		return new Object[][]{
		
				{
					Calendar.MONDAY,AccessTimePolicy.NO_TIME_OUT
				},
				{
					Calendar.TUESDAY,AccessTimePolicy.NO_TIME_OUT
				},
				{
					Calendar.WEDNESDAY,AccessTimePolicy.NO_TIME_OUT
				},
				{
					Calendar.THURSDAY,AccessTimePolicy.NO_TIME_OUT
				},
				{
					Calendar.FRIDAY,AccessTimePolicy.FAILURE
				},
				{
					Calendar.SATURDAY,AccessTimePolicy.FAILURE
				},
				{
					Calendar.SUNDAY,AccessTimePolicy.NO_TIME_OUT
				}
		};
	}
	
	@Test
	@Parameters(method="dataProviderForTestDOW")
	public void testDOW(int day, long accessTimePolicyStatus) throws InvalidTimeSlotException{
		
			Calendar calendar=Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			TimeSlot timeSlot= TimeSlot.getTimeSlot(null, null, "1-3,4,7", null);
			
			calendar.set(Calendar.DAY_OF_WEEK,day);
			assertEquals(accessTimePolicyStatus,timeSlot.getDuration(calendar));
			assertEquals(accessTimePolicyStatus,timeSlot.getTimeInMillis(calendar));
			
		}
	
	Object[][] dataProviderForTestTimePeriod() throws InvalidTimeSlotException{
			
			return new Object[][]{
			
					{
						16, 59, 58, 2, 2000l
					},
					{
						16, 59, 59, 1, 1000l
					},
					{
						17, 00, 00, AccessTimePolicy.FAILURE, AccessTimePolicy.FAILURE
					},
					{
						17, 00, 01, 9, 9000l
					},
					{
						17, 00, 02, 8, 8000l
					},
					{
						5, 30, 00, AccessTimePolicy.FAILURE, AccessTimePolicy.FAILURE
					},
					{
						19, 00, 00, AccessTimePolicy.FAILURE, AccessTimePolicy.FAILURE
					},
					{
						11, 00, 00, AccessTimePolicy.FAILURE, AccessTimePolicy.FAILURE
					},
					{
						6, 22, 00, 13080, 13080000l
					}
					};
		}
	
	@Test
	@Parameters(method="dataProviderForTestTimePeriod")
	public void testTimePeriod(int hourOfDay,int minute, int second, long duration, long millis) throws InvalidTimeSlotException{
		
			Calendar calendar=Calendar.getInstance();
			TimeSlot timeSlot= TimeSlot.getTimeSlot(null, null, null, "3-5,6-10,14-15,16-17,17:00:01-17:00:10, 21-1");
			
			calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.SECOND, second);
			assertEquals(duration,timeSlot.getDuration(calendar));

			long expectedMillis = calendar.getTimeInMillis() + millis;
			long actualMillis = timeSlot.getTimeInMillis(calendar) ;
			
			if(actualMillis > 0)
				assertEquals(expectedMillis, actualMillis);
			else
				assertEquals(AccessTimePolicy.FAILURE, actualMillis);
		
	}
	
		
	Object[][] dataProviderForTestAll() throws InvalidTimeSlotException{
			
			return new Object[][]{
			
					{
						Calendar.DECEMBER, 3, 7, 30, 00, 10920, 10920000
					},
					{
						Calendar.JUNE, 3, 3, 00, 00, 7200, 7200000
					},
					{
						Calendar.DECEMBER, 22, 7, 00, 00, 12720, 12720000
					},
					{
						Calendar.JUNE, 22, 13, 30, 00, AccessTimePolicy.FAILURE, AccessTimePolicy.FAILURE
					},
					{
						Calendar.MAY, 22, 12, 30, 00, AccessTimePolicy.FAILURE, AccessTimePolicy.FAILURE
					},
					{
						Calendar.AUGUST, 20, 11, 30, 00, AccessTimePolicy.FAILURE, AccessTimePolicy.FAILURE
					}
			};
		}
	
	@Test
	@Parameters(method="dataProviderForTestAll")
	public void testALL(int month, int date, int hour, int minute, int second, long duration, long millis) throws InvalidTimeSlotException{
		
			Calendar calendar=Calendar.getInstance();
			//calendar.set(Calendar.DAY_OF_MONTH, 1);
			TimeSlot timeSlot= TimeSlot.getTimeSlot("1-5,3-6,12", "20-30,1,2,3,4", null, "3-5,6-10:32,11-12,14-18,");

			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DATE, date);
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.SECOND, second);
			assertEquals(duration, timeSlot.getDuration(calendar));
			
			long expectedMillis = calendar.getTimeInMillis() + millis;
			long actualMillis = timeSlot.getTimeInMillis(calendar) ;
			
			if(actualMillis > 0)
				assertEquals(expectedMillis, actualMillis);
			else
				assertEquals(AccessTimePolicy.FAILURE, actualMillis);
			
		}
	
	@Test
	public void testNoTimeOutWhen_TimeSlotIs_Null_Or_Empty() throws InvalidTimeSlotException{
		
		Calendar calendar=Calendar.getInstance();
		TimeSlot timeSlotNull= TimeSlot.getTimeSlot(null, null, null, null);
		assertEquals(AccessTimePolicy.NO_TIME_OUT,timeSlotNull.getDuration(calendar));
		assertEquals(AccessTimePolicy.NO_TIME_OUT,timeSlotNull.getTimeInMillis(calendar));
		TimeSlot timeSlotEmpty= TimeSlot.getTimeSlot("", "", "", "");
		assertEquals(AccessTimePolicy.NO_TIME_OUT,timeSlotEmpty.getDuration(calendar));
		assertEquals(AccessTimePolicy.NO_TIME_OUT,timeSlotEmpty.getTimeInMillis(calendar));
		
	}
	
	public Object[][] data_Provider_For_Test_Invalid_MOY_DOM_DOW_TimePeriod_Should_throw_InvalidDayException()
			throws Exception {
		return new Object[][] {
				//MOY
				{
					"Invalid Input: 13, Invalid value:13, " + MOY_MSG_CONVENTION, "13", null, null, null
				},
				{
					"Invalid Input: 11,13, Invalid value:13, " + MOY_MSG_CONVENTION, "11,13", null, null, null
				},
				{
					"Invalid Input: 11-13, Invalid value:13, " + MOY_MSG_CONVENTION, "11-13", null, null, null
				},
				{
					"Invalid Input: 1-5,11-13, Invalid value:13, " + MOY_MSG_CONVENTION, "1-5,11-13", null, null, null
				},
				{
					"Invalid Input: 1.11, Invalid value:1.11, " + MOY_MSG_CONVENTION, "1.11", null, null, null
				},
				{
					"Invalid Input: abc, Invalid value:abc, " + MOY_MSG_CONVENTION, "abc", null, null, null
				},
				{
					"Invalid Input: #@$, Invalid value:#@$, " + MOY_MSG_CONVENTION, "#@$", null, null, null
				},
				{
					"Invalid Input: 11-#@$, Invalid value:11-#@$, " + MOY_MSG_CONVENTION, "11-#@$", null, null, null
				},
				{
					"Invalid Input: 2-,4, Invalid value:2-, value should not start or end with '-', " + MOY_MSG_CONVENTION, "2-,4", null, null, null
				},
				{
					"Invalid Input: -2, Invalid value:-2, value should not start or end with '-', " + MOY_MSG_CONVENTION, "-2", null, null, null
				},
				{
					"Invalid Input: ,  ,  ,, " + MOY_MSG_CONVENTION, ",  ,  ,", null, null, null
				},
				{
					"Invalid Input: 4-2, Invalid value:4-2, start value must be lesser than end value, " + MOY_MSG_CONVENTION, "4-2", null, null, null
				},
				{
					"Invalid Input: 2-4-6, Invalid value:2-4-6, " + MOY_MSG_CONVENTION, "2-4-6", null, null, null
				},
				//DOM
				{
					"Invalid Input: 32, Invalid value:32, " + DOM_MSG_CONVENTION, null, "32", null, null
				},
				{
					"Invalid Input: 21,32, Invalid value:32, " + DOM_MSG_CONVENTION, null, "21,32", null, null
				},
				{
					"Invalid Input: 21-32, Invalid value:32, " + DOM_MSG_CONVENTION, null, "21-32", null, null
				},
				{
					"Invalid Input: 11-20,21-32, Invalid value:32, " + DOM_MSG_CONVENTION, null, "11-20,21-32", null, null
				},
				{
					"Invalid Input: 1.11, Invalid value:1.11, " + DOM_MSG_CONVENTION, null, "1.11", null, null
				},
				{
					"Invalid Input: abc, Invalid value:abc, " + DOM_MSG_CONVENTION, null, "abc", null, null
				},
				{
					"Invalid Input: #@$, Invalid value:#@$, " + DOM_MSG_CONVENTION, null, "#@$", null, null
				},
				{
					"Invalid Input: 2-,4, Invalid value:2-, value should not start or end with '-', " + DOM_MSG_CONVENTION, null, "2-,4", null, null
				},
				{
					"Invalid Input: -2, Invalid value:-2, value should not start or end with '-', " + DOM_MSG_CONVENTION, null, "-2", null, null
				},
				{
					"Invalid Input: ,  ,  ,, " + DOM_MSG_CONVENTION,  null,",  ,  ,", null, null
				},
				{
					"Invalid Input: ,, " + DOM_MSG_CONVENTION,  null,",", null, null
				},
				
				//DOW
				{
					"Invalid Input: 8, Invalid value:8, " + DOW_MSG_CONVENTION, null, null, "8", null
				},
				{
					"Invalid Input: 1,8, Invalid value:8, " + DOW_MSG_CONVENTION, null, null, "1,8", null
				},
				{
					"Invalid Input: 1-9, Invalid value:9, " + DOW_MSG_CONVENTION, null, null, "1-9", null
				},
				{
					"Invalid Input: 1-3,5-10, Invalid value:10, " + DOW_MSG_CONVENTION, null, null, "1-3,5-10", null
				},
				{
					"Invalid Input: 1.11, Invalid value:1.11, " + DOW_MSG_CONVENTION, null, null, "1.11", null
				},
				{
					"Invalid Input: abc, Invalid value:abc, " + DOW_MSG_CONVENTION, null, null, "abc", null
				},
				{
					"Invalid Input: #@$, Invalid value:#@$, " + DOW_MSG_CONVENTION, null, null, "#@$", null
				},
				{
					"Invalid Input: 2-,4, Invalid value:2-, value should not start or end with '-', " + DOW_MSG_CONVENTION, null, null, "2-,4", null
				},
				{
					"Invalid Input: -2, Invalid value:-2, value should not start or end with '-', " + DOW_MSG_CONVENTION, null, null, "-2", null
				},
				{
					"Invalid Input: ,, " + DOW_MSG_CONVENTION, null, null, ",", null
				},
				{
					"Invalid Input: , ,, " + DOW_MSG_CONVENTION, null, null, ", ,", null
				},
				//TimePeriod
				//Hour
				{
					"Invalid Input: 24, Invalid Hour: 24 in Timeperiod: 24, " + HOUR_MSG_CONVENTION, null, null, null, "24"
				},
				{
					"Invalid Input: 11,24, Invalid Hour: 24 in Timeperiod: 24, " + HOUR_MSG_CONVENTION, null, null, null, "11,24"
				},
				{
					"Invalid Input: 11-24, Invalid Hour: 24 in Timeperiod: 24, " + HOUR_MSG_CONVENTION, null, null, null, "11-24"
				},
				{
					"Invalid Input: 1-5,11-24, Invalid Hour: 24 in Timeperiod: 24, " + HOUR_MSG_CONVENTION, null, null, null, "1-5,11-24"
				},
				{
					"Invalid Input: 1.1, Invalid Hour: 1.1 in Timeperiod: 1.1, " + HOUR_MSG_CONVENTION, null, null, null, "1.1"
				},
				{
					"Invalid Input: 2,1.1, Invalid Hour: 1.1 in Timeperiod: 1.1, " + HOUR_MSG_CONVENTION, null, null, null, "2,1.1"
				},
				{
					"Invalid Input: abc, Invalid Hour: abc in Timeperiod: abc, " + HOUR_MSG_CONVENTION, null, null, null, "abc"
				},
				{
					"Invalid Input: 2,abc, Invalid Hour: abc in Timeperiod: abc, " + HOUR_MSG_CONVENTION, null, null, null, "2,abc"
				},
				{
					"Invalid Input: @@@, Invalid Hour: @@@ in Timeperiod: @@@, " + HOUR_MSG_CONVENTION, null, null, null, "@@@"
				},
				{
					"Invalid Input: 1-5,@@@, Invalid Hour: @@@ in Timeperiod: @@@, " + HOUR_MSG_CONVENTION, null, null, null, "1-5,@@@"
				},
				
				//Minute
				{
					"Invalid Input: 11:61, Invalid Minutes: 61 in Timeperiod: 11:61, " + MINUTE_MSG_CONVENTION, null, null, null, "11:61"
				},
				{
					"Invalid Input: 11:30-11:65, Invalid Minutes: 65 in Timeperiod: 11:65, " + MINUTE_MSG_CONVENTION, null, null, null, "11:30-11:65"
				},
				{
					"Invalid Input: 1:30-1:40, 2:30-2:65, Invalid Minutes: 65 in Timeperiod: 2:65, " + MINUTE_MSG_CONVENTION, null, null, null, "1:30-1:40, 2:30-2:65"
				},
				{
					"Invalid Input: 11:1.1, Invalid Minutes: 1.1 in Timeperiod: 11:1.1, " + MINUTE_MSG_CONVENTION, null, null, null, "11:1.1"
				},
				{
					"Invalid Input: 11:abc, Invalid Minutes: abc in Timeperiod: 11:abc, " + MINUTE_MSG_CONVENTION, null, null, null, "11:abc"
				},
				{
					"Invalid Input: 11:@@@, Invalid Minutes: @@@ in Timeperiod: 11:@@@, " + MINUTE_MSG_CONVENTION, null, null, null, "11:@@@"
				},
				
				//Second
				{
					"Invalid Input: 11:20:61, Invalid Seconds: 61 in Timeperiod: 11:20:61, " + SECOND_MSG_CONVENTION, null, null, null, "11:20:61"
				},
				{
					"Invalid Input: 11:30:50-11:20:61, Invalid Seconds: 61 in Timeperiod: 11:20:61, " + SECOND_MSG_CONVENTION, null, null, null, "11:30:50-11:20:61"
				},
				{
					"Invalid Input: 11-20,11:30:50-11:20:61, Invalid Seconds: 61 in Timeperiod: 11:20:61, " + SECOND_MSG_CONVENTION, null, null, null, "11-20,11:30:50-11:20:61"
				},
				{
					"Invalid Input: 11:20:1.1, Invalid Seconds: 1.1 in Timeperiod: 11:20:1.1, " + SECOND_MSG_CONVENTION, null, null, null, "11:20:1.1"
				},
				{
					"Invalid Input: 11:20:abc, Invalid Seconds: abc in Timeperiod: 11:20:abc, " + SECOND_MSG_CONVENTION, null, null, null, "11:20:abc"
				},
				{
					"Invalid Input: 11:20:@@@, Invalid Seconds: @@@ in Timeperiod: 11:20:@@@, " + SECOND_MSG_CONVENTION, null, null, null, "11:20:@@@"
				},
				
				//Mixed
				{
					"Invalid Input: 13, Invalid value:13, " + MOY_MSG_CONVENTION, "13", "32", "8", "@@@:20:00"
				},
				{
					"Invalid Input: 32, Invalid value:32, " + DOM_MSG_CONVENTION, "11", "32", "8", "@@@:20:00"
				},
				{
					"Invalid Input: 8, Invalid value:8, " + DOW_MSG_CONVENTION, "11", "21", "8", "@@@:20:00"
				},
				{
					"Invalid Input: ,, " + MOY_MSG_CONVENTION, ",", "21", "8", "@@@:20:00"
				},
				{
					"Invalid Input: @@@:20:00, Invalid Hour: @@@ in Timeperiod: @@@:20:00, " + HOUR_MSG_CONVENTION, "11", "21", "5", "@@@:20:00"
				},
				{
					"Invalid Input: 11:20:00:23:00, Invalid Timeperiod(11:20:00:23:00), Timeperiod must be in hh[:mm[:ss]][-hh[:mm[:ss]]] format", "11", "21", "5", "11:20:00:23:00"
				},
				{
					"Invalid Input: ::-::, Invalid Timeperiod(::), Timeperiod must be in hh[:mm[:ss]][-hh[:mm[:ss]]] format", "11", "21", "5", "::-::"
				},
				{
					"Invalid Input: , ,, Timeperiod must be in hh[:mm[:ss]][-hh[:mm[:ss]]] format", "11", "21", "5", ", ,"
				},
				{
					"Invalid Input: ,,, Timeperiod must be in hh[:mm[:ss]][-hh[:mm[:ss]]] format", "11", "21", "5", ",,"
				},
				{
					"Invalid Input: --, Invalid TimePeriod Expression: --", "11", "21", "5", "--"
				},
				{
					"Invalid Input: -2-4, Invalid TimePeriod Expression: -2-4", "11", "21", "5", "-2-4"
				},
				{
					"Invalid Input: -,2-4, Invalid TimePeriod Expression: -", "", "", "", "-,2-4"
				},
				{
					"Invalid Input: 2-4-,6, Invalid TimePeriod Expression: 2-4-", "11", "21", "5", "2-4-,6"
				},
				{
					"Invalid Input: -2, Invalid TimePeriod Expression: -2", "", "", "", "-2"
				},
				{
					"Invalid Input: 2-4-6, Invalid TimePeriod Expression: 2-4-6", "", "", "", "2-4-6"
				},
				{
					"Invalid Input: 4:30, Invalid TimePeriod Expression: 4:30, specify only hour (e.g.1,4) or time period range (e.g.1-2, 1:30-5:00).", "", "", "", "4:30"
				},
				{
					"Invalid Input: 5:40:50, Invalid TimePeriod Expression: 5:40:50, specify only hour (e.g.1,4) or time period range (e.g.1-2, 1:30-5:00).", "", "", "", "5:40:50"
				},
				{
					"Invalid Input: 5:00:50, Invalid TimePeriod Expression: 5:00:50, specify only hour (e.g.1,4) or time period range (e.g.1-2, 1:30-5:00).", "", "", "", "5:00:50"
				},
				
		};
	}
	
	@Test
	@Parameters(method="data_Provider_For_Test_Invalid_MOY_DOM_DOW_TimePeriod_Should_throw_InvalidDayException")
	public void test_Invalid_MOY_DOM_DOW_TimePeriod_Should_throw_InvalidDayException(String expectedMessage, 
			String moyExpression, String domExpression, String dowExpression, String timePeriodExpression) throws Exception{
		
		exception.expect(InvalidTimeSlotException.class);
		exception.expectMessage(expectedMessage);
		TimeSlot.getTimeSlot(moyExpression, domExpression, dowExpression, timePeriodExpression);
	}

}
