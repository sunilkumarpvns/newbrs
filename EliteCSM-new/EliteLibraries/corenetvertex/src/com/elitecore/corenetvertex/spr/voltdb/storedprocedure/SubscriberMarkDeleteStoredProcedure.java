package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class SubscriberMarkDeleteStoredProcedure extends VoltProcedure {
    public final SQLStmt markSubscriberProfileDeleted = new SQLStmt("UPDATE TBLM_SUBSCRIBER SET STATUS = 'DELETED', MODIFIED_DATE= ? WHERE SUBSCRIBERIDENTITY = ?");

    public VoltTable[] run(String subscriberIdentity) throws VoltAbortException {

        voltQueueSQL(markSubscriberProfileDeleted,getTransactionTime(), subscriberIdentity);
        return voltExecuteSQL();

    }
}
