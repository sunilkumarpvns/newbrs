package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

public class MonetaryBalance implements ToStringable{
    private String id;
    private String subscriberId;
    private String serviceId;
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
    private final String parameter1;
    private final String parameter2;

    public MonetaryBalance(String id,
                           String subscriberId,
                           String serviceId,
                           double availBalance,
                           double initialBalance,
                           double totalReservation,
                           long creditLimit,
                           long nextBillingCycleCreditLimit,
                           long validFromDate,
                           long validToDate,
                           String currency,
                           String type,
                           long lastUpdateTime,
                           long creditLimitUpdateTime,
                           String parameter1,
                           String parameter2) {
        this.id = id;
        this.subscriberId = subscriberId;
        this.serviceId = serviceId;
        this.availBalance = availBalance;
        this.initialBalance = initialBalance;
        this.totalReservation = totalReservation;
        this.creditLimit = creditLimit;
        this.nextBillingCycleCreditLimit = nextBillingCycleCreditLimit;
        this.validFromDate = validFromDate;
        this.validToDate = validToDate;
        this.currency = currency;
        this.type = type;
        this.lastUpdateTime = lastUpdateTime;
        this.creditLimitUpdateTime = creditLimitUpdateTime;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public void setAvailBalance(double availBalance) {
        this.availBalance = availBalance;
    }

    public void setCreditLimit(long creditLimit) {
        this.creditLimit = creditLimit;
    }

    public void setInitialBalance(double initialBalance) {
        this.initialBalance = initialBalance;
    }

    public void setTotalReservation(double totalReservation) {
        this.totalReservation = totalReservation;
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

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public void setNextBillingCycleCreditLimit(long nextBillingCycleCreditLimit) {
        this.nextBillingCycleCreditLimit = nextBillingCycleCreditLimit;
    }

    public void setCreditLimitUpdateTime(long creditLimitUpdateTime) {
        this.creditLimitUpdateTime = creditLimitUpdateTime;
    }

    public String getId() {
        return id;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public double getAvailBalance() {
        return availBalance;
    }

    public double getInitialBalance() {
        return initialBalance;
    }

    public double getTotalReservation() {
        return totalReservation;
    }

    public long getCreditLimit() {
        return creditLimit;
    }

    public long getNextBillingCycleCreditLimit() {
        return nextBillingCycleCreditLimit;
    }

    public long getValidFromDate() {
        return validFromDate;
    }

    public long getValidToDate() {
        return validToDate;
    }

    public String getCurrency() {
        return currency;
    }

    public String getType() {
        return type;
    }

    public double getUsableBalance(){
        return getActualBalance()+creditLimit;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public long getCreditLimitUpdateTime() {
        return creditLimitUpdateTime;
    }

    public boolean isExist() {
        return getUsableBalance() > 0;
    }

    public String getServiceId() {
        return serviceId;
    }

    public double getActualBalance() {
        return availBalance-totalReservation;
    }

    public String getParameter1() {
        return parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

    public MonetaryBalance copy()  {
        return new MonetaryBalance(id,
                subscriberId,
                serviceId,
                availBalance,
                initialBalance,
                totalReservation,
                creditLimit,
                nextBillingCycleCreditLimit,
                validFromDate,
                validToDate,
                currency,
                type,
                lastUpdateTime,
                creditLimitUpdateTime,
                parameter1,
                parameter2);
    }

    @Override
    public String toString() {
        IndentingToStringBuilder indentingToStringBuilder = new IndentingToStringBuilder();
        indentingToStringBuilder.appendHeading(" -- Monetory Balance -- ");
        toString(indentingToStringBuilder);
        return indentingToStringBuilder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder out) {

        out.append("ID", id);
        out.append("Subscriber Identity", subscriberId);
        out.append("Service Id", serviceId);
        out.append("Available Balance", availBalance);
        out.append("Total Balance", initialBalance);
        out.append("Total Reservation", totalReservation);
        out.append("Credit Limit", creditLimit);
        out.append("Next Billing Cycle Credit Limit", nextBillingCycleCreditLimit);
        out.append("Valid From Date", validFromDate);
        out.append("Valid To Date", validToDate);
        out.append("Currency", currency);
        out.append("Type", type);
        out.append("Last Update Time", lastUpdateTime);
        out.append("Credit Limit Update Time", creditLimitUpdateTime);
    }

    public void substract(double value) {
        availBalance -= value;
    }

    public long maxAllowedBalance(double rate) {
        return (long) Math.floor((availBalance-totalReservation) / rate);
    }

    public void addReservation(double reservation) {
        totalReservation+=reservation;
    }

    public void substractReservation(double reservation) {
        totalReservation-=reservation;
    }


    public static class MonetaryBalanceBuilder {
        private final String id;
        private String subscriberId;
        private double availBalance;
        private double totalBalance;
        private double totalReservation;
        private long creditLimit;
        private long nextBillingCycleCreditLimit;
        private long validFromDate;
        private long validToDate;
        private String serviceId;
        private String currency;
        private String type;
        private long lastUpdateTime;
        private long creditLimitUpdateTime;
        private String parameter1;
        private String parameter2;

        public MonetaryBalanceBuilder(String id,
                                      String subscriberId,
                                      String serviceId,
                                      String currency,
                                      String type,
                                      long lastUpdateTime) {
            this.id = id;
            this.subscriberId = subscriberId;
            this.serviceId = serviceId;
            this.currency = currency;
            this.type = type;
            this.lastUpdateTime = lastUpdateTime;
        }


        public MonetaryBalanceBuilder withAvailableBalance(double availableBalance) {
            this.availBalance = availableBalance;
            return this;
        }

        public MonetaryBalanceBuilder withTotalBalance(double totalBalance) {
            this.totalBalance = totalBalance;
            return this;
        }

        public MonetaryBalanceBuilder withTotalReservation(double totalReservation) {
            this.totalReservation = totalReservation;
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

        public MonetaryBalanceBuilder withCreditLimit(long creditLimit) {
            this.creditLimit = creditLimit;
            return this;
        }

        public MonetaryBalanceBuilder withCreditLimitUpdateTime(long creditLimitUpdateTime) {
            this.creditLimitUpdateTime = creditLimitUpdateTime;
            return this;
        }

        public MonetaryBalanceBuilder withNextBillingCycleCreditLimit(long nextBillingCycleCreditLimit) {
            this.nextBillingCycleCreditLimit = nextBillingCycleCreditLimit;
            return this;
        }

        public MonetaryBalanceBuilder withParameter1(String parameter1) {
            this.parameter1 = parameter1;
            return this;
        }

        public MonetaryBalanceBuilder withParameter2(String parameter2) {
            this.parameter2 = parameter2;
            return this;
        }

        public MonetaryBalance build() {
            return new MonetaryBalance(this.id,
                    this.subscriberId,
                    this.serviceId,
                    //this.previousBalance,
                    this.availBalance,
                    this.totalBalance,
                    this.totalReservation,
                    this.creditLimit,
                    this.nextBillingCycleCreditLimit,
                    this.validFromDate,
                    this.validToDate,
                    this.currency,
                    this.type,
                    this.lastUpdateTime,
                    this.creditLimitUpdateTime,
                    this.parameter1,
                    this.parameter2
            );
        }

    }
}

