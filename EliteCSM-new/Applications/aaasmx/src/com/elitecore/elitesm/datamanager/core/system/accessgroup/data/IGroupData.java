package com.elitecore.elitesm.datamanager.core.system.accessgroup.data;

import java.sql.Timestamp;
import java.util.Set;

public interface IGroupData {
	public String getGroupId();
	public void setGroupId(String groupId) ;
	public String getName();
	public void setName(String name);
	public String getGroupMailId();
	public void setGroupMailId(String groupMailId);
	public String getDescription();
	public void setDescription(String description);
	public String getSystemGenerated();
	public void setSystemGenerated(String systemGenerated);
	public Timestamp getStatusChangeDate();
	public void setStatusChangeDate(Timestamp statusChangeDate);
	public String getCommonStatusId();
	public void setCommonStatusId(String commonStatusId);
	public Timestamp getCreateDate();
	public void setCreateDate(Timestamp createDate);
	public String getCreatedByStaffId();
	public void setCreatedByStaffId(String createdByStaffId);
	public Timestamp getLastModifiedDate();
	public void setLastModifiedDate(Timestamp lastModifiedDate);
	public String getLastModifiedByStaffId();
	public void setLastModifiedByStaffId(String lastModifiedByStaffId);
	/*public Set getGroupActionRel();
	public void setGroupActionRel(Set groupActionRel);*/
	public Set getStaffGroupRel();
	public void setStaffGroupRel(Set staffGroupRel);
}
