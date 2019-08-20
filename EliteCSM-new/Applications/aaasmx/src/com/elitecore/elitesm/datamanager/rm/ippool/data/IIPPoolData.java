package com.elitecore.elitesm.datamanager.rm.ippool.data;

import java.sql.Timestamp;
import java.util.Set;

import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;

public interface IIPPoolData {

    public String getIpPoolId();
    public void setIpPoolId(String iPPoolId);
    public String getCommonStatusId();
    public void setCommonStatusId(String commonStatusId);
    public Timestamp getCreateDate();
    public void setCreateDate(Timestamp createDate);
    public String getCreatedByStaffId();
    public void setCreatedByStaffId(String createdByStaffId);
    public String getDescription();
    public void setDescription(String description);
    public String getEditable();
    public void setEditable(String editable);
    public String getLastModifiedByStaffId();
    public void setLastModifiedByStaffId(String lastModifiedByStaffId);
    public Timestamp getLastModifiedDate();
    public void setLastModifiedDate(Timestamp lastModifiedDate);
    public String getNasIPAddress();
    public void setNasIPAddress(String nasIPAddress);
    public String getRuleSet();
	public void setRuleSet(String ruleSet);
    public String getName();
    public void setName(String name);
    public Timestamp getStatusChangedDate();
    public void setStatusChangedDate(Timestamp statusChangedDate);
    public String getSystemGenerated();
    public void setSystemGenerated(String systemGenerated);
    public IStaffData getLastModifiedByStaff();
    public void setLastModifiedByStaff(IStaffData lastModifiedByStaff);
    public IStaffData getCreatedByStaff();
    public void setCreatedByStaff(IStaffData createdByStaff);
    public Set<IPPoolDetailData> getIpPoolDetail();
    public void setIpPoolDetail(Set<IPPoolDetailData> ipPoolDetail);
    public String getAdditionalAttributes();
	public void setAdditionalAttributes(String additionalAttributes);
	public String getAuditUId();
	public void setAuditUId(String auditUId);
	public Set<IPPoolDetailData> getIpPoolDetailSet();
	public void setIpPoolDetailSet(Set<IPPoolDetailData> ipPoolDetailSet);
	public boolean isCheckValidate();
	public void setCheckValidate(boolean checkValidate);
	
}
