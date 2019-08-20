package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;

import java.sql.Timestamp;

public class MonetaryVoltUtil {
    public static void createAndQueAddStatement(VoltProcedure voltProcedure, SQLStmt stmt, Object[] args, Timestamp validFromDate, Timestamp validToDate, Timestamp creditLimitUpdateTime) {
        Object[] inputArgs = new Object[args.length+4];

        int argCount = args.length;

        System.arraycopy(args, 0, inputArgs, 0, argCount);
        inputArgs[argCount++] = voltProcedure.getTransactionTime();
        inputArgs[argCount++] = validFromDate;
        inputArgs[argCount++] = validToDate;
        inputArgs[argCount] = creditLimitUpdateTime;

        voltProcedure.voltQueueSQL(stmt, inputArgs);
    }
}
