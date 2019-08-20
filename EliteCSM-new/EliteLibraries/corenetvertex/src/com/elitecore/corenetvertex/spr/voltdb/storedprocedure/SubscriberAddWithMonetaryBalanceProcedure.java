package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.sql.Timestamp;

public class SubscriberAddWithMonetaryBalanceProcedure extends VoltProcedure {

    private final SQLStmt addSubscriber = new SQLStmt(SubscriberVoltStatements.SUBSCRIBER_ADD_STATEMENT);
    private final SQLStmt addBalance = new SQLStmt(MonetaryVoltStatements.ADD_MONETARY_BALANCE);
    /*
     * @param subscriberId
     * @param args only 44 args passed in this parameter
     * @param birthdate
     * @param expiryDate
     * @return
     * @throws VoltAbortException
     */
    public VoltTable[] run(String subscriberId, String[] subscriberArgs, Timestamp birthdate, Timestamp expiryDate //NOSONAR -- FIRST ARGUMENT SHOULD ALWAYS BE PARTITION KEY
            , String[] monetaryBalanceArgs, Timestamp balanceValidFromDate, Timestamp balanceValidToDate, Timestamp creditLimitUpdateTime) {

        SubscriberVoltUtil.createAndQueAddStatement(this,addSubscriber,subscriberArgs, birthdate, expiryDate);
        MonetaryVoltUtil.createAndQueAddStatement(this,addBalance,monetaryBalanceArgs, balanceValidFromDate, balanceValidToDate, creditLimitUpdateTime);
        return voltExecuteSQL();
    }
}
