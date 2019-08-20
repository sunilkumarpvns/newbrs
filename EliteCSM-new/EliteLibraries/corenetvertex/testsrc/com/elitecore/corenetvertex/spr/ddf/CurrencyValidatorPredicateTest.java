package com.elitecore.corenetvertex.spr.ddf;

import com.elitecore.corenetvertex.constants.MonetaryBalanceType;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class CurrencyValidatorPredicateTest {

    private CurrencyValidatorPredicate predicate;
    private static final String GLOBAL_CURRENCY = "INR";
    private MonetaryBalance balance;

    @Before
    public void setUp() {
        this.predicate = new CurrencyValidatorPredicate(GLOBAL_CURRENCY);
        this.balance = new MonetaryBalance(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                10,
                100,
                0,
                0,
                0,
                0,
                0,
                GLOBAL_CURRENCY,
                MonetaryBalanceType.DEFAULT.name(),
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                "parameter1",
                "parameter2");
    }

    @Test
    public void testReturnsTrueWhenMonetaryBalanceHaveSameCurrency() {
        balance.setCurrency(GLOBAL_CURRENCY);
        Assert.assertTrue(predicate.test(balance));
    }

    @Test
    public void testReturnsFalseWhenMonetaryHaveSameCurrencyWithDifferentCase() {
        balance.setCurrency(GLOBAL_CURRENCY.toLowerCase());
        Assert.assertFalse(predicate.test(balance));
    }

    @Test
    public void testReturnsFalseWhenMonetaryBalanceHaveDifferentCurrency() {
        balance.setCurrency("Algeria");
        Assert.assertFalse(predicate.test(balance));
    }
}