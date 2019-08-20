package com.elitecore.netvertexsm.datamanager.bitemplate.data;

public class BISubKeyData {
	private long id;
	private String key;
	private String value;
	private Long biTemplateId;
	
	public Long getBiTemplateId() {
		return biTemplateId;
	}
	public void setBiTemplateId(Long biTemplateId) {
		this.biTemplateId = biTemplateId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
