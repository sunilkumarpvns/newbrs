package com.elitecore.corenetvertex.spr.util;

import com.elitecore.corenetvertex.constants.MonetaryBalanceType;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;

import java.util.Objects;

public class SubscriberMonetaryBalanceUtil {
    public static MonetaryBalance getDefaultBalance(String serviceId, SubscriberMonetaryBalance balanceFromDb){
        for(MonetaryBalance balance: balanceFromDb.getAllBalance()){
            if(Objects.equals(serviceId,balance.getServiceId()) && MonetaryBalanceType.DEFAULT.name().equals(balance.getType())){
                return balance;
            }
        }
        return null;
    }

    public static MonetaryBalance getMatchingBalance(SubscriberMonetaryBalance balanceFromDb, MonetaryBalance monetaryBalance){
        for(MonetaryBalance balance: balanceFromDb.getAllBalance()){
            if(Objects.equals(balance.getValidFromDate(), monetaryBalance.getValidFromDate())
                    && Objects.equals(balance.getValidToDate(), monetaryBalance.getValidToDate())
                    && Objects.equals(balance.getServiceId(), monetaryBalance.getServiceId())
                    && Objects.equals(balance.getCurrency(), monetaryBalance.getCurrency())
                    ){
                return balance;
            }
        }
        return null;
    }
}
