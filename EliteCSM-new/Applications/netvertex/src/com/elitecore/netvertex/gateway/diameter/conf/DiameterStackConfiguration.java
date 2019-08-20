package com.elitecore.netvertex.gateway.diameter.conf;

import com.elitecore.corenetvertex.util.ToStringable;

import java.util.List;


public interface DiameterStackConfiguration extends ToStringable{
	
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
	public int getKeepAliveTime();
	public String getOriginHost();
	public String getOriginRealm();
	int getDWInterval();
	long getSessionCleanupInterval();
	long getSessionTimeOut();
	boolean getDuplicateRequestCheckEnabled();
	int getDuplicateRequestPurgeInterval();
	List<String> getGatewayList();
	void setIpAddress(String ipAddress);
	void setPort(int port);
}
