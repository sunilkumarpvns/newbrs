package com.elitecore.netvertex.service.offlinernc.servicepolicy.handler;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.netvertex.service.offlinernc.core.PacketOutputStream;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;

public interface OfflineRnCHandler {
	
	void init() throws InitializationFailedException;
	
	boolean isEligible(RnCRequest request);
	
	void handleRequest(RnCRequest request, RnCResponse response, PacketOutputStream out) throws Exception;
}
