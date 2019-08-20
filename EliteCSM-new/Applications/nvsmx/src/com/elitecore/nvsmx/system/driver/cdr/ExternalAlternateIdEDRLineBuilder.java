package com.elitecore.nvsmx.system.driver.cdr;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.core.driverx.cdr.deprecated.CSVLineBuilderSupport;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.nvsmx.ws.subscription.data.ExternalAlternateIdentityEDRData;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.nonNull;

public class ExternalAlternateIdEDRLineBuilder extends CSVLineBuilderSupport<ExternalAlternateIdentityEDRData> {

    public ExternalAlternateIdEDRLineBuilder(String delimiter, SimpleDateFormat timestampFormat, TimeSource timeSource) {
        super(delimiter, Maps.newHashMap(), timestampFormat, timeSource);
    }

    @Override
    public List<String> getCSVRecords(ExternalAlternateIdentityEDRData externalAlternateIdentityEDRData) {
        StringBuilder records = new StringBuilder();

        if(nonNull(externalAlternateIdentityEDRData.getSubscriberId())){
            appendValue(records, externalAlternateIdentityEDRData.getSubscriberId());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(externalAlternateIdentityEDRData.getAlternateId())){
            appendValue(records, externalAlternateIdentityEDRData.getAlternateId());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(externalAlternateIdentityEDRData.getUpdatedAlternateId())){
            appendValue(records, externalAlternateIdentityEDRData.getUpdatedAlternateId());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(externalAlternateIdentityEDRData.getStatus())){
            appendValue(records, externalAlternateIdentityEDRData.getStatus());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(externalAlternateIdentityEDRData.getUpdatedStatus())){
            appendValue(records, externalAlternateIdentityEDRData.getUpdatedStatus());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(externalAlternateIdentityEDRData.getOperation())){
            appendValue(records, externalAlternateIdentityEDRData.getOperation());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        String strDateFormat = new Timestamp(System.currentTimeMillis()).toString();
        if(strDateFormat.contains(CommonConstants.COMMA+"")) {
            strDateFormat = strDateFormat.replaceAll(CommonConstants.COMMA+"", "\\\\" + CommonConstants.COMMA);
        }
        records.append(strDateFormat);

        return Arrays.asList(records.toString());
    }
}


