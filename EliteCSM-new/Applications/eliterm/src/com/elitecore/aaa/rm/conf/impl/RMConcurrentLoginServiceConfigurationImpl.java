package com.elitecore.aaa.rm.conf.impl;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.aaa.radius.sessionx.conf.SessionManagerData;
import com.elitecore.aaa.radius.sessionx.conf.impl.SessionManagerConfigurationImpl;
import com.elitecore.aaa.rm.conf.RMConcurrentLoginServiceConfiguration;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;
import com.elitecore.core.commons.data.SysLogConfiguration;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.util.url.SocketDetail;

@ReadOrder(order = { "rmConcurrentServiceConfigurable"})

public class RMConcurrentLoginServiceConfigurationImpl extends CompositeConfigurable implements RMConcurrentLoginServiceConfiguration{
	
	private static final String MODULE = "CONCURRENT_LOGIN_SERVICE";	
	
	private String logLocation;
	@Configuration private RMConcurrentServiceConfigurable rmConcurrentServiceConfigurable;
	private String smInstanceId;
	
	@Deprecated
	public RMConcurrentLoginServiceConfigurationImpl(ServerContext serverContext) {

		this.rmConcurrentServiceConfigurable = new RMConcurrentServiceConfigurable();
	}
	
	public RMConcurrentLoginServiceConfigurationImpl() {

		
	}


	
	@PostRead
	public void postReadProcessing(){
		String serverHome = ((AAAConfigurationContext)getConfigurationContext()).getServerContext().getServerHome();
		if(rmConcurrentServiceConfigurable.getLogger().getIsLoggerEnabled()){
			String defaultLogLocation = serverHome + File.separator + "logs";
			if(rmConcurrentServiceConfigurable.getLogger().getLogLocation() != null && rmConcurrentServiceConfigurable.getLogger().getLogLocation().trim().length() >0){
				 String logLocation = getValidFileLocation(rmConcurrentServiceConfigurable.getLogger().getLogLocation(),defaultLogLocation,serverHome);
				 this.logLocation = logLocation + File.separator + "rm-concurrent";
			}else{
				this.logLocation = defaultLogLocation + File.separator + "rm-concurrent";
			}	
		}
		
		
		String smName = rmConcurrentServiceConfigurable.getSessionManagerName();
		if(smName!=null){
			SessionManagerData sessionManagerData =	getConfigurationContext().get(SessionManagerConfigurationImpl.class).getSessionManagerConfig(smName);
			if(sessionManagerData!=null){
				smInstanceId = sessionManagerData.getInstanceId();
			}
		}
	}
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostReload
	public void postReloadProcessing(){
		
	}

	@Override
	public String getKey() {
		return "";
	}

	@Override
	public List<PluginEntryDetail> getPostPluginList() {
		return rmConcurrentServiceConfigurable.getPostPlugins();
	}

	@Override
	public List<PluginEntryDetail> getPrePluginList() {
		return rmConcurrentServiceConfigurable.getPrePlugins();
	}

	@Override
	public boolean isCompressLogRolledUnits() {
		return rmConcurrentServiceConfigurable.getLogger().getIsbCompressRolledUnit();
	}

	@Override
	public boolean isServiceLevelLoggerEnabled() {
		return rmConcurrentServiceConfigurable.getLogger().getIsLoggerEnabled();
	}

	@Override
	public String logLevel() {
		return rmConcurrentServiceConfigurable.getLogger().getLogLevel();
	}

	@Override
	public int logMaxRolledUnits() {
		return rmConcurrentServiceConfigurable.getLogger().getLogMaxRolledUnits();
	}

	@Override
	public int logRollingType() {
		return rmConcurrentServiceConfigurable.getLogger().getLogRollingType();
	}

	@Override
	public int logRollingUnit() {
		return rmConcurrentServiceConfigurable.getLogger().getLogRollingUnit();
	}

	@Override
	public int mainThreadPriority() {
		return rmConcurrentServiceConfigurable.getMainThreadPriority();
	}

	@Override
	public int maxRequestQueueSize() {
		return rmConcurrentServiceConfigurable.getQueueSize();
	}

	@Override
	public int maxThreadPoolSize() {
		return rmConcurrentServiceConfigurable.getMaximumThread();
	}

	@Override
	public int minThreadPoolSize() {
		return rmConcurrentServiceConfigurable.getMinimumThread();
	}

	@Override
	public SysLogConfiguration getSysLogConfiguration() {
		return rmConcurrentServiceConfigurable.getLogger().getSysLogConfiguration();
	}


	@Override
	public boolean isDuplicateRequestDetectionEnabled() {
		return rmConcurrentServiceConfigurable.getIsDuplicateRequestCheckEnabled();
	}

	@Override
	public int getDupicateRequestQueuePurgeInterval() {
		return rmConcurrentServiceConfigurable.getDuplicateRequestQueuePurgeInterval();
	}

	public String getStartupMode() {
		return rmConcurrentServiceConfigurable.getStartupMode();
	}

	@Override
	public String getSessionManagerName() {
		return this.rmConcurrentServiceConfigurable.getSessionManagerName();
	}

	@Override
	public String getLogLocation() {
		return logLocation;
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


	@Override
	public int socketReceiveBufferSize() {
		return this.rmConcurrentServiceConfigurable.getSocketReceiveBufferSize();
	}

	@Override
	public int socketSendBufferSize() {
		return this.rmConcurrentServiceConfigurable.getSocketSendBufferSize();
	}

	@Override
	public int threadKeepAliveTime() {
		return (1000 * 60 * 60);
	}

	@Override
	public int workerThreadPriority() {
		return this.rmConcurrentServiceConfigurable.getWorkerThreadPriority();
	}

	@Override
	public String getSessionManagerInstanceId() {
		return smInstanceId;
	}

	@Override
	public String toString () {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		
		
		out.println();
		out.println();
		out.println(" -- RM Concurrent Login Service Configuration -- ");
		out.println();
		out.println("    Service Address = " + getSocketDetails());
		out.println("    Socket Recevie Buffer Size = " + socketReceiveBufferSize());
		out.println("    Socket Send Buffer Size = " + socketSendBufferSize());
		out.println("    Startup Mode = " + getStartupMode());
		out.println("    Threads");
		out.println("      Minimum Pool Size = " + minThreadPoolSize());
		out.println("      Maximum Pool Size = " + maxThreadPoolSize());
		out.println("      Queue Size = " + maxRequestQueueSize());
		out.println("      Worker Priority = " + workerThreadPriority());
		out.println("      Main Thread Priority = " + mainThreadPriority());
		out.println("    Duplicate Request Check Enabled = " + isDuplicateRequestDetectionEnabled());
		out.println("    Duplicate Request Check Queue Purge Interval = " + getDupicateRequestQueuePurgeInterval());
		out.println("    Logging"); 
		out.println("        Service Logger Enabled  = " + isServiceLevelLoggerEnabled());
		out.println("        Level = " + logLevel());
		out.println("        Rolling Type = " + logRollingType());
		out.println("        Rolling Unit = " + logRollingUnit());
		out.println("        Max Rolled Units = " + logMaxRolledUnits());
		out.println("        Compress Rolled Unit = " + isCompressLogRolledUnits());
		out.println("        Log Location = " + logLocation);
		out.println("    SessionManager = " + getSessionManagerName());
		out.println("    ");

		out.close();

		return stringBuffer.toString();
	}

	@Override
	public boolean isEligible(Class<? extends Configurable> configurableClass) {
		return false;
	}

	@Override
	public DriverConfiguration getDriverConfiguration(String driverInstanceId) {
		return null;
	}

	@XmlTransient
	@Override
	public Collection<DriverConfiguration> getDriverConfigurations() {
		return Collections.emptyList();
	}

	@Override
	public List<SocketDetail> getSocketDetails() {
		return rmConcurrentServiceConfigurable.getSocketDetails();
	}
}
