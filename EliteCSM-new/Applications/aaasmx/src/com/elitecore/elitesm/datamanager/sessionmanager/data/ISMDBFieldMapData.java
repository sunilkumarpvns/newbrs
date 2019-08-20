package com.elitecore.elitesm.datamanager.sessionmanager.data;


public interface ISMDBFieldMapData {
	
	public String getSmConfigId() ;
	public void setSmConfigId(String smConfigId);
	
	public String getField();
	public void setField(String Field);
	
	public String getDbFieldName();
	public void setDbFieldName(String dbFieldName);
	
	public String getReferringEntity();
	public void setReferringEntity(String referringEntity);
	
	public Integer getDataType();
	public void setDataType(Integer dataType);
	
	public String getDefaultValue();
	public void setDefaultValue(String defaultValue);
}
