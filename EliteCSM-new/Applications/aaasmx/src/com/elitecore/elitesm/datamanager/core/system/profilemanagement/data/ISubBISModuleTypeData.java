package com.elitecore.elitesm.datamanager.core.system.profilemanagement.data;

import java.sql.Timestamp;

public interface ISubBISModuleTypeData {
	public String getSubBisModuleTypeId();
	public void setSubBisModuleTypeId(String subBisModuleTypeId);
	public String getName();
	public void setName(String name) ;
	public String getAlias();
	public void setAlias(String alias);
	public String getDescription();
	public void setDescription(String description);
	public String getSystemGenerated();
	public void setSystemGenerated(String systemGenerated);
	public String getBisModuleTypeId();
	public void setBisModuleTypeId(String bisModuleTypeId);
	public Timestamp getCreateDate();
	public void setCreateDate(Timestamp createDate) ;
}

