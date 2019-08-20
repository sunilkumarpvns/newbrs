package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.sql.Timestamp;
import java.util.Objects;

public class SubscriptionAddStoredProcedure extends VoltProcedure {

	public final SQLStmt subscribeAddOn = new SQLStmt(
			"INSERT INTO TBLT_SUBSCRIPTION ( " +
					"SUBSCRIPTION_ID, " +
					"PACKAGE_ID, " +
					"PRODUCT_OFFER_ID, " +
					"PARENT_IDENTITY, " +
					"SUBSCRIBER_ID, " +
					"SERVER_INSTANCE_ID, " +
					"STATUS, " +
					"PRIORITY, " +
					"PARAM1, " +
					"PARAM2," +
					"TYPE," +
					"SUBSCRIPTION_TIME, " +
					"START_TIME, " +
					"END_TIME, " +
					"LAST_UPDATE_TIME" +
					") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

	public  final SQLStmt directDebitMonetaryBalance = new SQLStmt(
			"UPDATE TBLM_MONETARY_BALANCE " +
					"SET AVAILABLE_BALANCE = AVAILABLE_BALANCE - ?, " +
					"LAST_UPDATE_TIME = ? " +
					"WHERE SUBSCRIBER_ID = ? " +
					"AND ID = ?" );

	public VoltTable[] run(String subscriberID, String[] args, Timestamp startTime, Timestamp endTime,double debitAmount,String id) throws VoltAbortException { //NOSONAR-- FIRST ARGUMENT SHOULD ALWAYS BE PARTITION KEY

		Object[] inputArgs = new Object[args.length+4];

		int argCount = args.length;

		System.arraycopy(args, 0, inputArgs, 0, argCount);

		inputArgs[argCount++] = getTransactionTime();
		inputArgs[argCount++] = startTime;
		inputArgs[argCount++] = endTime;
		inputArgs[argCount] = getTransactionTime();

		voltQueueSQL(subscribeAddOn, inputArgs);

		if(Objects.nonNull(id) && debitAmount > 0d) {
			voltQueueSQL(directDebitMonetaryBalance, debitAmount, getTransactionTime(), subscriberID, id);
		}

		return voltExecuteSQL();
	}
}
