package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class SelectCoreSessionsBySingleKeyValueStoredProcedure extends VoltProcedure {

    private static final String CS_CORESESSION_ID = "CS.CoreSessionID";
    private static final String CS_SESSION_ID = "CS.SessionID";
    private static final String SUB_SUBSCRIBER_IDENTITY = "Sub.SubscriberIdentity";
    private static final String CS_SESSION_IPV4 = "CS.SessionIPv4";
    private static final String CS_SESSION_IPV6 = "CS.SessionIPv6";
    private static final String CS_SY_SESSION_ID = "CS.SySessionId";
    private static final String CS_GATEWAY_ADDRESS = "CS.GatewayAddress";

    public final SQLStmt selectCoreSessionByCoreSessionId = new SQLStmt(
            "SELECT * FROM TBLT_SESSION WHERE CORE_SESSION_ID = ?");

    public final SQLStmt selectCoreSessionBySessionId = new SQLStmt(
            "SELECT * FROM TBLT_SESSION WHERE SESSION_ID = ?");

    public final SQLStmt selectCoreSessionBySubscriberId = new SQLStmt(
            "SELECT * FROM TBLT_SESSION WHERE SUBSCRIBER_IDENTITY = ?");

    public final SQLStmt selectCoreSessionByIPv4 = new SQLStmt(
            "SELECT * FROM TBLT_SESSION WHERE SESSION_IP_V4 = ?");

    public final SQLStmt selectCoreSessionByIPv6 = new SQLStmt(
            "SELECT * FROM TBLT_SESSION WHERE SESSION_IP_V6 = ?");

    public final SQLStmt selectCoreSessionBySySessionId = new SQLStmt(
            "SELECT * FROM TBLT_SESSION WHERE SY_SESSION_ID = ?");

    public final SQLStmt selectCoreSessionByGatewayAddress = new SQLStmt(
            "SELECT * FROM TBLT_SESSION WHERE GATEWAY_ADDRESS = ?");


    public VoltTable[] run(String key, String value) throws VoltAbortException {

        if(key.equals(CS_CORESESSION_ID)){
            voltQueueSQL(selectCoreSessionByCoreSessionId, value);
        } else if(key.equals(CS_SESSION_ID)){
            voltQueueSQL(selectCoreSessionBySessionId, value);
        } else if(key.equals(SUB_SUBSCRIBER_IDENTITY)){
            voltQueueSQL(selectCoreSessionBySubscriberId, value);
        } else if(key.equals(CS_SESSION_IPV4)){
            voltQueueSQL(selectCoreSessionByIPv4, value);
        } else if(key.equals(CS_SESSION_IPV6)){
            voltQueueSQL(selectCoreSessionByIPv6, value);
        } else if(key.equals(CS_SY_SESSION_ID)){
            voltQueueSQL(selectCoreSessionBySySessionId, value);
        } else if(key.equals(CS_GATEWAY_ADDRESS)){
            voltQueueSQL(selectCoreSessionByGatewayAddress, value);
        }

        return voltExecuteSQL(true);
    }
}
