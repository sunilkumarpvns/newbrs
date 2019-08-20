package com.elitecore.corenetvertex.spr.util;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;

import javax.annotation.Nonnull;
import java.sql.Timestamp;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Objects;

public class RenewalIntervalUtility {

    public static Timestamp calculateExpiryTime(Integer renewalInterval,
                                                RenewalIntervalUnit renewalIntervalUnit,
                                                Timestamp resetTime,
                                                Timestamp startTime,
                                                Integer billDay) throws OperationFailedException {

        Timestamp balanceExpiry;
        if (RenewalIntervalUnit.TILL_BILL_DATE != renewalIntervalUnit) {
            if (renewalInterval == 0) {
                balanceExpiry = resetTime;
            } else {
                balanceExpiry = new Timestamp(renewalIntervalUnit.addTime(startTime.getTime(), renewalInterval));
                if (resetTime.getTime() < balanceExpiry.getTime()) {
                    balanceExpiry = resetTime;
                }

            }
        } else {
            balanceExpiry = calculateExpiryForTillBillDate(startTime, resetTime, billDay);
        }
        return balanceExpiry;
    }


    private static Timestamp calculateExpiryForTillBillDate(Timestamp startTime, Timestamp resetTime, Integer billDay) throws OperationFailedException {
        Timestamp balanceExpiry;
        if (Objects.nonNull(billDay)) {
            balanceExpiry = getBillDate(startTime, billDay);
            if (resetTime.getTime() < balanceExpiry.getTime()) {
                balanceExpiry = resetTime;
            }
            return balanceExpiry;
        } else {
            throw new OperationFailedException("Billing day is mandatory for TILL-BILL DATE configuration");
        }

    }

    public static long getProratedQuota(long quota, Timestamp startTime, Timestamp endTime, RenewalIntervalUnit renewalIntervalUnit, int renewalInterval) {

        if(Objects.equals(CommonConstants.QUOTA_UNDEFINED, quota) || Objects.equals(CommonConstants.QUOTA_UNLIMITED, quota)){
            return quota;
        }

        if(Objects.equals(RenewalIntervalUnit.TILL_BILL_DATE, renewalIntervalUnit) == false
                && Objects.equals(RenewalIntervalUnit.MONTH, renewalIntervalUnit) == false
                && Objects.equals(RenewalIntervalUnit.MONTH_END, renewalIntervalUnit) == false){
            return quota;
        }

        Calendar startTimeCalendar = Calendar.getInstance();
        Calendar endTimeCalendar = Calendar.getInstance();

        startTimeCalendar.setTimeInMillis(startTime.getTime());
        endTimeCalendar.setTimeInMillis(endTime.getTime());

        long totalDays=1;

        if(RenewalIntervalUnit.TILL_BILL_DATE == renewalIntervalUnit || renewalInterval<2){
            YearMonth yearMonthObject = YearMonth.of(startTimeCalendar.get(Calendar.YEAR), startTimeCalendar.get(Calendar.MONTH)+1);
            totalDays = yearMonthObject.lengthOfMonth();
        } else {
            Calendar quotaEndTime = Calendar.getInstance();
            quotaEndTime.setTimeInMillis(renewalIntervalUnit.addTime(startTime.getTime(),renewalInterval));
            if(RenewalIntervalUnit.MONTH == renewalIntervalUnit){
                totalDays = ChronoUnit.DAYS.between(startTimeCalendar.toInstant(), quotaEndTime.toInstant()) + 1;
            } else if(RenewalIntervalUnit.MONTH_END == renewalIntervalUnit) {
                totalDays = getTotalDaysInMonths(renewalInterval, startTimeCalendar);
            }
        }

        long availableDays = ChronoUnit.DAYS.between(startTimeCalendar.toInstant(), endTimeCalendar.toInstant()) + 1;

        if(availableDays>totalDays){
            return quota;
        } else {
            return (long) Math.ceil((double) availableDays * quota / totalDays);
        }
    }

    private static long getTotalDaysInMonths(int months, Calendar startTimeCalendar) {
        long totalDays = 0;

        Calendar calendar = (Calendar) startTimeCalendar.clone();

        for (int i = 0; i < months; i++) {
            YearMonth yearMonthObject = YearMonth.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
            totalDays += yearMonthObject.lengthOfMonth();
            calendar.add(Calendar.MONTH, 1);
        }
        return totalDays == 0 ? 1 : totalDays;
    }

    @Nonnull
    public static Timestamp getBillDate(Timestamp startTime, Integer billDay) {
        Timestamp balanceExpiry;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime.getTime());

        if (billDay <= calendar.get(Calendar.DAY_OF_MONTH)) {
            calendar.add(Calendar.MONTH, 1);
        }
        calendar.set(Calendar.DAY_OF_MONTH, billDay - 1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        balanceExpiry = new Timestamp(calendar.getTimeInMillis());
        return balanceExpiry;
    }
}
