package com.elitecore.aaa.rm.conf.impl;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.aaa.core.config.PluginsDetail;
import com.elitecore.aaa.core.config.PostPluginsDetail;
import com.elitecore.aaa.core.config.PrePluginsDetail;
import com.elitecore.aaa.core.config.ServiceLoggerDetail;
import com.elitecore.aaa.radius.conf.impl.RadiusServiceConfigurable;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.core.commons.config.core.BooleanAdapter;
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
@XmlRootElement(name = "ippool-service")
@ConfigurationProperties(moduleName ="RM_IPPOOL_CONFIGURABLE",readWith = XMLReader.class, reloadWith = XMLReader.class, synchronizeKey ="RM_IPPOOL")
@XMLProperties(name = "rm-ippool", schemaDirectories = {"system","schema"}, configDirectories = {"conf","services"})
public class RMIPPoolServiceConfigurable extends RadiusServiceConfigurable {
	
	private static final String MODULE = "RM_IPPOOL_CONFIGURABLE";
	private int socketReceiveBufferSize = -1;
	private int socketSendBufferSize = -1;
	private int queueSize = 10000;
	private int minimumThread = 5;
	private int maximumThread = 10;
	private int mainThreadPriority = 7;
	private int workerThreadPriority = 7;
	
	private Boolean duplicateRequestCheckEnabled = false;
	
	private int duplicateRequestQueuePurgeInterval = AAAServerConstants.DEFAULT_DUPLICATE_REQUEST_QUEUE_PURGE_INTERVAL;
	private List<SocketDetail> radiusSocketDataList;
	private String serviceAddress = "0.0.0.0:1811";


	private List<PluginEntryDetail> prePlugins; 
	private List<PluginEntryDetail> postPlugins;	
	private PluginsDetail pluginsDetails;
	private ServiceLoggerDetail logger;
	private AutoSessionCloserDetail autoSessionCloserDetail;
	
	private String defaultIPPoolName;
	private int dbQueryTimeOut;
	private  String dataSourceName;
	private int dbscantime=1000;
	private boolean operationFailureEnabled=true;
	
	
	public RMIPPoolServiceConfigurable(){
		logger = new ServiceLoggerDetail();
		prePlugins = new ArrayList<PluginEntryDetail>();
		postPlugins =  new ArrayList<PluginEntryDetail>();	
		pluginsDetails = new PluginsDetail();
		autoSessionCloserDetail = new AutoSessionCloserDetail();
	
	}
	
	@XmlElement(name = "default-ippool-name",type = String.class)
	public String getDefaultIPPoolName() {
		return defaultIPPoolName;
	}

	public void setDefaultIPPoolName(String defaultIPPoolName) {
		this.defaultIPPoolName = defaultIPPoolName;
	}

	@Reloadable(type=Integer.class)
	@XmlElement(name = "db-query-timeout",type = int.class)
	public int getDbQueryTimeOut() {
		return dbQueryTimeOut;
	}

	
	public void setDbQueryTimeOut(int dbQueryTimeOut) {
		this.dbQueryTimeOut = dbQueryTimeOut;
	}

	@XmlElement(name = "datasource-name",type = String.class)
	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	@XmlElement(name = "db-scantime",type = int.class,defaultValue ="1000")
	public int getDbScanTime() {
		return dbscantime;
	}

	public void setDbScanTime(int dbscantime) {
		this.dbscantime = dbscantime;
	}

	@Reloadable(type=Boolean.class)
	@XmlElement(name = "operation-failure-enabled",type = boolean.class,defaultValue ="true")
	public boolean getIsOperationFailureEnabled() {
		return operationFailureEnabled;
	}

	public void setIsOperationFailureEnabled(boolean operationFailureEnabled) {
		this.operationFailureEnabled = operationFailureEnabled;
	}

	
	@XmlElement(name ="auto-session-closure")
	public AutoSessionCloserDetail getAutoSessionCloserDetail() {
		return autoSessionCloserDetail;
	}

	public void setAutoSessionCloserDetail(
			AutoSessionCloserDetail autoSessionCloserDetail) {
		this.autoSessionCloserDetail = autoSessionCloserDetail;
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
	public List<SocketDetail> getRadiusSocketDataList() {
		return this.radiusSocketDataList;
	}
	public void setRadiusSocketDataList(List<SocketDetail> radiusSocketDataList) {
		this.radiusSocketDataList = radiusSocketDataList;
	}

	@XmlElement(name = "service-address",type = String.class,defaultValue ="0.0.0.0:1811")
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
	
	@XmlJavaTypeAdapter(BooleanAdapter.class)
	@XmlElement(name = "duplicate-request-check-enabled")
	public Boolean getIsDuplicateRequestCheckEnabled() {
		return duplicateRequestCheckEnabled;
	}
	
	public void setIsDuplicateRequestCheckEnabled(Boolean duplicateRequestCheckEnabled) {
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
		
		this.radiusSocketDataList = MultiSocketURLParser.parse(serviceAddress);
		
		PrePluginsDetail prePluginObj = getPluginsDetails().getPrePlugins();
		if(prePluginObj!=null){
			List<PluginEntryDetail> prePluginList = prePluginObj.getPrePlugins();
			if (prePluginList != null) {
				this.prePlugins = prePluginList;
			}
		}
		
		PostPluginsDetail postPluginObj = getPluginsDetails().getPostPlugins();
		if(postPluginObj!=null){
			List<PluginEntryDetail> postPluginList = postPluginObj.getPostPlugins(); 
			if (postPluginList != null) {
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
@XmlType(propOrder = {})
class AutoSessionCloserDetail{
	
	
	private boolean enabled;
	private int executionInterval=50;
	private int reservationTimeoutInterval=1;
	private int sessionTimeoutInterval=1;
	private int maxBatchSize=100;
	
	
	
	public AutoSessionCloserDetail(){
		//required By Jaxb.
		
	}
	
	@XmlElement(name = "execution-interval",type = int.class,defaultValue = "50")
	public int getExecutionInterval() {
		return executionInterval;
	}
	public void setExecutionInterval(int executionInterval) {
		this.executionInterval = executionInterval;
	}

	@XmlElement(name = "enabled",type = boolean.class,defaultValue = "false")
	public boolean getIsEnabled() {
		return enabled;
	}

	public void setIsEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@XmlElement(name = "reservation-timeout-interval",type = int.class,defaultValue = "1")
	public int getReservationTimeoutInterval() {
		return reservationTimeoutInterval;
	}

	public void setReservationTimeoutInterval(int reservationTimeoutInterval) {
		this.reservationTimeoutInterval = reservationTimeoutInterval;
	}

	@XmlElement(name = "session-timeout-interval",type = int.class,defaultValue = "1")
	public int getSessionTimeoutInterval() {
		return sessionTimeoutInterval;
	}

	public void setSessionTimeoutInterval(int sessionTimeoutInterval) {
		this.sessionTimeoutInterval = sessionTimeoutInterval;
	}

	@XmlElement(name = "max-batch-size",type = int.class,defaultValue = "100")
	public int getMaxBatchSize() {
		return maxBatchSize;
	}

	public void setMaxBatchSize(int maxBatchSize) {
		this.maxBatchSize = maxBatchSize;
	}

}
