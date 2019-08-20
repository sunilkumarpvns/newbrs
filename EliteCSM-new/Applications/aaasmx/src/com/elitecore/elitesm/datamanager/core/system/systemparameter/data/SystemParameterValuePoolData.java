package com.elitecore.elitesm.datamanager.core.system.systemparameter.data;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class SystemParameterValuePoolData extends BaseData implements ISystemParameterValuePoolData{
	private String parampoolId;
	private String parameterId;
	private String name;
	private String value;
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
	public String getParampoolId() {
		return parampoolId;
	}
	public void setParampoolId(String parampoolId) {
		this.parampoolId = parampoolId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
