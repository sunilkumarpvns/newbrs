package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionEDRData;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionResult;
import com.elitecore.corenetvertex.spr.params.MonetaryRechargeData;
import com.elitecore.corenetvertex.spr.data.SubscriberEDRData;

import java.sql.Timestamp;
import java.util.EnumMap;


public interface EDRListener {
    void addMonetaryEDR(SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String requestIPAddress, String remark);
    void addMonetaryEDR(SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper,String monetaryRechargePlanName,String requestIPAddress, String remark);
    void deleteMonetaryEDR(SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String requestIPAddress, String remark);
    void updateMonetaryEDR(SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String requestIPAddress, String remark);
    void rechargeMonetaryBalanceEDR(MonetaryRechargeData monetaryRechargeData);
    void addSubscriberEDR(SPRInfo sprInfo, String operation, String action, String requestIpAddress);
    void subscriberEDR(SubscriberEDRData subscriberEDRData);
    void updateSubscriberEDR(EnumMap<SPRFields, String> updatedProfile, String subscriberIdentity, String operation, String action, String requestIpAddress);
    void deleteSubscriberEDR(SPRInfo sprInfo, String operation, String action, String requestIpAddress);
    void restoreSubscriberEDR(SPRInfo sprInfo, String operation, String action, String requestIpAddress);
    void changeBillDayEDR(SPRInfo sprInfo, Timestamp nextBillDate, Timestamp billChangeDate, String operation, String action, String requestIpAddress);
    void addSubscriptionEDR(SPRInfo sprInfo, SubscriptionResult subscriptionResult, String operation, String requestIpAddress);
    void subscriptionEDR(SubscriptionEDRData subscriptionEDRData);
    void updateSubscriptionEDR(SPRInfo sprInfo, Subscription subscription, Long startTime,
                               Long endTime, String rejectReason, String operation, String requestIpAddress);
}
