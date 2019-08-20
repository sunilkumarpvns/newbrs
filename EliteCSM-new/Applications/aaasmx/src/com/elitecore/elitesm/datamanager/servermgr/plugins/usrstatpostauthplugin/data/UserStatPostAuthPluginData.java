package com.elitecore.elitesm.datamanager.servermgr.plugins.usrstatpostauthplugin.data;

import java.io.Serializable;
import java.util.List;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserStatPostAuthPluginData extends BaseData implements IUserStatPostAuthPluginData, Serializable,Differentiable{

	
	private static final long serialVersionUID = 1L;

	private String pluginId;

	@Expose
	@SerializedName("Plugin Instance Id")
	private String pluginInstanceId;
	
	@Expose
	@SerializedName("pluginData")
	private byte[] pluginData;

	
	@Expose
	@SerializedName("Datasource Name")
	private String dataSourceName;
	
	@Expose
	@SerializedName("Database ID")
	private String databaseId;
	
	@Expose
	@SerializedName("Table Name")
	private String tableName;
	
	@Expose
	@SerializedName("DB Query Timeout(Sec.)")
	private long dbQueryTimeout;
	
	@Expose
	@SerializedName("Maximum Query Timeout Count")
	private long maxQueryTimeoutCount;
	
	@Expose
	@SerializedName("Batch Update Interval")
	private long batchUpdateInterval;
	
	private List mappingList;
	
	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	public String getPluginInstanceId() {
		return pluginInstanceId;
	}

	public void setPluginInstanceId(String pluginInstanceId) {
		this.pluginInstanceId = pluginInstanceId;
	}

	public byte[] getPluginData() {
		return pluginData;
	}

	public void setPluginData(byte[] pluginData) {
		this.pluginData = pluginData;
	}
	
	public String getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}
	
	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public long getDbQueryTimeout() {
		return dbQueryTimeout;
	}

	public void setDbQueryTimeout(long dbQueryTimeout) {
		this.dbQueryTimeout = dbQueryTimeout;
	}

	public long getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}

	public void setMaxQueryTimeoutCount(long maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
	}

	
	public long getBatchUpdateInterval() {
		return batchUpdateInterval;
	}

	public void setBatchUpdateInterval(long batchUpdateInterval) {
		this.batchUpdateInterval = batchUpdateInterval;
	}
	
	public List getMappingList() {
		return mappingList;
	}

	public void setMappingList(List mappingList) {
		this.mappingList = mappingList;
	}

	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Database Datasource", EliteSMReferencialDAO.fetchDatabaseDatasourceData(databaseId));
		jsonObject.put("Datasource Name",dataSourceName);
		jsonObject.put("Table Name", tableName);
		jsonObject.put("DB Query Timeout (Sec.)", dbQueryTimeout);
		jsonObject.put("Maximum Query Timeout Count", maxQueryTimeoutCount);
		jsonObject.put("Batch Update Interval",batchUpdateInterval);
		if( pluginData != null )
			jsonObject.put("File Data", new String(pluginData));
		return jsonObject;
	}

}
