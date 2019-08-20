package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.core.driverx.cdr.deprecated.BaseCSVDriver;
import com.elitecore.core.driverx.cdr.deprecated.CSVLineBuilderSupport;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.spr.RnCABMFBatchOperation;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class RnCABMFCSVDataBuilder implements CSVDataBuilder<RnCABMFBatchOperation.BatchOperationData> {
    private static final String CDRTIMESTAMP = "LAST_UPDATE_TIME";
    private static final String ABMF_CSV_HEADER = "ID, " +
            "SUBSCRIBER_ID, " +
            "PACKAGE_ID, " +
            "SUBSCRIPTION_ID, " +
            "RATECARD_ID, " +
            "BALANCE_EXPIRY_TIME, " +
            "BILLING_CYCLE_TOTAL_TIME, " +
            "BILLING_CYCLE_AVAIL_TIME, " +
            "DAILY_RESET_TIME," +
            "DAILY_TIME_LIMIT," +
            "WEEKLY_RESET_TIME, " +
            "WEEKLY_TIME_LIMIT, " +
            "RESERVATION_TIME, " +
            "OPERATION_TYPE" +
            CommonConstants.COMMA + " " + CDRTIMESTAMP;

    @Override
    public String getHeader() {
        return ABMF_CSV_HEADER;
    }

    @Override
    public BaseCSVDriver.CSVLineBuilder<RnCABMFBatchOperation.BatchOperationData> getLineBuilder(TimeSource timeSource) {
        return new RnCABMFCSVDataBuilder.ABMFBatchOperationDataLineBuilder(",", new SimpleDateFormat(CommonConstants.DEFAULT_TIMESTAMP_FORMAT), timeSource);
    }

    public static class ABMFBatchOperationDataLineBuilder extends CSVLineBuilderSupport<RnCABMFBatchOperation.BatchOperationData> {

        public ABMFBatchOperationDataLineBuilder(String delimiter, SimpleDateFormat timestampFormat,
                                                 TimeSource timeSource) {
            super(delimiter, Maps.newHashMap(), timestampFormat, timeSource);
        }

        @Override
        public List<String> getCSVRecords(RnCABMFBatchOperation.BatchOperationData batchOperationData) {

            StringBuilder builder = new StringBuilder();
            RnCNonMonetaryBalance balance = batchOperationData.getRnCNonMonetaryBalance();

            appendValue(builder, balance.getId());
            appendValue(builder, batchOperationData.getSubscriberIdentity());
            appendValue(builder, balance.getPackageId());
            appendValue(builder, balance.getSubscriptionId());
            appendValue(builder, balance.getRatecardId());
            appendValue(builder, balance.getBalanceExpiryTime());
            appendValue(builder, balance.getBillingCycleTotal());
            appendValue(builder, balance.getBillingCycleAvailable());

            appendValue(builder, balance.getDailyResetTime());
            appendValue(builder, balance.getDailyLimit());
            appendValue(builder, balance.getWeeklyResetTime());
            appendValue(builder, balance.getWeeklyLimit());

            appendValue(builder, balance.getReservationTime());
            appendValue(builder, batchOperationData.getOperation().name());
            appendTimestamp(builder);

            return Arrays.asList(builder.toString());
        }

    }
}
