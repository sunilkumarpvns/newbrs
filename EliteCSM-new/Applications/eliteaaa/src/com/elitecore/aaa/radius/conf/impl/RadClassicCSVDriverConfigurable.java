package com.elitecore.aaa.radius.conf.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.conf.impl.ClassicCSVAcctDriverConfigurable;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.radius.drivers.DriverConfigurable;
import com.elitecore.aaa.radius.drivers.conf.impl.DriverSelectQueryBuilder;
import com.elitecore.aaa.radius.drivers.conf.impl.RadClassicCSVAcctConfigurationImpl;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyConfigurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;

@XmlType(propOrder = {})
@XmlRootElement(name = "classic-csv-acct-drivers")
@ConfigurationProperties(moduleName ="RAD-CLASSIC-CSV-CONFIGURABLE", readWith = DBReader.class, synchronizeKey = "", writeWith = XMLWriter.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf","db","services","acct","driver"},name = "classic-csv-acct-drivers")
public class RadClassicCSVDriverConfigurable extends ClassicCSVAcctDriverConfigurable<RadClassicCSVAcctConfigurationImpl>
implements DriverConfigurable {
	
	private static final String MODULE = "RAD-CLASSIC-CSV-CONFIGURABLE";
	private List<RadClassicCSVAcctConfigurationImpl> radClassicCSVAcctConfList;  
	
	public RadClassicCSVDriverConfigurable() {
		radClassicCSVAcctConfList = new ArrayList<RadClassicCSVAcctConfigurationImpl>();
	}
	
	@Override
	@XmlElement(name="classic-csv-acct-driver")
	public List<RadClassicCSVAcctConfigurationImpl> getDriverConfigurationList() {
		return radClassicCSVAcctConfList;
	}

	@Override
	protected RadClassicCSVAcctConfigurationImpl createConfigurationObject() {
		return new RadClassicCSVAcctConfigurationImpl();
	}

	@Override
	protected String getDriverQuery() {
		RadiusServicePolicyConfigurable servicePolicyConfigurable = ((AAAConfigurationContext)getConfigurationContext()).get(RadiusServicePolicyConfigurable.class);
		return new DriverSelectQueryBuilder(DriverTypes.RAD_CLASSIC_CSV_ACCT_DRIVER, servicePolicyConfigurable.getSelectedDriverIds()).build();
	}

	@Override
	protected String getModuleName() {
		return MODULE;
	}
}
