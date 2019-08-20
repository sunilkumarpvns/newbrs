package com.elitecore.corenetvertex.util.util;

import com.elitecore.corenetvertex.constants.MonetaryBalanceType;
import com.elitecore.corenetvertex.spr.MonetaryBalance;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.function.Predicate;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class Predicates {
    private static final String MODULE = "FUNCTIONS";
    public static final Predicate<MonetaryBalance> ALL_MONETARY_BALANCE = (MonetaryBalance monetaryBalance)-> {
        if (monetaryBalance.getValidToDate() < System.currentTimeMillis()) {

            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Skipping balance with id:" + monetaryBalance.getId()
                        + ". Reason: Quota has been expired on " + new Timestamp(monetaryBalance.getValidToDate()));
            }
            return false;
        } else {
            return true;
        }
    };

    public static final Predicate<MonetaryBalance> RECENT_MONETARY_BALANCE = (MonetaryBalance monetaryBalance)-> {

        if (monetaryBalance.getValidFromDate()>System.currentTimeMillis()) {
            getLogger().warn(MODULE, "Skipping balance with id:" + monetaryBalance.getId()
                    + ". Reason: Quota starts in future on " + new Timestamp(monetaryBalance.getValidToDate()));
            return false;
        }

        return ALL_MONETARY_BALANCE.test(monetaryBalance);
    };

    public static final Predicate<MonetaryBalance> DEFAULT_MONETARY_BALANCE = new Predicate<MonetaryBalance>() {
        @Override
        public boolean test(MonetaryBalance monetaryBalance) {
            return Objects.equals(monetaryBalance.getType(),MonetaryBalanceType.DEFAULT.name());
        }
    };

}
