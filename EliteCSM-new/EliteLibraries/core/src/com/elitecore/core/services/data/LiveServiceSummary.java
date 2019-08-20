package com.elitecore.core.services.data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class LiveServiceSummary implements Serializable {

	private static final long serialVersionUID = 1L;
	private String instanceName;
	private String instanceId;
	private String serviceType;
	private String enabled;
	private Date serviceStartupTime;
	private Date nextExecutionTime;
	private String socketAddress;
	private boolean isInnerService;
	
	private String status;
	
	private Map<String, String> basicDetails;
	
	private Map<String, String> threadSummary;
	
	private String remarks;

	public Map<String, String> getBasicDetails() {
		return basicDetails;
	}

	public void setBasicDetails(Map<String, String> basicDetails) {
		this.basicDetails = basicDetails;
	}

	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled;
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

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Map<String, String> getThreadSummary() {
		return threadSummary;
	}

	public void setThreadSummary(Map<String, String> threadSummary) {
		this.threadSummary = threadSummary;
	}

	public Date getNextExecutionTime() {
		return nextExecutionTime;
	}

	public void setNextExecutionTime(Date nextExecutionTime) {
		this.nextExecutionTime = nextExecutionTime;
	}

	public Date getServiceStartupTime() {
		return serviceStartupTime;
	}

	public void setServiceStartupTime(Date serviceStartupTime) {
		this.serviceStartupTime = serviceStartupTime;
	}

	public String getSocketAddress() {
		return socketAddress;
	}

	public void setSocketAddress(String socketAddress) {
		this.socketAddress = socketAddress;
	}

	public boolean isInnerService(){
		return isInnerService;
	}
	public void setIsInnerService(boolean isInnerService){
		this.isInnerService = isInnerService;
	}
	
	public void setRemarks(String remarks){
		this.remarks=remarks;
	}
	
	public String getRemarks(){
		return this.remarks;
	}
}
