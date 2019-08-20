package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.sql.Timestamp;

public class MonetaryBalanceUpdateCreditLimitStoredProcedure extends VoltProcedure {
    public  final SQLStmt updateMonetaryBalance = new SQLStmt(
            "UPDATE TBLM_MONETARY_BALANCE SET " +
                    "CREDIT_LIMIT = ?, " +
                    "NEXT_BILL_CYCLE_CREDIT_LIMIT = ?, " +
                    "LAST_UPDATE_TIME = ?, " +
                    "CREDIT_LIMIT_UPDATE_TIME = ? " +
                    "WHERE SUBSCRIBER_ID = ? " +
                    "AND " +
                    "ID = ?");


    public VoltTable[] run(String subscriberID, String id, long creditLimit, long nextBillingCycleCreditLimit, Timestamp creditLimitUpdateTime)
            throws VoltProcedure.VoltAbortException {

        voltQueueSQL(updateMonetaryBalance, creditLimit, nextBillingCycleCreditLimit, getTransactionTime(), creditLimitUpdateTime, subscriberID, id);
        return voltExecuteSQL();
    }
}