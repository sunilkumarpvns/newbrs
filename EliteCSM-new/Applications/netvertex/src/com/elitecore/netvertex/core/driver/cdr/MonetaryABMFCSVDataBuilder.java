package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.core.driverx.cdr.deprecated.BaseCSVDriver;
import com.elitecore.core.driverx.cdr.deprecated.CSVLineBuilderSupport;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.spr.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MonetaryABMFCSVDataBuilder implements CSVDataBuilder<MonetaryABMFOperation.MonetaryOperationData>{
    private static final String CDRTIMESTAMP = "LAST_UPDATE_TIME";
    private static final String ABMF_CSV_HEADER = "ID, SUBSCRIBER_ID, QUOTA_PROFILE_ID,SERVICE_ID,PACKAGE_ID,AVAILABLE_BALANCE,"
            + "TOTAL_BALANCE,TOTAL_RESERVATION,VALID_FROM_DATE,VALID_TO_DATE,CURRENCY,OPERATION_TYPE" + CommonConstants.COMMA + " " + CDRTIMESTAMP;

    @Override
    public String getHeader() {
        return ABMF_CSV_HEADER;
    }

    @Override
    public BaseCSVDriver.CSVLineBuilder<MonetaryABMFOperation.MonetaryOperationData> getLineBuilder(TimeSource timeSource) {
        return new MonetaryABMFCSVDataBuilder.MonetaryABMFOperationDataLineBuilder(",", new SimpleDateFormat(CommonConstants.DEFAULT_TIMESTAMP_FORMAT), timeSource);
    }

    public static class MonetaryABMFOperationDataLineBuilder extends CSVLineBuilderSupport<MonetaryABMFOperation.MonetaryOperationData> {

        public MonetaryABMFOperationDataLineBuilder(String delimiter, SimpleDateFormat timestampFormat,
                                                 TimeSource timeSource) {
            super(delimiter, Maps.newHashMap(), timestampFormat, timeSource);
        }

        @Override
        public List<String> getCSVRecords(MonetaryABMFOperation.MonetaryOperationData request) {


            List<MonetaryBalance> monetaryBalances = request.getMonetaryBalances();
            List<String> records = new ArrayList<>();
            for (MonetaryBalance monetaryBalance : monetaryBalances) {
                records.add(getCSVRecords(monetaryBalance, request.getOperation()));
            }
            return records;
        }

        public String getCSVRecords(MonetaryBalance monetaryBalance, ABMFOperationType abmfOperationType) {

            StringBuilder builder = new StringBuilder();
            appendValue(builder, monetaryBalance.getId());
            appendValue(builder, monetaryBalance.getSubscriberId());
            appendValue(builder, monetaryBalance.getServiceId());
            appendValue(builder, monetaryBalance.getAvailBalance());
            appendValue(builder, monetaryBalance.getInitialBalance());
            appendValue(builder, monetaryBalance.getTotalReservation());
            appendValue(builder, monetaryBalance.getValidFromDate());
            appendValue(builder, monetaryBalance.getValidToDate());
            appendValue(builder, monetaryBalance.getCurrency());
            appendValue(builder, abmfOperationType.name());
            appendTimestamp(builder);

            return builder.toString();
        }


    }
}
