package com.elitecore.netvertex.service.offlinernc.core;

public interface PacketOutputStream {
	
	void writeSuccessful(RnCRequest request, RnCResponse response) throws Exception;
	
	void writeError(RnCRequest request, RnCResponse response) throws Exception;
}
