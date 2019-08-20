package com.elitecore.corenetvertex.core.db;

import org.voltdb.client.ClientResponse;
import org.voltdb.client.NoConnectionsException;
import org.voltdb.client.ProcCallException;
import org.voltdb.client.ProcedureCallback;

import java.io.IOException;

public interface VoltDBClient {

    void markAlive();
    void markDead();

    ClientResponse callProcedure(String procName, Object... parameters)
            throws IOException, NoConnectionsException, ProcCallException;

    boolean callProcedure(ProcedureCallback callback, String procName, Object... parameters)
            throws IOException, NoConnectionsException;

    boolean isAlive();
}
