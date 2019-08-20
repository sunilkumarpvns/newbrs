package com.elitecore.elitesm.datamanager.servermgr.alert.data;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class AlertListenerTypeData extends BaseData implements Differentiable{
	private String typeId;
	private String typeName;
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	@Override
	public JSONObject toJson() {
		// TODO Auto-generated method stub
		return null;
	}
}
