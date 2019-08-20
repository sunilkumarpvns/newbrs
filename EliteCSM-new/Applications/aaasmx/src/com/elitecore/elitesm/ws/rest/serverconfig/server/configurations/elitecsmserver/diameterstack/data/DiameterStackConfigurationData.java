package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.diameterstack.data;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.routingconf.DiameterRoutingConfBLManager;
import com.elitecore.elitesm.blmanager.diameter.sessionmanager.DiameterSessionManagerBLManager;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.elitecore.elitesm.ws.rest.validator.esi.ValidateIPPort;

@XmlType(propOrder = {"diameterStackEnabled","serviceAddress","socketReceiveBufferSize","socketSendBufferSize","maxRequestQueueSize",
		"minThreadPoolSize","maxThreadPoolSize","mainThreadPriority","workerThreadPriority","ownURI","routingTableName",
		"sessionManagerId","securityStandard","duplicateRequestDetectionEnabled","dupicateRequestQueuePurgeInterval",
		"diameterSessionCleanupDetail","securityParameters","serviceLoggerDetail","selectedPeerConfDetail","diameterPluginsDetail",
		"rfcNaiDetail","diameterWebServiceConfigurationDetail"})
@XmlRootElement(name = "diameter-stack")
@ValidObject
public class DiameterStackConfigurationData implements Validator {

	@NotEmpty(message="Diameter Stack Enabled must be specified")
	private String diameterStackEnabled;
	
	private String serviceAddress;
	private String socketReceiveBufferSize;
	private String socketSendBufferSize;

	private String maxRequestQueueSize;

	private String minThreadPoolSize;
	private String maxThreadPoolSize;
	private String mainThreadPriority;
	private String workerThreadPriority;
	private String ownURI;

	@NotEmpty(message="Routing Table Name must be specified")
	private String routingTableName;

	@NotEmpty(message="Session Manager Id must be specified")
	private String sessionManagerId;

	@NotEmpty(message="Security Standard must be specified")
	private String securityStandard;
	
	@NotEmpty(message="Duplicate Request Detection Enabled must be specified")
	private String duplicateRequestDetectionEnabled;

	private String dupicateRequestQueuePurgeInterval;

	private DiameterSessionCleanupDetailData diameterSessionCleanupDetail;

	private PeerSecurityParametersData securityParameters;

	private ServiceLoggerDetailData serviceLoggerDetail;

	private SelectedPeerConfDetailData selectedPeerConfDetail;

	private DiameterPluginsDetailData diameterPluginsDetail;

	private RFCNaiDetailData rfcNaiDetail;
	private DiameterWebServiceConfigurationDetailData diameterWebServiceConfigurationDetail;

	public DiameterStackConfigurationData() {
		this.serviceLoggerDetail = new ServiceLoggerDetailData();
		this.selectedPeerConfDetail = new SelectedPeerConfDetailData();
		this.diameterPluginsDetail = new DiameterPluginsDetailData();
		this.diameterSessionCleanupDetail = new DiameterSessionCleanupDetailData();
		this.rfcNaiDetail = new RFCNaiDetailData();
		this.diameterWebServiceConfigurationDetail = new DiameterWebServiceConfigurationDetailData();

		// default value
		this.serviceAddress = "0.0.0.0:3868";
		this.socketReceiveBufferSize = "32767";
		this.socketSendBufferSize = "32767";
		this.maxRequestQueueSize = AAAServerConstants.DEFAULT_QUEUE_SIZE + "";
		this.minThreadPoolSize = "10";
		this.maxThreadPoolSize = "20";
		this.mainThreadPriority = "1";
		this.workerThreadPriority = "1";
		this.ownURI = "aaa://localhost:3868";
		this.dupicateRequestQueuePurgeInterval = "15";
	}

	@XmlElement(name = "diameter-stack-enable")
	@Pattern(regexp = RestValidationMessages.REGEX_TRUE_FALSE, message = "Invalid Value of Diameter Stack Enable( allow only true or false)")
	public String getDiameterStackEnabled() {
		return diameterStackEnabled;
	}

	public void setDiameterStackEnabled(String isEnabled) {
		this.diameterStackEnabled = isEnabled.toLowerCase();
	}

	@XmlElement(name = "service-address")
	@ValidateIPPort(message = "Invalid Diameter Service Address")
	public String getServiceAddress() {
		return serviceAddress;
	}
	public void setServiceAddress(String serviceIpAddress) {
		this.serviceAddress = serviceIpAddress;
	}

	@XmlElement(name = "socket-receive-buffer-size")
	@Pattern(regexp = RestValidationMessages.REGEX_NUMERIC, message = "Socket Receive Buffer Size must be numeric")
	public String getSocketReceiveBufferSize() {
		return socketReceiveBufferSize;
	}
	public void setSocketReceiveBufferSize(String socketReceiveBufferSize) {
		this.socketReceiveBufferSize = socketReceiveBufferSize;
	}

	@XmlElement(name = "socket-send-buffer-size")
	@Pattern(regexp = RestValidationMessages.REGEX_NUMERIC, message = "Socket Receive Buffer Size must be numeric")
	public String getSocketSendBufferSize() {
		return socketSendBufferSize;
	}
	public void setSocketSendBufferSize(String socketSendBufferSize) {
		this.socketSendBufferSize = socketSendBufferSize;
	}

	@XmlElement(name = "queue-size")
	@Pattern(regexp = RestValidationMessages.REGEX_NUMERIC, message = "Queue size must be numeric")
	public String getMaxRequestQueueSize() {
		return maxRequestQueueSize;
	}
	public void setMaxRequestQueueSize(String maxRequestQueueSize) {
		this.maxRequestQueueSize = maxRequestQueueSize;
	}

	@XmlElement(name = "minimum-thread")
	@Pattern(regexp = RestValidationMessages.REGEX_NUMERIC, message = "Minimum Thread value must be numeric")
	public String getMinThreadPoolSize() {
		return minThreadPoolSize;
	}
	public void setMinThreadPoolSize(String minThreadPoolSize) {
		this.minThreadPoolSize = minThreadPoolSize;
	}

	@XmlElement(name = "maximum-thread")
	@Pattern(regexp = RestValidationMessages.REGEX_NUMERIC, message = "Maximum Thread value must be numeric")
	public String getMaxThreadPoolSize() {
		return maxThreadPoolSize;
	}
	public void setMaxThreadPoolSize(String maxThreadPoolSize) {
		this.maxThreadPoolSize = maxThreadPoolSize;
	}

	@XmlElement(name = "main-thread-priority")
	@Pattern(regexp = RestValidationMessages.REGEX_NUMERIC, message = "Main Thread Priority value must be numeric")
	public String getMainThreadPriority() {
		return mainThreadPriority;
	}
	public void setMainThreadPriority(String mainThreadPriority) {
		this.mainThreadPriority = mainThreadPriority;
	}

	@XmlElement(name = "worker-thread-priority")
	@Pattern(regexp = RestValidationMessages.REGEX_NUMERIC, message = "Worker Thread Priority value must be numeric")
	public String getWorkerThreadPriority() {
		return workerThreadPriority;
	}
	public void setWorkerThreadPriority(String workerThreadPriority) {
		this.workerThreadPriority = workerThreadPriority;
	}

	@XmlElement(name = "own-diameter-URI")
	public String getOwnURI() {
		return ownURI;
	}
	public void setOwnURI(String ownURI) {
		this.ownURI = ownURI;
	}

	@XmlElement(name = "routing-table")
	public String getRoutingTableName() {
		return routingTableName;
	}
	public void setRoutingTableName(String routingTableName) {
		this.routingTableName = routingTableName;

	}

	@XmlElement(name = "session-manager-id")
	public String getSessionManagerId() {
		return sessionManagerId;
	}
	public void setSessionManagerId(String sessionManagerId) {
		this.sessionManagerId = sessionManagerId;
	}

	@XmlElement(name = "security-standard")
	@Pattern(regexp = RestValidationMessages.REGEX_SECUIRTY_STANDARD, message = "Invalid value of Security Standard")
	public String getSecurityStandard() {
		return securityStandard;
	}
	public void setSecurityStandard(String securityStandard) {
		this.securityStandard = securityStandard;
	}

	@XmlElement(name = "duplicate-request-check-enable")
	@Pattern(regexp = RestValidationMessages.REGEX_TRUE_FALSE, message = "Invalid Value of Duplicate Request Detection Enable(true / false)")
	public String getDuplicateRequestDetectionEnabled() {
		return duplicateRequestDetectionEnabled;
	}
	public void setDuplicateRequestDetectionEnabled(
			String duplicateRequestDetectionEnabled) {
		this.duplicateRequestDetectionEnabled = duplicateRequestDetectionEnabled.toLowerCase();
	}

	@XmlElement(name = "duplicate-request-purge-interval")
	@Pattern(regexp = RestValidationMessages.REGEX_NUMERIC, message = "Duplicate Request Queue Purge Interval must be numeric")
	public String getDupicateRequestQueuePurgeInterval() {
		return dupicateRequestQueuePurgeInterval;
	}
	public void setDupicateRequestQueuePurgeInterval(
			String dupicateRequestQueuePurgeInterval) {
		this.dupicateRequestQueuePurgeInterval = dupicateRequestQueuePurgeInterval;
	}

	@XmlElement(name = "diameter-session-cleanup")
	@Valid
	public DiameterSessionCleanupDetailData getDiameterSessionCleanupDetail() {
		return diameterSessionCleanupDetail;
	}
	public void setDiameterSessionCleanupDetail(
			DiameterSessionCleanupDetailData diameterSessionCleanupDetail) {
		this.diameterSessionCleanupDetail = diameterSessionCleanupDetail;
	}

	@XmlElement(name = "security-parameters")
	@Valid
	public PeerSecurityParametersData getSecurityParameters() {
		return securityParameters;
	}
	public void setSecurityParameters(
			PeerSecurityParametersData securityParameters) {
		this.securityParameters = securityParameters;
	}

	@XmlElement(name = "logging")
	@Valid
	public ServiceLoggerDetailData getServiceLoggerDetail() {
		return serviceLoggerDetail;
	}
	public void setServiceLoggerDetail(ServiceLoggerDetailData serviceLoggerDetail) {
		this.serviceLoggerDetail = serviceLoggerDetail;
	}

	@XmlElement(name = "peer-list")
	@Valid
	public SelectedPeerConfDetailData getSelectedPeerConfDetail() {
		return selectedPeerConfDetail;
	}
	public void setSelectedPeerConfDetail(
			SelectedPeerConfDetailData selectedPeerConfDetail) {
		this.selectedPeerConfDetail = selectedPeerConfDetail;
	}

	@XmlElement(name = "plugin-list")
	@Valid
	@NotNull(message="Plugin List must be specified")
	public DiameterPluginsDetailData getDiameterPluginsDetail() {
		return diameterPluginsDetail;
	}
	public void setDiameterPluginsDetail(
			DiameterPluginsDetailData diameterPluginsDetail) {
		this.diameterPluginsDetail = diameterPluginsDetail;
	}

	@XmlElement(name = "rfc-5729-nai")
	@Valid
	public RFCNaiDetailData getRfcNaiDetail() {
		return rfcNaiDetail;
	}
	public void setRfcNaiDetail(RFCNaiDetailData rfcNaiDetail) {
		this.rfcNaiDetail = rfcNaiDetail;
	}

	@XmlElement(name = "diameter-webservice")
	@Valid
	public DiameterWebServiceConfigurationDetailData getDiameterWebServiceConfigurationDetail() {
		return diameterWebServiceConfigurationDetail;
	}
	public void setDiameterWebServiceConfigurationDetail(
			DiameterWebServiceConfigurationDetailData diameterWebServiceConfigurationDetail) {
		this.diameterWebServiceConfigurationDetail = diameterWebServiceConfigurationDetail;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		if (RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(routingTableName) == false && Strings.isNullOrBlank(routingTableName) == false) {
			DiameterRoutingConfBLManager diameterRoutingConfBLManager = new DiameterRoutingConfBLManager();
			try {
				DiameterRoutingTableData routingTableData = diameterRoutingConfBLManager.getDiameterRoutingTableDataByName(routingTableName.trim());
			} catch (Exception e) {
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Configured "+ routingTableName+ " Diameter Routing table does not exist");
			}
		}

		if (RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(sessionManagerId) == false && Strings.isNullOrBlank(sessionManagerId) == false) {
			DiameterSessionManagerBLManager sessionManagerBlManger = new DiameterSessionManagerBLManager();
			try {
				DiameterSessionManagerData sessionManagerData = sessionManagerBlManger.getDiameterSessionManagerDataByName(sessionManagerId.trim());
			} catch (Exception e) {
				isValid = false;
				RestUtitlity.setValidationMessage(context,"Configured "+sessionManagerId+" Session Manager does not exist");
			}
		}
		
		return isValid;
	}
}
