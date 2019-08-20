package com.elitecore.corenetvertex.spr.data;

import com.elitecore.commons.io.IndentingPrintWriter;

import javax.xml.bind.annotation.XmlType;
import java.io.StringWriter;
import java.sql.Timestamp;

@XmlType(propOrder={"subscriberId", "packageId", "packageName","startTime","endTime","rejectReason","status","type","operation","currency","priority"})
public class SubscriptionEDRData {
    private String subscriberId;
    private String packageId;
    private String serviceInstanceId;
    private String packageName;
    private Timestamp startTime;
    private Timestamp endTime;
    private String status;
    private String rejectReason;
    private String type;
    private String operation;
    private int priority;
    private String requestIpAddress;
    private String fnfMembers;
    private String currency;

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getServiceInstanceId() {
        return serviceInstanceId;
    }

    public void setServiceInstanceId(String serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getRequestIpAddress() {
        return requestIpAddress;
    }

    public void setRequestIpAddress(String requestIpAddress) {
        this.requestIpAddress = requestIpAddress;
    }

    public String getFnfMembers() {
        return fnfMembers;
    }

    public void setFnfMembers(String fnfMembers) {
        this.fnfMembers = fnfMembers;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        StringWriter stringWriter = new StringWriter();
        IndentingPrintWriter out = new IndentingPrintWriter(stringWriter);
        toString(out);
        return stringWriter.toString();
    }

    public void toString(IndentingPrintWriter out) {
        out.println();
        out.println("Subscriber Id = " + subscriberId);
        out.println("AddOn Package Id = " + packageId);
        out.println("Service Instance Id = " + serviceInstanceId);
        out.println("Start Time = " + startTime);
        out.println("End Time = " + endTime);
        out.println("Status = " + status);
        out.println("Type = " + type);
        out.println("Operation = " + operation);
        out.println("Priority = " + priority);
        out.println("FnF Members = " + fnfMembers);
        out.println("Currency = " + currency);
    }

    public static class SubscriptionEDRDataBuilder {
        SubscriptionEDRData subscriptionEDRData = new SubscriptionEDRData();

        public SubscriptionEDRDataBuilder(String subscriberId, String packageId) {
            subscriptionEDRData.subscriberId = subscriberId;
            subscriptionEDRData.packageId = packageId;
        }

        public SubscriptionEDRDataBuilder packageName(String packageName) {
            subscriptionEDRData.packageName = packageName;
            return this;
        }

        public SubscriptionEDRDataBuilder serviceInstanceId(String serviceInstanceId) {
            subscriptionEDRData.serviceInstanceId = serviceInstanceId;
            return this;
        }

        public SubscriptionEDRDataBuilder subscriptionState(String status) {
            subscriptionEDRData.status = status;
            return this;
        }

        public SubscriptionEDRDataBuilder packageType(String type) {
            subscriptionEDRData.type = type;
            return this;
        }

        public SubscriptionEDRDataBuilder operation(String operation) {
            subscriptionEDRData.operation = operation;
            return this;
        }

        public SubscriptionEDRDataBuilder currency(String currency) {
            subscriptionEDRData.currency = currency;
            return this;
        }

        public SubscriptionEDRDataBuilder startTime(Timestamp startTime) {
            subscriptionEDRData.startTime = startTime;
            return this;
        }

        public SubscriptionEDRDataBuilder endTime(Timestamp endTime) {
            subscriptionEDRData.endTime = endTime;
            return this;
        }

        public SubscriptionEDRData build() {
            return subscriptionEDRData;
        }

    }

}
