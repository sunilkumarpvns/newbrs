package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class SubscriberRestoreStoredProcedure extends VoltProcedure {

    public final SQLStmt restoreSubscriberProfile = new SQLStmt("UPDATE TBLM_SUBSCRIBER SET STATUS='INACTIVE' , MODIFIED_DATE=? WHERE SUBSCRIBERIDENTITY=? AND STATUS='DELETED'");

    public VoltTable[] run(String subscriberIdentity) throws VoltAbortException {

        voltQueueSQL(restoreSubscriberProfile, getTransactionTime(),subscriberIdentity);
        return voltExecuteSQL();

    }


}
