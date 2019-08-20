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

/**
 * This interface defines factory for creating packets. An implementation
 * should create appropriate packet as per the protocol of the service.
 *
 */
public interface IPacketFactory {
    
	/**
	 * Is supposed to return the service/protocol specific implementation of 
	 * the packet.
	 *  
	 * @return
	 */
    public IPacket createNewPacket();
    
    /**
	 * Is supposed to return the service/protocol specific implementation of 
	 * the packet. Here the raw packet data is also passed, so that if any 
	 * version specific different protocol is there for same service, it can
	 * return version specific packet class implementation.
	 * 
     * @param data
     * @return
     */
    public IPacket createNewPacket(byte[] data);
    
}
