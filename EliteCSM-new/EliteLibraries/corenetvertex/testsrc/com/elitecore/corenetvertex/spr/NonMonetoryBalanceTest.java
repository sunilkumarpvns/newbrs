package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.data.CarryForwardStatus;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.sql.Timestamp;
import java.util.Random;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.junit.Assert.assertEquals;

/**v
 * Created by harsh on 8/10/17.
 */

@RunWith(HierarchicalContextRunner.class)
public class NonMonetoryBalanceTest {


    private NonMonetoryBalance nonMonitoryBalance = new NonMonetoryBalance.NonMonetaryBalanceBuilder("0",
            nextInt(0, Integer.MAX_VALUE),
            "0",
            0,
            "0",
            "0",
            0,
            "0", ResetBalanceStatus.NOT_RESET, null, null).build();

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
            public void availableVolumeBalance() {
                nonMonitoryBalance.setBillingCycleAvailableVolume(val);
                nonMonitoryBalance.addBillingCycleAvailableVolume(addition);

                assertEquals(val + addition, nonMonitoryBalance.getBillingCycleAvailableVolume());
            }


            @Test
            public void availableTimeBalance() {

                nonMonitoryBalance.setBillingCycleAvailableTime(val);
                nonMonitoryBalance.addBillingCycleAvailableTime(addition);

                assertEquals(val + addition, nonMonitoryBalance.getBillingCycleAvailableTime());
            }
        }

        @Test
        public void addDaily() {

            nonMonitoryBalance.setDailyVolume(val);
            nonMonitoryBalance.setDailyTime(val);

            nonMonitoryBalance.addDaily(addition, addition);

            assertEquals(val + addition, nonMonitoryBalance.getDailyVolume());
            assertEquals(val + addition, nonMonitoryBalance.getDailyTime());
        }

        @Test
        public void addWeekly() {

            nonMonitoryBalance.setWeeklyVolume(val);
            nonMonitoryBalance.setWeeklyTime(val);

            nonMonitoryBalance.addWeekly(addition, addition);

            assertEquals(val + addition, nonMonitoryBalance.getWeeklyVolume());
            assertEquals(val + addition, nonMonitoryBalance.getWeeklyTime());
        }

        public class OfReservation {

            @Test
            public void totalBalance() {

                nonMonitoryBalance.setReservationVolume(val);
                nonMonitoryBalance.addReservationVolume(addition);

                assertEquals(val + addition, nonMonitoryBalance.getReservationVolume());
            }

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
            nonMonitoryBalance.setBillingCycleTotalVolume(val);
            nonMonitoryBalance.setBillingCycleAvailableVolume(val);
            nonMonitoryBalance.setBillingCycleTime(val);
            nonMonitoryBalance.setBillingCycleAvailableTime(val);

            nonMonitoryBalance.substractBillingCycle(addition, addition);

            assertEquals(val - addition, nonMonitoryBalance.getBillingCycleAvailableVolume());
            assertEquals(val - addition, nonMonitoryBalance.getBillingCycleAvailableVolume());
        }

        @Test
        public void substractReservation() {
            nonMonitoryBalance.setReservationVolume(val);
            nonMonitoryBalance.setReservationTime(val);

            nonMonitoryBalance.substractReservation(addition, addition);

            assertEquals(val - addition, nonMonitoryBalance.getReservationVolume());
            assertEquals(val - addition, nonMonitoryBalance.getReservationTime());
        }

    }

    @Test
    public void resetDailyUsageTosetAllDailyUsageToZERO() {
        Random random = new Random();
        nonMonitoryBalance.setDailyVolume(random.nextInt());
        nonMonitoryBalance.setDailyTime(random.nextInt());

        nonMonitoryBalance.resetDailyUsage();

        assertEquals(nonMonitoryBalance.getDailyVolume(), 0);
        assertEquals(nonMonitoryBalance.getDailyTime(), 0);
    }

    @Test
    public void resetWeeklyUsageTosetAllWeeklyUsageToZERO() {
        Random random = new Random();
        nonMonitoryBalance.setWeeklyVolume(random.nextInt());
        nonMonitoryBalance.setWeeklyTime(random.nextInt());

        nonMonitoryBalance.resetWeeklyUsage();

        assertEquals(nonMonitoryBalance.getWeeklyVolume(), 0);
        assertEquals(nonMonitoryBalance.getWeeklyTime(), 0);
    }

    @Test
    public void copyCreateNewInstanceWithAllValueSame() {
        Random random = new Random();

        long billingCycleTotalVolume = random.nextInt();
        long billingCycleAvailableVolume = random.nextInt();
        long billingCycleTime  = random.nextInt();
        long billingCycleAvailableTime = random.nextInt();

        long dailyVolume = random.nextInt();
        long dailyTime = random.nextInt();

        long weeklyVolume = random.nextInt();
        long weeklyTime = random.nextInt();

        long reservationVolume = random.nextInt();
        long reservationTime = random.nextInt();

        long dailyResetTime = random.nextInt();
        long weeklyResetTime = random.nextInt();
        long billingCycleResetTime = random.nextInt();
        long startTime = random.nextInt();
        Timestamp quotaExpiryTime = new Timestamp(System.currentTimeMillis());

        int serviceId = nextInt(0, Integer.MAX_VALUE);
        NonMonetoryBalance nonMonetoryBalance = new NonMonetoryBalance("0",
                serviceId,
                "0",
                "0",
                0,
                0,
                "0",
                "0",
                billingCycleTotalVolume,
                billingCycleAvailableVolume,
                billingCycleTime,
                billingCycleAvailableTime,
                dailyVolume,
                dailyTime,
                weeklyVolume,
                weeklyTime,
                reservationVolume,
                reservationTime,
                dailyResetTime,
                weeklyResetTime,
                billingCycleResetTime,
                ResetBalanceStatus.NOT_RESET, 0,0, CarryForwardStatus.CARRY_FORWARD,null, null, startTime
        );

        NonMonetoryBalance.NonMonetaryBalanceBuilder nonMonetoryBalanceBuilder = new NonMonetoryBalance.NonMonetaryBalanceBuilder("0",
                serviceId,
                "0",
                0,
                "0",
                "0",
                0,
                "0", ResetBalanceStatus.NOT_RESET,null, null);

        nonMonetoryBalanceBuilder.withBillingCycleVolumeBalance(billingCycleTotalVolume, billingCycleAvailableVolume);
        nonMonetoryBalanceBuilder.withBillingCycleTimeBalance(billingCycleTime, billingCycleAvailableTime);
        nonMonetoryBalanceBuilder.withDailyUsage(dailyVolume, dailyTime);
        nonMonetoryBalanceBuilder.withWeeklyUsage(weeklyVolume, weeklyTime);
        nonMonetoryBalanceBuilder.withBillingCycleResetTime(billingCycleResetTime);
        nonMonetoryBalanceBuilder.withDailyResetTime(dailyResetTime);
        nonMonetoryBalanceBuilder.withWeeklyResetTime(weeklyResetTime);
        NonMonetoryBalance expected = nonMonetoryBalanceBuilder.build();

        ReflectionAssert.assertLenientEquals(expected.copy(), nonMonetoryBalance);
    }

    private long random() {
        return RandomUtils.nextLong();
    }

}
