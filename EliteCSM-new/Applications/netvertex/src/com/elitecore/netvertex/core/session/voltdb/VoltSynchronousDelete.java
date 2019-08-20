package com.elitecore.netvertex.core.session.voltdb;

import com.elitecore.core.serverx.sessionx.SessionResultCode;
import com.elitecore.corenetvertex.core.db.VoltDBClient;
import org.voltdb.client.ProcCallException;

import java.io.IOException;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class VoltSynchronousDelete {

    private static final String MODULE = "VOLT-DELETE-OPERATION";
    private VoltDBClient voltDBClient;

    public VoltSynchronousDelete(VoltDBClient voltDBClient) {
        this.voltDBClient = voltDBClient;
    }

    public int delete(VoltCriteriaData voltCriteriaData) {
        int deleteStatus = SessionResultCode.FAILURE.code;
        try {
            callProcedure(voltCriteriaData.getStoredProcedureName(), voltCriteriaData.getSimpleKey()
                    , voltCriteriaData.getSimpleValue());
            deleteStatus = SessionResultCode.SUCCESS.code;
        } catch (Exception e) {
            getLogger().error(MODULE, "Error in deleting session, Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
        return deleteStatus;
    }

    private void callProcedure(String procName, String key, String value) throws IOException, ProcCallException {
        voltDBClient.callProcedure(procName, key, value);
    }
}