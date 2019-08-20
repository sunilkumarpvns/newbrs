package com.elitecore.netvertexsm.datamanager.servermgr.alert.data;

import java.io.Serializable;

public class AlertListenerTypeData implements Serializable{

	private static final long serialVersionUID = 1L;
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
}
