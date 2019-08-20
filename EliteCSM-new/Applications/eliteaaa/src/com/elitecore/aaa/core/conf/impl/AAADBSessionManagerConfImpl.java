package com.elitecore.aaa.core.conf.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.core.conf.AAADBSessionManagerConf;
import com.elitecore.aaa.core.data.AAADBSMAttributeConfData;

public class AAADBSessionManagerConfImpl implements AAADBSessionManagerConf {
	
	private String name;
	
	private String connectionURL;
	private String username;
	private String password;
	private int minPoolSize;
	private int maxPoolSize;
	private String userIdentityAttribute;
	private String sessionIdAttribute;
	private String sessionGroupAttribtues;
	private Map<String, AAADBSMAttributeConfData> aaaDBSMAttrConfData;
	private String tableName = "TBLMCONCURRENTUSERS";
	private int pluginScanTime = 5000;
	private String dataSourceName;
	
	
	public AAADBSessionManagerConfImpl(String name, String connectionURL,
			String username, String password, int minPoolSize, int maxPoolSize,
			String userIdentityAttribute, String sessionIdAttribute,
			String sessionGroupAttribtues,String tableName,int pluginScantime,String dataSourceName) {
		super();
		this.name = name;
		this.connectionURL = connectionURL;
		this.username = username;
		this.password = password;
		this.minPoolSize = minPoolSize;
		this.maxPoolSize = maxPoolSize;
		this.userIdentityAttribute = userIdentityAttribute;
		this.sessionIdAttribute = sessionIdAttribute;
		this.sessionGroupAttribtues = sessionGroupAttribtues;
		this.tableName = tableName;
		this.pluginScanTime = pluginScantime;
		this.dataSourceName = dataSourceName;
		this.aaaDBSMAttrConfData = new HashMap<String, AAADBSMAttributeConfData>();
		
	}
	
	public AAADBSessionManagerConfImpl(String name, String connectionURL,
			String username, String password, int minPoolSize, int maxPoolSize,
			String userIdentityAttribute, String sessionIdAttribute,
			String sessionGroupAttribtues,String tableName,int pluginScantime,String dataSourceName,Map<String, AAADBSMAttributeConfData>  aaaDBSMAttrConfData) {
		super();
		this.name = name;
		this.connectionURL = connectionURL;
		this.username = username;
		this.password = password;
		this.minPoolSize = minPoolSize;
		this.maxPoolSize = maxPoolSize;
		this.userIdentityAttribute = userIdentityAttribute;
		this.sessionIdAttribute = sessionIdAttribute;
		this.sessionGroupAttribtues = sessionGroupAttribtues;
		this.tableName = tableName;
		this.aaaDBSMAttrConfData = aaaDBSMAttrConfData;
		this.pluginScanTime = pluginScantime;
		this.dataSourceName = dataSourceName;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getConnectionURL() {
		return connectionURL;
	}
	public void setConnectionURL(String connectionURL) {
		this.connectionURL = connectionURL;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getMinPoolSize() {
		return minPoolSize;
	}
	public void setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}
	public int getMaxPoolSize() {
		return maxPoolSize;
	}
	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}
	public String getUserIdentityAttribute() {
		return userIdentityAttribute;
	}
	public void setUserIdentityAttribute(String userIdentityAttribute) {
		this.userIdentityAttribute = userIdentityAttribute;
	}
	public String getSessionIdAttribute() {
		return sessionIdAttribute;
	}
	public void setSessionIdAttribute(String sessionIdAttribute) {
		this.sessionIdAttribute = sessionIdAttribute;
	}
	public String getSessionGroupAttribtues() {
		return sessionGroupAttribtues;
	}
	public void setSessionGroupAttribtues(String sessionGroupAttribtues) {
		this.sessionGroupAttribtues = sessionGroupAttribtues;
	}
	public AAADBSMAttributeConfData getAaaDBSMAttrConfData(String attributeId) {
		return aaaDBSMAttrConfData.get(attributeId);
	}
	public AAADBSMAttributeConfData addAaaDBSMAttrConfData(AAADBSMAttributeConfData aaaDBSMAttrConfData) {
		return this.aaaDBSMAttrConfData.put(aaaDBSMAttrConfData.getAttributeId(), aaaDBSMAttrConfData);
	}

	@Override
	public List<AAADBSMAttributeConfData> getAaaDBSMAttrConfDataList() {
		if(aaaDBSMAttrConfData != null){
			List<AAADBSMAttributeConfData> values = new ArrayList<AAADBSMAttributeConfData>(aaaDBSMAttrConfData.size());
			for(AAADBSMAttributeConfData data : aaaDBSMAttrConfData.values()){
				values.add(data);
			}
			return values;
		}
		return null;
	}
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public int getPluginScanTime() {
		return pluginScanTime;
	}

	@Override
	public String getDataSourceName() {
		return dataSourceName;
	}
	
	
}
