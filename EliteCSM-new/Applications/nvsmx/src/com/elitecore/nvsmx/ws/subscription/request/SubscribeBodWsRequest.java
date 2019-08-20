package com.elitecore.nvsmx.ws.subscription.request;

import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

import javax.xml.bind.annotation.XmlTransient;

public class SubscribeBodWsRequest extends BaseWebServiceRequest {
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
    private Integer updateAction;
    private String priority;
    private boolean updateBalanceIndication;
    private String parameter1;
    private String parameter2;
    private String webServiceName;
    private String webServiceMethodName;

    public SubscribeBodWsRequest(String parentId, String subscriberId, String alternateId, String cui, String bodId,
                                 String bodName, String subscriptionStatusValue, String subscriptionStatusName, String startTime,
                                 String endTime, Integer updateAction, String priority, boolean updateBalanceIndication,
                                 String parameter1, String parameter2, String webServiceName, String webServiceMethodName) {
        this.parentId = parentId;
        this.subscriberId = subscriberId;
        this.alternateId = alternateId;
        this.cui = cui;
        this.bodId = bodId;
        this.bodName = bodName;
        this.subscriptionStatusValue = subscriptionStatusValue;
        this.subscriptionStatusName = subscriptionStatusName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.updateAction = updateAction;
        this.priority = priority;
        this.updateBalanceIndication = updateBalanceIndication;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.webServiceName = webServiceName;
        this.webServiceMethodName = webServiceMethodName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getAlternateId() {
        return alternateId;
    }

    public void setAlternateId(String alternateId) {
        this.alternateId = alternateId;
    }

    public String getCui() {
        return cui;
    }

    public void setCui(String cui) {
        this.cui = cui;
    }

    public String getBodId() {
        return bodId;
    }

    public void setBodId(String bodId) {
        this.bodId = bodId;
    }

    public String getBodName() {
        return bodName;
    }

    public void setBodName(String bodName) {
        this.bodName = bodName;
    }

    public String getSubscriptionStatusValue() {
        return subscriptionStatusValue;
    }

    public void setSubscriptionStatusValue(String subscriptionStatusValue) {
        this.subscriptionStatusValue = subscriptionStatusValue;
    }

    public String getSubscriptionStatusName() {
        return subscriptionStatusName;
    }

    public void setSubscriptionStatusName(String subscriptionStatusName) {
        this.subscriptionStatusName = subscriptionStatusName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getUpdateAction() {
        return updateAction;
    }

    public void setUpdateAction(Integer updateAction) {
        this.updateAction = updateAction;
    }

    public String getPriority() {
        return priority;
    }

    public boolean isUpdateBalanceIndication() { return updateBalanceIndication; }

    @Override
    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    @Override
    public String getParameter2() {
        return parameter2;
    }

    @Override
    public void visit(DiagnosticContextInjector manager) {
        manager.add(this);
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    @XmlTransient
    @Override
    public String getWebServiceName() {
        return webServiceName;
    }

    public void setWebServiceName(String webServiceName) {
        this.webServiceName = webServiceName;
    }

    @XmlTransient
    @Override
    public String getWebServiceMethodName() {
        return webServiceMethodName;
    }

    public void setWebServiceMethodName(String webServiceMethodName) {
        this.webServiceMethodName = webServiceMethodName;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);

        builder.append("Subscriber Id", subscriberId);
        builder.append("Alternate Id", alternateId);
        builder.append("Parent Id", parentId);
        builder.append("Cui", cui);
        builder.append("BodPackage Id", bodId);
        builder.append("BodPackage Name", bodName);
        builder.append("Subscription Status Name", subscriptionStatusName);
        builder.append("Subscription Status Value", subscriptionStatusValue);
        builder.append("Start Time", startTime);
        builder.append("End Time", endTime);
        builder.append("Priority", priority);
        builder.append("Update Balance Indication", updateBalanceIndication);

        return builder.toString();
    }
}
