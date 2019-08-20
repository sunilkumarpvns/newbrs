package com.elitecore.netvertexsm.web.core.system.accessgroup.forms;

import java.util.List;

import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffGroupRoleRelData;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class UpdateAccessGroupForm extends BaseWebForm{
	private long staffId;
	private long roleId;
	private String available;
	private String selected;
	private String action;
	private List<GroupData> groupDatas;
	public List<StaffGroupRoleRelData> staffGroupRoleRelDatas;
	public String getAvailable() {
		return available;
	}
	public void setAvailable(String available) {
		this.available = available;
	}
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
	public long getStaffId() {
		return staffId;
	}
	public void setStaffId(long staffId) {
		this.staffId = staffId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List<GroupData> getGroupDatas() {
		return groupDatas;
	}
	public void setGroupDatas(List<GroupData> groupDatas) {
		this.groupDatas = groupDatas;
	}
	public List<StaffGroupRoleRelData> getStaffGroupRoleRelDatas() {
		return staffGroupRoleRelDatas;
	}
	public void setStaffGroupRoleRelDatas(
			List<StaffGroupRoleRelData> staffGroupRoleRelDatas) {
		this.staffGroupRoleRelDatas = staffGroupRoleRelDatas;
	}
	
	
}
