package com.elitecore.nvsmx.ws.subscription.data;

import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.nvsmx.ws.subscription.request.UpdateMonetaryBalanceRequest;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.StringWriter;

@XmlType(propOrder={"balanceId","subscriberId","serviceId", "serviceName","currentBalance", "previousBalance", "validToDate", "validFromDate","currency"})
public class UpdatedMonetaryBalanceData {
    private String balanceId;
    private String subscriberId;
    private String serviceId;
    private String serviceName;
    private double currentBalance;
    private double previousBalance;
    private long validToDate;
    private long validFromDate;
    private String currency;
    private UpdateMonetaryBalanceRequest request;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setValidFromDate(long validFromDate) {
        this.validFromDate = validFromDate;
    }

    public String getServiceId() {
        return serviceId;
    }

    public long getValidFromDate() {
        return validFromDate;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public double getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(double previousBalance) {
        this.previousBalance = previousBalance;
    }

    public long getValidToDate() {
        return validToDate;
    }

    public void setValidToDate(long validToDate) {
        this.validToDate = validToDate;
    }

    public String getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(String balanceId) {
        this.balanceId = balanceId;
    }

    @XmlTransient
    public UpdateMonetaryBalanceRequest getRequest() {
        return request;
    }

    public void setRequest(UpdateMonetaryBalanceRequest request) {
        this.request = request;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
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
        out.println("Balance Id = " + balanceId);
        out.println("Service Id = " + serviceId);
        out.println("Service Name = " + serviceName);
        out.println("Valid From Date = " + validFromDate);
        out.println("Valid To Date = " + validToDate);
        out.println("Previous Balance = " + previousBalance);
        out.println("Current Balance = " + currentBalance);
        out.println("Currency = " + currency);
    }

    public static UpdatedMonetaryBalanceData create(MonetaryBalance monetaryBalance) {
        UpdatedMonetaryBalanceData monetaryBalanceData = new UpdatedMonetaryBalanceData();
        monetaryBalanceData.setServiceId(monetaryBalance.getServiceId());
        monetaryBalanceData.setValidFromDate(monetaryBalance.getValidFromDate());
        return monetaryBalanceData;
    }

}
