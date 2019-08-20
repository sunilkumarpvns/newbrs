package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.sql.Timestamp;

public class UsageResetStoredProcedure extends VoltProcedure {

    public final SQLStmt usageHistoryInsertQuery = new SQLStmt(
            "INSERT INTO TBLT_USAGE_HISTORY "
                    + " (CREATED_DATE,"
                    + " ID,"
                    + " SUBSCRIBER_ID,"
                    + "	PACKAGE_ID,"
                    + " SUBSCRIPTION_ID,"
                    + " QUOTA_PROFILE_ID,"
                    + " SERVICE_ID,"
                    + " DAILY_TOTAL,"
                    + " DAILY_UPLOAD,"
                    + " DAILY_DOWNLOAD,"
                    + " DAILY_TIME,"
                    + " WEEKLY_TOTAL,"
                    + " WEEKLY_UPLOAD,"
                    + " WEEKLY_DOWNLOAD,"
                    + " WEEKLY_TIME,"
                    + " BILLING_CYCLE_TOTAL,"
                    + " BILLING_CYCLE_UPLOAD,"
                    + " BILLING_CYCLE_DOWNLOAD,"
                    + " BILLING_CYCLE_TIME,"
                    + " CUSTOM_TOTAL,"
                    + " CUSTOM_UPLOAD,"
                    + " CUSTOM_DOWNLOAD,"
                    + " CUSTOM_TIME,"
                    + " DAILY_RESET_TIME,"
                    + " WEEKLY_RESET_TIME,"
                    + " BILLING_CYCLE_RESET_TIME,"
                    + " CUSTOM_RESET_TIME,"
                    + " LAST_UPDATE_TIME,"
                    + " PRODUCT_OFFER_ID"
                    + ")"
                    + "SELECT "
                    + " CURRENT_TIMESTAMP,"
                    + " CAST(? AS VARCHAR(36)),"
                    + " SUBSCRIBER_ID,"
                    + "	PACKAGE_ID,"
                    + " SUBSCRIPTION_ID,"
                    + " QUOTA_PROFILE_ID,"
                    + " SERVICE_ID,"
                    + " DAILY_TOTAL,"
                    + " DAILY_UPLOAD,"
                    + " DAILY_DOWNLOAD,"
                    + " DAILY_TIME,"
                    + " WEEKLY_TOTAL,"
                    + " WEEKLY_UPLOAD,"
                    + " WEEKLY_DOWNLOAD,"
                    + " WEEKLY_TIME,"
                    + " BILLING_CYCLE_TOTAL,"
                    + " BILLING_CYCLE_UPLOAD,"
                    + " BILLING_CYCLE_DOWNLOAD,"
                    + " BILLING_CYCLE_TIME,"
                    + " CUSTOM_TOTAL,"
                    + " CUSTOM_UPLOAD,"
                    + " CUSTOM_DOWNLOAD,"
                    + " CUSTOM_TIME,"
                    + " DAILY_RESET_TIME,"
                    + " WEEKLY_RESET_TIME,"
                    + " BILLING_CYCLE_RESET_TIME,"
                    + " CUSTOM_RESET_TIME,"
                    + " LAST_UPDATE_TIME,"
                    + " PRODUCT_OFFER_ID"
                    + " FROM "
                    + " TBLT_USAGE"
                    + " WHERE SUBSCRIBER_ID = ? AND PRODUCT_OFFER_ID = ?");

    public final SQLStmt usageResetQuery = new SQLStmt(
            "UPDATE TBLT_USAGE SET"
                    + " DAILY_TOTAL=?,"
                    + " DAILY_UPLOAD=?,"
                    + " DAILY_DOWNLOAD = ?,"
                    + " DAILY_TIME = ?,"
                    + " WEEKLY_TOTAL = ?,"
                    + " WEEKLY_UPLOAD = ?,"
                    + " WEEKLY_DOWNLOAD = ?,"
                    + " WEEKLY_TIME = ?,"
                    + " BILLING_CYCLE_TOTAL = ?,"
                    + " BILLING_CYCLE_UPLOAD = ?,"
                    + " BILLING_CYCLE_DOWNLOAD = ?,"
                    + " BILLING_CYCLE_TIME = ?,"
                    + " CUSTOM_TOTAL = ?,"
                    + " CUSTOM_UPLOAD = ?,"
                    + " CUSTOM_DOWNLOAD = ?,"
                    + " CUSTOM_TIME = ?,"
                    + " DAILY_RESET_TIME = ?,"
                    + " WEEKLY_RESET_TIME = ?,"
                    + " BILLING_CYCLE_RESET_TIME = ?,"
                    + " LAST_UPDATE_TIME = ? "
                    + " WHERE SUBSCRIBER_ID=? AND PRODUCT_OFFER_ID=? AND QUOTA_PROFILE_ID=?");


    public VoltTable[] run(String subscriberId, String productOfferId, String usageHistoryId, String[] usageArgs1,String[] usageArgs2,String[] usageArgs3) throws VoltAbortException {

        processUsageHistoryInsert(usageHistoryId, subscriberId, productOfferId);

        processUsageReset(subscriberId, productOfferId, usageArgs1, usageArgs2, usageArgs3);

        return voltExecuteSQL();
    }

    private void processUsageReset(String subscriberId, String productOfferId, String[]... usageArgs) {

        for (String[] usageArg : usageArgs) {

            if (usageArg == null) {
                continue;
            }

            voltQueueSQL(usageResetQuery, createInputArgs(subscriberId, productOfferId, usageArg));
        }
    }

    private Object[] createInputArgs(String subscriberId, String productOfferId, String[] usageArgs) {
        Object[] resetArgs = new Object[usageArgs.length + 3];
        resetArgs[0] = getLong(usageArgs[0]);
        resetArgs[1] = getLong(usageArgs[1]);
        resetArgs[2] = getLong(usageArgs[2]);
        resetArgs[3] = getLong(usageArgs[3]);
        resetArgs[4] = getLong(usageArgs[4]);
        resetArgs[5] = getLong(usageArgs[5]);
        resetArgs[6] = getLong(usageArgs[6]);
        resetArgs[7] = getLong(usageArgs[7]);
        resetArgs[8] = getLong(usageArgs[8]);
        resetArgs[9] = getLong(usageArgs[9]);
        resetArgs[10] = getLong(usageArgs[10]);
        resetArgs[11] = getLong(usageArgs[11]);
        resetArgs[12] = getLong(usageArgs[12]);
        resetArgs[13] = getLong(usageArgs[13]);
        resetArgs[14] = getLong(usageArgs[14]);
        resetArgs[15] = getLong(usageArgs[15]);
        resetArgs[16] = getLong(usageArgs[16]);
        resetArgs[17] = new Timestamp(getLong(usageArgs[17]));
        resetArgs[18] = new Timestamp(getLong(usageArgs[18]));
        resetArgs[19] = getTransactionTime();
        resetArgs[20] = subscriberId;
        resetArgs[21] = productOfferId;
        resetArgs[22] = usageArgs[19];
        return resetArgs;
    }

    private void processUsageHistoryInsert(String usageHistoryId, String subscriberId, String productOfferId) {
        voltQueueSQL(usageHistoryInsertQuery, usageHistoryId, subscriberId, productOfferId);
    }

    private long getLong(String longValue) {
        return Long.parseLong(longValue);
    }
}
