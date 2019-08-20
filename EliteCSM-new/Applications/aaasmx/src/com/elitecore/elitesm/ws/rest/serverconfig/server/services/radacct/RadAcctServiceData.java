package com.elitecore.elitesm.ws.rest.serverconfig.server.services.radacct;


import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadServicePolicyData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.ServiceRestUtility;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.data.PluginsDetail;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.data.ServiceLoggerDetail;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.elitecore.elitesm.ws.rest.validator.esi.ValidateIPPort;

@XmlRootElement(name = "acct-service")
@XmlAccessorType()
@XmlType(propOrder = {"serviceAddress", "socketReceiveBufferSize", "socketSendBufferSize", "queueSize", "minimumThread", 
		"maximumThread", "mainThreadPriority", "workerThreadPriority", "duplicateRequestCheckEnabled" ,"duplicateRequestQueuePurgeInterval",
		"servicePolicies", "logger", "pluginsDetails", "rrdDetail" })
@ValidObject
public class RadAcctServiceData implements Validator  {
	
	private static final String RAD_ACCT_SERVICE_POLICY = "Radius Accounting Service Policy";
	
	@Pattern(regexp = RestValidationMessages.PARAMETER_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Socket Receive Buffer Size.")
	private String socketReceiveBufferSize;
	
	@Pattern(regexp = RestValidationMessages.PARAMETER_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Socket Send Buffer Size.")
	private String socketSendBufferSize;
	
	@Pattern(regexp = RestValidationMessages.PARAMETER_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Queue Size.")
	private String queueSize;

	@Pattern(regexp = RestValidationMessages.PARAMETER_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Minimum Thread.")
	private String minimumThread;
	
	@Pattern(regexp = RestValidationMessages.PARAMETER_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Maximum Thread.")
	private String maximumThread;
	
	@Pattern(regexp = RestValidationMessages.PARAMETER_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Main Thread Priority.")
	private String mainThreadPriority;
	
	@Pattern(regexp = RestValidationMessages.PARAMETER_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Worker Thread Priority.")
	private String workerThreadPriority;
	
	@NotEmpty(message = "Duplicate Request Check Enabled must be specified. It can be true or false.")
	@Pattern(regexp = RestValidationMessages.TRUE_FALSE_WITH_EMPTY, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Duplicate Request Check Enabled. It can be true or false." )
	private String duplicateRequestCheckEnabled;
	
	@Pattern(regexp = RestValidationMessages.PARAMETER_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Duplicate Request Purge Interval.")
	private String duplicateRequestQueuePurgeInterval;
	
	@ValidateIPPort(message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Service Address.")
	private String serviceAddress;
	
	@Valid
	private PluginsDetail pluginsDetails;
	
	@Valid
	private ServiceLoggerDetail logger;
	
	private RRDDetail rrdDetail;
	
	@Size(min = 1, message = "Alteast one Service Policy must be specified")
	private List<String> servicePolicies;
	
	public RadAcctServiceData(){
		
		setSocketReceiveBufferSize("-1");
		setSocketSendBufferSize("-1");
		setQueueSize(AAAServerConstants.DEFAULT_QUEUE_SIZE + "");
		setMinimumThread("5");
		setMaximumThread("10");
		setMainThreadPriority("7");
		setWorkerThreadPriority("7");
		setDuplicateRequestQueuePurgeInterval(AAAServerConstants.DEFAULT_DUPLICATE_REQUEST_QUEUE_PURGE_INTERVAL + "");
		setServiceAddress("0.0.0.0:1813");
		
		
		logger = new ServiceLoggerDetail();
		pluginsDetails = new PluginsDetail();
		rrdDetail = new RRDDetail();
		this.servicePolicies = new ArrayList<String>();
	}
	
	@XmlElement(name ="plugin-list")
	public PluginsDetail getPluginsDetails() {
		return pluginsDetails;
	}

	public void setPluginsDetails(PluginsDetail pluginsDetails) {
		this.pluginsDetails = pluginsDetails;
	}

	@XmlElementWrapper(name="service-policies")
	@XmlElement(name="service-policy",type=String.class)
	public List<String> getServicePolicies() {
		return servicePolicies;
	}

	public void setServicePolicies(List<String> servicePolicies) {
		this.servicePolicies = servicePolicies;
	}

	@XmlElement(name = "service-address",type = String.class)
	public String getServiceAddress() {
		return serviceAddress;
	}
	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}
	
	@XmlElement(name = "socket-receive-buffer-size",type = String.class)
	public String getSocketReceiveBufferSize() {
		return socketReceiveBufferSize;
	}
	public void setSocketReceiveBufferSize(String socketReceiveBufferSize) {
		this.socketReceiveBufferSize = socketReceiveBufferSize;
	}
	
	@XmlElement(name = "socket-send-buffer-size",type = String.class)
	public String getSocketSendBufferSize() {
		return socketSendBufferSize;
	}
	public void setSocketSendBufferSize(String socketSendBufferSize) {
		this.socketSendBufferSize = socketSendBufferSize;
	}
	
	@XmlElement(name = "queue-size", type = String.class)
	public String getQueueSize() {
		return queueSize;
	}
	public void setQueueSize(String queueSize) {
		this.queueSize = queueSize;
	}
	
	@XmlElement(name = "minimum-thread",type = String.class)
	public String getMinimumThread() {
		return minimumThread;
	}
	public void setMinimumThread(String minimumThread) {
		this.minimumThread = minimumThread;
	}
	
	@XmlElement(name = "maximum-thread",type = String.class)
	public String getMaximumThread() {
		return maximumThread;
	}
	public void setMaximumThread(String maximumThread) {
		this.maximumThread = maximumThread;
	}
	
	@XmlElement(name = "main-thread-priority",type = String.class)
	public String getMainThreadPriority() {
		return mainThreadPriority;
	}
	public void setMainThreadPriority(String mainThreadPriority) {
		this.mainThreadPriority = mainThreadPriority;
	}
	
	@XmlElement(name = "worker-thread-priority",type = String.class)
	public String getWorkerThreadPriority() {
		return workerThreadPriority;
	}
	public void setWorkerThreadPriority(String workerThreadPriority) {
		this.workerThreadPriority = workerThreadPriority;
	}
	
	@XmlElement(name = "duplicate-request-check-enabled",type = String.class)
	public String getDuplicateRequestCheckEnabled() {
		return duplicateRequestCheckEnabled;
	}
	public void setDuplicateRequestCheckEnabled(String duplicateRequestCheckEnabled) {
		this.duplicateRequestCheckEnabled = duplicateRequestCheckEnabled.toLowerCase();
	}
	
	@XmlElement(name = "duplicate-request-purge-interval",type = String.class)
	public String getDuplicateRequestQueuePurgeInterval() {
		return duplicateRequestQueuePurgeInterval;
	}
	public void setDuplicateRequestQueuePurgeInterval(String duplicateRequestQueuePurgeInterval) {
		this.duplicateRequestQueuePurgeInterval = duplicateRequestQueuePurgeInterval;
	}

	@XmlElement(name = "logging")
	public ServiceLoggerDetail getLogger() {
		return logger;
	}
	public void setLogger(ServiceLoggerDetail logger) {
		this.logger = logger;
	}
	
	@XmlElement(name = "rrd")
	public RRDDetail getRrdDetail() {
		return rrdDetail;
	}
	public void setRrdDetail(RRDDetail rrdDetailsList ){
		this.rrdDetail = rrdDetailsList;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		
		Boolean isValid = true;
		 
		isValid = ServiceRestUtility.validateRadiusService(context, this.servicePolicies, this.pluginsDetails, RadServicePolicyData.class, ServiceRestUtility.RAD_ACCT, RAD_ACCT_SERVICE_POLICY, isValid );
		
		return isValid;
		
	}
}