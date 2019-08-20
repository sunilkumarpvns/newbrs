package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class SelectSessionRulesBySingleKeyValueStoredProcedure extends VoltProcedure {
    private static final String CS_CORESESSION_ID = "CS.CoreSessionID";
    private static final String CS_AF_SESSION_ID = "CS.AFSessionId";

    public final SQLStmt selectSessionRuleByCoreSessionId = new SQLStmt(
            "SELECT * FROM TBLT_SUB_SESSION WHERE SESSION_ID = ?");

    public final SQLStmt selectSessionRuleByAfSessionId = new SQLStmt(
            "SELECT * FROM TBLT_SUB_SESSION WHERE AF_SESSION_ID = ?");

    public VoltTable[] run(String key, String value) throws VoltAbortException {
        if(key.equals(CS_CORESESSION_ID)){
            voltQueueSQL(selectSessionRuleByCoreSessionId, value);
        } else if(key.equals(CS_AF_SESSION_ID)){
            voltQueueSQL(selectSessionRuleByAfSessionId, value);
        }
        return voltExecuteSQL(true);
    }
}
