package com.elitecore.aaa.core.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.DBAuthDriverConfiguration;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.data.CacheParameters;

public abstract class DBAuthDriverConfigurationImpl implements DBAuthDriverConfiguration{

	private CacheParameters cacheParams ;
	private String userIdentity;
	private String tablename = "TBLRADIUSCUSTOMER";
	private int dbQueryTimeout = 2;
	private long maxQueryTimeoutCount = 200;
	private String driverInstanceId;
	private AccountDataFieldMapping accountDataFieldMapping;
	private String dsName;
	private String profileLookUpColumn = "USER_IDENTITY";
	private String driverName ="";
	private List<String> userIdentityAttributeList;
	
	public DBAuthDriverConfigurationImpl(){
		//required by Jaxb.
		accountDataFieldMapping = new AccountDataFieldMapping();
		userIdentityAttributeList = new ArrayList<String>();
		cacheParams=new CacheParameters();
		
	}
	@XmlElement(name = "user-identity-attributes")
	public String getUserIdentity() {
		return userIdentity;
	}
	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}

	public void setDSName(String dsName) {
		this.dsName = dsName;
	}
	public void setProfileLookUpColumnName(String profileLookUpColumn) {
		this.profileLookUpColumn = profileLookUpColumn;
	}
	
	@XmlElement(name = "cache-parameter-detail")
	public CacheParameters getCacheParams() {
		return cacheParams;
	}
	public void setCacheParams(CacheParameters cacheParams) {
		this.cacheParams = cacheParams;
	}
	public void setUserIdentityAttribute(List<String> userIdentityAttribute) {
		this.userIdentityAttributeList = userIdentityAttribute;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	public void setDbQueryTimeout(int dbQueryTimeout) {
		this.dbQueryTimeout = dbQueryTimeout;
	}
	public void setMaxQueryTimeoutCount(long maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	public void setAccountDataFieldMapping(AccountDataFieldMapping accountDataFieldMapping) {
		this.accountDataFieldMapping = accountDataFieldMapping;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	@XmlElement(name = "profile-lookup-column" ,type = String.class,defaultValue ="USER_IDENTITY")
	public String getProfileLookUpColumnName() {
		return this.profileLookUpColumn;
	}

	@XmlElement(name = "table-name" ,type = String.class)
	public String getTablename() {
		return tablename;
	}

	@XmlElement(name = "db-query-timeout" ,type = int.class)
	public int getDbQueryTimeout() {
		return dbQueryTimeout;
	}
	@XmlElement(name = "maximum-query-timeout-count" ,type = long.class)
	public long getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}
	@XmlElement(name = "id" ,type = String.class)
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	
	@XmlElement(name = "db-field-mapping-list")
	public AccountDataFieldMapping getAccountDataFieldMapping(){
		return this.accountDataFieldMapping;
	}
	@XmlElement(name = "datasource" ,type = String.class)
	public String getDSName() {		
		return this.dsName;
	}

	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		out.println();
		out.println(" -- DB Auth Driver Configuration -- ");
		out.println();
		out.println("    Driver Name = " + driverName);
		out.println("    Table Name = " + tablename);
		out.println("    DB Query Timeout = " + dbQueryTimeout);
		out.println("    Primary Column Name = " + this.cacheParams.getPrimaryKeyColumn());
		out.println("    Sequence Name = " + this.cacheParams.getSequenceName());
		out.println("    Maximum Query Timeout Count = " + maxQueryTimeoutCount);
		out.println("    Datasource Name = " + dsName);
		out.println("    User Identity Attributes = " + ((userIdentityAttributeList != null) ? userIdentityAttributeList : ""));
		out.println();
		out.println(" -- Field Mapping ( Logical Field Name = DB Field Name) -- ");
		out.println(accountDataFieldMapping.toString());

		out.println();

		out.close();
		return stringBuffer.toString();
	}

	@Override
	@XmlElement(name = "driver-name",type = String.class)
	public String getDriverName() {
		return driverName;
	}
	
	@XmlTransient
	public String getSequenceName() {
		return cacheParams.getSequenceName();
	}

	@Override
	@XmlTransient
	public String getPrimaryKeyColumn() {
		return cacheParams.getPrimaryKeyColumn();
	}

	@Override
	@XmlTransient
	public List<String> getUserIdentityAttributes() {
		return userIdentityAttributeList;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof DBAuthDriverConfigurationImpl))
			return false;
		
		DBAuthDriverConfigurationImpl other = (DBAuthDriverConfigurationImpl) obj;
		return this.driverInstanceId == other.driverInstanceId;
	}
}

