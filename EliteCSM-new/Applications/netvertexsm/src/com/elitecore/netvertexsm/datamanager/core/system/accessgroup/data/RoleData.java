package com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data;

import com.elitecore.corenetvertex.sm.acl.RoleModuleActionData;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public class RoleData implements IRoleData{
	private long roleId;
	private String name;
	private String roleMailId;
	private String description;
	private String systemGenerated;
	private Timestamp statusChangeDate;
	private String commonStatusId;
	private Timestamp createDate;
	private String createdByStaffId;
	private Timestamp lastModifiedDate;
	private String lastModifiedByStaffId;
	private Set<RoleModuleActionData> roleModuleActionData;
	private Set<RoleActionRelData> roleActionRelDatas;
	private Set<String> actionsAlias;


//	private Set groupActionRel;
	//private Set staffRoleRel;
	public String getCommonStatusId() {
		return commonStatusId;
	}
	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	public String getRoleMailId() {
		return roleMailId;
	}
	public void setRoleMailId(String roleMailId) {
		this.roleMailId = roleMailId;
	}
	public String getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}
	public void setLastModifiedByStaffId(String lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Timestamp getStatusChangeDate() {
		return statusChangeDate;
	}
	public void setStatusChangeDate(Timestamp statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
/*	public Set getGroupActionRel() {
		return groupActionRel;
	}
	public void setGroupActionRel(Set groupActionRel) {
		this.groupActionRel = groupActionRel;
	}*/
	/*public Set getStaffRoleRel() {
		return staffRoleRel;
	}
	public void setStaffRoleRel(Set staffGroupRel) {
		this.staffRoleRel = staffGroupRel;
	}*/

	public Set<RoleModuleActionData> getRoleModuleActionData() {
		return roleModuleActionData;
}
	public void setRoleModuleActionData(Set<RoleModuleActionData> roleModuleActionData) {
		this.roleModuleActionData = roleModuleActionData;
	}
	public Set<RoleActionRelData> getRoleActionRelDatas() {
		return roleActionRelDatas;
	}

	public void setRoleActionRelDatas(Set<RoleActionRelData> roleActionRelDatas) {
		this.roleActionRelDatas = roleActionRelDatas;
}

	public Set<String> getActionsAlias() {
		if (Collectionz.isNullOrEmpty(actionsAlias)) {
			actionsAlias = Collectionz.newHashSet();
			if(Collectionz.isNullOrEmpty(roleActionRelDatas) == false){
				for(RoleActionRelData roleActionRelData :roleActionRelDatas){
					actionsAlias.add(roleActionRelData.getActionData().getAlias());
				}
			}
		}

		return actionsAlias;
	}

}
