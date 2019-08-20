package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class DeleteCoreSessionsBySingleKeyValueStoredProcedure extends VoltProcedure {

    private static final String CS_CORESESSION_ID = "CS.CoreSessionID";
    private static final String CS_GATEWAY_ADDRESS = "CS.GatewayAddress";

    public final SQLStmt deleteCoreSessionByCoreSessionId = new SQLStmt(
            "DELETE FROM TBLT_SESSION WHERE CORE_SESSION_ID = ?");

    public final SQLStmt deleteCoreSessionByGatewayAddress = new SQLStmt(
            "DELETE FROM TBLT_SESSION WHERE GATEWAY_ADDRESS = ?");


    public VoltTable[] run(String key, String value) throws VoltAbortException {
        if(key.equals(CS_CORESESSION_ID)){
            voltQueueSQL(deleteCoreSessionByCoreSessionId, value);
        } else if(key.equals(CS_GATEWAY_ADDRESS)){
            voltQueueSQL(deleteCoreSessionByGatewayAddress, value);
        }

        return voltExecuteSQL(true);
    }
}
