package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.sql.Timestamp;

public class SubscriptionUpdateDetailStoredProcedure extends VoltProcedure {

	public final SQLStmt updateSubscriptionDetail = new SQLStmt(
			"UPDATE TBLT_SUBSCRIPTION " +
					"SET START_TIME=?, " +
					"END_TIME=?, " +
					"PRIORITY=?, " +
					"STATUS=?, " +
					"REJECT_REASON=?, " +
					"LAST_UPDATE_TIME=? " +
					"WHERE SUBSCRIBER_ID=? "+
					"AND SUBSCRIPTION_ID=?");

    public VoltTable[] run(String subscriberId,String subscriptionId, Timestamp startTime, Timestamp endTime,int priority,int status,String rejectReason) throws VoltAbortException {
		voltQueueSQL(updateSubscriptionDetail, startTime, endTime,priority,status,rejectReason, getTransactionTime(),subscriberId, subscriptionId);
		return voltExecuteSQL();
	}
}
