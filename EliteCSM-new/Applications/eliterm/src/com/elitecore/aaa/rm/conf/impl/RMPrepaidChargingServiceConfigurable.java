package com.elitecore.aaa.rm.conf.impl;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.config.PluginsDetail;
import com.elitecore.aaa.core.config.PostPluginsDetail;
import com.elitecore.aaa.core.config.PrePluginsDetail;
import com.elitecore.aaa.core.config.ServiceLoggerDetail;
import com.elitecore.aaa.radius.conf.impl.RadiusServiceConfigurable;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.Reloadable;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.util.url.MultiSocketURLParser;
import com.elitecore.core.util.url.SocketDetail;

@XmlType(propOrder = {})
@XmlRootElement(name = "prepaid-charging-service")
@ConfigurationProperties(moduleName ="RM_PREPAID_SERVICE_CONFIGURABLE",readWith = XMLReader.class, reloadWith = XMLReader.class, synchronizeKey ="RM_PREPAID_CHARGING_SERVICE")
@XMLProperties(name = "rm-prepaid-charging-service", schemaDirectories = {"system","schema"}, configDirectories = {"conf","services"})
public class RMPrepaidChargingServiceConfigurable extends RadiusServiceConfigurable {

	private static final String MODULE = "RM_PREPAID_SERVICE_CONFIGURABLE";
	private String serviceAddress = "0.0.0.0:1923";
	private int socketReceiveBufferSize = -1;
	private int socketSendBufferSize = -1;
	private int queueSize = 10000;
	private int minimumThread = 5;
	private int maximumThread = 10;
	private int mainThreadPriority = 7;
	private int workerThreadPriority = 7;
	private List<SocketDetail> socketDetails;
	private String startupMode = "Automatic";
	private List<PluginEntryDetail> prePlugins; 
	private List<PluginEntryDetail> postPlugins;	
	private String fallbackDriver;	
	private boolean duplicateRequestCheckEnabled;
	private int duplicateRequestQueuePurgeInterval = AAAServerConstants.DEFAULT_DUPLICATE_REQUEST_QUEUE_PURGE_INTERVAL;
	private PluginsDetail pluginsDetails;
	private ServiceLoggerDetail logger;

	public RMPrepaidChargingServiceConfigurable(){
		logger = new ServiceLoggerDetail();
		prePlugins = new ArrayList<PluginEntryDetail>();
		postPlugins =  new ArrayList<PluginEntryDetail>();	
		pluginsDetails = new PluginsDetail();
	}
	
	@XmlElement(name ="startup-mode",type =String.class,defaultValue ="Automatic")
	public String getStartupMode() {
		return startupMode;
	}

	public void setStartupMode(String startupMode) {
		this.startupMode = startupMode;
	}

	@XmlElement(name ="duplicate-request-check-enabled",type =boolean.class)
	public boolean getIsDuplicateRequestCheckEnabled() {
		return duplicateRequestCheckEnabled;
	}

	public void setIsDuplicateRequestCheckEnabled(boolean duplicateRequestCheckEnabled) {
		this.duplicateRequestCheckEnabled = duplicateRequestCheckEnabled;
	}

	@XmlElement(name ="duplicate-request-purge-interval",type =int.class,defaultValue ="15")
	public int getDuplicateRequestQueuePurgeInterval() {
		return duplicateRequestQueuePurgeInterval;
	}

	public void setDuplicateRequestQueuePurgeInterval(
			int duplicateRequestQueuePurgeInterval) {
		this.duplicateRequestQueuePurgeInterval = duplicateRequestQueuePurgeInterval;
	}
	
	@XmlElement(name ="prepaid-fallback-driver",type = String.class)
	public String getFallbackDriver() {
		return fallbackDriver;
	}

	public void setFallbackDriver(String fallbackDriver) {
		this.fallbackDriver = fallbackDriver;
	}
	
	@XmlElement(name ="plugin-list")
	public PluginsDetail getPluginsDetails() {
		return pluginsDetails;
	}

	public void setPluginsDetails(PluginsDetail pluginsDetails) {
		this.pluginsDetails = pluginsDetails;
	}


	@XmlTransient
	public List<PluginEntryDetail> getPrePlugins() {
		return prePlugins;
	}
	public void setPrePlugins(List<PluginEntryDetail> prePlugins) {
		this.prePlugins = prePlugins;
	}
	@XmlTransient
	public List<PluginEntryDetail> getPostPlugins() {
		return postPlugins;
	}
	public void setPostPlugins(List<PluginEntryDetail> postPlugins) {
		this.postPlugins = postPlugins;
	}
	
	@XmlTransient
	public List<SocketDetail> getSocketDetails() {
		return this.socketDetails;
	}
	public void setSocketDetails(List<SocketDetail> socketDetails) {
		this.socketDetails = socketDetails;
	}
	
	@Reloadable(type=ServiceLoggerDetail.class)
	@XmlElement(name = "logging")
	public ServiceLoggerDetail getLogger() {
		return logger;
	}
	public void setLogger(ServiceLoggerDetail logger) {
		this.logger = logger;
	}


	@XmlElement(name = "service-address",type = String.class,defaultValue ="0.0.0.0:1923")
	public String getServiceAddress() {
		return serviceAddress;
	}
	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}
	@XmlElement(name = "socket-receive-buffer-size",type = int.class,defaultValue ="-1")
	public int getSocketReceiveBufferSize() {
		return socketReceiveBufferSize;
	}
	public void setSocketReceiveBufferSize(int socketReceiveBufferSize) {
		this.socketReceiveBufferSize = socketReceiveBufferSize;
	}
	@XmlElement(name = "socket-send-buffer-size",type = int.class,defaultValue ="-1")
	public int getSocketSendBufferSize() {
		return socketSendBufferSize;
	}
	public void setSocketSendBufferSize(int socketSendBufferSize) {
		this.socketSendBufferSize = socketSendBufferSize;
	}
	@XmlElement(name = "queue-size",type = int.class,defaultValue ="10000")
	public int getQueueSize() {
		return queueSize;
	}
	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}
	@XmlElement(name = "minimum-thread",type = int.class,defaultValue ="5")
	public int getMinimumThread() {
		return minimumThread;
	}
	public void setMinimumThread(int minimumThread) {
		this.minimumThread = minimumThread;
	}
	@XmlElement(name = "maximum-thread",type = int.class,defaultValue="10")
	public int getMaximumThread() {
		return maximumThread;
	}
	public void setMaximumThread(int maximumThread) {
		this.maximumThread = maximumThread;
	}
	@XmlElement(name = "main-thread-priority",type = int.class,defaultValue ="7")
	public int getMainThreadPriority() {
		return mainThreadPriority;
	}
	public void setMainThreadPriority(int mainThreadPriority) {
		this.mainThreadPriority = mainThreadPriority;
	}
	@XmlElement(name = "worker-thread-priority",type = int.class,defaultValue ="7")
	public int getWorkerThreadPriority() {
		return workerThreadPriority;
	}
	public void setWorkerThreadPriority(int workerThreadPriority) {
		this.workerThreadPriority = workerThreadPriority;
	}

	
	@PostRead
	public void postReadProcessing() throws MalformedURLException {
		super.postReadProcessing();
		validateDuplicateRequestPurgeInterval();
		
		this.socketDetails = MultiSocketURLParser.parse(serviceAddress);
		
		PrePluginsDetail prePluginObj = getPluginsDetails().getPrePlugins();
		if(prePluginObj!=null){
			List<PluginEntryDetail> prePluginList = prePluginObj.getPrePlugins();
			if(prePluginList!=null){
				this.prePlugins = prePluginList;
			}
		}
		
		PostPluginsDetail postPluginObj = getPluginsDetails().getPostPlugins();
		if(postPluginObj!=null){
			List<PluginEntryDetail> postPluginList = postPluginObj.getPostPlugins(); 
			if(postPluginList!=null){
				this.postPlugins = postPluginList;
			}
		}
	}
	
	@PostWrite
	public void postWriteProcessing(){
		super.postWriteProcessing();
	}
	
	@PostReload
	public void postReloadProcessing(){
		super.postReloadProcessing();
	}


	@Override
	@XmlTransient
	public String getModuleName() {
		return MODULE;
	}
}
