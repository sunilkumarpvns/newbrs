/**
 * 
 */
package com.elitecore.aaa.rm.conf.impl;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.List;

import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.rm.conf.GTPPrimeConfiguration;
import com.elitecore.aaa.rm.service.gtpprime.data.GTPPrimeClientDataImpl;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;
import com.elitecore.core.commons.data.SysLogConfiguration;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.util.mbean.data.config.EliteNetConfigurationData;
import com.elitecore.core.util.url.SocketDetail;

/**
 * @author dhaval.jobanputra
 * 
 */
@ReadOrder(order = { "gtpPrimeConfigurable"})

public class GTPPrimeConfigurationImpl extends CompositeConfigurable implements GTPPrimeConfiguration{

	private final static String KEY = "GTP_PRIME_SER";
	
	private static final String MODULE = "GTP-PRIME-CONF-IMPL";
	
	private String serverHome;
	private String logLocation;
	@Configuration private GTPPrimeConfigurable gtpPrimeConfigurable;

	@Deprecated
	public GTPPrimeConfigurationImpl(ServerContext serverContext) {
		
		logLocation = serverHome + File.separator + "logs" + File.separator +"rm-gtp-prime";
	}
	
	public GTPPrimeConfigurationImpl() {
		
		logLocation = serverHome + File.separator + "logs" + File.separator +"rm-gtp-prime";
	}
	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public List<PluginEntryDetail> getPostPluginList() {
		return gtpPrimeConfigurable.getPostPlugins();
	}

	@Override
	public List<PluginEntryDetail> getPrePluginList() {
		return gtpPrimeConfigurable.getPrePlugins();
	}

	@Override
	public boolean isCompressLogRolledUnits() {
		return gtpPrimeConfigurable.getLogger().getIsbCompressRolledUnit();
	}

	@Override
	public boolean isServiceLevelLoggerEnabled() {
		return gtpPrimeConfigurable.getLogger().getIsLoggerEnabled();
	}

	@Override
	public String logLevel() {
		return gtpPrimeConfigurable.getLogger().getLogLevel();
	}

	@Override
	public int logMaxRolledUnits() {
		return gtpPrimeConfigurable.getLogger().getLogMaxRolledUnits();
	}

	@Override
	public int logRollingType() {
		return gtpPrimeConfigurable.getLogger().getLogRollingType();
	}

	@Override
	public int logRollingUnit() {
		return gtpPrimeConfigurable.getLogger().getLogRollingUnit();
	}

	@Override
	public int mainThreadPriority() {
		return gtpPrimeConfigurable.getMainThreadPriority();
	}

	@Override
	public int maxRequestQueueSize() {
		return gtpPrimeConfigurable.getQueueSize();
	}

	@Override
	public int maxThreadPoolSize() {
		return gtpPrimeConfigurable.getMaximumThread();
	}

	@Override
	public int minThreadPoolSize() {
		return gtpPrimeConfigurable.getMinimumThread();
	}

	@Override
	public int socketReceiveBufferSize() {
		return gtpPrimeConfigurable.getSocketReceiveBufferSize();
	}

	@Override
	public int socketSendBufferSize() {
		return gtpPrimeConfigurable.getSocketSendBufferSize();
	}

	@Override
	public int threadKeepAliveTime() {
		return (1000 * 60 * 60);
	}

	@Override
	public int workerThreadPriority() {
		return gtpPrimeConfigurable.getWorkerThreadPriority();
	}

	@Override
	public String redirectionIP() {
		return gtpPrimeConfigurable.getRedirectionIP();
	}
	
	@Override
	public long getIdleCommunicationTimeInterval(){
		return gtpPrimeConfigurable.getIdleCommunicationTimeInterval();
	}
	
	@PostRead
	public void postReadProcessing(){
		
		if(gtpPrimeConfigurable.getLogger().getIsLoggerEnabled()){
			String serverHome = ((AAAConfigurationContext)getConfigurationContext()).getServerContext().getServerHome();
			String defaultLogLocation = serverHome + File.separator + "logs";
			if(gtpPrimeConfigurable.getLogger().getLogLocation() != null && gtpPrimeConfigurable.getLogger().getLogLocation().trim().length() >0){
				 String logLocation = getValidFileLocation(gtpPrimeConfigurable.getLogger().getLogLocation(),defaultLogLocation,serverHome);
				 this.logLocation = logLocation + File.separator + "rm-gtp-prime";
			}else{
				this.logLocation = defaultLogLocation + File.separator + "rm-gtp-prime";
			}	
		}
		

	}
	private String getValidFileLocation(String location,String defaultLocation,String serverHome){
		if(location == null || location.trim().length() == 0)
			return defaultLocation;
		
		File file = new File(location);
		try{
			if(file.isAbsolute()){
				if(file.exists()){
					if(file.canWrite()){
						return location;
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
							LogManager.getLogger().warn(MODULE, "No write access at location : " + location + ". Using default location : " + defaultLocation);
						}
					}
				}else{
					if(file.mkdirs())
						return location;
					
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Cannot create directory at location : " + location);
					}
				}
			}else{
				//in case of relative path like ../../../../../.. it will make it absolute and check whether access is available
				location = serverHome + File.separator + location;
				file = new File(location);
				if(file.exists()){
					if(file.isDirectory() && file.canWrite()){
						return location;
					}
				}else{
					if(file.mkdirs())
						return location;
					
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Cannot create directory at location : " + location);
					}
				}
			}
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Invalid location : " + location + ". Using default location : " + defaultLocation);
			}
			return defaultLocation;
		}catch(SecurityException ex){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(getKey(), "Invalid location : "+ location +" Reason: " + ex.getMessage() + ". Using default location : " + defaultLocation);
			return defaultLocation;
		}
	}
	
	
	public EliteNetConfigurationData getNetConfigurationData() {
		return null;}

	public int stringToInteger(String originalString, int defaultValue) {
		int resultValue = defaultValue;
		try {
			resultValue = Integer.parseInt(originalString);
		} catch (Exception e) {
		}
		return resultValue;
	}

	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println();
		out.println("\tGTP' Configurations");
		out.println("\tService-Address:            " + getSocketDetails());
		out.println("\tSocket Receive Buffer Size: " + socketReceiveBufferSize());
		out.println("\tSocket Send Buffer Size:    " + socketSendBufferSize());
		out.println("\tQueue Size:                 " + maxRequestQueueSize());
		out.println("\tMinimum Thread:             " + minThreadPoolSize());
		out.println("\tMaximum Thread:             " + maxThreadPoolSize());
		out.println("\tMain Thread priority:       " + mainThreadPriority());
		out.println("\tWorker Thread priority:     " + workerThreadPriority());
		out.println("\tService Level Redirection IP: " + redirectionIP());
		out.println("\tService Idle Communication time interval: " + getIdleCommunicationTimeInterval());
		out.println("\tLogging Details: ");
		out.println("\t\tService Logger Enabled:" + isServiceLevelLoggerEnabled());
		out.println("\t\tLog-Level:           " + logLevel());
		out.println("\t\tRolling Type:        " + logRollingType());
		out.println("\t\tRolling Unit:        " + logRollingUnit());
		out.println("\t\tMax Rolled Unit:     " + logMaxRolledUnits());
		out.println("\t\tCompress Rolled Unit:" + isCompressLogRolledUnits());
		out.println("\t\tLog Location:" + getLogLocation());
		out.println("\tPlugin Details: ");
		out.print("\t\tPre-Plugin List: ");
		for (int i=0 ;i<getPrePluginList().size() ; i++){
			out.print(getPrePluginList() + "  " );
		}
		out.println();
		out.print("\t\tPost-Plugin List: ");
		for (int i=0 ;i<getPostPluginList().size() ; i++){
			out.println(getPostPluginList() + "  " );
		}
		out.println();
		for(int i=0;i<getClientList().size();i++){
			out.println(getClientList().get(i).toString());
		}
		return stringWriter.toString();
	}

	@Override
	public String getLogLocation() {
		return logLocation;
	}

	@Override
	public SysLogConfiguration getSysLogConfiguration() {
		return gtpPrimeConfigurable.getLogger().getSysLogConfiguration();
	}

	@Override
	public List<GTPPrimeClientDataImpl> getClientList() {
		return gtpPrimeConfigurable.getClientDetail().getClientList();
	}

	@Override
	public GTPPrimeClientDataImpl getClient(InetAddress addr) {
		return gtpPrimeConfigurable.getAddrClientMap().get(addr);
	}

	@Override
	public boolean isEligible(
			Class<? extends com.elitecore.core.commons.config.core.Configurable> configurableClass) {
		// TODO Auto-generated method stub
		return false;
	}
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostReload
	public void postReloadProcessing(){
		
	}

	@Override
	public List<SocketDetail> getSocketDetails() {
		return gtpPrimeConfigurable.getSocketDetails();
	}

	@Override
	public boolean isDuplicateRequestDetectionEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getDupicateRequestQueuePurgeInterval() {
		// TODO Auto-generated method stub
		return 0;
	}

}
