package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class MonetaryBalanceDirectDebitStoredProcedure extends VoltProcedure {
    public  final SQLStmt directDebitMonetaryBalance = new SQLStmt(
            "UPDATE TBLM_MONETARY_BALANCE " +
                    "SET AVAILABLE_BALANCE = AVAILABLE_BALANCE - ?, " +
                    "LAST_UPDATE_TIME = ? " +
                    "WHERE SUBSCRIBER_ID = ? " +
                    "AND ID = ?" );


    public VoltTable[] run(String subscriberId, String id, double debitAmount)
            throws VoltProcedure.VoltAbortException {

        voltQueueSQL(directDebitMonetaryBalance, debitAmount, getTransactionTime(), subscriberId, id);
        return voltExecuteSQL();
    }
}
