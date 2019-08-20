package com.elitecore.elitesm.datamanager.externalsystem.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ESITypeAndInstanceData {
	
	private long esiTypeId;
	private String esiInstanceId;
	
	@Expose
	@SerializedName("Name")
	private String name;

	@Expose
	@SerializedName("Description")
	private String description;

	@Expose
	@SerializedName("Extended Radius Type")
	private String esiTypeName;
	
	@Expose
	@SerializedName("Address")
	private String address;
	
	@Expose
	@SerializedName("Shared Secret")
	private String sharedSecret;
	
	@Expose
	@SerializedName("Realm Names")
	private String realmNames;
	
	@Expose
	@SerializedName("Timeout(ms)")
	private Long timeout;
	
	@Expose
	@SerializedName("Retry Count")
	private Long retryLimit;
	
	@Expose
	@SerializedName("Status Check Duration")
	private Long statusCheckDuration;
	
	@Expose
	@SerializedName("Expired Request Limit Count")
	private Long expiredRequestLimitCount;
	
	@Expose
	@SerializedName("Status Check Method (Sec.)")
	private Long statusCheckMethod;
	
	@Expose
	@SerializedName("Packet Bytes")
	private String packetBytes;
	
	@Expose
	@SerializedName("Minimum Local Port")
	private Integer minLocalPort;
	
	@Expose
	@SerializedName("Supported Attribute")
	private String supportedAttribute;
	
	@Expose
	@SerializedName("Unsupported Attribute")
	private String unSupportedAttribute;
	
	private String auditUId;
	
	public String getRealmNames() {
		return realmNames;
	}
	public void setRealmNames(String realmNames) {
		this.realmNames = realmNames;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSharedSecret() {
		return sharedSecret;
	}
	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
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

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Long getTimeout() {
		return timeout;
	}
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
	public Integer getMinLocalPort() {
		return minLocalPort;
	}
	public void setMinLocalPort(Integer minLocalPort) {
		this.minLocalPort = minLocalPort;
	}
	public Long getExpiredRequestLimitCount() {
		return expiredRequestLimitCount;
	}
	public void setExpiredRequestLimitCount(Long expiredRequestLimitCount) {
		this.expiredRequestLimitCount = expiredRequestLimitCount;
	}
	public String getEsiTypeName() {
		return esiTypeName;
	}
	public void setEsiTypeName(String esiTypeName) {
		this.esiTypeName = esiTypeName;
	}
	public long getEsiTypeId() {
		return esiTypeId;
	}
	public void setEsiTypeId(long esiTypeId) {
		this.esiTypeId = esiTypeId;
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
	public void setPacketBytes(String packetBytes) {
		this.packetBytes = packetBytes;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
}
