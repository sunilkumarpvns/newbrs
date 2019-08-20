package com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data;

import java.sql.Timestamp;
import java.util.List;

import com.elitecore.elitesm.datamanager.core.base.data.CommonStatusData;
import com.elitecore.elitesm.datamanager.radius.system.standardmaster.data.StandardMasterData;

public interface IConcurrentLoginPolicyData {
	
	public String getCommonStatusId();
	public void setCommonStatusId(String commonStatusId);
	
	public String getConcurrentLoginId();
	public void setConcurrentLoginId(String concurrentLoginId);

	public String getConcurrentLoginPolicyModeId();
	public void setConcurrentLoginPolicyModeId(String concurrentLoginPolicyModeId);
	
	public String getConcurrentLoginPolicyTypeId();
	public void setConcurrentLoginPolicyTypeId(String concurrentLoginPolicyTypeId);
	
	public Timestamp getCreateDate();
	public void setCreateDate(Timestamp createDate);
	
	public String getDescription();
	public void setDescription(String description);
	
	public String getCreatedByStaffId();
	public void setCreatedByStaffId(String createdByStaffId);
	
	public String getLastModifiedByStaffId();
	public void setLastModifiedByStaffId(String lastModifiedByStaffId);
	
	public Timestamp getLastModifiedDate();
	public void setLastModifiedDate(Timestamp lastModifiedDate);
	
	public Integer getLogin();
	public void setLogin(Integer login);
	
	public String getName();
	public void setName(String name);
	
	public String getServiceParameterId();
	public void setServiceParameterId(String serviceParameterId);
	
	public Timestamp getStatusChangeDate();
	public void setStatusChangeDate(Timestamp statusChangeDate);
	
	public String getSystemGenerated();
	public void setSystemGenerated(String systemGenerated);
	
	public CommonStatusData getCommonStatus();
	public void setCommonStatus(CommonStatusData commonStatus);
	
	public String getAttribute();
	public void setAttribute(String attribute);
	
	public StandardMasterData getPolicyType();
	public void setPolicyType(StandardMasterData policyType);
	
	public List getConcurrentLoginPolicyDetail() ;
	public void setConcurrentLoginPolicyDetail(List lstConcurrentLoginPolicyDetail);
	
	public String getAuditUId();
	public void setAuditUId(String auditUId);
}
