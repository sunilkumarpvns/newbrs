package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class SubscriptionSelectStoredProcedure extends VoltProcedure {

	public final SQLStmt selectSubscriptionBySubscriptionId = new SQLStmt(
			"SELECT * FROM TBLT_SUBSCRIPTION" +
					" WHERE SUBSCRIBER_ID=?");

	public VoltTable[] run(String subscriberID) throws VoltAbortException {
		voltQueueSQL(selectSubscriptionBySubscriptionId, subscriberID);

		return voltExecuteSQL();
	}
}
