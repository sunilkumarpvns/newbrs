package com.elitecore.elitesm.datamanager.servermgr.drivers.data;

import java.io.Serializable;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class LogicalNameValuePoolData extends BaseData implements Serializable,ILogicalNameValuePool{
	
	private String id;
	private String name;
	private String value;
	 
	public LogicalNameValuePoolData(){};
	
	public LogicalNameValuePoolData(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	

	

}
