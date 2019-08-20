package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.util.UUID;

public class SaveSubSessionStoredProcedure extends VoltProcedure {

    public final SQLStmt insertSubSessionQuery = new SQLStmt(
            "INSERT INTO TBLT_SUB_SESSION(" +
                    "SR_ID" +
                    ", START_TIME" +
                    ", LAST_UPDATE_TIME" +
                    ", SESSION_ID" +
                    ", AF_SESSION_ID" +
                    ", PCC_RULE" +
                    ", MEDIA_TYPE" +
                    ", MEDIA_COMPONENT_NUMBER" +
                    ", FLOW_NUMBER" +
                    ", GATEWAY_ADDRESS" +
                    ", UPLINK_FLOW" +
                    ", DOWNLINK_FLOW" +
                    ", ADDITIONAL_PARAMETER" +
                    ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");

    public VoltTable[] run(String coreSessionId, String[] args) throws VoltAbortException { //NOSONAR -- FIRST ARGUMENT SHOULD ALWAYS BE PARTITION KEY
        Object[] insertArgs = new Object[args.length + 3];
        System.arraycopy(args, 0, insertArgs, 3, args.length);
        insertArgs[0] = UUID.randomUUID().toString();
        insertArgs[1] = getTransactionTime();
        insertArgs[2] = getTransactionTime();
        voltQueueSQL(insertSubSessionQuery, insertArgs);
        return voltExecuteSQL();
    }
}
