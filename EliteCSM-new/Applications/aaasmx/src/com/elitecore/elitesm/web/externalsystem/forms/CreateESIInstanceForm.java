package com.elitecore.elitesm.web.externalsystem.forms;

import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateESIInstanceForm extends BaseWebForm{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private long esiTypeId;
	private String desc;
	private String address;
	private String sharedSecret="secret";	
	private Integer minLocalPort=10;
	private Long expiredRequestLimitCount=50L;
	private List esiTypeList;
	private Long retryLimit=1L;
	private Long statusCheckDuration=120L;
	private Long timeout=1000L;
	private String realmNames;
	private String supportedAttribute;
	private String unSupportedAttribute;
	private Long statusCheckMethod=1L;
	private String packetBytes;
	
		
	public String getRealmNames() {
		return realmNames;
	}
	public void setRealmNames(String realmNames) {
		this.realmNames = realmNames;
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

	public long getEsiTypeId() {
		return esiTypeId;
	}
	public void setEsiTypeId(long esiTypeId) {
		this.esiTypeId = esiTypeId;
	}	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public String getSharedSecret() {
		return sharedSecret;
	}
	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}
	public List getEsiTypeList() {
		return esiTypeList;
	}
	public void setEsiTypeList(List esiTypeList) {
		this.esiTypeList = esiTypeList;
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
	
	public String getPacketBytes() {
		return packetBytes;
	}
	public void setPacketBytes(String udpBytes) {
		this.packetBytes = udpBytes;
	}
	public Long getStatusCheckMethod() {
		return statusCheckMethod;
	}
	public void setStatusCheckMethod(Long statusCheckMethod) {
		this.statusCheckMethod = statusCheckMethod;
	}
	
	
	
}
