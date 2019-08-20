package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.impl.AAAServerConfigurable;
import com.elitecore.aaa.core.conf.impl.ClassicCSVAcctDriverConfigurable;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.diameter.conf.impl.DiameterEAPServiceConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterNasServiceConfigurable;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.TGPPServerPolicyConfigurable;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterFailureConstants;


@XmlType(propOrder={})
@XmlRootElement(name = "classic-csv-drivers")
@ConfigurationProperties(moduleName = "NAS_CLASSIC_CSV_ACCT_DDRIVER", readWith = DBReader.class, writeWith = XMLWriter.class,synchronizeKey ="")
@XMLProperties(name = "classic-csv-drivers",schemaDirectories = {"system","schema"},configDirectories = {"conf","db","diameter","driver"})
public class DiameterClassicCSVAcctDriverConfigurable extends ClassicCSVAcctDriverConfigurable<NasClassicCSVAcctDriverConfigurationImpl> {

	public static final String MODULE="NAS_CLASSIC_CSV_ACCT_DDRIVER";
	private List<NasClassicCSVAcctDriverConfigurationImpl> nasClassicCSVAcctDriverConfList;
	
	public DiameterClassicCSVAcctDriverConfigurable() {
		nasClassicCSVAcctDriverConfList = new ArrayList<NasClassicCSVAcctDriverConfigurationImpl>();
	}
	
	@Override
	protected NasClassicCSVAcctDriverConfigurationImpl createConfigurationObject() {
		return new NasClassicCSVAcctDriverConfigurationImpl();
	}

	@Override
	protected String getDriverQuery() {
		String query = null;
		AAAServerConfigurable serverConfigurable = getConfigurationContext().get(AAAServerConfigurable.class);
		if (isTGPPServerEnabled(serverConfigurable)) {
			TGPPServerPolicyConfigurable policyConfigurable = getConfigurationContext().get(TGPPServerPolicyConfigurable.class);
			
			if (policyConfigurable.getSelectedDriverIds().size() > 0) {
				query = "select DRIVERINSTANCEID, DRIVERTYPEID,NAME from tblmdriverinstance where DRIVERTYPEID='"+ DriverTypes.NAS_CLASSIC_CSV_ACCT_DRIVER.value  
						+ "' AND (DRIVERINSTANCEID IN ("
						+ Strings.join(",", policyConfigurable.getSelectedDriverIds(), Strings.WITHIN_SINGLE_QUOTES) + ") "
						+ "OR DRIVERINSTANCEID IN (select DISTINCT DRIVERINSTANCEID from tblmnaspolicyauthdriverrel where naspolicyid in (" + getReadQueryForNasServicePolicyConfiguration() +")) "
						+ "OR DRIVERINSTANCEID IN (select DISTINCT DRIVERINSTANCEID from tblmnaspolicyacctdriverrel where naspolicyid in (" + getReadQueryForNasServicePolicyConfiguration() +")) "
						+ "OR DRIVERINSTANCEID IN (select DISTINCT DRIVERINSTANCEID from tblmeappolicyauthdriverrel where eappolicyid in (" + getReadQueryForEapServicePolicyConfiguration() +")) "
						+ "OR DRIVERINSTANCEID IN (select DISTINCT DRIVERINSTANCEID from TBLMEAPPOLICYADDDRIVERREL  where eappolicyid in (" + getReadQueryForEapServicePolicyConfiguration() +")) "
						+ "OR DRIVERINSTANCEID IN (select DISTINCT DRIVERINSTANCEID from TBLMNASADDAUTHDRIVERREL where naspolicyid in (" + getReadQueryForNasServicePolicyConfiguration() + "))" 
						+ "OR DRIVERINSTANCEID IN (select DISTINCT DRIVERINSTANCEID from TBLMNASADDAUTHDRIVERREL where naspolicyid in (" + getReadQueryForNasServicePolicyConfiguration() +"))"
						+ "OR NAME IN (select DISTINCT FAILUREARGUMENTS from TBLMROUTINGCONFFAILUREPARAMS where FAILUREACTION="+DiameterFailureConstants.RECORD.failureAction+"))";
			} else {
				query = getQueryWithoutTGPPDrivers();
			}
		} else {
			query = getQueryWithoutTGPPDrivers();
		}
		
		return query;
	}

	private String getQueryWithoutTGPPDrivers() {
		String query;
		query = "select DRIVERINSTANCEID, DRIVERTYPEID,NAME from tblmdriverinstance where DRIVERTYPEID='"+ DriverTypes.NAS_CLASSIC_CSV_ACCT_DRIVER.value +"' AND ((DRIVERINSTANCEID IN" +
				 "(select DISTINCT DRIVERINSTANCEID from tblmnaspolicyauthdriverrel where naspolicyid in (" + getReadQueryForNasServicePolicyConfiguration() +")) OR DRIVERINSTANCEID IN" +
				 "(select DISTINCT DRIVERINSTANCEID from tblmnaspolicyacctdriverrel where naspolicyid in (" + getReadQueryForNasServicePolicyConfiguration() +")) OR DRIVERINSTANCEID IN" +
				 "(select DISTINCT DRIVERINSTANCEID from tblmeappolicyauthdriverrel where eappolicyid in (" + getReadQueryForEapServicePolicyConfiguration() +")) OR DRIVERINSTANCEID IN" +
				 "(select DISTINCT DRIVERINSTANCEID from TBLMEAPPOLICYADDDRIVERREL  where eappolicyid in (" + getReadQueryForEapServicePolicyConfiguration() +")) OR DRIVERINSTANCEID IN" +
				 "(select DISTINCT DRIVERINSTANCEID from TBLMNASADDAUTHDRIVERREL where naspolicyid in (" + getReadQueryForNasServicePolicyConfiguration() +")))" +
				 "OR (NAME IN (select DISTINCT FAILUREARGUMENTS from TBLMROUTINGCONFFAILUREPARAMS where FAILUREACTION="+DiameterFailureConstants.RECORD.failureAction+")))";
		return query;
	}

	private boolean isTGPPServerEnabled(AAAServerConfigurable serverConfigurable) {
		return serverConfigurable.getconfiguredServicesMap().get(AAAServerConstants.DIA_TGPP_SERVER_SERVICE_ID) !=null 
				&& serverConfigurable.getconfiguredServicesMap().get(AAAServerConstants.DIA_TGPP_SERVER_SERVICE_ID);
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
	
	private String getReadQueryForNasServicePolicyConfiguration() {
		DiameterNasServiceConfigurable diameterNasServiceConfigurable = getConfigurationContext().get(DiameterNasServiceConfigurable.class);
		if(diameterNasServiceConfigurable == null) {
			return "''";
		}
		List<String> servicePolicies = diameterNasServiceConfigurable.getServicePolicies();
		if(servicePolicies == null || servicePolicies.size() == 0 || servicePolicies.contains(AAAServerConstants.ALL)) {
			return "select naspolicyid from tblmnaspolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + "'";
		}
		return "select naspolicyid from tblmnaspolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + 
				"' AND NAME IN ("+Strings.join(",", servicePolicies, Strings.WITHIN_SINGLE_QUOTES)+")";

	}
	
	private String getReadQueryForEapServicePolicyConfiguration() {

		DiameterEAPServiceConfigurable diameterEAPServiceConfigurable = getConfigurationContext().get(DiameterEAPServiceConfigurable.class);
		if(diameterEAPServiceConfigurable == null) {
			return "''";
		}
		List<String> servicePolicies = diameterEAPServiceConfigurable.getServicePolicies();
		if(servicePolicies == null || servicePolicies.size() == 0 || servicePolicies.contains(AAAServerConstants.ALL)) {
			return "select eappolicyid from tblmeappolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + "'";
		}
		return "select eappolicyid from tblmeappolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + 
				"' AND NAME IN ("+Strings.join(",", servicePolicies, Strings.WITHIN_SINGLE_QUOTES)+") ";

	}
}
