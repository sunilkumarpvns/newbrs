package com.elitecore.aaa.core.conf.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.conf.context.AAAConfigurationState;
import com.elitecore.aaa.core.config.AlertListnersDetail;
import com.elitecore.aaa.core.config.DataSourceDetail;
import com.elitecore.aaa.core.config.LoggerDetail;
import com.elitecore.aaa.core.config.RFCNaiDetail;
import com.elitecore.aaa.core.config.RadiusSessionCleanupDetail;
import com.elitecore.aaa.core.config.ServiceDetails;
import com.elitecore.aaa.core.config.ServiceTypes;
import com.elitecore.aaa.core.server.axixserver.WebServiceConfiguration;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.Reloadable;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;
import static com.elitecore.aaa.core.config.RadiusSessionCleanupDetail.MAX_SESSION_CLEANUP_INTERVAL;
import static com.elitecore.aaa.core.config.RadiusSessionCleanupDetail.MIN_SESSION_CLEANUP_INTERVAL;
import static com.elitecore.aaa.core.config.RadiusSessionCleanupDetail.MAX_SESSION_TIMEOUT;

@XmlType(propOrder = {})
@XmlRootElement(name = "elite-aaa-server")
@ConfigurationProperties(moduleName ="AAA_SERVER_CONFIGURABLE",readWith = XMLReader.class, reloadWith = XMLReader.class, synchronizeKey ="ELITE_AAA_SERVER")
@XMLProperties(name = "elite-aaa-server", schemaDirectories = {"system","schema"}, configDirectories = {"conf"})
public class AAAServerConfigurable extends Configurable {
	
	private static final String MODULE = "AAA-SERVER-CONFIGURABLE"; 
	
	private String serverName;
	private String domainName;
	private int snmpPort = 161;
	private String snmpIpAddress = "0.0.0.0";
	private List<String> realmNamesList;
	private Map<String,Boolean> configuredServices;	
	private String snmpAddress = "0.0.0.0:1161";
	private String snmpCommunity ="public";
	private WebServiceConfiguration webServiceConfiguration;
	private ServiceTypes serviceTypes;
	private DataSourceDetail aaaDatasourceDetail;
	private LoggerDetail loggingDetail;
	private AlertListnersDetail alertListnersDetail;
	private RFCNaiDetail rfcNaiDetail;
	private int internalSchedulerMaxThread = 15;
	private KpiServiceConfiguration kpiServiceConfiguration;
	private RadiusSessionCleanupDetail radiusSessionCleanupDetail;
	
	public AAAServerConfigurable(){
		realmNamesList = new ArrayList<String>();
		configuredServices = new HashMap<String, Boolean>();
		webServiceConfiguration = new WebServiceConfiguration();
		serviceTypes = new ServiceTypes();
		aaaDatasourceDetail = new DataSourceDetail();
		loggingDetail = new LoggerDetail();
		alertListnersDetail = new AlertListnersDetail();
		rfcNaiDetail = new RFCNaiDetail();
		kpiServiceConfiguration = new KpiServiceConfiguration();
		radiusSessionCleanupDetail = new RadiusSessionCleanupDetail();
	}
	
	
	@XmlTransient
	public int getSNMPPort() {
		return snmpPort;
	}
	public void setSNMPPort(int snmpPort) {
		this.snmpPort = snmpPort;
	}
	@XmlTransient
	public String getSnmpIpAddress() {
		return snmpIpAddress;
	}
	public void setSnmpIpAddress(String snmpIpAddress) {
		this.snmpIpAddress = snmpIpAddress;
	}
	
	@XmlTransient
	public List<String> getRealmNames() {
		return realmNamesList;
	}
	public void setRealmNames(List<String> realmNamesList) {
		this.realmNamesList = realmNamesList;
	}

	@XmlTransient
	public Map<String, Boolean> getconfiguredServicesMap() {
		return configuredServices;
	}
	public void setconfiguredServicesMap(Map<String, Boolean> configuredServices) {
		this.configuredServices = configuredServices;
	}

	@XmlElement(name = "web-service-config")
	public WebServiceConfiguration getWebServiceConfiguration() {
		return webServiceConfiguration;
	}
	public void setWebServiceConfiguration(WebServiceConfiguration webServiceConfiguration) {
		this.webServiceConfiguration = webServiceConfiguration;
	}

	@XmlElement(name = "service-list")
	public ServiceTypes getServiceList() {
		return serviceTypes;
	}

	public void setServiceList(ServiceTypes serviceList) {
		this.serviceTypes = serviceList;
	}

	@XmlElement(name = "aaa-db-datasource")
	public DataSourceDetail getAAADatasource() {
		return aaaDatasourceDetail;
	}

	public void setAAADatasource(DataSourceDetail aaaDatasource) {
		this.aaaDatasourceDetail = aaaDatasource;
	}
	
	@Reloadable(type=LoggerDetail.class)
	@XmlElement(name = "logging")
	public LoggerDetail getLoggerDetail() {
		return loggingDetail;
	}

	public void setLoggerDetail(LoggerDetail logging) {
		this.loggingDetail = logging;
	}

	@XmlElement(name = "snmp-address", type = String.class, defaultValue = "0.0.0.0:1161")
	public String getSnmpAddress() {
		return snmpAddress;
	}

	public void setSnmpAddress(String snmpAddress) {
		this.snmpAddress = snmpAddress;
	}
	@XmlElement(name = "snmp-community",type = String.class,defaultValue ="public")
	public String getSNMPCommunity() {
		return snmpCommunity;
	}

	public void setSNMPCommunity(String snmpCommunity) {
		this.snmpCommunity = snmpCommunity;
	}

	@XmlElement(name = "internal-schedular-thread-size",type = Integer.class,defaultValue ="15")
	public int getInternalSchedulerMaxThread() {
		return internalSchedulerMaxThread;
	}

	public void setInternalSchedulerMaxThread(int internalSchedulerMaxThread) {
		this.internalSchedulerMaxThread = internalSchedulerMaxThread;
	}

	@XmlElement(name = "alert-listeners")
	public AlertListnersDetail getAlertListners() {
		return alertListnersDetail;
	}

	public void setAlertListners(AlertListnersDetail alertListners) {
		this.alertListnersDetail = alertListners;
	}

	@XmlElement(name = "rfc-4282-nai")
	public RFCNaiDetail getRfcNai() {
		return rfcNaiDetail;
	}

	public void setRfcNai(RFCNaiDetail rfcNai) {
		this.rfcNaiDetail = rfcNai;
	}

	@XmlElement(name = "server-name",type = String.class)
	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	@XmlElement(name = "domain-name",type = String.class)
	public String getDomainName() {
		return domainName;
	}
	
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	@XmlElement(name = "radius-session-cleanup",type = RadiusSessionCleanupDetail.class)
	public RadiusSessionCleanupDetail getRadiusSessionCleanupDetail() {
		return radiusSessionCleanupDetail;
	}


	public void setRadiusSessionCleanupDetail(RadiusSessionCleanupDetail radiusSessionCleanupDetail) {
		this.radiusSessionCleanupDetail = radiusSessionCleanupDetail;
	}


	@PostRead
	public void postReadProcessing() throws Exception {
		
		if (serverName == null || serverName.trim().length() == 0) {
			try {
				serverName = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {

			}
		}
		
		if(this.snmpAddress != null){
			URLData address;
			try {
				address = URLParser.parse(this.snmpAddress);
				this.snmpIpAddress = address.getHost();
				this.snmpPort = address.getPort();
			} catch (InvalidURLException e) {

			}		
		}

		String realmNames = rfcNaiDetail.getRealmName();
		if(realmNames !=null){
			String[] realms = realmNames.split(",");
			for(String realmName : realms){
				if(realmName != null && realmName.trim().length() > 0){
					this.realmNamesList.add(realmName);
				}
			}
		}

		Map<String,Boolean> serviceMap = new HashMap<String, Boolean>();
		for(ServiceDetails services : serviceTypes.getService()){
			serviceMap.put(services.getServiceId(), services.getEnabled());
		}
		this.configuredServices = serviceMap;

		String webServiceAddress = this.webServiceConfiguration.getServiceAddress();
		if(webServiceAddress !=null){

			URLData address;
			try {
				address = URLParser.parse(webServiceAddress);
				webServiceConfiguration.setIpAddress(address.getHost());
				webServiceConfiguration.setPort(address.getPort());

			} catch (InvalidURLException e) {

			} 		
		}
		String httpsWebServiceAddress = this.webServiceConfiguration.getHttpsServiceAddress();
		if(httpsWebServiceAddress !=null && httpsWebServiceAddress.trim().length()>0){

			URLData address;
			try {
				address = URLParser.parse(httpsWebServiceAddress);
				webServiceConfiguration.setHttpsIPAddress(address.getHost());
				webServiceConfiguration.setHttpsPort(address.getPort());

			} catch (InvalidURLException e) {

			} 		
		}
		if(webServiceConfiguration.getThreadPoolSize() >= 5){
			webServiceConfiguration.setThreadPoolSize(webServiceConfiguration.getThreadPoolSize());
		}
		
		
		//initializing the DB Connection Manager
		// In case of FALLBACK state, Server should not initialize DBConnectionManager. 
		if(((AAAConfigurationContext)getConfigurationContext()).state()==AAAConfigurationState.NORMAL){
			initDBConnectionManager();
		}	
		
		setValidInternalSchedularThreadSize();
		
		postRadiusSessionCleanUpConfiguration();
	}
	
	private void setValidInternalSchedularThreadSize() {
		if(this.internalSchedulerMaxThread<15){
			this.internalSchedulerMaxThread = 15;
		}
	}


	private void initDBConnectionManager() throws DatabaseInitializationException, DatabaseTypeNotSupportedException {
		EliteAAADBConnectionManager.getInstance().init(getAAADatasource(), ((AAAConfigurationContext)getConfigurationContext()).getServerContext().getTaskScheduler());
	}

	@PostWrite
	public void postWriteProcessing(){
		//This method is blank right now
	}
	
	@PostReload
	public void postReloadProcessing(){
		setValidInternalSchedularThreadSize();
	}

	@XmlElement(name = "kpi-service-config")
	public KpiServiceConfiguration getKpiServiceConfiguration() {
		return kpiServiceConfiguration;
	}

	public void setKpiServiceConfiguration(KpiServiceConfiguration kpiServiceConfiguration) {
		this.kpiServiceConfiguration = kpiServiceConfiguration;
	}


	public boolean isServiceEnabled(String serviceId) {
		boolean bIsServiceEnabled = false;
		if(serviceId != null && getconfiguredServicesMap().get(serviceId)!=null) {
			bIsServiceEnabled = getconfiguredServicesMap().get(serviceId);
		}
		return bIsServiceEnabled;
	}
	
	private void postRadiusSessionCleanUpConfiguration(){
		
		long sessionCleanupInterval = radiusSessionCleanupDetail.getSessionCleanupInterval();
		if (sessionCleanupInterval > 0 && sessionCleanupInterval < MIN_SESSION_CLEANUP_INTERVAL) {
			LogManager.getLogger().warn(MODULE, "Session cleanup interval: " +sessionCleanupInterval + " is less than " + MIN_SESSION_CLEANUP_INTERVAL + " seconds. Interval less than " + MIN_SESSION_CLEANUP_INTERVAL + " seconds is not allowed. Session cleanup interval set to " + MIN_SESSION_CLEANUP_INTERVAL);
			LogManager.getLogger().warn(MODULE, "To disable session cleanup configure it to -1.");
			sessionCleanupInterval = MIN_SESSION_CLEANUP_INTERVAL;
		}
		if (sessionCleanupInterval > MAX_SESSION_CLEANUP_INTERVAL){
			LogManager.getLogger().warn(MODULE, "Session cleanup interval: " +sessionCleanupInterval + " is more than " + MAX_SESSION_CLEANUP_INTERVAL + " seconds. Interval more than " + MAX_SESSION_CLEANUP_INTERVAL + " seconds is not allowed. Session cleanup interval set to " + MAX_SESSION_CLEANUP_INTERVAL);
			sessionCleanupInterval = MAX_SESSION_CLEANUP_INTERVAL;
		}
		radiusSessionCleanupDetail.setSessionCleanupInterval(sessionCleanupInterval);
		
		long sessionTimeOut = radiusSessionCleanupDetail.getSessionTimeOut();
		if (sessionTimeOut > MAX_SESSION_TIMEOUT){
			LogManager.getLogger().warn(MODULE, "Session Timeout: " +sessionTimeOut + " is more than " + MAX_SESSION_TIMEOUT + " seconds. Session Timeout more than " + MAX_SESSION_TIMEOUT + " seconds is not allowed. Session Timeout set to " + MAX_SESSION_TIMEOUT);
			sessionTimeOut = MAX_SESSION_TIMEOUT;
		}
		radiusSessionCleanupDetail.setSessionTimeOut(sessionTimeOut);
	}
}
