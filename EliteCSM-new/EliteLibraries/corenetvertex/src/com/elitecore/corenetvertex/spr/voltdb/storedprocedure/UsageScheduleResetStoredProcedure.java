package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import java.sql.Timestamp;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class UsageScheduleResetStoredProcedure extends VoltProcedure {

    public final SQLStmt query = new SQLStmt(
            "INSERT INTO TBLM_RESET_USAGE_REQ "
                    + " (BILLING_CYCLE_ID,"
                    + "SUBSCRIBER_IDENTITY,"
                    + "ALTERNATE_IDENTITY,"
                    + "STATUS,"
                    + "SERVER_INSTANCE_ID,"
                    + "PACKAGE_ID,"
                    + "RESET_REASON,"
                    + "PARAM1,"
                    + "PARAM2,"
                    + "PARAM3,"
                    + "PRODUCT_OFFER_ID,"
                    + "BILLING_CYCLE_DATE,"
                    + "CREATED_DATE)"
                    + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");

    public VoltTable[] run(Timestamp billingCycleDate, String[] usage) throws VoltAbortException {

        Object[] insertArgs = new Object[usage.length + 2];
        int argCount = usage.length;

        System.arraycopy(usage, 0, insertArgs, 0, usage.length);

        insertArgs[argCount++] = billingCycleDate;
        insertArgs[argCount] = getTransactionTime();

        voltQueueSQL(query, insertArgs);

        return voltExecuteSQL();
    }
}