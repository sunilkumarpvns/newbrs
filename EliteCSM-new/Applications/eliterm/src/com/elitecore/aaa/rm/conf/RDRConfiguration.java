/**
 * 
 */
package com.elitecore.aaa.rm.conf;

import java.net.InetAddress;
import java.util.List;

import com.elitecore.aaa.rm.data.RDRClientDataImpl;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.util.url.SocketDetail;

/**
 * @author nitul.kukadia
 *
 */
public interface RDRConfiguration {
	public String getServiceAddress();
	public SocketDetail getSocketDetail();
	public boolean isServiceLevelLoggerEnabled();
	public void reloadConfiguration() throws LoadConfigurationException;  
	public int mainThreadPriority(); 
	public int maxRequestQueueSize();
	public int maxThreadPoolSize(); 
	public int minThreadPoolSize(); 
	public int socketReceiveBufferSize(); 
	public int socketSendBufferSize(); 
	public int threadKeepAliveTime(); 
	public int workerThreadPriority(); 
	public String redirectionIP();
	public String getKey();
	public String logLevel();
	public int logRollingType();
	public int logRollingUnit();
	public int logMaxRolledUnits();
	public boolean isCompressLogRolledUnits();

	public List<PluginEntryDetail> getPrePluginList();
	public List<PluginEntryDetail> getPostPluginList();
	
	public String getLogLocation();
	public void readConfiguration();
	
	public List<RDRClientDataImpl> getRDRClients();
	
	public RDRClientDataImpl getRDRClient(InetAddress ip) ;

}
