package com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.radiuspolicytype.data;

import java.util.Set;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class RadiusPolicyParamTypeData extends BaseData implements IRadiusPolicyParamTypeData {
	private String radiusPolicyParamTypeId;
	private String parameterUsage;
	private String name;
	private String description;	
	private int orderBy;
	private Set lstOperatorParamTypeMap;
	
	public int getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(int orderBy) {
		this.orderBy = orderBy;
	}
	public String getRadiusPolicyParamTypeId() {
		return radiusPolicyParamTypeId;
	}
	public void setRadiusPolicyParamTypeId(String radiusPolicyParamTypeId) {
		this.radiusPolicyParamTypeId = radiusPolicyParamTypeId;
	}
	public String getParameterUsage() {
		return parameterUsage;
	}
	public void setParameterUsage(String parameterUsage) {
		this.parameterUsage = parameterUsage;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Set getOperatorParamTypeMap() {
		return lstOperatorParamTypeMap;
	}
	public void setOperatorParamTypeMap(Set lstOperatorParamTypeMap) {
		this.lstOperatorParamTypeMap = lstOperatorParamTypeMap;
	}

}
