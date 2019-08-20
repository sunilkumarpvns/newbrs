package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import java.sql.Timestamp;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;
/**
 * * This Stored Procedure does following work:
 * > Subscriber Change Data Package
 * > Schedule Old Usage Delete
 * > New Usage Insert
 */
public class SubscriberChangeDataPackageAndScheduleOldUsageDeleteAndNewUsageInsertStoredProcedure extends VoltProcedure {

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

    public VoltTable[] run(String subscriberId, String dataPackage,
                           Timestamp billingCycleDateForScheduleUsageReset, String[] dataForScheduleResetUsage,
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
        voltQueueSQL(subscriberUpdateDataPackageQuery, dataPackage, subscriberId);

        VoltDBStoredProcUtil.insertInto_TBLT_RESET_USAGE_REQ(this, insertUsageResetRequestQuery, billingCycleDateForScheduleUsageReset, dataForScheduleResetUsage);

        VoltDBStoredProcUtil.insertInto_TBLT_USAGE(this, usageInsertQuery, usageArgs1, usageArgs2, usageArgs3, usageArgs4, usageArgs5, usageArgs6, usageArgs7,
                usageArgs8, usageArgs9, usageArgs10, usageArgs11, usageArgs12, usageArgs13, usageArgs14, usageArgs15);
        return voltExecuteSQL();
    }
}
