package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.sql.Timestamp;

public class SubscriberUpdateStoredProcedure extends VoltProcedure {

    public final SQLStmt subscriberUpdateQuery = new SQLStmt(
            "UPDATE TBLM_SUBSCRIBER SET " +
                    "USERNAME = ?, " +
                    "PASSWORD = ?, " +
                    "CUSTOMERTYPE = ?, " +
                    "STATUS = ?, " +
                    "PRODUCT_OFFER = ?, " +
                    "IMSPACKAGE = ?, " +
                    "BILLINGDATE = ?, " +
                    "AREA = ?, " +
                    "CITY = ?, " +
                    "PARAM1 = ?, " +
                    "PARAM2 = ?, " +
                    "PARAM3 = ?, " +
                    "PARAM4 = ?, " +
                    "PARAM5 = ?, " +
                    "ZONE = ?, " +
                    "COUNTRY = ?, " +
                    "ROLE = ?, " +
                    "COMPANY = ?, " +
                    "DEPARTMENT = ?, " +
                    "ARPU = ?, " +
                    "CADRE = ?, " +
                    "EMAIL = ?, " +
                    "PHONE = ?, " +
                    "SIPURL = ?, " +
                    "CUI = ?, " +
                    "IMSI = ?, " +
                    "MSISDN = ?, " +
                    "MAC = ?, " +
                    "EUI64 = ?, " +
                    "MODIFIED_EUI64 = ?, " +
                    "ENCRYPTIONTYPE = ?, " +
                    "ESN = ?, " +
                    "MEID = ?, " +
                    "PARENTID = ?, " +
                    "GROUPNAME = ?, " +
                    "IMEI = ?, " +
                    "CALLING_STATION_ID = ?, " +
                    "NAS_PORT_ID = ?, " +
                    "FRAMED_IP = ?, " +
                    "SUBSCRIBERLEVELMETERING = ?, " +
                    "PASSWORD_CHECK = ?, " +
                    "SY_INTERFACE = ?, " +
                    "PAYG_INTL_DATA_ROAMING = ?,  " +
                    "BILLING_ACCOUNT_ID = ?, " +
                    "SERVICE_INSTANCE_ID = ?, " +
                    "NEXTBILLDATE = ?, " +
                    "BIRTHDATE = ?, " +
                    "EXPIRYDATE = ?, " +
                    "CREATED_DATE = ?, " +
                    "MODIFIED_DATE = ? " +
                    " WHERE SUBSCRIBERIDENTITY = ?");


    public VoltTable[] run(String subscriberId, String[] subscriberArgs, Timestamp birthdate, Timestamp expiryDate, Timestamp createdDate) {
        Object[] inputArgs = new Object[subscriberArgs.length + 3];
        int argCount = subscriberArgs.length-1;

        System.arraycopy(subscriberArgs, 1, inputArgs, 0, subscriberArgs.length-1);

        inputArgs[argCount] = birthdate;
        inputArgs[argCount++] = expiryDate;
        inputArgs[argCount++] = createdDate;
        inputArgs[argCount++] = getTransactionTime();
        inputArgs[argCount] = subscriberId;

        voltQueueSQL(subscriberUpdateQuery, inputArgs);
        return voltExecuteSQL();
    }

}