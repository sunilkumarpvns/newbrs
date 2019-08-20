package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

public class SubscriberVoltStatements {
    /* Total Column 48*/
    public static final String SUBSCRIBER_ADD_STATEMENT =
            "INSERT INTO TBLM_SUBSCRIBER (" +
                    "SUBSCRIBERIDENTITY, " +
                    "USERNAME, " +
                    "PASSWORD, " +
                    "CUSTOMERTYPE, " +
                    "STATUS, " +
                    "PRODUCT_OFFER, " +
                    "IMSPACKAGE, " +
                    "BILLINGDATE, " +
                    "AREA, " +
                    "CITY, " +
                    "PARAM1, " +
                    "PARAM2, " +
                    "PARAM3, " +
                    "PARAM4, " +
                    "PARAM5, " +
                    "ZONE, " +
                    "COUNTRY, " +
                    "ROLE, " +
                    "COMPANY, " +
                    "DEPARTMENT, " +
                    "ARPU, " +
                    "CADRE, " +
                    "EMAIL, " +
                    "PHONE, " +
                    "SIPURL, " +
                    "CUI, " +
                    "IMSI, " +
                    "MSISDN, " +
                    "MAC, " +
                    "EUI64, " +
                    "MODIFIED_EUI64, " +
                    "ENCRYPTIONTYPE, " +
                    "ESN, " +
                    "MEID, " +
                    "PARENTID, " +
                    "GROUPNAME, " +
                    "IMEI, " +
                    "CALLING_STATION_ID, " +
                    "NAS_PORT_ID, " +
                    "FRAMED_IP, " +
                    "SUBSCRIBERLEVELMETERING, " +
                    "PASSWORD_CHECK, " +
                    "SY_INTERFACE, " +
                    "PAYG_INTL_DATA_ROAMING, " +
                    "BILLING_ACCOUNT_ID," +
                    "SERVICE_INSTANCE_ID," +
                    "NEXTBILLDATE, " +
                    "BILLCHANGEDATE, " +
                    "BIRTHDATE, " +
                    "EXPIRYDATE, " +
                    "CREATED_DATE, " +
                    "MODIFIED_DATE" +
                    ") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public  static final String SUBSCRIBER_UPDATE_EXPIRY_DATE = "UPDATE TBLM_SUBSCRIBER SET EXPIRYDATE = ?, MODIFIED_DATE = ? " +
            "WHERE SUBSCRIBERIDENTITY = ? ";
}
