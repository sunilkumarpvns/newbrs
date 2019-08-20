package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.sql.Timestamp;

public class SubscriberChangeBillDayStoredProcedure extends VoltProcedure {

    public final SQLStmt changeBillDayForSubscriber = new SQLStmt(
            "UPDATE TBLM_SUBSCRIBER " +
                    "SET NEXTBILLDATE=? " +
                    ", BILLCHANGEDATE=? " +
                    "WHERE SUBSCRIBERIDENTITY=? ");

    public VoltTable[] run(String subscriberId, Timestamp nextBillDate, Timestamp billChangeDate) throws VoltAbortException {
        voltQueueSQL(changeBillDayForSubscriber, nextBillDate, billChangeDate, subscriberId);
        return voltExecuteSQL();
    }
}