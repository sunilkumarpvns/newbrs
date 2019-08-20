package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class SubscriberPurgeAndDeleteUsageStoredProcedure extends VoltProcedure {

    public final SQLStmt deleteUsageQuery = new SQLStmt("DELETE FROM TBLT_USAGE WHERE SUBSCRIBER_ID = ? AND SUBSCRIPTION_ID IS NULL");

    public final SQLStmt purgeSubscriberQuery = new SQLStmt("DELETE FROM TBLM_SUBSCRIBER WHERE SUBSCRIBERIDENTITY=? AND STATUS ='DELETED'");

    public final SQLStmt unsubscribeBySubscriberId = new SQLStmt("UPDATE TBLT_SUBSCRIPTION  SET STATUS=5, LAST_UPDATE_TIME=? WHERE SUBSCRIBER_ID=?");

    public static final SQLStmt deleteSubscriberMonetaryBalance = new SQLStmt("DELETE FROM TBLM_MONETARY_BALANCE WHERE SUBSCRIBER_ID = ?");

    public long run(String subscriberId) throws VoltAbortException {
        voltQueueSQL(purgeSubscriberQuery, subscriberId);
        VoltTable[] purgeSubscriberResults = voltExecuteSQL();
        VoltTable purgeSubscriberResult = purgeSubscriberResults[0];
        long noOfUpdatedSubscribers = purgeSubscriberResult.asScalarLong();
        if (noOfUpdatedSubscribers == 0) {
            throw new VoltAbortException("No Subscriber found with id: "+subscriberId);
        }
        voltQueueSQL(deleteUsageQuery, subscriberId);
        voltQueueSQL(unsubscribeBySubscriberId, getTransactionTime(), subscriberId);
        voltQueueSQL(deleteSubscriberMonetaryBalance, subscriberId);

        voltExecuteSQL();
        return noOfUpdatedSubscribers;
    }


}