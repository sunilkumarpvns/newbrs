package com.elitecore.aaa.radius.conf.impl;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.conf.RadDynAuthConfiguration;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.DynAuthServicePolicyConfiguration;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.impl.DynAuthServicePolicyConfigurable;
import com.elitecore.aaa.util.constants.AAAServerConstants;
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
import com.elitecore.core.util.url.SocketDetail;

@ReadOrder(order = { "radDynAuthServiceConfigurable"
					,"dynAuthServicePolicyConfigurable"})
public class RadDynAuthConfigurationImpl extends CompositeConfigurable implements RadDynAuthConfiguration{
	
	private static final String MODULE = "RAD-DYNAUTH-CONF-IMPL";
	private final static String KEY = "RAD_DYNAUTH";
	
	@Configuration private RadDynAuthServiceConfigurable radDynAuthServiceConfigurable;
	@Configuration private DynAuthServicePolicyConfigurable dynAuthServicePolicyConfigurable;
	 
	private String logLocation;
	
	@Deprecated
	public RadDynAuthConfigurationImpl(ServerContext serverContext) {
		this.radDynAuthServiceConfigurable = new RadDynAuthServiceConfigurable();
	}
	
	
	public RadDynAuthConfigurationImpl() {
		
	}
	
	
	@PostRead
	public void postReadProcessing() {
		AAAServerContext serverContext = ((AAAConfigurationContext)getConfigurationContext()).getServerContext();
		
		//post read processing for service configurable
		postReadProcessingForServiceConfigurable(serverContext);
		
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostReload
	public void postReloadProcessing(){
		
	}
	
	private void postReadProcessingForServiceConfigurable(AAAServerContext serverContext){
		postReadForServiceLevelLogger(serverContext);
		validateQueueSize();
	}

	private void validateQueueSize() {
		if (radDynAuthServiceConfigurable.getQueueSize() <= 0) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Configured queue size: " + radDynAuthServiceConfigurable.getQueueSize() 
						+ " is invalid. Using default queue size: " + AAAServerConstants.DEFAULT_QUEUE_SIZE);
			}
			radDynAuthServiceConfigurable.setQueueSize(AAAServerConstants.DEFAULT_QUEUE_SIZE);
			return;
		} else if (radDynAuthServiceConfigurable.getQueueSize() > 50000) {
		if (LogManager.getLogger().isWarnLogLevel()) {
			LogManager.getLogger().warn(MODULE, "Configured queue size: " + radDynAuthServiceConfigurable.getQueueSize() 
						+ " is greater than maximum queue size: " + AAAServerConstants.MAX_QUEUE_SIZE + 
						", so using " + AAAServerConstants.MAX_QUEUE_SIZE + " as max queue size.");
		}
			radDynAuthServiceConfigurable.setQueueSize(AAAServerConstants.MAX_QUEUE_SIZE);
			return;
	}
	}

	private void postReadForServiceLevelLogger(AAAServerContext serverContext) {
		if(radDynAuthServiceConfigurable.getLogger().getIsLoggerEnabled()){
			String defaultLogLocation = serverContext.getServerHome() + File.separator + "logs";
			if(radDynAuthServiceConfigurable.getLogger().getLogLocation() != null && radDynAuthServiceConfigurable.getLogger().getLogLocation().trim().length() >0){
				 String logLocation = getValidFileLocation(radDynAuthServiceConfigurable.getLogger().getLogLocation(),defaultLogLocation,serverContext);
				 this.logLocation = logLocation + File.separator + "rad-dynauth";
			}else{
				this.logLocation = defaultLogLocation + File.separator + "rad-dynauth";
			}	
		}
	}
	
	private String getValidFileLocation(String location,String defaultLocation,AAAServerContext serverContext){
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
				location = serverContext.getServerHome() + File.separator + location;
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
	public int mainThreadPriority() {
		return this.radDynAuthServiceConfigurable.getMainThreadPriority();
	}

	@Override
	public int maxRequestQueueSize() {
		return this.radDynAuthServiceConfigurable.getQueueSize();
	}

	@Override
	public int maxThreadPoolSize() {
		return this.radDynAuthServiceConfigurable.getMaximumThread();
	}

	@Override
	public int minThreadPoolSize() {
		return this.radDynAuthServiceConfigurable.getMinimumThread();
	}

	@Override
	public int socketReceiveBufferSize() {
		return this.radDynAuthServiceConfigurable.getSocketReceiveBufferSize();
	}

	@Override
	public int socketSendBufferSize() {
		return this.radDynAuthServiceConfigurable.getSocketSendBufferSize();
	}

	@Override
	public int workerThreadPriority() {
		return this.radDynAuthServiceConfigurable.getWorkerThreadPriority();
	}
	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public int threadKeepAliveTime() {
		return (1000 * 60 * 60);
	}

	@Override
	public boolean isCompressLogRolledUnits() {
		return this.radDynAuthServiceConfigurable.getLogger().getIsbCompressRolledUnit();
	}

	@Override
	public boolean isServiceLevelLoggerEnabled() {
		return this.radDynAuthServiceConfigurable.getLogger().getIsLoggerEnabled();
	}

	@Override
	public String logLevel() {
		return this.radDynAuthServiceConfigurable.getLogger().getLogLevel();
	}

	@Override
	public int logMaxRolledUnits() {
		return this.radDynAuthServiceConfigurable.getLogger().getLogMaxRolledUnits();
	}

	@Override
	public int logRollingType() {
		return this.radDynAuthServiceConfigurable.getLogger().getLogRollingType();
	}

	@Override
	public int logRollingUnit() {
		return this.radDynAuthServiceConfigurable.getLogger().getLogRollingUnit();
	}

	@Override
	public DynAuthServicePolicyConfiguration getDynAuthServicePolicyConfiguraion(
			String policyId) {
		return this.dynAuthServicePolicyConfigurable.getDynAuthServicePolicyConfiguration(policyId);
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();
		out.println();
		out.println(" -- RAD Dynauth Service Configuration -- ");
		out.println();
		out.println("    Service Address = " + getSocketDetails());
		out.println("    Socket Recevie Buffer Size = " + socketReceiveBufferSize());
		out.println("    Socket Send Buffer Size = " + socketSendBufferSize());
		out.println("    Duplicate Request Check Enabled = " + isDuplicateRequestDetectionEnabled());
		out.println("    Duplicate Request Check Queue Purge Interval = " + getDupicateRequestQueuePurgeInterval());
		out.println("    Threads");
		out.println("      Minimum Pool Size = " + minThreadPoolSize());
		out.println("      Maximum Pool Size = " + maxThreadPoolSize());
		out.println("      Queue Size = " + maxRequestQueueSize());
		out.println("      Worker Priority = " + workerThreadPriority() );
		out.println("      Main Thread Priority = " + mainThreadPriority() );
		out.println("    Logging"); 
		out.println("        Level = " + logLevel()); //TODO also concatenate the string form of level value
		out.println("        Rolling Type = " + logRollingType());
		out.println("        Rolling Unit = " + logRollingUnit());
		out.println("        Max Rolled Units = " + logMaxRolledUnits());
		out.println("        Compress Rolled Unit = " + isCompressLogRolledUnits());
		out.println("        Log File Location = " + getLogLocation());
		out.println(this.radDynAuthServiceConfigurable.getExternalDataBaseDetail());
		out.println("    Pre Plugins : " + getPrePluginList());
		out.println("    Post Plugins: " + getPostPluginList());
		if(getExternalDataBaseDetail()!=null)
			out.println("    "+getExternalDataBaseDetail());
		out.println("    ");

		out.close();
		return stringBuffer.toString();

	}
	@Override
	public DynAuthExternalSystemDetail getExternalDataBaseDetail() {
		return this.radDynAuthServiceConfigurable.getExternalDataBaseDetail();
	}

	@Override
	public List<PluginEntryDetail> getPostPluginList() {
		return this.radDynAuthServiceConfigurable.getPostPlugins();
	}

	@Override
	public List<PluginEntryDetail> getPrePluginList() {
		return this.radDynAuthServiceConfigurable.getPrePlugins();
	}

	@Override
	public String getLogLocation() {
		return logLocation;
	}

	@Override
	public SysLogConfiguration getSysLogConfiguration() {
		return this.radDynAuthServiceConfigurable.getLogger().getSysLogConfiguration();
	}

	@Override
	public boolean isDuplicateRequestDetectionEnabled() {
		return this.radDynAuthServiceConfigurable.getIsDuplicateRequestCheckEnabled();
	}
	
	@Override
	public int getDupicateRequestQueuePurgeInterval() {
		return this.radDynAuthServiceConfigurable.getDuplicateRequestQueuePurgeInterval();
	}

	@Override
	public boolean isEligible(Class<? extends com.elitecore.core.commons.config.core.Configurable> configurableClass) {
		return false;
	}


	@Override
	public Map<String, DynAuthServicePolicyConfiguration> getDynAuthServicePolicyConfiguraionMap() {
		return this.dynAuthServicePolicyConfigurable.getDynAuthServicePolicyConfigurationMap();
	}


	@Override
	public List<SocketDetail> getSocketDetails() {
		return radDynAuthServiceConfigurable.getSocketDetails();
	}
}
