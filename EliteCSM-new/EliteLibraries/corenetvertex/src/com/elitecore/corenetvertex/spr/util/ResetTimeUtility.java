package com.elitecore.corenetvertex.spr.util;

import java.util.Calendar;

public class ResetTimeUtility {

    public static long calculateWeeklyResetTime() {
        return calculateWeeklyResetTime(System.currentTimeMillis());
    }


    public static long calculateDailyResetTime() {
        return calculateDailyResetTime(System.currentTimeMillis());
    }

    public static long calculateQuotaResetTime() {
        return calculateQuotaResetTime(System.currentTimeMillis());
    }

    public static long calculateWeeklyResetTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

        return calendar.getTimeInMillis();
    }


    public static long calculateDailyResetTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public static long calculateQuotaResetTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.YEAR,20);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }
}
