package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.sql.Timestamp;

public class UsageReplaceStoredProcedure extends VoltProcedure {

    public final SQLStmt usageReplaceQuery = new SQLStmt(
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
                    + " WHERE SUBSCRIBER_ID=? AND ID=?");

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
                    + " TBLT_USAGE "
                    + " WHERE ID = ?");


    public VoltTable[] run(String subscriberId, String usageHistoryId,
                           String[] usageArgs1,
                           String[] usageArgs2,
                           String[] usageArgs3,
                           String[] usageArgs4,
                           String[] usageArgs5,
                           String[] usageArgs6,
                           String[] usageArgs7,
                           String[] usageArgs8,
                           String[] usageArgs9,
                           String[] usageArgs10,
                           String[] usageArgs11,
                           String[] usageArgs12,
                           String[] usageArgs13,
                           String[] usageArgs14,
                           String[] usageArgs15) throws VoltAbortException {

        processUsageHistoryInsert(usageHistoryId,usageArgs1, usageArgs2, usageArgs3, usageArgs4, usageArgs5, usageArgs6, usageArgs7,
                usageArgs8, usageArgs9, usageArgs10, usageArgs11, usageArgs12, usageArgs13, usageArgs14, usageArgs15);

        processUsageReplace(subscriberId, usageArgs1, usageArgs2, usageArgs3, usageArgs4, usageArgs5, usageArgs6, usageArgs7,
                usageArgs8, usageArgs9, usageArgs10, usageArgs11, usageArgs12, usageArgs13, usageArgs14, usageArgs15);

        return voltExecuteSQL();
    }

    private void processUsageReplace(String subscriberId, String[]... usageArgs) {
        for (String[] usageArg : usageArgs) {

            if (usageArg == null) {
                continue;
            }

            voltQueueSQL(usageReplaceQuery, createInputArgs(subscriberId, usageArg));
        }
    }

    private Object[] createInputArgs(String subscriberId, String[] usageArgs) {
        Object[] replaceArgs = new Object[usageArgs.length + 2];
        replaceArgs[0] = getLong(usageArgs[0]);
        replaceArgs[1] = getLong(usageArgs[1]);
        replaceArgs[2] = getLong(usageArgs[2]);
        replaceArgs[3] = getLong(usageArgs[3]);
        replaceArgs[4] = getLong(usageArgs[4]);
        replaceArgs[5] = getLong(usageArgs[5]);
        replaceArgs[6] = getLong(usageArgs[6]);
        replaceArgs[7] = getLong(usageArgs[7]);
        replaceArgs[8] = getLong(usageArgs[8]);
        replaceArgs[9] = getLong(usageArgs[9]);
        replaceArgs[10] = getLong(usageArgs[10]);
        replaceArgs[11] = getLong(usageArgs[11]);
        replaceArgs[12] = getLong(usageArgs[12]);
        replaceArgs[13] = getLong(usageArgs[13]);
        replaceArgs[14] = getLong(usageArgs[14]);
        replaceArgs[15] = getLong(usageArgs[15]);
        replaceArgs[16] = new Timestamp(getLong(usageArgs[16]));
        replaceArgs[17] = new Timestamp(getLong(usageArgs[17]));
        replaceArgs[18] = new Timestamp(getLong(usageArgs[18]));
        replaceArgs[19] = getTransactionTime();
        replaceArgs[20] = subscriberId;
        replaceArgs[21] = usageArgs[19];
        return replaceArgs;
    }

    private void processUsageHistoryInsert(String usageHistoryId, String[]... usageArgs) {
        for (String[] usageArg : usageArgs) {

            if (usageArg == null) {
                continue;
            }

            voltQueueSQL(usageHistoryInsertQuery, usageHistoryId, usageArg[19]);
        }
    }

    private long getLong(String longValue) {
        return Long.parseLong(longValue);
    }
}