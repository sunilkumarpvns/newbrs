package com.elitecore.netvertex.service.offlinernc.servicepolicy.handler;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCEvent;
import com.elitecore.netvertex.service.offlinernc.core.PacketOutputStream;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;

public class WriteHandler implements OfflineRnCHandler {

	@Override
	public void init() throws InitializationFailedException {
		// no-op
	}

	@Override
	public boolean isEligible(RnCRequest request) {
		return (OfflineRnCEvent.CDR == request.getEventType());
	}

	@Override
	public void handleRequest(RnCRequest request, RnCResponse response, PacketOutputStream out) throws Exception {
		out.writeSuccessful(request, response);
	}


}
