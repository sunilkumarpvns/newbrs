package com.elitecore.nvsmx.system.driver.cdr;

import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.driverx.cdr.deprecated.BaseCSVDriver;
import com.elitecore.corenetvertex.spr.EDRListener;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalanceWrapper;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SubscriberEDRData;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionEDRData;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionResult;
import com.elitecore.corenetvertex.spr.params.MonetaryRechargeData;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.ws.subscription.blmanager.OperationType;
import com.elitecore.nvsmx.ws.subscription.data.BalanceEDRData;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.ThreadContext;

import java.sql.Timestamp;
import java.util.EnumMap;

import static com.elitecore.commons.base.Strings.isNullOrBlank;
import static com.elitecore.commons.logging.LogManager.getLogger;
import static java.util.Objects.nonNull;

public class BalanceEDRListener implements EDRListener {

    private static final String MODULE = "BLNC-EDR-LSTNR";
    private BaseCSVDriver balanceEDRDriver;




    @Override
    public void addMonetaryEDR(SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String monetaryRechargePlanName, String requestIPAddress, String remark) {
        balanceEDRDriver = EDRDriverManager.getInstance().getDriver(NVSMXCommonConstants.BALANCE_EDR_DRIVER);
        MonetaryBalance currentMonetaryBalance = subscriberMonetaryBalanceWrapper.getCurrentMonetaryBalance();
        MonetaryBalance previousMonetaryBalance = subscriberMonetaryBalanceWrapper.getPreviousMonetaryBalance();
        SPRInfo sprInfo = subscriberMonetaryBalanceWrapper.getSprInfo();

        String operation = subscriberMonetaryBalanceWrapper.getOperation();
        if(isNullOrBlank(operation) && isNullOrBlank(ThreadContext.get("Operation")) == false){
            operation = ThreadContext.get("Operation");
        }

        String ipAddress = requestIPAddress;
        if(isNullOrBlank(ipAddress) && isNullOrBlank(ThreadContext.get("IpAddress")) == false){
            ipAddress = ThreadContext.get("IpAddress");
        }

        try {
            BalanceEDRData balanceEDRData = BalanceEDRData.create(currentMonetaryBalance);
            balanceEDRData.setSubscriberId(currentMonetaryBalance.getSubscriberId());
            balanceEDRData.setBalanceId(currentMonetaryBalance.getId());
            balanceEDRData.setPreviousBalance(nonNull(previousMonetaryBalance)?previousMonetaryBalance.getAvailBalance():0);
            balanceEDRData.setCurrentBalance(currentMonetaryBalance.getAvailBalance());
            balanceEDRData.setAmount(0);
            balanceEDRData.setTransactionType(OperationType.fromVal("CR").name());
            balanceEDRData.setValidToDate(new Timestamp(currentMonetaryBalance.getValidToDate()));
            balanceEDRData.setOperation(operation);
            balanceEDRData.setRemarks(remark);
            balanceEDRData.setCurrency(currentMonetaryBalance.getCurrency());
            balanceEDRData.setServiceInstanceId(nonNull(sprInfo)?sprInfo.getServiceInstanceId():null);
            balanceEDRData.setRequestIPAddress(ipAddress);
            balanceEDRData.setCreditLimit(currentMonetaryBalance.getCreditLimit());
            balanceEDRData.setNextBillingCycleCreditLimit(currentMonetaryBalance.getNextBillingCycleCreditLimit());
            if(StringUtils.isNotBlank(monetaryRechargePlanName)){
                balanceEDRData.setMonetaryRechargePlanName(monetaryRechargePlanName);
            }
            balanceEDRData.setAction(subscriberMonetaryBalanceWrapper.getAction());
            balanceEDRData.setTransactionId(subscriberMonetaryBalanceWrapper.getTransactionId());
            balanceEDRDriver.handleRequest(balanceEDRData);
        } catch (DriverProcessFailedException e) {
            getLogger().error(MODULE, "Error while adding monetary balance for subscriberIdentity: " + currentMonetaryBalance.getSubscriberId()+ ". Reason: " + e.getMessage());
            getLogger().trace(MODULE,e);
        }
    }

    @Override
    public void addMonetaryEDR(SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String requestIPAddress, String remark) {
        addMonetaryEDR(subscriberMonetaryBalanceWrapper,null,requestIPAddress,remark);
    }

    @Override
    public void deleteMonetaryEDR(SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String requestIPAddress, String remark) {
        addMonetaryEDR(subscriberMonetaryBalanceWrapper,null, requestIPAddress, remark);
    }

    @Override
    public void updateMonetaryEDR(SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String requestIPAddress, String remark) {
        balanceEDRDriver = EDRDriverManager.getInstance().getDriver(NVSMXCommonConstants.BALANCE_EDR_DRIVER);
        MonetaryBalance currentMonetaryBalance = subscriberMonetaryBalanceWrapper.getCurrentMonetaryBalance();
        MonetaryBalance previousMonetaryBalance = subscriberMonetaryBalanceWrapper.getPreviousMonetaryBalance();
        SPRInfo sprInfo = subscriberMonetaryBalanceWrapper.getSprInfo();

        String operation = subscriberMonetaryBalanceWrapper.getOperation();
        if(isNullOrBlank(operation) && isNullOrBlank(ThreadContext.get("Operation")) == false){
            operation = ThreadContext.get("Operation");
        }

        String ipAddress = requestIPAddress;
        if(isNullOrBlank(ipAddress) && isNullOrBlank(ThreadContext.get("IpAddress")) == false){
            ipAddress = ThreadContext.get("IpAddress");
        }

        try {
            String transactionType = "";
            Double amount = nonNull(currentMonetaryBalance) ? currentMonetaryBalance.getAvailBalance() : 0;
            Double previousBalance = nonNull(previousMonetaryBalance) ? previousMonetaryBalance.getAvailBalance() : 0;
            Double currentBalance = previousBalance + amount;

            if(currentBalance > previousBalance){
                transactionType = OperationType.fromVal("CR").name();
            }else if(currentBalance < previousBalance){
                transactionType = OperationType.fromVal("DR").name();
            }

            amount = Math.abs(amount);
            BalanceEDRData balanceEDRData = BalanceEDRData.create(currentMonetaryBalance);
            balanceEDRData.setSubscriberId(currentMonetaryBalance.getSubscriberId());
            balanceEDRData.setBalanceId(currentMonetaryBalance.getId());
            balanceEDRData.setPreviousBalance(previousBalance);
            balanceEDRData.setCurrentBalance(currentBalance);
            balanceEDRData.setAmount(amount);
            balanceEDRData.setTransactionType(transactionType);
            balanceEDRData.setValidToDate(new Timestamp(currentMonetaryBalance.getValidToDate()));
            balanceEDRData.setOperation(operation);
            balanceEDRData.setRemarks(remark);
            balanceEDRData.setCreditLimit(currentMonetaryBalance.getCreditLimit());
            balanceEDRData.setNextBillingCycleCreditLimit(currentMonetaryBalance.getNextBillingCycleCreditLimit());
            balanceEDRData.setCurrency(currentMonetaryBalance.getCurrency());
            balanceEDRData.setServiceInstanceId(nonNull(sprInfo)?sprInfo.getServiceInstanceId():null);
            balanceEDRData.setRequestIPAddress(ipAddress);
            balanceEDRData.setAction(subscriberMonetaryBalanceWrapper.getAction());
            balanceEDRData.setTransactionId(subscriberMonetaryBalanceWrapper.getTransactionId());
            balanceEDRDriver.handleRequest(balanceEDRData);
        } catch (DriverProcessFailedException e) {
            getLogger().error(MODULE, "Error while updating monetary balance for subscriberIdentity: " + currentMonetaryBalance.getSubscriberId()
                    + ". Reason: " + e.getMessage());
            getLogger().trace(MODULE,e);
        }
    }

    @Override
    public void rechargeMonetaryBalanceEDR(MonetaryRechargeData monetaryRechargeData) {
        balanceEDRDriver = EDRDriverManager.getInstance().getDriver(NVSMXCommonConstants.BALANCE_EDR_DRIVER);
        try {
            Double amountToBeAdded = monetaryRechargeData.getAmount().doubleValue();
            Double priceDoubleValue = monetaryRechargeData.getPrice().doubleValue();
            String transactionType = "";
            Double amount = monetaryRechargeData.getMonetaryBalance().getAvailBalance();
            Double previousBalance = monetaryRechargeData.getMonetaryBalance().getInitialBalance();
            Double currentBalance = amount - priceDoubleValue + amountToBeAdded;

            String operation = monetaryRechargeData.getOperation();
            if(isNullOrBlank(operation) && isNullOrBlank(ThreadContext.get("Operation")) == false){
                operation = ThreadContext.get("Operation");
            }

            String ipAddress = monetaryRechargeData.getRequestIPAddress();
            if(isNullOrBlank(ipAddress) && isNullOrBlank(ThreadContext.get("IpAddress")) == false){
                ipAddress = ThreadContext.get("IpAddress");
            }

            if(currentBalance > previousBalance){
                transactionType = OperationType.fromVal("CR").name();
            }else if(currentBalance < previousBalance){
                transactionType = OperationType.fromVal("DR").name();
            }

            BalanceEDRData balanceEDRData = BalanceEDRData.create(monetaryRechargeData.getMonetaryBalance());
            balanceEDRData.setSubscriberId(monetaryRechargeData.getMonetaryBalance().getSubscriberId());
            balanceEDRData.setBalanceId(monetaryRechargeData.getMonetaryBalance().getId());
            balanceEDRData.setPreviousBalance(previousBalance);
            balanceEDRData.setCurrentBalance(currentBalance);
            balanceEDRData.setAmount(amountToBeAdded);
            balanceEDRData.setTransactionType(transactionType);
            balanceEDRData.setValidToDate(new Timestamp(monetaryRechargeData.getExtendedValidity()));
            balanceEDRData.setOperation(operation);
            balanceEDRData.setRemarks(monetaryRechargeData.getRemark());
            balanceEDRData.setCreditLimit(monetaryRechargeData.getMonetaryBalance().getCreditLimit());
            balanceEDRData.setMonetaryRechargePlanName(monetaryRechargeData.getMonetaryRechargePlanName());
            balanceEDRData.setPrice(priceDoubleValue);
            balanceEDRData.setCurrency(monetaryRechargeData.getMonetaryBalance().getCurrency());
            balanceEDRData.setServiceInstanceId(monetaryRechargeData.getSprInfo().getServiceInstanceId());
            balanceEDRData.setRequestIPAddress(ipAddress);
            balanceEDRData.setAction(monetaryRechargeData.getAction());
            balanceEDRDriver.handleRequest(balanceEDRData);
        } catch (DriverProcessFailedException e) {
            getLogger().error(MODULE, "Error while recharging monetary balance for subscriberIdentity: " + monetaryRechargeData.getMonetaryBalance().getSubscriberId()+ ". Reason: " + e.getMessage());
            getLogger().trace(MODULE,e);
        }
    }

    @Override
    public void addSubscriberEDR(SPRInfo sprInfo, String operation, String action, String requestIpAddress) {
        //Blank Implementation
    }

    @Override
    public void subscriberEDR(SubscriberEDRData subscriberEDRData) {
        //Blank Implementation
    }

    @Override
    public void updateSubscriberEDR(EnumMap<SPRFields, String> updatedProfileWithSubscriberId, String subscriberIdentity,
                                    String operation, String action, String requestIpAddress) {
        //Blank Implementation
    }

    @Override
    public void deleteSubscriberEDR(SPRInfo sprInfo, String operation, String action, String requestIpAddress) {
        //Blank Implementation
    }

    @Override
    public void addSubscriptionEDR(SPRInfo sprInfo, SubscriptionResult subscriptionResult, String operation, String requestIpAddress) {
        //Blank Implementation
    }

    @Override
    public void subscriptionEDR(SubscriptionEDRData subscriptionEDRData) {
        //Blank Implementation
    }

    @Override
    public void updateSubscriptionEDR(SPRInfo sprInfo, Subscription subscription, Long startTime, Long endTime, String rejectReason, String operation, String requestIpAddress) {
        //Blank Implementation
    }

    @Override
    public void restoreSubscriberEDR(SPRInfo sprInfo, String operation, String action, String requestIpAddress) {
        //Blank Implementation
    }

    @Override
    public void changeBillDayEDR(SPRInfo sprInfo, Timestamp nextBillDate, Timestamp billChangeDate, String operation, String action, String requestIpAddress) {
        //Blank Implementation
    }

}
