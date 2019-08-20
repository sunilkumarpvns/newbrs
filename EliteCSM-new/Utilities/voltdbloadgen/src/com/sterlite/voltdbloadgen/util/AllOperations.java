package com.sterlite.voltdbloadgen.util;

import org.voltdb.VoltTable;
import org.voltdb.client.Client;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.NullCallback;


import static com.sterlite.voltdbloadgen.dbtask.AddToExistingUsageTask.createUsageArrayForReplaceUsage;
import static com.sterlite.voltdbloadgen.dbtask.InsertCoreSessionTask.getSessionDataArray;
import static com.sterlite.voltdbloadgen.dbtask.InsertUsageTask.createInsertUsageArray;
import static com.sterlite.voltdbloadgen.util.Commons.printIfFail;

public class AllOperations {

    private static String SESSION_SELECT = "VoltSelectCoreSessionById";
    private static String SESSION_INSERT = "VoltSaveCoreSessionV2";
    private static String SESSION_UPDATE = "VoltUpdateCoreSession";
    private static String SESSION_DELETE = "VoltDeleteCoreSessionById";
    private static String USAGE_INSERT = "VoltInsertUsage";
    private static String USAGE_SELECT = "VoltGetUsageV2";
    private static String USAGE_UPDATE = "VoltAddToExistingUsageV2";
    private static String SUBSCRIBER_SELECT = "VoltSelectProfileById";
    public static boolean isSync;

    private static NullCallback NULL_CALL_BACK = new NullCallback();

    public static void session_insert(Client client, String sessionId) throws Exception {
        String[] sessionData = getSessionDataArray(sessionId);
        if (isSync) {
            client.callProcedure(SESSION_INSERT, sessionId, sessionData);
        } else {
            client.callProcedure(NULL_CALL_BACK, SESSION_INSERT, sessionId, sessionData);
        }
    }

    public static void session_select(Client client, String sessionId) throws Exception {
        ClientResponse clientResponse = client.callProcedure(SESSION_SELECT, sessionId);
        printIfFail(clientResponse, sessionId);
    }

    public static void session_update(Client client, String sessionId) throws Exception {
        String[] sessionData = getSessionDataArray(sessionId);
        if (isSync) {
            client.callProcedure(SESSION_UPDATE, sessionId, sessionData, new java.util.Date());
        } else {
            client.callProcedure(NULL_CALL_BACK, SESSION_UPDATE, sessionId, sessionData, new java.util.Date());
        }
    }

    public static void session_delete(Client client, String sessionId) throws Exception {
        if (isSync) {
            client.callProcedure(SESSION_DELETE, sessionId);
        } else {
            client.callProcedure(NULL_CALL_BACK, SESSION_DELETE, sessionId);
        }
    }

    public static void usage_insert(Client client, String subscriberId) throws Exception {
        if (isSync) {
            client.callProcedure(USAGE_INSERT, subscriberId, createInsertUsageArray(subscriberId));
        } else {
            client.callProcedure(NULL_CALL_BACK, USAGE_INSERT, subscriberId, createInsertUsageArray(subscriberId));
        }
    }

    public static VoltTable usage_select(Client client, String subscriberId) throws Exception {
        ClientResponse clientResponse = client.callProcedure(USAGE_SELECT, subscriberId);
        return clientResponse.getResults()[0];
    }

    public static void usage_update(Client client, VoltTable voltTable) throws Exception {
        voltTable.advanceRow();
        String subscriberId = voltTable.getString("SUBSCRIBER_ID");
        String id = voltTable.getString("ID");

        if (isSync) {
            client.callProcedure(USAGE_UPDATE, subscriberId, createUsageArrayForReplaceUsage(subscriberId, id));
        } else {
            client.callProcedure(NULL_CALL_BACK, USAGE_UPDATE, subscriberId, createUsageArrayForReplaceUsage(subscriberId, id));
        }
    }

    public static void subscriber_select(Client client, String subscriberId) throws Exception {
        ClientResponse clientResponse = client.callProcedure(SUBSCRIBER_SELECT, subscriberId);
        printIfFail(clientResponse, subscriberId);
    }

    public static void usageOps(Client client, String subscriberId) throws Exception {
        VoltTable voltTable = usage_select(client, subscriberId);

        if (voltTable == null) return;

        if (voltTable.getRowCount() > 0) {
            usage_update(client, voltTable);
        } else {
            usage_insert(client, subscriberId);
        }
    }
}
