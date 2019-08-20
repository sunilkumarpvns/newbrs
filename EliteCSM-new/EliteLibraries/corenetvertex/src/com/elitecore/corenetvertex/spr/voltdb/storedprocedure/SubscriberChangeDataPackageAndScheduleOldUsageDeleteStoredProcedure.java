package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import java.sql.Timestamp;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

/**
 * This Stored Procedure does following work:
 * > Subscriber Change Data Package
 * > Schedule Old Usage Delete
 */
public class SubscriberChangeDataPackageAndScheduleOldUsageDeleteStoredProcedure extends VoltProcedure {

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

    public VoltTable[] run(String subscriberId, String dataPackage,
                           Timestamp billingCycleDateForScheduleUsageReset,
                           String[] dataForScheduleResetUsage) {
        voltQueueSQL(subscriberUpdateDataPackageQuery, dataPackage, subscriberId);
        VoltDBStoredProcUtil.insertInto_TBLT_RESET_USAGE_REQ(this, insertUsageResetRequestQuery, billingCycleDateForScheduleUsageReset, dataForScheduleResetUsage);
        return voltExecuteSQL();
    }
}
