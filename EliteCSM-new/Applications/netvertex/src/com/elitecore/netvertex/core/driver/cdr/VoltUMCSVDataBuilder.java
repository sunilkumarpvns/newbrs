package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.core.driverx.cdr.deprecated.BaseCSVDriver;
import com.elitecore.core.driverx.cdr.deprecated.CSVLineBuilderSupport;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.voltdb.VoltUMOperation;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class VoltUMCSVDataBuilder implements CSVDataBuilder<VoltUMOperation.VoltUMOperationData> {
    private static final String CDRTIMESTAMP = "CDRTIMESTAMP";

    private static final String VOLT_UM_CSV_HEADER = "ID, SUBSCRIBERID, PACKAGEID, SUBSCRIPTIONID, QUOTAPROFILEID, SERVICEID"
            + ", BILLINGCYCLETOTAL, BILLINGCYCLEUPLOAD, BILLINGCYCLEDOWNLOAD, BILLINGCYCLETIME"
            + ", DAILYTOTAL, DAILYUPLOAD, DAILYDOWNLOAD, DAILYTIME, WEEKLYTOTAL, WEEKLYUPLOAD, WEEKLYDOWNLOAD, WEEKLYTIME"
            + ", CUSTOMTOTAL, CUSTOMUPLOAD, CUSTOMDOWNLOAD, CUSTOMTIME"
            + ", BILLINGCYCLERESETTIME, DAILYRESETTIME, WEEKLYRESETTIME, CUSTOMRESETTIME, OPERATIONTYPE" + CommonConstants.COMMA + " " + CDRTIMESTAMP;

    @Override
    public String getHeader() {
        return VOLT_UM_CSV_HEADER;
    }

    @Override
    public BaseCSVDriver.CSVLineBuilder<VoltUMOperation.VoltUMOperationData> getLineBuilder(TimeSource timeSource) {
        return new VoltUMOperationDataLineBuilder(",", new SimpleDateFormat(CommonConstants.DEFAULT_TIMESTAMP_FORMAT), timeSource);
    }


    public static class VoltUMOperationDataLineBuilder extends CSVLineBuilderSupport<VoltUMOperation.VoltUMOperationData> {

        private static final String REPLACE = "REPLACE";
        private static final String ADD_TO_EXISTING = "ADD_TO_EXISTING";
        private static final String INSERT = "INSERT";

        public VoltUMOperationDataLineBuilder(String delimiter, SimpleDateFormat timestampFormat,
                                               TimeSource timeSource) {
            super(delimiter, Maps.newHashMap(), timestampFormat, timeSource);
        }

        @Override
        public List<String> getCSVRecords(VoltUMOperation.VoltUMOperationData voltUMOperationData) {

            Collection<SubscriberUsage> usages = voltUMOperationData.getUsages();
            String[] records = new String[usages.size()];

            int i=0;
            for (SubscriberUsage usage : usages) {
                StringBuilder builder = new StringBuilder();

                appendValue(builder, usage.getId());
                appendValue(builder, voltUMOperationData.getSubscriberIdentity());
                appendValue(builder, usage.getPackageId());
                appendValue(builder, usage.getSubscriptionId());
                appendValue(builder, usage.getQuotaProfileId());
                appendValue(builder, usage.getServiceId());

                appendValue(builder, usage.getBillingCycleTotal());
                appendValue(builder, usage.getBillingCycleUpload());
                appendValue(builder, usage.getBillingCycleDownload());
                appendValue(builder, usage.getBillingCycleTime());

                appendValue(builder, usage.getDailyTotal());
                appendValue(builder, usage.getDailyUpload());
                appendValue(builder, usage.getDailyDownload());
                appendValue(builder, usage.getDailyTime());

                appendValue(builder, usage.getWeeklyTotal());
                appendValue(builder, usage.getWeeklyUpload());
                appendValue(builder, usage.getWeeklyDownload());
                appendValue(builder, usage.getWeeklyTime());

                appendValue(builder, usage.getCustomTotal());
                appendValue(builder, usage.getCustomUpload());
                appendValue(builder, usage.getCustomDownload());
                appendValue(builder, usage.getCustomTime());

                appendValue(builder, usage.getBillingCycleResetTime());
                appendValue(builder, usage.getDailyResetTime());
                appendValue(builder, usage.getWeeklyResetTime());
                appendValue(builder, usage.getCustomResetTime());
                addOperationType(voltUMOperationData, builder);

                appendTimestamp(builder);

                records[i++] = builder.toString();
            }

            return Arrays.asList(records);
        }

        private void addOperationType(VoltUMOperation.VoltUMOperationData voltUMOperationData, StringBuilder builder) {

            int operation = voltUMOperationData.getOperation();

            if (operation == 0) {
                appendValue(builder, INSERT);
            } else if (operation == 1) {
                appendValue(builder, ADD_TO_EXISTING);
            } else if (operation == 2) {
                appendValue(builder, REPLACE);
            }
        }

    }

}
