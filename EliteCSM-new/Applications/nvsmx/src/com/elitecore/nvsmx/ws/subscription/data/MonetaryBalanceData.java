package com.elitecore.nvsmx.ws.subscription.data;

import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.corenetvertex.spr.MonetaryBalance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.StringWriter;

@XmlType(propOrder={"id","serviceId", "serviceName","availBalance","totalBalance", "actualBalance","creditLimit","validFromDate","validToDate","parameter1","parameter2","currency"})
public class MonetaryBalanceData {
    private String id;
    private String serviceId;
    private String serviceName;
    private double availBalance;
    private double totalBalance;
    private double actualBalance;
    private long creditLimit;
    private long validFromDate;
    private long validToDate;
    private String parameter1;
    private String parameter2;
    private String currency;

    @XmlElement(name="id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name="serviceName")
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }


    public void setAvailBalance(double availBalance) {
        this.availBalance = availBalance;
    }

    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
    }

    public void setActualBalance(double actualBalance) {
        this.actualBalance = actualBalance;
    }

    public void setValidFromDate(long validFromDate) {
        this.validFromDate = validFromDate;
    }

    public void setValidToDate(long validToDate) {
        this.validToDate = validToDate;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @XmlElement(name="serviceId")
    public String getServiceId() {
        return serviceId;
    }

    @XmlElement(name="availBalance")
    public double getAvailBalance() {
        return availBalance;
    }

    @XmlElement(name="totalBalance")
    public double getTotalBalance() {
        return totalBalance;
    }

    @XmlElement(name="actualBalance")
    public double getActualBalance() {
        return actualBalance;
    }

    @XmlElement(name="creditLimit")
    public long getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(long creditLimit) {
        this.creditLimit = creditLimit;
    }

    @XmlElement(name="validFromDate")
    public long getValidFromDate() {
        return validFromDate;
    }

    @XmlElement(name="validToDate")
    public long getValidToDate() {
        return validToDate;
    }

    @XmlElement(name="currency")
    public String getCurrency() {
        return currency;
    }

    @XmlElement(name="parameter1")
    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    @XmlElement(name="parameter2")
    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    public boolean isExist() {
        return availBalance > 0;
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
        out.println("Id = " + id);
        out.println("Service Id = " + serviceId);
        out.println("Service Name = " + serviceName);
        out.println("Available Balance = " + availBalance);
        out.println("Total Balance = " + totalBalance);
        out.println("Actual Balance = " + actualBalance);
        out.println("Valid From Date = " + validFromDate);
        out.println("Valid To Date = " + validToDate);
        out.println("Credit Limit = " + creditLimit);
        out.println("Parameter1 = " + parameter1);
        out.println("Parameter2 = " + parameter2);
        out.println("Currency = " + currency);
    }

    public static MonetaryBalanceData create(MonetaryBalance monetaryBalance) {
        MonetaryBalanceData monetaryBalanceData = new MonetaryBalanceData();
        monetaryBalanceData.setId(monetaryBalance.getId());
        monetaryBalanceData.setServiceId(monetaryBalance.getServiceId());
        monetaryBalanceData.setAvailBalance(monetaryBalance.getAvailBalance());
        monetaryBalanceData.setTotalBalance(monetaryBalance.getInitialBalance());
        monetaryBalanceData.setActualBalance(monetaryBalance.getActualBalance());
        monetaryBalanceData.setValidFromDate(monetaryBalance.getValidFromDate());
        monetaryBalanceData.setValidToDate(monetaryBalance.getValidToDate());
        monetaryBalanceData.setParameter1(monetaryBalance.getParameter1());
        monetaryBalanceData.setParameter2(monetaryBalance.getParameter2());
        monetaryBalanceData.setCurrency(monetaryBalance.getCurrency());
        monetaryBalanceData.setCreditLimit(monetaryBalance.getCreditLimit());
        return monetaryBalanceData;
    }

}
