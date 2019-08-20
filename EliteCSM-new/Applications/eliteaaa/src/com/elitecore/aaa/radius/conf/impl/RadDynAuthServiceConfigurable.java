package com.elitecore.aaa.radius.conf.impl;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.config.PluginsDetail;
import com.elitecore.aaa.core.config.PostPluginsDetail;
import com.elitecore.aaa.core.config.PrePluginsDetail;
import com.elitecore.aaa.core.config.ServiceLoggerDetail;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.Reloadable;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.commons.plugins.data.PluginMode;
import com.elitecore.core.commons.plugins.data.ServiceTypeConstants;
import com.elitecore.core.util.url.MultiSocketURLParser;
import com.elitecore.core.util.url.SocketDetail;

@XmlType(propOrder = {})
@XmlRootElement(name = "dynauth-service")
@ConfigurationProperties(moduleName ="RAD_DYNA_AUTH_CONFIGURABLE",synchronizeKey ="RAD_DYNAUTH", readWith = XMLReader.class, reloadWith = XMLReader.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf","services"},name = "rad-dynauth")
public class RadDynAuthServiceConfigurable extends RadiusServiceConfigurable {
	
	private static final String MODULE = "RAD_DYNA_AUTH_CONFIGURABLE";
	private int socketReceiveBufferSize = -1;
	private int socketSendBufferSize = -1;
	private int queueSize;
	private List<SocketDetail> socketDetails;
	private String serviceAddress = "0.0.0.0:3799";
	private List<PluginEntryDetail> prePlugins; 
	private List<PluginEntryDetail> postPlugins;	
	private PluginsDetail pluginsDetails;
	private ServiceLoggerDetail logger;
	private int minimumThread = 5;
	private int maximumThread = 10;
	private int mainThreadPriority = 7;
	private int workerThreadPriority = 7;
	private boolean duplicateRequestCheckEnabled;
	private int duplicateRequestQueuePurgeInterval = AAAServerConstants.DEFAULT_DUPLICATE_REQUEST_QUEUE_PURGE_INTERVAL;
	private DynAuthExternalSystemDetail externalDataBaseDetail;
	private List<String> servicePolicies;
	
	public RadDynAuthServiceConfigurable(){
		logger = new ServiceLoggerDetail();
		prePlugins = new ArrayList<PluginEntryDetail>();
		postPlugins = new ArrayList<PluginEntryDetail>();	
		pluginsDetails = new PluginsDetail();
		this.externalDataBaseDetail = new DynAuthExternalSystemDetail();
		this.servicePolicies = new ArrayList<String>();
		this.socketDetails = new ArrayList<SocketDetail>();
	}
	
	@XmlElementWrapper(name="service-policies")
	@XmlElement(name="service-policy",type=String.class)
	public List<String> getServicePolicies() {
		return servicePolicies;
	}

	public void setServicePolicies(List<String> servicePolicies) {
		this.servicePolicies = servicePolicies;
	}
	@Reloadable(type=DynAuthExternalSystemDetail.class)
	@XmlElement(name="process-request-from-external-system")
	public DynAuthExternalSystemDetail getExternalDataBaseDetail() {
		return externalDataBaseDetail;
	}

	public void setExternalDataBaseDetail(
			DynAuthExternalSystemDetail externalDataBaseDetail) {
		this.externalDataBaseDetail = externalDataBaseDetail;
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

	@XmlElement(name = "service-address",type = String.class,defaultValue ="0.0.0.0:3799")
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
	@XmlElement(name = "queue-size", type = int.class, defaultValue = AAAServerConstants.DEFAULT_QUEUE_SIZE + "")
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
	@XmlElement(name = "maximum-thread",type = int.class,defaultValue ="10")
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
	@XmlElement(name = "duplicate-request-check-enabled",type = boolean.class)
	public boolean getIsDuplicateRequestCheckEnabled() {
		return duplicateRequestCheckEnabled;
	}
	public void setIsDuplicateRequestCheckEnabled(boolean duplicateRequestCheckEnabled) {
		this.duplicateRequestCheckEnabled = duplicateRequestCheckEnabled;
	}
	@XmlElement(name = "duplicate-request-purge-interval",type = int.class,defaultValue ="15")
	public int getDuplicateRequestQueuePurgeInterval() {
		return duplicateRequestQueuePurgeInterval;
	}
	public void setDuplicateRequestQueuePurgeInterval(int duplicateRequestQueuePurgeInterval) {
		this.duplicateRequestQueuePurgeInterval = duplicateRequestQueuePurgeInterval;
	}

	@Reloadable(type=ServiceLoggerDetail.class)
	@XmlElement(name = "logging")
	public ServiceLoggerDetail getLogger() {
		return logger;
	}
	public void setLogger(ServiceLoggerDetail logger) {
		this.logger = logger;
	}
		
	@PostRead
	public void postReadProcessing() throws MalformedURLException {
		super.postReadProcessing();
		validateDuplicateRequestPurgeInterval();
		
		this.socketDetails = MultiSocketURLParser.parse(serviceAddress);
		
		PrePluginsDetail prePluginObj = getPluginsDetails().getPrePlugins();
		if(prePluginObj!=null){
			List<PluginEntryDetail> prePluginList = prePluginObj.getPrePlugins();
			
			if (Collectionz.isNullOrEmpty(prePluginList) == false) {
				setPluginsCallerId(prePluginList, PluginMode.PRE);
				((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).registerPlugins(prePluginList);
				this.prePlugins = prePluginList;
			}
		}
		
		PostPluginsDetail postPluginObj = getPluginsDetails().getPostPlugins();
		if(postPluginObj!=null){
			List<PluginEntryDetail> postPluginList = postPluginObj.getPostPlugins();
			
			if (Collectionz.isNullOrEmpty(postPluginList) == false) {
				setPluginsCallerId(postPluginList, PluginMode.POST);
				((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).registerPlugins(postPluginList);
				this.postPlugins = postPluginList;
			}
		}
		List<String> tempServicePolicies = new ArrayList<String>();
		if(this.servicePolicies!=null){
			int numOfPolicies = servicePolicies.size();
			String policyName = null;
			for(int i=0;i<numOfPolicies;i++){
				policyName = servicePolicies.get(i);
				if(policyName!=null && policyName.trim().length()>0){
					tempServicePolicies.add(policyName);
				}
			}
		}
		this.servicePolicies = tempServicePolicies;
		
		postProcessingForPurgeInterval();
		
	}
	
	private void setPluginsCallerId(List<PluginEntryDetail> plugins, PluginMode mode) {
		for (int i = 0; i < plugins.size(); i++) {
			PluginEntryDetail data = plugins.get(i);
			PluginCallerIdentity key = PluginCallerIdentity.createAndGetIdentity(ServiceTypeConstants.RAD_DYNAUTH, mode, i, data.getPluginName()).getId();
			data.setCallerId(key);
		}
	}

	private void postProcessingForPurgeInterval() {
		if(!(this.duplicateRequestQueuePurgeInterval >=5 && duplicateRequestQueuePurgeInterval <= 25)){
			duplicateRequestQueuePurgeInterval  = 15;
		}
	}

	@PostWrite
	public void postWriteProcessing(){
		super.postWriteProcessing();
	}
	
	@PostReload
	public void postReloadProcessing(){
		super.postReloadProcessing();
		postProcessingForPurgeInterval();
	}

	@Override
	@XmlTransient
	public String getModuleName() {
		return MODULE;
	}
}


