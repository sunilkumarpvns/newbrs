package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.spr.MonetaryBalance;

import java.util.Objects;

public class MonetaryOperationUtils {


    public static BalanceDiff diff(MonetaryBalance left, MonetaryBalance right) {

        double balanceDiff = left.getAvailBalance() - right.getAvailBalance();
        double reservationDiff = left.getTotalReservation() - right.getTotalReservation();

        if(balanceDiff == 0.0 && reservationDiff == 0.0) {
            return BalanceDiff.NO_DIFF;
        }

        if(balanceDiff != 0.0 &&  reservationDiff == 0.0) {
            return BalanceDiff.BALANCE_DIFF;
        }

        if(balanceDiff == 0.0 &&  reservationDiff != 0.0) {
            return BalanceDiff.RESERVATION_DIFF;
        }

        return BalanceDiff.BALANCE_AND_RESERVATION_DIFF;

    }

    public static void closeReservation(MonetaryBalance monetaryBalance, GyServiceUnits serviceUnits) {
        if (Objects.isNull(serviceUnits)) {
            return;
        }

        if (serviceUnits.getReservedMonetaryBalance() <= 0) {
            return;
        }

        monetaryBalance.substractReservation(serviceUnits.getReservedMonetaryBalance());
    }


    public enum BalanceDiff {
        NO_DIFF,
        BALANCE_DIFF,
        RESERVATION_DIFF,
        BALANCE_AND_RESERVATION_DIFF;
    }
}
