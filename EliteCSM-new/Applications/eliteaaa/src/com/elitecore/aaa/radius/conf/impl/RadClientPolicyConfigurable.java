package com.elitecore.aaa.radius.conf.impl;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;

@XmlType(propOrder = {})
@XmlRootElement(name = "policies")
@ConfigurationProperties(moduleName ="CLIENT_POLICY_CONFIGURABLE",synchronizeKey ="CLIENT_POLICIES", readWith = XMLReader.class, reloadWith = XMLReader.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf"},name = "client-policies")
//FIXME this class is not being used anywhere. Check if needed to be removed
public class RadClientPolicyConfigurable extends Configurable {

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
}
