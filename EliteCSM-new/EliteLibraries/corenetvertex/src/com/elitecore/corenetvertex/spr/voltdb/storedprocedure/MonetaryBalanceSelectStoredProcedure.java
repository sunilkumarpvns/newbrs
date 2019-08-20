package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class MonetaryBalanceSelectStoredProcedure extends VoltProcedure {
    public final SQLStmt subscriberProfileById = new SQLStmt("SELECT * FROM TBLM_MONETARY_BALANCE WHERE SUBSCRIBER_ID = ?");

    public VoltTable[] run(String subscriberIdentity) throws VoltProcedure.VoltAbortException {

        voltQueueSQL(subscriberProfileById, subscriberIdentity);
        return voltExecuteSQL();
    }
}
