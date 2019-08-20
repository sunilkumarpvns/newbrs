package com.elitecore.elitesm.datamanager.core.system.profilemanagement.data;

import java.sql.Timestamp;

public interface IConfigurationProfileHistoryData {
	public String getConfigProfileHistoryId();
	public void setConfigProfileHistoryId(String configProfileHistoryId);
	public String getName();
	public void setName(String name);
	public String getDescription();
	public void setDescription(String description);
	public Timestamp getCreateDate();
	public void setCreateDate(Timestamp createDate);
	public String getConfigurationProfileId();
	public void setConfigurationProfileId(String configurationProfileId) ;
	public String getVersion();
	public void setVersion(String version);
	public String getCreatedBy();
	public void setCreatedBy(String createdBy);
	public byte[] getXmlData();
	public void setXmlData(byte[] xmlData);
}
