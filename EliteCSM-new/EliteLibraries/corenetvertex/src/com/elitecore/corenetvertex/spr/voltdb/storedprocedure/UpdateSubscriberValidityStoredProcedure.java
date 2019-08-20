package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class UpdateSubscriberValidityStoredProcedure extends VoltProcedure {
    public final SQLStmt UPDATE_SUBSCRIBER_VALIDITY = new SQLStmt(SubscriberVoltStatements.SUBSCRIBER_UPDATE_EXPIRY_DATE);

    public VoltTable[] run(String subscriberIdentity, Timestamp subscriberValidity) throws VoltAbortException {

        voltQueueSQL(UPDATE_SUBSCRIBER_VALIDITY,subscriberValidity, getTransactionTime(), subscriberIdentity);
        return voltExecuteSQL();
    }

}