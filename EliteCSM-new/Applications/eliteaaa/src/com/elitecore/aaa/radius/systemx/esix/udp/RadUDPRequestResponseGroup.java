/**
 * 
 */
package com.elitecore.aaa.radius.systemx.esix.udp;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;

/**
 * @author pulin
 *
 */
public interface RadUDPRequestResponseGroup {

	public void addRequest(String key, RadServiceRequest request);
	
	public void addResponse(String key, IRadiusPacket response);
	
	public RadServiceRequest getRequestPacket(String key);
	
	public boolean containRequest(String key);
	
	public boolean hasAnyRequest();
}
