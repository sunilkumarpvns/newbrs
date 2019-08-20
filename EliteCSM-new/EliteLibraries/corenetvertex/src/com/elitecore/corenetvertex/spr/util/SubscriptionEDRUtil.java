package com.elitecore.corenetvertex.spr.util;

import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionEDRData;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionResult;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

public class SubscriptionEDRUtil {

    public static List<SubscriptionEDRData> prepareAddonSubscriptionEDRData(SPRInfo sprInfo, SubscriptionResult subscriptionResult, String operation, String requestIpAddress, String packageName){
        List<SubscriptionEDRData> listSubscriptionEDRData = new ArrayList<>();
        for(Subscription subscription : subscriptionResult.getSubscriptions()){
            SubscriptionEDRData subscriptionEDRData = new SubscriptionEDRData();
            subscriptionEDRData.setSubscriberId(subscription.getSubscriberIdentity());
            subscriptionEDRData.setType(subscription.getType().name());
            subscriptionEDRData.setStatus(subscription.getStatus().getName());
            subscriptionEDRData.setOperation(operation);
            subscriptionEDRData.setServiceInstanceId(sprInfo.getServiceInstanceId());
            subscriptionEDRData.setPriority(subscription.getPriority());
            subscriptionEDRData.setRequestIpAddress(requestIpAddress);
            subscriptionEDRData.setPackageName(packageName);

            setSubscriptionEDRData(subscription.getProductOfferId(), subscription.getStartTime(), subscription.getEndTime(), subscriptionEDRData);


            if(nonNull(subscription.getFnFGroupName())){
                subscriptionEDRData.setFnfMembers(nonNull(subscription.getFnFGroupMembers())
                        ?subscription.getFnFGroupMembers().toString()
                        :null);
            }
            listSubscriptionEDRData.add(subscriptionEDRData);
        }
        return listSubscriptionEDRData;
    }

    public static SubscriptionEDRData prepareUpdateAddonSubscriptionEDRData(SPRInfo sprInfo, Subscription subscription, Long startTime, Long endTime, String rejectReason, String operation, String requestIpAddress){
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
        return subscriptionEDRData;
    }

    public static void setSubscriptionEDRData(String productOfferId, Timestamp startTime, Timestamp endTime, SubscriptionEDRData subscriptionEDRData) {
        subscriptionEDRData.setPackageId(productOfferId);
        subscriptionEDRData.setStartTime(startTime);
        subscriptionEDRData.setEndTime(endTime);
    }
}
