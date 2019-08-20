package com.elitecore.aaa.core.policies.accesspolicy.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

public class AccessPolicyTime implements Comparable<AccessPolicyTime>,Serializable {
	
	private static final long serialVersionUID = 1L;
	private int startDay;
	private int endDay;
	private int startHour;
	private int stopHour;
	private int startMinute;
	private int stopMinute;
	private int startSecond;
	private int stopSecond;
	
	public AccessPolicyTime(int startDay,int startHour,int startMinute,int endDay,int stopHour,int stopMinute){
		this(startDay,startHour,startMinute,0,endDay,stopHour,stopMinute,0);
	}
	public AccessPolicyTime(int startDay,int startHour,int startMinute,int startSecond,
			int endDay,int stopHour,int stopMinute,int stopSecond){
		this.startDay = startDay;
		this.startHour = startHour;
		this.startMinute = startMinute;
		this.startSecond = startSecond;
		this.endDay = endDay;
		this.stopHour = stopHour;
		this.stopMinute = stopMinute;
		this.stopSecond = stopSecond;
	}
	public int getStartSecond() {
		return startSecond;
	}

	public int getStopSecond() {
		return stopSecond;
	}

	public int getStartDay() {
		return startDay;
	}

	public int getEndDay() {
		return endDay;
	}

	public int getStartHour() {
		return startHour;
	}

	public int getStopHour() {
		return stopHour;
	}

	public int getStartMinute() {
		return startMinute;
	}

	public int getStopMinute() {
		return stopMinute;
	}

	/**
	 * Compares this object with the specified object for order. 
	 * Returns a -1, 1, or a 0 as this object is less than, greater or conflict 
	 * if two time intersects each other between two times.
	 */
	public int compareTo(AccessPolicyTime o) {
		int startTimeLocal = (startDay*24*60*60) + (startHour*60*60) + (startMinute*60) + startSecond;
		int startTimeArg = (o.startDay*24*60*60)+(o.startHour*60*60)+(o.startMinute*60) + o.startSecond;
		int endTimeLocal = (endDay*24*60*60) + (stopHour*60*60) + (stopMinute*60)+stopSecond;
		int endTimeArg = (o.endDay*24*60*60)+(o.stopHour*60*60)+(o.stopMinute*60)+o.stopSecond;
		
		if(startTimeArg<startTimeLocal && endTimeArg<startTimeLocal)
			return -1;
		else if(startTimeArg>endTimeLocal && endTimeArg>endTimeLocal)
			return 1;
		return 0;
	}
	
	public void mergTime(AccessPolicyTime o){
		int startTimeLocal = (startDay*24*60*60) + (startHour*60*60) + (startMinute*60) + startSecond;
		int startTimeArg = (o.startDay*24*60*60)+(o.startHour*60*60)+(o.startMinute*60) + o.startSecond;
		int endTimeLocal = (endDay*24*60*60) + (stopHour*60*60) + (stopMinute*60)+stopSecond;
		int endTimeArg = (o.endDay*24*60*60)+(o.stopHour*60*60)+(o.stopMinute*60)+o.stopSecond;
		
		if(startTimeLocal > startTimeArg){
			this.startDay = o.startDay;
			this.startHour = o.startHour;
			this.startMinute = o.startMinute;
			this.startSecond = o.startSecond;
		}
		
		if(endTimeLocal < endTimeArg){
			this.endDay = o.endDay;
			this.stopHour = o.stopHour;
			this.stopMinute = o.stopMinute;
			this.stopSecond = o.stopSecond;
		}
	}
	
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("Start Day is " + startDay + " Start time is " + startHour + ":" +startMinute+":" +startSecond);
		out.println("End Day is " + endDay + " End time is " + stopHour + ":" +stopMinute+":" +stopSecond);
		return stringBuffer.toString();
	}
}
