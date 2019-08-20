package com.elitecore.elitesm.datamanager.core.system.staff.data;

import java.io.Serializable;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class StaffGroupRelData extends BaseData implements IStaffGroupRelData,Serializable,Differentiable{
	private String staffId;
	private String groupId;
/*	private IStaffData staffData;
	private IGroupData groupData;*/
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getStaffId() {
		return staffId;
	}
	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}
	/*public IStaffData getStaffData() {
		return staffData;
	}
	public void setStaffData(IStaffData staffData) {
		this.staffData = staffData;
	}
	public IGroupData getGroupData() {
		return groupData;
	}
	public void setGroupData(IGroupData groupData) {
		this.groupData = groupData;
	}*/
	@Override
	public JSONObject toJson() {
		JSONObject object=new JSONObject();
		object.put("Group Id", groupId);
		return object;
	}
}
