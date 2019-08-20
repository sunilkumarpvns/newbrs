package com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data;

import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.SQLPoolValueData;

public interface IDatasourceSchemaData {
	
	public String getDbAuthId(); 
	public void setDbAuthId(String dbAuthId);	
	public String getFieldId();
	public void setFieldId(String fieldId);
	public String getFieldName();
	public void setFieldName(String fieldName);
	public String getDataType();
	public void setDataType(String dataType);
	public long getLength();
	public void setLength(long length);
	
	public Integer getAppOrder();
	public void setAppOrder(Integer appOrder);
	
	public String getDisplayName();
	public void setDisplayName(String displayName);
	
	public Set getDbdsParamPoolValueSet();
	public void setDbdsParamPoolValueSet(Set dbdsParamPoolValueSet);
	
	
	
	public SQLParamPoolValueData getSqlData();
	public void setSqlData(SQLParamPoolValueData sqlData);
	
	public String getSqlId();
	public void setSqlId(String sqlId);
	
	public List<SQLPoolValueData> getLstSQLPoolValue();
	public void setLstSQLPoolValue(List<SQLPoolValueData> lstSQLPoolValue);
	
}
