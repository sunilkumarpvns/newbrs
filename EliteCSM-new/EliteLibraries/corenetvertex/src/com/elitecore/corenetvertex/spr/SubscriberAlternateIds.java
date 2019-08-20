package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.spr.data.AlternateIdType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscriberAlternateIds {
    private String subscriberId;
    private List<SubscriberAlternateIdStatusDetail> subscriberAlternateIdStatusList;
    private Map<String, SubscriberAlternateIdStatusDetail> allAlternateIdStatusMap;
    private Map<String, SubscriberAlternateIdStatusDetail> externalAlternateIdStatusMap;
    private SubscriberAlternateIdStatusDetail sprTypeAlternateId;

    public SubscriberAlternateIds(String subscriberId, List<SubscriberAlternateIdStatusDetail> subscriberAlternateIdStatusList) {
        this.subscriberId = subscriberId;
        this.subscriberAlternateIdStatusList = subscriberAlternateIdStatusList;
        createAlternateIdStatusMap(subscriberAlternateIdStatusList);
    }

    private void createAlternateIdStatusMap(List<SubscriberAlternateIdStatusDetail> subscriberAlternateIdStatusList) {
        if(CollectionUtils.isEmpty(subscriberAlternateIdStatusList)){
            return ;
        }
        Map<String, SubscriberAlternateIdStatusDetail> map = new HashMap<>();
        Map<String, SubscriberAlternateIdStatusDetail> tempExternalIdMap = new HashMap<>();
        for(SubscriberAlternateIdStatusDetail subscriberAlternateIdStatus:subscriberAlternateIdStatusList){
            map.put(subscriberAlternateIdStatus.getAlternateId(),subscriberAlternateIdStatus);
            if(AlternateIdType.SPR.name().equalsIgnoreCase(subscriberAlternateIdStatus.getType())){
                this.sprTypeAlternateId = subscriberAlternateIdStatus;
            }else{
                tempExternalIdMap.put(subscriberAlternateIdStatus.getAlternateId(),subscriberAlternateIdStatus);
            }
        }
        this.allAlternateIdStatusMap = map;
        this.externalAlternateIdStatusMap = tempExternalIdMap;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public SubscriberAlternateIdStatusDetail byAlternateId(String alternatId){
        if(MapUtils.isEmpty(allAlternateIdStatusMap)){
            return null;
        }
        return allAlternateIdStatusMap.get(alternatId);
    }


    public List<SubscriberAlternateIdStatusDetail> getSubscriberAlternateIdStatusList() {
        return subscriberAlternateIdStatusList;
    }

    public SubscriberAlternateIdStatusDetail getSprTypeAlternateId(){
        return sprTypeAlternateId;
    }




}
