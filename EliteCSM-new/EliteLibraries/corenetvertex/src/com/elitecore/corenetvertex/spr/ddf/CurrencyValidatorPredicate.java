package com.elitecore.corenetvertex.spr.ddf;

import java.util.function.Predicate;
import com.elitecore.corenetvertex.spr.MonetaryBalance;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class CurrencyValidatorPredicate implements Predicate<MonetaryBalance> {
    private static final String MODULE = "CURRENCY-VALIDATOR";

    private String systemCurrency;

    public CurrencyValidatorPredicate(String systemCurrency) {
        this.systemCurrency = systemCurrency;
    }

    @Override
    public boolean test(MonetaryBalance monetaryBalance) {
        if (systemCurrency.equals(monetaryBalance.getCurrency()) == false) {
            getLogger().warn(MODULE, "Skipping monetary balance with id:" + monetaryBalance.getId()
                    + ". Reason: System Currency: " + systemCurrency + ", balance currency: " + monetaryBalance.getCurrency() + " is different");
            return false;
        }

        return true;
    }

    public static CurrencyValidatorPredicate create(String systemCurrency) {
        return new CurrencyValidatorPredicate(systemCurrency);
    }
}
