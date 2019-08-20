package com.elitecore.corenetvertex.spr.params;

import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfo;

import java.math.BigDecimal;

public class MonetaryRechargeData {
    private final String subscriberId;
    private final MonetaryBalance monetaryBalance;
    private final String remark;
    private final BigDecimal price;
    private final BigDecimal amount;
    private final String monetaryRechargePlanName;
    private final long extendedValidity;
    private final String requestIPAddress;
    private final String operation;
    private SPRInfo sprInfo;
    private String action;

    public MonetaryRechargeData(String subscriberId, MonetaryBalance monetaryBalance, String remark, BigDecimal price,
                                BigDecimal amount, String monetaryRechargePlanName, long extendedValidity,
                                String requestIPAddress, String operation, String action) {
        this.subscriberId = subscriberId;
        this.monetaryBalance = monetaryBalance;
        this.remark = remark;
        this.price = price;
        this.amount = amount;
        this.monetaryRechargePlanName = monetaryRechargePlanName;
        this.extendedValidity = extendedValidity;
        this.requestIPAddress = requestIPAddress;
        this.operation = operation;
        this.action = action;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public MonetaryBalance getMonetaryBalance() {
        return monetaryBalance;
    }

    public String getRemark() {
        return remark;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getMonetaryRechargePlanName() {
        return monetaryRechargePlanName;
    }

    public long getExtendedValidity() {
        return extendedValidity;
    }

    public String getRequestIPAddress() {
        return requestIPAddress;
    }

    public String getOperation() {
        return operation;
    }

    public SPRInfo getSprInfo() {
        return sprInfo;
    }

    public void setSprInfo(SPRInfo sprInfo) {
        this.sprInfo = sprInfo;
    }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }
}
