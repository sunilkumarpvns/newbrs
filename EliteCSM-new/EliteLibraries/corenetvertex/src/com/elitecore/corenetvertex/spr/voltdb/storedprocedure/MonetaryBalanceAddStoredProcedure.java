package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.sql.Timestamp;

    public class MonetaryBalanceAddStoredProcedure extends VoltProcedure {

        private final SQLStmt addBalance = new SQLStmt(MonetaryVoltStatements.ADD_MONETARY_BALANCE);

        public VoltTable[] run(String subscriberID, String[] args, Timestamp validFromDate, Timestamp validToDate, Timestamp creditLimitUpdateTime) throws VoltProcedure.VoltAbortException {//NOSONAR -- FIRST ARGUMENT SHOULD ALWAYS BE PARTITION KEY
            MonetaryVoltUtil.createAndQueAddStatement(this,addBalance, args, validFromDate, validToDate, creditLimitUpdateTime);
            return voltExecuteSQL();
        }

    }
