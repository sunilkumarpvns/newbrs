package com.elitecore.core.serverx.policies.data;

import java.io.Serializable;

public class TimeSlotData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8296058308208559952L;
	
	private String monthOfYear;
	private String dayOfMonth;
	private String dayOfWeek;
	private String timePeriod;
	
	public TimeSlotData(String monthOfYear, String dayOfMonth, String dayOfWeek, String timePeriod){
		this.monthOfYear = monthOfYear;
		this.dayOfMonth = dayOfMonth;
		this.dayOfWeek = dayOfWeek;
		this.timePeriod = timePeriod;
	}
	
	/**
	 * 
	 * @return returns the month of year
	 */
	public String getMOY(){
		return this.monthOfYear;
	}
	
	/**
	 * 
	 * @return returns the day of month
	 */
	public String getDOM(){
		return this.dayOfMonth;
	}
	
	/**
	 * 
	 * @return returns the day of week
	 */
	public String getDOW(){
		return this.dayOfWeek;
	}
	
	/**
	 * 
	 * @return returns the time period bounds
	 */
	public String getTimePeriod(){
		return this.timePeriod;
	}
}
