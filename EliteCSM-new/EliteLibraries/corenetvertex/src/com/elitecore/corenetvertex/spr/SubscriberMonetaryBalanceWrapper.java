package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.spr.data.SPRInfo;

/**
 * @author Prakashkumar Pala
 * @since 23-Oct-2018
 * Wrapper Class to store current and previous monetary balacne as well as additional info like SPR, operation and action.
 */
public class SubscriberMonetaryBalanceWrapper {
    private MonetaryBalance currentMonetaryBalance;
    private MonetaryBalance previousMonetaryBalance;
    private SPRInfo sprInfo;
    private String operation;
    private String action;
    private String transactionId;

    public SubscriberMonetaryBalanceWrapper() {
    }

    public SubscriberMonetaryBalanceWrapper(MonetaryBalance currentMonetaryBalance, MonetaryBalance previousMonetaryBalance
            , SPRInfo sprInfo, String operation, String action,String transactionId) {
        this.currentMonetaryBalance = currentMonetaryBalance;
        this.previousMonetaryBalance = previousMonetaryBalance;
        this.sprInfo = sprInfo;
        this.operation = operation;
        this.action = action;
        this.transactionId=transactionId;
    }

    public MonetaryBalance getPreviousMonetaryBalance() {
        return previousMonetaryBalance;
    }

    public MonetaryBalance getCurrentMonetaryBalance() {
        return currentMonetaryBalance;
    }

    public SPRInfo getSprInfo() {
        return sprInfo;
    }

    public void setSprInfo(SPRInfo sprInfo) {
        this.sprInfo = sprInfo;
    }

    public String getOperation() { return operation; }

    public void setOperation(String operation) { this.operation = operation; }

    public String getAction() { return action; }

    public String getTransactionId() { return transactionId; }
}
