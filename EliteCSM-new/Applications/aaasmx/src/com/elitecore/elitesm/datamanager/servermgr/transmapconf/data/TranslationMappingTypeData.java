package com.elitecore.elitesm.datamanager.servermgr.transmapconf.data;

import java.io.Serializable;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class TranslationMappingTypeData extends BaseData implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String mappingTypeId;
	private String name;
	

	public String getMappingTypeId() {
		return mappingTypeId;
	}
	public void setMappingTypeId(String mappingTypeId) {
		this.mappingTypeId = mappingTypeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
