package com.elitecore.aaa.radius.plugins.proxy.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.radius.plugins.proxy.conf.ProxyDecisionPluginConf;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;

@XmlType(propOrder = {})
@XmlRootElement(name = "user-not-found-decision-plugin")
@ConfigurationProperties(moduleName ="PROXY_DECISION_PLUGIN_CONFIGURABLE",synchronizeKey ="PROXY_DECISION_PLUGIN", readWith = XMLReader.class, reloadWith = XMLReader.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf","plugins"},name = "proxy-decision-plugin")
public class ProxyDecisionPluginConfigurable extends Configurable implements ProxyDecisionPluginConf {

	private String datasourceName;
	private String tableName;
	private String userIdentity;
	
	public ProxyDecisionPluginConfigurable() {
		
	}

	@PostRead
	public void postReadProcessing() {
		// TODO Auto-generated method stub
	}

	@PostWrite
	public void postWriteProcessing() {

	}
	
	@PostReload
	public void postReloadProcessing() {

	}

	@Override
	@XmlElement(name ="datasource-name",type = String.class)
	public String getDataSourceName() {
		return datasourceName;
	}
	public void setDataSourceName(String datasourceName){
		this.datasourceName = datasourceName;
	}

	@Override
	@XmlElement(name ="table-name",type = String.class)
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName){
		this.tableName = tableName;
	}

	@Override
	@XmlElement(name ="user-identity",type = String.class)
	public String getUserIdentity() {
		return userIdentity;
	}
	public void setUserIdentity(String userIdentity){
		this.userIdentity = userIdentity;
	}	
}
