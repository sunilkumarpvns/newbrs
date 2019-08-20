package com.elitecore.netvertex.gateway.radius.communicator;

import com.elitecore.core.systemx.esix.udp.UDPResponse;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;

/**
 * This RadUDPResponse interface provide functionality for accessing the Radius Response packet.
 * @author Harsh Patel
 *
 */
public interface RadUDPResponse extends UDPResponse {
	
	/**
	 * This method return the Radius Response Packet
	 * @return<code>IRadiusPacket</code>
	 */
	IRadiusPacket getRadiusPacket();
}
