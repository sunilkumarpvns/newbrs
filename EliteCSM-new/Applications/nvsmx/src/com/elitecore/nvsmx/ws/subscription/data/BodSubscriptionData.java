package com.elitecore.nvsmx.ws.subscription.data;

import com.elitecore.corenetvertex.constants.SubscriptionState;

public class BodSubscriptionData {

    private String bodSubscriptionId;
    private String subscriberIdentity;
    private String bodId;
    private String bodName;
    private Long startTime;
    private Long endTime;
    private SubscriptionState bodStatus;
    private String parameter1;
    private String parameter2;
    private Integer priority;

    public BodSubscriptionData(){}

    public BodSubscriptionData(String bodSubscriptionId, String subscriberIdentity, String bodId, String bodName, Long startTime, Long endTime, SubscriptionState bodStatus, String parameter1, String parameter2, Integer priority) {
        this.bodSubscriptionId = bodSubscriptionId;
        this.subscriberIdentity = subscriberIdentity;
        this.bodId = bodId;
        this.bodName = bodName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bodStatus = bodStatus;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.priority = priority;
    }

    public String getBodSubscriptionId() { return bodSubscriptionId; }

    public void setBodSubscriptionId(String bodSubscriptionId) { this.bodSubscriptionId = bodSubscriptionId; }

    public String getSubscriberIdentity() { return subscriberIdentity; }

    public void setSubscriberIdentity(String subscriberIdentity) { this.subscriberIdentity = subscriberIdentity; }

    public String getBodId() { return bodId; }

    public void setBodId(String bodId) { this.bodId = bodId; }

    public String getBodName() { return bodName; }

    public void setBodName(String bodName) { this.bodName = bodName; }

    public Long getStartTime() { return startTime; }

    public void setStartTime(Long startTime) { this.startTime = startTime; }

    public Long getEndTime() { return endTime; }

    public void setEndTime(Long endTime) { this.endTime = endTime; }

    public SubscriptionState getBodStatus() { return bodStatus; }

    public void setBodStatus(SubscriptionState bodStatus) { this.bodStatus = bodStatus; }

    public String getParameter1() { return parameter1; }

    public void setParameter1(String parameter1) { this.parameter1 = parameter1; }

    public String getParameter2() { return parameter2; }

    public void setParameter2(String parameter2) { this.parameter2 = parameter2;}

    public Integer getPriority() { return priority; }

    public void setPriority(Integer priority) { this.priority = priority;}
}
