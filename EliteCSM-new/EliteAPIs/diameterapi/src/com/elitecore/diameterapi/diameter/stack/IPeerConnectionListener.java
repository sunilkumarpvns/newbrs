/**
 * 
 */
package com.elitecore.diameterapi.diameter.stack;

import java.io.IOException;

import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;

/**
 * @author pulindani
 *
 */
public interface IPeerConnectionListener {
	
	public void sendDiameterPacket(DiameterPacket diameterPacket) throws IOException;
	
	public boolean isConnected();

}
