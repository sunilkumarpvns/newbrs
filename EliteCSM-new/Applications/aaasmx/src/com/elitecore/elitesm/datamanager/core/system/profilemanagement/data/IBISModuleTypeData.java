package com.elitecore.elitesm.datamanager.core.system.profilemanagement.data;

import java.sql.Timestamp;

public interface IBISModuleTypeData {
	public String getBisModuleTypeId();
	public void setBisModuleTypeId(String bisModuleTypeId);
	public String getName();
	public void setName(String name);
	public String getAlias();
	public void setAlias(String alias);
	public String getDescription();
	public void setDescription(String description);
	public String getSystemGenerated() ;
	public void setSystemGenerated(String systemGenerated);
	public Timestamp getCreateDate();
	public void setCreateDate(Timestamp createDate);
}
