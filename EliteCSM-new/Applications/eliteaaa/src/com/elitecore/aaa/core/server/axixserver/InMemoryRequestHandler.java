package com.elitecore.aaa.core.server.axixserver;

import java.net.UnknownHostException;

import com.elitecore.core.system.comm.ILocalResponseListener;

public interface InMemoryRequestHandler {
	public void handleRequest(byte[] requestBytes, ILocalResponseListener responseListener) throws UnknownHostException;
}
