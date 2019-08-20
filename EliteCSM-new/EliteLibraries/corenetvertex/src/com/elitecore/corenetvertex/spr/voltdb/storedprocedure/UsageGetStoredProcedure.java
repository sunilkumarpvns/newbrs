package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class UsageGetStoredProcedure extends VoltProcedure {

    public final SQLStmt selectQuery = new SQLStmt(
            "SELECT * FROM TBLT_USAGE WHERE SUBSCRIBER_ID = ?");

    public VoltTable[] run(String subscriberId) throws VoltAbortException {
        voltQueueSQL(selectQuery, subscriberId);
        return voltExecuteSQL(true);
    }
}