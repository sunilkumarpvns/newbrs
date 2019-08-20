package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class SubscriberFetchDeletedMarkProfileStoredProcedure extends VoltProcedure {

    public final SQLStmt fetchDeletedMarkSubscriberProfile = new SQLStmt("SELECT * FROM TBLM_SUBSCRIBER WHERE STATUS = 'DELETED' ");

    public VoltTable[] run() throws VoltAbortException {

        voltQueueSQL(fetchDeletedMarkSubscriberProfile);
        return voltExecuteSQL();

    }
}
