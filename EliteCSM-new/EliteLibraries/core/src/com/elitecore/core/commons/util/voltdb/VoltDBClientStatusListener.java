package com.elitecore.core.commons.util.voltdb;

import org.voltdb.client.ClientStatusListenerExt;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class VoltDBClientStatusListener extends ClientStatusListenerExt {
    private static final String MODULE = "VOLTDB-CLIENT";
    private String dsName;

    public VoltDBClientStatusListener(String dsName) {
        this.dsName = dsName;
    }

    @Override
    public void connectionLost(String hostname, int port, int connectionsLeft, DisconnectCause cause) {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Connection lost with datasource: " + dsName + ",host: " + hostname + ", port: " + port
                    + ", Connection left: " + connectionsLeft +". DisconnectCause: " + cause);
        }
    }

    @Override
    public void connectionCreated(String hostname, int port, AutoConnectionStatus status) {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Connection created event triggered with datasource: " + dsName + ",host: " + hostname + ", port: " + port
                    + ". AutoConnectionStatus: " + status);
        }
    }
}