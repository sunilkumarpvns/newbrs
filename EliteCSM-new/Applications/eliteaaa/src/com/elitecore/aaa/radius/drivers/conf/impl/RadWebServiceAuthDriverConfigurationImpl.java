package com.elitecore.aaa.radius.drivers.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.radius.drivers.conf.RadWebServiceAuthDriverConfiguration;
import com.elitecore.core.commons.configuration.LoadConfigurationException;

@XmlType(propOrder = {})
public class RadWebServiceAuthDriverConfigurationImpl implements RadWebServiceAuthDriverConfiguration{
	
	private String driverInstanceId;
	private String driverName="";
	private String serviceAddress;
	private String imsiAttribute="0:1";
	private AccountDataFieldMapping accountDataFieldMapping;
	private int maxQueryTimeoutCount=50;
	private List<String> userIdentityAttribute = null;
	private String userIdentities;
	private static final int DEFAULT_STATUS_CHECK_DURATION=120;
	private int statusCheckDuration=DEFAULT_STATUS_CHECK_DURATION;
	private SimpleDateFormat expiryDatePatterns[];
	
	public RadWebServiceAuthDriverConfigurationImpl(){
		//required by Jaxb.
		accountDataFieldMapping = new AccountDataFieldMapping();
		userIdentityAttribute = new ArrayList<String>();
	}
	
	@XmlElement(name = "user-identity-attributes")
	public String getUserIdentities() {
		return userIdentities;
	}

	public void setUserIdentities(String userIdentities) {
		this.userIdentities = userIdentities;
	}
	@Override
	@XmlElement(name = "id",type = String.class)
	public String getDriverInstanceId() {
		return driverInstanceId;
	}

	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public void setAccountDataFieldMapping(AccountDataFieldMapping accountDataFieldMapping) {
		this.accountDataFieldMapping = accountDataFieldMapping;
	}

	public void setUserIdentityAttribute(List<String> userIdentityAttribute) {
		this.userIdentityAttribute = userIdentityAttribute;
	}

	public void setExpiryDatePatterns(SimpleDateFormat[] expiryDatePatterns) {
		this.expiryDatePatterns = expiryDatePatterns;
	}

	@XmlTransient
	@Override
	public DriverTypes getDriverType() {
		return DriverTypes.RAD_WEBSERVICE_AUTH_DRIVER;
	}

	@Override
	@XmlElement(name = "driver-name",type = String.class)
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String name){
		this.driverName = name;
	}

	@Override
	@XmlElement(name = "service-address",type = String.class)
	public String getServiceAddress() {
		return serviceAddress;
	}
	
	public void setServiceAddress(String serviceAddress){
		this.serviceAddress = serviceAddress;
	}
	@Override
	@XmlElement(name = "status-check-duration",type = int.class)
	public int getStatusCheckDuration() {
		return statusCheckDuration;
	}
	public void setStatusCheckDuration(int statusCheck){
		this.statusCheckDuration = statusCheck;
	}
	
	@Override
	@XmlTransient
	public SimpleDateFormat[] getExpiryDatePatterns() {
		return expiryDatePatterns;
	}

	@Override
	public void readConfiguration() throws LoadConfigurationException {}
	
	@Override
	
	@XmlElement(name = "webservice-field-mapping-list")
	public AccountDataFieldMapping getAccountDataFieldMapping() {
		return accountDataFieldMapping;
	}
	
	@XmlElement(name ="maximum-query-timeout-count",type = int.class)
	@Override
	public int getMaxQueryTimeoutCount() {
		return this.maxQueryTimeoutCount;
	}
	public void setMaxQueryTimeoutCount(int maxQueryTimeOut){
		this.maxQueryTimeoutCount =maxQueryTimeOut;
	}
	
	@Override
	@XmlElement(name = "imsi-attribute",type = String.class)
	public String getIMSIAttribute() {
		return imsiAttribute;
	}
	public void setIMSIAttribute(String imsiAttribute){
		this.imsiAttribute = imsiAttribute;
	}
	
	@Override
	@XmlTransient
	public List<String> getUserIdentityAttributes() {
		return userIdentityAttribute;
	}
	
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();
		out.println();
		out.println(" -- Web Service Auth Driver Configuration -- ");
		out.println();
		out.println("      -------------------------------------");
		out.println("    Service Address 	 = " + serviceAddress);
		out.println("    IMSI Attribute		 = " + imsiAttribute);
		out.println("    User Identity Attributes = " + ((userIdentityAttribute != null) ? userIdentityAttribute : ""));
		out.println("      -------------------------------------");
		out.println();
		out.println(" -- Field Mapping ( Logical Field Name = Web Service Field Name) -- ");
		out.println(accountDataFieldMapping);

		out.close();
		return stringBuffer.toString();
	}
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof RadWebServiceAuthDriverConfigurationImpl))
			return false;
		
		RadWebServiceAuthDriverConfigurationImpl other = (RadWebServiceAuthDriverConfigurationImpl) obj;
		return this.driverInstanceId == other.driverInstanceId;
	}
}

