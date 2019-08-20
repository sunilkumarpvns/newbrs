package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

/**
 * This Stored Procedure does following work:
 * > Subscriber Change Data Package
 */
public class SubscriberChangeDataPackageStoredProcedure extends VoltProcedure {

    public final SQLStmt subscriberUpdateDataPackageQuery = new SQLStmt(
            "UPDATE TBLM_SUBSCRIBER SET PRODUCT_OFFER=? WHERE SUBSCRIBERIDENTITY=?");

    public VoltTable[] run(String subscriberId, String dataPackage) {
        voltQueueSQL(subscriberUpdateDataPackageQuery, dataPackage, subscriberId);
        return voltExecuteSQL();
    }
}
