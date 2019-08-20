package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class UsageHistoryInsertStoredProcedure extends VoltProcedure {

    public final SQLStmt queryForHistory = new SQLStmt(
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
                    + " WHERE SUBSCRIBER_ID = ? AND PACKAGE_ID = ?");


    public VoltTable[] run(String subscriberId, String usageHistoryId, String packageId) throws VoltAbortException {
        voltQueueSQL(queryForHistory, usageHistoryId, subscriberId, packageId);

        return voltExecuteSQL();
    }

}