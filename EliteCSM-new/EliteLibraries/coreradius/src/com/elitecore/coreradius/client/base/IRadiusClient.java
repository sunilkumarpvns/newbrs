package com.elitecore.coreradius.client.base;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import com.elitecore.coreradius.client.CommunicationException;
import com.elitecore.coreradius.client.RequestTimeoutException;
import com.elitecore.coreradius.commons.RadiusGeneralException;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;

public interface IRadiusClient {

	public void openSocket() throws CommunicationException;

	public void closeSocket() throws CommunicationException;

	public void setRequestType(int requestType);

	public int getRequestType();
	
	public void setServerAddress(InetAddress serverAddress);

	public InetAddress getServerAddress();
	
	public void setServerPort(int serverPort);

	public int getServerPort(); 
	
	public void setSharedSecret(String sharedSecret);

	public String getSharedSecret(); 
	
	public void setTimeout(int timeout);

	public int getTimeout();
	
	public IRadiusPacket sendReceiveRadiusPacket(IRadiusPacket radiusRequestPacket) throws RequestTimeoutException, CommunicationException, IOException, RadiusGeneralException;
	
	public IRadiusPacket createRadiusRequestPacket(List<IRadiusAttribute> requestAttributes);
}
