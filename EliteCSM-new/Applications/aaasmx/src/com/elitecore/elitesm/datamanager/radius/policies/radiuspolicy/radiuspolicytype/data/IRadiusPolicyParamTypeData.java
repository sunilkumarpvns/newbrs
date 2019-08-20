package com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.radiuspolicytype.data;

import java.util.Set;


public interface IRadiusPolicyParamTypeData {
	public int getOrderBy();
	public void setOrderBy(int orderBy);
	public String getRadiusPolicyParamTypeId();
	public void setRadiusPolicyParamTypeId(String radiusPolicyParamTypeId);
	public String getParameterUsage();
	public void setParameterUsage(String parameterUsage);
	public String getName();
	public void setName(String name);
	public String getDescription();
	public void setDescription(String description);
	public Set getOperatorParamTypeMap();
	public void setOperatorParamTypeMap(Set lstOperatorParamTypeMap);
	
}
