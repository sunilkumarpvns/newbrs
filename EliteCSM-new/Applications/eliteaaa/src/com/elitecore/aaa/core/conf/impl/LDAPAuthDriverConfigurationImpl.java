package com.elitecore.aaa.core.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.LDAPAuthDriverConfiguration;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;

@XmlType(propOrder = {})
public abstract class LDAPAuthDriverConfigurationImpl implements LDAPAuthDriverConfiguration{
	
	private String expriryPatterns ="MM/dd/yyyy";
	private String userIdentity;
	private String ldapDriverId;
	private String driverInstanceId;	
	private int passwordDecryptType =0;
	private String driverName="";
	private long queryMaxExecTime=1;	
	private String dsName;
	private List<String> userIdentityAttribute = null;
	private int statusCheckDuration=120;
	private long maxQueryTimeoutCount = 100;
	private SimpleDateFormat[] expiryDatePatterns;	
	private AccountDataFieldMapping accountDataFieldMapping = null;
	private LDAP_SEARCH_SCOPE searchScope;
	private String searchFilter;
	
	public LDAPAuthDriverConfigurationImpl() {
		//required by Jaxb.
		accountDataFieldMapping = new AccountDataFieldMapping();
		userIdentityAttribute = new ArrayList<String>();
		searchFilter = "";
		searchScope = LDAP_SEARCH_SCOPE.SCOPE_SUB;
	}

	@XmlElement(name = "user-identity-attributes")
	public String getUserIdentity() {
		return userIdentity;
	}
	
	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
	}

	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}
	
	@XmlElement(name = "expiry-date-pattern",type =String.class,defaultValue ="MM/dd/yyyy")
	public String getExpriryPatterns() {
		return expriryPatterns;
	}

	public void setExpriryPatterns(String expriryPatterns) {
		this.expriryPatterns = expriryPatterns;
	}
	public void setLDAPdriverid(String ldapDriverId) {
		this.ldapDriverId = ldapDriverId;
	}

	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	public void setPasswordDecryptType(int passwordDecryptType) {
		this.passwordDecryptType = passwordDecryptType;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public void setQueryMaxExecTime(long queryMaxExecTime) {
		this.queryMaxExecTime = queryMaxExecTime;
	}

	public void setDSName(String dsName) {
		this.dsName = dsName;
	}

	public void setUserIdentityAttribute(List<String> userIdentityAttribute) {
		this.userIdentityAttribute = userIdentityAttribute;
	}

	public void setStatusCheckDuration(int statusCheckDuration) {
		this.statusCheckDuration = statusCheckDuration;
	}

	public void setMaxQueryTimeoutCount(long maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
	}

	public void setExpiryDatePatterns(SimpleDateFormat[] expiryDatePatterns) {
		this.expiryDatePatterns = expiryDatePatterns;
	}

	public void setAccountDataFieldMapping(
			AccountDataFieldMapping accountDataFieldMapping) {
		this.accountDataFieldMapping = accountDataFieldMapping;
	}


	public void setSearchScope(LDAP_SEARCH_SCOPE searchScope) {
		this.searchScope = searchScope;
	}

	@XmlTransient
	public String getLDAPdriverid() {		
		return this.ldapDriverId;
	}
	
	@XmlTransient
	public SimpleDateFormat[] getExpiryDatePatterns() {
		return this.expiryDatePatterns;
	}

	@XmlElement(name = "search-filter",type = String.class)
	public String getSearchFilter() {
		return searchFilter;
	}

	@XmlElement(name = "search-scope", defaultValue = "SCOPE_SUB")
	public LDAP_SEARCH_SCOPE getSearchScope() {
		return searchScope;
	}
	
	@XmlElement(name = "password-decrypt-type",type = int.class)
	public int getPasswordDecryptType() {
		return this.passwordDecryptType;
	}
	@XmlElement(name ="max-query-execution-time",type =long.class,defaultValue ="1")
	public long getQueryMaxExecTime() {
		return this.queryMaxExecTime;
	}
	@Override
	@XmlElement(name = "id",type = String.class)
	public String getDriverInstanceId() {
		return this.driverInstanceId;
	}

	@XmlElement(name = "ldap-field-mapping-list")
	public AccountDataFieldMapping getAccountDataFieldMapping(){
		return this.accountDataFieldMapping;
	}

	@XmlElement(name = "datasource",type = String.class)
	@Override
	public String getDSName() {		
		return this.dsName;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();
		out.println();
		out.println(" --  LDAP Auth Driver Configuration -- ");
		out.println();
		out.println("    Passowrd Decrypt Type    = " + passwordDecryptType);
		out.println("    Query Max Execution Time = " + queryMaxExecTime);
		out.println("    Datasource Name          = " + dsName);
		out.println("    Search Scope             = " + searchScope);
		out.println("    Search Filter            = " + (searchFilter != null ? searchFilter : ""));
		out.println("    Max Query Timeout Count  = " + maxQueryTimeoutCount);
		out.println("    User Identity Attributes = " + ((userIdentityAttribute != null) ? userIdentityAttribute : ""));
		out.print  ("    Expiry Date Patterns     =");
		for(int i=0;i<expiryDatePatterns.length;i++){
			out.print("  " + expiryDatePatterns[i].toPattern());
		}
		out.println();
		out.println(" -- Field Mapping ( Logical Field Name = LDAP Field Name) -- ");
		out.println();
		out.println(String.valueOf(accountDataFieldMapping));
		out.println();

		out.close();
		return stringBuffer.toString();
	 
		}

	@Override
	@XmlElement(name ="status-check-duration",type = int.class)
	public int getStatusCheckDuration() {
		return this.statusCheckDuration;
	}

	@Override
	@XmlElement(name ="maximum-query-timeout-count",type = long.class)
	public long getMaxQueryTimeoutCount() {
		return this.maxQueryTimeoutCount;
	}

	@XmlElement(name = "driver-name",type = String.class)
	public String getDriverName() {
		return driverName;
	}

	@Override
	@XmlTransient
	public List<String> getUserIdentityAttributes() {
		return userIdentityAttribute;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof LDAPAuthDriverConfigurationImpl))
			return false;
		
		LDAPAuthDriverConfigurationImpl other = (LDAPAuthDriverConfigurationImpl) obj;
		return this.ldapDriverId == other.ldapDriverId;
	}
}
