

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
import com.elitecore.aaa.rm.conf.RMIPPoolConfiguration;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.data.SysLogConfiguration;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.util.url.SocketDetail;


/**
 * 
 * @author Elitecore Technologies Ltd.
 * 
 */

@ReadOrder(order = { "rmipPoolConfigurable"})
public class RMIPPoolConfigurationImpl extends CompositeConfigurable implements RMIPPoolConfiguration{
	
	private static final String MODULE = "RM-IPPOOL-CONF-IMPL";
	@Configuration private RMIPPoolServiceConfigurable rmipPoolConfigurable;
	private String logLocation;

	public RMIPPoolConfigurationImpl() {
		
	}
	

	
	@PostRead
	public void postReadProcessing(){
		String serverHome  = ((AAAConfigurationContext)getConfigurationContext()).getServerContext().getServerHome();
		if(rmipPoolConfigurable.getLogger().getIsLoggerEnabled()){
			String defaultLogLocation = serverHome + File.separator + "logs";
			if(rmipPoolConfigurable.getLogger().getLogLocation() != null && rmipPoolConfigurable.getLogger().getLogLocation().trim().length() >0){
				 String logLocation = getValidFileLocation(rmipPoolConfigurable.getLogger().getLogLocation(),defaultLogLocation,serverHome);
				 this.logLocation = logLocation + File.separator + "rm-ippool";
			}else{
				this.logLocation = defaultLogLocation + File.separator + "rm-ippool";
			}	
		}
		

	}
	
	@Override
	public String getDataSourceName() {
		return rmipPoolConfigurable.getDataSourceName();
	}
	
	@Override
	public int getStatusCheckDuration() {
		return rmipPoolConfigurable.getDbScanTime();
	}
	
	@Override
	public List<PluginEntryDetail> getPostPluginList() {
		return rmipPoolConfigurable.getPostPlugins();
	}

	@Override
	public List<PluginEntryDetail> getPrePluginList() {
		return rmipPoolConfigurable.getPrePlugins();
	}

	@Override
	public boolean isCompressLogRolledUnits() {
		return rmipPoolConfigurable.getLogger().getIsbCompressRolledUnit();
	}

	@Override
	public boolean isServiceLevelLoggerEnabled() {
		return rmipPoolConfigurable.getLogger().getIsLoggerEnabled();
	}

	@Override
	public String logLevel() {
		return rmipPoolConfigurable.getLogger().getLogLevel();
	}

	@Override
	public int logMaxRolledUnits() {
		return rmipPoolConfigurable.getLogger().getLogMaxRolledUnits();
	}

	@Override
	public int logRollingType() {
		return rmipPoolConfigurable.getLogger().getLogRollingType();
	}

	@Override
	public int logRollingUnit() {
		return rmipPoolConfigurable.getLogger().getLogRollingUnit();
	}
	
	@Override
	public int mainThreadPriority() {
		return rmipPoolConfigurable.getMainThreadPriority();
	}

	@Override
	public int maxRequestQueueSize() {
		return rmipPoolConfigurable.getQueueSize();
	}

	@Override
	public int maxThreadPoolSize() {
		return rmipPoolConfigurable.getMaximumThread();
	}

	@Override
	public int minThreadPoolSize() {
		return rmipPoolConfigurable.getMinimumThread();
	}

	@Override
	public int socketReceiveBufferSize() {
		return rmipPoolConfigurable.getSocketReceiveBufferSize();
	}

	@Override
	public int socketSendBufferSize() {
		return rmipPoolConfigurable.getSocketSendBufferSize();
	}

	@Override
	public int threadKeepAliveTime() {
		return (1000 * 60 * 60);
	}

	@Override
	public int workerThreadPriority() {
		return rmipPoolConfigurable.getWorkerThreadPriority();
	}
	
			
	@Override
	public int getExecutionInterval() {
		return rmipPoolConfigurable.getAutoSessionCloserDetail().getExecutionInterval();
	}

	@Override
	public int getMaxBatchSize() {
		return rmipPoolConfigurable.getAutoSessionCloserDetail().getMaxBatchSize();
	}

	@Override
	public int getReservationTimeoutInterval() {
		return rmipPoolConfigurable.getAutoSessionCloserDetail().getReservationTimeoutInterval();
	}

	@Override
	public int getSessionTimeoutInterval() {
		return rmipPoolConfigurable.getAutoSessionCloserDetail().getSessionTimeoutInterval();
	}

	@Override
	public boolean isAutoSessionClosureEnabled() {
		return rmipPoolConfigurable.getAutoSessionCloserDetail().getIsEnabled();
	}

	
	public String toString () {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		
		
		out.println();
		out.println();
		out.println(" -- RM IPPOOL Service Configuration -- ");
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
		out.println("    auto-session-closure");
		out.println("        enabled = " + isAutoSessionClosureEnabled());
		out.println("        execution-interval = " + getExecutionInterval());
		out.println("        reservation-timeout-interval = " + getReservationTimeoutInterval());
		out.println("        session-timeout-interval = " + getSessionTimeoutInterval());
		out.println("        max-batch-size = " + getMaxBatchSize());
		out.println("        datasource-name = " + getDataSourceName());
		out.println("        operation-failure-enabled = " + isOperationFailureEnabled());
		out.println("        Default IP Pool Name  = " + getDefaultIPPoolName());
		out.println("        DB Query TimeOut  = " + getDbQueryTimeOut());
		out.println("    ");

		out.close();

		return stringBuffer.toString();
	}

	@Override
	public boolean isOperationFailureEnabled() {
		return rmipPoolConfigurable.getIsOperationFailureEnabled();
	}

	@Override
	public String getLogLocation() {
		return logLocation;
	}

	@Override
	public SysLogConfiguration getSysLogConfiguration() {
		return rmipPoolConfigurable.getLogger().getSysLogConfiguration();
	}

	@Override
	public String getDefaultIPPoolName() {
		return rmipPoolConfigurable.getDefaultIPPoolName();
	}

	@Override
	public int getDbQueryTimeOut() {
		return this.rmipPoolConfigurable.getDbQueryTimeOut();
	}

	@Override
	public boolean isDuplicateRequestDetectionEnabled() {
		return rmipPoolConfigurable.getIsDuplicateRequestCheckEnabled();
	}

	@Override
	public int getDupicateRequestQueuePurgeInterval() {
		return rmipPoolConfigurable.getDuplicateRequestQueuePurgeInterval();
	}


	@XmlTransient
	public void reloadConfiguration() throws LoadConfigurationException {
		
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
	public String getKey() {
		return "";
	}

	@Override
	public boolean isEligible(Class<? extends Configurable> configurableClass) {
		return false;
	}
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostReload
	public void postReloadProcessing(){
		
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
		return rmipPoolConfigurable.getRadiusSocketDataList();
	}
}
