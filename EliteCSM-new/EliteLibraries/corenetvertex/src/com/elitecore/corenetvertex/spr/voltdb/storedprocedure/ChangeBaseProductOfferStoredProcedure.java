package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.sql.Timestamp;
import java.util.Objects;

public class ChangeBaseProductOfferStoredProcedure extends VoltProcedure {
    public final SQLStmt subscriberUpdateDataPackageQuery = new SQLStmt(
            "UPDATE TBLM_SUBSCRIBER SET PRODUCT_OFFER=? WHERE SUBSCRIBERIDENTITY=?");

    public final SQLStmt insertUsageResetRequestQuery = new SQLStmt(
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

    public final SQLStmt usageInsertQuery = new SQLStmt(
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
                    + " DAILY_RESET_TIME,"
                    + " WEEKLY_RESET_TIME,"
                    + " BILLING_CYCLE_RESET_TIME,"
                    + " CUSTOM_RESET_TIME,"
                    + " LAST_UPDATE_TIME,"
                    + " PRODUCT_OFFER_ID"
                    + ")"
                    + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

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

    public final SQLStmt addMonetaryBalance = new SQLStmt(MonetaryVoltStatements.ADD_MONETARY_BALANCE);
    public final SQLStmt updateMonetaryBalance = new SQLStmt(MonetaryVoltStatements.UPDATE_MONETARY_BALANCE);

    public VoltTable[] run(String subscriberId, String dataPackage,
                           Timestamp billingCycleDateForScheduleUsageReset, String[] dataForScheduleResetUsage,
                           String[] usageResetElements1,String[] usageResetElements2, String[] usageResetElements3,
                           String productOfferID, String usageHistoryID, String[] addMonetaryBalanceArray,
                           String[] updateMonetaryBalanceArray,
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
                           String[] usageArgs15
    ) {

        if(Objects.nonNull(dataPackage)) {
            voltQueueSQL(subscriberUpdateDataPackageQuery, dataPackage, subscriberId);
        }

        if (Objects.nonNull(dataForScheduleResetUsage)) {
            VoltDBStoredProcUtil.insertInto_TBLT_RESET_USAGE_REQ(this, insertUsageResetRequestQuery, billingCycleDateForScheduleUsageReset, dataForScheduleResetUsage);
        }

        if (Objects.nonNull(usageArgs1)) {
            VoltDBStoredProcUtil.insertInto_TBLT_USAGE(this, usageInsertQuery, usageArgs1, usageArgs2, usageArgs3, usageArgs4, usageArgs5, usageArgs6, usageArgs7,
                    usageArgs8, usageArgs9, usageArgs10, usageArgs11, usageArgs12, usageArgs13, usageArgs14, usageArgs15);
        }

        if(Objects.nonNull(usageResetElements1) || Objects.nonNull(usageResetElements2) || Objects.nonNull(usageResetElements3)) {
            processUsageHistoryInsert(subscriberId,productOfferID, usageHistoryID);

            processUsageReset(subscriberId, productOfferID, usageResetElements1,usageResetElements2,usageResetElements3);
        }

        if (Objects.nonNull(addMonetaryBalanceArray)) {
            voltQueueSQL(addMonetaryBalance, addMonetaryBalanceArray);
        }

        if (Objects.nonNull(updateMonetaryBalanceArray)) {
            voltQueueSQL(updateMonetaryBalance, updateMonetaryBalanceArray);
        }
        return voltExecuteSQL();
    }


    private void processUsageReset(String subscriberId, String productOfferID, String[]... usageArgs) {
        for (String[] usageArg : usageArgs) {

            if (usageArg == null) {
                continue;
            }

            voltQueueSQL(usageResetQuery, createInputArgs(subscriberId, productOfferID, usageArg));
        }
    }

    private void processUsageHistoryInsert(String subscriberId, String productOfferId, String usageHisotoryID) {
        voltQueueSQL(usageHistoryInsertQuery, usageHisotoryID, subscriberId, productOfferId);
    }

    private Object[] createInputArgs(String subscriberId, String productOfferId, String[] usageArgs) {
        Object[] resetArgs = new Object[usageArgs.length + 3];
        resetArgs[0] = Long.parseLong(usageArgs[0]);
        resetArgs[1] = Long.parseLong(usageArgs[1]);
        resetArgs[2] = Long.parseLong(usageArgs[2]);
        resetArgs[3] = Long.parseLong(usageArgs[3]);
        resetArgs[4] = Long.parseLong(usageArgs[4]);
        resetArgs[5] = Long.parseLong(usageArgs[5]);
        resetArgs[6] = Long.parseLong(usageArgs[6]);
        resetArgs[7] = Long.parseLong(usageArgs[7]);
        resetArgs[8] = Long.parseLong(usageArgs[8]);
        resetArgs[9] = Long.parseLong(usageArgs[9]);
        resetArgs[10] = Long.parseLong(usageArgs[10]);
        resetArgs[11] = Long.parseLong(usageArgs[11]);
        resetArgs[12] = Long.parseLong(usageArgs[12]);
        resetArgs[13] = Long.parseLong(usageArgs[13]);
        resetArgs[14] = Long.parseLong(usageArgs[14]);
        resetArgs[15] = Long.parseLong(usageArgs[15]);
        resetArgs[16] = Long.parseLong(usageArgs[16]);
        resetArgs[17] = new Timestamp(Long.parseLong(usageArgs[17]));
        resetArgs[18] = new Timestamp(Long.parseLong(usageArgs[18]));
        resetArgs[19] = getTransactionTime();
        resetArgs[20] = subscriberId;
        resetArgs[21] = productOfferId;
        resetArgs[22] = usageArgs[19];
        return resetArgs;
    }
}
