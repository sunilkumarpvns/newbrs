package com.elitecore.corenetvertex.spr;

public class MonetaryBalanceBuilder {
    private String id;
    private String subscriberId;
    private String quotaProfileId;
    private String serviceId;
    private String packageId;
    private double previousBalance;
    private double availBalance;
    private double initialBalance;
    private double totalReservation;
    private long creditLimit;
    private long nextBillingCycleCreditLimit;
    private long validFromDate;
    private long validToDate;
    private String currency;
    private String type;
    private long lastUpdateTime;
    private long creditLimitUpdateTime;

    public MonetaryBalanceBuilder(String id, String subscriberId) {
        this.id = id;
        this.subscriberId = subscriberId;
    }

    public MonetaryBalanceBuilder withServiceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public MonetaryBalanceBuilder withAvailableBalance(double availableBalance) {
        this.availBalance = availableBalance;
        return this;
    }

    public MonetaryBalanceBuilder withInitialBalance(double initialBalance) {
        this.initialBalance = initialBalance;
        return this;
    }

    public MonetaryBalanceBuilder withTotalReservation(double totalReservation) {
        this.totalReservation = totalReservation;
        return this;
    }

    public MonetaryBalanceBuilder withCreditLimit(long creditLimit) {
        this.creditLimit = creditLimit;
        return this;
    }

    public MonetaryBalanceBuilder withNextBillingCycleCreditLimit(long nextBillingCycleCreditLimit) {
        this.nextBillingCycleCreditLimit = nextBillingCycleCreditLimit;
        return this;
    }

    public MonetaryBalanceBuilder withValidFromDate(long validFromDate) {
        this.validFromDate = validFromDate;
        return this;
    }

    public MonetaryBalanceBuilder withValidToDate(long validToDate) {
        this.validToDate = validToDate;
        return this;
    }

    public MonetaryBalanceBuilder withCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public MonetaryBalanceBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public MonetaryBalanceBuilder withLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
        return this;
    }

    public MonetaryBalanceBuilder withCreditLimitUpdateTime(long creditLimitUpdateTime) {
        this.creditLimitUpdateTime = creditLimitUpdateTime;
        return this;
    }

    public MonetaryBalance build() {
        return new MonetaryBalance(id, subscriberId, serviceId, availBalance,
            initialBalance, totalReservation, creditLimit, nextBillingCycleCreditLimit, validFromDate, validToDate, currency, type, lastUpdateTime, creditLimitUpdateTime, "", "");
    }

}