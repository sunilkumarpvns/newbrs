package com.elitecore.netvertex.gateway.radius.conf;

import com.elitecore.corenetvertex.util.ToStringable;

import java.util.List;


/**
 * Configuration class for Radius Gateway Event Listener
 * 
 * @author Subhash Punani
 *
 */

public interface RadiusListenerConfiguration extends ToStringable {
	public boolean isEnabled();
	public String getIPAddress();
	public int getPort();
	public int getSocketReceiveBufferSize();
	public int getSocketSendBufferSize();
	public int getQueueSize();
	public int getMinimumThread();
	public int getMaximumThread();
	public int getMainThreadPriority();
	public int getWorkerThreadPriority();
	boolean isDuplicateRequestDetectionEnabled();
	int getDupicateRequestQueuePurgeInterval();
	public void setIpAddress(String ipAddress);
	public void setPort(int port);
}
