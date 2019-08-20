package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.resourcemanager.rmserver.data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.eliteaaaserver.data.AlertListnersDetailData;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.eliteaaaserver.data.DataSourceDetailData;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.eliteaaaserver.data.KpiServiceConfigurationData;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.eliteaaaserver.data.LoggerDetailData;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.eliteaaaserver.data.ServiceTypeData;
import com.elitecore.elitesm.ws.rest.validator.esi.ValidateIPPort;

@XmlType(propOrder = {"serverName","domainName","serviceTypes","aaaDatasourceDetail","loggerDetail","snmpAddress","snmpCommunity","internalSchedulerMaxThread",
		"filterResponseAttributes","alertListnersDetail","webServiceConfiguration","kpiServiceConfiguration"})
@XmlRootElement(name = "elite-rm-server")
public class RMServerConfigurationData{

	@NotEmpty(message="Server Name must be specified")
	private String serverName;
	
	@NotEmpty(message="Domain Name must be specified")
	private String domainName;
	private String snmpAddress;
	private String snmpCommunity;
	private WebServiceConfigurationData webServiceConfiguration;
	private ServiceTypeData serviceTypes;
	@NotNull(message="AAA Datasource Detail must be specified")
	private DataSourceDetailData aaaDatasourceDetail;
	private LoggerDetailData loggerDetail;
	private AlertListnersDetailData alertListnersDetail;
	private String internalSchedulerMaxThread ;
	private KpiServiceConfigurationData kpiServiceConfiguration;
	private String filterResponseAttributes;
	
	public RMServerConfigurationData(){
		webServiceConfiguration = new WebServiceConfigurationData();
		serviceTypes = new ServiceTypeData();
		aaaDatasourceDetail = new DataSourceDetailData();
		loggerDetail = new LoggerDetailData();
		alertListnersDetail = new AlertListnersDetailData();
		kpiServiceConfiguration = new KpiServiceConfigurationData();
		internalSchedulerMaxThread = "15";
		snmpCommunity ="public";
		snmpAddress = "0.0.0.0:1161";
	}

	@XmlElement(name = "web-service-config")
	@Valid
	public WebServiceConfigurationData getWebServiceConfiguration() {
		return webServiceConfiguration;
	}
	public void setWebServiceConfiguration(WebServiceConfigurationData webServiceConfiguration) {
		this.webServiceConfiguration = webServiceConfiguration;
	}

	@XmlElement(name = "service-list")
	@Valid
	public ServiceTypeData getServiceTypes() {
		return serviceTypes;
	}

	public void setServiceTypes(ServiceTypeData serviceList) {
		this.serviceTypes = serviceList;
	}

	@XmlElement(name = "aaa-db-datasource")
	public DataSourceDetailData getAaaDatasourceDetail() {
		return aaaDatasourceDetail;
	}

	public void setAaaDatasourceDetail(DataSourceDetailData aaaDatasource) {
		this.aaaDatasourceDetail = aaaDatasource;
	}
	
	@XmlElement(name = "logging")
	@Valid
	public LoggerDetailData getLoggerDetail() {
		return loggerDetail;
	}
	public void setLoggerDetail(LoggerDetailData logging) {
		this.loggerDetail = logging;
	}

	@XmlElement(name = "snmp-address")
	@ValidateIPPort(message="Invalid SNMP Address")
	public String getSnmpAddress() {
		return snmpAddress;
	}
	public void setSnmpAddress(String snmpAddress) {
		this.snmpAddress = snmpAddress;
	}
	
	@XmlElement(name = "snmp-community")
	public String getSnmpCommunity() {
		return snmpCommunity;
	}
	public void setSnmpCommunity(String snmpCommunity) {
		this.snmpCommunity = snmpCommunity;
	}

	@XmlElement(name = "internal-schedular-thread-size")
	@Pattern(regexp=RestValidationMessages.REGEX_NUMERIC_POSITIVE,message="Internal Scheduler Thread Size must be numeric")
	public String getInternalSchedulerMaxThread() {
		return internalSchedulerMaxThread;
	}

	public void setInternalSchedulerMaxThread(String internalSchedulerMaxThread) {
		this.internalSchedulerMaxThread = internalSchedulerMaxThread;
	}

	@XmlElement(name = "alert-listeners")
	@Valid
	public AlertListnersDetailData getAlertListnersDetail() {
		return alertListnersDetail;
	}

	public void setAlertListnersDetail(AlertListnersDetailData alertListners) {
		this.alertListnersDetail = alertListners;
	}

	@XmlElement(name = "server-name")
	
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	@XmlElement(name = "domain-name")
	public String getDomainName() {
		return domainName;
	}
	
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	@XmlElement(name = "kpi-service-config")
	@Valid
	public KpiServiceConfigurationData getKpiServiceConfiguration() {
		return kpiServiceConfiguration;
	}

	public void setKpiServiceConfiguration(KpiServiceConfigurationData kpiServiceConfiguration) {
		this.kpiServiceConfiguration = kpiServiceConfiguration;
	}
	
	@XmlElement(name="filter-response-attributes")
	public String getFilterResponseAttributes() {
		return filterResponseAttributes;
	}
	public void setFilterResponseAttributes(String filterResponseAttributes) {
		this.filterResponseAttributes = filterResponseAttributes;
	}
}
