package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.data.CarryForwardStatus;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.Arrays;

public class SubscriberNonMonetoryBalanceTest {

    private SubscriberNonMonitoryBalance subscriberNonMonitoryBalance;

    @Test
    public void add_balance_test(){
        NonMonetoryBalance nonMonetoryBalance1 = new NonMonetoryBalance("1", RandomUtils.nextInt(0, Integer.MAX_VALUE), "1", "1" ,1,
                0, "1", "1", 0,0,0,0,
                0,0,0,0,0,0,0,0, CommonConstants.QUOTA_UNLIMITED, ResetBalanceStatus.NOT_RESET, 0, 0, CarryForwardStatus.CARRY_FORWARD, null, null, 0);
        NonMonetoryBalance nonMonetoryBalance2 = new NonMonetoryBalance("2", RandomUtils.nextInt(0, Integer.MAX_VALUE), "2", "2" ,2,
                1, "2", "2", 0,0,0,0,
                0,0,0,0,0,0,0,0, CommonConstants.QUOTA_UNLIMITED, ResetBalanceStatus.NOT_RESET,0, 0, CarryForwardStatus.CARRY_FORWARD, null, null, 0);
        NonMonetoryBalance nonMonetoryBalance3 = new NonMonetoryBalance("3", RandomUtils.nextInt(0, Integer.MAX_VALUE), "3", "3" ,3,
                2, "3", "3", 0,0,0,0,
                0,0,0,0,0,0,0,0, CommonConstants.QUOTA_UNLIMITED, ResetBalanceStatus.NOT_RESET,0, 0, CarryForwardStatus.CARRY_FORWARD, null, null, 0);
        NonMonetoryBalance nonMonetoryBalance4 = new NonMonetoryBalance("4", RandomUtils.nextInt(0, Integer.MAX_VALUE), "1", "1" ,4,
                0, "4", "1", 0,0,0,0,
                0,0,0,0,0,0,0,0, CommonConstants.QUOTA_UNLIMITED, ResetBalanceStatus.NOT_RESET,0, 0, CarryForwardStatus.CARRY_FORWARD, null, null, 0);
        NonMonetoryBalance nonMonetoryBalance5 = new NonMonetoryBalance("5", RandomUtils.nextInt(0, Integer.MAX_VALUE), "2", "2" ,5,
                1, "5", "2", 0,0,0,0,
                0,0,0,0,0,0,0,0, CommonConstants.QUOTA_UNLIMITED, ResetBalanceStatus.NOT_RESET,0, 0, CarryForwardStatus.CARRY_FORWARD, null, null, 0);
        NonMonetoryBalance nonMonetoryBalance6 = new NonMonetoryBalance("6", RandomUtils.nextInt(0, Integer.MAX_VALUE), "3", "3" ,6,
                2, "6", "3", 0,0,0,0,
                0,0,0,0,0,0,0,0, CommonConstants.QUOTA_UNLIMITED, ResetBalanceStatus.NOT_RESET,0, 0, CarryForwardStatus.CARRY_FORWARD, null, null, 0);


        ArrayList<NonMonetoryBalance> nonMonetoryBalances = new ArrayList<>();
        nonMonetoryBalances.add(nonMonetoryBalance1);
        nonMonetoryBalances.add(nonMonetoryBalance2);
        nonMonetoryBalances.add(nonMonetoryBalance3);
        nonMonetoryBalances.add(nonMonetoryBalance4);
        nonMonetoryBalances.add(nonMonetoryBalance5);
        nonMonetoryBalances.add(nonMonetoryBalance6);

        subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(nonMonetoryBalances);

        NonMonetoryBalance expectedBalance1 = subscriberNonMonitoryBalance.getPackageBalance("1").getBalance("1").getHsqBalance().get(0);
        NonMonetoryBalance expectedBalance2 = subscriberNonMonitoryBalance.getPackageBalance("2").getBalance("2").getFupLevel1Balance().get(0);
        NonMonetoryBalance expectedBalance3 = subscriberNonMonitoryBalance.getPackageBalance("3").getBalance("3").getFupLevel2Balance().get(0);
        NonMonetoryBalance expectedBalance4 = subscriberNonMonitoryBalance.getPackageBalance("1").getBalance("1").getHsqBalance().get(1);
        NonMonetoryBalance expectedBalance5 = subscriberNonMonitoryBalance.getPackageBalance("2").getBalance("2").getFupLevel1Balance().get(1);
        NonMonetoryBalance expectedBalance6 = subscriberNonMonitoryBalance.getPackageBalance("3").getBalance("3").getFupLevel2Balance().get(1);

        Assert.assertSame(expectedBalance1, nonMonetoryBalance1);
        Assert.assertSame(expectedBalance2, nonMonetoryBalance2);
        Assert.assertSame(expectedBalance3, nonMonetoryBalance3);
        Assert.assertSame(expectedBalance4, nonMonetoryBalance4);
        Assert.assertSame(expectedBalance5, nonMonetoryBalance5);
        Assert.assertSame(expectedBalance6, nonMonetoryBalance6);
    }

    @Test
    public void get_Expired_Balance_While_Adding_Balance(){
        NonMonetoryBalance nonMonetoryBalance = new NonMonetoryBalance("1", RandomUtils.nextInt(0, Integer.MAX_VALUE), "1", "1" ,1,
                0, "1", "1", 0,0,0,0,
                0,0,0,0,0,0,0,0, 0, ResetBalanceStatus.NOT_RESET,0, 0, CarryForwardStatus.CARRY_FORWARD, null, null, 0);

        subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(Arrays.asList(nonMonetoryBalance));

        Assert.assertEquals(subscriberNonMonitoryBalance.getIdWiseExpiredBalance(nonMonetoryBalance.getId()), nonMonetoryBalance);
    }

    @Test
    public void copy_Expired_Balance_When_Creating_Copy_Of_Object(){
        NonMonetoryBalance nonMonetoryBalance = new NonMonetoryBalance("1", RandomUtils.nextInt(0, Integer.MAX_VALUE), "1", "1" ,1,
                0, "1", "1", 0,0,0,0,
                0,0,0,0,0,0,0,0, 0, ResetBalanceStatus.NOT_RESET,0, 0, CarryForwardStatus.CARRY_FORWARD, null, null, 0);

        subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(Arrays.asList(nonMonetoryBalance)).copy();

        ReflectionAssert.assertLenientEquals(subscriberNonMonitoryBalance.getIdWiseExpiredBalance(nonMonetoryBalance.getId()), nonMonetoryBalance);
    }
}