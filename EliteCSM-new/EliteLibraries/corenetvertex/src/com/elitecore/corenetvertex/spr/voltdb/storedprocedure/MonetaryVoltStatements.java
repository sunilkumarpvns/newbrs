package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

public class MonetaryVoltStatements {
    public static final String ADD_MONETARY_BALANCE =  "INSERT INTO TBLM_MONETARY_BALANCE (" +
                    "ID," +
                    "SUBSCRIBER_ID," +
                    "SERVICE_ID," +
                    "AVAILABLE_BALANCE," +
                    "INITIAL_BALANCE," +
                    "TOTAL_RESERVATION," +
                    "CREDIT_LIMIT," +
                    "NEXT_BILL_CYCLE_CREDIT_LIMIT," +
                    "CURRENCY," +
                    "TYPE," +
                    "PARAM1," +
                    "PARAM2," +
                    "LAST_UPDATE_TIME," +
                    "VALID_FROM_DATE," +
                    "VALID_TO_DATE," +
                    "CREDIT_LIMIT_UPDATE_TIME" +
                    ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    public static final String RECHARGE_MONETARY_BALANCE =
            "UPDATE TBLM_MONETARY_BALANCE SET "
                    + "AVAILABLE_BALANCE = AVAILABLE_BALANCE -? + ?, VALID_TO_DATE = ? "
                    + "WHERE SUBSCRIBER_ID = ? AND ID = ?";

    public static final String UPDATE_MONETARY_BALANCE = "UPDATE TBLM_MONETARY_BALANCE " +
                    "SET AVAILABLE_BALANCE = AVAILABLE_BALANCE + CAST( ? AS FLOAT ), " +
                    "INITIAL_BALANCE = INITIAL_BALANCE + CAST( ? AS FLOAT ), " +
                    "VALID_TO_DATE = ?, " +
                    "LAST_UPDATE_TIME = ? " +
                    "WHERE SUBSCRIBER_ID = ? " +
                    "AND " +
                    "ID = ?";
}
