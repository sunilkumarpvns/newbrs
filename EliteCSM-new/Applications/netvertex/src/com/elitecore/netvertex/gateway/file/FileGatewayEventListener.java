package com.elitecore.netvertex.gateway.file;

import com.elitecore.netvertex.service.offlinernc.core.PacketOutputStream;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;

public interface FileGatewayEventListener {

	public void received(RnCRequest request, RnCResponse response, PacketOutputStream out) throws Exception;

	public void receivedIntermediate(RnCRequest intermediateRequest, RnCResponse rncResponse,
			PacketOutputStream outputStream) throws Exception;
}
