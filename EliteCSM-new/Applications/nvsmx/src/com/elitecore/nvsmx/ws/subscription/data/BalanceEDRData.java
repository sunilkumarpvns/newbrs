package com.elitecore.nvsmx.ws.subscription.data;

import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.nvsmx.ws.subscription.request.UpdateMonetaryBalanceRequest;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.StringWriter;
import java.sql.Timestamp;

@XmlType(propOrder={"balanceId","subscriberId","packageId","serviceInstanceId","requestIPAddress","serviceId","ratingGroupId"
        ,"quotaProfileId","addOnPackageId","previousBalance","currentBalance", "transactionType","amount","billingCycleVolume"
        ,"billingCycleTime","validFromDate","validToDate","transactionId","operation","action","remarks","creditLimit", "nextBillingCycleCreditLimit"
        , "monetaryRechargePlanName", "price", "currency"})
public class BalanceEDRData {
    private String balanceId;
    private String subscriberId;
    private String packageId;
    private String serviceInstanceId;
    private String requestIPAddress;
    private String serviceId;
    private String ratingGroupId;
    private String quotaProfileId;
    private String addOnPackageId;
    private double previousBalance;
    private double currentBalance;
    private String transactionType;
    private String transactionId;
    private double amount;
    private long billingCycleVolume;
    private long billingCycleTime;
    private Timestamp validFromDate;
    private Timestamp validToDate;
    private String operation;
    private String action;
    private String remarks;
    private Long creditLimit;
    private Long nextBillingCycleCreditLimit;
    private String monetaryRechargePlanName;
    private double price;
    private String currency;
    private UpdateMonetaryBalanceRequest request;

    public String getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(String balanceId) {
        this.balanceId = balanceId;
    }

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

    public String getRequestIPAddress() {
        return requestIPAddress;
    }

    public void setRequestIPAddress(String requestIPAddress) {
        this.requestIPAddress = requestIPAddress;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getRatingGroupId() {
        return ratingGroupId;
    }

    public void setRatingGroupId(String ratingGroupId) {
        this.ratingGroupId = ratingGroupId;
    }

    public String getQuotaProfileId() {
        return quotaProfileId;
    }

    public void setQuotaProfileId(String quotaProfileId) {
        this.quotaProfileId = quotaProfileId;
    }

    public String getAddOnPackageId() {
        return addOnPackageId;
    }

    public void setAddOnPackageId(String addOnPackageId) {
        this.addOnPackageId = addOnPackageId;
    }

    public double getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(double previousBalance) {
        this.previousBalance = previousBalance;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getBillingCycleVolume() {
        return billingCycleVolume;
    }

    public void setBillingCycleVolume(long billingCycleVolume) {
        this.billingCycleVolume = billingCycleVolume;
    }

    public long getBillingCycleTime() {
        return billingCycleTime;
    }

    public void setBillingCycleTime(long billingCycleTime) {
        this.billingCycleTime = billingCycleTime;
    }

    public Timestamp getValidFromDate() {
        return validFromDate;
    }

    public void setValidFromDate(Timestamp validFromDate) {
        this.validFromDate = validFromDate;
    }

    public Timestamp getValidToDate() {
        return validToDate;
    }

    public void setValidToDate(Timestamp validToDate) {
        this.validToDate = validToDate;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Long creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Long getNextBillingCycleCreditLimit() {
        return nextBillingCycleCreditLimit;
    }

    public void setNextBillingCycleCreditLimit(Long nextBillingCycleCreditLimit) {
        this.nextBillingCycleCreditLimit = nextBillingCycleCreditLimit;
    }

    public String getMonetaryRechargePlanName() {
        return monetaryRechargePlanName;
    }

    public void setMonetaryRechargePlanName(String monetaryRechargePlanName) {
        this.monetaryRechargePlanName = monetaryRechargePlanName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTransactionId() { return transactionId; }

    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    @XmlTransient
    public UpdateMonetaryBalanceRequest getRequest() {
        return request;
    }

    public void setRequest(UpdateMonetaryBalanceRequest request) {
        this.request = request;
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
        out.println("Balance Id = " + balanceId);
        out.println("Subscriber Id = " + subscriberId);
        out.println("Package Id = " + packageId);
        out.println("Service Instance Id = " + serviceInstanceId);
        out.println("Request IP Address = " + requestIPAddress);
        out.println("Service Id = " + serviceId);
        out.println("Rating Group Id = " + ratingGroupId);
        out.println("Quota Profile Id = " + quotaProfileId);
        out.println("Add On Package Id = " + addOnPackageId);
        out.println("Previous Balance = " + previousBalance);
        out.println("Current Balance = " + currentBalance);
        out.println("Transaction Type = " + transactionType);
        out.println("Amount = " + amount);
        out.println("Operation = " + transactionType);
        out.println("Billing Cycle Volume = " + billingCycleVolume);
        out.println("Billing Cycle Time = " + billingCycleTime);
        out.println("Valid From Date = " + validFromDate.toString());
        out.println("Valid To Date = " + validToDate.toString());
        out.println("Operation = " + operation);
        out.println("Action = " + action);
        out.println("Remarks = " + remarks);
        out.println("Credit Limit = " + creditLimit);
        out.println("Next Billing Cycle Credit Limit = " + nextBillingCycleCreditLimit);
        out.println("Monetary Recharge Plan Name = " + monetaryRechargePlanName);
        out.println("Price = " + price);
        out.println("Currency = " + currency);
        out.println("Transaction Id="+transactionId);
    }

    public static BalanceEDRData create(MonetaryBalance monetaryBalance) {
        BalanceEDRData monetaryBalanceData = new BalanceEDRData();
        monetaryBalanceData.setServiceId(monetaryBalance.getServiceId());
        monetaryBalanceData.setValidFromDate(new Timestamp(monetaryBalance.getValidFromDate()));
        return monetaryBalanceData;
    }

}
