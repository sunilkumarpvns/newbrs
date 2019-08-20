package com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data;

import java.util.List;


public interface IAccessPolicyData {
	public String getAccessPolicyId();
	public void setAccessPolicyId(String accessPolicyId);
	public String getAssigned();
	public void setAssigned(String assigned);
	public String getCommonStatusId();
	public void setCommonStatusId(String commonStatusId);
	public String getDescription();
	public void setDescription(String description);
	public java.sql.Timestamp getLastUpdated();
	public void setLastUpdated(java.sql.Timestamp lastUpdated);
	public String getName();
	public void setName(String name);
	public List getAccessPolicyDetailDataList();
	public void setAccessPolicyDetailDataList(List accessPolicyDetailData);
	public String getAccessStatus();
	public void setAccessStatus(String accessStatus);
	public String getSystemGenerated();
	public void setSystemGenerated(String systemGenerated);
	public java.sql.Timestamp getStatusChangeDate();
	public void setStatusChangeDate(java.sql.Timestamp statusChangeDate);
}
