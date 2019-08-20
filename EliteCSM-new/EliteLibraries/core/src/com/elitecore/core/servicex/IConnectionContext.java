/*
 *  Core Server Framework
 *
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 12th December 2006
 *  Created By Ezhava Baiju D
 */

package com.elitecore.core.servicex;

import java.io.IOException;

import com.elitecore.core.commons.packet.IPacket;

public interface IConnectionContext {
	
	/**
	 * 
	 * @param packet
	 * @throws IOException
	 */
	public void sendResponse(IPacket packet) throws IOException;
	
	public void sendResponse(byte[] packetBytes) throws IOException;

   //TODO baiju change these methods signature 
   public Object getObject(Object objectKey);
   
   public void setObject(Object objectKey, Object objectValue);
   
   public void setDisconnectionState(boolean bClosedConnection);
   
   public boolean getDisconnectionState();
   public void closeConnection();
   
   public String getHostAddress();
}
