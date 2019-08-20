package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;

import java.sql.Timestamp;

public class SubscriberVoltUtil {
    public static void createAndQueAddStatement (VoltProcedure voltProcedure, SQLStmt stmt, String[] subscriberArgs, Timestamp birthdate, Timestamp expiryDate) {
        Object[] inputArgs = new Object[subscriberArgs.length + 4];
        int argCount = subscriberArgs.length;

        System.arraycopy(subscriberArgs, 0, inputArgs, 0, subscriberArgs.length);

        inputArgs[argCount++] = birthdate;
        inputArgs[argCount++] = expiryDate;
        inputArgs[argCount++] = voltProcedure.getTransactionTime();
        inputArgs[argCount] = voltProcedure.getTransactionTime();
        voltProcedure.voltQueueSQL(stmt, inputArgs);
    }
}
