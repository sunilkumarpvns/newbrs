/**
 * 
 */
package com.elitecore.aaa.rm.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.List;

import com.elitecore.aaa.rm.conf.RDRConfiguration;
import com.elitecore.aaa.rm.data.RDRClientDataImpl;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.util.url.SocketDetail;

/**
 * @author nitul.kukadia
 *
 */

@ReadOrder(order = { "rdrConfigurable"})
public class RDRConfigurationImpl extends CompositeConfigurable implements RDRConfiguration {
	
	@Configuration private RDRConfigurable rdrConfigurable;
	private final static String KEY = "RDR_SER";
	
	@Deprecated
	public RDRConfigurationImpl(ServerContext serverContext) {
		
	}
	
	public RDRConfigurationImpl() {
		
	}

	@Override
	public void readConfiguration() {

	}
	
	@Override
	public String getServiceAddress() {		
		return rdrConfigurable.getSocketDetail().getIPAddress();
	}
	@Override
	public SocketDetail getSocketDetail() {		
		return rdrConfigurable.getSocketDetail();
	}
	@Override
	public boolean isServiceLevelLoggerEnabled() {		
		return rdrConfigurable.getLogger().getIsLoggerEnabled();
	}
	@Override
	public void reloadConfiguration() throws LoadConfigurationException {
		
	}
	@Override
	public int mainThreadPriority() {		
		return rdrConfigurable.getMainThreadPriority();
	}
	@Override
	public int maxRequestQueueSize() {		
		return rdrConfigurable.getQueueSize();
	}
	@Override
	public int maxThreadPoolSize() {
		return rdrConfigurable.getMaximumThread();
	}
	@Override
	public int minThreadPoolSize() {	
		return rdrConfigurable.getMinimumThread();
	}
	@Override
	public int socketReceiveBufferSize() {		
		return rdrConfigurable.getSocketReceiveBufferSize();
	}
	@Override
	public int socketSendBufferSize() {
		return rdrConfigurable.getSocketSendBufferSize();
	}
	@Override
	public int threadKeepAliveTime() {		
		return (1000 * 60 * 60);
	}
	@Override
	public int workerThreadPriority() {		
		return rdrConfigurable.getWorkerThreadPriority();
	}	
	@Override
	public String redirectionIP() {		
		return null;
	}
	@Override
	public String getKey() {		
		return KEY;
	}
	@Override
	public String logLevel() {		
		return rdrConfigurable.getLogger().getLogLevel();
	}
	@Override
	public int logRollingType() {		
		return rdrConfigurable.getLogger().getLogRollingType();
	}
	@Override
	public int logRollingUnit() {		
		return rdrConfigurable.getLogger().getLogRollingUnit();
	}
	@Override
	public int logMaxRolledUnits() {		
		return rdrConfigurable.getLogger().getLogMaxRolledUnits();
	}	
	@Override
	public boolean isCompressLogRolledUnits() {	
		return rdrConfigurable.getLogger().getIsbCompressRolledUnit();
	}	
	@Override
	public List<PluginEntryDetail> getPrePluginList() {		
		return this.rdrConfigurable.getPluginsDetails().getPrePlugins().getPrePlugins();
	}
	@Override
	public List<PluginEntryDetail> getPostPluginList() {		
		return this.rdrConfigurable.getPluginsDetails().getPostPlugins().getPostPlugins();
	}
	
	@Override
	public String getLogLocation() {		
		return null;
	}
	
	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println();
		out.println("\tRDR Configurations");
		out.println("\tService-Address:            " + getSocketDetail());
		out.println("\tSocket Receive Buffer Size: " + socketReceiveBufferSize());
		out.println("\tSocket Send Buffer Size:    " + socketSendBufferSize());
		out.println("\tQueue Size:                 " + maxRequestQueueSize());
		out.println("\tMinimum Thread:             " +  minThreadPoolSize());
		out.println("\tMaximum Thread:             " + maxThreadPoolSize());
		out.println("\tMain Thread priority:       " + mainThreadPriority());
		out.println("\tWorker Thread priority:     " + workerThreadPriority());
		out.println("\tservice Level Redirection IP: " + redirectionIP());
		out.println("\tLogging Details: ");
		out.println("\t\tService Logger Enabled:" + isServiceLevelLoggerEnabled());
		out.println("\t\tLog-Level:           " + logLevel());
		out.println("\t\tRolling Type:        " + logRollingType());
		out.println("\t\tRolling Unit:        " + logRollingUnit());
		out.println("\t\tMax Rolled Unit:     " + logMaxRolledUnits());
		out.println("\t\tCompress Rolled Unit:" + isCompressLogRolledUnits());
		out.println("\t\tLog Location:" + getLogLocation());
		out.println("\tPlugin Details: ");
		out.println("\t\tPre-Plugin List:  "+ (getPrePluginList().size() > 0 ? getPrePluginList().toString(): "No Plugins"));
		out.println("\t\tPost-Plugin List: "+ (getPostPluginList().size() > 0 ? getPostPluginList().toString(): "No Plugins"));
		out.println();		
		return stringWriter.toString();
	}

	@Override
	public boolean isEligible(Class<? extends Configurable> configurableClass) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostReload
	public void postReloadProcessing(){
		
	}
	
	@PostRead
	public void postReadProcessing(){
		
	}

	@Override
	public List<RDRClientDataImpl> getRDRClients() {
		return this.rdrConfigurable.getRDRClients();
	}
	
	@Override
	public RDRClientDataImpl getRDRClient(InetAddress ip) {
		if(rdrConfigurable.getRDRClientMap()==null)
			return null;
		else {
			return this.rdrConfigurable.getRDRClientMap().get(ip);
		}
	}
	
}
