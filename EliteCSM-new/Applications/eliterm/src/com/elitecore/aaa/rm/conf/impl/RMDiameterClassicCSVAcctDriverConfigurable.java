package com.elitecore.aaa.rm.conf.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.impl.ClassicCSVAcctDriverConfigurable;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.diameter.service.application.drivers.conf.impl.NasClassicCSVAcctDriverConfigurationImpl;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterFailureConstants;

@XmlType(propOrder={})
@XmlRootElement(name = "rm-dia-classic-csv-drivers")
@ConfigurationProperties(moduleName = "RM-DIA-CLASSIC-CSV-DRIVER-CONFIGURABLE", readWith = DBReader.class, writeWith = XMLWriter.class,synchronizeKey ="")
@XMLProperties(name = "rm-dia-classic-csv-drivers",schemaDirectories = {"system","schema"},configDirectories = {"conf","db","diameter","driver"})
public class RMDiameterClassicCSVAcctDriverConfigurable extends ClassicCSVAcctDriverConfigurable<NasClassicCSVAcctDriverConfigurationImpl>  {

	private static final String MODULE="RM-DIA-CLASSIC-CSV-DRIVER-CONFIGURABLE";
	
	private List<NasClassicCSVAcctDriverConfigurationImpl> nasClassicCSVAcctDriverConfList;
	
	public RMDiameterClassicCSVAcctDriverConfigurable() {
		nasClassicCSVAcctDriverConfList = new ArrayList<NasClassicCSVAcctDriverConfigurationImpl>();
	}
	
	@Override
	protected NasClassicCSVAcctDriverConfigurationImpl createConfigurationObject() {
		return new NasClassicCSVAcctDriverConfigurationImpl();
	}

	@Override
	protected String getDriverQuery() {
		 String	query  = "select DRIVERINSTANCEID, DRIVERTYPEID,NAME from tblmdriverinstance where DRIVERTYPEID='"+ DriverTypes.NAS_CLASSIC_CSV_ACCT_DRIVER.value  
					+ "' AND "
					+ " NAME IN (select DISTINCT FAILUREARGUMENTS from TBLMROUTINGCONFFAILUREPARAMS where FAILUREACTION="+DiameterFailureConstants.RECORD.failureAction+")";
		return query;
	}

	@Override
	protected String getModuleName() {
		return MODULE;
	}
	
	@Override
	@XmlElement(name="classic-csv-driver")
	public List<NasClassicCSVAcctDriverConfigurationImpl> getDriverConfigurationList() {
		return nasClassicCSVAcctDriverConfList;
	}
	
}
