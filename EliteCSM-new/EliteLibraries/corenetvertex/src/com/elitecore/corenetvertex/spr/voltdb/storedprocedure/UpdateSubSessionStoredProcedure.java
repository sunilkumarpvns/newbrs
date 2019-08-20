package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.util.Date;

public class UpdateSubSessionStoredProcedure extends VoltProcedure {

    public final SQLStmt updateSubSessionQuery = new SQLStmt(
            "UPDATE TBLT_SUB_SESSION SET" +
                    "  START_TIME = ?" +
                    ", LAST_UPDATE_TIME = ?" +
                    ", SESSION_ID = ?" +
                    ", AF_SESSION_ID = ?" +
                    ", PCC_RULE = ?" +
                    ", MEDIA_TYPE = ?" +
                    ", MEDIA_COMPONENT_NUMBER = ?" +
                    ", FLOW_NUMBER = ?" +
                    ", GATEWAY_ADDRESS = ?" +
                    ", UPLINK_FLOW = ?" +
                    ", DOWNLINK_FLOW = ?" +
                    ", ADDITIONAL_PARAMETER = ?" +
                    " WHERE SESSION_ID = ?");

    public VoltTable[] run(String coreSessionId, String[] values, Date startTime) throws VoltAbortException {
        Object[] updateRuleArgs = new Object[values.length + 3];
        System.arraycopy(values, 0, updateRuleArgs, 2, values.length);
        updateRuleArgs[0] = startTime;
        updateRuleArgs[1] = getTransactionTime();
        updateRuleArgs[values.length+2] = coreSessionId;
        voltQueueSQL(updateSubSessionQuery, updateRuleArgs);

        return voltExecuteSQL();
    }
}
