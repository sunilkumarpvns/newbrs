package com.elitecore.corenetvertex.pm.util;

import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.acesstime.TimePeriod;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class TimeSlotUtil {
    private static final long ONE_DAY_TOTAL_SEC = TimeUnit.DAYS.toSeconds(1);
    public static List<TimePeriod> createTimeSlots(List<TimePeriod> timePeriods){

        ArrayList<TimePeriod> resultSlots = new ArrayList<>();

        if(timePeriods==null || timePeriods.isEmpty()){
            return resultSlots;
        }

        TreeSet<Time> timeSlots = new TreeSet<>();
        for(TimePeriod period :timePeriods) {
            timeSlots.add(new Time(period.getStartHour(), period.getStartMinute(), period.getStartSecond()));
            timeSlots.add(new Time(period.getStopHour(), period.getStopMinute(), period.getStopSecond()));
        }

        if(timeSlots.isEmpty() == false) {
            Time currTime;
            Time nextTime = timeSlots.pollFirst();
            while(nextTime != null) {
                currTime = nextTime;
                nextTime = timeSlots.higher(currTime);
                if(nextTime != null)
                    resultSlots.add(new TimePeriod(currTime.getHour(), currTime.getMinute(), currTime.getSecond(), nextTime.getHour(), nextTime.getMinute(), nextTime.getSecond()));
            }
            resultSlots.trimToSize();
        }
        return resultSlots;
    }

    public static long getNextSessionTimeOut(List<TimePeriod> timeSlots, TimePeriod currTimePeriod){
        if (timeSlots == null || timeSlots.isEmpty()) {
            return AccessTimePolicy.NO_TIME_OUT;
        }

        //	In case 1
        TimePeriod firstTimePeriod = timeSlots.get(0);
        if(firstTimePeriod.compareTo(currTimePeriod) < 0) {
            return (long) firstTimePeriod.getStartTimeInSec() - currTimePeriod.getStopTimeInSec();
        }

        //	In case 2
        for(TimePeriod timePeriod : timeSlots){
            int result = timePeriod.compareTo(currTimePeriod);
            if(result == 0){
                int diffInSec = timePeriod.getStopTimeInSec() - currTimePeriod.getStopTimeInSec();
                if(diffInSec > 0){
                    return diffInSec;
                }
            } else if(result < 0) {
                break;
            }
        }
        //	In case 3
        return (ONE_DAY_TOTAL_SEC + firstTimePeriod.getStartTimeInSec() - currTimePeriod.getStartTimeInSec());
    }
}
