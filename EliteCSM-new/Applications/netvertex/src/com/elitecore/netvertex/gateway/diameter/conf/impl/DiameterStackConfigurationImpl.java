package com.elitecore.netvertex.gateway.diameter.conf.impl;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterStackConfiguration;

import java.util.List;

public class DiameterStackConfigurationImpl implements DiameterStackConfiguration{

	private boolean diameterListenerEnable = false;
	private String ipAddress = "0.0.0.0";
	private int port = DiameterConstants.DIAMETER_SERVICE_PORT;
	private int socketReceiveBufferSize = 32767;
	private int socketSendBufferSize = 32767;
	private int queueSize = 150000;
	private int minimumThread = 1;
	private int maximumThread = 3;
	private int mainThreadPriority = 1;
	private int workerThreadPriority = 1;
	private int keepAliveTime = 2000;
	private String originHost = "netvertex.elitecore.com";
	private String originRealm = "elitecore.com";
	private int dwInterval = 60;
	private long sessionCleanupInterval = 86400; // Seconds in a Day(24 hours)
	private long sessionTimeOut = 86400; // Seconds in a Day(24 hours)
	private boolean duplicateRequestCheckEnabled = false;
	private int duplicateRequestPurgeInterval = 15;

	public DiameterStackConfigurationImpl(boolean diameterListenerEnable, String ipAddress, int port,
			int socketReceiveBufferSize, int socketSendBufferSize, int queueSize, int minimumThread, int maximumThread, int mainThreadPriority,
			int workerThreadPriority, int keepAliveTime, String originHost, String originRealm, int dwInterval, long sessionCleanupInterval,
			long sessionTimeOut, boolean duplicateRequestCheckEnabled, int duplicateRequestPurgeInterval) {
		this.diameterListenerEnable = diameterListenerEnable;
		this.ipAddress = ipAddress;
		this.port = port;
		this.socketReceiveBufferSize = socketReceiveBufferSize;
		this.socketSendBufferSize = socketSendBufferSize;
		this.queueSize = queueSize;
		this.minimumThread = minimumThread;
		this.maximumThread = maximumThread;
		this.mainThreadPriority = mainThreadPriority;
		this.workerThreadPriority = workerThreadPriority;
		this.keepAliveTime = keepAliveTime;
		this.originHost = originHost;
		this.originRealm = originRealm;
		this.dwInterval = dwInterval;
		this.sessionCleanupInterval = sessionCleanupInterval;
		this.sessionTimeOut = sessionTimeOut;
		this.duplicateRequestCheckEnabled = duplicateRequestCheckEnabled;
		this.duplicateRequestPurgeInterval = duplicateRequestPurgeInterval;
	}
	
	/**
	 * To have a default configuration when RnC server instance is running
	 */
	public DiameterStackConfigurationImpl() {
		// no-op
	}

	@Override
	public boolean isEnabled() {
		return diameterListenerEnable;
	}

	@Override
	public String getIPAddress() {
		return ipAddress;
	}

	@Override
	public int getKeepAliveTime() {
		return keepAliveTime;
	}

	@Override
	public int getMainThreadPriority() {
		return mainThreadPriority;
	}

	@Override
	public int getMaximumThread() {
		return maximumThread;
	}

	@Override
	public int getMinimumThread() {
		return minimumThread;
	}
	
	@Override
	public String getOriginHost() {
		return originHost;
	}

	@Override
	public String getOriginRealm() {
		return originRealm;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public int getQueueSize() {
		return queueSize;
	}

	@Override
	public int getSocketReceiveBufferSize() {
		return socketReceiveBufferSize;
	}

	@Override
	public int getSocketSendBufferSize() {
		return socketSendBufferSize;
	}

	@Override
	public int getWorkerThreadPriority() {
		return workerThreadPriority;
	}
	
	@Override
	public long getSessionCleanupInterval() {
		return sessionCleanupInterval;
	}
	
	@Override
	public long getSessionTimeOut() {
		return sessionTimeOut;
	}
	
	@Override
	public List<String> getGatewayList() {
		return null;
	}
	
	@Override
	public int getDWInterval() {
		return dwInterval;
	}

	@Override
	public boolean getDuplicateRequestCheckEnabled() {
		return duplicateRequestCheckEnabled;
	}

	@Override
	public int getDuplicateRequestPurgeInterval() {
		return duplicateRequestPurgeInterval;
	}

	@Override
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- Diameter Stack Configuration-- ");
		builder.incrementIndentation();
		toString(builder);
		builder.decrementIndentation();
		return builder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.append("Diameter Stack Enabled", diameterListenerEnable);
		builder.append("IP Address", ipAddress);
		builder.append("Port", port);
		builder.append("Origin Host", originHost);
		builder.append("Origin Realm", originRealm);
		builder.append("Main Thread Priority", mainThreadPriority);
		builder.append("Maximum Thread", maximumThread);
		builder.append("Minimum Thread", minimumThread);
		builder.append("Queue Size", queueSize);
		builder.append("Socket Receive Size", socketReceiveBufferSize);
		builder.append("Socket Send Buffer size", socketSendBufferSize);
		builder.append("Worker Thread Priority", workerThreadPriority);
		builder.append("keeP Alive Time (In Sec)", keepAliveTime);
		builder.append("DW Interval  (In Sec)", dwInterval);
		builder.append("DuplicateRequest Check Enabled", duplicateRequestCheckEnabled);
		builder.append("DuplicateRequest Purge Interval", duplicateRequestPurgeInterval);
		builder.append("Session Cleanup Interval (In Sec)", sessionCleanupInterval);
		builder.append("Session TimeOut (In Sec)", sessionTimeOut);
	}
}
