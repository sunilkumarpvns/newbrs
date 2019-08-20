package com.elitecore.elitesm.web.externalsystem.forms;

import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateESIInstanceForm extends BaseWebForm{
	
	private String esiInstanceId;
	
	private String name;
	private String description;
	private String address;

	private String sharedSecret;
	private String esiTypeName;
	private List esiTypeList;
	private Integer minLocalPort;
	private Long expiredRequestLimitCount;
	private Long retryLimit=1L;
	private Long statusCheckDuration=120L;
	private Long timeout=1000L;
	private String realmNames;
	private String supportedAttribute;
	private String unSupportedAttribute;
	private Long statusCheckMethod;
	private String packetBytes;
	private String auditUId;
	
	public String getRealmNames() {
		return realmNames;
	}
	public void setRealmNames(String realmNames) {
		this.realmNames = realmNames;
	}
	public String getEsiInstanceId() {
		return esiInstanceId;
	}
	public void setEsiInstanceId(String esiInstanceId) {
		this.esiInstanceId = esiInstanceId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Integer getMinLocalPort() {
		return minLocalPort;
	}
	public void setMinLocalPort(Integer minLocalPort) {
		this.minLocalPort = minLocalPort;
	}
	public String getSharedSecret() {
		return sharedSecret;
	}
	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}
	public String getEsiTypeName() {
		return esiTypeName;
	}
	public void setEsiTypeName(String esiTypeName) {
		this.esiTypeName = esiTypeName;
	}

	public Long getTimeout() {
		return timeout;
	}
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}

	public Long getExpiredRequestLimitCount() {
		return expiredRequestLimitCount;
	}
	public void setExpiredRequestLimitCount(Long expiredRequestLimitCount) {
		this.expiredRequestLimitCount = expiredRequestLimitCount;
	}
	public Long getRetryLimit() {
		return retryLimit;
	}
	public void setRetryLimit(Long retryLimit) {
		this.retryLimit = retryLimit;
	}
	public Long getStatusCheckDuration() {
		return statusCheckDuration;
	}
	public void setStatusCheckDuration(Long statusCheckDuration) {
		this.statusCheckDuration = statusCheckDuration;
	}
	public String getSupportedAttribute() {
		return supportedAttribute;
	}
	public void setSupportedAttribute(String supportedAttribute) {
		this.supportedAttribute = supportedAttribute;
	}
	public String getUnSupportedAttribute() {
		return unSupportedAttribute;
	}
	public void setUnSupportedAttribute(String unSupportedAttribute) {
		this.unSupportedAttribute = unSupportedAttribute;
	}
	public Long getStatusCheckMethod() {
		return statusCheckMethod;
	}
	public void setStatusCheckMethod(Long statusCheckMethod) {
		this.statusCheckMethod = statusCheckMethod;
	}
	public String getPacketBytes() {
		return packetBytes;
	}
	public void setPacketBytes(String udpBytes) {
		this.packetBytes = udpBytes;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}

}
