package com.elitecore.aaa.core.conf.impl;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.HSSAuthDriverConfiguration;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;

@XmlType(propOrder = {})
public abstract class HSSAuthDriverConfigurationImpl implements HSSAuthDriverConfiguration {

	private String driverInstanceId;
	private String driverName = "HSS-Driver";
	private AccountDataFieldMapping accountDataFieldMapping;
	private List<String> userIdentityAttributes;
	private int requestTimeout = 3000;
	private String applicationId = "10415:16777265";
	private String userIdString = "0:1";
	private int commandCode = CommandCode.MULTIMEDIA_AUTHENTICATION.code;
	private List<PeerInfoImpl> peerList;
	private String additionalAttibutes;
	private int numberOfTriplets = 3;
	
	@Override
	@XmlElement(name = "driver-name",type = String.class)
	public String getDriverName() {
		return driverName;
	}
	
	@Override
	@XmlElement(name = "tgpp-application-id",type = String.class, defaultValue="10415:16777265")
	public String getApplicationId() {
		return applicationId;
	}
	
	@Override
	@XmlElement(name = "command-code",type = int.class, defaultValue="303")
	public int getCommandCode() {
		return commandCode;
	}
	
	@Override
	@XmlTransient
	public List<String> getUserIdentityAttributes() {
		return userIdentityAttributes;
	}

	@Override
	@XmlElement(name = "request-timeout", defaultValue="3000",type = int.class)
	public int getRequestTimeout(){
		return requestTimeout;
	}
	
	@Override
	@XmlElement(name = "db-field-mapping-list")
	public AccountDataFieldMapping getAccountDataFieldMapping() {
		return accountDataFieldMapping;
	}
	
	@Override
	public int getNumberOfTriplets() {
		return numberOfTriplets;
	}

	@Override
	@XmlTransient
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	
	@XmlElement(name = "user-identity-attributes", type = String.class , defaultValue = "0:1")
	public String getUserIdentity() {
		return userIdString;
	}
	
	@XmlElement(name = "additional-attributes", type=String.class)
	public String getAdditionalAttributes() {
		return additionalAttibutes;
	}
	
	@Override
	@XmlElementWrapper(name="peer-list")
	@XmlElement(name="peer-info")
	public List<PeerInfoImpl> getPeerList() {
		return peerList;
	}
	
	public void setPeerList(List<PeerInfoImpl> peerList) {
		this.peerList = peerList;
	}

	public void setUserIdentity(String userIdString) {
		this.userIdString = userIdString;
	}
	
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	
	public void setRequestTimeout(int requestTimeout){
		this.requestTimeout = requestTimeout;
	}

	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public void setAccountDataFieldMapping(
			AccountDataFieldMapping accountDataFieldMapping) {
		this.accountDataFieldMapping = accountDataFieldMapping;
	}

	public void setUserIdentityAttributes(List<String> userIdentityAttributes) {
		this.userIdentityAttributes = userIdentityAttributes;
	}
	
	public void setAdditionalAttributes(String additionalAttributeStr) {
		this.additionalAttibutes = additionalAttributeStr;
	}
	
	public void setCommandCode(int commandCode) {
		this.commandCode = commandCode;
	}
	
	public void setNumberOfTriplets(int numberOfTriplets) {
		this.numberOfTriplets = numberOfTriplets;
	}
	
}
