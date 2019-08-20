package com.elitecore.aaa.radius.plugins.quotamgr.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.radius.plugins.quotamgr.conf.QuotaManagerPluginConf;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;


@XmlType(propOrder = {})
@XmlRootElement(name = "prepaid-quota-manager")
@ConfigurationProperties(moduleName = "QUOTA_MANAGER_PLUGIN_CONFIGURABLE", readWith = XMLReader.class, reloadWith = XMLReader.class, synchronizeKey = "")
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf","plugins"},name = "quota-manager-plugin")
public class QuotaManagerPluginConfigurable extends Configurable implements QuotaManagerPluginConf{

	private String discconetionURL;

	public QuotaManagerPluginConfigurable() {
		// TODO Auto-generated constructor stub
	}

	@PostRead
	public void postReadProcessing() {
		// TODO Auto-generated method stub
		
	}
	
	@PostReload
	public void postReloadProcessing() {

	}
	
	@PostWrite
	public void postWriteProcessing() {

	}

	@Override
	@XmlElement(name ="disconnection-url",type = String.class)
	public String getDiconnectURL() {
		return discconetionURL;
	}
	public void setDiconnectURL(String discconetionURL) {
		this.discconetionURL = discconetionURL;
	}

}
