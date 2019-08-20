package com.elitecore.netvertexsm.datamanager.datasource.esiradius.data;

import java.sql.Timestamp;

public class EsiRadiusData implements IEsiRadiusData {
	private long esiInstanceId;
	private String name;
	private String description;
	private String address;
	private String sharedSecret;
	private Long timeout;
	private Long expiredReqLimitCnt;
	private Long minLocalPort;
	private String status;
	private Long createdByStaffId;
	private Long lastModifiedByStaffId;
	private Timestamp lastModifiedDate;
	private Timestamp createDate;
	private Long retryLimit;
	private Long statusCheckDuration;
	
	public long getEsiInstanceId() {
		return esiInstanceId;
	}
	public void setEsiInstanceId(long esiInstanceId) {
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
	public String getSharedSecret() {
		return sharedSecret;
	}
	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}
	public Long getTimeout() {
		return timeout;
	}
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
	public Long getExpiredReqLimitCnt() {
		return expiredReqLimitCnt;
	}
	public void setExpiredReqLimitCnt(Long expiredReqLimitCnt) {
		this.expiredReqLimitCnt = expiredReqLimitCnt;
	}
	public Long getMinLocalPort() {
		return minLocalPort;
	}
	public void setMinLocalPort(Long minLocalPort) {
		this.minLocalPort = minLocalPort;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(Long createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	public Long getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}
	public void setLastModifiedByStaffId(Long lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
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
}
