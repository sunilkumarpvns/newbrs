package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.MonetaryBalanceType;
import com.elitecore.corenetvertex.spr.ABMFOperationType;
import com.elitecore.corenetvertex.spr.MonetaryABMFOperationImpl;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class MonetaryABMFOperationDataLineBuilderTest {
    private static final String ID = "1";
    private static final long CONSTANT_TIME = 1504105520090l;
    private static final String PACKAGE_ID = "PACKAGE_ID";
    private static final String QUOTA_ID = "QUOTA_ID";
    private static final String SERVICE_ID = "SERVICE_ID";
    private static final String SUBSCRIBER_IDENTITY = "007";
    private static final String DEFAULT_TIMESTAMP_FORMAT = "dd-MMM-yy hh.mm.ss.S a";
    private static final String CURRENCY = "INR";

    private final SimpleDateFormat timestampFormat = new SimpleDateFormat(DEFAULT_TIMESTAMP_FORMAT);

    @Test
    public void test_csvLineBuilderForMonetaryABMF_returns_expectedRecord() {

        TimeSource timeSource = new TimeSource() {

            @Override
            public long currentTimeInMillis() {
                return CONSTANT_TIME;
            }
        };

        MonetaryABMFOperationImpl.MonetaryOperationData monetaryOperationData = createMonetaryOperationData();
        List<String> actualCSVRecords = new MonetaryABMFCSVDataBuilder().getLineBuilder(timeSource).getCSVRecords(monetaryOperationData);

        List<String> expectedCSVRecord = createExpectedCSVRecord(timeSource,monetaryOperationData);

        System.out.println("Expected: " + expectedCSVRecord);
        System.out.println("Actual:   " + actualCSVRecords);

        ReflectionAssert.assertLenientEquals(expectedCSVRecord, actualCSVRecords);
    }

    private List<String> createExpectedCSVRecord(TimeSource timeSource, MonetaryABMFOperationImpl.MonetaryOperationData monetaryOperationData) {
        List<String> expectedCSVRecords = new ArrayList<>();
        List<MonetaryBalance> monetaryBalances = monetaryOperationData.getMonetaryBalances();
        for (MonetaryBalance monetaryBalance : monetaryBalances) {
            expectedCSVRecords.add(monetaryBalance.getId() + CommonConstants.COMMA
                    + monetaryBalance.getSubscriberId() + CommonConstants.COMMA
                    + monetaryBalance.getServiceId() + CommonConstants.COMMA
                    + monetaryBalance.getAvailBalance() + CommonConstants.COMMA
                    + monetaryBalance.getInitialBalance() + CommonConstants.COMMA
                    + monetaryBalance.getTotalReservation() + CommonConstants.COMMA
                    + monetaryBalance.getValidFromDate() + CommonConstants.COMMA
                    + monetaryBalance.getValidToDate() + CommonConstants.COMMA
                    + monetaryBalance.getCurrency() + CommonConstants.COMMA
                    + monetaryOperationData.getOperation().name() + CommonConstants.COMMA
            + timestampFormat.format(new Timestamp(timeSource.currentTimeInMillis())));
        }

        return expectedCSVRecords;
    }

    private MonetaryABMFOperationImpl.MonetaryOperationData createMonetaryOperationData() {

        Random random = new Random();
        return new MonetaryABMFOperationImpl.MonetaryOperationData(
                Arrays.asList(new MonetaryBalance.MonetaryBalanceBuilder(ID, SUBSCRIBER_IDENTITY, SERVICE_ID, CURRENCY, MonetaryBalanceType.DEFAULT.name(), CONSTANT_TIME)
                        .withAvailableBalance(random.nextDouble())
                        .withTotalBalance(random.nextDouble())
                        .withTotalReservation(random.nextDouble())
                        .withValidFromDate(random.nextLong())
                        .withValidToDate(random.nextLong())
                        .build()), SUBSCRIBER_IDENTITY, ABMFOperationType.REPORT_AND_RESERVE);
    }


}
