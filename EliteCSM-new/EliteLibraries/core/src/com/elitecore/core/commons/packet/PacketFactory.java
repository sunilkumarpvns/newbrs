/*
 *  Server Framework
 *  
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *  
 *  Created on 10th April 2006
 *  Author : Ezhava Baiju D  
 */

package com.elitecore.core.commons.packet;

import java.io.IOException;
import java.io.InputStream;

import com.elitecore.commons.logging.LogManager;
/**
 * Abstract base implementation of @see com.elitecore.core.commons.packet.IPacketFactory. 
 *
 */
public abstract class PacketFactory implements IPacketFactory {


	public IPacket createNewPacket(byte[] data){
		return createNewPacket();
	}
	
	public byte[] createPacketBytes(InputStream inStream) throws IOException{
		LogManager.getLogger().trace("PACKET-FACTORY", "Create Packet Bytes Method of Packet Factory called.");
		return null;
	}
}
