package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class IsVoltDBAvailable extends VoltProcedure {

    public final SQLStmt isAvailableQuery = new SQLStmt(
            "select 1 from IS_AVAILABLE");

    public VoltTable[] run(String id) throws VoltAbortException {//NOSONAR -- FIRST ARGUMENT SHOULD ALWAYS BE PARTITION KEY

        voltQueueSQL(isAvailableQuery);

        return voltExecuteSQL();
    }
}
