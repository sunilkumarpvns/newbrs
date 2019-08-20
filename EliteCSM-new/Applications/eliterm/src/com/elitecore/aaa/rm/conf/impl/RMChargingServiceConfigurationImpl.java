package com.elitecore.aaa.rm.conf.impl;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.aaa.rm.conf.RMChargingServiceConfiguration;
import com.elitecore.aaa.rm.policies.conf.RMChargingPolicyConfiguration;
import com.elitecore.aaa.rm.policies.conf.impl.RMChargingPolicyConfigurationImpl;
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
import com.elitecore.core.util.url.SocketDetail;

@ReadOrder(order = { "rmChargingConfigurable","rmChargingServicePolicyConfigurable"})

public class RMChargingServiceConfigurationImpl extends CompositeConfigurable implements RMChargingServiceConfiguration{

	private static final String MODULE = "RM-CHRGN";
	private static final String SERVICE_ID = "RM-CHRGN";
	private static final String KEY = "RM_CHGR_SER";
	private String logLocation;
	@Configuration private RMChargingServiceConfigurable rmChargingConfigurable;
	@Configuration private RMChargingServicePolicyConfigurable rmChargingServicePolicyConfigurable;

	private List<DriverConfiguration> driverConfigurations;

	public RMChargingServiceConfigurationImpl() {
		this.driverConfigurations = new LinkedList<DriverConfiguration>();
	}

	@Override
	public  String getKey(){
		return KEY;
	}

	@Override
	public List<PluginEntryDetail> getPostPluginList() {
		return rmChargingConfigurable.getPostPlugins();
	}

	@Override
	public List<PluginEntryDetail> getPrePluginList() {
		return rmChargingConfigurable.getPrePlugins();
	}

	@Override
	public boolean isCompressLogRolledUnits() {
		return rmChargingConfigurable.getLogger().getIsbCompressRolledUnit();
	}

	@Override
	public boolean isServiceLevelLoggerEnabled() {
		return rmChargingConfigurable.getLogger().getIsLoggerEnabled();
	}

	@Override
	public String logLevel() {
		return rmChargingConfigurable.getLogger().getLogLevel();
	}

	@Override
	public int logMaxRolledUnits() {
		return rmChargingConfigurable.getLogger().getLogMaxRolledUnits();
	}

	@Override
	public int logRollingType() {
		return rmChargingConfigurable.getLogger().getLogRollingType();
	}

	@Override
	public int logRollingUnit() {
		return rmChargingConfigurable.getLogger().getLogRollingUnit();
	}
	
	@Override
	public int mainThreadPriority() {
		return rmChargingConfigurable.getMainThreadPriority();
	}

	@Override
	public int maxRequestQueueSize() {
		return rmChargingConfigurable.getQueueSize();
	}

	@Override
	public int maxThreadPoolSize() {
		return rmChargingConfigurable.getMaximumThread();
	}

	@Override
	public int minThreadPoolSize() {
		return rmChargingConfigurable.getMinimumThread();
	}

	@Override
	public SysLogConfiguration getSysLogConfiguration() {
		return rmChargingConfigurable.getLogger().getSysLogConfiguration();
	}


	@Override
	public boolean isDuplicateRequestDetectionEnabled() {
		return rmChargingConfigurable.getIsDuplicateRequestCheckEnabled();
	}

	@Override
	public int getDupicateRequestQueuePurgeInterval() {
		return rmChargingConfigurable.getDuplicateRequestQueuePurgeInterval();
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
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(getKey(), "Invalid location : "+ location +" Reason: " + ex.getMessage() + ". Using default location : " + defaultLocation);
			}
			LogManager.getLogger().trace(ex);
			return defaultLocation;
		}
	}

	@Override
	public int socketReceiveBufferSize() {
		return this.rmChargingConfigurable.getSocketReceiveBufferSize();
	}

	@Override
	public int socketSendBufferSize() {
		return this.rmChargingConfigurable.getSocketSendBufferSize();
	}

	@Override
	public int threadKeepAliveTime() {
		return (1000 * 60 * 60);
	}

	@Override
	public int workerThreadPriority() {
		return this.rmChargingConfigurable.getWorkerThreadPriority();
	}



	@Override
	public String toString(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();
		out.println();
		out.println(" -- RM Charging Service Configuration -- ");
		out.println();
		out.println();
		out.println("    Service Address = " + getSocketDetails());
		out.println("    Socket Recevie Buffer Size = " + socketReceiveBufferSize());
		out.println("    Socket Send Buffer Size = " + socketSendBufferSize());
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
		out.println("    ");
		out.close();
		return stringBuffer.toString();
	}

	@Override
	public Map<String, RMChargingPolicyConfigurationImpl> getPolicyConfigrationMap() {
		return this.rmChargingServicePolicyConfigurable.getPolicyConfigrationMap();
	}

	@Override
	public RMChargingPolicyConfiguration getPolicyConfigration(String policyId) {
		return rmChargingServicePolicyConfigurable.getPolicyConfigration(policyId);
	}

	@PostRead
	public void postReadProcessing(){
		/**
		 * Post Processing is required after reading RM charging Service configuration.
		 */
		postReadProcessingForRmChargingServiceConfigurable();
	}


	private void postReadProcessingForRmChargingServiceConfigurable() {
		String serverHome = ((AAAConfigurationContext)getConfigurationContext()).getServerContext().getServerHome();
		if(rmChargingConfigurable.getLogger().getIsLoggerEnabled()){
			String defaultLogLocation = serverHome + File.separator + "logs";
			if(rmChargingConfigurable.getLogger().getLogLocation() != null && rmChargingConfigurable.getLogger().getLogLocation().trim().length() >0){
				String logLocation = getValidFileLocation(rmChargingConfigurable.getLogger().getLogLocation(),defaultLogLocation,serverHome);
				this.logLocation = logLocation + File.separator + "rm-charging";
			}else{
				this.logLocation = defaultLogLocation + File.separator + "rm-charging";
			}	
		}

	}

	@PostWrite
	public void postWriteProcessing(){
		// Do nothing
	}

	@PostReload
	public void postReloadProcessing(){
		// Do nothing
	}

	@Override
	@XmlTransient
	public String getServiceIdentifier() {
		return SERVICE_ID;
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
		return driverConfigurations;
	}

    @Override
	public List<SocketDetail> getSocketDetails() {
		return rmChargingConfigurable.getSocketDetails();
	}
}
