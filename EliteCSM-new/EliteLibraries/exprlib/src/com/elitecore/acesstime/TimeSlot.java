package com.elitecore.acesstime;

import com.elitecore.acesstime.exception.InvalidTimeSlotException;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author milan paliwal
 * @author harsh
 * 
 * <p>TimeSlot store the {@link List} of {@linkplain TimePeriod} in the sorted order</p>
 * 
 */
public class TimeSlot implements Serializable{


	private static final long serialVersionUID = 1L;
	public static final int ONE_DAY_MS = 86400000;	//	One day milliseconds
	private static final int MONTH_OF_YEAR_LIMIT = 12;
	private static final int DAY_OF_MONTH_LIMIT = 31;
	private static final int DAY_OF_WEEK_LIMIT = 7;

    public static final int SUNDAY = 7;
	
	/**
	 * @ monthsOfYear will store months in bitwise..  Example Months of Year = 1 , 3 then bit will be 00101
	 * @ daysOfMonth will store date in bitwise..  Example Day of Month = 3 , 4 , 7 then bit will be 1001100
	 * @ daysOfMonth will store date in bitwise..  Example Day of Week = 4 then bit will be 001000
	 */
	private long monthsOfYear;
	private long daysOfMonth;
	private long daysOfWeek;
	private ArrayList<TimePeriod> timePeriods;
	
	//suppressing the use of default constructor
	private TimeSlot(){
		timePeriods = new ArrayList<TimePeriod>();
	}
	
	public static TimeSlot getTimeSlot(String monthOfYearExpression, String dayOfMonthExpression, String dayOfWeekExpression, String timeExpression) throws InvalidTimeSlotException{
		TimeSlot ts = new TimeSlot();
	
		if(monthOfYearExpression != null && monthOfYearExpression.trim().length() > 0){
			ts.setMonthOfYear(monthOfYearExpression);
		}
		if(dayOfMonthExpression != null && dayOfMonthExpression.trim().length() > 0){
			ts.setDayOfMonth(dayOfMonthExpression);
		}
		if(dayOfWeekExpression != null && dayOfWeekExpression.trim().length() > 0){
			ts.setDayOfWeek(dayOfWeekExpression);
		}
		if(timeExpression != null && timeExpression.trim().length() > 0){
			ts.setTime(timeExpression);
		}
		
		return ts;
	}
	
	private void setMonthOfYear(String monthOfYearExpression)throws InvalidTimeSlotException{


        long[] months;
        long monthsOfYear = 0;
        String[] monthTokens = monthOfYearExpression.split(",");
			
			if(monthTokens.length == 0){
				throw new InvalidTimeSlotException("Invalid Input: "+monthOfYearExpression+", Month of Year must be between 1 to "+MONTH_OF_YEAR_LIMIT);
			}

		
			for(String token : monthTokens){
					if(token.trim().length() == 0 ){
						throw new InvalidTimeSlotException("Invalid Input: "+monthOfYearExpression+", Month of Year must be between 1 to "+MONTH_OF_YEAR_LIMIT);
					}
					
					try{
						months = parseDate(token.trim(), MONTH_OF_YEAR_LIMIT);
					}catch (InvalidTimeSlotException e) {
						throw new InvalidTimeSlotException("Invalid Input: "+monthOfYearExpression+", "+ e.getMessage() +", Month of Year must be between 1 to "+MONTH_OF_YEAR_LIMIT,e);
					}
					
					
					for(long month : months){
						monthsOfYear = monthsOfYear | (1 << month) ;
					}
				
			}
			
			this.monthsOfYear = monthsOfYear ;
		
	}
	
	private void setDayOfMonth(String dayOfMonthExpression)throws InvalidTimeSlotException{

        long[] dates;
        long daysOfMonth = 0;
        String[] dateTokens = dayOfMonthExpression.split(",");
			
			if(dateTokens.length == 0){
				throw new InvalidTimeSlotException("Invalid Input: "+dayOfMonthExpression+", Day of Month must be between 1 to "+DAY_OF_MONTH_LIMIT);
			}
			
			for(String token : dateTokens){
					if(token.trim().length() == 0 ){
						throw new InvalidTimeSlotException("Invalid Input: "+dayOfMonthExpression+", Day of Month must be between 1 to "+DAY_OF_MONTH_LIMIT);
					}
					
					try{
						dates = parseDate(token.trim(), DAY_OF_MONTH_LIMIT);
					} catch (InvalidTimeSlotException e) {
						throw new InvalidTimeSlotException("Invalid Input: "+dayOfMonthExpression+", "+ e.getMessage() +", Day of Month must be between 1 to "+DAY_OF_MONTH_LIMIT,e);
					}
					
					for(long date : dates){
						daysOfMonth = daysOfMonth | (1 << date) ;
					}
				}
			
			this.daysOfMonth = daysOfMonth ;
	}
	
	private void setDayOfWeek(String dayOfWeekExpression)throws InvalidTimeSlotException{

        long[] days;
        long daysOfWeek = 0;
        String[] dayTokens = dayOfWeekExpression.split(",");
			
			if(dayTokens.length == 0){
				throw new InvalidTimeSlotException("Invalid Input: "+dayOfWeekExpression+", Day of Week must be between 1 to "+DAY_OF_WEEK_LIMIT);
			}

			for(String token : dayTokens){
			
				if(token.trim().length() == 0 ){
					throw new InvalidTimeSlotException("Invalid Input: "+dayOfWeekExpression+", Day of Week must be between 1 to "+DAY_OF_WEEK_LIMIT);
				}
				
				try{
					days = parseDate(token.trim(), DAY_OF_WEEK_LIMIT);
				}catch (InvalidTimeSlotException e) {
					throw new InvalidTimeSlotException("Invalid Input: "+dayOfWeekExpression+", "+ e.getMessage() +", Day of Week must be between 1 to "+DAY_OF_WEEK_LIMIT,e);
				}
				
				for(long day : days){
					daysOfWeek = daysOfWeek | (1 << day) ;
				}
			}
		
			this.daysOfWeek = daysOfWeek ;
		
	}
	
	private void setTime(String timeExpression)throws InvalidTimeSlotException{
		
			
			ArrayList<TimePeriod> timePeriods = new ArrayList<TimePeriod>();
        String[] timeTokens = timeExpression.split(",");
			TimePeriod[] times;
			
			if(timeTokens.length == 0){
																											
				throw new InvalidTimeSlotException("Invalid Input: " +timeExpression + ", Timeperiod must be in hh[:mm[:ss]][-hh[:mm[:ss]]] format");
			}
			
			for(String token : timeTokens){
				
				if(token.trim().length() == 0){
					throw new InvalidTimeSlotException("Invalid Input: " +timeExpression + ", Timeperiod must be in hh[:mm[:ss]][-hh[:mm[:ss]]] format");
				}
		
				try{
					times = parseTime(token.trim());
				}catch(InvalidTimeSlotException e){
					throw new InvalidTimeSlotException("Invalid Input: " +timeExpression+", "+e.getMessage()+", Timeperiod must be in hh[:mm[:ss]][-hh[:mm[:ss]]] format", e);
				}
				
				
				for(TimePeriod timePeriod : times){
					addTimeSlot(timePeriods, timePeriod);
				}
			}
			
			this.timePeriods = timePeriods ;
		
	}
	
	private long[] parseDate(String dateToken, int limit)  throws InvalidTimeSlotException{
		
		/* 
		 * dateToken.startsWith("-") OR dateToken.endsWith("-"), this check is for avoiding situations below
		 * e.g. -1,-12,12-,1-
		 */
		if(dateToken.startsWith("-") || dateToken.endsWith("-")){
			throw new InvalidTimeSlotException("Invalid value:"+dateToken+", value should not start or end with '-'");
		}
        String[] dateString = dateToken.split("-");
        long[] dates;
		try{
			if( dateString.length == 1 ){
				dates = new long[1];
				dates[0] = Long.parseLong(dateString[0]);
				if(dates[0] > limit){
					throw new InvalidTimeSlotException("Invalid value:"+dates[0]);
				}
			}else if( dateString.length == 2 ){
				
				long startDate = Long.parseLong(dateString[0]);
				long endDate = Long.parseLong(dateString[1]);
				
				if(endDate > limit){
					throw new InvalidTimeSlotException("Invalid value:"+endDate);
				}
				
				if(startDate >= endDate){
					throw new InvalidTimeSlotException("Invalid value:"+ dateToken + ", start value must be lesser than end value");
				}
				
				int index = 0 ;
				int size = (int) (endDate - startDate + 1) ;
				dates = new long[size];
				for( long i = startDate ; i <= endDate ; i++ ){
					
					dates[index] = i ;
					index++ ;
				}
			}else{
				throw new InvalidTimeSlotException("Invalid value:"+dateToken );
			}
			
		}catch(NumberFormatException e){
			throw new InvalidTimeSlotException("Invalid value:"+dateToken,e);
		}
		
		return dates;
	}
	
	private TimePeriod[] parseTime (String timeToken)throws InvalidTimeSlotException{
	
		 
		/* timeToken.startsWith("-") OR timeToken.endsWith("-"), this check is for avoiding situations below		 
		 * e.g. -1,-12,12-,1-,-12:00,12:00-,-2-4-
		 */
		
		if(timeToken.startsWith("-") || timeToken.endsWith("-")){
			throw new InvalidTimeSlotException("Invalid TimePeriod Expression: " +timeToken);
		}
        String[] timeString = timeToken.split("-");
        TimePeriod[] timePeriods;
        int startHour;
        int startMinute;
        int startSecond;
        int stopHour;
        int stopMinute;
        int stopSecond;
        int[] startTime;
        int[] stopTime = null;
		boolean isEndTimeSet = true;

		/*
		 * timeString.length will be 1 if there is no stoptime provided,
		 * e.g. 11, then the timeperiod will be considered as 11:00:00 - 11:59:59
		 * 
		 * timeString.length will be 2 if both starttime and stoptime are provided,
		 * e.g. 6:00:00-11:30:00, then the timeperiod will be considered as 6:00:00 - 11:30:00
		 * 
		 * And if timeString.length is more then 2 then it is considered as invalid TimePeriod and InvalidDayException is thrown.
		 * e.g. 6:00:00-11:30:00-, 6:00:00-11:30:00-12:00:00 
		  */
		 
		if(timeString.length == 1){
			startTime = createHHMMSS(timeString[0]);
			isEndTimeSet = false ;
		}else if(timeString.length == 2){
			startTime = createHHMMSS(timeString[0]);
			stopTime = createHHMMSS(timeString[1]);
		}else{
			throw new InvalidTimeSlotException("Invalid TimePeriod Expression: " +timeToken);
		}
		
		startHour = startTime[0];
		startMinute = startTime[1];
		startSecond = startTime[2];
		if(isEndTimeSet){
			stopHour = stopTime[0];
			stopMinute = stopTime[1];
			stopSecond = stopTime[2];
		}else{
			
			/*
			 * In case of not giving range in timeperiod, valid entries for time period is to configuring only hour(s) e.g.1,2,10,12,14
			 * Here, if timeperiod input is 2, then allowed timeperiod is 2:00:00 - 2:59:59
			 * 
			 * Here configuring hh:mm:ss without range, e.g.4:30,5:30:50 will be considered invalid.
			 * */
			if(startMinute > 0 || startSecond > 0){
				throw new InvalidTimeSlotException("Invalid TimePeriod Expression: "+timeToken+", specify only hour (e.g.1,4) or time period range (e.g.1-2, 1:30-5:00).");
			}
				
			stopHour = startTime[0];
			stopMinute = TimePeriod.LAST_MINUTE_OF_HOUR;
			stopSecond = TimePeriod.LAST_SECOND_OF_MINUTE;
		}
		
		if (startHour == stopHour && startMinute == stopMinute && startSecond == stopSecond) {
			
			throw new InvalidTimeSlotException("Start time: " + startHour + ":" + startMinute + ":" + startSecond + " and end time: " 
			+ stopHour + ":" + stopMinute + ":" + stopSecond + " is same");
		}
		
		if(startHour > stopHour || ((startHour == stopHour && startMinute > stopMinute) || (startHour == stopHour && startMinute == stopMinute && startSecond > stopSecond))) {
			
			timePeriods = new TimePeriod[2];
			timePeriods[0] = new TimePeriod(startHour,startMinute,startSecond, TimePeriod.LAST_HOUR_OF_DAY, TimePeriod.LAST_MINUTE_OF_HOUR, TimePeriod.LAST_SECOND_OF_MINUTE) ;
			timePeriods[1] = new TimePeriod(TimePeriod.FIRST_HOUR_OF_DAY, TimePeriod.FIRST_MINUTE_OF_HOUR, TimePeriod.FIRST_SECOND_OF_MINUTE, stopHour, stopMinute, stopSecond) ; 
		} else {
			timePeriods = new TimePeriod[1];
			timePeriods[0] = new TimePeriod(startHour,startMinute,startSecond,stopHour,stopMinute,stopSecond);
		}
		
		return timePeriods;
	}
	
	/*
	 * Iterate sortedTimePeriod one by one and compare fetched timeperiod with timeperiod passed in argument.
	 * result will 0 if
	 * 				currTimeperiod(ex 10-12) and new TimePerid(11-13) is conflicted,
	 * 				then mergetime period with new timeperiod and remove currTimeperiod. [1-2,10-12,15-16] 
	 * 				then after remove [1-2,15-16] and timeperiod passed in argument will be 10-13.
	 * result will -1 if
	 * 					both start and stop time of timeperid(10-12) passed in argument is less then current timeperiod(13-14) 's start time.
	 * 					so we need to break an list is sorted.
	 * 
	 *	In last we add timperiod at index position.Index represent the position which is least greater element than timeperiod in argument in list.
	 *	ex [1-2,11-12,13-14] and new Timeperiod [9-10] then INDEX value is 1
	 */
	
	private void addTimeSlot(ArrayList<TimePeriod> sortedTimePeriods, TimePeriod timePeriod){
		int index = 0;
		while(index < sortedTimePeriods.size()){
			TimePeriod currTimePeriod = sortedTimePeriods.get(index);
			int result = currTimePeriod.compareTo(timePeriod);
			if(result == 0){
				timePeriod.merge(currTimePeriod);
				sortedTimePeriods.remove(currTimePeriod);
				continue;
            } else if (result < 0) {
				break;
			}
			
			index++;
		}
		sortedTimePeriods.add(index,timePeriod);
	}
	
	
	
	
	public List<Integer> getMonthOfYear() {
		return getList(monthsOfYear,MONTH_OF_YEAR_LIMIT);
	}
	
	public List<Integer> getDayOfMonth() {
		return getList(daysOfMonth,DAY_OF_MONTH_LIMIT);
	}
	
	public List<Integer> getDayOfWeek() {
		return getList(daysOfWeek,DAY_OF_WEEK_LIMIT);
	}

	public List<TimePeriod> getTimePeriods() {
		return timePeriods;
	}
	
	//The method will convert the long value to list Eg. x=5 then list = 1 , 4
	private List<Integer> getList(long value ,int limit ){
		List<Integer> returnList= new ArrayList<Integer>();
		if(value>0){
			for(int counter=0 ; counter <= limit ; counter++){
				if ((value & (1 << counter)) > 0)
					returnList.add(counter);
			}
		}
		return returnList;
	}
	
	
	
	public int[] createHHMMSS(String symbol) throws InvalidTimeSlotException{
		String[] strInHHMMSS=symbol.split(":");
        int hour = -1;
        int minute = -1;
        int second;
		
		try{
				
			if(strInHHMMSS.length==3){
				hour=Integer.parseInt(strInHHMMSS[0]);
				minute=Integer.parseInt(strInHHMMSS[1]);
				second=Integer.parseInt(strInHHMMSS[2]);
			}else if(strInHHMMSS.length==2){
				hour=Integer.parseInt(strInHHMMSS[0]);
				minute=Integer.parseInt(strInHHMMSS[1]);
				second = 0;
			}else if(strInHHMMSS.length==1){
				hour=Integer.parseInt(strInHHMMSS[0]);
				minute = 0;
				second = 0;
			}else{
				throw new InvalidTimeSlotException("Invalid Timeperiod("+symbol+")");
			}
			
			if(hour > TimePeriod.LAST_HOUR_OF_DAY){
				throw new InvalidTimeSlotException("Invalid Hour: " + hour + " in Timeperiod: "+ symbol + ", Hour must be between "+TimePeriod.FIRST_HOUR_OF_DAY+" to "+TimePeriod.LAST_HOUR_OF_DAY);
			}else if(minute > TimePeriod.LAST_MINUTE_OF_HOUR){
				throw new InvalidTimeSlotException("Invalid Minutes: " + minute + " in Timeperiod: "+ symbol + ", Minutes must be between "+TimePeriod.FIRST_MINUTE_OF_HOUR+" to "+TimePeriod.LAST_MINUTE_OF_HOUR);
			}else if(second > TimePeriod.LAST_SECOND_OF_MINUTE){
				throw new InvalidTimeSlotException("Invalid Seconds: " + second + " in Timeperiod: "+ symbol +", Seconds must be between "+TimePeriod.FIRST_SECOND_OF_MINUTE+" to "+TimePeriod.LAST_SECOND_OF_MINUTE);
			}
			
			}catch(NumberFormatException e){
			 /*
			  * to check which input is not valid
			  */
				if(hour == -1){
					throw new InvalidTimeSlotException("Invalid Hour: " + strInHHMMSS[0] + " in Timeperiod: "+ symbol + ", Hour must be between "+TimePeriod.FIRST_HOUR_OF_DAY+" to "+TimePeriod.LAST_HOUR_OF_DAY,e);
				}
				if(minute == -1){
					throw new InvalidTimeSlotException("Invalid Minutes: " + strInHHMMSS[1] + " in Timeperiod: "+ symbol + ", Minutes must be between "+TimePeriod.FIRST_MINUTE_OF_HOUR+" to "+TimePeriod.LAST_MINUTE_OF_HOUR,e);
				}
				throw new InvalidTimeSlotException("Invalid Seconds: " + strInHHMMSS[2] + " in Timeperiod: "+ symbol +", Seconds must be between "+TimePeriod.FIRST_SECOND_OF_MINUTE+" to "+TimePeriod.LAST_SECOND_OF_MINUTE,e);
			
			}
		
		return new int []{hour,minute,second};
	}
	
	/**
	 * Compare the currentDate with {@linkplain TimePeriod}s if any timePeriod is satisfied 
	 * then return the duration in second = (Satisfied{@linkplain TimePeriod}'s endTime - currentDate)
	 * @param currentDate specify the time which has to be compared with {@link TimePeriod} 
	 * @return duration in Second 
	 * 		<p><li>if CurrentDate is satisfied with {@linkplain TimePeriod} of {@linkplain TimeSlot}</li></p>
	 * 		<p><li>{@linkplain AccessTimePolicy.NO_TIME_SLOT} if no {@link TimePeriod} is there only check for MOY, DOM,DOW</li></p>
	 * 		<p><li>{@linkplain AccessTimePolicy.FAILURE} if no {@linkplain TimePeriod} satisfied with currentDate</li></p>
	 */
	public long getDuration(Calendar currentDate){
		
 		int month=currentDate.get(Calendar.MONTH)+1;
		int date=currentDate.get(Calendar.DATE);
		int weekDay=currentDate.get(Calendar.DAY_OF_WEEK)-1;
		if(weekDay==0)
			weekDay = SUNDAY ;
		
		if(monthsOfYear>0){
			if((monthsOfYear & (1 << month)) == 0)
				return AccessTimePolicy.FAILURE;
		}
		if(daysOfMonth>0){
			if((daysOfMonth & (1 << date)) == 0)
				return AccessTimePolicy.FAILURE;
		}
		if(daysOfWeek>0){
			if((daysOfWeek & (1 << weekDay)) == 0)
				return AccessTimePolicy.FAILURE;
		}
		
		if(timePeriods.isEmpty())
			return AccessTimePolicy.NO_TIME_OUT;
		
		TimePeriod currTimePeriod = new TimePeriod(currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), currentDate.get(Calendar.SECOND), currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), currentDate.get(Calendar.SECOND));
		
		for(TimePeriod  timePeriod : timePeriods){
			int result = timePeriod.compareTo(currTimePeriod);
			//ex : currentTime is 14:30 am and TimePeriod in sorted TimePeriods is 14:00-15:00
			if(result == 0){
				int diffInSec = timePeriod.getStopTimeInSec() - currTimePeriod.getStopTimeInSec();
				if(diffInSec > 0){
					return diffInSec;	
				}
			//ex : currentTime is 11:00 am and  TimePeriod in sorted TimePeriods is greater than 11:00am e.g 14:00-15:00 
            } else if (result < 0) {
				break;
            }
		}
		
		return AccessTimePolicy.FAILURE;
		
	}
	

	/**
	 * Compare the currentDate with {@linkplain TimePeriod}s if any timePeriod is satisfied 
	 * and return the duration in MilliScond = currentDate + Satisfied {@linkplain TimePeriod}'s endTime second
	 * @param currentDate specify the time which has to be compared with {@link TimePeriod}
	 * @return 	<p>duration in MilliSecond if CurrentDate is satisfied with {@linkplain TimePeriod} of {@linkplain TimeSlot}</p> 
	 * 			<p>{@linkplain AccessTimePolicy.NO_TIME_SLOT} if no {@link TimePeriod} is there only check for MOY, DOM,DOW</p>
	 * 			<p>{@linkplain AccessTimePolicy.FAILURE} if no {@linkplain TimePeriod} satisfied with currentDate</p>
	 */
	public long getTimeInMillis(Calendar currentDate){
		int durationInSec = (int)getDuration(currentDate);
		if(durationInSec > 0){
		currentDate.add(Calendar.SECOND, durationInSec);
		return currentDate.getTimeInMillis();
		}
			
		return durationInSec;
	}


    public String toString(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.print("            all months of year : ");
		for(int month:getMonthOfYear())
			out.print(month+" ");
		out.println();
		out.print("            all day of month : ");
		for(int date:getDayOfMonth())
			out.print(date+" ");
		out.println();
		out.print("            all day of week : ");
		for(int weekDay:getDayOfWeek())
			out.print(weekDay+" ");
		out.println();
		out.println("            all time : ");
		for(TimePeriod timePeriod:timePeriods)
			out.print(timePeriod);
		out.close();
		return stringBuffer.toString();
	}


}
