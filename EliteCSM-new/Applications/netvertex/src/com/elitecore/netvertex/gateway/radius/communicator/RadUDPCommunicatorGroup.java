package com.elitecore.netvertex.gateway.radius.communicator;

import com.elitecore.core.systemx.esix.udp.UDPCommunicatorGroup;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceRequest;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceResponse;

/**
 * The RadUDPCommunicatorGroup provide the functionality for sending Radius Request. 
 * @author harsh
 */
public interface RadUDPCommunicatorGroup extends UDPCommunicatorGroup {
	/**
	 * This method 
	 * @param request
	 * @param response
	 * @param responseListner
	 */
	void handleRequest(RadServiceRequest request,RadServiceResponse response,RadResponseListner responseListner);
	void handleRequest(IRadiusPacket request, RadResponseListner radUDPResponseListener);
}
