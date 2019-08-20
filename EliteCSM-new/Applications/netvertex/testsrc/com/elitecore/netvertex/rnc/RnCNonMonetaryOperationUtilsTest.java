package com.elitecore.netvertex.rnc;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;


import static com.elitecore.netvertex.rnc.MonetaryOperationUtils.BalanceDiff.BALANCE_AND_RESERVATION_DIFF;
import static com.elitecore.netvertex.rnc.MonetaryOperationUtils.BalanceDiff.BALANCE_DIFF;
import static com.elitecore.netvertex.rnc.MonetaryOperationUtils.BalanceDiff.NO_DIFF;
import static com.elitecore.netvertex.rnc.MonetaryOperationUtils.BalanceDiff.RESERVATION_DIFF;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class RnCNonMonetaryOperationUtilsTest {

    public static final String INR = "INR";
    private Calendar calendar;
    private ExecutionContext executionContext;
    private PCRFRequest pcrfRequest;
    @Before
    public void setUp() {
        calendar = Calendar.getInstance();

        FixedTimeSource timeSource = new FixedTimeSource(calendar.getTimeInMillis());
        pcrfRequest = new PCRFRequestImpl(timeSource);
        pcrfRequest.setSessionStartTime(calendar.getTime());
        executionContext = spy(new ExecutionContext(pcrfRequest, new PCRFResponseImpl(), mock(CacheAwareDDFTable.class), INR));
        when(executionContext.getCurrentTime()).thenReturn(calendar);
    }

    public class Subtraction {
        private RnCNonMonetaryBalance nonMonetoryBalance;
        @Before
        public void setUp() {
            nonMonetoryBalance = createNonMonetaryBalance();
            LogManager.getLogger().debug(RnCNonMonetaryOperationUtilsTest.class.getSimpleName(), "Non-Monetary Balance is " + nonMonetoryBalance);
        }


        @Test
        public void setNewDailyResetTimeWhenUsageResetTimeIsInPreviousDayAndSessionStartedInCurrentDay() {
            long dailyResetTime = calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(1);
            nonMonetoryBalance.setDailyResetTime(dailyResetTime);

            LogManager.getLogger().debug(RnCNonMonetaryOperationUtilsTest.class.getSimpleName(), "Session Start time is " + pcrfRequest.getSessionStartTime());

            RnCNonMonetaryOperationUtils.subtract(nonMonetoryBalance,
                    nextLong(1, nonMonetoryBalance.getBillingCycleAvailable()),
                    executionContext);

            long expected = dailyResetTime + TimeUnit.DAYS.toMillis(1);
            assertEquals(expected, nonMonetoryBalance.getDailyResetTime());
        }


        @Test
        public void setNewWeeklyResetTimeWhenUsageResetTimeIsInPreviousWeekAndSessionStartedInCurrentWeek() {
            long weeklyResetTime = calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(7);
            nonMonetoryBalance.setWeeklyResetTime(weeklyResetTime);

            LogManager.getLogger().debug(RnCNonMonetaryOperationUtilsTest.class.getSimpleName(), "Session Start time is " + pcrfRequest.getSessionStartTime());

            RnCNonMonetaryOperationUtils.subtract(nonMonetoryBalance,
                    nextLong(1, nonMonetoryBalance.getBillingCycleAvailable()),
                    executionContext);

            long expected = weeklyResetTime + TimeUnit.DAYS.toMillis(7);
            assertEquals(expected, nonMonetoryBalance.getWeeklyResetTime());
        }

        public class Time {

            private long deductabledTime;


            @Before
            public void setUp() {
                deductabledTime = nextLong(1,nonMonetoryBalance.getBillingCycleAvailable());
                LogManager.getLogger().debug(RnCNonMonetaryOperationUtilsTest.class.getSimpleName(), "Deductable Time is " + deductabledTime);
            }

            @Test
            public void subtractionFromBillingCycle() {
                long expected = nonMonetoryBalance.getBillingCycleAvailable() - deductabledTime;
                RnCNonMonetaryOperationUtils.subtract(nonMonetoryBalance, deductabledTime, executionContext);
                assertEquals(expected, nonMonetoryBalance.getBillingCycleAvailable());
            }

            @Test
            public void addInToDailyVolumeWhenUsageResetTimeIsInCurrentWeek() {
                long expected = nonMonetoryBalance.getDailyLimit() + deductabledTime;
                RnCNonMonetaryOperationUtils.subtract(nonMonetoryBalance, deductabledTime, executionContext);
                assertEquals(expected, nonMonetoryBalance.getDailyLimit());

            }

            @Test
            public void addInToWeeklyVolumeWhenUsageResetTimeIsInCurrentWeek() {
                long expected = nonMonetoryBalance.getWeeklyLimit() + deductabledTime;
                RnCNonMonetaryOperationUtils.subtract(nonMonetoryBalance, deductabledTime, executionContext);
                assertEquals(expected, nonMonetoryBalance.getWeeklyLimit());

            }

            @Test
            public void resetDailyVolumeWhenUsageResetTimeIsInPreviousDayAndSessionStartedIsNotStartedInCurrentDay() {
                nonMonetoryBalance.setDailyResetTime(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(1));

                pcrfRequest.setSessionStartTime(new Date(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(1)));

                LogManager.getLogger().debug(RnCNonMonetaryOperationUtilsTest.class.getSimpleName(), "Session Start time is " + pcrfRequest.getSessionStartTime());

                RnCNonMonetaryOperationUtils.subtract(nonMonetoryBalance, deductabledTime, executionContext);
                assertEquals(0, nonMonetoryBalance.getDailyLimit());
            }

            @Test
            public void resetWeeklyVolumeWhenUsageResetTimeIsInPreviousWeekAndSessionStartedIsNotStartedInCurrentWeek() {

                nonMonetoryBalance.setWeeklyResetTime(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(7));

                pcrfRequest.setSessionStartTime(new Date(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(7)));

                LogManager.getLogger().debug(RnCNonMonetaryOperationUtilsTest.class.getSimpleName(), "Session Start time is " + pcrfRequest.getSessionStartTime());

                RnCNonMonetaryOperationUtils.subtract(nonMonetoryBalance, deductabledTime,  executionContext);
                assertEquals(0, nonMonetoryBalance.getWeeklyLimit());
            }

            @Test
            public void resetWeeklyVolumeWhenUsageResetTimeIsInPreviousWeekAndSessionStartedIsInCurrentWeek() {

                nonMonetoryBalance.setWeeklyResetTime(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(7));

                pcrfRequest.setSessionStartTime(new Date(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(7)));

                LogManager.getLogger().debug(RnCNonMonetaryOperationUtilsTest.class.getSimpleName(), "Session Start time is " + pcrfRequest.getSessionStartTime());

                RnCNonMonetaryOperationUtils.subtract(nonMonetoryBalance, deductabledTime, executionContext);
                assertEquals(0, nonMonetoryBalance.getWeeklyLimit());
            }

            @Test
            public void setDeductedVolumeAsDailyVolumeWhenUsageResetTimeIsInPreviousDayAndSessionStartedInCurrentDay() {
                long dailyResetTime = calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(1);
                nonMonetoryBalance.setDailyResetTime(dailyResetTime);

                LogManager.getLogger().debug(RnCNonMonetaryOperationUtilsTest.class.getSimpleName(), "Session Start time is " + pcrfRequest.getSessionStartTime());

                RnCNonMonetaryOperationUtils.subtract(nonMonetoryBalance, deductabledTime, executionContext);

                assertEquals(deductabledTime, nonMonetoryBalance.getDailyLimit());
            }


            @Test
            public void setDeductabledVolumeAsWeeklVolumeWhenUsageResetTimeIsInPreviousWeekAndSessionStartedInCurrentWeek() {
                long weeklyResetTime = calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(7);
                nonMonetoryBalance.setWeeklyResetTime(weeklyResetTime);

                LogManager.getLogger().debug(RnCNonMonetaryOperationUtilsTest.class.getSimpleName(), "Session Start time is " + pcrfRequest.getSessionStartTime());

                RnCNonMonetaryOperationUtils.subtract(nonMonetoryBalance, deductabledTime, executionContext);

                assertEquals(deductabledTime, nonMonetoryBalance.getWeeklyLimit());
            }

        }

        @After
        public void logDeductedNonMonetaryBalance() {
            LogManager.getLogger().debug(RnCNonMonetaryOperationUtilsTest.class.getSimpleName(), "Non-Monetary Balance after subtraction is " + nonMonetoryBalance);
        }


    }

    public class CloseReservation {
        private RnCNonMonetaryBalance nonMonetoryBalance;
        private GyServiceUnits gyServiceUnits;

        @Before
        public void setUp() {
            nonMonetoryBalance = createNonMonetaryBalance();
            gyServiceUnits = createGSU(nonMonetoryBalance);
        }

        @Test
        public void subtractReservationTimeFromNonMonetaryBalanceWhenReservationRequiredFlagIsTrueAndGSUIsNotNull() {
            long expected = nonMonetoryBalance.getReservationTime() - gyServiceUnits.getTime();
            RnCNonMonetaryOperationUtils.closeReservation(nonMonetoryBalance, gyServiceUnits);
            assertEquals(expected, nonMonetoryBalance.getReservationTime());
        }

        @Test
        public void doesNotSubtractReservationTimeFromNonMonetaryBalanceWhenReservationRequiredFlagIsFalse() {
            gyServiceUnits.setReservationRequired(false);
            RnCNonMonetaryBalance previousCopy = nonMonetoryBalance.copy();
            RnCNonMonetaryOperationUtils.closeReservation(nonMonetoryBalance, gyServiceUnits);
            ReflectionAssert.assertReflectionEquals(previousCopy, nonMonetoryBalance);
        }

        @Test
        public void doesNotSubtractReservationTimeFromNonMonetaryBalanceWhenGSUIsNull() {
            RnCNonMonetaryBalance previousCopy = nonMonetoryBalance.copy();
            RnCNonMonetaryOperationUtils.closeReservation(nonMonetoryBalance, null);
            ReflectionAssert.assertReflectionEquals(previousCopy, nonMonetoryBalance);
        }
    }

    public class Diff {

        private RnCNonMonetaryBalance previousBalance;
        private RnCNonMonetaryBalance currentBalance;

        @Before
        public void setUp() {
            previousBalance = createNonMonetaryBalance();
            currentBalance = previousBalance.copy();
        }

        @Test
        public void noDiffWhenAllParametersAreSame() {
            assertEquals(NO_DIFF, RnCNonMonetaryOperationUtils.diff(currentBalance, previousBalance));
        }

        public class ReservationDiffWhen {

            @Test
            public void timeReservationIsNotSame() {
                currentBalance.addReservationTime(1l);
                assertEquals(RESERVATION_DIFF, RnCNonMonetaryOperationUtils.diff(currentBalance, previousBalance));
            }
        }

        public class BalanceDiffWhen {

            @Test
            public void billingCycleTimeIsNotSame() {
                currentBalance.addBillingCycleAvailableTime(1l);
                assertEquals(BALANCE_DIFF, RnCNonMonetaryOperationUtils.diff(currentBalance, previousBalance));
            }

            @Test
            public void dailyTimeIsNotSame() {
                currentBalance.addDaily(1);
                assertEquals(BALANCE_DIFF, RnCNonMonetaryOperationUtils.diff(currentBalance, previousBalance));
            }

            @Test
            public void weeklyTimeIsNotSame() {
                currentBalance.addWeekly(1);
                assertEquals(BALANCE_DIFF, RnCNonMonetaryOperationUtils.diff(currentBalance, previousBalance));
            }

        }

        public class BalanceAndReservationDiffWhen {

            public class TimeReservationIsNotSame {

                @Before
                public void setUp() {
                    currentBalance.addReservationTime(1l);
                }

                @Test
                public void billingCycleTimeIsNotSame() {
                    currentBalance.addBillingCycleAvailableTime(1l);
                    assertEquals(BALANCE_AND_RESERVATION_DIFF, RnCNonMonetaryOperationUtils.diff(currentBalance, previousBalance));
                }

                @Test
                public void dailyTimeIsNotSame() {
                    currentBalance.addDaily(1);
                    assertEquals(BALANCE_AND_RESERVATION_DIFF, RnCNonMonetaryOperationUtils.diff(currentBalance, previousBalance));
                }

                @Test
                public void weeklyTimeIsNotSame() {
                    currentBalance.addWeekly(1);
                    assertEquals(BALANCE_AND_RESERVATION_DIFF, RnCNonMonetaryOperationUtils.diff(currentBalance, previousBalance));
                }
            }

        }

    }

    private GyServiceUnits createGSU(RnCNonMonetaryBalance nonMonetoryBalance) {
        return new GyServiceUnits(nonMonetoryBalance.getPackageId(),
                0,
                nonMonetoryBalance.getRatecardId(),
                nextLong(1, nonMonetoryBalance.getBillingCycleAvailable()),
                nonMonetoryBalance.getSubscriptionId(),
                0,
                nonMonetoryBalance.getId(),
                true,
                null,
                0d,
                0.0,60,0, 60,0,-1, 0);
    }

    private RnCNonMonetaryBalance createNonMonetaryBalance() {

        long billingCycleTotalTime = nextLong(2, 1000);
        return new RnCNonMonetaryBalance.RnCNonMonetaryBalanceBuilder(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                null,
                UUID.randomUUID().toString(), ResetBalanceStatus.NOT_RESET, null, ChargingType.SESSION).
                withBillingCycleTimeBalance(billingCycleTotalTime, nextLong(2, billingCycleTotalTime))
                .withDailyUsage(nextLong(2, billingCycleTotalTime))
                .withWeeklyUsage(nextLong(2, billingCycleTotalTime))
                .withDailyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .withWeeklyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(6))
                .build();
    }
}