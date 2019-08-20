package com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data;

import java.util.Set;

public class SystemParameterData implements ISystemParameterData{
	private long parameterId;
	private String name;
	private String description;
	private String alias;
	private String value;
	private String systemGenerated;
	private Set parameterDetail;
	
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getParameterId() {
		return parameterId;
	}
	public void setParameterId(long parameterId) {
		this.parameterId = parameterId;
	}
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Set getParameterDetail() {
		return parameterDetail;
	}
	public void setParameterDetail(Set parameterDetail) {
		this.parameterDetail = parameterDetail;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
