package com.elitecore.nvsmx.system.driver.cdr;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.core.driverx.cdr.deprecated.CSVLineBuilderSupport;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.spr.data.SubscriptionEDRData;
import org.apache.commons.lang.StringEscapeUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.nonNull;

public class SubscriptionEDRLineBuilder extends CSVLineBuilderSupport<SubscriptionEDRData> {

    public SubscriptionEDRLineBuilder(String delimiter, SimpleDateFormat timestampFormat, TimeSource timeSource) {
        super(delimiter, Maps.newHashMap(), timestampFormat, timeSource);
    }

    @Override
    public List<String> getCSVRecords(SubscriptionEDRData subscriptionEDRData) {
        StringBuilder records = new StringBuilder();

        if(nonNull(subscriptionEDRData.getSubscriberId())){
            appendValue(records, subscriptionEDRData.getSubscriberId());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(subscriptionEDRData.getPackageId())){
            appendValue(records, subscriptionEDRData.getPackageId());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(subscriptionEDRData.getServiceInstanceId())){
            appendValue(records, subscriptionEDRData.getServiceInstanceId());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(subscriptionEDRData.getRequestIpAddress())){
            appendValue(records, subscriptionEDRData.getRequestIpAddress());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(subscriptionEDRData.getPackageName())){
            appendValue(records, subscriptionEDRData.getPackageName());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(subscriptionEDRData.getStartTime())){
            appendValue(records, subscriptionEDRData.getStartTime().toString());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(subscriptionEDRData.getEndTime())){
            appendValue(records, subscriptionEDRData.getEndTime().toString());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(subscriptionEDRData.getStatus())){
            appendValue(records, subscriptionEDRData.getStatus());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(subscriptionEDRData.getType())){
            appendValue(records, subscriptionEDRData.getType());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(subscriptionEDRData.getOperation())){
            appendValue(records, subscriptionEDRData.getOperation());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(subscriptionEDRData.getCurrency())){
            appendValue(records, subscriptionEDRData.getCurrency());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(subscriptionEDRData.getPriority())){
            appendValue(records, subscriptionEDRData.getPriority());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(subscriptionEDRData.getFnfMembers())){
            appendValue(records, StringEscapeUtils.escapeCsv(subscriptionEDRData.getFnfMembers()));
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        String strDateFormat = new Timestamp(System.currentTimeMillis()).toString();
        if(strDateFormat.contains(Character.toString(CommonConstants.COMMA))) {
            strDateFormat = strDateFormat.replaceAll(Character.toString(CommonConstants.COMMA), "\\\\" + CommonConstants.COMMA);
        }
        records.append(strDateFormat);

        return Arrays.asList(records.toString());
    }
}
