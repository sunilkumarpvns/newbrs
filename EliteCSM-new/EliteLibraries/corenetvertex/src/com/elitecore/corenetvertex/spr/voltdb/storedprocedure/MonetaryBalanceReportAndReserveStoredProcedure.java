package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class MonetaryBalanceReportAndReserveStoredProcedure extends VoltProcedure {
    public  final SQLStmt reportAndReserveMonetaryBalance = new SQLStmt(
            "UPDATE TBLM_MONETARY_BALANCE " +
                    "SET TOTAL_RESERVATION = TOTAL_RESERVATION + ?, " +
                    "AVAILABLE_BALANCE = AVAILABLE_BALANCE - ?, " +
                    "LAST_UPDATE_TIME = ? " +
                    "WHERE SUBSCRIBER_ID = ? " +
                    "AND ID = ?");


    public VoltTable[] run(String subscriberId, String id, double totalReservation, double availableBalance)
            throws VoltProcedure.VoltAbortException {

        voltQueueSQL(reportAndReserveMonetaryBalance, totalReservation, availableBalance, getTransactionTime(), subscriberId, id);
        return voltExecuteSQL();
    }
}
