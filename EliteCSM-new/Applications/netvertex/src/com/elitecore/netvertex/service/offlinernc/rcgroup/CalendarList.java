package com.elitecore.netvertex.service.offlinernc.rcgroup;

import java.sql.Timestamp;
import java.util.List;


public class CalendarList {
	
	private String calendarListName;
    private List<Calendar> calendars;
   
    public CalendarList(String calendarListName, List<Calendar> calendars) {
    	this.calendarListName = calendarListName;
    	this.calendars = calendars;
    }

	public String getCalendarListName() {
		return calendarListName;
	}

	public boolean contains(Timestamp time) {
		for (Calendar calendar : calendars) {
			if (calendar.contains(time)) {
				return true;
			}
		}
		return false;
	}
    
}
