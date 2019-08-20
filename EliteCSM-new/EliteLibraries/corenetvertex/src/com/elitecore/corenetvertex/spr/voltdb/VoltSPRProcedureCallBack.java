package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.ProcedureCallback;

import static com.elitecore.commons.logging.LogManager.getLogger;


public class VoltSPRProcedureCallBack implements ProcedureCallback {


    private AlertListener alertListener;
    private final String message;
    private final String module;

    public VoltSPRProcedureCallBack(AlertListener alertListener,String message, String module) {
        this.alertListener = alertListener;
        this.message = message;
        this.module = module;
    }

    @Override
    public void clientCallback(ClientResponse clientResponse) throws Exception {
        if(ClientResponse.SUCCESS != clientResponse.getStatus()){
            handleFailScenario(clientResponse,message,alertListener);
        }
    }


    public void handleFailScenario(ClientResponse clientResponse, String message,AlertListener alertListener) {

        switch (clientResponse.getStatus()) {
            case ClientResponse.CONNECTION_LOST:
                alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, module,
                        "Connection lost while " + message);
                getLogger().error(module,"Connection is lost while "+message+createErrorMessage(clientResponse));
                break;

            case ClientResponse.CONNECTION_TIMEOUT:
                alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, module,
                        "DB query timeout while " + message);
                getLogger().error(module,"Connection time out while "+message+createErrorMessage(clientResponse));
                break;

            case ClientResponse.SERVER_UNAVAILABLE:
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(module, "VoltDB is Dead. Reason: Service is not available");
                }
                getLogger().error(module,"Service is not available while "+message+createErrorMessage(clientResponse));
                break;
            default:
                getLogger().error(module,"Internal Error occur while "+message+createErrorMessage(clientResponse));

        }
    }

    public static String createErrorMessage(ClientResponse clientResponse) {
        return " Error Code: " + clientResponse.getStatus()
                + ". Reason: " + clientResponse.getStatusString();
    }

}