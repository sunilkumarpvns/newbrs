package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pkg.ChargingType;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class SubscriberRnCNonMonetaryBalanceTest {

    private SubscriberRnCNonMonetaryBalance subscriberNonMonitoryBalance;

    @Test
    public void getPackageBalanceFromPackageId(){
        RnCNonMonetaryBalance nonMonetoryBalance = createNonMonetaryBalanceWithPackageId();

        ArrayList<RnCNonMonetaryBalance> nonMonetoryBalances = new ArrayList<>();
        nonMonetoryBalances.add(nonMonetoryBalance);
        subscriberNonMonitoryBalance = new SubscriberRnCNonMonetaryBalance(nonMonetoryBalances);
        RnCNonMonetaryBalance expectedBalance = subscriberNonMonitoryBalance.getPackageBalance("1").getBalance("1");

        Assert.assertSame(expectedBalance, nonMonetoryBalance);
    }

    @Test
    public void getPackageBalanceFromSubscriptionId(){
        RnCNonMonetaryBalance nonMonetoryBalance = createNonMonetaryBalanceWithSubscriptionId();

        ArrayList<RnCNonMonetaryBalance> nonMonetoryBalances = new ArrayList<>();
        nonMonetoryBalances.add(nonMonetoryBalance);
        subscriberNonMonitoryBalance = new SubscriberRnCNonMonetaryBalance(nonMonetoryBalances);
        RnCNonMonetaryBalance expectedBalance = subscriberNonMonitoryBalance.getPackageBalance("2").getBalance("2");

        Assert.assertSame(expectedBalance, nonMonetoryBalance);
    }

    @Test
    public void copyCreateNewInstanceWithAllValueSame() {

        RnCNonMonetaryBalance nonMonetoryBalance = createNonMonetaryBalanceWithSubscriptionId();

        ArrayList<RnCNonMonetaryBalance> nonMonetoryBalances = new ArrayList<>();
        nonMonetoryBalances.add(nonMonetoryBalance);
        subscriberNonMonitoryBalance = new SubscriberRnCNonMonetaryBalance(nonMonetoryBalances);

        ReflectionAssert.assertReflectionEquals(subscriberNonMonitoryBalance.copy(), subscriberNonMonitoryBalance);
    }

    @Test
    public void getExpiredBalanceWhileAddingBalance(){
        RnCNonMonetaryBalance nonMonetoryBalance = createNonMonetaryBalanceWithPackageId();
        nonMonetoryBalance.setBalanceExpiryTime(0);

        subscriberNonMonitoryBalance = new SubscriberRnCNonMonetaryBalance(Arrays.asList(nonMonetoryBalance));

        Assert.assertEquals(subscriberNonMonitoryBalance.getIdWiseExpiredBalance(nonMonetoryBalance.getId()), nonMonetoryBalance);
    }

    @Test
    public void copyExpiredBalanceWhileCreatingCopyOfObject(){
        RnCNonMonetaryBalance nonMonetoryBalance = createNonMonetaryBalanceWithPackageId();
        nonMonetoryBalance.setBalanceExpiryTime(0);

        subscriberNonMonitoryBalance = new SubscriberRnCNonMonetaryBalance(Arrays.asList(nonMonetoryBalance)).copy();

        ReflectionAssert.assertLenientEquals(subscriberNonMonitoryBalance.getIdWiseExpiredBalance(nonMonetoryBalance.getId()), nonMonetoryBalance);
    }

    private RnCNonMonetaryBalance createNonMonetaryBalanceWithPackageId(){
        return new RnCNonMonetaryBalance("1", "1", "1","1" ,"1",
                null, 1, 1, 0,0,0,0,
                0,CommonConstants.QUOTA_UNLIMITED, ResetBalanceStatus.NOT_RESET, null, ChargingType.SESSION);
    }

    private RnCNonMonetaryBalance createNonMonetaryBalanceWithSubscriptionId(){
        return new RnCNonMonetaryBalance("2", "2","2", "2" ,"1",
                "2", 2, 2, 0,0,0,0,
                0,CommonConstants.QUOTA_UNLIMITED, ResetBalanceStatus.NOT_RESET, null, ChargingType.SESSION);
    }
}