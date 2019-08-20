package com.elitecore.elitesm.web.servicepolicy.acct.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ExternalSystemBean extends BaseWebForm{

	private static final long serialVersionUID = 1L;
	
	private long esiInstanceId;
	private long esiTypeId;
	private String name;
	private Integer weightage;
	private String value;
	
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public long getEsiInstanceId() {
		return esiInstanceId;
	}
	public void setEsiInstanceId(long esiInstanceId) {
		this.esiInstanceId = esiInstanceId;
	}
	public long getEsiTypeId() {
		return esiTypeId;
	}
	public void setEsiTypeId(long esiTypeId) {
		this.esiTypeId = esiTypeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getWeightage() {
		return weightage;
	}
	public void setWeightage(Integer weightage) {
		this.weightage = weightage;
	}
	
	
	
}
