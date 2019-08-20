/**
 * 
 */
package com.elitecore.core.servicex;

import java.net.InetAddress;

/**
 * @author nitul.kukadia
 *
 */
public interface TCPServiceRequest extends ServiceRequest {
	
	public InetAddress getSourceAddress();
	
	public int getSourcePort();
	
	public byte[] getRequestBytes();

}
