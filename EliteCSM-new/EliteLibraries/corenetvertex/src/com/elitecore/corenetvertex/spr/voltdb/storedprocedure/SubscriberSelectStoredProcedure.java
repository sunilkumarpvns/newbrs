package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class SubscriberSelectStoredProcedure extends VoltProcedure {
    public final SQLStmt subscriberProfileById = new SQLStmt("SELECT * FROM TBLM_SUBSCRIBER WHERE SUBSCRIBERIDENTITY=?");

    public VoltTable[] run(String subscriberIdentity) throws VoltAbortException {

        voltQueueSQL(subscriberProfileById, subscriberIdentity);
        return voltExecuteSQL();

    }

}
