package com.elitecore.aaa.core.conf;

import java.util.List;

import com.elitecore.aaa.core.data.AAADBSMAttributeConfData;

public interface AAADBSessionManagerConf {

	public String getName();
	
	public String getConnectionURL();
	public String getUsername();
	public String getPassword();
	
	public int getMinPoolSize();
	public int getMaxPoolSize();
	
	public String getUserIdentityAttribute();
	public String getSessionIdAttribute();
	
	public String getSessionGroupAttribtues();
	
	public AAADBSMAttributeConfData getAaaDBSMAttrConfData(String attributeId);
	public AAADBSMAttributeConfData addAaaDBSMAttrConfData(AAADBSMAttributeConfData aaaDBSMAttrConfData);
	public List<AAADBSMAttributeConfData> getAaaDBSMAttrConfDataList();
	
	public String getTableName();
	
	public int getPluginScanTime();
	public String getDataSourceName();
	
	
	
}
