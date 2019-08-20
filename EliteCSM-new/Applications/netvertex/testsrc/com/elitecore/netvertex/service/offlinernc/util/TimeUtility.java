package com.elitecore.netvertex.service.offlinernc.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class TimeUtility {

	private TimeUtility() {
		// no-op
	}
	
	public static String dayBefore(String startDate, DateFormat formatter) throws ParseException {
		return daysBefore(startDate, formatter, 1);
	}
	
	public static String daysBefore(String startDate, DateFormat formatter, int days) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(formatter.parse(startDate));
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - days);
		return formatter.format(cal.getTime());
	}

	public static String dayAfter(String startDate, DateFormat formatter) throws ParseException {
		return daysAfter(startDate, formatter, 1);
	}
	
	public static String daysAfter(String startDate, DateFormat formatter, int days) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(formatter.parse(startDate));
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + days);
		return formatter.format(cal.getTime());
	}
	
	public static Timestamp dayBefore(Timestamp date) {
		return daysBefore(date, 1);
	}
	
	public static Timestamp daysBefore(Timestamp date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(date.getTime()));
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - days);
		return new Timestamp(cal.getTime().getTime());
	}
	
	public static Timestamp dayAfter(Timestamp date) {
		return daysAfter(date, 1);
	}
	
	public static Timestamp daysAfter(Timestamp date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(date.getTime()));
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + days);
		return new Timestamp(cal.getTime().getTime());
	}
}
