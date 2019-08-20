package com.elitecore.elitesm.ws.rest.serverconfig.server.services.rmippool;


import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.ServiceRestUtility;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.data.PluginsDetail;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.data.ServiceLoggerDetail;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.elitecore.elitesm.ws.rest.validator.esi.ValidateIPPort;

@XmlRootElement(name = "ippool-service")
@XmlAccessorType()
@XmlType(propOrder = {"serviceAddress", "socketReceiveBufferSize", "socketSendBufferSize", "queueSize", "minimumThread", 
		"maximumThread", "mainThreadPriority", "workerThreadPriority", "duplicateRequestCheckEnabled" ,"duplicateRequestQueuePurgeInterval",
		"logger", "pluginsDetails", "autoSessionCloserDetail", "defaultIpPoolName", "dbQueryTimeOut", "dataSourceName", "dbScanTime", "operationFailureEnabled" })
@ValidObject
public class RMIpPoolServiceData implements Validator  {
	
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
	
	private AutoSessionCloserDetail autoSessionCloserDetail;
	
	private String defaultIpPoolName;
	
	@Pattern(regexp = RestValidationMessages.PARAMETER_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "DB Query Timeout")
	private String dbQueryTimeOut;
	
	
	private  String dataSourceName;
	
	@Pattern(regexp = RestValidationMessages.PARAMETER_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Db Scantime")
	private String dbScanTime="1000";
	
	@Pattern(regexp = RestValidationMessages.TRUE_FALSE_WITH_EMPTY, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Operation Failure Enabled. It can be true or false." )
	@NotEmpty(message = "Operation Failure Enabled must be specified. It can be true or false.")
	private String operationFailureEnabled;
	
	public RMIpPoolServiceData(){
		
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
	}
	
	@XmlElement(name ="plugin-list")
	public PluginsDetail getPluginsDetails() {
		return pluginsDetails;
	}

	public void setPluginsDetails(PluginsDetail pluginsDetails) {
		this.pluginsDetails = pluginsDetails;
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
	
	@XmlElement(name ="auto-session-closure")
	public AutoSessionCloserDetail getAutoSessionCloserDetail() {
		return autoSessionCloserDetail;
	}

	public void setAutoSessionCloserDetail(
			AutoSessionCloserDetail autoSessionCloserDetail) {
		this.autoSessionCloserDetail = autoSessionCloserDetail;
	}
	
	@XmlElement(name = "db-query-timeout")
	public String getDbQueryTimeOut() {
		return dbQueryTimeOut;
	}

	public void setDbQueryTimeOut(String dbQueryTimeOut) {
		this.dbQueryTimeOut = dbQueryTimeOut;
	}
	
	@XmlElement(name = "default-ippool-name")
	public String getDefaultIpPoolName() {
		return defaultIpPoolName;
	}

	public void setDefaultIpPoolName(String defaultIpPoolName) {
		this.defaultIpPoolName = defaultIpPoolName;
	}
	
	@XmlElement(name = "db-scantime")
	public String getDbScanTime() {
		return dbScanTime;
	}

	public void setDbScanTime(String dbScanTime) {
		this.dbScanTime = dbScanTime;
	}


	@XmlElement(name = "datasource-name")
	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	
	@XmlElement(name = "operation-failure-enabled")
	public String getOperationFailureEnabled() {
		return operationFailureEnabled;
	}

	public void setOperationFailureEnabled(String operationFailureEnabled) {
		this.operationFailureEnabled = operationFailureEnabled.toLowerCase();
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		Boolean isValid = true;
		
		DatabaseDSBLManager dataManager = new DatabaseDSBLManager();
		try {
			DatabaseDSData databaseDSData = (DatabaseDSData) dataManager.getDatabaseDSDataByName(dataSourceName);
		} catch (DataManagerException e) {
			RestUtitlity.setValidationMessage(context, "Given Data source name does not exist");
			isValid = false;
			e.printStackTrace();
		}
		
		isValid = ServiceRestUtility.validateRadiusPlugin(context, this.pluginsDetails, ServiceRestUtility.RM_IPPOOL_SERVICE, isValid);
		return isValid;
	}

}