package com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data;

import java.sql.Timestamp;
import java.util.Set;

public interface IRoleData {
	public long getRoleId();
	public void setRoleId(long roleId) ;
	public String getName();
	public void setName(String name);
	public String getRoleMailId();
	public void setRoleMailId(String roleMailId);
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
	/*public Set getStaffRoleRel();
	public void setStaffRoleRel(Set staffRoleRel);*/
}
