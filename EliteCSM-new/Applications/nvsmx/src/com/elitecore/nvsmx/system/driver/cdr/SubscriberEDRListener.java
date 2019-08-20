package com.elitecore.nvsmx.system.driver.cdr;

import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.driverx.cdr.deprecated.BaseCSVDriver;
import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.spr.EDRListener;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalanceWrapper;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionEDRData;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionResult;
import com.elitecore.corenetvertex.spr.params.MonetaryRechargeData;
import com.elitecore.corenetvertex.spr.util.SPRInfoUtil;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.corenetvertex.spr.data.SubscriberEDRData;
import org.apache.logging.log4j.ThreadContext;

import java.sql.Timestamp;
import java.util.EnumMap;

import static com.elitecore.commons.base.Strings.isNullOrBlank;
import static com.elitecore.commons.logging.LogManager.getLogger;
import static java.util.Objects.nonNull;

public class SubscriberEDRListener implements EDRListener {

    private static final String MODULE = "SUBSCRBR-EDR-LSTNR";
    private BaseCSVDriver subscriberEDRDriver;

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
        subscriberEDRDriver = EDRDriverManager.getInstance().getDriver(NVSMXCommonConstants.SUBSCRIBER_EDR_DRIVER);

        try {
            subscriberEDRDriver.handleRequest(prepareSubscriberEDRData(sprInfo, operation, action, requestIpAddress));
        } catch (DriverProcessFailedException e) {
            getLogger().error(MODULE, "Error while adding subscriber: " + sprInfo.getSubscriberIdentity()+ ". Reason: " + e.getMessage());
            getLogger().trace(MODULE,e);
        }
    }

    @Override
    public void subscriberEDR(SubscriberEDRData subscriberEDRData) {
        subscriberEDRDriver = EDRDriverManager.getInstance().getDriver(NVSMXCommonConstants.SUBSCRIBER_EDR_DRIVER);

        try {
            subscriberEDRDriver.handleRequest(subscriberEDRData);
        } catch (DriverProcessFailedException e) {
            getLogger().error(MODULE, "Error while adding subscriber: " + subscriberEDRData.getSprInfo().getSubscriberIdentity()+ ". Reason: " + e.getMessage());
            getLogger().trace(MODULE,e);
        }
    }

    @Override
    public void updateSubscriberEDR(EnumMap<SPRFields, String> updatedProfile, String subscriberIdentity, String operation, String action, String requestIpAddress) {
        subscriberEDRDriver = EDRDriverManager.getInstance().getDriver(NVSMXCommonConstants.SUBSCRIBER_EDR_DRIVER);
        SPRInfoImpl sprInfo=SPRInfoUtil.createSPRInfoImpl(updatedProfile,subscriberIdentity);

        try {
            subscriberEDRDriver.handleRequest(prepareSubscriberEDRData(sprInfo, operation, action, requestIpAddress));
        } catch (DriverProcessFailedException e) {
            getLogger().error(MODULE, "Error while adding subscriber: " + sprInfo.getSubscriberIdentity()+ ". Reason: " + e.getMessage());
            getLogger().trace(MODULE,e);
        }

    }

    @Override
    public void deleteSubscriberEDR(SPRInfo sprInfo, String operation, String action, String requestIpAddress) {
        subscriberEDRDriver = EDRDriverManager.getInstance().getDriver(NVSMXCommonConstants.SUBSCRIBER_EDR_DRIVER);
        sprInfo.setStatus(SubscriberStatus.DELETED.name());

        try {
            subscriberEDRDriver.handleRequest(prepareSubscriberEDRData(sprInfo, operation, action, requestIpAddress));
        } catch (DriverProcessFailedException e) {
            getLogger().error(MODULE, "Error while deleting subscriber: " + sprInfo.getSubscriberIdentity()+ ". Reason: " + e.getMessage());
            getLogger().trace(MODULE,e);
        }
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
        subscriberEDRDriver = EDRDriverManager.getInstance().getDriver(NVSMXCommonConstants.SUBSCRIBER_EDR_DRIVER);
        sprInfo.setStatus(SubscriberStatus.ACTIVE.name());

        try {
            subscriberEDRDriver.handleRequest(prepareSubscriberEDRData(sprInfo, operation, action, requestIpAddress));
        } catch (DriverProcessFailedException e) {
            getLogger().error(MODULE, "Error while restoring subscriber: " + sprInfo.getSubscriberIdentity()+ ". Reason: " + e.getMessage());
            getLogger().trace(MODULE,e);
        }

    }

    @Override
    public void changeBillDayEDR(SPRInfo sprInfo, Timestamp nextBillDate, Timestamp billChangeDate, String operation,
                                 String action, String requestIpAddress) {
        subscriberEDRDriver = EDRDriverManager.getInstance().getDriver("subscriberEDRDriver");
        sprInfo.setNextBillDate(nextBillDate);
        sprInfo.setBillChangeDate(billChangeDate);

        try {
            subscriberEDRDriver.handleRequest(prepareSubscriberEDRData(sprInfo, operation, action, requestIpAddress));
        } catch (DriverProcessFailedException e) {
            getLogger().error(MODULE, "Error while changing bill day for subscriber: " + sprInfo.getSubscriberIdentity() + ". Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
    }

    private SubscriberEDRData prepareSubscriberEDRData(SPRInfo sprInfo, String operation, String action, String requestIpAddress) {
        SubscriberEDRData subscriberEDRData = new SubscriberEDRData();
        subscriberEDRData.setSprInfo(sprInfo);

        if(isNullOrBlank(operation) && isNullOrBlank(ThreadContext.get("Operation")) == false){
            subscriberEDRData.setOperation(ThreadContext.get("Operation"));
        } else{
            subscriberEDRData.setOperation(operation);
        }

        subscriberEDRData.setAction(action);

        if(isNullOrBlank(requestIpAddress) && isNullOrBlank(ThreadContext.get("IpAddress")) == false){
            subscriberEDRData.setRequestIpAddress(ThreadContext.get("IpAddress"));
        } else{
            subscriberEDRData.setRequestIpAddress(requestIpAddress);
        }

        return subscriberEDRData;
    }

}
