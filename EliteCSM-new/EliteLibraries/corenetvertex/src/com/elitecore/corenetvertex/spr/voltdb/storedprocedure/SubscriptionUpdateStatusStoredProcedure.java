package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class SubscriptionUpdateStatusStoredProcedure extends VoltProcedure {

	public final SQLStmt updateSubscriptionStatusBySubscriptionId = new SQLStmt(
			"UPDATE TBLT_SUBSCRIPTION " +
					"SET STATUS=?, " +
					"REJECT_REASON=?, " +
					"LAST_UPDATE_TIME=? " +
					"WHERE SUBSCRIBER_ID=?");

	public VoltTable[] run(String subscriberID, int subscriptionStatus, String rejectReason) throws VoltAbortException {
		voltQueueSQL(updateSubscriptionStatusBySubscriptionId, subscriptionStatus, rejectReason,
				getTransactionTime(), subscriberID);

		return voltExecuteSQL();
	}
}
