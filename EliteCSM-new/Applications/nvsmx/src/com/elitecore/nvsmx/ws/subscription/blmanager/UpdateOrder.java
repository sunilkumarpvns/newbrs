package com.elitecore.nvsmx.ws.subscription.blmanager;

import com.elitecore.corenetvertex.constants.MonetaryBalanceType;
import com.elitecore.corenetvertex.spr.MonetaryBalance;

import java.util.List;
import java.util.Objects;

enum UpdateOrder {

    OLDEST("OLDEST") {
        @Override
        MonetaryBalance getBalance(List<MonetaryBalance> monetaryBalances, String serviceId, OperationType operationType) {

            MonetaryBalance oldestBalance = null;
            long currentTime = System.currentTimeMillis();

            for (MonetaryBalance monetaryBalance : monetaryBalances) {

                if (Objects.equals(monetaryBalance.getServiceId(),serviceId)==false) {
                    continue;
                }

                if (monetaryBalance.getValidToDate()<currentTime) {
                    continue;
                }

                if(MonetaryBalanceType.DEFAULT.name().equals(monetaryBalance.getType())){
                    return monetaryBalance;
                }

                if (oldestBalance == null || monetaryBalance.getValidFromDate() < oldestBalance.getValidFromDate()) {
                    oldestBalance = monetaryBalance;
                } else if (monetaryBalance.getValidFromDate() == oldestBalance.getValidFromDate()) {
                    if (OperationType.CREDIT == operationType && monetaryBalance.getValidToDate() > oldestBalance.getValidToDate()) {
                        oldestBalance = monetaryBalance;
                    }
                }
            }

            return oldestBalance;

        }
    },
    LATEST("LATEST") {
        @Override
        MonetaryBalance getBalance(List<MonetaryBalance> monetaryBalances, String serviceId, OperationType operationType) {

            MonetaryBalance latestBalance = null;
            long currentTime = System.currentTimeMillis();

            for (MonetaryBalance monetaryBalance : monetaryBalances) {

                if (Objects.equals(monetaryBalance.getServiceId(),serviceId)==false) {
                    continue;
                }

                if (monetaryBalance.getValidToDate()<currentTime) {
                    continue;
                }

                if(MonetaryBalanceType.DEFAULT.name().equals(monetaryBalance.getType())){
                    return monetaryBalance;
                }

                if (latestBalance == null || monetaryBalance.getValidFromDate() > latestBalance.getValidFromDate()) {
                    latestBalance = monetaryBalance;
                } else if (monetaryBalance.getValidFromDate() == latestBalance.getValidFromDate()) {
                    if (OperationType.CREDIT == operationType && monetaryBalance.getValidToDate() > latestBalance.getValidToDate()) {
                        latestBalance = monetaryBalance;
                    }
                }
            }

            return latestBalance;

        }
    };

    private String name;

    UpdateOrder(String name) {
        this.name = name;
    }

    static UpdateOrder fromVal(String name) {

        if (OLDEST.name.equalsIgnoreCase(name)) {
            return OLDEST;
        } else if (LATEST.name.equalsIgnoreCase(name)) {
            return LATEST;
        }

        return null;
    }

    public static String getNames() {
        return OLDEST.name + ", " + LATEST.name;
    }

    abstract MonetaryBalance getBalance(List<MonetaryBalance> monetaryBalances, String serviceId, OperationType operationType);
}
