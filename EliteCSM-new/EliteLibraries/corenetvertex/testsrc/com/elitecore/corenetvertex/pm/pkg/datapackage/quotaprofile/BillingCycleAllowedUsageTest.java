package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.apache.commons.lang3.RandomUtils.*;
import static org.junit.Assert.assertEquals;

@RunWith(HierarchicalContextRunner.class)
public class BillingCycleAllowedUsageTest {

    public static final int TOTAL = 10;
    public static final int DOWNLOAD = 20;
    public static final int UPLOAD = 30;
    public static final int TIME = 40;
    private BillingCycleAllowedUsage billingCycleAllowedUsage;

    @Before
    public void setUp() throws Exception {
        billingCycleAllowedUsage = new BillingCycleAllowedUsage(TOTAL, DOWNLOAD,UPLOAD, TIME,
        DataUnit.BYTE, DataUnit.BYTE, DataUnit.BYTE, TimeUnit.SECOND);
    }

    public class ValueCheck {

        @Test
        public void totalShouldGiveAssignedTotal() {
            assertEquals(TOTAL, billingCycleAllowedUsage.getTotal());
            assertEquals(TOTAL, billingCycleAllowedUsage.getTotalInBytes());
        }

        @Test
        public void uploadShouldGiveAssignedUpload() {
            assertEquals(UPLOAD, billingCycleAllowedUsage.getUpload());
            assertEquals(UPLOAD, billingCycleAllowedUsage.getUploadInBytes());
        }

        @Test
        public void downloadShouldGiveAssignedDownload() {
            assertEquals(DOWNLOAD, billingCycleAllowedUsage.getDownload());
            assertEquals(DOWNLOAD, billingCycleAllowedUsage.getDownloadInBytes());
        }

        @Test
        public void timeShouldGiveAssignedTime() {
            assertEquals(TIME, billingCycleAllowedUsage.getTime());
            assertEquals(TIME, billingCycleAllowedUsage.getTimeInSeconds());
        }
    }

    public class byteValueShouldBeUnlimitedWhenUnlimitedValuePassedInDataValue {

        @Before // passed Unit as GB
        public void setUp() throws Exception {
            billingCycleAllowedUsage = new BillingCycleAllowedUsage(CommonConstants.QUOTA_UNLIMITED, CommonConstants.QUOTA_UNLIMITED, CommonConstants.QUOTA_UNLIMITED, TIME,
                    DataUnit.GB, DataUnit.GB, DataUnit.GB, TimeUnit.SECOND);
        }

        @Test
        public void total() {
            assertEquals(CommonConstants.QUOTA_UNLIMITED, billingCycleAllowedUsage.getTotal());
            assertEquals(CommonConstants.QUOTA_UNLIMITED, billingCycleAllowedUsage.getTotalInBytes());
        }

        @Test
        public void upload() {
            assertEquals(CommonConstants.QUOTA_UNLIMITED, billingCycleAllowedUsage.getUpload());
            assertEquals(CommonConstants.QUOTA_UNLIMITED, billingCycleAllowedUsage.getUploadInBytes());
        }

        @Test
        public void download() {
            assertEquals(CommonConstants.QUOTA_UNLIMITED, billingCycleAllowedUsage.getDownload());
            assertEquals(CommonConstants.QUOTA_UNLIMITED, billingCycleAllowedUsage.getDownloadInBytes());
        }
    }

    public class secondValueShouldBeUnlimitedWhenUnlimitedValuePassedInTime {

        @Before
        public void setUp() throws Exception {
            billingCycleAllowedUsage = new BillingCycleAllowedUsage(TOTAL, DOWNLOAD,UPLOAD, CommonConstants.QUOTA_UNLIMITED,
                    DataUnit.BYTE, DataUnit.BYTE, DataUnit.BYTE, TimeUnit.MONTH);
        }

        @Test
        public void time() {
            assertEquals(CommonConstants.QUOTA_UNLIMITED, billingCycleAllowedUsage.getTime());
            assertEquals(CommonConstants.QUOTA_UNLIMITED, billingCycleAllowedUsage.getTimeInSeconds());
        }
    }

    public class GetBalanceCheck {

        private Calendar currentTime;
        private Calendar pastTime;
        private Calendar futureTime;

        @Before
        public void before() {
            currentTime = new GregorianCalendar();
            futureTime = new Calendar.Builder().setInstant(currentTime.getTimeInMillis() + 1).build();
            pastTime = new Calendar.Builder().setInstant(currentTime.getTimeInMillis() - 1).build();
        }

        @Test
        public void shouldReturnSameBalanceAsassignedWhenSubscriberUsageIsNull() {
            BillingCycleBalance balance = new BillingCycleBalance(TOTAL, DOWNLOAD,UPLOAD, TIME);
            SubscriberUsage subscriberUsage = null;
            assertEquals(balance, billingCycleAllowedUsage.getBalance(subscriberUsage, currentTime));
        }

        @Test
        public void shouldReturnSameBalanceAsAssignedWhenServiceRgBalanceIsNull() {
            BillingCycleBalance expectedBalance = new BillingCycleBalance(TOTAL, DOWNLOAD,UPLOAD, TIME);
            NonMonetoryBalance serviceRgNonMonitoryBalance = null;
            assertEquals(expectedBalance, billingCycleAllowedUsage.getBalance(serviceRgNonMonitoryBalance, currentTime));
        }


        @Test
        public void shouldReturnSameConfiguredBalanceWhenSubscriberUsagePassedAndBillingCycleReached() {
            BillingCycleBalance expectedBalance = new BillingCycleBalance(TOTAL, DOWNLOAD,UPLOAD, TIME);
            SubscriberUsage subscriberUsage= createSubscriberUsageWith(11,22,33,44, pastTime);
            assertEquals(expectedBalance, billingCycleAllowedUsage.getBalance(subscriberUsage, currentTime));
        }

        private SubscriberUsage createSubscriberUsageWith(long total, long download, long upload, long time, Calendar calendar) {
            return new SubscriberUsage.SubscriberUsageBuilder("id",
                    "subscriberId",
                    "serviceId",
                    "quotaId",
                    "packageId","productOfferId")
                    .withBillingCycleUsage(total, download, upload, time)
                    .withBillingCycleResetTime(calendar.getTimeInMillis()).build();
        }

        @Test
        public void returnBalanceAfterUsageDeductionWhenSubscriberUsagePassedAndBillingCycleNotReached() {
            BalanceImpl expectedBalance = new BalanceImpl(4, 9,14, 19);
            SubscriberUsage subscriberUsage = createSubscriberUsageWith(6,11,16,21, futureTime);
            assertEquals(expectedBalance, billingCycleAllowedUsage.getBalance(subscriberUsage, currentTime));
        }

        @Test
        public void returnConfiguredQuotaAsBalanceWhenServiceRgNonMonitoryBalanceAndBillingCycleNotReached() {
            BillingCycleBalance expectedBalance = new BillingCycleBalance(TOTAL, DOWNLOAD,UPLOAD, TIME);
            NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRgBalanceWith(RandomUtils.nextLong(),RandomUtils.nextLong(), RandomUtils.nextLong(), RandomUtils.nextLong(), pastTime);
            ReflectionAssert.assertReflectionEquals(expectedBalance, billingCycleAllowedUsage.getBalance(serviceRgNonMonitoryBalance, currentTime));
        }


        public class returnActualMinusReservationAsBalanceWhenServiceRgNonMonitoryBalanceAndBillingCycleReached {

            private int availableVolume = RandomUtils.nextInt(Integer.MAX_VALUE);
            private int reserveVolume = RandomUtils.nextInt(Integer.MAX_VALUE);
            private int availableTime = RandomUtils.nextInt(availableVolume);
            private int reserveTime = RandomUtils.nextInt(availableTime);

            private NonMonetoryBalance serviceRgNonMonitoryBalance;

            @Before
            public void setUp() {
                serviceRgNonMonitoryBalance = createServiceRgBalanceWith(availableVolume,
                        reserveVolume,
                        availableTime,
                        reserveTime,
                        futureTime);
            }

            @Test
            public void forTotalVolume() {
                BillingCycleAllowedUsage billingCycleAllowedUsage = new BillingCycleAllowedUsage(TOTAL, CommonConstants.QUOTA_UNDEFINED,CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNLIMITED,
                        DataUnit.BYTE, DataUnit.BYTE, DataUnit.BYTE, TimeUnit.MONTH);
                BillingCycleBalance expectedBalance = new BillingCycleBalance(availableVolume - reserveVolume, CommonConstants.QUOTA_UNDEFINED,CommonConstants.QUOTA_UNDEFINED, availableTime - reserveTime);
                ReflectionAssert.assertReflectionEquals(expectedBalance, billingCycleAllowedUsage.getBalance(serviceRgNonMonitoryBalance, currentTime));
            }

            @Test
            public void forUploadVolume() {
                BillingCycleAllowedUsage billingCycleAllowedUsage = new BillingCycleAllowedUsage(CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED,UPLOAD, CommonConstants.QUOTA_UNLIMITED,
                        DataUnit.BYTE, DataUnit.BYTE, DataUnit.BYTE, TimeUnit.MONTH);
                BillingCycleBalance expectedBalance = new BillingCycleBalance(CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED,availableVolume - reserveVolume, availableTime - reserveTime);
                ReflectionAssert.assertReflectionEquals(expectedBalance, billingCycleAllowedUsage.getBalance(serviceRgNonMonitoryBalance, currentTime));
            }

            @Test
            public void forDownloadVolume() {
                BillingCycleAllowedUsage billingCycleAllowedUsage = new BillingCycleAllowedUsage(CommonConstants.QUOTA_UNDEFINED, DOWNLOAD,CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED,
                        DataUnit.BYTE, DataUnit.BYTE, DataUnit.BYTE, TimeUnit.MONTH);
                BillingCycleBalance expectedBalance = new BillingCycleBalance(CommonConstants.QUOTA_UNDEFINED, availableVolume - reserveVolume,CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED);
                ReflectionAssert.assertReflectionEquals(expectedBalance, billingCycleAllowedUsage.getBalance(serviceRgNonMonitoryBalance, currentTime));
            }

            @Test
            public void forTime() {
                BillingCycleAllowedUsage billingCycleAllowedUsage = new BillingCycleAllowedUsage(CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED,UPLOAD, TIME,
                        DataUnit.BYTE, DataUnit.BYTE, DataUnit.BYTE, TimeUnit.MONTH);
                BillingCycleBalance expectedBalance = new BillingCycleBalance(CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED,availableVolume - reserveVolume, availableTime - reserveTime);
                ReflectionAssert.assertReflectionEquals(expectedBalance, billingCycleAllowedUsage.getBalance(serviceRgNonMonitoryBalance, currentTime));
            }
        }





        private NonMonetoryBalance createServiceRgBalanceWith(long volume, long reservationVolume, long time, long reservationTime, Calendar calendar) {
            return new NonMonetoryBalance.NonMonetaryBalanceBuilder("id",
                    nextInt(0, Integer.MAX_VALUE),
                    "packageId",
                    1l,
                    "subscriberIdentity",
                    "subscriptionId",
                    0,
            "quotaProfileId", ResetBalanceStatus.NOT_RESET, null, null)
                    .withBillingCycleVolumeBalance(CommonConstants.QUOTA_UNLIMITED, volume)
                    .withReservation(reservationVolume, reservationTime)
                    .withBillingCycleTimeBalance(CommonConstants.QUOTA_UNLIMITED, time)
                    .withBillingCycleResetTime(calendar.getTimeInMillis()).build();
        }

    }


    @Test
    public void getDownload() throws Exception {

    }
}