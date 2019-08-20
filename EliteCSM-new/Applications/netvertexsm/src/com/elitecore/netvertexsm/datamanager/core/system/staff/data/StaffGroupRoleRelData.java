package com.elitecore.netvertexsm.datamanager.core.system.staff.data;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;

/**
 * This class is used to provide the relation between Staff, Group and Role
 * @author ishai.bhatt
 *
 */

@Entity
@Table(name="TBLM_STAFF_GROUP_ROLE_RELOLD")
public class StaffGroupRoleRelData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GroupData groupData;
	private StaffData staffData;
	private RoleData roleData;
	
	@Id
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="GROUPID")
	public GroupData getGroupData() {
		return groupData;
	}
	public void setGroupData(GroupData groupData) {
		this.groupData = groupData;
	}
	
	@Id
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="STAFFID")
	public StaffData getStaffData() {
		return staffData;
	}
	public void setStaffData(StaffData staffData) {
		this.staffData = staffData;
	}
	
	@Id
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="ROLEID")
	public RoleData getRoleData() {
		return roleData;
	}
	public void setRoleData(RoleData roleData) {
		this.roleData = roleData;
	}
	
	

}
