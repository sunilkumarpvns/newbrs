package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.sql.Timestamp;

public class MonetaryBalanceUpdateStoredProcedure extends VoltProcedure {
    private final SQLStmt updateMonetaryBalance = new SQLStmt(MonetaryVoltStatements.UPDATE_MONETARY_BALANCE);

    public VoltTable[] run(String subscriberID, String id, double availableBalance, double initialBalance, Timestamp validToDate)
            throws VoltProcedure.VoltAbortException {

        voltQueueSQL(updateMonetaryBalance, availableBalance, initialBalance, validToDate, getTransactionTime(), subscriberID, id);
        return voltExecuteSQL();
    }
}
