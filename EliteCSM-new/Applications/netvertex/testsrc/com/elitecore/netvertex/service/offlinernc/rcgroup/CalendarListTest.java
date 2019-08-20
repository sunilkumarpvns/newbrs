package com.elitecore.netvertex.service.offlinernc.rcgroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.pd.calender.CalenderData;
import com.elitecore.corenetvertex.pd.calender.CalenderDetails;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.service.offlinernc.DummyOfflineRnCServiceContext;
import com.elitecore.netvertex.service.offlinernc.OfflineRnCServiceContext;
import com.elitecore.netvertex.service.offlinernc.ratecard.RateCardFactory;

public class CalendarListTest {
	
	private static final String START_DATE_ONE = "05-01-2018 00:00:00";
	private static final String END_DATE_ONE = "02-02-2018 00:00:00";
	
	private static final String END_DATE_TWO = "02-02-2020 00:00:00";
	private static final String START_DATE_TWO = "01-01-2020 00:00:00";
	
	private static final String TIME_PRESENT_IN_FIRST_RANGE_ONLY = "06-01-2018 00:00:00";
	
	private static final String TIME_BEFORE_START_DATE = "04-01-2018 00:00:00";
	private static final String TIME_AFTER_END_DATE = "03-02-2018 00:00:00";
	
	private static final String CALENDAR_LIST_NAME = "CalendarList1";
	private static final String CAL1 = "Cal1";
	private static final String CAL2 = "Cal2";
	private static final String TIME_FORMAT = "dd-MM-yyyy hh:mm:ss";
	
	private OfflineRnCServiceContext serviceContext = new DummyOfflineRnCServiceContext();
	private DateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
	private CalendarList calendarList;
	private RateCardGroupFactory rateCardGroupFactory;
	private CalenderData calenderData;
	
	@Before
	public void setUp() throws ParseException, InitializationFailedException {
		
		DummyNetvertexServerConfiguration serverConfiguration = new DummyNetvertexServerConfiguration();
		SystemParameterConfiguration spySystemParam = serverConfiguration.spySystemParameterConf();
		Mockito.when(spySystemParam.getEdrDateTimeStampFormat()).thenReturn(TIME_FORMAT);

		rateCardGroupFactory = new RateCardGroupFactory(new RateCardFactory(serviceContext, spySystemParam),spySystemParam);
		
		calenderData = new CalenderData();
		calenderData.setCalenderList(CALENDAR_LIST_NAME);
		
		addCalendar(CAL1, START_DATE_ONE, END_DATE_ONE);
		addCalendar(CAL2, START_DATE_TWO, END_DATE_TWO);
		
		calendarList = rateCardGroupFactory.createCalendarList(calenderData);
	}

	@Test
	public void getCalendarListNameReturnsNameOfCalendarList() {
		assertEquals(CALENDAR_LIST_NAME, calendarList.getCalendarListName());
	}
	
	@Test
	public void dateBelongsInCalendarListIfItIsInBetweenOfStartDateAndEndDateOfAnyCalendar() throws ParseException {
		assertTrue(calendarList.contains(timestampOf(TIME_PRESENT_IN_FIRST_RANGE_ONLY)));
	}
	
	@Test
	public void dateBelongsInCalendarListIfItIsEqualToStartDateOfAnyCalendar() throws ParseException {
		assertTrue(calendarList.contains(timestampOf(START_DATE_ONE)));
		assertTrue(calendarList.contains(timestampOf(START_DATE_TWO)));
	}

	@Test
	public void dateBelongsInCalendarListIfItIsEqualToEndDateOfAnyCalendar() throws ParseException {
		assertTrue(calendarList.contains(timestampOf(END_DATE_ONE)));
	}
	
	@Test
	public void dateDoesNotBelongInCalendarListIfItIsThereBeforeStartDateOfCalendar() throws ParseException {
		assertFalse(calendarList.contains(timestampOf(TIME_BEFORE_START_DATE)));
	}
	
	@Test
	public void dateDoesNotBelongInCalendarListIfItIsThereAfterEndDateOfCalendar() throws ParseException {
		assertFalse(calendarList.contains(timestampOf(TIME_AFTER_END_DATE)));
	}
	
	private void addCalendar(String name, String start, String end) throws ParseException {
		CalenderDetails calenderDetails1 = new CalenderDetails();
		calenderDetails1.setCalenderName(name);
		calenderDetails1.setFromDate(new Timestamp(formatter.parse(start).getTime()));
		calenderDetails1.setToDate(new Timestamp(formatter.parse(end).getTime()));
		calenderData.getCalenderDetails().add(calenderDetails1);
	}
	
	private Timestamp timestampOf(String date) throws ParseException {
		return new Timestamp(formatter.parse(date).getTime());
	}
}