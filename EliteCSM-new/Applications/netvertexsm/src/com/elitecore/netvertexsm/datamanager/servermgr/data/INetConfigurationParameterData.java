package com.elitecore.netvertexsm.datamanager.servermgr.data;

import java.util.Set;

/**
 * 
 * @author dhavalraval
 *
 */
public interface INetConfigurationParameterData {
	public String getParameterId();
	public void setParameterId(String parameterId);
	public String getConfigId();
	public void setConfigId(String configId);
	public int getSerialNo();
	public void setSerialNo(int serialNo);
	public String getName();
	public void setName(String name);
	public String getDisplayName();
	public void setDisplayName(String displayName);
	public String getAlias();
	public void setAlias(String alias);
	public String getDescription();
	public void setDescription(String description);
	public String getType();
	public void setType(String type);
	public String getRegExp();
	public void setRegExp(String regExp);
	public int getMaxInstances();
	public void setMaxInstances(int maxInstances);
	public String getMultipleInstances();
	public void setMultipleInstances(String multipleInstances);
	public String getParentParameterId();
	public void setParentParameterId(String parentParameterId);
	public String getDefaultValue();
	public void setDefaultValue(String defaultValue);
	public Set<INetConfigurationParameterData> getNetConfigChildParameters() ;
	public void setNetConfigChildParameters(Set<INetConfigurationParameterData> netConfigChildParameters);
	public Set<INetConfigurationValuesData> getNetConfigParamValues();
	public void setNetConfigParamValues(Set<INetConfigurationValuesData> netConfigParamValues);
	
	@Deprecated 
	public INetConfigurationParameterData getNetConfigurationParameter() ;
	@Deprecated
	public void setNetConfigurationParameter(INetConfigurationParameterData netConfigurationParameter);
	
	public int getMaxLength();
	public void setMaxLength(int maxLength);
	public Set<INetConfigParamValuePoolData> getNetConfigParamValuePool();
	public void setNetConfigParamValuePool(Set<INetConfigParamValuePoolData> netConfigParamValuePool);
	public String getEditable();
	public void setEditable(String editable);
	public String getStartUpMode();
	public void setStartUpMode(String startUpMode);
	public String getStatus();
	public void setStatus(String status);
	public String toString();
	
	public String getIsNotNull();
	public void setIsNotNull(String isnotnull);
	
}
