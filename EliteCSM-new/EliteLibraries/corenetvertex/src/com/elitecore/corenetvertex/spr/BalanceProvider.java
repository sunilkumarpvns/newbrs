package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;

import java.util.function.Predicate;

public interface BalanceProvider {
    SubscriberMonetaryBalance getMonetaryBalance(String subscriberId, Predicate<MonetaryBalance> predicate) throws OperationFailedException;

    SubscriberNonMonitoryBalance getNonMonetaryBalance(SPRInfo sprInfo) throws OperationFailedException;

    SubscriberRnCNonMonetaryBalance getRnCNonMonetaryBalance(String subscriberId) throws OperationFailedException;

    public static class DummyBalanceProvider implements BalanceProvider {
        private static final DummyBalanceProvider DUMMY_MONETARY_BALANCE_PROVIDER = new DummyBalanceProvider();

        private DummyBalanceProvider() {}

        @Override
        public SubscriberMonetaryBalance getMonetaryBalance(String subscriberId, Predicate<MonetaryBalance> predicate) throws OperationFailedException{
            throw new OperationFailedException("get monetary balance operation is not supported for subscriber ID: " + subscriberId, ResultCode.OPERATION_NOT_SUPPORTED);
        }

        @Override
        public SubscriberNonMonitoryBalance getNonMonetaryBalance(SPRInfo sprInfo) throws OperationFailedException {
            throw new OperationFailedException("get not monetary balance operation is not supported for subscriber ID: " + sprInfo.getSubscriberIdentity(), ResultCode.OPERATION_NOT_SUPPORTED);
        }

        @Override
        public SubscriberRnCNonMonetaryBalance getRnCNonMonetaryBalance(String subscriberId) throws OperationFailedException {
            throw new OperationFailedException("get RnC not monetary balance operation is not supported for subscriber ID: " + subscriberId, ResultCode.OPERATION_NOT_SUPPORTED);
        }

        public static BalanceProvider instance() {
            return DUMMY_MONETARY_BALANCE_PROVIDER;
        }
    }
}
