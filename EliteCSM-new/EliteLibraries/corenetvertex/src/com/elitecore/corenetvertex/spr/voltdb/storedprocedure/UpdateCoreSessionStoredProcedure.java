package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.util.Date;

public class UpdateCoreSessionStoredProcedure extends VoltProcedure {

    public final SQLStmt updateCoreSessionQuery = new SQLStmt(
            "UPDATE TBLT_SESSION SET" +
                    "  START_TIME = ?" +
                    ", LAST_UPDATE_TIME = ?" +
                    ", SUBSCRIBER_IDENTITY = ?" +   //0 index of values[], 2nd of updateArgs[]
                    ", CORE_SESSION_ID = ?" +
                    ", SESSION_ID = ?" +
                    ", SESSION_MANAGER_ID = ?" +
                    ", SESSION_IP_V4 = ?" +
                    ", SESSION_IP_V6 = ?" +
                    ", ACCESS_NETWORK = ?" +
                    ", SESSION_TYPE = ?" +
                    ", QOS_PROFILE = ?" +
                    ", SOURCE_GATEWAY = ?" +
                    ", SY_SESSION_ID = ?" +
                    ", SY_GATEWAY_NAME = ?" +
                    ", GATEWAY_NAME = ?" +
                    ", CONGESTION_STATUS = ?" +
                    ", IMSI = ?" +
                    ", MSISDN = ?" +
                    ", NAI = ?" +
                    ", NAI_REALM = ?" +
                    ", NAI_USER_NAME = ?" +
                    ", SIP_URL = ?" +
                    ", PCC_RULES = ?" +
                    ", REQUESTED_QOS = ?" +
                    ", SESSION_USAGE = ?" +
                    ", REQUEST_NUMBER = ?" +
                    ", USAGE_RESERVATION = ?" +
                    ", GATEWAY_ADDRESS = ?" +
                    ", GATEWAY_REALM = ?" +
                    ", PACKAGE_USAGE = ?" +
                    ", CHARGING_RULE_BASE_NAMES = ?" +
                    ", CALLING_STATION_ID = ?" +
                    ", CALLED_STATION_ID = ?" +
                    ", QUOTA_RESERVATION = ?" +
                    ", PCC_PROFILE_SELECTION_STATE = ?" +
                    ", UNACCOUNTED_QUOTA = ?" +
                    ", SGSN_MCC_MNC = ?" +
                    ", SERVICE = ?" +
                    ", LOCATION = ?" +
                    ", PARAM1 = ?" +
                    ", PARAM2 = ?" +
                    ", PARAM3 = ?" +
                    ", PARAM4 = ?" +
                    ", PARAM5 = ?" +    //41 index of values[], 43th updateArgs[]
                    " WHERE CORE_SESSION_ID = ?");

    public VoltTable[] run(String coreSessionId, String[] values, Date startTime) throws VoltAbortException {
        Object[] updateArgs = new Object[values.length + 3];
        System.arraycopy(values, 0, updateArgs, 2, values.length);
        updateArgs[0] = startTime;
        updateArgs[1] = getTransactionTime();
        updateArgs[values.length+2] = coreSessionId;
        voltQueueSQL(updateCoreSessionQuery, updateArgs);

        return voltExecuteSQL();
    }
}
