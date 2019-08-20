package com.elitecore.corenetvertex.spr;


import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@com.elitecore.corenetvertex.spr.Table(name = "TBLM_MONETARY_BALANCE_HISTORY")
@Table(name = "TBLM_MONETARY_BALANCE_HISTORY")
public class TblmMonetaryBalanceHistoryEntity {
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
	private Timestamp createdDate;
    private String type;
    private Long creditLimit;
    private Long nextBillingCycleCreditLimit;
    private Timestamp creditLimitUpdateTime;

    @Id
    @Column(name = "ID", nullable = false, length = 36)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "SUBSCRIBER_ID", nullable = true, length = 255)
    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    @Basic
    @Column(name = "SERVICE_ID", nullable = true, length = 36)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Basic
    @Column(name = "AVAILABLE_BALANCE", nullable = true, precision = 0)
    public double getAvailBalance() { return availBalance; }

    public void setAvailBalance(double availBalance) {  this.availBalance = availBalance; }

    @Basic
    @Column(name = "INITIAL_BALANCE", nullable = true, precision = 0)
    public double getInitialBalance() { return initialBalance; }

    public void setInitialBalance(double initialBalance) {  this.initialBalance = initialBalance; }

    @Basic
    @Column(name = "TOTAL_RESERVATION", nullable = true, precision = 0)
    public double getTotalReservation() { return totalReservation; }

    public void setTotalReservation(double totalReservation) {  this.totalReservation = totalReservation; }

    @Basic
    @Column(name = "VALID_FROM_DATE", nullable = true)
    public Timestamp getValidFromDate() { return validFromDate; }

    public void setValidFromDate(Timestamp validFromDate) {  this.validFromDate = validFromDate; }

    @Basic
    @Column(name = "VALID_TO_DATE", nullable = true)
    public Timestamp getValidToDate() { return validToDate; }

    public void setValidToDate(Timestamp validToDate) {  this.validToDate = validToDate; }

    @Basic
    @Column(name = "CURRENCY", nullable = true, length = 5)
    public String getCurrency() { return currency; }

    public void setCurrency(String currency) {  this.currency = currency; }

    @Basic
    @Column(name = "LAST_UPDATE_TIME ", nullable = true)
    public Timestamp getLastUpdateTime() { return lastUpdateTime; }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {  this.lastUpdateTime = lastUpdateTime; }

    @Basic
    @Column(name = "PARAM1", nullable = true, length = 255)
    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    @Basic
    @Column(name = "PARAM2", nullable = true, length = 255)
    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

	@Column(name = "CREATE_DATE", nullable = true, length = 255)
	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

    @Column(name = "TYPE", nullable = true, length = 32)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "CREDIT_LIMIT", nullable = true)
    public Long getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Long creditLimit) {
        this.creditLimit = creditLimit;
    }

    @Basic
    @Column(name = "NEXT_BILL_CYCLE_CREDIT_LIMIT", nullable = true)
    public Long getNextBillingCycleCreditLimit() {
        return nextBillingCycleCreditLimit;
    }

    public void setNextBillingCycleCreditLimit(Long nextBillingCycleCreditLimit) {
        this.nextBillingCycleCreditLimit = nextBillingCycleCreditLimit;
    }

    @Basic
    @Column(name = "CREDIT_LIMIT_UPDATE_TIME", nullable = true)
    public Timestamp getCreditLimitUpdateTime() {
        return creditLimitUpdateTime;
    }

    public void setCreditLimitUpdateTime(Timestamp creditLimitUpdateTime) {
        this.creditLimitUpdateTime = creditLimitUpdateTime;
    }
}