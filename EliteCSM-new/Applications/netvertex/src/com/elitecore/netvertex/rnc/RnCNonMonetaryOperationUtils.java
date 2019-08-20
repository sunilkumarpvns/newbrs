package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.rnc.MonetaryOperationUtils.BalanceDiff;

import java.util.Objects;

public class RnCNonMonetaryOperationUtils {


    public static void subtract(RnCNonMonetaryBalance balance,
                                long deductableTime,
                                ExecutionContext executionContext) {


        balance.substractBillingCycle(deductableTime);

        if (balance.isPreviousDayUsage(executionContext.getCurrentTime().getTimeInMillis())) {
            if (executionContext.isSessionCreatedToday()) {
                balance.setDailyLimit(deductableTime);
            } else {
                balance.resetDailyUsage();
            }
            balance.setNextDailyResetTime();
        } else {
            balance.addDaily(deductableTime);
        }

        if (balance.isPreviousWeekUsage(executionContext.getCurrentTime().getTimeInMillis())) {
            if (executionContext.isSessionCreatedInCurrentWeek()) {
                balance.setWeeklyLimit(deductableTime);
            } else {
                balance.resetWeeklyUsage();
            }
            balance.setNextWeeklyResetTime();
        } else {
            balance.addWeekly(deductableTime);
        }

    }

    public static void closeReservation(RnCNonMonetaryBalance nonMonetoryBalance, GyServiceUnits serviceUnits) {

        if (Objects.isNull(serviceUnits)) {
            return;
        }

        if (serviceUnits.isNonMonetaryReservationRequired() == false) {
            return;
        }

        nonMonetoryBalance.substractReservation(serviceUnits.getTime());
    }

    public static BalanceDiff diff(RnCNonMonetaryBalance oldBalance, RnCNonMonetaryBalance newBalance) {


        boolean hasReservationDiff = false;
        if (oldBalance.getReservationTime() != newBalance.getReservationTime()) {
            hasReservationDiff = true;
        }


        boolean hasBalanceDiff = false;

        if (oldBalance.getBillingCycleAvailable() != newBalance.getBillingCycleAvailable()
                || oldBalance.getDailyLimit() != newBalance.getDailyLimit()
                || oldBalance.getWeeklyLimit() != newBalance.getWeeklyLimit()) {
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

    public enum RateCardResults {
        FAILURE,
        NO_BALANCE,
        SUCCESS;
    }

}
