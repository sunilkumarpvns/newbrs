package com.elitecore.nvsmx.ws.subscription.rest.request;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SubscribeBodRestRequest {
    private String parentId;
    private String subscriberId;
    private String alternateId;
    private String cui;
    private String bodId;
    private String bodName;
    private String subscriptionStatusValue;
    private String subscriptionStatusName;
    private String startTime;
    private String endTime;
    private String priority;
    private String updateAction;
    private String updateBalanceIndication;

    public SubscribeBodRestRequest(){}

    public SubscribeBodRestRequest(String parentId, String subscriberId, String alternateId, Integer updateAction, String cui, String bodId, String bodName, String subscriptionStatusValue, String subscriptionStatusName, Long startTime, Long endTime, Integer priority, String parameter1, String parameter2, String webServiceName, String wsSubscribeBod) {
    }

    public String getParentId() { return parentId; }

    public void setParentId(String parentId) { this.parentId = parentId; }

    public String getSubscriberId() { return subscriberId; }

    public void setSubscriberId(String subscriberId) { this.subscriberId = subscriberId; }

    public String getAlternateId() { return alternateId; }

    public void setAlternateId(String alternateId) { this.alternateId = alternateId; }

    public String getCui() { return cui; }

    public void setCui(String cui) { this.cui = cui; }

    public String getBodId() { return bodId; }

    public void setBodId(String bodId) { this.bodId = bodId; }

    public String getBodName() { return bodName; }

    public void setBodName(String bodName) { this.bodName = bodName; }

    public String getSubscriptionStatusValue() { return subscriptionStatusValue; }

    public void setSubscriptionStatusValue(String subscriptionStatusValue) { this.subscriptionStatusValue = subscriptionStatusValue; }

    public String getSubscriptionStatusName() { return subscriptionStatusName; }

    public void setSubscriptionStatusName(String subscriptionStatusName) { this.subscriptionStatusName = subscriptionStatusName; }

    public String getStartTime() { return startTime; }

    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }

    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getPriority() { return priority; }

    public void setPriority(String priority) { this.priority = priority; }

    public String getUpdateAction() { return updateAction; }

    public void setUpdateAction(String updateAction) { this.updateAction = updateAction; }

    public String getUpdateBalanceIndication() { return updateBalanceIndication; }
}
