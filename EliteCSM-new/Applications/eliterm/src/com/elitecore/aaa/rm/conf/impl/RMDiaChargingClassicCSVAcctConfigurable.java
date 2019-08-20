package com.elitecore.aaa.rm.conf.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.impl.ClassicCSVAcctDriverConfigurable;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.rm.drivers.conf.impl.RMRadClassicCSVAcctDriverConfImpl;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;
import com.elitecore.core.commons.util.ConfigurationUtil;

@XmlType(propOrder = {})
@XmlRootElement(name = "rm-dia-charging-drivers")
@ConfigurationProperties(moduleName ="DIA-CHRGNG-CSV-CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "rm-dia-charging-drivers", schemaDirectories = {"system","schema"}, configDirectories = {"conf","db","services","charging","driver"})
public class RMDiaChargingClassicCSVAcctConfigurable extends ClassicCSVAcctDriverConfigurable<RMRadClassicCSVAcctDriverConfImpl> {

	private static final String MODULE = "DIA-CHRGNG-CSV-CONFIGURABLE";
	private List<RMRadClassicCSVAcctDriverConfImpl> rmRadClassicCSVAcctDriverConfImplList;
	
	public RMDiaChargingClassicCSVAcctConfigurable() {
		rmRadClassicCSVAcctDriverConfImplList = new ArrayList<RMRadClassicCSVAcctDriverConfImpl>();
	}
	
	@XmlElement(name="rm-dia-charging-driver")
	public List<RMRadClassicCSVAcctDriverConfImpl> getDriverConfigurationList() {
		return rmRadClassicCSVAcctDriverConfImplList;
	}

	@Override
	protected RMRadClassicCSVAcctDriverConfImpl createConfigurationObject() {
		return new RMRadClassicCSVAcctDriverConfImpl();
	}

	@Override
	protected String getDriverQuery() {
		return "select DISTINCT DRIVERINSTANCEID, DRIVERTYPEID from TBLMDRIVERINSTANCE where DRIVERTYPEID='"+DriverTypes.RM_CLASSIC_CSV_ACCT_DRIVER.value+"'";	
	}

	@Override
	protected String getModuleName() {
		return MODULE;
	}

	private String getReadQueryForChargingServicePolicyConfiguration() {

		List<String> servicePolicies = null;
		RMChargingServiceConfigurable rmChargingServiceConfigurable = getConfigurationContext().get(RMChargingServiceConfigurable.class);
		if(rmChargingServiceConfigurable != null) {
			servicePolicies = rmChargingServiceConfigurable.getServicePolicies();
		}
		if(servicePolicies == null || servicePolicies.size() == 0 || servicePolicies.contains(AAAServerConstants.ALL)) {
			return "select policyid from tblmcgpolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + "'";
		}
		return "select policyid from tblmcgpolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + 
				"' AND NAME IN ("+ConfigurationUtil.getStrFromStringArrayList(servicePolicies)+")";

	}
}
