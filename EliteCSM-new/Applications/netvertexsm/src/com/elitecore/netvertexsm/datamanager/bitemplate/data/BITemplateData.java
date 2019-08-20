package com.elitecore.netvertexsm.datamanager.bitemplate.data;

import java.util.List;

import com.elitecore.netvertexsm.web.core.base.BaseData;

public class BITemplateData extends BaseData{
	private long id;
	private String name;
	private String description;
	private String key;
	private List<BISubKeyData> biSubKeyList;

	public String getDescription() {
		return description;
	}
	public List<BISubKeyData> getBiSubKeyList() {
		return biSubKeyList;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setBiSubKeyList(List<BISubKeyData> biSubKeyList) {
		this.biSubKeyList = biSubKeyList;
	}
	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getKey() {
		return key;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setKey(String key) {
		this.key = key;
	}
}
