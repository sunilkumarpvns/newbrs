package com.elitecore.elitesm.datamanager.core.system.systemparameter.data;

import java.util.Set;

public interface ISystemParameterData {
	public String getAlias();
	public void setAlias(String alias) ;
	public String getName();
	public void setName(String name);
	public String getParameterId();
	public void setParameterId(String parameterId);
	public String getSystemGenerated();
	public void setSystemGenerated(String systemGenerated);
	public String getValue();
	public void setValue(String value);
	public Set getParameterDetail();
	public void setParameterDetail(Set parameterDetail);
	public String getDescription();
	public void setDescription(String description);
}
