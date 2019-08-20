package com.elitecore.nvsmx.system.driver.cdr;

import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.driverx.cdr.deprecated.BaseCSVDriver;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.spr.EDRListener;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalanceWrapper;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionResult;
import com.elitecore.corenetvertex.spr.params.MonetaryRechargeData;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.elitecore.corenetvertex.spr.data.SubscriptionEDRData;
import com.elitecore.corenetvertex.spr.data.SubscriberEDRData;

import java.sql.Timestamp;
import java.util.EnumMap;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static java.util.Objects.nonNull;

public class SubscriptionEDRListener implements EDRListener {

    private static final String MODULE = "SUBSCRPTN-EDR-LSTNR";
    private BaseCSVDriver subscriptionEDRDriver;

    @Override
    public void addMonetaryEDR(SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String requestIPAddress, String remark) {
        //Blank Implementation
    }

    @Override
    public void addMonetaryEDR(SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String monetaryRechargePlanName, String requestIPAddress, String remark) {
        //Dummy Implementation
    }

    @Override
    public void deleteMonetaryEDR(SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String requestIPAddress, String remark) {
        //Blank Implementation
    }

    @Override
    public void updateMonetaryEDR(SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String requestIPAddress, String remark) {
        //Blank Implementation
    }

    @Override
    public void rechargeMonetaryBalanceEDR(MonetaryRechargeData monetaryRechargeData) {
        //Blank Implementation
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
    public void updateSubscriberEDR(EnumMap<SPRFields, String> updatedProfile, String subscriberIdentity,
                                    String operation, String action, String requestIpAddress) {
        //Blank Implementation
    }

    @Override
    public void deleteSubscriberEDR(SPRInfo sprInfo, String operation, String action, String requestIpAddress) {
        //Blank Implementation
    }

    @Override
    public void addSubscriptionEDR(SPRInfo sprInfo, SubscriptionResult subscriptionResult, String operation, String requestIpAddress) {
        subscriptionEDRDriver = EDRDriverManager.getInstance().getDriver(NVSMXCommonConstants.SUBSCRIPTION_EDR_DRIVER);
        SubscriptionEDRData subscriptionEDRData = new SubscriptionEDRData();

        for(Subscription subscription : subscriptionResult.getSubscriptions()){
            subscriptionEDRData.setSubscriberId(subscription.getSubscriberIdentity());
            subscriptionEDRData.setType(subscription.getType().name());
            subscriptionEDRData.setStatus(subscription.getStatus().getName());
            subscriptionEDRData.setOperation(operation);
            subscriptionEDRData.setServiceInstanceId(sprInfo.getServiceInstanceId());
            subscriptionEDRData.setPriority(subscription.getPriority());
            subscriptionEDRData.setRequestIpAddress(requestIpAddress);

            if(Objects.equals(SubscriptionType.BOD, subscription.getType())) {
                setBODSubscriptionEDRData(subscription.getPackageId(), subscription.getStartTime(), subscription.getEndTime(), subscriptionEDRData);
            } else {
                setSubscriptionEDRData(subscription.getProductOfferId(), subscription.getStartTime(), subscription.getEndTime(), subscriptionEDRData);
            }

            if(nonNull(subscription.getFnFGroupName())){
                subscriptionEDRData.setFnfMembers(nonNull(subscription.getFnFGroupMembers())
                        ?subscription.getFnFGroupMembers().toString()
                        :null);
            }

            try {
                subscriptionEDRDriver.handleRequest(subscriptionEDRData);
            } catch (DriverProcessFailedException e) {
                getLogger().error(MODULE, "Error while subscribing addOn for : " + subscriptionEDRData.getSubscriberId()+ ". Reason: " + e.getMessage());
                getLogger().trace(MODULE,e);
            }
        }
    }

    @Override
    public void subscriptionEDR(SubscriptionEDRData subscriptionEDRData) {
        subscriptionEDRDriver = EDRDriverManager.getInstance().getDriver(NVSMXCommonConstants.SUBSCRIPTION_EDR_DRIVER);
        String strMessage = "";
            try {
                //Identify action whether it is SubscribeAddon or UnsubscribeAddon
                if(subscriptionEDRData.getStatus().equalsIgnoreCase(SubscriptionState.STARTED.getName())){
                    strMessage = "Error while subscribing addOn for : " + subscriptionEDRData.getSubscriberId()+ ". Reason: ";
                }else{
                    strMessage = "Error while changing addOn subscription for : " + subscriptionEDRData.getSubscriberId()+ ". Reason: ";
                }
                subscriptionEDRDriver.handleRequest(subscriptionEDRData);
            } catch (DriverProcessFailedException e) {
                getLogger().error(MODULE, strMessage + e.getMessage());
                getLogger().trace(MODULE,e);
            }
    }

    @Override
    public void updateSubscriptionEDR(SPRInfo sprInfo, Subscription subscription, Long startTime, Long endTime, String rejectReason, String operation, String requestIpAddress) {
        subscriptionEDRDriver = EDRDriverManager.getInstance().getDriver(NVSMXCommonConstants.SUBSCRIPTION_EDR_DRIVER);

        Timestamp startTimeStamp = nonNull(startTime)?new Timestamp(startTime):null;
        Timestamp endTimeStamp = nonNull(endTime)?new Timestamp(endTime):null;
        SubscriptionEDRData subscriptionEDRData = new SubscriptionEDRData();
        subscriptionEDRData.setSubscriberId(subscription.getSubscriberIdentity());
        subscriptionEDRData.setType(subscription.getType().name());
        subscriptionEDRData.setStatus(subscription.getStatus().name);
        subscriptionEDRData.setRejectReason(rejectReason);
        subscriptionEDRData.setOperation(operation);
        subscriptionEDRData.setServiceInstanceId(sprInfo.getServiceInstanceId());
        subscriptionEDRData.setPriority(subscription.getPriority());
        subscriptionEDRData.setRequestIpAddress(requestIpAddress);

        setSubscriptionEDRData(subscription.getProductOfferId(), startTimeStamp, endTimeStamp, subscriptionEDRData);

        try {
            subscriptionEDRDriver.handleRequest(subscriptionEDRData);
        } catch (DriverProcessFailedException e) {
            getLogger().error(MODULE, "Error while changing addOn subscription for : " + subscriptionEDRData.getSubscriberId()+ ". Reason: " + e.getMessage());
            getLogger().trace(MODULE,e);
        }
    }

    @Override
    public void restoreSubscriberEDR(SPRInfo sprInfo, String operation, String action, String requestIpAddress) {
        //Blank Implementation
    }

    @Override
    public void changeBillDayEDR(SPRInfo sprInfo, Timestamp nextBillDate, Timestamp billChangeDate, String operation, String action, String requestIpAddress) {
        //Blank Implementation
    }

    private void setSubscriptionEDRData(String productOfferId, Timestamp startTime, Timestamp endTime, SubscriptionEDRData subscriptionEDRData) {
        subscriptionEDRData.setPackageId(productOfferId);
        if(productOfferId!=null){
            subscriptionEDRData.setPackageName(DefaultNVSMXContext.getContext().getPolicyRepository().getProductOffer().byId(productOfferId).getName());
        }
        subscriptionEDRData.setStartTime(startTime);
        subscriptionEDRData.setEndTime(endTime);
    }

    private void setBODSubscriptionEDRData(String bodPackageId, Timestamp startTime, Timestamp endTime, SubscriptionEDRData subscriptionEDRData) {
        subscriptionEDRData.setPackageId(bodPackageId);
        if(nonNull(bodPackageId)){
            subscriptionEDRData.setPackageName(DefaultNVSMXContext.getContext().getPolicyRepository().getBoDPackage().byId(bodPackageId).getName());
        }
        subscriptionEDRData.setStartTime(startTime);
        subscriptionEDRData.setEndTime(endTime);
    }
}