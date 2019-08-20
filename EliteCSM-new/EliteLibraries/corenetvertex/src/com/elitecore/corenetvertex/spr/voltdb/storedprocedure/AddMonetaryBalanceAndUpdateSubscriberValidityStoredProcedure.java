package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class AddMonetaryBalanceAndUpdateSubscriberValidityStoredProcedure extends VoltProcedure {
    public final SQLStmt ADD_MONETARY_BALANCE = new SQLStmt(MonetaryVoltStatements.ADD_MONETARY_BALANCE);
    public final SQLStmt UPDATE_SUBSCRIBER_VALIDITY = new SQLStmt(SubscriberVoltStatements.SUBSCRIBER_UPDATE_EXPIRY_DATE);

    public VoltTable[] run(String subscriberID, String[] args, Timestamp validFromDate, Timestamp validToDate, Timestamp creditLimitUpdateTime, Timestamp subscriberValidity) throws VoltAbortException {

        MonetaryVoltUtil.createAndQueAddStatement(this,ADD_MONETARY_BALANCE, args, validFromDate, validToDate, creditLimitUpdateTime);

        voltQueueSQL(UPDATE_SUBSCRIBER_VALIDITY,subscriberValidity, getTransactionTime(), subscriberID);
        return voltExecuteSQL();
    }

}