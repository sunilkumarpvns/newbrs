package com.elitecore.aaa.rm.conf.impl;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.aaa.rm.conf.RMPrepaidChargingServiceConfiguration;
import com.elitecore.aaa.rm.drivers.conf.impl.RMRadClassicCSVAcctDriverConfImpl;
import com.elitecore.commons.base.Strings;
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
import com.elitecore.core.util.url.SocketDetail;

@ReadOrder(order = { "rmPrepaidChargingServiceConfigurable",
		"rmCrestelAttributeMappingDriverConfigurable","crestelAttributeMappingConfigurable",
"rmClassicCSVAcctConfigurable"})

public class RMPrepaidChargingServiceConfigImpl extends CompositeConfigurable implements RMPrepaidChargingServiceConfiguration{	

	private String MODULE = "RM_PREPAID_CHARGING_SERVICE";
	public static String PREPAID_FALLBACK_DRIVER = "PREPAID_FALLBACK_DRIVER";
	private Map crestelDriverConfig;

	private DriverConfiguration fallbackDriverConfig;
	private String driverInstanceId ;
	private String logLocation;
	@Configuration private RMPrepaidChargingServiceConfigurable rmPrepaidChargingServiceConfigurable;
	@Configuration private RmCrestelAttributeMappingDriverConfigurable rmCrestelAttributeMappingDriverConfigurable;
	@Configuration private RMCrestelAttributeMappingConfigurable crestelAttributeMappingConfigurable;
	@Configuration private RMPrepaidClassicCSVAcctConfigurable rmClassicCSVAcctConfigurable;

	private List<DriverConfiguration> driverConfigurations;

	public RMPrepaidChargingServiceConfigImpl() {
		driverConfigurations = new LinkedList<DriverConfiguration>();
		crestelDriverConfig = new HashMap();
	}

	@Override
	public DriverConfiguration getDriverConfiguration(String driverInstanceId) {
		return fallbackDriverConfig;
	}



	@Override
	public List<PluginEntryDetail> getPostPluginList() {
		return rmPrepaidChargingServiceConfigurable.getPostPlugins();
	}

	@Override
	public List<PluginEntryDetail> getPrePluginList() {
		return rmPrepaidChargingServiceConfigurable.getPrePlugins();
	}

	@Override
	public boolean isCompressLogRolledUnits() {
		return rmPrepaidChargingServiceConfigurable.getLogger().getIsbCompressRolledUnit();
	}

	@Override
	public boolean isServiceLevelLoggerEnabled() {
		return rmPrepaidChargingServiceConfigurable.getLogger().getIsLoggerEnabled();
	}

	@Override
	public String logLevel() {
		return rmPrepaidChargingServiceConfigurable.getLogger().getLogLevel();
	}

	@Override
	public int logMaxRolledUnits() {
		return rmPrepaidChargingServiceConfigurable.getLogger().getLogMaxRolledUnits();
	}

	@Override
	public int logRollingType() {
		return rmPrepaidChargingServiceConfigurable.getLogger().getLogRollingType();
	}

	@Override
	public int logRollingUnit() {
		return rmPrepaidChargingServiceConfigurable.getLogger().getLogRollingUnit();
	}
	
	@Override
	public int mainThreadPriority() {
		return rmPrepaidChargingServiceConfigurable.getMainThreadPriority();
	}

	@Override
	public int maxRequestQueueSize() {
		return rmPrepaidChargingServiceConfigurable.getQueueSize();
	}

	@Override
	public int maxThreadPoolSize() {
		return rmPrepaidChargingServiceConfigurable.getMaximumThread();
	}

	@Override
	public int minThreadPoolSize() {
		return rmPrepaidChargingServiceConfigurable.getMinimumThread();
	}

	@Override
	public int socketReceiveBufferSize() {
		return rmPrepaidChargingServiceConfigurable.getSocketReceiveBufferSize();
	}

	@Override
	public int socketSendBufferSize() {
		return rmPrepaidChargingServiceConfigurable.getSocketSendBufferSize();
	}

	@Override
	public int threadKeepAliveTime() {
		return (1000 * 60 * 60);
	}

	@Override
	public int workerThreadPriority() {
		return rmPrepaidChargingServiceConfigurable.getWorkerThreadPriority();
	}

	public String getStartupMode() {
		return rmPrepaidChargingServiceConfigurable.getStartupMode();
	}

	public String getKey(){
		return "RM_PREPAID_CHARGING_SERVICE";
	}


	@PostRead
	public void postReadProcessing(){
		String serverHome = ((AAAConfigurationContext)getConfigurationContext()).getServerContext().getServerHome();

		if(rmPrepaidChargingServiceConfigurable.getLogger().getIsLoggerEnabled()){
			String defaultLogLocation = serverHome + File.separator + "logs";
			if(rmPrepaidChargingServiceConfigurable.getLogger().getLogLocation() != null && rmPrepaidChargingServiceConfigurable.getLogger().getLogLocation().trim().length() >0){
				String logLocation = getValidFileLocation(rmPrepaidChargingServiceConfigurable.getLogger().getLogLocation(),defaultLogLocation,serverHome);
				this.logLocation = logLocation + File.separator + "rm-prepaid";
			}else {
				this.logLocation = defaultLogLocation + File.separator + "rm-prepaid";
			}	
		}

		/**
		 * Post Processing is required after reading driver configurataion
		 */
		try{
			Map rmCrestelCharging = rmCrestelAttributeMappingDriverConfigurable.getConfigMap();
			Map<String,Object> dccaMappingPolicyConfigMap = crestelAttributeMappingConfigurable.getConfigMap();
			if(dccaMappingPolicyConfigMap!=null){
				for(Map.Entry<String,Object> entry:  dccaMappingPolicyConfigMap.entrySet() ){
					rmCrestelCharging.put(entry.getKey(),entry.getValue());
				}
			}
			this.crestelDriverConfig = rmCrestelCharging;
		}catch(Exception e){

		}

		String fallbackDriverName = rmPrepaidChargingServiceConfigurable.getFallbackDriver();
		if(Strings.isNullOrBlank(fallbackDriverName) == false) {
			List<RMRadClassicCSVAcctDriverConfImpl> tempDriverList =  rmClassicCSVAcctConfigurable.getDriverConfigurationList();
			if(tempDriverList!=null){
				for(int i=0;i<tempDriverList.size();i++){
					this.fallbackDriverConfig = tempDriverList.get(i);
					this.driverInstanceId = fallbackDriverConfig.getDriverInstanceId();
				}
				this.driverConfigurations.addAll(tempDriverList);
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
	public Map getDriverConfig() {
		return this.crestelDriverConfig;
	}

	@Override
	public Collection<DriverConfiguration> getDriverConfigurations() {
		return driverConfigurations;
	}

	public String getDriverInstanceId(){
		return driverInstanceId;
	}


	@Override
	public String getLogLocation() {
		return logLocation;
	}

	@Override
	public SysLogConfiguration getSysLogConfiguration() {
		return this.rmPrepaidChargingServiceConfigurable.getLogger().getSysLogConfiguration();
	}

	@Override
	public boolean isDuplicateRequestDetectionEnabled() {
		return rmPrepaidChargingServiceConfigurable.getIsDuplicateRequestCheckEnabled();
	}

	@Override
	public int getDupicateRequestQueuePurgeInterval() {
		return rmPrepaidChargingServiceConfigurable.getDuplicateRequestQueuePurgeInterval();
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
	public boolean isEligible(Class<? extends com.elitecore.core.commons.config.core.Configurable> configurableClass) {
		return false;
	}

	@Override
	public List<SocketDetail> getSocketDetails() {
		return rmPrepaidChargingServiceConfigurable.getSocketDetails();
	}
}


