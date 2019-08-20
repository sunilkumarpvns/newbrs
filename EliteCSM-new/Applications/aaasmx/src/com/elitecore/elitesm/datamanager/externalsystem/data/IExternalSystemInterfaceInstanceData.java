package com.elitecore.elitesm.datamanager.externalsystem.data;

import java.sql.Timestamp;

public interface IExternalSystemInterfaceInstanceData {
	
	public String getEsiInstanceId();
	public void setEsiInstanceId(String esiInstanceId) ;
	
	public Long getEsiTypeId() ;
	public void setEsiTypeId(Long esiTypeId) ;
	
	public String getName() ;
	public void setName(String name) ;
	
	public String getDescription() ;
	public void setDescription(String description) ;
	
	public String getAddress();
	public void setAddress(String address);
	
	public Integer getMinLocalPort();
	public void setMinLocalPort(Integer minLocalPort);
	
	public String getSharedSecret() ;
	public void setSharedSecret(String sharedSecret) ;
	
	
	public Long getExpiredRequestLimitCount();
	public void setExpiredRequestLimitCount(Long expiredRequestLimitCount);
	
	public String getStatus() ;
	public void setStatus(String status) ;
	
	public String getCreatedByStaffId() ;
	public void setCreatedByStaffId(String createdByStaffId) ;
	
	public String getLastModifiedByStaffId() ;
	public void setLastModifiedByStaffId(String lastModifiedByStaffId) ;
	
	public Timestamp getLastModifiedDate() ;
	public void setLastModifiedDate(Timestamp lastModifiedDate) ;
	
	public Timestamp getCreateDate() ;
	public void setCreateDate(Timestamp createDate) ;
	
	public Long getRetryLimit();
	public void setRetryLimit(Long retryLimit);
	public Long getStatusCheckDuration();
	public void setStatusCheckDuration(Long statusCheckDuration);
	
	public String getAuditUId();
	public void setAuditUId(String auditUId);
	
}
