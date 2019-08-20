package com.elitecore.netvertex.core.session.voltdb;

import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.core.serverx.sessionx.SchemaMapping;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionResultCode;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.core.db.VoltDBClient;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;


public class VoltSynchronousSave {

    private static final String MODULE = "VOLT-SAVE-OPERATION";
    private VoltDBClient voltDBClient;

    public VoltSynchronousSave(VoltDBClient voltDBClient) {
        this.voltDBClient = voltDBClient;
    }

    public int execute(SessionData sessionData, SchemaMapping schemaMapping, String procName) {
        int saveStatus = SessionResultCode.FAILURE.code;

        try {
            String[] sessionDataArray = getDataArray(sessionData, schemaMapping);
            String coreSessionId = sessionData.getValue(PCRFKeyConstants.CS_CORESESSION_ID.val);
            callProcedure(procName, sessionDataArray, coreSessionId);
            saveStatus = SessionResultCode.SUCCESS.code;
        } catch (Exception e) {
            getLogger().error(MODULE, "Error in saving session, Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
        return saveStatus;
    }

    private void callProcedure(String procName, String[] sessionDataArray, String coreSessionId) throws ProcCallException, IOException {
        voltDBClient.callProcedure(procName, coreSessionId, sessionDataArray);
    }

    private String[] getDataArray(SessionData sessionData, SchemaMapping schemaMapping) {
        List<FieldMapping> fieldMappings = schemaMapping.getFieldMappings();
        String[] newSessionDataArr = new String[fieldMappings.size()];

        for (int i=0; i < fieldMappings.size(); i++) {
            newSessionDataArr[i] = sessionData.getValue(fieldMappings.get(i).getPropertyName());
        }

        return newSessionDataArr;
    }
}