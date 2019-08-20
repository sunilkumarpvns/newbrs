package com.elitecore.aaa.rm.conf.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.parsers.ParserConfigurationException;

import com.elitecore.aaa.core.config.PluginsDetail;
import com.elitecore.aaa.core.config.PostPluginsDetail;
import com.elitecore.aaa.core.config.PrePluginsDetail;
import com.elitecore.aaa.core.config.ServiceLoggerDetail;
import com.elitecore.aaa.rm.data.RDRClientDataImpl;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.Reloadable;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;

@XmlType(propOrder = {})
@XmlRootElement(name = "rdr-service")
@ConfigurationProperties(moduleName ="RDR_SERVICE_CONFIGURABLE",readWith = XMLReader.class, reloadWith = XMLReader.class, synchronizeKey ="RDR_SER")
@XMLProperties(name = "rdr-service", schemaDirectories = {"system","schema"}, configDirectories = {"conf","services"})
public class RDRConfigurable extends Configurable {
	
	private int socketReceiveBufferSize = -1;
	private int socketSendBufferSize = -1;
	private int queueSize = 10000;
	private int minimumThread = 5;
	private int maximumThread = 10;
	private int mainThreadPriority = 7;
	private int workerThreadPriority = 7;
	private SocketDetail socketDetail = new SocketDetail("0.0.0.0", 3386);
	private String serviceAddress = "0.0.0.0:3386";
	private List<PluginEntryDetail> prePlugins; 
	private List<PluginEntryDetail> postPlugins;	
	private PluginsDetail pluginsDetails;
	private ServiceLoggerDetail logger;
	
	private List<RDRClientDataImpl> clients;
	private Map<InetAddress,RDRClientDataImpl> addrClientMap;
	
	public RDRConfigurable(){
		logger = new ServiceLoggerDetail();
		prePlugins = new ArrayList<PluginEntryDetail>();
		postPlugins =  new ArrayList<PluginEntryDetail>();	
		pluginsDetails = new PluginsDetail();
		clients = new ArrayList<RDRClientDataImpl>();
		addrClientMap = new HashMap<InetAddress , RDRClientDataImpl>();
	}
	
	@XmlElementWrapper(name ="clients")
	@XmlElement(name ="client")
	public List<RDRClientDataImpl> getRDRClients() {
		return this.clients;
	}

	public void setRDRClients(List<RDRClientDataImpl> lstClients) {
		this.clients = lstClients;
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
	@XmlTransient
	public SocketDetail getSocketDetail() {
		return socketDetail;
	}
	public void setSocketDetail(SocketDetail socketDetail) {
		this.socketDetail = socketDetail;
	}

	@XmlElement(name = "service-address",type = String.class,defaultValue ="0.0.0.0:3386")
	public String getServiceAddress() {
		return serviceAddress;
	}
	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
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
	@Reloadable(type=ServiceLoggerDetail.class)
	@XmlElement(name = "logging")
	public ServiceLoggerDetail getLogger() {
		return logger;
	}
	public void setLogger(ServiceLoggerDetail logger) {
		this.logger = logger;
	}
	@PostRead
	public void postReadProcessing() throws ParserConfigurationException{
		URLData address;
		try {
			address = URLParser.parse(this.serviceAddress);
			this.socketDetail = new SocketDetail(address.getHost(), address.getPort());
		} catch (InvalidURLException e) {

		}
		
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
		List<RDRClientDataImpl> clientsList = this.clients;
		if(clientsList != null && clientsList.size() >0){
			Map<InetAddress,RDRClientDataImpl> addrClientMap = new HashMap<InetAddress, RDRClientDataImpl>();
			RDRClientDataImpl rdrClientData;
			int size = clientsList.size();
			for(int i=0;i<size;i++){
				rdrClientData = clientsList.get(i);
				String clientIp = rdrClientData.getClientIP();
				try{
					InetAddress.getByName(clientIp);
				} catch (UnknownHostException e){
					throw new ParserConfigurationException("Client IP found invalid: " + clientIp);
				}
				int rollingType = rdrClientData.getRollingType();
				try {
					if (!(rollingType == 1 || rollingType == 2)){
						throw new ParserConfigurationException("Rolling type invalid.");
					}
				} catch (Exception e) {
					throw new ParserConfigurationException("Rolling type invalid: " + rollingType);
				}
				try {
					addrClientMap.put(InetAddress.getByName(clientIp), rdrClientData);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
				}
			}
			this.addrClientMap = addrClientMap;
		}
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostReload
	public void postReloadProcessing(){
		
	}
	
	@XmlTransient
	public Map<InetAddress,RDRClientDataImpl> getRDRClientMap() {
		return this.addrClientMap;
	}

	public void setRDRClients(Map<InetAddress,RDRClientDataImpl> addrClientMap) {
		this.addrClientMap = addrClientMap;
	}
	
}
