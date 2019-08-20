package com.elitecore.elitesm.datamanager.radius.bwlist.data;

import java.io.Serializable;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class BWListTypeData extends BaseData implements Serializable{

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
