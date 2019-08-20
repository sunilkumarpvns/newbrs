package com.elitecore.nvsmx.staff;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.SpInterfaceType;
import com.elitecore.corenetvertex.core.conf.FailReason;
import com.elitecore.corenetvertex.core.ldap.LDAPDataSource;
import com.elitecore.corenetvertex.ldap.LdapBaseDn;
import com.elitecore.corenetvertex.ldap.LdapData;
import com.elitecore.corenetvertex.sm.acl.LdapAuthConfigurationData;
import com.elitecore.corenetvertex.sm.acl.LdapAuthConfigurationFieldMappingData;
import com.elitecore.corenetvertex.util.DataSourceProvider;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to map LDAP Datasource with user prefixes and DNs.
 * @author dhyani.raval
 */
public class StaffLDAPConfiguration {

    private LDAPDataSource ldapDataSource;
    private StaffLDAPProfileFieldMapping staffLDAPProfileFieldMapping;
    private String userPrefix;
    private ArrayList<String> searchBaseDNs;

    public StaffLDAPConfiguration(LDAPDataSource ldapDataSource, StaffLDAPProfileFieldMapping staffLDAPProfileFieldMapping, String userPrefix, ArrayList<String> searchBaseDNs) {

        this.ldapDataSource = ldapDataSource;
        this.staffLDAPProfileFieldMapping = staffLDAPProfileFieldMapping;
        this.userPrefix = userPrefix;
        this.searchBaseDNs = searchBaseDNs;

    }

    public LDAPDataSource getLdapDataSource() {
        return this.ldapDataSource;
    }

    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- LDAP Staff Interface Detail -- ");
        toString(builder);
        return builder.toString();
    }

    public StaffLDAPProfileFieldMapping getStaffLDAPProfileFieldMapping() {
        return staffLDAPProfileFieldMapping;
    }

    public SpInterfaceType getType() {
        return SpInterfaceType.LDAP_SP_INTERFACE;
    }

    public String getUserPrefix() {
        return userPrefix;
    }

    public ArrayList<String> getSearchBaseDnList() {
        return searchBaseDNs;
    }

    public static @Nullable StaffLDAPConfiguration create(LdapAuthConfigurationData ldapAuthConfigurationData, FailReason failReason, DataSourceProvider dataSourceProvider) {

        if(ldapAuthConfigurationData == null) {
            failReason.add("No LDAP configuration found.");
            return null;
        }

        LdapData ldapDSData = ldapAuthConfigurationData.getLdapData();

        StaffLDAPProfileFieldMapping staffLDAPProfileFieldMapping = new StaffLDAPProfileFieldMapping();
        List<LdapAuthConfigurationFieldMappingData> ldapFieldMappingDatas = ldapAuthConfigurationData.getLdapAuthConfigurationFieldMappingDataList();
        if(Collectionz.isNullOrEmpty(ldapFieldMappingDatas)) {
            failReason.add("Field mapping not configured in LDAP Authentication");
        }

        for(int fieldMappingIndex=0; fieldMappingIndex<ldapFieldMappingDatas.size(); fieldMappingIndex++) {
            LdapAuthConfigurationFieldMappingData fieldMapping = ldapFieldMappingDatas.get(fieldMappingIndex);
            staffLDAPProfileFieldMapping.setFieldMapping(fieldMapping.getStaffAttribute(), fieldMapping.getLdapAttribute());
        }

        ArrayList<String> searchBaseDnDetails = null;
        if(ldapDSData != null) {
            List<LdapBaseDn> ldapBaseDnDetailDatas = ldapAuthConfigurationData.getLdapData().getLdapBaseDns();
            if(Collectionz.isNullOrEmpty(ldapBaseDnDetailDatas)) {
                failReason.add("LDAP search base dn not configured in data source:" + ldapDSData.getName());
            }
            searchBaseDnDetails = new ArrayList(ldapBaseDnDetailDatas.size());

            for(LdapBaseDn ldapBaseDn : ldapBaseDnDetailDatas) {
                searchBaseDnDetails.add(ldapBaseDn.getSearchBaseDn());
            }

        }


        FailReason dsFailReason = new FailReason("LDAP DS");
        LDAPDataSource ldapDataSource = dataSourceProvider.getLDAPDataSource(ldapAuthConfigurationData.getLdapData(), failReason);
        failReason.addChildModuleFailReasonIfNotEmpty(dsFailReason);

        if(failReason.isEmpty() == false) {
            return null;
        }

        return new StaffLDAPConfiguration(
                ldapDataSource,
                staffLDAPProfileFieldMapping,
                ldapAuthConfigurationData.getLdapData().getUserDnPrefix(),
                searchBaseDnDetails);
    }

    public void toString(IndentingToStringBuilder builder) {
        builder.append("LDAP Datasource Name", ldapDataSource);
        builder.append("User Prefix", userPrefix);
        builder.appendChild("Search Base DNs", searchBaseDNs);
    }
}
