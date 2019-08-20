package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class MonetaryBalanceReserveStoredProcedure extends VoltProcedure {
    public  final SQLStmt reserveMonetaryBalance = new SQLStmt(
            "UPDATE TBLM_MONETARY_BALANCE " +
                    "SET TOTAL_RESERVATION = TOTAL_RESERVATION + ?, " +
                    "LAST_UPDATE_TIME = ? " +
                    "WHERE SUBSCRIBER_ID = ? AND ID = ?");


    public VoltTable[] run(String subscriberId, String id, double totalReservation)
            throws VoltProcedure.VoltAbortException {

        voltQueueSQL(reserveMonetaryBalance, totalReservation, getTransactionTime(), subscriberId, id);
        return voltExecuteSQL();
    }
}
