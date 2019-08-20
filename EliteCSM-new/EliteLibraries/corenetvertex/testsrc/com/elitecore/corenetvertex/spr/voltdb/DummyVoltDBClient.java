package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.corenetvertex.core.db.VoltDBClient;
import org.voltdb.client.Client;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.NoConnectionsException;
import org.voltdb.client.ProcCallException;
import org.voltdb.client.ProcedureCallback;

import java.io.IOException;

public class DummyVoltDBClient implements VoltDBClient {

    private static final String MODULE = "Dummy-VoltDB-Client";
    private Client client;

    public DummyVoltDBClient(Client client) {
        this.client = client;
    }

    @Override
    public void markAlive() { }

    @Override
    public void markDead() { }

    @Override
    public ClientResponse callProcedure(String procName, Object... parameters)
            throws IOException, NoConnectionsException, ProcCallException {
        return client.callProcedure(procName, parameters);
    }

    @Override
    public boolean callProcedure(ProcedureCallback callback, String procName, Object... parameters) throws IOException, NoConnectionsException {
        return client.callProcedure(callback, procName, parameters);
    }

    @Override
    public boolean isAlive() {
        return true;
    }
}
