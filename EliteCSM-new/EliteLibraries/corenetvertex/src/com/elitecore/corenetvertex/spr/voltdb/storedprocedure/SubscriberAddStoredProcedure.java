package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.sql.Timestamp;

public class SubscriberAddStoredProcedure extends VoltProcedure {

    private final SQLStmt addSubscriber = new SQLStmt(SubscriberVoltStatements.SUBSCRIBER_ADD_STATEMENT);
    /*
     * @param subscriberId
     * @param args only 44 args passed in this parameter
     * @param birthdate
     * @param expiryDate
     * @return
     * @throws VoltAbortException
     */
    public VoltTable[] run(String subscriberId, String[] args, Timestamp birthdate, Timestamp expiryDate) throws VoltAbortException {//NOSONAR- FIRST ARGUMENT SHOULD ALWAYS BE PARTITION KEY
        SubscriberVoltUtil.createAndQueAddStatement(this, addSubscriber,args,birthdate,expiryDate);
        return voltExecuteSQL();
    }
}
