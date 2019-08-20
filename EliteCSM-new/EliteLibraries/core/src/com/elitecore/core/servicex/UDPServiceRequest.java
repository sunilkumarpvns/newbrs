package com.elitecore.core.servicex;

import java.net.InetAddress;
import com.elitecore.core.util.url.SocketDetail;

public interface UDPServiceRequest extends ServiceRequest{

	/**
	 * 
	 * @return Returns InetAddress from where request is received.
	 */
	public InetAddress getSourceAddress();
	
	
	/**
	 * 
	 * @return Returns the port number from where the request is received.
	 */
	public int getSourcePort();
	/**
	 * 
	 * @return Server <b>Socket</b> on which request has been received
	 */
	public SocketDetail getServerSocket();
	
	/**
	 * 
	 * @return Returns raw request packet bytes.
	 */
	public byte[] getRequestBytes();

	public IPacketHash getPacketHash();
}
