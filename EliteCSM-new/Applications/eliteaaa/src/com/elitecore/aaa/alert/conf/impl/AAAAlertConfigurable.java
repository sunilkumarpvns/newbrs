package com.elitecore.aaa.alert.conf.impl;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.impl.AAAServerConfigurable;
import com.elitecore.aaa.core.config.AlertListnersDetail;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;

@XmlType(propOrder = {})
@XmlRootElement(name = "alert-listners")
@ConfigurationProperties(moduleName = "AAA_ALERT_CONFIGURABLE",synchronizeKey ="AAA-ALERT-MNGR", readWith = DBReader.class, writeWith = XMLWriter.class)
@XMLProperties(schemaDirectories = {"system","schema"},configDirectories = {"conf"},name = "alert-listners")
public class AAAAlertConfigurable extends BaseAlertConfigurable{
	
	public AAAAlertConfigurable() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public AlertListnersDetail getAlertListnersDetail() {
		AAAServerConfigurable aaaServerConfigurable = getConfigurationContext().get(AAAServerConfigurable.class);
		AlertListnersDetail alertListenersDetail = aaaServerConfigurable.getAlertListners();
		return alertListenersDetail;
	}
	
}