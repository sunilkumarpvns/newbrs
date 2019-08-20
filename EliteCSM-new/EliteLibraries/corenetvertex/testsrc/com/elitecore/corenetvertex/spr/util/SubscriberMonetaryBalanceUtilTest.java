package com.elitecore.corenetvertex.spr.util;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.MonetaryBalanceType;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import org.junit.Assert;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;

public class SubscriberMonetaryBalanceUtilTest {
    private SubscriberMonetaryBalance createSubscriberMonetaryBalance(String serviceId, long validFromDate, long validToDate,
                                                                      String currency, String type) {
        SubscriberMonetaryBalance subscriberMonetaryBalance = new SubscriberMonetaryBalance(TimeSource.systemTimeSource());
        subscriberMonetaryBalance.addMonitoryBalances(createMonetaryBalance(serviceId, validFromDate, validToDate, currency, type));
        return subscriberMonetaryBalance;
    }

    private MonetaryBalance createMonetaryBalance(String serviceId, long validFromDate, long validToDate,
                                                  String currency, String type) {
        return new MonetaryBalance("id", "subId", serviceId
                , 100, 100, 0, 0,
                0, validFromDate, validToDate, currency, type,
                0, 0, null, null);
    }

    @Test
    public void testsGetDefaultBalanceGivesDefaultBalanceForAllServiceIfExists(){
        SubscriberMonetaryBalance subscriberMonetaryBalance = createSubscriberMonetaryBalance(null,
                100, CommonConstants.FUTURE_DATE, "INR", MonetaryBalanceType.DEFAULT.name());
        MonetaryBalance monetaryBalance = createMonetaryBalance(null,
                100, CommonConstants.FUTURE_DATE, "INR", MonetaryBalanceType.DEFAULT.name());
        ReflectionAssert.assertReflectionEquals(monetaryBalance, SubscriberMonetaryBalanceUtil.getDefaultBalance(null,subscriberMonetaryBalance));
    }
    @Test
    public void testsGetDefaultBalanceGivesDefaultBalanceForDataServiceIfExists(){
        SubscriberMonetaryBalance subscriberMonetaryBalance = createSubscriberMonetaryBalance("DATA",
                100, CommonConstants.FUTURE_DATE, "INR", MonetaryBalanceType.DEFAULT.name());
        MonetaryBalance monetaryBalance = createMonetaryBalance("DATA",
                100, CommonConstants.FUTURE_DATE, "INR", MonetaryBalanceType.DEFAULT.name());
        ReflectionAssert.assertReflectionEquals(monetaryBalance, SubscriberMonetaryBalanceUtil.getDefaultBalance("DATA",subscriberMonetaryBalance));
    }
    @Test
    public void testsGetDefaultBalanceGivesNullIfDefaultBalanceDoesNotExist(){
        SubscriberMonetaryBalance subscriberMonetaryBalance = createSubscriberMonetaryBalance("DATA",
                100, CommonConstants.FUTURE_DATE, "INR", MonetaryBalanceType.PROMOTIONAL.name());
        Assert.assertNull(SubscriberMonetaryBalanceUtil.getDefaultBalance(null,subscriberMonetaryBalance));
    }
    @Test
    public void testsGetMatchingBalanceReturnMatchingBalanceIfAllCriteriaMatch(){
        SubscriberMonetaryBalance subscriberMonetaryBalance = createSubscriberMonetaryBalance(null,
                100, CommonConstants.FUTURE_DATE, "INR", MonetaryBalanceType.DEFAULT.name());
        MonetaryBalance monetaryBalance = createMonetaryBalance(null,
                100, CommonConstants.FUTURE_DATE, "INR", MonetaryBalanceType.DEFAULT.name());
        ReflectionAssert.assertReflectionEquals(monetaryBalance, SubscriberMonetaryBalanceUtil.getMatchingBalance(subscriberMonetaryBalance,monetaryBalance));
    }
    @Test
    public void testsGetMatchingBalanceReturnNullIfServiceIdDifferent() {
        SubscriberMonetaryBalance subscriberMonetaryBalance = createSubscriberMonetaryBalance(null,
                100, CommonConstants.FUTURE_DATE, "INR", MonetaryBalanceType.DEFAULT.name());
        MonetaryBalance monetaryBalance = createMonetaryBalance("DATA",
                100, CommonConstants.FUTURE_DATE, "INR", MonetaryBalanceType.DEFAULT.name());
        Assert.assertNull(SubscriberMonetaryBalanceUtil.getMatchingBalance(subscriberMonetaryBalance, monetaryBalance));
    }

    @Test
    public void testsGetMatchingBalanceReturnNullIfStartDateDifferent(){
        SubscriberMonetaryBalance subscriberMonetaryBalance = createSubscriberMonetaryBalance(null,
                100, CommonConstants.FUTURE_DATE, "INR", MonetaryBalanceType.DEFAULT.name());
        MonetaryBalance monetaryBalance = createMonetaryBalance(null,
                10, CommonConstants.FUTURE_DATE, "INR", MonetaryBalanceType.DEFAULT.name());
        Assert.assertNull(SubscriberMonetaryBalanceUtil.getMatchingBalance(subscriberMonetaryBalance, monetaryBalance));
    }
    @Test
    public void testsGetMatchingBalanceReturnNullIfEndDateDifferent(){
        SubscriberMonetaryBalance subscriberMonetaryBalance = createSubscriberMonetaryBalance(null,
                100, CommonConstants.FUTURE_DATE+10, "INR", MonetaryBalanceType.DEFAULT.name());
        MonetaryBalance monetaryBalance = createMonetaryBalance(null,
                100, CommonConstants.FUTURE_DATE, "INR", MonetaryBalanceType.DEFAULT.name());
        Assert.assertNull(SubscriberMonetaryBalanceUtil.getMatchingBalance(subscriberMonetaryBalance, monetaryBalance));
    }
    @Test
    public void testsGetMatchingBalanceReturnNullIfCurrency(){
        SubscriberMonetaryBalance subscriberMonetaryBalance = createSubscriberMonetaryBalance(null,
                100, CommonConstants.FUTURE_DATE, "INR", MonetaryBalanceType.DEFAULT.name());
        MonetaryBalance monetaryBalance = createMonetaryBalance(null,
                100, CommonConstants.FUTURE_DATE, "USD", MonetaryBalanceType.DEFAULT.name());
        Assert.assertNull(SubscriberMonetaryBalanceUtil.getMatchingBalance(subscriberMonetaryBalance, monetaryBalance));
    }
}
