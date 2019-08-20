package com.elitecore.netvertex.util;

import java.util.Calendar;

public class CalenderUtil {

	
	public static Calendar getPreviousDayMidNight(Calendar baseCalender) {
	
		Calendar previousDay = Calendar.getInstance();
		previousDay.setTimeInMillis(baseCalender.getTimeInMillis());
		previousDay.add(Calendar.DAY_OF_MONTH, -1);
		previousDay.set(Calendar.HOUR, 23);
		previousDay.set(Calendar.MINUTE, 59);
		previousDay.set(Calendar.SECOND, 59);
		previousDay.set(Calendar.MILLISECOND, 0);
		
		return previousDay;
	}
	
	public static Calendar getTodayMidNight(Calendar baseCalender) {
		
		Calendar midNight = Calendar.getInstance();
		midNight.setTimeInMillis(baseCalender.getTimeInMillis());
		midNight.set(Calendar.HOUR, 23);
		midNight.set(Calendar.MINUTE, 59);
		midNight.set(Calendar.SECOND, 59);
		midNight.set(Calendar.MILLISECOND, 0);
		
		return midNight;
	}

	public static Calendar getNextWeeklyResetTime(Calendar baseCalender) {
		
		Calendar weekResetTime = Calendar.getInstance();
		weekResetTime.setTimeInMillis(baseCalender.getTimeInMillis());
		weekResetTime.add(Calendar.DAY_OF_MONTH, 7-weekResetTime.get(Calendar.DAY_OF_WEEK));
		weekResetTime.set(Calendar.HOUR, 23);
		weekResetTime.set(Calendar.MINUTE, 59);
		weekResetTime.set(Calendar.SECOND, 59);
		weekResetTime.set(Calendar.MILLISECOND, 0);
		
		return weekResetTime;
	}

	public static Calendar getNextHourTime(Calendar baseCalender) {
		
		Calendar nextHourTime = Calendar.getInstance();
		nextHourTime.setTimeInMillis(baseCalender.getTimeInMillis());
		nextHourTime.add(Calendar.HOUR, 1);
		
		return nextHourTime;
	}
	
	public static Calendar getPreviousHourTime(Calendar baseCalender) {
		
		Calendar nextHourTime = Calendar.getInstance();
		nextHourTime.setTimeInMillis(baseCalender.getTimeInMillis());
		nextHourTime.add(Calendar.HOUR, -1);
		
		return nextHourTime;
	}

	public static Calendar getPreviousDay(Calendar baseCalender) {
		Calendar previousDay = Calendar.getInstance();
		previousDay.setTimeInMillis(baseCalender.getTimeInMillis());
		previousDay.add(Calendar.DAY_OF_MONTH, -1);
		
		return previousDay;
	}

	public static Calendar getPreviousWeeklyResetTime(Calendar baseCalender) {
		Calendar weekResetTime = Calendar.getInstance();
		weekResetTime.setTimeInMillis(baseCalender.getTimeInMillis());
		weekResetTime.add(Calendar.DAY_OF_MONTH, -weekResetTime.get(Calendar.DAY_OF_WEEK));
		weekResetTime.set(Calendar.HOUR, 23);
		weekResetTime.set(Calendar.MINUTE, 59);
		weekResetTime.set(Calendar.SECOND, 59);
		weekResetTime.set(Calendar.MILLISECOND, 0);
		
		return weekResetTime;
	}

	public static Calendar getPreviousWeekTime(Calendar baseCalender) {
		
		Calendar weekResetTime = Calendar.getInstance();
		weekResetTime.setTimeInMillis(baseCalender.getTimeInMillis());
		weekResetTime.add(Calendar.DAY_OF_MONTH, -weekResetTime.get(Calendar.DAY_OF_WEEK));
		
		return weekResetTime;
	}

	public static Calendar getBillingCycleResetTime(Calendar baseCalender) {
		Calendar billingCycleResetTime = Calendar.getInstance();
		billingCycleResetTime.setTimeInMillis(baseCalender.getTimeInMillis());
		billingCycleResetTime.add(Calendar.DAY_OF_MONTH, 28-billingCycleResetTime.get(Calendar.DAY_OF_MONTH));
		return billingCycleResetTime;
	}
}
