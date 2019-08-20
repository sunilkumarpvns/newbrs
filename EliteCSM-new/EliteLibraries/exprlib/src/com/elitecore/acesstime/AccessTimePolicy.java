
/**
 *
 *
 */

package com.elitecore.acesstime;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * @author milan paliwal
 * @author harsh
 * 
 * <p>{@link AccessTimePolicy} store {@link List} of {@link TimeSlot}</p>
 * 
 * <p>{@link AccessTimePolicy} provide the duration in Second and Millisecond by calling {@link TimeSlot} one by one</p>
 * 
 *
 */
public class AccessTimePolicy implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public static final int FAILURE = -1;
	public static final int NO_TIME_OUT = 0;
	private List<TimeSlot> timeSlots;
	
	public static final int SECOND = 1;
	public static final int MILLISECOND = 0;

	public AccessTimePolicy() {
		timeSlots = new ArrayList<TimeSlot>(); 
	}
	
	public List<TimeSlot> getListTimeSlot() {
		return timeSlots;
	}

	public void setListTimeSlot(List<TimeSlot> timeSlots) {
		if(timeSlots != null)
			this.timeSlots = timeSlots;
	}
	
	public void addTimeSlot(TimeSlot timeSlot){
		if(timeSlot != null)
			timeSlots.add(timeSlot);
	}

	/**
	 * Provide the time in terms of Duration from currentTime	 * 
	 * <p><li>NO_TIME_SLOT is return when no Time is there or when Time is there only for
	 * MOY,DOM,DOW no time is specified</li>
	 * <li>If there is time base policy with TIME e.g.10-12 then sessionTimeout must be "FAILUER" or greater than "0". 
	 * NO_TIME_SLOT will be return in that case </li></p> 
	 * @return duration or UTC millisecond on the basis of argument
	 */
	public long applyPolicy(){		
		return applyPolicy(SECOND);
	}
	
	/**
	 * Provide the time in terms of Duration from currentTime or UTC milliseconds from the epoch
	 * @param timeUnit specify what time should be return
	 * 		  <p> if SECOND(1)- provide Duration from currentTime</p>
	 * 		  <p> if MILLISECOND(0)- provide UTC milliseconds from the epoch</p>
	 *	<p> <pre><li>NO_TIME_SLOT is return when no Time is there or when Time is there only for
	 * MOY,DOM,DOW no time is specified</li></pre>
	 * 			<pre><li>If there is time base policy with TIME e.g.10-12 then sessionTimeout must be "FAILUER" or greater than "0". 
	 * NO_TIME_SLOT will be return in that case </li></pre></p> 
	 * @return duration or UTC millisecond on the basis of argument
	 */
	public long applyPolicy(int timeUnit){
		if(timeSlots.isEmpty()){
			return NO_TIME_OUT;
		}
		
		if(timeUnit == SECOND){
			return getTimeDuration();
		}else if(timeUnit == MILLISECOND){
			return getTotalTime();
		}else{
			return FAILURE;
		}
	}
	
	/**
	 * 
	 *  @return duration in Second from current time
	 * 		<p><li>if CurrentDate is satisfied with {@linkplain TimePeriod} of {@linkplain TimeSlot}</li></p>
	 * 		<p><li>{@linkplain AccessTimePolicy.NO_TIME_SLOT} if no {@link TimePeriod} is there only check for MOY, DOM,DOW</li></p>
	 * 		<p><li>{@linkplain AccessTimePolicy.FAILURE} if no {@linkplain TimePeriod} satisfied with currentDate</li></p>
	 */
	private long getTimeDuration(){
		Calendar currentDate=getCalender();
		for(TimeSlot timeSlot:timeSlots){		
			long sessionTimeOut=timeSlot.getDuration(currentDate);	
			if(sessionTimeOut >= 0){
				return sessionTimeOut;
		}
		}
		return FAILURE;	
	}
	
	/**
	 * Provide time as UTC milliseconds from the epoch.
	 * @return  <p>duration in MilliSecond if CurrentDate is satisfied with {@linkplain TimePeriod} of {@linkplain TimeSlot}</p> 
	 * 			<p>{@linkplain AccessTimePolicy.NO_TIME_SLOT} if no {@link TimePeriod} is there only check for MOY, DOM,DOW</p>
	 * 			<p>{@linkplain AccessTimePolicy.FAILURE} if no {@linkplain TimePeriod} satisfied with currentDate</p>
	 */
	private long getTotalTime(){
		Calendar currentDate=getCalender();
		for(TimeSlot timeSlot:timeSlots){		
			long sessionTimeOut=timeSlot.getTimeInMillis(currentDate);	
			if(sessionTimeOut >= 0){
				return sessionTimeOut;
		}
		}
		return FAILURE;	
	}

	
	protected Calendar getCalender() {
		
		return Calendar.getInstance();
	}

	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);		
		Iterator<TimeSlot> timeSlotIterator=this.getListTimeSlot().iterator();
		int i=1;
		while(timeSlotIterator.hasNext()){
			TimeSlot timeSlot=timeSlotIterator.next();
			out.println("");
			out.println("          TimeSlot"+ i++);
			out.print(timeSlot);
		}
		out.close();
		return stringBuffer.toString();
	}

}
