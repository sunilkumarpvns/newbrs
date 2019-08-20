package com.elitecore.corenetvertex.spr.data.impl;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.SpInterfaceType;
import com.elitecore.corenetvertex.core.conf.FailReason;
import com.elitecore.corenetvertex.core.ldap.LDAPDataSource;
import com.elitecore.corenetvertex.ldap.LdapBaseDn;
import com.elitecore.corenetvertex.ldap.LdapData;
import com.elitecore.corenetvertex.sm.spinterface.LdapSpInterfaceData;
import com.elitecore.corenetvertex.sm.spinterface.SpInterfaceData;
import com.elitecore.corenetvertex.sm.spinterface.SpInterfaceFieldMappingData;

import java.util.ArrayList;

import com.elitecore.corenetvertex.spr.LDAPSPInterfaceConfiguration;
import com.elitecore.corenetvertex.spr.data.ProfileFieldMapping;
import com.elitecore.corenetvertex.util.DataSourceProvider;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;

import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * @author Harsh Patel
 *
 */
public class LDAPSPInterfaceConfigurationImpl implements LDAPSPInterfaceConfiguration {

	private static final String MODULE = "LDAP-SP-INTERFACE-CONF-IMPL";
	private long maxQueryTimeoutCount;
	private int passwordDecryptType;
	private String expiryDatePattern;
	private LDAPDataSource ldapDataSource;
	private String name;
	private ProfileFieldMapping profileFieldMapping;
	private String userPrefix;
	private ArrayList<String> searchBaseDNs;

	public LDAPSPInterfaceConfigurationImpl(long maxQueryTimeoutCount, int passwordDecryptType,
											String expiryDatePattern,
											LDAPDataSource ldapDataSource, String driverName, ProfileFieldMapping profileFieldMapping,
											String userPrefix,
											ArrayList<String> searchBaseDNs) {

		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
		this.passwordDecryptType = passwordDecryptType;
		this.expiryDatePattern = expiryDatePattern;
		this.ldapDataSource = ldapDataSource;
		this.name = driverName;
		this.profileFieldMapping = profileFieldMapping;
		this.userPrefix = userPrefix;
		this.searchBaseDNs = searchBaseDNs;

	}

	public LDAPSPInterfaceConfigurationImpl() {
	}
	

	public void setQueryMaxExecTime(long maxQueryTimeOutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeOutCount;
	}

	public void setPasswordDecryptType(int passwordDecryptType) {
		this.passwordDecryptType = passwordDecryptType;
	}

	public void setExpiryDatePatterns(String expiryDatePattern) {
		this.expiryDatePattern = expiryDatePattern;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This method returns the data source name
	 * 
	 * @return
	 */
	@Override
	public LDAPDataSource getLdapDataSource() {
		return this.ldapDataSource;
	}

	@Override
	public String getExpiryDatePattern() {
		return expiryDatePattern;
	}


	@Override
	public int getPasswordDecryptType() {
		return passwordDecryptType;
	}

	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- LDAP SP Interface Detail -- ");
		toString(builder);
		return builder.toString();
	}

	@Override
	public String getName() {
		return name;
	}

	public void setAccountDataFieldMapping(ProfileFieldMapping accountDataFieldMapping) {
		this.profileFieldMapping = accountDataFieldMapping;
	}

	@Override
	public ProfileFieldMapping getProfileFieldMapping() {
		return profileFieldMapping;
	}

	@Override
	public SpInterfaceType getType() {
		return SpInterfaceType.LDAP_SP_INTERFACE;
	}

	@Override
	public String getUserPrefix() {
		return userPrefix;
	}

	@Override
	public ArrayList<String> getSearchBaseDnList() {
		return searchBaseDNs;
	}

	@Override
	public long getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}


	public static LDAPSPInterfaceConfiguration create(SpInterfaceData spInterfaceData,
													  DataSourceProvider dataSourceProvider,
													  FailReason failReason) {


		LdapSpInterfaceData ldapSPInterfaceData = spInterfaceData.getLdapSpInterfaceData();

		if(ldapSPInterfaceData == null) {
			failReason.add("SP interface not found with name: " + spInterfaceData.getName());
			return null;
		}

		LdapData ldapDSData = ldapSPInterfaceData.getLdapData();

		if (ldapDSData == null) {
			failReason.add("DataSource not found in SP interface: " + spInterfaceData.getName());
		}



		ProfileFieldMapping profileFieldMapping = new ProfileFieldMapping();
		List<SpInterfaceFieldMappingData> ldapFieldMappingDatas = ldapSPInterfaceData.getSpInterfaceFieldMappingDatas();
		if(Collectionz.isNullOrEmpty(ldapFieldMappingDatas)) {
			failReason.add("Field mapping not configured in SP interface: " + spInterfaceData.getName());
		}



		for(int fiedlMappingIndex=0; fiedlMappingIndex<ldapFieldMappingDatas.size(); fiedlMappingIndex++) {
			SpInterfaceFieldMappingData fieldMapping = ldapFieldMappingDatas.get(fiedlMappingIndex);
			profileFieldMapping.setFieldMapping(fieldMapping.getLogicalName(), fieldMapping.getFieldName());
		}

		ArrayList<String> searchBaseDnDetails = null;
		if(ldapDSData != null) {
			List<LdapBaseDn> ldapBaseDnDetailDatas = ldapSPInterfaceData.getLdapData().getLdapBaseDns();
			if(Collectionz.isNullOrEmpty(ldapBaseDnDetailDatas)) {
				failReason.add("LDAP search base dn not configured in data source:" + ldapDSData.getName());
			}
			searchBaseDnDetails = new ArrayList(ldapBaseDnDetailDatas.size());

			for(LdapBaseDn ldapBaseDn : ldapBaseDnDetailDatas) {
				searchBaseDnDetails.add(ldapBaseDn.getSearchBaseDn());
			}

		}



		Integer maxQueryTimeoutCount = ldapSPInterfaceData.getMaxQueryTimeoutCount();
		if(maxQueryTimeoutCount == null) {
			if(getLogger().isWarnLogLevel())
				getLogger().warn(MODULE, "Taking 3 as maximum query timeout count. Reason: Maximum query timeout count not configured in sp interface:" + spInterfaceData.getName());
			maxQueryTimeoutCount = 3;
		}

		FailReason dsFailReason = new FailReason("DB DS");
		LDAPDataSource ldapDataSource = dataSourceProvider.getLDAPDataSource(ldapSPInterfaceData.getLdapData(), failReason);
		failReason.addChildModuleFailReasonIfNotEmpty(dsFailReason);

		if(failReason.isEmpty() == false) {
			return null;
		}

		return new LDAPSPInterfaceConfigurationImpl(
				maxQueryTimeoutCount,
				ldapSPInterfaceData.getPasswordDecryptType(),
				ldapSPInterfaceData.getExpiryDatePattern(),
				ldapDataSource,
				spInterfaceData.getName(),
				profileFieldMapping,
				ldapSPInterfaceData.getLdapData().getUserDnPrefix(),
				searchBaseDnDetails);
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.append("Name", name);
		builder.append("Passowrd Decrypt Type", passwordDecryptType);
		builder.append("Max Query Timeout Count", maxQueryTimeoutCount);
		builder.append("LDAP Datasource Name", ldapDataSource);
		builder.append("Expiry Date Pattern", expiryDatePattern);
		builder.append("User Prefix", userPrefix);
		builder.appendChild("Search Base DNs", searchBaseDNs);
	}
}
