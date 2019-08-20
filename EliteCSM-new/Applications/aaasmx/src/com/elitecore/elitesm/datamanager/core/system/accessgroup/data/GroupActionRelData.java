package com.elitecore.elitesm.datamanager.core.system.accessgroup.data;

import java.io.Serializable;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IActionData;

import net.sf.json.JSONObject;

public class GroupActionRelData extends BaseData implements IGroupActionRelData, Differentiable, Serializable{
	private static final long serialVersionUID = 1L;
	private String actionId;
	private String groupId;
	private IActionData actionData;
	
	public String getActionId() {
		return actionId;
	}
	
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public IActionData getActionData() {
		return actionData;
	}
	public void setActionData(IActionData actionData) {
		this.actionData = actionData;
	}
	
	@Override
	public JSONObject toJson() {
	JSONObject object = new JSONObject();
		if(actionData !=null){
			object.put(actionData.getName(), actionData.getStatus().equalsIgnoreCase("E") ? "Enabled" : "Disabled" );
		}
		return object;
	}
	
}
