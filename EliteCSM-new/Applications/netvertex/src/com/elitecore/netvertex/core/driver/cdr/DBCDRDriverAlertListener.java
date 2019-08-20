package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.core.commons.alert.AlertListener;
import com.elitecore.core.commons.alert.Events;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.alerts.Alerts;

public class DBCDRDriverAlertListener implements AlertListener {


    private static final String MODULE = "DB-ALERT-DRIVER-LISTENER";
    private NetVertexServerContext serverContext;

    public DBCDRDriverAlertListener(NetVertexServerContext serverContext) {
        this.serverContext = serverContext;
    }

    @Override
    public void generateAlert(Events event, String message) {
        if (event == Events.DB_CONNECTION_NOT_AVAILABLE) {
            serverContext.generateSystemAlert("", Alerts.DB_NO_CONNECTION, MODULE, message);
        }
    }
}
