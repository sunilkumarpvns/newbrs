package com.elitecore.corenetvertex.spr;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class DataBalanceOperation {

    private List<NonMonetoryBalance> nonMonetaryBalanceInsertOperation;
    private List<NonMonetoryBalance> nonMonetaryBalanceUpdateOperation;


    public DataBalanceOperation() {
        this.nonMonetaryBalanceInsertOperation = null;
        this.nonMonetaryBalanceUpdateOperation = null;
    }

    public List<NonMonetoryBalance> getNonMonetaryBalanceInsertOperation() {
        return nonMonetaryBalanceInsertOperation;
    }

    public List<NonMonetoryBalance> getNonMonetaryBalanceUpdateOperation() {
        return nonMonetaryBalanceUpdateOperation;
    }

    public void setNonMonetaryBalanceInsertOperations(NonMonetoryBalance nonMonetaryBalance) {
        if(CollectionUtils.isEmpty(nonMonetaryBalanceInsertOperation)) {
            nonMonetaryBalanceInsertOperation = new ArrayList<>();
        }
        nonMonetaryBalanceInsertOperation.add(nonMonetaryBalance);
    }

    public void setNonMonetaryBalanceUpdateOperations(NonMonetoryBalance nonMonetaryBalance) {
        if(CollectionUtils.isEmpty(nonMonetaryBalanceUpdateOperation)) {
            nonMonetaryBalanceUpdateOperation = new ArrayList<>();
        }
        nonMonetaryBalanceUpdateOperation.add(nonMonetaryBalance);
    }
}
