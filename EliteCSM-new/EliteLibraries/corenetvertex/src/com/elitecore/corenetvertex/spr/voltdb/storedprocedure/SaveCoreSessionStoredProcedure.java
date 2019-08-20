package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.util.UUID;

public class SaveCoreSessionStoredProcedure extends VoltProcedure {

    public final SQLStmt insertCoreSessionQuery = new SQLStmt(
            "INSERT INTO TBLT_SESSION(" +
                    "CS_ID" +
                    ", START_TIME" +
                    ", LAST_UPDATE_TIME" +
                    ", SUBSCRIBER_IDENTITY" +   //0 index of args[], 3rd of insertArgs[]
                    ", CORE_SESSION_ID" +
                    ", SESSION_ID" +
                    ", SESSION_MANAGER_ID" +
                    ", SESSION_IP_V4" +
                    ", SESSION_IP_V6" +
                    ", ACCESS_NETWORK" +
                    ", SESSION_TYPE" +
                    ", QOS_PROFILE" +
                    ", SOURCE_GATEWAY" +
                    ", SY_SESSION_ID" +
                    ", SY_GATEWAY_NAME" +
                    ", GATEWAY_NAME" +
                    ", CONGESTION_STATUS" +
                    ", IMSI" +
                    ", MSISDN" +
                    ", NAI" +
                    ", NAI_REALM" +
                    ", NAI_USER_NAME" +
                    ", SIP_URL" +
                    ", PCC_RULES" +
                    ", REQUESTED_QOS" +
                    ", SESSION_USAGE" +
                    ", REQUEST_NUMBER" +
                    ", USAGE_RESERVATION" +
                    ", GATEWAY_ADDRESS" +
                    ", GATEWAY_REALM" +
                    ", PACKAGE_USAGE" +
                    ", CHARGING_RULE_BASE_NAMES" +
                    ", CALLING_STATION_ID" +
                    ", CALLED_STATION_ID" +
                    ", QUOTA_RESERVATION" +
                    ", PCC_PROFILE_SELECTION_STATE" +
                    ", UNACCOUNTED_QUOTA" +
                    ", SGSN_MCC_MNC" +
                    ", SERVICE" +
                    ", LOCATION" +
                    ", PARAM1" +
                    ", PARAM2" +
                    ", PARAM3" +
                    ", PARAM4" +
                    ", PARAM5" +    //41 index of args[], 44th of insertArgs[]
                    ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

    public VoltTable[] run(String coreSessionId, String[] args) throws VoltAbortException { //NOSONAR -- FIRST ARGUMENT SHOULD ALWAYS BE PARTITION KEY
        Object[] insertArgs = new Object[args.length + 3];
        System.arraycopy(args, 0, insertArgs, 3, args.length);
        insertArgs[0] = UUID.randomUUID().toString();
        insertArgs[1] = getTransactionTime();
        insertArgs[2] = getTransactionTime();
        voltQueueSQL(insertCoreSessionQuery, insertArgs);

        return voltExecuteSQL();
    }
}
