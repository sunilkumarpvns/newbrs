package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class RechargeMonetaryBalanceStoredProcedure extends VoltProcedure {
    public final SQLStmt RECHARGE_MONETARY_BALANCE = new SQLStmt(MonetaryVoltStatements.RECHARGE_MONETARY_BALANCE);

    public VoltTable[] run(String subscriberIdentity, String id, BigDecimal price, BigDecimal amount, Timestamp extendedValidity) throws VoltProcedure.VoltAbortException {

        voltQueueSQL(RECHARGE_MONETARY_BALANCE, price, amount, extendedValidity, subscriberIdentity, id);
        return voltExecuteSQL();
    }

}