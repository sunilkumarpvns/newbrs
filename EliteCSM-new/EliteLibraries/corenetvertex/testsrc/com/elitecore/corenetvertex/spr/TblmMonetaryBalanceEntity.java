package com.elitecore.corenetvertex.spr;


import com.elitecore.corenetvertex.util.QueryBuilder;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.sql.Types;

@Entity
@Table(name = "TBLM_MONETARY_BALANCE")
@com.elitecore.corenetvertex.spr.Table(name="TBLM_MONETARY_BALANCE")
public class TblmMonetaryBalanceEntity {
    private String id;
    private String subscriberId;
    private String serviceId;
    private double availBalance;
    private double initialBalance;
    private double totalReservation;
    private Timestamp validFromDate;
    private Timestamp validToDate;
    private String currency;
    private Timestamp lastUpdateTime;
    private String parameter1;
    private String parameter2;
    private String type;
    private Long creditLimit;
    private Long nextBillingCycleCreditLimit;
    private Timestamp creditLimitUpdateTime;

    @Id
    @Column(name = "ID", nullable = false, length = 36)
    @com.elitecore.commons.kpi.annotation.Column(name = "ID", type = Types.VARCHAR)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "SUBSCRIBER_ID", nullable = true, length = 255)
    @com.elitecore.commons.kpi.annotation.Column(name = "SUBSCRIBER_ID", type = Types.VARCHAR)
    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    @Basic
    @Column(name = "SERVICE_ID", nullable = true, length = 36)
    @com.elitecore.commons.kpi.annotation.Column(name = "SERVICE_ID", type = Types.VARCHAR)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Basic
    @Column(name = "AVAILABLE_BALANCE", nullable = true, precision = 0)
    @com.elitecore.commons.kpi.annotation.Column(name = "AVAILABLE_BALANCE", type = Types.NUMERIC)
    public double getAvailBalance() { return availBalance; }

    public void setAvailBalance(double availBalance) {  this.availBalance = availBalance; }

    @Basic
    @Column(name = "INITIAL_BALANCE", nullable = true, precision = 0)
    @com.elitecore.commons.kpi.annotation.Column(name = "INITIAL_BALANCE", type = Types.NUMERIC)
    public double getInitialBalance() { return initialBalance; }

    public void setInitialBalance(double initialBalance) {  this.initialBalance = initialBalance; }

    @Basic
    @Column(name = "TOTAL_RESERVATION", nullable = true, precision = 0)
    @com.elitecore.commons.kpi.annotation.Column(name = "TOTAL_RESERVATION", type = Types.NUMERIC)
    public double getTotalReservation() { return totalReservation; }

    public void setTotalReservation(double totalReservation) {  this.totalReservation = totalReservation; }

    @Basic
    @Column(name = "VALID_FROM_DATE", nullable = true)
    @com.elitecore.commons.kpi.annotation.Column(name = "VALID_FROM_DATE", type = Types.TIMESTAMP)
    public Timestamp getValidFromDate() { return validFromDate; }

    public void setValidFromDate(Timestamp validFromDate) {  this.validFromDate = validFromDate; }

    @Basic
    @Column(name = "VALID_TO_DATE", nullable = true)
    @com.elitecore.commons.kpi.annotation.Column(name = "VALID_TO_DATE", type = Types.TIMESTAMP)
    public Timestamp getValidToDate() { return validToDate; }

    public void setValidToDate(Timestamp validToDate) {  this.validToDate = validToDate; }

    @Basic
    @Column(name = "CURRENCY", nullable = true, length = 5)
    @com.elitecore.commons.kpi.annotation.Column(name = "CURRENCY", type = Types.VARCHAR)
    public String getCurrency() { return currency; }

    public void setCurrency(String currency) {  this.currency = currency; }

    @Basic
    @Column(name = "LAST_UPDATE_TIME ", nullable = true)
    @com.elitecore.commons.kpi.annotation.Column(name = "LAST_UPDATE_TIME", type = Types.TIMESTAMP)
    public Timestamp getLastUpdateTime() { return lastUpdateTime; }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {  this.lastUpdateTime = lastUpdateTime; }

    @Basic
    @Column(name = "PARAM1", nullable = true, length = 255)
    @com.elitecore.commons.kpi.annotation.Column(name = "PARAM1", type = Types.VARCHAR)
    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    @Basic
    @Column(name = "PARAM2", nullable = true, length = 255)
    @com.elitecore.commons.kpi.annotation.Column(name = "PARAM2", type = Types.VARCHAR)
    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    @Basic
    @Column(name = "TYPE", nullable = true, length = 32)
    @com.elitecore.commons.kpi.annotation.Column(name = "TYPE", type = Types.VARCHAR)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Basic
    @Column(name = "CREDIT_LIMIT", nullable = true)
    @com.elitecore.commons.kpi.annotation.Column(name = "CREDIT_LIMIT", type = Types.NUMERIC)
    public Long getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Long creditLimit) {
        this.creditLimit = creditLimit;
    }

    @Basic
    @Column(name = "NEXT_BILL_CYCLE_CREDIT_LIMIT", nullable = true)
    @com.elitecore.commons.kpi.annotation.Column(name = "NEXT_BILL_CYCLE_CREDIT_LIMIT", type = Types.NUMERIC)
    public Long getNextBillingCycleCreditLimit() {
        return nextBillingCycleCreditLimit;
    }

    public void setNextBillingCycleCreditLimit(Long nextBillingCycleCreditLimit) {
        this.nextBillingCycleCreditLimit = nextBillingCycleCreditLimit;
    }

    @Basic
    @Column(name = "CREDIT_LIMIT_UPDATE_TIME", nullable = true)
    @com.elitecore.commons.kpi.annotation.Column(name = "CREDIT_LIMIT_UPDATE_TIME", type = Types.TIMESTAMP)
    public Timestamp getCreditLimitUpdateTime() {
        return creditLimitUpdateTime;
    }

    public void setCreditLimitUpdateTime(Timestamp creditLimitUpdateTime) {
        this.creditLimitUpdateTime = creditLimitUpdateTime;
    }

    public static TblmMonetaryBalanceEntity from(MonetaryBalance monetaryBalance, double price, double amount, long validityToBeExtended) {
        TblmMonetaryBalanceEntity monetaryBalanceEntity = new TblmMonetaryBalanceEntity();
        monetaryBalanceEntity.setId(monetaryBalance.getId());
        monetaryBalanceEntity.setSubscriberId(monetaryBalance.getSubscriberId());
        monetaryBalanceEntity.setServiceId(monetaryBalance.getServiceId());
        monetaryBalanceEntity.setAvailBalance(monetaryBalance.getAvailBalance() - price + amount);
        monetaryBalanceEntity.setInitialBalance(monetaryBalance.getInitialBalance());
        monetaryBalanceEntity.setTotalReservation(monetaryBalance.getTotalReservation());
        monetaryBalanceEntity.setValidFromDate(new Timestamp(monetaryBalance.getValidFromDate()));
        monetaryBalanceEntity.setValidToDate(new Timestamp(monetaryBalance.getValidToDate() + validityToBeExtended));
        monetaryBalanceEntity.setCurrency(monetaryBalance.getCurrency());

        return monetaryBalanceEntity;
    }

    public static String createTableQuery() {
        return QueryBuilder.buildCreateQuery(TblmMonetaryBalanceEntity.class);
    }

    public static String dropTableQuery() {
        return QueryBuilder.buildDropQuery(TblmMonetaryBalanceEntity.class);
    }
}