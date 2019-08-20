package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.core.driverx.cdr.deprecated.BaseCSVDriver;
import com.elitecore.core.driverx.cdr.deprecated.CSVLineBuilderSupport;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.spr.ABMFBatchOperation;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class ABMFCSVDataBuilder implements CSVDataBuilder<ABMFBatchOperation.BatchOperationData> {
    private static final String CDRTIMESTAMP = "LAST_UPDATE_TIME";
    private static final String ABMF_CSV_HEADER = "ID, " +
            "SUBSCRIBER_ID, " +
            "PACKAGE_ID, " +
            "SUBSCRIPTION_ID, " +
            "QUOTA_PROFILE_ID, " +
            "DATA_SERVICE_TYPE_ID," +
            "RATING_GROUP_ID, " +
            "BALANCE_LEVEL, " +
            "BILLING_CYCLE_VOLUME, " +
            "BILLING_CYCLE_AVAIL_VOLUME,"+
            "BILLING_CYCLE_TIME, " +
            "BILLING_CYCLE_AVAIL_TIME, " +
            "DAILY_VOLUME, " +
            "DAILY_TIME," +
            "WEEKLY_TOTAL, " +
            "WEEKLY_TIME, " +
            "DAILY_RESET_TIME," +
            "WEEKLY_RESET_TIME, " +
            "QUOTA_EXPIRY_TIME, " +
            "RESERVATION_VOLUME," +
            "RESERVATION_TIME, " +
            "OPERATION_TYPE" +
            CommonConstants.COMMA + " " + CDRTIMESTAMP;

    @Override
    public String getHeader() {
        return ABMF_CSV_HEADER;
    }

    @Override
    public BaseCSVDriver.CSVLineBuilder<ABMFBatchOperation.BatchOperationData> getLineBuilder(TimeSource timeSource) {
        return new ABMFCSVDataBuilder.ABMFBatchOperationDataLineBuilder(",", new SimpleDateFormat(CommonConstants.DEFAULT_TIMESTAMP_FORMAT), timeSource);
    }

    public static class ABMFBatchOperationDataLineBuilder extends CSVLineBuilderSupport<ABMFBatchOperation.BatchOperationData> {

        public ABMFBatchOperationDataLineBuilder(String delimiter, SimpleDateFormat timestampFormat,
                                               TimeSource timeSource) {
            super(delimiter, Maps.newHashMap(), timestampFormat, timeSource);
        }

        @Override
        public List<String> getCSVRecords(ABMFBatchOperation.BatchOperationData batchOperationData) {

            StringBuilder builder = new StringBuilder();
            NonMonetoryBalance balance = batchOperationData.getNonMonitoryBalance();

            appendValue(builder, balance.getId());
            appendValue(builder, batchOperationData.getSubscriberIdentity());
            appendValue(builder, balance.getPackageId());
            appendValue(builder, balance.getSubscriptionId());
            appendValue(builder, balance.getQuotaProfileId());
            appendValue(builder, balance.getServiceId());
            appendValue(builder, balance.getRatingGroupId());
            appendValue(builder, balance.getLevel());

            appendValue(builder, balance.getBillingCycleTotalVolume());
            appendValue(builder, balance.getBillingCycleAvailableVolume());
            appendValue(builder, balance.getBillingCycleTime());
            appendValue(builder, balance.getBillingCycleAvailableTime());

            appendValue(builder, balance.getDailyVolume());
            appendValue(builder, balance.getDailyTime());

            appendValue(builder, balance.getWeeklyVolume());
            appendValue(builder, balance.getWeeklyTime());

            appendValue(builder, balance.getDailyResetTime());
            appendValue(builder, balance.getWeeklyResetTime());
            appendValue(builder, balance.getBillingCycleResetTime());

            appendValue(builder, balance.getReservationVolume());
            appendValue(builder, balance.getReservationTime());
            appendValue(builder, batchOperationData.getOperation().name());
            appendTimestamp(builder);

            return Arrays.asList(builder.toString());
        }

    }
}
