package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class RechargeMonetaryBalanceAndUpdateSubscriberValidityStoredProcedure extends VoltProcedure {
    public final SQLStmt RECHARGE_MONETARY_BALANCE = new SQLStmt(MonetaryVoltStatements.RECHARGE_MONETARY_BALANCE);
    public final SQLStmt UPDATE_SUBSCRIBER_VALIDITY = new SQLStmt(SubscriberVoltStatements.SUBSCRIBER_UPDATE_EXPIRY_DATE);

    public VoltTable[] run(String subscriberIdentity, String id, BigDecimal price, BigDecimal amount, Timestamp extendedValidity, Timestamp subscriberValidity) throws VoltAbortException {

        voltQueueSQL(RECHARGE_MONETARY_BALANCE, price, amount, extendedValidity, subscriberIdentity, id);
        voltQueueSQL(UPDATE_SUBSCRIBER_VALIDITY,subscriberValidity, getTransactionTime(), subscriberIdentity);
        return voltExecuteSQL();
    }

}