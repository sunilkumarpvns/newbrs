package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class DeleteSessionRulesBySingleKeyValueStoredProcedure extends VoltProcedure {
    private static final String CS_CORESESSION_ID = "CS.CoreSessionID";
    private static final String CS_AF_SESSION_ID = "CS.AFSessionId";
    private static final String CS_GATEWAY_ADDRESS = "CS.GatewayAddress";

    public final SQLStmt deleteSessionRuleByCoreSessionId = new SQLStmt(
            "DELETE FROM TBLT_SUB_SESSION WHERE SESSION_ID = ?");

    public final SQLStmt deleteSessionRuleByAfSessionId = new SQLStmt(
            "DELETE FROM TBLT_SUB_SESSION WHERE AF_SESSION_ID = ?");

    public final SQLStmt deleteSessionRuleByGatewayAddress = new SQLStmt(
            "DELETE FROM TBLT_SUB_SESSION WHERE GATEWAY_ADDRESS = ?");

    public VoltTable[] run(String key, String value) throws VoltAbortException {
        if(key.equals(CS_CORESESSION_ID)){
            voltQueueSQL(deleteSessionRuleByCoreSessionId, value);
        } else if(key.equals(CS_AF_SESSION_ID)){
            voltQueueSQL(deleteSessionRuleByAfSessionId, value);
        } else if(key.equals(CS_GATEWAY_ADDRESS)){
            voltQueueSQL(deleteSessionRuleByGatewayAddress, value);
        }
        return voltExecuteSQL(true);
    }
}
