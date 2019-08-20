package com.elitecore.nvsmx.system.spr;

import com.elitecore.corenetvertex.core.db.VoltDBClient;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.NoConnectionsException;
import org.voltdb.client.ProcCallException;
import org.voltdb.client.ProcedureCallback;

import java.io.IOException;

public class VoltDBClientAdapter implements VoltDBClient {

    private com.elitecore.core.commons.util.voltdb.VoltDBClient voltDBClient;

    public VoltDBClientAdapter(com.elitecore.core.commons.util.voltdb.VoltDBClient voltDBClient) {
        this.voltDBClient = voltDBClient;
    }

    @Override
    public void markAlive() {
        voltDBClient.markAlive();
    }

    @Override
    public void markDead() {
        voltDBClient.markDead();
    }

    @Override
    public ClientResponse callProcedure(String procName, Object... parameters) throws IOException, NoConnectionsException, ProcCallException {
        return voltDBClient.callProcedure(procName, parameters);
    }

    @Override
    public boolean callProcedure(ProcedureCallback callback, String procName, Object... parameters) throws IOException, NoConnectionsException {
        return voltDBClient.callProcedure(callback, procName, parameters);
    }

    @Override
    public boolean isAlive() {
        return voltDBClient.isAlive();
    }
}
