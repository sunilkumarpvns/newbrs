package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class SubscriptionSelectBySubscriptionIdStoredProcedure extends VoltProcedure {


    public final SQLStmt selectBySubscriptionId = new SQLStmt("SELECT * FROM TBLT_SUBSCRIPTION WHERE SUBSCRIBER_ID=? AND SUBSCRIPTION_ID=?");

    public VoltTable[] run(String subscriberId,String subscriptionId) throws VoltAbortException {

        voltQueueSQL(selectBySubscriptionId,subscriberId,subscriptionId);
        return voltExecuteSQL();

    }
}
