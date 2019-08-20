package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class UsageDeleteStoredProcedure extends VoltProcedure {

    public final SQLStmt deleteQuery = new SQLStmt(
            "DELETE FROM TBLT_USAGE WHERE SUBSCRIBER_ID = ? AND SUBSCRIPTION_ID IS NULL");

    public VoltTable[] run(String subscriberId) throws VoltAbortException {
        voltQueueSQL(deleteQuery, subscriberId);
        return voltExecuteSQL(true);
    }
}
