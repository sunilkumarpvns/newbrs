package com.elitecore.netvertex.gateway.radius.communicator;

import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;

/**
 * The RadUDPRequest provide the functionality for accessing Radius Request packet 
 * @author Harsh Patel
 *
 */
public interface RadUDPRequest extends UDPRequest {
	/**
	 * This method return the Radius Request packet.
	 * @return<code>IRadiuPacket</code>
	 */
	IRadiusPacket getRadiusPacket();
	
	/**
	 * This method return the shared secret.
	 * @return <code>String</code>
	 */
	String getSharedSecret();
	
	/**
	 * This method set the shared secret.
	 * @param sharedSecret
	 */
	void setSharedSecret(String sharedSecret);
}
