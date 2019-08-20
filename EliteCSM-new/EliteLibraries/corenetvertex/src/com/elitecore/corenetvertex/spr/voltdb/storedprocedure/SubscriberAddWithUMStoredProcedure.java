package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.sql.Timestamp;

public class SubscriberAddWithUMStoredProcedure extends VoltProcedure {

    private final SQLStmt addSubscriber = new SQLStmt(SubscriberVoltStatements.SUBSCRIBER_ADD_STATEMENT);
    private final SQLStmt addMonetaryBalance = new SQLStmt(MonetaryVoltStatements.ADD_MONETARY_BALANCE);

    public final SQLStmt usageInsertQuery = new SQLStmt(
            "INSERT INTO TBLT_USAGE "
                    + "( ID,"
                    + " SUBSCRIBER_ID,"
                    + " PACKAGE_ID,"
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
                    + "PRODUCT_OFFER_ID"
                    + ")"
                    + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

    /*
     * @param subscriberId
     * @param args only 44 args passed in this parameter
     * @param birthdate
     * @param expiryDate
     * @return
     * @throws VoltAbortException
     */
    public VoltTable[] run(String subscriberId, String[] subscriberArgs, Timestamp birthdate, Timestamp expiryDate, //NOSONAR -- FIRST ARGUMENT SHOULD ALWAYS BE PARTITION KEY
                           String[] balanceArgs,
                           Timestamp balanceValidFromDate,
                           Timestamp balanceValidToDate,
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
                           String[] usageArgs15,
                           Timestamp creditLimitUpdateTime) {

        SubscriberVoltUtil.createAndQueAddStatement(this,addSubscriber,subscriberArgs, birthdate, expiryDate);

        processInsertForUsage(usageArgs1, usageArgs2, usageArgs3, usageArgs4, usageArgs5, usageArgs6, usageArgs7,
                usageArgs8, usageArgs9, usageArgs10, usageArgs11, usageArgs12, usageArgs13, usageArgs14, usageArgs15);

        if(balanceArgs.length > 0){
            MonetaryVoltUtil.createAndQueAddStatement(this, addMonetaryBalance, balanceArgs, balanceValidFromDate, balanceValidToDate, creditLimitUpdateTime);
        }

        return voltExecuteSQL();
    }

    private void processInsertForUsage(String[]... usageArgs) {
        for (String[] usageArg : usageArgs) {

            if (usageArg == null) {
                continue;
            }

            voltQueueSQL(usageInsertQuery, createInputArgs(usageArg));
        }
    }

    private Object[] createInputArgs(String[] usageArgs) {
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
        insertArgs[26] = getTransactionTime();
        insertArgs[27] = usageArgs[26];
        return insertArgs;
    }

    private long getLong(String longTime) {
        return Long.parseLong(longTime);
    }
}
