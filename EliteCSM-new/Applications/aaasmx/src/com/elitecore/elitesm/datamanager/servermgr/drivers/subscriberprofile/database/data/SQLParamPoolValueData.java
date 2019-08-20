package com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class SQLParamPoolValueData extends BaseData implements ISQLParamPoolValueData{
	
	private String sqlId;
	private String query;
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getSqlId() {
		return sqlId;
	}
	public void setSqlId(String sqlId) {
		this.sqlId = sqlId;
	}

}
