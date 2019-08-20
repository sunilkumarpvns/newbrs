package com.elitecore.elitesm.datamanager.core.system.systemparameter.data;

import java.util.Set;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class SystemParameterData extends BaseData implements ISystemParameterData{
	private String parameterId;
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
	public String getParameterId() {
		return parameterId;
	}
	public void setParameterId(String parameterId) {
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
