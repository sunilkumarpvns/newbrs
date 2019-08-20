package com.elitecore.nvsmx.system.driver.cdr;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.core.driverx.cdr.deprecated.CSVLineBuilderSupport;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SubscriberEDRData;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.nonNull;

public class SubscriberEDRLineBuilder extends CSVLineBuilderSupport<SubscriberEDRData> {

    public SubscriberEDRLineBuilder(String delimiter, SimpleDateFormat timestampFormat, TimeSource timeSource) {
        super(delimiter, Maps.newHashMap(), timestampFormat, timeSource);
    }

    @Override
    public List<String> getCSVRecords(SubscriberEDRData subscriberEDRData) {
        StringBuilder records = new StringBuilder();
        SPRInfo sprInfo = subscriberEDRData.getSprInfo();

        if(nonNull(sprInfo.getSubscriberIdentity())){
            appendValue(records, sprInfo.getSubscriberIdentity());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(sprInfo.getUserName())){
            appendValue(records, sprInfo.getUserName());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(sprInfo.getServiceInstanceId())){
            appendValue(records, sprInfo.getServiceInstanceId());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(subscriberEDRData.getRequestIpAddress())){
            appendValue(records, subscriberEDRData.getRequestIpAddress());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(sprInfo.getCustomerType())){
            appendValue(records, sprInfo.getCustomerType());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(sprInfo.getStatus())){
            appendValue(records, sprInfo.getStatus());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(sprInfo.getProductOffer())){
            appendValue(records, sprInfo.getProductOffer());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(sprInfo.getExpiryDate())){
            appendValue(records, sprInfo.getExpiryDate().toString());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(sprInfo.getBillingDate())){
            appendValue(records, sprInfo.getBillingDate());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(sprInfo.getImsi())){
            appendValue(records, sprInfo.getImsi());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(sprInfo.getMsisdn())){
            appendValue(records, sprInfo.getMsisdn());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(sprInfo.getImei())){
            appendValue(records, sprInfo.getImei());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if (nonNull(sprInfo.getNextBillDate())) {
            appendValue(records, sprInfo.getNextBillDate().toString());
        } else {
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if (nonNull(sprInfo.getBillChangeDate())) {
            appendValue(records, sprInfo.getBillChangeDate().toString());
        } else {
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(sprInfo.getCreatedDate())){
            appendValue(records, sprInfo.getCreatedDate().toString());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(sprInfo.getModifiedDate())){
            appendValue(records, sprInfo.getModifiedDate().toString());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(subscriberEDRData.getOperation())){
            appendValue(records, subscriberEDRData.getOperation());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(subscriberEDRData.getAction())){
            appendValue(records, subscriberEDRData.getAction());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(subscriberEDRData.getCurrency())){
            appendValue(records, subscriberEDRData.getCurrency());
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
