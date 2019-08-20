package com.elitecore.netvertexsm.datamanager.datasource.esiradius.data;

import java.sql.Timestamp;

public interface IEsiRadiusData {
	public long getEsiInstanceId();
	public void setEsiInstanceId(long esiInstanceId);
	public String getName();
	public void setName(String name);
	public String getDescription();
	public void setDescription(String description);
	public String getAddress();
	public void setAddress(String address);
	public String getSharedSecret();
	public void setSharedSecret(String sharedSecret);
	public Long getTimeout();
	public void setTimeout(Long timeout);
	public Long getExpiredReqLimitCnt();
	public void setExpiredReqLimitCnt(Long expiredReqLimitCnt);
	public Long getMinLocalPort();
	public void setMinLocalPort(Long minLocalPort);
	public String getStatus();
	public void setStatus(String status);
	public Long getCreatedByStaffId();
	public void setCreatedByStaffId(Long createdByStaffId);
	public Long getLastModifiedByStaffId();
	public void setLastModifiedByStaffId(Long lastModifiedByStaffId);
	public Timestamp getLastModifiedDate();
	public void setLastModifiedDate(Timestamp lastModifiedDate);
	public Timestamp getCreateDate();
	public void setCreateDate(Timestamp createDate);
	public Long getRetryLimit();
	public void setRetryLimit(Long retryLimit);
	public Long getStatusCheckDuration();
	public void setStatusCheckDuration(Long statusCheckDuration);
}
