package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.rnc.MonetaryOperationUtils.BalanceDiff;

import java.util.Objects;

public class NonMonetaryOperationUtils {


    public static void subtract(NonMonetoryBalance balance,
                                long deductableVolume,
                                long deductableTime,
                                ExecutionContext executionContext) {


        balance.substractBillingCycle(deductableVolume, deductableTime);

        if (balance.isPreviousDayUsage(executionContext.getCurrentTime().getTimeInMillis())) {
            if (executionContext.isSessionCreatedToday()) {
                balance.setDailyVolume(deductableVolume);
                balance.setDailyTime(deductableTime);
            } else {
                balance.resetDailyUsage();
            }
            balance.setNextDailyResetTime();
        } else {
            balance.addDaily(deductableVolume, deductableTime);
        }

        if (balance.isPreviousWeekUsage(executionContext.getCurrentTime().getTimeInMillis())) {
            if (executionContext.isSessionCreatedInCurrentWeek()) {
                balance.setWeeklyVolume(deductableVolume);
                balance.setWeeklyTime(deductableTime);
            } else {
                balance.resetWeeklyUsage();
            }
            balance.setNextWeeklyResetTime();
        } else {
            balance.addWeekly(deductableVolume, deductableTime);
        }

    }

    public static void closeReservation(NonMonetoryBalance nonMonetoryBalance, GyServiceUnits serviceUnits) {

        if (Objects.isNull(serviceUnits)) {
            return;
        }

        if (serviceUnits.isNonMonetaryReservationRequired() == false) {
            return;
        }

        nonMonetoryBalance.substractReservation(serviceUnits.getVolume(), serviceUnits.getTime());
    }

    public static BalanceDiff diff(NonMonetoryBalance oldBalance, NonMonetoryBalance newBalance) {


        boolean hasReservationDiff = false;
        if (oldBalance.getReservationVolume() != newBalance.getReservationVolume()
                || oldBalance.getReservationTime() != newBalance.getReservationTime()) {
            hasReservationDiff = true;
        }


        boolean hasBalanceDiff = false;

        if (oldBalance.getBillingCycleAvailableVolume() != newBalance.getBillingCycleAvailableVolume()
                || oldBalance.getBillingCycleAvailableTime() != newBalance.getBillingCycleAvailableTime()
                || oldBalance.getDailyVolume() != newBalance.getDailyVolume()
                || oldBalance.getDailyTime() != newBalance.getDailyTime()
                || oldBalance.getWeeklyVolume() != newBalance.getWeeklyVolume()
                || oldBalance.getWeeklyTime() != newBalance.getWeeklyTime()) {
            hasBalanceDiff = true;
        }


        if (hasBalanceDiff && hasReservationDiff) {
            return BalanceDiff.BALANCE_AND_RESERVATION_DIFF;
        } else if (hasBalanceDiff == false && hasReservationDiff) {
            return BalanceDiff.RESERVATION_DIFF;
        } else if (hasBalanceDiff && hasReservationDiff == false) {
            return BalanceDiff.BALANCE_DIFF;
        } else {
            return BalanceDiff.NO_DIFF;
        }
    }

}
