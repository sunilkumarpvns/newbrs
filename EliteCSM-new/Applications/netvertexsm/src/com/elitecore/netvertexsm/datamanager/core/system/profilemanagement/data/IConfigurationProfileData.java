package com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data;

import java.sql.Blob;

public interface IConfigurationProfileData {
	public String getConfigurationProfileId();
	public void setConfigurationProfileId(String configurationProfileId);
	public String getName();
	public void setName(String name);
	public String getAlias();
	public void setAlias(String alias) ;
	public String getDescription();
	public void setDescription(String description);
	public String getSystemGenerated();
	public void setSystemGenerated(String systemGenerated) ;
	public byte[] getXmlData();
	public void setXmlData(byte[] xmlData);
	public void setXmlDataBlob(Blob dataBlob);
	public Blob getXmlDataBlob();
}
