package com.elitecore.aaa.radius.conf;

import java.util.List;

import com.elitecore.core.commons.data.SysLogConfiguration;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.util.url.SocketDetail;

public interface BaseRadiusServiceConfiguration {
	public List<SocketDetail> getSocketDetails();
	public boolean isServiceLevelLoggerEnabled(); // server or service ??
	public int mainThreadPriority(); 
	public int maxRequestQueueSize();
	public int maxThreadPoolSize(); 
	public int minThreadPoolSize();	 
	public int socketReceiveBufferSize(); 
	public int socketSendBufferSize(); 
	public int threadKeepAliveTime(); 
	public int workerThreadPriority(); 
	public String getKey();
	public String logLevel();
	public int logRollingType();
	public int logRollingUnit();
	public int logMaxRolledUnits();
	public SysLogConfiguration getSysLogConfiguration();
	
	public boolean isCompressLogRolledUnits();
	public String getLogLocation();
	public List<PluginEntryDetail> getPrePluginList();
	public List<PluginEntryDetail> getPostPluginList();
	
	public boolean isDuplicateRequestDetectionEnabled();
	public int getDupicateRequestQueuePurgeInterval();
}
