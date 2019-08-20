package com.elitecore.aaa.core.conf.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
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
import com.elitecore.aaa.core.config.ServiceDetails;
import com.elitecore.aaa.core.config.ServiceTypes;
import com.elitecore.aaa.core.server.axixserver.WebServiceConfiguration;
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
@XmlType(propOrder = {})
@XmlRootElement(name = "elite-rm-server")
@ConfigurationProperties(moduleName ="RM_SERVER_CONFIGURABLE",readWith = XMLReader.class, reloadWith = XMLReader.class, synchronizeKey ="ELITE_RM_SERVER")
@XMLProperties(name = "elite-rm-server", schemaDirectories = {"system","schema"}, configDirectories = {"conf"})
public class RMServerConfigurable extends Configurable{

	private String serverName;
	private String domainName;
	private ServiceTypes serviceList;
	private DataSourceDetail aaaDatasource;
	private LoggerDetail logging;
	private String snmpAddress ="0.0.0.0:1161";
	private String snmpCommunity ="public";
	private AlertListnersDetail alertListners;
	private WebServiceConfiguration webServiceConfiguration;
	private Map<String,Boolean> serviceMap;	
	private int snmpPort = 161;
	private String snmpIpAddress = "0.0.0.0";
	private boolean graphGeneration;
	private boolean userTrimming;
	private KpiServiceConfiguration kpiServiceConfiguration;

	public RMServerConfigurable(){
		serviceList = new ServiceTypes();
		aaaDatasource = new DataSourceDetail();
		logging = new LoggerDetail();
		alertListners = new AlertListnersDetail();
		webServiceConfiguration = new WebServiceConfiguration();
		serviceMap = new HashMap<String, Boolean>();
		kpiServiceConfiguration = new KpiServiceConfiguration();
	}
	
	@XmlElement(name="username-triming",type=boolean.class)
	public boolean getIsUserTrimming() {
		return userTrimming;
	}

	public void setIsUserTrimming(boolean userTrimming) {
		this.userTrimming = userTrimming;
	}

	@XmlElement(name="graph-generation",type=boolean.class)
	public boolean getIsGraphGeneration() {
		return graphGeneration;
	}

	public void setIsGraphGeneration(boolean graphGeneration) {
		this.graphGeneration = graphGeneration;
	}
	
	@XmlElement(name = "service-list")
	public ServiceTypes getServiceList() {
		return serviceList;
	}

	public void setServiceList(ServiceTypes serviceList) {
		this.serviceList = serviceList;
	}
	
	@XmlElement(name = "aaa-db-datasource")
	public DataSourceDetail getAAADatasource() {
		return aaaDatasource;
	}

	public void setAAADatasource(DataSourceDetail aaaDatasource) {
		this.aaaDatasource = aaaDatasource;
	}
	
	@Reloadable(type=LoggerDetail.class)
	@XmlElement(name = "logging")
	public LoggerDetail getLoggerDetail() {
		return logging;
	}

	public void setLoggerDetail(LoggerDetail logging) {
		this.logging = logging;
	}
	@XmlElement(name = "snmp-address", type = String.class, defaultValue = "0.0.0.0:1161")
	public String getSnmpAddress() {
		return snmpAddress;
	}

	public void setSnmpAddress(String snmpAddress) {
		this.snmpAddress = snmpAddress;
	}
	@XmlElement(name = "snmp-community",type = String.class,defaultValue ="public")
	public String getSnmpCommunity() {
		return snmpCommunity;
	}

	public void setSnmpCommunity(String snmpCommunity) {
		this.snmpCommunity = snmpCommunity;
	}

	@XmlElement(name = "alert-listeners")
	public AlertListnersDetail getAlertListners() {
		return alertListners;
	}

	public void setAlertListners(AlertListnersDetail alertListners) {
		this.alertListners = alertListners;
	}
	@XmlElement(name = "web-service-config")
	public WebServiceConfiguration getWebServiceConfiguration() {
		return webServiceConfiguration;
	}
	public void setWebServiceConfiguration(
			WebServiceConfiguration webServiceConfiguration) {
		this.webServiceConfiguration = webServiceConfiguration;
	}
	
	@XmlTransient
	public int getSnmpPort() {
		return snmpPort;
	}
	public void setSnmpPort(int snmpPort) {
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
	public Map<String, Boolean> getServiceMap() {
		return serviceMap;
	}
	public void setServiceMap(Map<String, Boolean> serviceMap) {
		this.serviceMap = serviceMap;
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
	
	@PostRead
	public void postReadProcessing() throws DatabaseInitializationException, DatabaseTypeNotSupportedException {
		
		if (serverName == null || serverName.trim().length() == 0) {
			try {
				serverName = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {

			}
		}
		
		URLData address;
		try {
			address = URLParser.parse(this.snmpAddress);
			this.snmpIpAddress = address.getHost();
			this.snmpPort = address.getPort();
		} catch (InvalidURLException e1) {

		}
		Map<String,Boolean> serviceMap = new HashMap<String, Boolean>();
		for(ServiceDetails services : serviceList.getService()){
			serviceMap.put(services.getServiceId(), services.getEnabled());
		}
		this.serviceMap = serviceMap;
		
		String webServiceAddress = this.webServiceConfiguration.getServiceAddress();
		if(webServiceAddress !=null){

			URLData webServiceaddress;
			try {
				webServiceaddress = URLParser.parse(webServiceAddress);
				webServiceConfiguration.setIpAddress(webServiceaddress.getHost());
				webServiceConfiguration.setPort(webServiceaddress.getPort());
			} catch (InvalidURLException e) {

			} 		
			 		
		}
		String httpsWebServiceAddress = this.webServiceConfiguration.getHttpsServiceAddress();
		if(httpsWebServiceAddress !=null && httpsWebServiceAddress.trim().length()>0){

			URLData webServiceaddress;
			try {
				webServiceaddress = URLParser.parse(httpsWebServiceAddress);
				webServiceConfiguration.setHttpsIPAddress(webServiceaddress.getHost());
				webServiceConfiguration.setHttpsPort(webServiceaddress.getPort());

			} catch (InvalidURLException e) {

			} 		
		}

		if(webServiceConfiguration.getThreadPoolSize() >= 5){
			webServiceConfiguration.setThreadPoolSize(webServiceConfiguration.getThreadPoolSize());
		}
		if(((AAAConfigurationContext)getConfigurationContext()).state()==AAAConfigurationState.NORMAL){
			initDBConnectionManager();
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
		
	}
	
	@XmlTransient
	public KpiServiceConfiguration getKpiServiceConfiguration() {
		return kpiServiceConfiguration;
	}

	public void setKpiServiceConfiguration(KpiServiceConfiguration kpiServiceConfiguration) {
		this.kpiServiceConfiguration = kpiServiceConfiguration;
	}
}




