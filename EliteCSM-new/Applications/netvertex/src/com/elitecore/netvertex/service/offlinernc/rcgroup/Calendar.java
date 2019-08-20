package com.elitecore.netvertex.service.offlinernc.rcgroup;

import java.sql.Timestamp;

import com.elitecore.commons.base.TimeRange;


public class Calendar {
	
	private String calendarName;
	private TimeRange timeRange;

	public Calendar(String calendarName, TimeRange timeRange) {
		this.calendarName = calendarName;
		this.timeRange = timeRange;		
	}

	public String getCalendarName() {
		return calendarName;
	}

	public TimeRange getTimeRange() {
		return timeRange;
	}

	public boolean contains(Timestamp time) {
		return timeRange.contains(time);
	}
	
}
