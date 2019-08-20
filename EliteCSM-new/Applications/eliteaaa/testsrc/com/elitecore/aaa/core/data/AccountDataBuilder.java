package com.elitecore.aaa.core.data;

import java.util.Date;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AccountDataBuilder {

	private AccountData accountData = new AccountData();
	
	public AccountDataBuilder userIdentity(String userIdentity) {
		accountData.setUserIdentity(userIdentity);
		return this;
	}
	
	public AccountDataBuilder imsi(String imsi) {
		accountData.setImsi(imsi);
		return this;
	}
	
	public AccountDataBuilder authorizationPolicy(String authorizationPolicy) {
		accountData.setAuthorizationPolicy(authorizationPolicy);
		return this;
	}
	
	public AccountData build() {
		return accountData;
	}

	public AccountDataBuilder username(String username) {
		accountData.setUserName(username);
		return this;
	}

	public AccountDataBuilder imei(String imei) {
		accountData.setImei(imei);
		return this;
	}

	public AccountDataBuilder msisdn(String msisdn) {
		accountData.setMsisdn(msisdn);
		return this;
	}

	public AccountDataBuilder meid(String meid) {
		accountData.setMeid(meid);
		return this;
	}

	public AccountDataBuilder mdn(String mdn) {
		accountData.setMdn(mdn);
		return this;
	}

	public AccountDataBuilder cui(String cui) {
		accountData.setCUI(cui);
		return this;
	}

	public AccountDataBuilder passwordCheck(String passwordCheck) {
		accountData.setPasswordCheck(passwordCheck);
		return this;
	}

	public AccountDataBuilder password(String password) {
		accountData.setPassword(password);
		return this;
	}

	public AccountDataBuilder encryptionType(String encryptionType) {
		accountData.setEncryptionType(encryptionType);
		return this;
	}

	public AccountDataBuilder customerType(String customerType) {
		accountData.setCustomerType(customerType);
		return this;
	}

	public AccountDataBuilder serviceType(String serviceType) {
		accountData.setServiceType(serviceType);
		return this;
	}

	public AccountDataBuilder callingStationId(String callingStationId) {
		accountData.setCallingStationId(callingStationId);
		return this;
	}

	public AccountDataBuilder calledStationId(String calledStationId) {
		accountData.setCalledStationId(calledStationId);
		return this;
	}

	public AccountDataBuilder maxSessionTime(long maxSessionTime) {
		accountData.setMaxSessionTime(maxSessionTime);
		return this;
	}

	public AccountDataBuilder macValidation(boolean macValidation) {
		accountData.setMacValidation(macValidation);
		return this;
	}

	public AccountDataBuilder checkItems(String checkItems) {
		accountData.setCheckItem(checkItems);
		return this;
	}

	public AccountDataBuilder rejectItems(String rejectItems) {
		accountData.setRejectItem(rejectItems);
		return this;
	}

	public AccountDataBuilder replyItems(String replyItems) {
		accountData.setReplyItem(replyItems);
		return this;
	}

	public AccountDataBuilder concurrentLoginPolicy(String concurrentLoginPolicy) {
		accountData.setConcurrentLoginPolicy(concurrentLoginPolicy);
		return this;
	}

	public AccountDataBuilder accessPolicy(String acessPolicy) {
		accountData.setAccessPolicy(acessPolicy);
		return this;
	}

	public AccountDataBuilder additionalPolicy(String additionalPolicy) {
		accountData.setAdditionalPolicy(additionalPolicy);
		return this;
	}

	public AccountDataBuilder creditLimit(long creditLimit) {
		accountData.setCreditLimit(creditLimit);
		return this;
	}

	public AccountDataBuilder expiryDate(Date expiryDate) {
		accountData.setExpiryDate(expiryDate);
		return this;
	}

	public AccountDataBuilder email(String emailId) {
		accountData.setEmailId(emailId);
		return this;
	}

	public AccountDataBuilder creditLimitCheck(boolean creditLimitCheck) {
		accountData.setCreditLimitCheckRequired(true);
		return this;
	}

	public AccountDataBuilder customerStatus(String status) {
		accountData.setAccountStatus(status);
		return this;
	}

	public AccountDataBuilder param1(String param1) {
		accountData.setParam1(param1);
		return this;
	}

	public AccountDataBuilder param2(String param2) {
		accountData.setParam2(param2);
		return this;
	}

	public AccountDataBuilder param3(String param3) {
		accountData.setParam3(param3);
		return this;
	}

	public AccountDataBuilder param4(String param4) {
		accountData.setParam4(param4);
		return this;
	}

	public AccountDataBuilder param5(String param5) {
		accountData.setParam5(param5);
		return this;
	}

	public AccountDataBuilder hotlinePolicy(String hotlinePolicy) {
		accountData.setHotlinePolicy(hotlinePolicy);
		return this;
	}

	public AccountDataBuilder gracePolicy(String gracePolicy) {
		accountData.setGracePolicy(gracePolicy);
		return this;
	}

	public AccountDataBuilder callbackId(String callbackId) {
		accountData.setCallbackId(callbackId);
		return this;
	}

	public AccountDataBuilder deviceVendor(String vendor) {
		accountData.setDeviceVendor(vendor);
		return this;
	}

	public AccountDataBuilder deviceName(String name) {
		accountData.setDeviceName(name);
		return this;
	}

	public AccountDataBuilder devicePort(String port) {
		accountData.setDevicePort(port);
		return this;
	}

	public AccountDataBuilder geoLocation(String location) {
		accountData.setGeoLocation(location);
		return this;
	}

	public AccountDataBuilder deviceVLAN(String vlan) {
		accountData.setDeviceVLAN(vlan);
		return this;
	}

	public AccountDataBuilder dynamicCheckItems(String dynamicCheckItems) {
		accountData.setDynamicCheckItems(dynamicCheckItems);
		return this;
	}

	public AccountDataBuilder framedIPv4Address(String framedIPv4Address) {
		accountData.setFramedIPv4Address(framedIPv4Address);
		return this;
	}

	public AccountDataBuilder framedIPv6Prefix(String prefix) {
		accountData.setFramedIPv6Prefix(prefix);
		return this;
	}

	public AccountDataBuilder framedPool(String pool) {
		accountData.setFramedPool(pool);
		return this;
	}

	public AccountDataBuilder policyGroup(String group) {
		accountData.setAuthorizationPolicyGroup(group);
		return this;
	}

	public AccountDataBuilder groupName(String groupName) {
		accountData.setGroupName(groupName);
		return this;
	}

	public AccountDataBuilder customerServices(String customerServices) {
		accountData.setCustomerServices(customerServices);
		return this;
	}
	
	
}
