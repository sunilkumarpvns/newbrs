package com.elitecore.nvsmx.system.driver.cdr;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.core.driverx.cdr.deprecated.CSVLineBuilderSupport;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.nvsmx.ws.subscription.data.BalanceEDRData;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.nonNull;

public class BalanceEDRLineBuilder extends CSVLineBuilderSupport<BalanceEDRData> {

    public BalanceEDRLineBuilder(String delimiter, SimpleDateFormat timestampFormat, TimeSource timeSource) {
        super(delimiter, Maps.newHashMap(), timestampFormat, timeSource);
    }

    @Override
    public List<String> getCSVRecords(BalanceEDRData updatedMonetaryBalance) {
        StringBuilder records = new StringBuilder();

        if(nonNull(updatedMonetaryBalance.getBalanceId())){
            appendValue(records, updatedMonetaryBalance.getBalanceId());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(updatedMonetaryBalance.getSubscriberId())){
            appendValue(records, updatedMonetaryBalance.getSubscriberId());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(updatedMonetaryBalance.getPackageId())){
            appendValue(records,updatedMonetaryBalance.getPackageId());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(updatedMonetaryBalance.getServiceInstanceId())){
            appendValue(records,updatedMonetaryBalance.getServiceInstanceId());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(updatedMonetaryBalance.getRequestIPAddress())){
            appendValue(records,updatedMonetaryBalance.getRequestIPAddress());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(updatedMonetaryBalance.getServiceId())){
            appendValue(records, updatedMonetaryBalance.getServiceId());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(updatedMonetaryBalance.getRatingGroupId())){
            appendValue(records, updatedMonetaryBalance.getRatingGroupId());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(updatedMonetaryBalance.getQuotaProfileId())){
            appendValue(records,updatedMonetaryBalance.getQuotaProfileId());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(updatedMonetaryBalance.getAddOnPackageId())){
            appendValue(records,updatedMonetaryBalance.getAddOnPackageId());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(updatedMonetaryBalance.getPreviousBalance())){
            appendValue(records,updatedMonetaryBalance.getPreviousBalance());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(updatedMonetaryBalance.getCurrentBalance())){
            appendValue(records,updatedMonetaryBalance.getCurrentBalance());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(updatedMonetaryBalance.getAmount())){
            appendValue(records,updatedMonetaryBalance.getAmount());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(updatedMonetaryBalance.getTransactionType())){
            appendValue(records,updatedMonetaryBalance.getTransactionType());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(updatedMonetaryBalance.getBillingCycleVolume())){
            appendValue(records,updatedMonetaryBalance.getBillingCycleVolume());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(updatedMonetaryBalance.getBillingCycleTime())){
            appendValue(records,updatedMonetaryBalance.getBillingCycleTime());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(updatedMonetaryBalance.getCreditLimit())){
            appendValue(records,updatedMonetaryBalance.getCreditLimit());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(updatedMonetaryBalance.getNextBillingCycleCreditLimit())){
            appendValue(records,updatedMonetaryBalance.getNextBillingCycleCreditLimit());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(updatedMonetaryBalance.getValidFromDate())){
            appendValue(records,updatedMonetaryBalance.getValidFromDate().toString());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(updatedMonetaryBalance.getValidToDate())){
            appendValue(records,updatedMonetaryBalance.getValidToDate().toString());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(updatedMonetaryBalance.getOperation())){
            appendValue(records, updatedMonetaryBalance.getOperation());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(updatedMonetaryBalance.getAction())){
            appendValue(records, updatedMonetaryBalance.getAction());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if(nonNull(updatedMonetaryBalance.getRemarks())){
            appendValue(records, updatedMonetaryBalance.getRemarks());
        }else{
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if (nonNull(updatedMonetaryBalance.getMonetaryRechargePlanName())) {
            appendValue(records, updatedMonetaryBalance.getMonetaryRechargePlanName());
        } else {
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if (nonNull(updatedMonetaryBalance.getPrice())) {
            appendValue(records, updatedMonetaryBalance.getPrice());
        } else {
            appendValue(records, CommonConstants.EMPTY_STRING);
        }

        if (nonNull(updatedMonetaryBalance.getCurrency())) {
            appendValue(records, updatedMonetaryBalance.getCurrency());
        } else {
            appendValue(records, CommonConstants.EMPTY_STRING);
        }
        if(nonNull(updatedMonetaryBalance.getTransactionId()))
        {
            appendValue(records,updatedMonetaryBalance.getTransactionId());
        }else {
            appendValue(records,CommonConstants.EMPTY_STRING);
        }

        String strDateFormat = new Timestamp(System.currentTimeMillis()).toString();
        if(strDateFormat.contains(Character.toString(CommonConstants.COMMA))) {
            strDateFormat = strDateFormat.replaceAll(Character.toString(CommonConstants.COMMA), "\\\\" + CommonConstants.COMMA);
        }
        records.append(strDateFormat);

        return Arrays.asList(records.toString());
    }
}
