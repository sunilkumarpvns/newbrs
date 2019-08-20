package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import java.sql.Timestamp;
import java.util.Date;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;

public class VoltDBStoredProcUtil {

    private VoltDBStoredProcUtil(){
        //NO NEED TO IMPLEMENT
    }

    public static void insertInto_TBLT_USAGE(VoltProcedure procedure, SQLStmt sqlStmt,
                                             String[]... usageArgs) {
        for (String[] usageArg : usageArgs) {
            if (usageArg == null) {
                continue;
            }
            procedure.voltQueueSQL(sqlStmt, createInputArgs(usageArg, procedure.getTransactionTime()));
        }
    }

    private static Object[] createInputArgs(String[] usageArgs, Date transactionTime) {
        Object[] insertArgs = new Object[usageArgs.length + 1];
        insertArgs[0] = usageArgs[0];
        insertArgs[1] = usageArgs[1];
        insertArgs[2] = usageArgs[2];
        insertArgs[3] = usageArgs[3];
        insertArgs[4] = usageArgs[4];
        insertArgs[5] = usageArgs[5];
        insertArgs[6] = usageArgs[6];
        insertArgs[7] = usageArgs[7];
        insertArgs[8] = usageArgs[8];
        insertArgs[9] = usageArgs[9];
        insertArgs[10] = usageArgs[10];
        insertArgs[11] = usageArgs[11];
        insertArgs[12] = usageArgs[12];
        insertArgs[13] = usageArgs[13];
        insertArgs[14] = usageArgs[14];
        insertArgs[15] = usageArgs[15];
        insertArgs[16] = usageArgs[16];
        insertArgs[17] = usageArgs[17];
        insertArgs[18] = usageArgs[18];
        insertArgs[19] = usageArgs[19];
        insertArgs[20] = usageArgs[20];
        insertArgs[21] = usageArgs[21];
        insertArgs[22] = new Timestamp(getLong(usageArgs[22]));
        insertArgs[23] = new Timestamp(getLong(usageArgs[23]));
        insertArgs[24] = new Timestamp(getLong(usageArgs[24]));
        insertArgs[25] = new Timestamp(getLong(usageArgs[25]));
        insertArgs[26] = transactionTime;
        insertArgs[27] = usageArgs[26];
        return insertArgs;
    }

    public static long getLong(String longTime) {
        return Long.parseLong(longTime);
    }

    public static void insertInto_TBLT_RESET_USAGE_REQ(VoltProcedure voltProcedure, SQLStmt sqlStmt,
                                                       Timestamp billingCycleDateForScheduleUsageReset, String[] dataForScheduleResetUsage) {
        Object[] inputForScheduleUsageReset = new Object[dataForScheduleResetUsage.length + 2];
        int argCount = dataForScheduleResetUsage.length;

        System.arraycopy(dataForScheduleResetUsage, 0, inputForScheduleUsageReset, 0, dataForScheduleResetUsage.length);

        inputForScheduleUsageReset[argCount++] = billingCycleDateForScheduleUsageReset;
        inputForScheduleUsageReset[argCount] = voltProcedure.getTransactionTime();

        voltProcedure.voltQueueSQL(sqlStmt, inputForScheduleUsageReset);
    }
}
