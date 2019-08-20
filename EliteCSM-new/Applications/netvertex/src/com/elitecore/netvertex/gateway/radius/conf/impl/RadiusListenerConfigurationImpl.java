package com.elitecore.netvertex.gateway.radius.conf.impl;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.netvertex.gateway.radius.conf.RadiusListenerConfiguration;

public class RadiusListenerConfigurationImpl implements RadiusListenerConfiguration {

	private boolean radiusListnerEnabled = false;
	private String ipAddress = "0.0.0.0";
	private int port = 2813;
	private int socketReceiveBufferSize = 32767;
	private int socketSendBufferSize = 32767;
	private int queueSize = 150000;
	private int minimumThread = 1;
	private int maximumThread = 3;
	private int mainThreadPriority = 1;
	private int workerThreadPriority = 1;
	private boolean duplicateRequestCheckEnabled = false;
	private int duplicateRequestQueuePurgeInterval = 15;
	
	
	public RadiusListenerConfigurationImpl(boolean radiusListnerEnabled, String ipAddress, int port,
                                           int socketReceiveBufferSize, int socketSendBufferSize, int queueSize, int minimumThread, int maximumThread, int mainThreadPriority,
                                           int workerThreadPriority, boolean duplicateRequestCheckEnabled, int duplicateRequestQueuePurgeInterval) {
		this.radiusListnerEnabled = radiusListnerEnabled;
		this.ipAddress = ipAddress;
		this.port = port;
		this.socketReceiveBufferSize = socketReceiveBufferSize;
		this.socketSendBufferSize = socketSendBufferSize;
		this.queueSize = queueSize;
		this.minimumThread = minimumThread;
		this.maximumThread = maximumThread;
		this.mainThreadPriority = mainThreadPriority;
		this.workerThreadPriority = workerThreadPriority;
		this.duplicateRequestCheckEnabled = duplicateRequestCheckEnabled;
		this.duplicateRequestQueuePurgeInterval = duplicateRequestQueuePurgeInterval;
	}
	
	/**
	 * To have a default configuration when RnC server instance is running
	 */
	
	public RadiusListenerConfigurationImpl() {
		//no-op
	}

	@Override
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
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
	public String getIPAddress() {
		return ipAddress;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public boolean isEnabled() {
		return radiusListnerEnabled;
	}
	
	@Override
	public boolean isDuplicateRequestDetectionEnabled() {
		return duplicateRequestCheckEnabled;
	}

	@Override
	public int getDupicateRequestQueuePurgeInterval() {
		return duplicateRequestQueuePurgeInterval;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- Radius Listener Configuration -- ");
		builder.incrementIndentation();
		toString(builder);
		builder.decrementIndentation();
		return builder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.append("Radius Listener Enabled", radiusListnerEnabled);
		builder.append("IP Address", ipAddress);
		builder.append("Port", port);
		builder.append("Main Thread Priority", mainThreadPriority);
		builder.append("Maximum Thread", maximumThread);
		builder.append("Minimum Thread", minimumThread);
		builder.append("Queue Size", queueSize);
		builder.append("Socket Receive Size", socketReceiveBufferSize);
		builder.append("Socket Send Buffer size", socketSendBufferSize);
		builder.append("Worker Thread Priority", workerThreadPriority);
		builder.append("Duplicate Check Enabled", duplicateRequestCheckEnabled);
		builder.append("Duplicate Check Queue Purge Interval ", duplicateRequestQueuePurgeInterval);
	}
}
