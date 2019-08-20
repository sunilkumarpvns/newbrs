package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.corenetvertex.spr.EDRListener;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalanceWrapper;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SubscriberEDRData;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionEDRData;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionResult;
import com.elitecore.corenetvertex.spr.params.MonetaryRechargeData;

import java.sql.Timestamp;
import java.util.EnumMap;

public class SubscriberEDRListener implements EDRListener {

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
    public void updateSubscriberEDR(EnumMap<SPRFields, String> updatedProfile, String subscriberIdentity, String operation,
                                    String action, String requestIpAddress) {
        //Blank Implementation
    }

    @Override
    public void deleteSubscriberEDR(SPRInfo sprInfo, String operation, String action, String requestIpAddress) {
        //Blank Implementation
    }

    @Override
    public void updateSubscriptionEDR(SPRInfo sprInfo, Subscription subscription, Long startTime, Long endTime, String rejectReason, String operation, String requestIpAddress) {
        //Blank Implementation
    }

    @Override
    public void subscriptionEDR(SubscriptionEDRData subscriptionEDRData) {
        //Blank Implementation
    }

    @Override
    public void restoreSubscriberEDR(SPRInfo sprInfo, String operation, String action, String requestIpAddress) {
        //Blank Implementation
    }

    @Override
    public void addSubscriptionEDR(SPRInfo sprInfo, SubscriptionResult subscriptionResult, String operation, String requestIpAddress) {
        //Blank Implementation
    }

    @Override
    public void changeBillDayEDR(SPRInfo sprInfo, Timestamp nextBillDate, Timestamp billChangeDate, String operation, String action, String requestIpAddress) {
        //Blank Implementation
    }
}
