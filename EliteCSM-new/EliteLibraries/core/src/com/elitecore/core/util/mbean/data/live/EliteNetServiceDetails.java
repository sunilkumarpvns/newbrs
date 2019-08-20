package com.elitecore.core.util.mbean.data.live;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

public class EliteNetServiceDetails implements Serializable {
	
	private String instanceName;	
	private String instanceId;	
	private String status;
	private String serviceType;
	private String enabled;
	private Timestamp serviceStartupTime;
	private Timestamp nextExecutionTime;
	private Map<String, String> basicDetails;  
	private Map<String, String> threadSummary;	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	public Map getBasicDetails() {
		return basicDetails;
	}
	public void setBasicDetails(Map basicDetails) {
		this.basicDetails = basicDetails;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public Map getThreadSummary() {
		return threadSummary;
	}
	public void setThreadSummary(Map threadSummary) {
		this.threadSummary = threadSummary;
	}
	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}
	public Timestamp getNextExecutionTime() {
		return nextExecutionTime;
	}
	public void setNextExecutionTime(Timestamp nextExecutionTime) {
		this.nextExecutionTime = nextExecutionTime;
	}
	public Timestamp getServiceStartupTime() {
		return serviceStartupTime;
	}
	public void setServiceStartupTime(Timestamp serviceStartupTime) {
		this.serviceStartupTime = serviceStartupTime;
	}
	
}
