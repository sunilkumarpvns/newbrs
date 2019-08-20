package com.elitecore.acesstime;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Objects;

/**
 * 
 * @author milan 
 * @author harsh
 *
 * <p>TimePeriod Specify the time duration in terms of StartHour, StartMinute, StartSecond and EndHour, EndMinute, EndSecond</p>
 *  
 *
 */
public class TimePeriod implements Comparable<TimePeriod>, Serializable{

	private static final long serialVersionUID = 1L;
	public static final int LAST_HOUR_OF_DAY = 23;
	public static final int LAST_MINUTE_OF_HOUR=59;
	public static final int LAST_SECOND_OF_MINUTE=59;
	
	public static final int FIRST_HOUR_OF_DAY=0;
	public static final int FIRST_MINUTE_OF_HOUR=0;
	public static final int FIRST_SECOND_OF_MINUTE=0;
	
	
	private int startHour;
	private int startMinute;
	private int startSecond;
	
	private int stopHour;
	private int stopMinute;
	private int stopSecond;
	
	/*
	 * provide total startTime in second from day start 00:00:00 am to startTime Provide 
	 */
	private int totalStartTimeInSec = 0;

	/*
	 * stop time in second from day start 00:00:00 am to stopTime provided
	 */
	private int totalStopTimeInSec = 0;
	
	public TimePeriod(int startHour,int startMinute,int startSecond,int stopHour,int stopMinute,int stopSecond){
		this.startHour=startHour;
		this.startMinute=startMinute;
		this.startSecond=startSecond;
		
		totalStartTimeInSec = (startHour * 60 * 60) + (startMinute * 60) + startSecond;
		
		
		this.stopHour=stopHour;
		this.stopMinute=stopMinute;
		this.stopSecond=stopSecond;
		
		totalStopTimeInSec = (stopHour * 60 * 60) +  (stopMinute * 60) + stopSecond;
	}
	
	public int getStartHour() {
		return startHour;
	}
	public int getStartMinute() {
		return startMinute;
	}
	public int getStartSecond() {
		return startSecond;
	}
	public int getStopHour() {
		return stopHour;
	}
	public int getStopMinute() {
		return stopMinute;
	}
	public int getStopSecond() {
		return stopSecond;
	}
	
	public int getStartTimeInSec() {
		return totalStartTimeInSec;
	}

	public int getStopTimeInSec() {
		return totalStopTimeInSec;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if ((obj instanceof TimePeriod) == false) {
			return false;
		}

		TimePeriod other = (TimePeriod) obj;
		return totalStartTimeInSec == other.totalStartTimeInSec &&
				totalStopTimeInSec == other.totalStopTimeInSec;

	}

	@Override
	public int hashCode() {
		return Objects.hash(totalStartTimeInSec, totalStopTimeInSec);
	}

	public String toString(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("                      "+startHour+":"+startMinute+":"+startSecond+" - "+stopHour+":"+stopMinute+":"+stopSecond);
		out.close();
		return stringBuffer.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * Compare timePeriod in following way :
	 * 		1) If the Timeperiod in argument has startTime and stopTime greater than stopTime then sent 1
	 * 				----> ex.  10-12.compareTo(13-14)
	 *  
	 * 		2) If the Timeperiod in argument has startTime and stopTime less than startTime then sent -1
	 * 				----> ex.  10-12.compareTo(8-9)
	 * 
	 * 		3) else consider that time as in between and send 0
	 * 				----> ex.  10-12.compareTo(9-11) 
	 *  
	 * 	for Example CurrentTime Period is 10-12
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TimePeriod timePeriod) {
		
		if(timePeriod.totalStartTimeInSec > this.totalStopTimeInSec && timePeriod.totalStopTimeInSec > this.totalStopTimeInSec){
			return 1;
		}
			
			
		if(timePeriod.totalStartTimeInSec < this.totalStartTimeInSec && timePeriod.totalStopTimeInSec < this.totalStartTimeInSec){
			return -1;
		}
		return 0;
	}
		
	/**
	 * Merge time e.g. StartTime and StopTime with Time of {@link TimePeriod}.
	 * <p>In merging process, Current TimePeriod might set new startTime and stopTime on the basis of following :</p>
	 *		<p><li>Set StartTime of {@link TimePeriod} which has smaller either of two(current TimePeriod or TimePeriod in Argument)</li></p>
	 *		<p><li>Set StopTime of {@link TimePeriod} which has bigger either of two(current TimePeriod or TimePeriod in Argument)</li></p>
	 * @param timePeriod
	 */
		
	public void merge(TimePeriod timePeriod){
		if(this.totalStartTimeInSec > timePeriod.totalStartTimeInSec){
			this.startHour = timePeriod.startHour;
			this.startMinute = timePeriod.startMinute;
			this.startSecond = timePeriod.startSecond;
			
			totalStartTimeInSec = (this.startHour * 60 * 60) + (this.startMinute * 60) + this.startSecond;
		}
		
		if(this.totalStopTimeInSec < timePeriod.totalStopTimeInSec){
			this.stopHour = timePeriod.stopHour;
			this.stopMinute = timePeriod.stopMinute;
			this.stopSecond = timePeriod.stopSecond;
			
			totalStopTimeInSec = (this.stopHour * 60 * 60) +  (this.stopMinute * 60) + this.stopSecond;
		}
	}
	
}