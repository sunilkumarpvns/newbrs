package com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data;

import java.util.Set;

public interface ISystemParameterData {
	public String getAlias();
	public void setAlias(String alias) ;
	public String getName();
	public void setName(String name);
	public long getParameterId();
	public void setParameterId(long parameterId);
	public String getSystemGenerated();
	public void setSystemGenerated(String systemGenerated);
	public String getValue();
	public void setValue(String value);
	public Set getParameterDetail();
	public void setParameterDetail(Set parameterDetail);
	public String getDescription();
	public void setDescription(String description);
}
