package com.elitecore.aaa.diameter.conf;

import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.config.RFCNaiDetail;
import com.elitecore.aaa.core.config.ServiceLoggerDetail;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.conf.impl.AbortSessionTranslationMappingDetail;
import com.elitecore.aaa.diameter.conf.impl.DiameterPluginsDetail;
import com.elitecore.aaa.diameter.conf.impl.DiameterSessionCleanupDetail;
import com.elitecore.aaa.diameter.conf.impl.DiameterWebServiceConfigurationDetail;
import com.elitecore.aaa.diameter.conf.impl.GenericTranslationMappingDetail;
import com.elitecore.aaa.diameter.conf.impl.InPluginsDetail;
import com.elitecore.aaa.diameter.conf.impl.OutPluginsDetail;
import com.elitecore.aaa.diameter.conf.impl.PeerSecurityParameters;
import com.elitecore.aaa.diameter.conf.impl.ReAuthTranslationMappingDetail;
import com.elitecore.aaa.diameter.conf.impl.SelectedPeerConfDetail;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.xml.OptionalStringAdapter;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.Reloadable;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.plugins.data.PluginMode;
import com.elitecore.core.commons.plugins.data.ServiceTypeConstants;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;
import com.elitecore.diameterapi.core.common.transport.constant.SecurityStandard;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;


@XmlType(propOrder = {})
@XmlRootElement(name = "diameter-stack")
@ConfigurationProperties(moduleName ="DIA-STACK-CONFIGURABLE",readWith = XMLReader.class, reloadWith = XMLReader.class, synchronizeKey = AAAServerConstants.DIAMETER_STACK)
@XMLProperties(name = "diameter-stack", schemaDirectories = {"system","schema"}, configDirectories = {"conf"})
public class DiameterStackConfigurable extends Configurable {
	
	private static final String MODULE = "DIA-STACK-CONFIGURABLE"; 
	
	private static final long MAX_SESSION_TIMEOUT = 86400;
	private static final long MAX_SESSION_CLEANUP_INTERVAL = 43200;
	private static final long MIN_SESSION_CLEANUP_INTERVAL = 300;
	
	private boolean isDiameterStackEnabled = false;
	private String serviceIpAddress = "0.0.0.0";
	private int servicePort = DiameterConstants.DIAMETER_SERVICE_PORT;

	private String ownURI = "aaa://localhost:3868";
	private String routingTableName = "";
	private int mainThreadPriority = 1;
	private int workerThreadPriority = 1;

	private int socketReceiveBufferSize = 32767;
	private int socketSendBufferSize = 32767;
	private int minThreadPoolSize = 10;
	private int maxThreadPoolSize = 20;
	private int maxRequestQueueSize;
	private int keepAliveTime = 3000;
	
	private SecurityStandard securityStandard = SecurityStandard.NONE;
	private PeerSecurityParameters securityParameter;

	private ServiceLoggerDetail serviceLoggerDetail;
	
	private List<String> naiRealmNames;

	private RFCNaiDetail rfcNaiDetail;
	private DiameterSessionCleanupDetail diameterSessionCleanupDetail;
	private SelectedPeerConfDetail selectedPeerConfDetail;
	private DiameterPluginsDetail diameterPluginsDetail;
	private DiameterWebServiceConfigurationDetail diameterWebServiceConfigurationDetail;

	private List<PluginEntryDetail> inPlugins;
	private List<PluginEntryDetail> outPlugins;
	
	private String strServiceAddress = "0.0.0.0:3868";
	
	private boolean isRealmVerificationRequired=true;

	private boolean isDuplicateRequestDetectionEnabled;

	private int dupicateRequestQueuePurgeInterval;
	
	private String logLocation;
	private Map<String, String> wsTranslationMappingConfigMap;
	private Optional<String> sessionManagerId = Optional.absent();
	
	public DiameterStackConfigurable() {
		this.serviceLoggerDetail = new ServiceLoggerDetail();
		this.inPlugins = new ArrayList<PluginEntryDetail>();
		this.outPlugins =  new ArrayList<PluginEntryDetail>();
		this.selectedPeerConfDetail = new SelectedPeerConfDetail();
		this.diameterPluginsDetail = new DiameterPluginsDetail();
		this.diameterSessionCleanupDetail = new DiameterSessionCleanupDetail();
		this.naiRealmNames = new ArrayList<String>();
		this.rfcNaiDetail = new RFCNaiDetail();
		this.wsTranslationMappingConfigMap = new HashMap<String, String>();
		this.diameterWebServiceConfigurationDetail = new DiameterWebServiceConfigurationDetail();
	}

	@XmlElement(name="session-manager-id", type=String.class)
	@XmlJavaTypeAdapter(value = OptionalStringAdapter.class)
	public Optional<String> getSessionManagerId() {
		return sessionManagerId;
	}
	public void setSessionManagerId(Optional<String> sessionManagerId) {
		this.sessionManagerId = sessionManagerId;
	}

	@XmlElement(name="peer-list")
	public SelectedPeerConfDetail getSelectedPeerConfDetail() {
		return selectedPeerConfDetail;
	}


	public void setSelectedPeerConfDetail(
			SelectedPeerConfDetail selectedPeerConfDetail) {
		this.selectedPeerConfDetail = selectedPeerConfDetail;
	}

	@XmlElement(name="diameter-stack-enable", type=boolean.class, defaultValue ="false")
	public boolean isDiameterStackEnabled() {
		return isDiameterStackEnabled;
	}


	public void setDiameterStackEnabled(boolean isEnabled) {
		this.isDiameterStackEnabled = isEnabled;
	}

	@PostRead
	public void postReadProcessing() throws InvalidURLException {
		
		validateQueueSize();
		if(getLogger().getIsLoggerEnabled()){
			String serverHome = ((AAAConfigurationContext)getConfigurationContext()).getServerContext().getServerHome();
			String defaultLogLocation = serverHome + File.separator + "logs";
			if(getLogger().getLogLocation() != null && getLogger().getLogLocation().trim().length() >0){
				 String logLocation = getValidFileLocation(getLogger().getLogLocation(),defaultLogLocation,serverHome);
				 this.logLocation = logLocation + File.separator + "diameter-stack";
			}else{
				this.logLocation = defaultLogLocation + File.separator + "diameter-stack";
			}	
		}
		
		URLData address = URLParser.parse(this.strServiceAddress);
		this.serviceIpAddress = address.getHost();
		this.servicePort = address.getPort();
		
		if(getDiameterPluginsDetail()!=null){
			InPluginsDetail inPluginObj = getDiameterPluginsDetail().getInPlugins();
			if(inPluginObj!=null){
				List<PluginEntryDetail> inPluginList = inPluginObj.getInPlugins();
				
				if (Collectionz.isNullOrEmpty(inPluginList) == false) {
					setPluginsCallerId(inPluginList, PluginMode.IN);
					((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).registerPlugins(inPluginList);
					this.inPlugins = inPluginList;
				}
			}
			
			OutPluginsDetail outPluginObj = getDiameterPluginsDetail().getOutPlugins();
			if(outPluginObj!=null){
				List<PluginEntryDetail> outPluginList = outPluginObj.getOutPlugins(); 
				
				if (Collectionz.isNullOrEmpty(outPluginList) == false) {
					setPluginsCallerId(outPluginList, PluginMode.OUT);
					((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).registerPlugins(outPluginList);
					this.outPlugins = outPluginList;
				}
			}
		}
		
		postProcessingOfRFCNAIConfiguration();
		postDiameterSessionCleanConfiguration();
		
		SelectedPeerConfDetail selectedPeerConfDetail = getSelectedPeerConfDetail();
		if(selectedPeerConfDetail!=null){
			List<String> configuredPeer = selectedPeerConfDetail.getPeerList();
			
			if(configuredPeer!=null && configuredPeer.size()>0){
				List<String> validPeers = new ArrayList<String>();
				int numOfPeer = configuredPeer.size();
				for(int i=0;i<numOfPeer;i++){
					String identity = configuredPeer.get(i);
					if(identity!=null && identity.trim().length()>0){
						validPeers.add(identity.trim());
					}
				}
				selectedPeerConfDetail.setPeerList(validPeers);
			}
		}
		
		Map<String, String> tempWSTransaltionMappingMap = new HashMap<String, String>();
		ReAuthTranslationMappingDetail reAuthTranslationMappingDetail = diameterWebServiceConfigurationDetail.getReAuthTranslationMappingDetail();
		if(reAuthTranslationMappingDetail!=null && reAuthTranslationMappingDetail.getTranslationMapping()!=null && reAuthTranslationMappingDetail.getTranslationMapping().trim().length()>0){
			tempWSTransaltionMappingMap.put(TranslatorConstants.DIAMETER_RE_AUTH, reAuthTranslationMappingDetail.getTranslationMapping());
		}
		
		AbortSessionTranslationMappingDetail abortSessionTranslationMappingDetail = diameterWebServiceConfigurationDetail.getAbortSessionTranslationMappingDetail();
		if(abortSessionTranslationMappingDetail!=null && abortSessionTranslationMappingDetail.getTranslationMapping()!=null && abortSessionTranslationMappingDetail.getTranslationMapping().trim().length()>0){
			tempWSTransaltionMappingMap.put(TranslatorConstants.DIAMETER_ABORT_SESSION, abortSessionTranslationMappingDetail.getTranslationMapping());
		}
		
		GenericTranslationMappingDetail genericTranslationMappingDetail = diameterWebServiceConfigurationDetail.getGenericTranslationMappingDetail();
		if(genericTranslationMappingDetail!=null && genericTranslationMappingDetail.getTranslationMapping()!=null && genericTranslationMappingDetail.getTranslationMapping().trim().length()>0){
			tempWSTransaltionMappingMap.put(TranslatorConstants.DIAMETER_GENERIC_REQUEST, genericTranslationMappingDetail.getTranslationMapping());
		}
		this.wsTranslationMappingConfigMap = tempWSTransaltionMappingMap;
				
	}
	
	private void validateQueueSize() {
		if (getMaxRequestQueueSize() <= 0) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Configured queue size: " + getMaxRequestQueueSize() 
						+ " is invalid. Using default queue size: " + AAAServerConstants.DEFAULT_QUEUE_SIZE);
			}
			setMaxRequestQueueSize(AAAServerConstants.DEFAULT_QUEUE_SIZE);
			return;
		} else if (getMaxRequestQueueSize() > 50000) {
		if (LogManager.getLogger().isWarnLogLevel()) {
			LogManager.getLogger().warn(MODULE, "Configured queue size: " + getMaxRequestQueueSize() 
						+ " is greater than maximum queue size: " + AAAServerConstants.MAX_QUEUE_SIZE + 
						", so using " + AAAServerConstants.MAX_QUEUE_SIZE + " as max queue size.");
		}
			setMaxRequestQueueSize(AAAServerConstants.MAX_QUEUE_SIZE);
			return;
	}
	}

	
	private void setPluginsCallerId(List<PluginEntryDetail> plugins, PluginMode mode) {
		for (int index = 0; index < plugins.size(); index++) {
			PluginEntryDetail data = plugins.get(index);
			PluginCallerIdentity key = PluginCallerIdentity.createAndGetIdentity(ServiceTypeConstants.DIA_STACK, mode, index, data.getPluginName()).getId();
			data.setCallerId(key);
		}
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
				LogManager.getLogger().warn(MODULE, "Invalid location : "+ location +" Reason: " + ex.getMessage() + ". Using default location : " + defaultLocation);
			return defaultLocation;
		}
	}

	@XmlTransient
	public String getIpAddress() {
		return serviceIpAddress;
	}


	public void setIpAddress(String serviceIpAddress) {
		this.serviceIpAddress = serviceIpAddress;
	}


	@XmlTransient
	public int getPort() {
		return servicePort;
	}


	public void setPort(int servicePort) {
		this.servicePort = servicePort;
	}

	@XmlElement(name="own-diameter-URI",type=String.class,defaultValue="aaa://localhost:3868")
	public String getOwnURI() {
		return ownURI;
	}


	public void setOwnURI(String ownURI) {
		this.ownURI = ownURI;
	}

	@Reloadable(type = String.class)
	@XmlElement(name="routing-table",type=String.class,defaultValue="")
	public String getRoutingTableName() {
		return routingTableName;
	}


	public void setRoutingTableName(String routingTableName) {
		this.routingTableName = routingTableName;
	}

	@XmlElement(name="main-thread-priority",type=int.class,defaultValue="1")
	public int getMainThreadPriority() {
		return mainThreadPriority;
	}


	public void setMainThreadPriority(int mainThreadPriority) {
		this.mainThreadPriority = mainThreadPriority;
	}


	@XmlElement(name="worker-thread-priority",type=int.class,defaultValue="1")
	public int getWorkerThreadPriority() {
		return workerThreadPriority;
	}


	public void setWorkerThreadPriority(int workerThreadPriority) {
		this.workerThreadPriority = workerThreadPriority;
	}

	@XmlElement(name="socket-receive-buffer-size",type=int.class,defaultValue="32767")	
	public int getSocketReceiveBufferSize() {
		return socketReceiveBufferSize;
	}


	public void setSocketReceiveBufferSize(int socketReceiveBufferSize) {
		this.socketReceiveBufferSize = socketReceiveBufferSize;
	}

	@XmlElement(name="socket-send-buffer-size",type=int.class,defaultValue="32767")
	public int getSocketSendBufferSize() {
		return socketSendBufferSize;
	}


	public void setSocketSendBufferSize(int socketSendBufferSize) {
		this.socketSendBufferSize = socketSendBufferSize;
	}

	@XmlElement(name="minimum-thread",type=int.class,defaultValue="10")
	public int getMinThreadPoolSize() {
		return minThreadPoolSize;
	}


	public void setMinThreadPoolSize(int minThreadPoolSize) {
		this.minThreadPoolSize = minThreadPoolSize;
	}


	@XmlElement(name="maximum-thread",type=int.class,defaultValue="20")
	public int getMaxThreadPoolSize() {
		return maxThreadPoolSize;
	}

	
	public void setMaxThreadPoolSize(int maxThreadPoolSize) {
		this.maxThreadPoolSize = maxThreadPoolSize;
	}

	@XmlElement(name="queue-size", type=int.class, defaultValue = AAAServerConstants.DEFAULT_QUEUE_SIZE + "")
	public int getMaxRequestQueueSize() {
		return maxRequestQueueSize;
	}


	public void setMaxRequestQueueSize(int maxRequestQueueSize) {
		this.maxRequestQueueSize = maxRequestQueueSize;
	}

	@XmlTransient
	public int getKeepAliveTime() {
		return keepAliveTime;
	}
	
	@Reloadable(type=ServiceLoggerDetail.class)
	@XmlElement(name="logging")
	public ServiceLoggerDetail getLogger() {
		return serviceLoggerDetail;
	}


	public void setLogger(ServiceLoggerDetail serviceLoggerDetail) {
		this.serviceLoggerDetail = serviceLoggerDetail;
	}

	@XmlTransient
	public boolean isNAIEnabled() {
		return rfcNaiDetail.getEnabled();
	}

	@XmlTransient
	public boolean isRealmVerificationRequired() {
		return isRealmVerificationRequired;
	}
	
	@XmlTransient
	public List<String> getNaiRealmNames() {
		return naiRealmNames;
	}


	@XmlTransient
	public List<String> getHostIdentityList() {
		return this.selectedPeerConfDetail.getPeerList();
	}

	@XmlElement(name="rfc-5729-nai")
	public RFCNaiDetail getRfcNaiDetail() {
		return rfcNaiDetail;
	}


	public void setRfcNaiDetail(RFCNaiDetail rfcNaiDetail) {
		this.rfcNaiDetail = rfcNaiDetail;
	}


	@Reloadable(type=DiameterSessionCleanupDetail.class)
	@XmlElement(name="diameter-session-cleanup")
	public DiameterSessionCleanupDetail getDiameterSessionCleanupDetail() {
		return diameterSessionCleanupDetail;
	}


	public void setDiameterSessionCleanupDetail(
			DiameterSessionCleanupDetail diameterSessionCleanupDetail) {
		this.diameterSessionCleanupDetail = diameterSessionCleanupDetail;
	}

	@XmlElement(name="plugin-list")
	public DiameterPluginsDetail getDiameterPluginsDetail() {
		return diameterPluginsDetail;
	}


	public void setDiameterPluginsDetail(DiameterPluginsDetail diameterPluginsDetail) {
		this.diameterPluginsDetail = diameterPluginsDetail;
	}
	
	@XmlElement(name="diameter-webservice")
	public DiameterWebServiceConfigurationDetail getDiameterWebServiceConfigurationDetail() {
		return diameterWebServiceConfigurationDetail;
	}

	public void setDiameterWebServiceConfigurationDetail(
			DiameterWebServiceConfigurationDetail diameterWebServiceConfigurationDetail) {
		this.diameterWebServiceConfigurationDetail = diameterWebServiceConfigurationDetail;
	}

	@XmlTransient
	public List<PluginEntryDetail> getOutPluginList() {
		return outPlugins;
	}
	
	@XmlTransient
	public List<PluginEntryDetail> getInPluginList() {
		return inPlugins;
	}
	

	@XmlElement(name="service-address",type=String.class,defaultValue="0.0.0.0:3868")
	public String getStrAddress() {
		return strServiceAddress;
	}


	public void setStrAddress(String strServiceAddress) {
		this.strServiceAddress = strServiceAddress;
	}

	@XmlElement(name="duplicate-request-check-enable" , type =boolean.class,defaultValue="true")
	public boolean getIsDuplicateRequestDetectionEnabled() {
		return isDuplicateRequestDetectionEnabled;
	}
	public void setIsDuplicateRequestDetectionEnabled(boolean isDuplicateRequestDetectionEnabled) {
		this.isDuplicateRequestDetectionEnabled = isDuplicateRequestDetectionEnabled;
	}

	@XmlElement(name="duplicate-request-purge-interval" , type =int.class,defaultValue="15")
	public int getDupicateRequestQueuePurgeInterval() {
		return dupicateRequestQueuePurgeInterval;
	}
	
	public void setDupicateRequestQueuePurgeInterval(int dupicateRequestQueuePurgeInterval) {
		this.dupicateRequestQueuePurgeInterval = dupicateRequestQueuePurgeInterval;
	}
	
	@XmlElement(name="security-parameters")
	public PeerSecurityParameters getSecurityParameters() {
		return securityParameter;
	}
	
	@XmlElement(name = "security-standard",type = SecurityStandard.class,defaultValue="NONE")
	public SecurityStandard getSecurityStandard() {
		return securityStandard;
	}
	
	public void setSecurityParameters(PeerSecurityParameters securityParameters) {
		this.securityParameter = securityParameters;
	}
	
	public void setSecurityStandard(SecurityStandard securityStandard) {
		this.securityStandard = securityStandard;
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostReload
	public void postReloadProcessing(){
		postProcessingOfRFCNAIConfiguration();
		postDiameterSessionCleanConfiguration();
	}
	
	public String getWsTranslationMappingName(String methodId) {
		return this.wsTranslationMappingConfigMap.get(methodId);
	}
	
	private void postProcessingOfRFCNAIConfiguration(){
				
		if(rfcNaiDetail==null || !rfcNaiDetail.getEnabled()){
			return;
		}
		String configuredRealms = rfcNaiDetail.getRealmName();
		if(configuredRealms == null || !(configuredRealms.trim().length()>0)){
			rfcNaiDetail.setEnabled(false);
		}else {
			if("*".equalsIgnoreCase(configuredRealms)){
				this.isRealmVerificationRequired = false;
			}else {
				StringTokenizer tk = new StringTokenizer(configuredRealms,",");
				String nextRealm;
				List<String> tempRealmConfigured = new ArrayList<String>();
				while(tk.hasMoreTokens()){
					nextRealm = tk.nextToken();
					if(nextRealm.trim().length()>0)
					tempRealmConfigured.add(nextRealm);
				}
				this.naiRealmNames = tempRealmConfigured;
			}
		}
		
	}
	
	private void postDiameterSessionCleanConfiguration(){
		if(this.diameterSessionCleanupDetail==null){
			return;
		}
		long sessionCleanupInterval = diameterSessionCleanupDetail.getSessionCleanupInterval();
		if (sessionCleanupInterval < MIN_SESSION_CLEANUP_INTERVAL && sessionCleanupInterval > 0) {
			LogManager.getLogger().warn(MODULE, "Session cleanup interval: " +sessionCleanupInterval + " is less than " + MIN_SESSION_CLEANUP_INTERVAL + " seconds. Interval less than " + MIN_SESSION_CLEANUP_INTERVAL + " seconds is not allowed. Session cleanup interval set to " + MIN_SESSION_CLEANUP_INTERVAL);
			LogManager.getLogger().warn(MODULE, "To disable session cleanup configure it to -1.");
			sessionCleanupInterval = MIN_SESSION_CLEANUP_INTERVAL;
		}
		if (sessionCleanupInterval > MAX_SESSION_CLEANUP_INTERVAL){
			LogManager.getLogger().warn(MODULE, "Session cleanup interval: " +sessionCleanupInterval + " is more than " + MAX_SESSION_CLEANUP_INTERVAL + " seconds. Interval more than " + MAX_SESSION_CLEANUP_INTERVAL + " seconds is not allowed. Session cleanup interval set to " + MAX_SESSION_CLEANUP_INTERVAL);
			sessionCleanupInterval = MAX_SESSION_CLEANUP_INTERVAL;
		}
		diameterSessionCleanupDetail.setSessionCleanupInterval(sessionCleanupInterval);
		
		long sessionTimeOut = diameterSessionCleanupDetail.getSessionTimeOut();
		if (sessionTimeOut > MAX_SESSION_TIMEOUT){
			LogManager.getLogger().warn(MODULE, "Session Timeout: " +sessionTimeOut + " is more than " + MAX_SESSION_TIMEOUT + " seconds. Session Timeout more than " + MAX_SESSION_TIMEOUT + " seconds is not allowed. Session Timeout set to " + MAX_SESSION_TIMEOUT);
			sessionTimeOut = MAX_SESSION_TIMEOUT;
		}
		diameterSessionCleanupDetail.setSessionTimeOut(sessionTimeOut);
	}
	
	@XmlTransient
	public String getLogLocation() {
		return logLocation;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		
		out.println();
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Diameter Stack Enabled", 
				isDiameterStackEnabled()));
		out.println(format("%-30s: %s", "Stack Address", 
				getIpAddress()));
		out.println(format("%-30s: %s", "Socket Receive Buffer Size", 
				getSocketReceiveBufferSize()));
		out.println(format("%-30s: %s", "Socket Send Buffer Size", 
				getSocketSendBufferSize()));
		out.println(format("%-30s: %s", "Queue Size", 
				getMaxRequestQueueSize()));
		out.println(format("%-30s: %s", "Minimum Thread", 
				getMinThreadPoolSize()));
		out.println(format("%-30s: %s", "Maximum Thread", 
				getMaxThreadPoolSize()));
		out.println(format("%-30s: %s", "Main Thread Priority", 
				getMainThreadPriority()));
		out.println(format("%-30s: %s", "Worker Thread Priority", 
				getWorkerThreadPriority()));
		out.println(format("%-30s: %s", "Diameter URI", 
				getOwnURI()));
		out.println(format("%-30s: %s", "Routing Table Name", 
				getRoutingTableName()));
		out.println(format("%-30s: %s", "Session Manager Id", 
				getSessionManagerId().isPresent() ? getSessionManagerId().get() : ""));
		out.println(format("%-30s: %s", "Secuity Standard", 
				getSecurityStandard()));

		out.println(format("%-30s: %s", "Duplicate Request Check", 
				getIsDuplicateRequestDetectionEnabled()));
		if (getIsDuplicateRequestDetectionEnabled()) {
			out.println(format("%-30s: %s", "Duplicate Request Purge Interval", 
					getDupicateRequestQueuePurgeInterval()));
		}
		
		out.print(getDiameterSessionCleanupDetail());
		
		out.print(getSelectedPeerConfDetail());
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "In Plugins", getInPluginList()));
		out.println(format("%-30s: %s", "Out Plugins", getOutPluginList()));
		
		out.print(getRfcNaiDetail());
		
		out.print(getDiameterWebServiceConfigurationDetail());
		
		out.close();
		return stringBuffer.toString();

	}

}


