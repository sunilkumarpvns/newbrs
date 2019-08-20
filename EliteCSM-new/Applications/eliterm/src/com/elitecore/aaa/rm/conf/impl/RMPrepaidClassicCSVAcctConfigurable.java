package com.elitecore.aaa.rm.conf.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.impl.ClassicCSVAcctDriverConfigurable;
import com.elitecore.aaa.rm.drivers.conf.impl.RMRadClassicCSVAcctDriverConfImpl;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;


@XmlType(propOrder = {})
@XmlRootElement(name = "rm-prapaid-fallback-drivers")
@ConfigurationProperties(moduleName ="PREP-CHRGNG-CSV-CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "rm-prapaid-fallback-drivers", schemaDirectories = {"system","schema"}, configDirectories = {"conf","db","services","prepaid","driver"})
public class RMPrepaidClassicCSVAcctConfigurable  extends ClassicCSVAcctDriverConfigurable<RMRadClassicCSVAcctDriverConfImpl> {

	private static final String MODULE = "PREP-CHRGNG-CSV-CONFIGURABLE";
	private List<RMRadClassicCSVAcctDriverConfImpl> rmRadClassicCSVAcctDriverConfImplList;
	
	public RMPrepaidClassicCSVAcctConfigurable() {
		rmRadClassicCSVAcctDriverConfImplList = new ArrayList<RMRadClassicCSVAcctDriverConfImpl>();
	}
	
	@XmlElement(name="rm-prapaid-fallback-driver")
	public List<RMRadClassicCSVAcctDriverConfImpl> getDriverConfigurationList() {
		return rmRadClassicCSVAcctDriverConfImplList;
	}

	@Override
	protected RMRadClassicCSVAcctDriverConfImpl createConfigurationObject() {
		return new RMRadClassicCSVAcctDriverConfImpl();
	}

	@Override
	protected String getDriverQuery() {
		RMPrepaidChargingServiceConfigurable chargingServiceConfigurable = getConfigurationContext().get(RMPrepaidChargingServiceConfigurable.class);
		String fallbackDriver = chargingServiceConfigurable.getFallbackDriver();
		String query = "select * from TBLMDRIVERINSTANCE where NAME= '" + fallbackDriver + "'";
		return query;
	}

	@Override
	protected String getModuleName() {
		return MODULE;
	}
}
