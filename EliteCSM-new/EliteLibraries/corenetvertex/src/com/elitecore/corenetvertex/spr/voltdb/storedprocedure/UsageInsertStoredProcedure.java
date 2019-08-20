package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.sql.Timestamp;

public class UsageInsertStoredProcedure extends VoltProcedure {

    public final SQLStmt insert = new SQLStmt(
            "INSERT INTO TBLT_USAGE "
                    + "( ID,"
                    + " SUBSCRIBER_ID,"
                    + "PACKAGE_ID,"
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
                    + " PRODUCT_OFFER_ID,"
                    + " DAILY_RESET_TIME,"
                    + " WEEKLY_RESET_TIME,"
                    + " BILLING_CYCLE_RESET_TIME,"
                    + " CUSTOM_RESET_TIME,"
                    + " LAST_UPDATE_TIME"
                    + ")"
                    + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

    public VoltTable[] run(String subscriberId, Timestamp dailyResetTime, Timestamp weekyResetTime, Timestamp billingCycleResetTime, Timestamp customResetTime, String[] usage) throws VoltAbortException {//NOSONAR -- FIRST ARGUMENT SHOULD ALWAYS BE PARTITION KEY

        Object[] insertArgs = new Object[usage.length + 5];
        int argCount = usage.length;

        System.arraycopy(usage, 0, insertArgs, 0, usage.length);

        insertArgs[argCount++] = dailyResetTime;
        insertArgs[argCount++] = weekyResetTime;
        insertArgs[argCount++] = billingCycleResetTime;
        insertArgs[argCount++] = customResetTime;
        insertArgs[argCount] = getTransactionTime();

        voltQueueSQL(insert, insertArgs);

        return voltExecuteSQL();
    }
}