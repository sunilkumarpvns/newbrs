package com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data;

import java.sql.Timestamp;
import java.util.List;

import com.elitecore.elitesm.datamanager.core.base.data.CommonStatusData;

public interface IRadiusPolicyData {
	public String getName();
	public void setName(String name);
	public String getDescription();
	public void setDescription(String description);
	public Timestamp getCreateDate();
	public void setCreateDate(Timestamp createDate);
	public String getSystemGenerated();
	public void setSystemGenerated(String systemGenerated);
	public String getCreatedByStaffId();
	public void setCreatedByStaffId(String createdByStaffId);
	public String getLastModifiedByStaffId();
	public void setLastModifiedByStaffId(String lastModifiedByStaffId);
	public Timestamp getStatusChangeDate();
	public void setStatusChangeDate(Timestamp statusChangeDate);
	public String getEditable();
	public void setEditable(String editable);
	public String getRadiusPolicyId();
	public void setRadiusPolicyId(String radiusPolicyId);
	public String getCommonStatusId();
	public void setCommonStatusId(String commonStatusId);
	public CommonStatusData getCommonStatus();
	public void setCommonStatus(CommonStatusData commonStatus);
	public Timestamp getLastUpdated();
	public void setLastUpdated(Timestamp lastUpdated);
	public String getCheckItem();
	public void setCheckItem(String checkItem);
	public String getAddItem();
	public void setAddItem(String addItem);
	public String getRejectItem();
	public void setRejectItem(String rejectItem);
	public String getReplyItem();
	public void setReplyItem(String replyItem);
	public List<RadiusPolicyTimePeriod> getRadiusPolicyTimePeriodList() ;
	public void setRadiusPolicyTimePeriodList(List<RadiusPolicyTimePeriod> radiusPolicyTimePeriodList);
	public String getAuditUId();
	public void setAuditUId(String auditUId);
}
