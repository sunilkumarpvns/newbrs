package com.elitecore.elitesm.web.plugins.forms;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UserStatisticPostAuthPluginForm extends BaseWebForm{

	private static final long serialVersionUID = 1L;
	private String pluginId;
	private String pluginType;
	private String pluginInstanceId;
	private byte[] pluginData;
	private String action;
	private String pluginName;
	private String description;
	private String userStatPostAuthJson;
	private String auditUId;
	private String status;
	private String databaseName;
	private String databaseId;
	private String dataSourceName;
	private List databaseDSList;
	private String tableName;
	private long dbQueryTimeoutInMs;
	private long maxQueryTimeoutCount;
	private long batchUpdateIntervalInMs;
	
	public String getPluginId() {
		return pluginId;
	}
	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}
	public String getPluginType() {
		return pluginType;
	}
	public void setPluginType(String pluginType) {
		this.pluginType = pluginType;
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
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getPluginName() {
		return pluginName;
	}
	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUserStatPostAuthJson() {
		return userStatPostAuthJson;
	}
	public void setUserStatPostAuthJson(String userStatPostAuthJson) {
		this.userStatPostAuthJson = userStatPostAuthJson;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDatabaseId() {
		return databaseId;
	}
	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}
	public List getDatabaseDSList() {
		return databaseDSList;
	}
	public void setDatabaseDSList(List databaseDSList) {
		this.databaseDSList = databaseDSList;
	}
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
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
	public long getDbQueryTimeoutInMs() {
		return dbQueryTimeoutInMs;
	}
	public void setDbQueryTimeoutInMs(long dbQueryTimeoutInMs) {
		this.dbQueryTimeoutInMs = dbQueryTimeoutInMs;
	}
	public long getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}
	public void setMaxQueryTimeoutCount(long maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
	}
	public long getBatchUpdateIntervalInMs() {
		return batchUpdateIntervalInMs;
	}
	public void setBatchUpdateIntervalInMs(long batchUpdateIntervalInMs) {
		this.batchUpdateIntervalInMs = batchUpdateIntervalInMs;
	}
	public JSONObject getDefaultmapping(){
		
		JSONObject plJsonObject=new JSONObject();
		JSONArray array=new JSONArray();
		
		JSONObject  defaultMappingEntryOne=new JSONObject();
		defaultMappingEntryOne.put("attributeId","0:1");
		defaultMappingEntryOne.put("packetType","0");
		defaultMappingEntryOne.put("dbField","USER_NAME");
		defaultMappingEntryOne.put("dataType","String");
		array.add(defaultMappingEntryOne);
		
		JSONObject  defaultMappingEntryTwo=new JSONObject();
		defaultMappingEntryTwo.put("attributeId","0:31");
		defaultMappingEntryTwo.put("packetType","0");
		defaultMappingEntryTwo.put("dbField","CALLING_STATION_ID");
		defaultMappingEntryTwo.put("dataType","String");
		array.add(defaultMappingEntryTwo);
		
		JSONObject  defaultMappingEntryThree=new JSONObject();
		defaultMappingEntryThree.put("attributeId","0:8");
		defaultMappingEntryThree.put("packetType","0");
		defaultMappingEntryThree.put("dbField","FRAMED_IP_ADDRESS");
		defaultMappingEntryThree.put("dataType","String");
		array.add(defaultMappingEntryThree);
		
		JSONObject  defaultMappingEntryFour=new JSONObject();
		defaultMappingEntryFour.put("attributeId","0:4");
		defaultMappingEntryFour.put("packetType","0");
		defaultMappingEntryFour.put("dbField","NAS_IP_ADDRESS");
		defaultMappingEntryFour.put("dataType","String");
		array.add(defaultMappingEntryFour);
		
		JSONObject  defaultMappingEntryFive=new JSONObject();
		defaultMappingEntryFive.put("attributeId","0:18");
		defaultMappingEntryFive.put("packetType","1");
		defaultMappingEntryFive.put("dbField","REPLY_MESSAGE");
		defaultMappingEntryFive.put("dataType","String");
		array.add(defaultMappingEntryFive);
		
		plJsonObject.put("attributeList", array);
		
		return plJsonObject;
	}
	
}
