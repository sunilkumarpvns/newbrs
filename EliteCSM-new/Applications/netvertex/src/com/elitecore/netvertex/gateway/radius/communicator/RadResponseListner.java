package com.elitecore.netvertex.gateway.radius.communicator;

import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceRequest;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceResponse;

public interface RadResponseListner  {
	/**
	 * This method do the processing on received response packet.
	 * @param radUDPResponse
	 */
	void responseReceived(RadUDPRequest radUDPRequest,RadUDPResponse radUDPResponse);
	void requestDropped(UDPRequest radUDPRequest);
	void requestTimeout(UDPRequest radUDPRequest);
	RadServiceRequest getServiceRequest();
	RadServiceResponse getServiceResponse();
}
