package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class SubscriptionCheckExistsStoredProcedure extends VoltProcedure {

    public final SQLStmt checkSubscriptionExistBySubscriberIdAndAddOnId = new SQLStmt(
            "SELECT SUBSCRIPTION_ID, " +
            "STATUS, " +
            "END_TIME " +
            "FROM TBLT_SUBSCRIPTION " +
            "WHERE SUBSCRIBER_ID=? " +
            "AND PACKAGE_ID=?");

    public VoltTable[] run(String subscriberId, String packageId) throws VoltAbortException {

        voltQueueSQL(checkSubscriptionExistBySubscriberIdAndAddOnId, subscriberId, packageId);
        return voltExecuteSQL();

    }

}
