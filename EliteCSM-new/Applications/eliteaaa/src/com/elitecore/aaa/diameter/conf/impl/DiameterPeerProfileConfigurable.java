package com.elitecore.aaa.diameter.conf.impl;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;

@XmlType(propOrder = {})
@XmlRootElement(name = "peer-profiles")
@ConfigurationProperties(moduleName = "DIA-PEER-PROFILE-CONFIGURABLE", readWith = DBReader.class, reloadWith = DBReader.class, writeWith = XMLWriter.class,synchronizeKey ="")
@XMLProperties(name = "peer-profile",schemaDirectories = {"system","schema"},configDirectories = {"conf","diameter"})
public class DiameterPeerProfileConfigurable extends PeerProfileConfigurable{
	
	private static final String MODULE = "DIA-PEER-PROFILE-CONFIGURABLE"; 
	
	protected String getModule() {
		return MODULE;
	}

}
