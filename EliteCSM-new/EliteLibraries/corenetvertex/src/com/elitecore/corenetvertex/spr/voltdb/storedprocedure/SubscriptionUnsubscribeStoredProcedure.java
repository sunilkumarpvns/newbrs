package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class SubscriptionUnsubscribeStoredProcedure extends VoltProcedure {

	public final SQLStmt unsubscribeBySubscriberId = new SQLStmt(
			"UPDATE TBLT_SUBSCRIPTION  SET STATUS= ?, " +
					"LAST_UPDATE_TIME= ? " +
					"WHERE SUBSCRIBER_ID= ?");

	public VoltTable[] run(String subscriberId, int subscriptionState) throws VoltAbortException {
		voltQueueSQL(unsubscribeBySubscriberId, subscriptionState, getTransactionTime(), subscriberId);

		return voltExecuteSQL();
	}
}
