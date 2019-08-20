package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pkg.ChargingType;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.Random;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.junit.Assert.assertEquals;

@RunWith(HierarchicalContextRunner.class)
public class RnCNonMonetaryBalanceTest {


    private RnCNonMonetaryBalance nonMonitoryBalance = new RnCNonMonetaryBalance.RnCNonMonetaryBalanceBuilder("0",
            "0",
            "0",
            "0",
            "0",
            "0",
            ResetBalanceStatus.NOT_RESET, null, ChargingType.SESSION).build();

    public class Addition {


        private Random random;
        private long val;
        private long addition;


        @Before
        public void setUp() {
            random = new Random();
            val = random.nextInt();
            addition = random.nextInt();
        }

        public class OfBillingCycle {

            @Test
            public void availableTimeBalance() {

                nonMonitoryBalance.setBillingCycleAvailable(val);
                nonMonitoryBalance.addBillingCycleAvailableTime(addition);
                assertEquals(val + addition, nonMonitoryBalance.getBillingCycleAvailable());
            }
        }

        @Test
        public void addDaily() {

            nonMonitoryBalance.setDailyLimit(val);
            nonMonitoryBalance.addDaily(addition);
            assertEquals(val + addition, nonMonitoryBalance.getDailyLimit());
        }

        @Test
        public void addWeekly() {

            nonMonitoryBalance.setWeeklyLimit(val);
            nonMonitoryBalance.addWeekly(addition);
            assertEquals(val + addition, nonMonitoryBalance.getWeeklyLimit());
        }

        public class OfReservation {

            @Test
            public void timeBalance() {

                nonMonitoryBalance.setReservationTime(val);
                nonMonitoryBalance.addReservationTime(addition);

                assertEquals(val + addition, nonMonitoryBalance.getReservationTime());
            }
        }
    }


    public class Substraction {

        private Random random;
        long val;
        long addition;


        @Before
        public void setUp() {
            random = new Random();
            val = random.nextInt();
            addition = random.nextInt();
        }

        @Test
        public void substractBillingCycle() {


            nonMonitoryBalance.setBillingCycleTotal(val);
            nonMonitoryBalance.setBillingCycleAvailable(val);

            nonMonitoryBalance.substractBillingCycle(addition);

            assertEquals(val - addition, nonMonitoryBalance.getBillingCycleAvailable());
        }

        @Test
        public void substractReservation() {
            nonMonitoryBalance.setReservationTime(val);
            nonMonitoryBalance.substractReservation(addition);
            assertEquals(val - addition, nonMonitoryBalance.getReservationTime());
        }

    }

    @Test
    public void resetDailyUsageTosetAllDailyUsageToZERO() {
        Random random = new Random();
        nonMonitoryBalance.setDailyLimit(random.nextInt());
        nonMonitoryBalance.resetDailyUsage();
        assertEquals(nonMonitoryBalance.getDailyLimit(), 0);
    }

    @Test
    public void resetWeeklyUsageTosetAllWeeklyUsageToZERO() {
        Random random = new Random();
        nonMonitoryBalance.setWeeklyLimit(random.nextInt());
        nonMonitoryBalance.resetWeeklyUsage();
        assertEquals(nonMonitoryBalance.getWeeklyLimit(), 0);
    }

    @Test
    public void copyCreateNewInstanceWithAllValueSame() {
        Random random = new Random();

        long billingCycleTotalTime  = random.nextInt();
        long billingCycleAvailableTime = random.nextInt();
        long dailyTime = random.nextInt();
        long weeklyTime = random.nextInt();
        long reservationTime = random.nextInt();

        long dailyResetTime = random.nextInt();
        long weeklyResetTime = random.nextInt();
        long billingCycleResetTime = random.nextInt();

        RnCNonMonetaryBalance nonMonetoryBalance = new RnCNonMonetaryBalance("0",
                "0",
                "0",
                "0",
                "0",
                "0",
                billingCycleTotalTime,
                billingCycleAvailableTime,
                dailyTime,
                weeklyTime,
                reservationTime,
                dailyResetTime,
                weeklyResetTime,
                billingCycleResetTime,
                ResetBalanceStatus.NOT_RESET, null,
                ChargingType.SESSION
        );

        RnCNonMonetaryBalance.RnCNonMonetaryBalanceBuilder nonMonetoryBalanceBuilder = new RnCNonMonetaryBalance.RnCNonMonetaryBalanceBuilder("0",
                "0",
                "0",
                "0",
                "0",
                "0",
                ResetBalanceStatus.NOT_RESET, null, ChargingType.SESSION);

        nonMonetoryBalanceBuilder.withBillingCycleTimeBalance(billingCycleTotalTime, billingCycleAvailableTime);
        nonMonetoryBalanceBuilder.withDailyUsage(dailyTime);
        nonMonetoryBalanceBuilder.withWeeklyUsage(weeklyTime);
        nonMonetoryBalanceBuilder.withBillingCycleResetTime(billingCycleResetTime);
        nonMonetoryBalanceBuilder.withDailyResetTime(dailyResetTime);
        nonMonetoryBalanceBuilder.withWeeklyResetTime(weeklyResetTime);
        nonMonetoryBalanceBuilder.withReservation(reservationTime);
        RnCNonMonetaryBalance expected = nonMonetoryBalanceBuilder.build();

        ReflectionAssert.assertReflectionEquals(expected.copy(), nonMonetoryBalance);
    }

    private long random() {
        return RandomUtils.nextLong();
    }

}
