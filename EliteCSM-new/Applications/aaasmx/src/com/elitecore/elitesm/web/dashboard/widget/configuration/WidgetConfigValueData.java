package com.elitecore.elitesm.web.dashboard.widget.configuration;

import java.io.Serializable;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class WidgetConfigValueData extends BaseData implements Serializable{

	private static final long serialVersionUID = 1L;
	private String valueId;
	private Long parameterId;
	private String parameterValue;
	
	public String getValueId() {
		return valueId;
	}
	public void setValueId(String valueId) {
		this.valueId = valueId;
	}
	public Long getParameterId() {
		return parameterId;
	}
	public void setParameterId(Long parameterId) {
		this.parameterId = parameterId;
	}
	public String getParameterValue() {
		return parameterValue;
	}
	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}
}
