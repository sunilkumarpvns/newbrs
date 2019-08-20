package com.elitecore.aaa.rm.conf.impl;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.config.PluginsDetail;
import com.elitecore.aaa.core.config.PostPluginsDetail;
import com.elitecore.aaa.core.config.PrePluginsDetail;
import com.elitecore.aaa.core.config.ServiceLoggerDetail;
import com.elitecore.aaa.rm.service.gtpprime.data.GTPPrimeClientDataImpl;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.util.url.MultiSocketURLParser;
import com.elitecore.core.util.url.SocketDetail;

@XmlType(propOrder = {})
@XmlRootElement(name = "gtp-prime-service")
@ConfigurationProperties(moduleName ="GTP_PRIME_CONFIGURABLE",readWith = XMLReader.class, reloadWith = XMLReader.class, synchronizeKey ="GTP_PRIME_SER")
@XMLProperties(name = "gtp-prime", schemaDirectories = {"system","schema"}, configDirectories = {"conf","services"})
public class GTPPrimeConfigurable extends Configurable {

	private String serviceAddress = "0.0.0.0";
	private int socketReceiveBufferSize = -1;
	private int socketSendBufferSize = -1;
	private int queueSize = 10000;
	private int minimumThread = 5;
	private int maximumThread = 10;
	private int mainThreadPriority = 7;
	private int workerThreadPriority = 7;
	private String redirectionIP = null;
	private long idleCommunicationTime = 900;
	private List<SocketDetail> socketDetails;
	private List<PluginEntryDetail> prePlugins; 
	private List<PluginEntryDetail> postPlugins;	
	private PluginsDetail pluginsDetails;
	private ServiceLoggerDetail logger;
	private ClientsDetail clientDetail;
	private Map<InetAddress, GTPPrimeClientDataImpl> addrClientMap;

	public GTPPrimeConfigurable(){
		logger = new ServiceLoggerDetail();
		prePlugins = new ArrayList<PluginEntryDetail>();
		postPlugins =  new ArrayList<PluginEntryDetail>();	
		pluginsDetails = new PluginsDetail();
		clientDetail = new ClientsDetail();
		addrClientMap = new HashMap<InetAddress, GTPPrimeClientDataImpl>();
	}
	
	@XmlTransient
	public Map<InetAddress, GTPPrimeClientDataImpl> getAddrClientMap() {
		return addrClientMap;
	}

	public void setAddrClientMap(Map<InetAddress, GTPPrimeClientDataImpl> addrClientMap) {
		this.addrClientMap = addrClientMap;
	}

	@XmlElement(name = "clients")
	public ClientsDetail getClientDetail(){
		return clientDetail;
	}
	public void setClientDetail(ClientsDetail clientDetail){
		this.clientDetail = clientDetail;
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
	public void setSocketDetails(List<SocketDetail> setSocketDetails) {
		this.socketDetails = setSocketDetails;
	}
	
	@XmlElement(name = "logging")
	public ServiceLoggerDetail getLogger() {
		return logger;
	}
	public void setLogger(ServiceLoggerDetail logger) {
		this.logger = logger;
	}

	@XmlElement(name = "service-address",type = String.class,defaultValue ="0.0.0.0")
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
	@XmlElement(name = "redirection-ip",type = String.class)
	public String getRedirectionIP(){
		return redirectionIP;
	}
	public void setRedirectionIP(String redirectionIP){
		this.redirectionIP = redirectionIP;
	}
	
	@XmlElement(name = "max-idle-communication-time-interval",type = long.class,defaultValue ="900")
	public long getIdleCommunicationTimeInterval(){
		return idleCommunicationTime;
	}
	public void setIdleCommunicationTimeInterval(long idleCommunicationTime){
		this.idleCommunicationTime = idleCommunicationTime;
	}
	
	@PostRead
	public void postReadProcessing() throws MalformedURLException {
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
		
		List<GTPPrimeClientDataImpl> clientList = getClientDetail().getClientList();
		List<GTPPrimeClientDataImpl> tempClientList = new ArrayList<GTPPrimeClientDataImpl>();
		if(clientList != null){
			int size  = clientList.size();
			GTPPrimeClientDataImpl clientDataImpl;
			try{
				for(int i = 0; i<size ; i++){
					clientDataImpl = clientList.get(i);
					
					InetAddress.getByName(clientDataImpl.getClientIP());
					
					if(clientDataImpl.getRedirectionIP() == null || !(clientDataImpl.getRedirectionIP().trim().length()>0)){
						clientDataImpl.setRedirectionIP(getRedirectionIP());
					}
					
					int minSequence = 0;
					int maxSequence = Integer.MAX_VALUE;
					String strSeqRange = clientDataImpl.getSequenceRange().trim();
					if (strSeqRange!=null && strSeqRange.indexOf('-')!= -1){
						try{
							minSequence = Integer.parseInt(strSeqRange.substring(0, strSeqRange.indexOf('-')));
							maxSequence = Integer.parseInt(strSeqRange.substring(strSeqRange.indexOf('-') + 1, strSeqRange.length() ));
						}catch(Exception e){
						}
						 
					} 
					clientDataImpl.setMaxSequenceRange(maxSequence);
					clientDataImpl.setMinSequenceRange(minSequence);
					
					try {
						addrClientMap.put(InetAddress.getByName(clientDataImpl.getClientIP()), clientDataImpl);
						tempClientList.add(clientDataImpl);
					} catch (UnknownHostException e) {
					}
				}
				clientDetail.setClientList(tempClientList);
				
			}catch(Exception e){
				
			}
			
			
		}
	}
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostReload
	public void postReloadProcessing(){
		
	}
}

@XmlType(propOrder = {})
class ClientsDetail{
	
	private List<GTPPrimeClientDataImpl> clientList;
	
	public ClientsDetail(){
		clientList = new ArrayList<GTPPrimeClientDataImpl>();
	}
	
	@XmlElement(name = "client")
	public List<GTPPrimeClientDataImpl> getClientList(){
		return clientList;
	}
	public void setClientList(List<GTPPrimeClientDataImpl> clientList){
		this.clientList = clientList;
	}
}
