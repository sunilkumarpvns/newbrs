package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class UsageAddToExistingStoredProcedure extends VoltProcedure {

    //18
    public final SQLStmt usageAddToExistingQuery = new SQLStmt(
            "UPDATE TBLT_USAGE SET"
                    + " DAILY_TOTAL = DAILY_TOTAL + ?,"
                    + " DAILY_UPLOAD = DAILY_UPLOAD + ?,"
                    + " DAILY_DOWNLOAD = DAILY_DOWNLOAD + ?,"
                    + " DAILY_TIME = DAILY_TIME + ?,"
                    + " WEEKLY_TOTAL = WEEKLY_TOTAL + ?,"
                    + " WEEKLY_UPLOAD = WEEKLY_UPLOAD + ?,"
                    + " WEEKLY_DOWNLOAD = WEEKLY_DOWNLOAD + ?,"
                    + " WEEKLY_TIME = WEEKLY_TIME + ?,"
                    + " BILLING_CYCLE_TOTAL = BILLING_CYCLE_TOTAL + ?,"
                    + " BILLING_CYCLE_UPLOAD = BILLING_CYCLE_UPLOAD + ?,"
                    + " BILLING_CYCLE_DOWNLOAD = BILLING_CYCLE_DOWNLOAD + ?,"
                    + " BILLING_CYCLE_TIME = BILLING_CYCLE_TIME + ?,"
                    + " CUSTOM_TOTAL = CUSTOM_TOTAL + ?,"
                    + " CUSTOM_UPLOAD = CUSTOM_UPLOAD + ?,"
                    + " CUSTOM_DOWNLOAD = CUSTOM_DOWNLOAD + ?,"
                    + " CUSTOM_TIME = CUSTOM_TIME + ?,"
                    + " LAST_UPDATE_TIME = ? "
                    + " WHERE SUBSCRIBER_ID=? AND ID=?");


    public VoltTable[] run(String subscriberId, String[] usageArgs1,
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

        processUsageAddToExisting(subscriberId,usageArgs1, usageArgs2, usageArgs3, usageArgs4, usageArgs5, usageArgs6, usageArgs7,
                usageArgs8, usageArgs9, usageArgs10, usageArgs11, usageArgs12, usageArgs13, usageArgs14, usageArgs15);

        return voltExecuteSQL();
    }

    private void processUsageAddToExisting(String subscriberId, String[]... usageArgs){
        for (String[] usageArg : usageArgs) {

            if (usageArg == null) {
                continue;
            }

            voltQueueSQL(usageAddToExistingQuery, createInputArgs(subscriberId, usageArg));
        }
    }

    private Object[] createInputArgs(String subscriberId, String[] usageArgs) {
        Object[] addToExistingArgs = new Object[usageArgs.length + 2];
        addToExistingArgs[0] = getLong(usageArgs[0]);
        addToExistingArgs[1] = getLong(usageArgs[1]);
        addToExistingArgs[2] = getLong(usageArgs[2]);
        addToExistingArgs[3] = getLong(usageArgs[3]);
        addToExistingArgs[4] = getLong(usageArgs[4]);
        addToExistingArgs[5] = getLong(usageArgs[5]);
        addToExistingArgs[6] = getLong(usageArgs[6]);
        addToExistingArgs[7] = getLong(usageArgs[7]);
        addToExistingArgs[8] = getLong(usageArgs[8]);
        addToExistingArgs[9] = getLong(usageArgs[9]);
        addToExistingArgs[10] = getLong(usageArgs[10]);
        addToExistingArgs[11] = getLong(usageArgs[11]);
        addToExistingArgs[12] = getLong(usageArgs[12]);
        addToExistingArgs[13] = getLong(usageArgs[13]);
        addToExistingArgs[14] = getLong(usageArgs[14]);
        addToExistingArgs[15] = getLong(usageArgs[15]);
        addToExistingArgs[16] = getTransactionTime();
        addToExistingArgs[17] = subscriberId;
        addToExistingArgs[18] = usageArgs[16];
        return addToExistingArgs;
    }

    private long getLong(String longValue) {
        return Long.parseLong(longValue);
    }
}