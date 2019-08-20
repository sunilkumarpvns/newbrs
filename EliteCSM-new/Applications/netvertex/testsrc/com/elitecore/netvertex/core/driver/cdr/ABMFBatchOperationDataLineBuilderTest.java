package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.spr.ABMFBatchOperation;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ABMFBatchOperationDataLineBuilderTest {
    private static final String SUBSCRIPTION_ID = "subscription1";
    private static final String ID = "1";
    private static final long CONSTANT_TIME = 1504105520090l;
    private static final String PACKAGE_ID = "PACKAGE_ID";
    private static final String QUOTA_ID = "QUOTA_ID";
    private static final long SERVICE_ID = RandomUtils.nextInt(0, Integer.MAX_VALUE);
    private static final String SUBSCRIBER_IDENTITY = "007";
    private static final String DEFAULT_TIMESTAMP_FORMAT = "dd-MMM-yy hh.mm.ss.S a";
    private static final long RATING_GROUP_ID = 100;
    private static final int LEVEL = 1;


    private final SimpleDateFormat timestampFormat = new SimpleDateFormat(DEFAULT_TIMESTAMP_FORMAT);

    @Test
    public void test_csvLineBuilderForABMF_returns_expectedRecord() {

        TimeSource timeSource = new TimeSource() {

            @Override
            public long currentTimeInMillis() {
                return CONSTANT_TIME;
            }
        };

        ABMFBatchOperation.BatchOperationData batchOperationData = createBatchOperationData();
        List<String> actualCSVRecords = new ABMFCSVDataBuilder().getLineBuilder(timeSource).getCSVRecords(batchOperationData);

        List<String> expectedCSVRecord = createExpectedCSVRecord(timeSource,batchOperationData);

        System.out.println("Expected: " + expectedCSVRecord);
        System.out.println("Actual:   " + actualCSVRecords);

        ReflectionAssert.assertLenientEquals(expectedCSVRecord, actualCSVRecords);
    }

    private List<String> createExpectedCSVRecord(TimeSource timeSource, ABMFBatchOperation.BatchOperationData batchOperationData) {

        NonMonetoryBalance balance = batchOperationData.getNonMonitoryBalance();

        return Arrays.asList(balance.getId() + CommonConstants.COMMA +
                balance.getSubscriberIdentity() + CommonConstants.COMMA +
                balance.getPackageId() + CommonConstants.COMMA +
                balance.getSubscriptionId() + CommonConstants.COMMA +
                balance.getQuotaProfileId() + CommonConstants.COMMA +
                balance.getServiceId() + CommonConstants.COMMA +
                balance.getRatingGroupId() + CommonConstants.COMMA +
                balance.getLevel() + CommonConstants.COMMA +
                balance.getBillingCycleTotalVolume() + CommonConstants.COMMA +
                balance.getBillingCycleAvailableVolume() + CommonConstants.COMMA +
                balance.getBillingCycleTime() + CommonConstants.COMMA +
                balance.getBillingCycleAvailableTime() + CommonConstants.COMMA +
                balance.getDailyVolume() + CommonConstants.COMMA +
                balance.getDailyTime() + CommonConstants.COMMA +
                balance.getWeeklyVolume() + CommonConstants.COMMA +
                balance.getWeeklyTime() + CommonConstants.COMMA +
                balance.getDailyResetTime() + CommonConstants.COMMA +
                balance.getWeeklyResetTime() + CommonConstants.COMMA +
                balance.getBillingCycleResetTime()+ CommonConstants.COMMA +
                balance.getReservationVolume() + CommonConstants.COMMA +
                balance.getReservationTime() + CommonConstants.COMMA +
                batchOperationData.getOperation() + CommonConstants.COMMA
                + timestampFormat.format(new Timestamp(timeSource.currentTimeInMillis())));
    }

    private ABMFBatchOperation.BatchOperationData createBatchOperationData() {

        Random random = new Random();
        return new ABMFBatchOperation.BatchOperationData(
                new NonMonetoryBalance.NonMonetaryBalanceBuilder(ID, SERVICE_ID, PACKAGE_ID, RATING_GROUP_ID, SUBSCRIBER_IDENTITY, SUBSCRIPTION_ID, LEVEL, QUOTA_ID, ResetBalanceStatus.NOT_RESET, null, null)
                        .withBillingCycleVolumeBalance(random.nextInt(),random.nextInt())
                        .withBillingCycleTimeBalance(random.nextInt(),random.nextInt()).withBillingCycleResetTime(random.nextInt())
                        .withDailyUsage(random.nextInt(),random.nextInt()).withDailyResetTime(random.nextInt())
                        .withWeeklyUsage(random.nextInt(),random.nextInt()).withWeeklyResetTime(random.nextInt())
                        .withReservation(random.nextInt(),random.nextInt())
                        .build()
                , SUBSCRIBER_IDENTITY, ABMFBatchOperation.BatchOperation.REPORT);

    }
}
