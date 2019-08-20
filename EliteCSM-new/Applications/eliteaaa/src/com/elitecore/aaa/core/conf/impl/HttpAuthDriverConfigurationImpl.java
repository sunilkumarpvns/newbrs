package com.elitecore.aaa.core.conf.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.HttpAuthDriverConfiguration;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;

public abstract class HttpAuthDriverConfigurationImpl implements HttpAuthDriverConfiguration{

	private String driverInstanceId;
	private String driverName ="";
	private String httpURL;
	private String httpAuthDriverId;
	private int statusCheckDuration;
	private String userIdentity;
	private int maxQueryTimeoutCount;
	private String strExpriryPatterns ="MM/dd/yyyy";
	private SimpleDateFormat[] expiryPatterns;
	private AccountDataFieldMapping accountDataFieldMapping;
	private List<String> userIdentityAttribute = null;

	public HttpAuthDriverConfigurationImpl() {
		//required by Jaxb.
		this.accountDataFieldMapping = new AccountDataFieldMapping();
		userIdentityAttribute = new ArrayList<String>();
	}

	@XmlElement(name = "user-identity-attributes",type = String.class)
	public String getUserIdentity() {
		return userIdentity;
	}

	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}

	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public void setHttpURL(String httpURL) {
		this.httpURL = httpURL;
	}

	public void setHttpAuthDriverId(String httpAuthDriverId) {
		this.httpAuthDriverId = httpAuthDriverId;
	}

	public void setStatusCheckDuration(int statusCheckDuration) {
		this.statusCheckDuration = statusCheckDuration;
	}

	public void setMaxQueryTimeoutCount(int maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
	}

	public void setExpiryPatterns(SimpleDateFormat[] expiryPatterns) {
		this.expiryPatterns = expiryPatterns;
	}

	public void setAccountDataFieldMapping(AccountDataFieldMapping accountDataFieldMapping) {
		this.accountDataFieldMapping = accountDataFieldMapping;
	}

	public void setUserIdentityAttribute(List<String> userIdentityAttribute) {
		this.userIdentityAttribute = userIdentityAttribute;
	}

	@Override
	@XmlElement(name = "id",type = String.class)
	public String getDriverInstanceId() {
		return driverInstanceId;
	}

	@Override
	@XmlElement(name = "driver-name",type = String.class)
	public String getDriverName() {
		return driverName;
	}
	
	@Override
	@XmlElement(name = "http-field-mapping-list")
	public AccountDataFieldMapping getAccountDataFieldMapping() {
		return this.accountDataFieldMapping;
	}

	@Override
	@XmlTransient
	public String getHttpAuthDriverId() {
		return this.httpAuthDriverId;
	}

	@Override
	@XmlElement(name = "http-url",type = String.class)
	public String getHttpURL() {
		return this.httpURL;
	}

	@Override
	@XmlElement(name = "maximum-query-timeout-count")
	public int getMaxQueryTimeoutCount() {
		return this.maxQueryTimeoutCount;
	}

	@Override
	@XmlElement(name = "status-check-duration",type = int.class)
	public int getStatusCheckDuration() {
		return this.statusCheckDuration;
	}
	@XmlTransient
	public SimpleDateFormat[] getExpiryPatterns() {
		return this.expiryPatterns;
	}

	@Override
	@XmlTransient
	public List<String> getUserIdentityAttributes() {
		return userIdentityAttribute;
	}

	@XmlElement(name = "expiry-date-formates",type = String.class,defaultValue ="MM/dd/yyyy")
	public String getStrExpriryPatterns() {
		return strExpriryPatterns;
	}

	public void setStrExpriryPatterns(String strExpriryPatterns) {
		this.strExpriryPatterns = strExpriryPatterns;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof HttpAuthDriverConfigurationImpl))
			return false;
		
		HttpAuthDriverConfigurationImpl other = (HttpAuthDriverConfigurationImpl) obj;
		return this.driverInstanceId == other.driverInstanceId;
	}
}
