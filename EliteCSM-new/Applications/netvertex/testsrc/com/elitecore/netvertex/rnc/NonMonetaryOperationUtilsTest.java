package com.elitecore.netvertex.rnc;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
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
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class NonMonetaryOperationUtilsTest {

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
        private NonMonetoryBalance nonMonetoryBalance;
        @Before
        public void setUp() {
            nonMonetoryBalance = createNonMonetaryBalance();
            LogManager.getLogger().debug(NonMonetaryOperationUtilsTest.class.getSimpleName(), "Non-Monetary Balance is " + nonMonetoryBalance);
        }


        @Test
        public void setNewDailyResetTimeWhenUsageResetTimeIsInPreviousDayAndSessionStartedInCurrentDay() {
            long dailyResetTime = calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(1);
            nonMonetoryBalance.setDailyResetTime(dailyResetTime);

            LogManager.getLogger().debug(NonMonetaryOperationUtilsTest.class.getSimpleName(), "Session Start time is " + pcrfRequest.getSessionStartTime());

            NonMonetaryOperationUtils.subtract(nonMonetoryBalance,
                    nextLong(1, nonMonetoryBalance.getBillingCycleAvailableVolume()),
                    nextLong(1, nonMonetoryBalance.getBillingCycleAvailableTime()),
                    executionContext);

            long expected = dailyResetTime + TimeUnit.DAYS.toMillis(1);
            assertEquals(expected, nonMonetoryBalance.getDailyResetTime());
        }


        @Test
        public void setNewWeeklyResetTimeWhenUsageResetTimeIsInPreviousWeekAndSessionStartedInCurrentWeek() {
            long weeklyResetTime = calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(7);
            nonMonetoryBalance.setWeeklyResetTime(weeklyResetTime);

            LogManager.getLogger().debug(NonMonetaryOperationUtilsTest.class.getSimpleName(), "Session Start time is " + pcrfRequest.getSessionStartTime());

            NonMonetaryOperationUtils.subtract(nonMonetoryBalance,
                    nextLong(1, nonMonetoryBalance.getBillingCycleAvailableVolume()),
                    nextLong(1, nonMonetoryBalance.getBillingCycleAvailableTime()),
                    executionContext);

            long expected = weeklyResetTime + TimeUnit.DAYS.toMillis(7);
            assertEquals(expected, nonMonetoryBalance.getWeeklyResetTime());
        }

        public class Volume {
            private long deductabledVolume;

            @Before
            public void setUp() {
                deductabledVolume = nextLong(1,nonMonetoryBalance.getBillingCycleAvailableVolume());
                LogManager.getLogger().debug(NonMonetaryOperationUtilsTest.class.getSimpleName(), "Deductable Volume is " + deductabledVolume);
            }


            @Test
            public void subtractionFromBillingCycle() {
                long expected = nonMonetoryBalance.getBillingCycleAvailableVolume() - deductabledVolume;
                NonMonetaryOperationUtils.subtract(nonMonetoryBalance, deductabledVolume, 0, executionContext);
                assertEquals(expected, nonMonetoryBalance.getBillingCycleAvailableVolume());
            }

            @Test
            public void addInToDailyVolumeWhenUsageResetTimeIsInCurrentWeek() {
                long expected = nonMonetoryBalance.getDailyVolume() + deductabledVolume;
                NonMonetaryOperationUtils.subtract(nonMonetoryBalance, deductabledVolume, 0, executionContext);
                assertEquals(expected, nonMonetoryBalance.getDailyVolume());

            }

            @Test
            public void addInToWeeklyVolumeWhenUsageResetTimeIsInCurrentWeek() {
                long expected = nonMonetoryBalance.getWeeklyVolume() + deductabledVolume;
                NonMonetaryOperationUtils.subtract(nonMonetoryBalance, deductabledVolume, 0, executionContext);
                assertEquals(expected, nonMonetoryBalance.getWeeklyVolume());

            }

            @Test
            public void resetDailyVolumeWhenUsageResetTimeIsInPreviousDayAndSessionStartedIsNotStartedInCurrentDay() {
                nonMonetoryBalance.setDailyResetTime(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(1));

                pcrfRequest.setSessionStartTime(new Date(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(1)));

                LogManager.getLogger().debug(NonMonetaryOperationUtilsTest.class.getSimpleName(), "Session Start time is " + pcrfRequest.getSessionStartTime());

                NonMonetaryOperationUtils.subtract(nonMonetoryBalance, deductabledVolume, 0, executionContext);
                assertEquals(0, nonMonetoryBalance.getDailyVolume());
            }

            @Test
            public void resetWeeklyVolumeWhenUsageResetTimeIsInPreviousWeekAndSessionStartedIsNotStartedInCurrentWeek() {

                nonMonetoryBalance.setWeeklyResetTime(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(7));

                pcrfRequest.setSessionStartTime(new Date(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(7)));

                LogManager.getLogger().debug(NonMonetaryOperationUtilsTest.class.getSimpleName(), "Session Start time is " + pcrfRequest.getSessionStartTime());

                NonMonetaryOperationUtils.subtract(nonMonetoryBalance, deductabledVolume, 0, executionContext);
                assertEquals(0, nonMonetoryBalance.getWeeklyVolume());
            }

            @Test
            public void resetWeeklyVolumeWhenUsageResetTimeIsInPreviousWeekAndSessionStartedIsInCurrentWeek() {

                nonMonetoryBalance.setWeeklyResetTime(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(7));

                pcrfRequest.setSessionStartTime(new Date(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(7)));

                LogManager.getLogger().debug(NonMonetaryOperationUtilsTest.class.getSimpleName(), "Session Start time is " + pcrfRequest.getSessionStartTime());

                NonMonetaryOperationUtils.subtract(nonMonetoryBalance, deductabledVolume, 0, executionContext);
                assertEquals(0, nonMonetoryBalance.getWeeklyVolume());
            }

            @Test
            public void setDeductedVolumeAsDailyVolumeWhenUsageResetTimeIsInPreviousDayAndSessionStartedInCurrentDay() {
                long dailyResetTime = calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(1);
                nonMonetoryBalance.setDailyResetTime(dailyResetTime);

                LogManager.getLogger().debug(NonMonetaryOperationUtilsTest.class.getSimpleName(), "Session Start time is " + pcrfRequest.getSessionStartTime());

                NonMonetaryOperationUtils.subtract(nonMonetoryBalance, deductabledVolume, 0, executionContext);

                assertEquals(deductabledVolume, nonMonetoryBalance.getDailyVolume());
            }


            @Test
            public void setDeductabledVolumeAsWeeklVolumeWhenUsageResetTimeIsInPreviousWeekAndSessionStartedInCurrentWeek() {
                long weeklyResetTime = calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(7);
                nonMonetoryBalance.setWeeklyResetTime(weeklyResetTime);

                LogManager.getLogger().debug(NonMonetaryOperationUtilsTest.class.getSimpleName(), "Session Start time is " + pcrfRequest.getSessionStartTime());

                NonMonetaryOperationUtils.subtract(nonMonetoryBalance, deductabledVolume, 0, executionContext);

                assertEquals(deductabledVolume, nonMonetoryBalance.getWeeklyVolume());
            }

        }

        public class Time {

            private long deductabledTime;


            @Before
            public void setUp() {
                deductabledTime = nextLong(1,nonMonetoryBalance.getBillingCycleAvailableVolume());
                LogManager.getLogger().debug(NonMonetaryOperationUtilsTest.class.getSimpleName(), "Deductable Time is " + deductabledTime);
            }

            @Test
            public void subtractionFromBillingCycle() {
                long expected = nonMonetoryBalance.getBillingCycleAvailableTime() - deductabledTime;
                NonMonetaryOperationUtils.subtract(nonMonetoryBalance, 0, deductabledTime, executionContext);
                assertEquals(expected, nonMonetoryBalance.getBillingCycleAvailableTime());
            }

            @Test
            public void addInToDailyVolumeWhenUsageResetTimeIsInCurrentWeek() {
                long expected = nonMonetoryBalance.getDailyTime() + deductabledTime;
                NonMonetaryOperationUtils.subtract(nonMonetoryBalance, 0, deductabledTime, executionContext);
                assertEquals(expected, nonMonetoryBalance.getDailyTime());

            }

            @Test
            public void addInToWeeklyVolumeWhenUsageResetTimeIsInCurrentWeek() {
                long expected = nonMonetoryBalance.getWeeklyTime() + deductabledTime;
                NonMonetaryOperationUtils.subtract(nonMonetoryBalance, 0, deductabledTime, executionContext);
                assertEquals(expected, nonMonetoryBalance.getWeeklyTime());

            }

            @Test
            public void resetDailyVolumeWhenUsageResetTimeIsInPreviousDayAndSessionStartedIsNotStartedInCurrentDay() {
                nonMonetoryBalance.setDailyResetTime(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(1));

                pcrfRequest.setSessionStartTime(new Date(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(1)));

                LogManager.getLogger().debug(NonMonetaryOperationUtilsTest.class.getSimpleName(), "Session Start time is " + pcrfRequest.getSessionStartTime());

                NonMonetaryOperationUtils.subtract(nonMonetoryBalance, 0, deductabledTime, executionContext);
                assertEquals(0, nonMonetoryBalance.getDailyTime());
            }

            @Test
            public void resetWeeklyVolumeWhenUsageResetTimeIsInPreviousWeekAndSessionStartedIsNotStartedInCurrentWeek() {

                nonMonetoryBalance.setWeeklyResetTime(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(7));

                pcrfRequest.setSessionStartTime(new Date(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(7)));

                LogManager.getLogger().debug(NonMonetaryOperationUtilsTest.class.getSimpleName(), "Session Start time is " + pcrfRequest.getSessionStartTime());

                NonMonetaryOperationUtils.subtract(nonMonetoryBalance, 0, deductabledTime,  executionContext);
                assertEquals(0, nonMonetoryBalance.getWeeklyTime());
            }

            @Test
            public void resetWeeklyVolumeWhenUsageResetTimeIsInPreviousWeekAndSessionStartedIsInCurrentWeek() {

                nonMonetoryBalance.setWeeklyResetTime(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(7));

                pcrfRequest.setSessionStartTime(new Date(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(7)));

                LogManager.getLogger().debug(NonMonetaryOperationUtilsTest.class.getSimpleName(), "Session Start time is " + pcrfRequest.getSessionStartTime());

                NonMonetaryOperationUtils.subtract(nonMonetoryBalance, 0, deductabledTime, executionContext);
                assertEquals(0, nonMonetoryBalance.getWeeklyTime());
            }

            @Test
            public void setDeductedVolumeAsDailyVolumeWhenUsageResetTimeIsInPreviousDayAndSessionStartedInCurrentDay() {
                long dailyResetTime = calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(1);
                nonMonetoryBalance.setDailyResetTime(dailyResetTime);

                LogManager.getLogger().debug(NonMonetaryOperationUtilsTest.class.getSimpleName(), "Session Start time is " + pcrfRequest.getSessionStartTime());

                NonMonetaryOperationUtils.subtract(nonMonetoryBalance, 0, deductabledTime, executionContext);

                assertEquals(deductabledTime, nonMonetoryBalance.getDailyTime());
            }


            @Test
            public void setDeductabledVolumeAsWeeklVolumeWhenUsageResetTimeIsInPreviousWeekAndSessionStartedInCurrentWeek() {
                long weeklyResetTime = calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(7);
                nonMonetoryBalance.setWeeklyResetTime(weeklyResetTime);

                LogManager.getLogger().debug(NonMonetaryOperationUtilsTest.class.getSimpleName(), "Session Start time is " + pcrfRequest.getSessionStartTime());

                NonMonetaryOperationUtils.subtract(nonMonetoryBalance,  0, deductabledTime, executionContext);

                assertEquals(deductabledTime, nonMonetoryBalance.getWeeklyTime());
            }

        }

        @After
        public void logDeductedNonMonetaryBalance() {
            LogManager.getLogger().debug(NonMonetaryOperationUtilsTest.class.getSimpleName(), "Non-Monetary Balance after subtraction is " + nonMonetoryBalance);
        }


    }

    public class CloseReservation {
        private NonMonetoryBalance nonMonetoryBalance;
        private GyServiceUnits gyServiceUnits;

        @Before
        public void setUp() {
            nonMonetoryBalance = createNonMonetaryBalance();
            gyServiceUnits = createGSU(nonMonetoryBalance);
        }

        @Test
        public void subtractReservationVolumeFromNonMonetaryBalanceWhenReservationRequiredFlagIsTrueAndGSUIsNotNull() {
            long expected = nonMonetoryBalance.getReservationVolume() - gyServiceUnits.getVolume();
            NonMonetaryOperationUtils.closeReservation(nonMonetoryBalance, gyServiceUnits);
            assertEquals(expected, nonMonetoryBalance.getReservationVolume());
        }

        @Test
        public void subtractReservationTimeFromNonMonetaryBalanceWhenReservationRequiredFlagIsTrueAndGSUIsNotNull() {
            long expected = nonMonetoryBalance.getReservationTime() - gyServiceUnits.getTime();
            NonMonetaryOperationUtils.closeReservation(nonMonetoryBalance, gyServiceUnits);
            assertEquals(expected, nonMonetoryBalance.getReservationTime());
        }

        @Test
        public void doesNotSubtractReservationTimeFromNonMonetaryBalanceWhenReservationRequiredFlagIsFalse() {
            gyServiceUnits.setReservationRequired(false);
            NonMonetoryBalance previousCopy = nonMonetoryBalance.copy();
            NonMonetaryOperationUtils.closeReservation(nonMonetoryBalance, gyServiceUnits);
            ReflectionAssert.assertReflectionEquals(previousCopy, nonMonetoryBalance);
        }

        @Test
        public void doesNotSubtractReservationTimeFromNonMonetaryBalanceWhenGSUIsNull() {
            NonMonetoryBalance previousCopy = nonMonetoryBalance.copy();
            NonMonetaryOperationUtils.closeReservation(nonMonetoryBalance, null);
            ReflectionAssert.assertReflectionEquals(previousCopy, nonMonetoryBalance);
        }
    }

    public class Diff {

        private NonMonetoryBalance previousBalance;
        private NonMonetoryBalance currentBalance;

        @Before
        public void setUp() {
            previousBalance = createNonMonetaryBalance();
            currentBalance = previousBalance.copy();
        }

        @Test
        public void noDiffWhenAllParametersAreSame() {
            assertEquals(NO_DIFF, NonMonetaryOperationUtils.diff(currentBalance, previousBalance));
        }

        public class ReservationDiffWhen {

            @Test
            public void volumeReservationIsNotSame() {
                currentBalance.addReservationVolume(1l);
                assertEquals(RESERVATION_DIFF, NonMonetaryOperationUtils.diff(currentBalance, previousBalance));
            }

            @Test
            public void timeReservationIsNotSame() {
                currentBalance.addReservationTime(1l);
                assertEquals(RESERVATION_DIFF, NonMonetaryOperationUtils.diff(currentBalance, previousBalance));
            }
        }

        public class BalanceDiffWhen {

            @Test
            public void billingCycleVolumeIsNotSame() {
                currentBalance.addBillingCycleAvailableVolume(1l);
                assertEquals(BALANCE_DIFF, NonMonetaryOperationUtils.diff(currentBalance, previousBalance));
            }

            @Test
            public void billingCycleTimeIsNotSame() {
                currentBalance.addBillingCycleAvailableTime(1l);
                assertEquals(BALANCE_DIFF, NonMonetaryOperationUtils.diff(currentBalance, previousBalance));
            }

            @Test
            public void dailyVolumeIsNotSame() {
                currentBalance.addDaily(1l, 0);
                assertEquals(BALANCE_DIFF, NonMonetaryOperationUtils.diff(currentBalance, previousBalance));
            }

            @Test
            public void dailyTimeIsNotSame() {
                currentBalance.addDaily(0, 1);
                assertEquals(BALANCE_DIFF, NonMonetaryOperationUtils.diff(currentBalance, previousBalance));
            }

            @Test
            public void weeklyVolumeIsNotSame() {
                currentBalance.addWeekly(1l, 0);
                assertEquals(BALANCE_DIFF, NonMonetaryOperationUtils.diff(currentBalance, previousBalance));
            }

            @Test
            public void weeklyTimeIsNotSame() {
                currentBalance.addWeekly(0, 1);
                assertEquals(BALANCE_DIFF, NonMonetaryOperationUtils.diff(currentBalance, previousBalance));
            }

        }

        public class BalanceAndReservationDiffWhen {



            public class VolumeReservationIsNotSame {

                @Before
                public void setUp() {
                    currentBalance.addReservationVolume(1l);
                }

                @Test
                public void billingCycleVolumeIsNotSame() {
                    currentBalance.addBillingCycleAvailableVolume(1l);
                    assertEquals(BALANCE_AND_RESERVATION_DIFF, NonMonetaryOperationUtils.diff(currentBalance, previousBalance));
                }

                @Test
                public void billingCycleTimeIsNotSame() {
                    currentBalance.addBillingCycleAvailableTime(1l);
                    assertEquals(BALANCE_AND_RESERVATION_DIFF, NonMonetaryOperationUtils.diff(currentBalance, previousBalance));
                }

                @Test
                public void dailyVolumeIsNotSame() {
                    currentBalance.addDaily(1l, 0);
                    assertEquals(BALANCE_AND_RESERVATION_DIFF, NonMonetaryOperationUtils.diff(currentBalance, previousBalance));
                }

                @Test
                public void dailyTimeIsNotSame() {
                    currentBalance.addDaily(0, 1);
                    assertEquals(BALANCE_AND_RESERVATION_DIFF, NonMonetaryOperationUtils.diff(currentBalance, previousBalance));
                }

                @Test
                public void weeklyVolumeIsNotSame() {
                    currentBalance.addWeekly(1l, 0);
                    assertEquals(BALANCE_AND_RESERVATION_DIFF, NonMonetaryOperationUtils.diff(currentBalance, previousBalance));
                }

                @Test
                public void weeklyTimeIsNotSame() {
                    currentBalance.addWeekly(0, 1);
                    assertEquals(BALANCE_AND_RESERVATION_DIFF, NonMonetaryOperationUtils.diff(currentBalance, previousBalance));
                }
            }

            public class TimeReservationIsNotSame {

                @Before
                public void setUp() {
                    currentBalance.addReservationTime(1l);
                }

                @Test
                public void billingCycleVolumeIsNotSame() {
                    currentBalance.addBillingCycleAvailableVolume(1l);
                    assertEquals(BALANCE_AND_RESERVATION_DIFF, NonMonetaryOperationUtils.diff(currentBalance, previousBalance));
                }

                @Test
                public void billingCycleTimeIsNotSame() {
                    currentBalance.addBillingCycleAvailableTime(1l);
                    assertEquals(BALANCE_AND_RESERVATION_DIFF, NonMonetaryOperationUtils.diff(currentBalance, previousBalance));
                }

                @Test
                public void dailyVolumeIsNotSame() {
                    currentBalance.addDaily(1l, 0);
                    assertEquals(BALANCE_AND_RESERVATION_DIFF, NonMonetaryOperationUtils.diff(currentBalance, previousBalance));
                }

                @Test
                public void dailyTimeIsNotSame() {
                    currentBalance.addDaily(0, 1);
                    assertEquals(BALANCE_AND_RESERVATION_DIFF, NonMonetaryOperationUtils.diff(currentBalance, previousBalance));
                }

                @Test
                public void weeklyVolumeIsNotSame() {
                    currentBalance.addWeekly(1l, 0);
                    assertEquals(BALANCE_AND_RESERVATION_DIFF, NonMonetaryOperationUtils.diff(currentBalance, previousBalance));
                }

                @Test
                public void weeklyTimeIsNotSame() {
                    currentBalance.addWeekly(0, 1);
                    assertEquals(BALANCE_AND_RESERVATION_DIFF, NonMonetaryOperationUtils.diff(currentBalance, previousBalance));
                }
            }

        }

    }

    private GyServiceUnits createGSU(NonMonetoryBalance nonMonetoryBalance) {
        return new GyServiceUnits(nonMonetoryBalance.getPackageId(),
                nextLong(1, nonMonetoryBalance.getBillingCycleAvailableVolume()),
                nonMonetoryBalance.getQuotaProfileId(),
                nextLong(1, nonMonetoryBalance.getBillingCycleAvailableTime()),
                nonMonetoryBalance.getSubscriptionId(),
                0,
                nonMonetoryBalance.getId(),
                true,
                null,
                0d,
                0.0,0,0, 0,0,-1, nonMonetoryBalance.getLevel());
    }

    private NonMonetoryBalance createNonMonetaryBalance() {


        long billingCycleTotalVolume = nextLong(2, 1000);
        long billingCycleTotalTime = nextLong(2, 1000);
        return new NonMonetoryBalance.NonMonetaryBalanceBuilder(
                UUID.randomUUID().toString(),
                nextLong(1,1000),
                UUID.randomUUID().toString(),
                nextLong(1,1000),
                UUID.randomUUID().toString(),
                null,
                nextInt(0,3),
                UUID.randomUUID().toString(), ResetBalanceStatus.NOT_RESET, null, null).
                withBillingCycleVolumeBalance(billingCycleTotalVolume, nextLong(2, billingCycleTotalVolume)).
                withBillingCycleTimeBalance(billingCycleTotalTime, nextLong(2, billingCycleTotalTime))
                .withDailyUsage(nextLong(2, billingCycleTotalVolume), nextLong(2, billingCycleTotalTime))
                .withWeeklyUsage(nextLong(2, billingCycleTotalVolume), nextLong(2, billingCycleTotalTime))
                .withDailyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .withWeeklyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(6))
                .build();
    }

}