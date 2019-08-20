package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class UsageDeleteByPackageIdStoredProcedure extends VoltProcedure {

    public final SQLStmt deleteQuery = new SQLStmt(
            "DELETE FROM TBLT_USAGE WHERE SUBSCRIBER_ID = ? AND PACKAGE_ID = ?");

    public VoltTable[] run(String subscriberId, String packageId) throws VoltAbortException {
        voltQueueSQL(deleteQuery, subscriberId, packageId);
        return voltExecuteSQL(true);
    }
}