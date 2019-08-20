package com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.data;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.elitesm.ws.rest.adapter.StringToDateAdapter;

@XmlRootElement(name = "subscriber-profile")
public class SubscriberProfileData {

	private String subscriberProfileId;
	private String userName;
	private String password;
	private Long encryptionType;
	private String customerStatus;
	private Date expiryDate;
	private boolean bExpiryDateCheck = false;
	private boolean bCreditLimitCheck;
	private String checkItem;
	private String replyItem;
	private String rejectItem;
	private Long creditLimit;
	private String concurrentLoginPolicy;
	private String accessPolicy;
	private String authorizationPolicy;
	private String ipPoolName;
	private String param1;
	private String param2;
	private String param3;
	private String param4;
	private String param5;
	private String groupName;
	private String emailId;
	private String customerServices;
	private String customerType;
	private String passwordCheck = "YES";
	private String serviceType;
	private String callingStationId;
	private String calledStationId;
	private long maxSessionTime;
	private String cui;
	private String hotlinePolicy;
	private String userIdentity;
	private String gracePolicy;
	private String callbackId;
	private String additionalPolicy;

	private boolean bGracePeriodApplicable;

	private boolean macValidation;

	private String dynamicCheckItems;

	private String imsi;
	private String meid;
	private String msisdn;
	private String mdn;
	private String imei;

	private String deviceVendor;
	private String deviceName;
	private String devicePort;
	private String geoLocation;
	private String deviceVLAN;
	private String authorizationPolicyGroup;

	private String framedIPv4Address;
	private String framedIPv6Prefix;
	private String framedPool;

	public String getSubscriberProfileId() {
		return subscriberProfileId;
	}

	public void setSubscriberProfileId(String subscriberProfileId) {
		this.subscriberProfileId = subscriberProfileId;
	}

	@XmlElement(name = "user-name")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@XmlElement(name = "password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@XmlElement(name = "encryption-type")
	public Long getEncryptionType() {
		return encryptionType;
	}

	public void setEncryptionType(Long encryptionType) {
		this.encryptionType = encryptionType;
	}

	@XmlElement(name = "customer-status")
	public String getCustomerStatus() {
		return customerStatus;
	}
	
	public void setCustomerStatus(String customerStatus) {
		this.customerStatus = customerStatus;
	}
	
	@XmlElement(name = "expiry-date")
	@XmlJavaTypeAdapter(value = StringToDateAdapter.class)
	public Date getExpiryDate() {
		return expiryDate;
	}
	
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	@XmlElement(name = "expiry-date-check")
	public boolean isbExpiryDateCheck() {
		return bExpiryDateCheck;
	}

	public void setbExpiryDateCheck(boolean bExpiryDateCheck) {
		this.bExpiryDateCheck = bExpiryDateCheck;
	}

	@XmlElement(name = "credit-limit-check")
	public boolean isbCreditLimitCheck() {
		return bCreditLimitCheck;
	}

	public void setbCreditLimitCheck(boolean bCreditLimitCheck) {
		this.bCreditLimitCheck = bCreditLimitCheck;
	}

	@XmlElement(name = "check-item")
	public String getCheckItem() {
		return checkItem;
	}

	public void setCheckItem(String checkItem) {
		this.checkItem = checkItem;
	}

	@XmlElement(name = "reply-item")
	public String getReplyItem() {
		return replyItem;
	}

	public void setReplyItem(String replyItem) {
		this.replyItem = replyItem;
	}

	@XmlElement(name = "reject-item")
	public String getRejectItem() {
		return rejectItem;
	}

	public void setRejectItem(String rejectItem) {
		this.rejectItem = rejectItem;
	}

	@XmlElement(name = "credit-limit")
	public Long getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(Long creditLimit) {
		this.creditLimit = creditLimit;
	}

	@XmlElement(name = "concurrent-login-policy")
	public String getConcurrentLoginPolicy() {
		return concurrentLoginPolicy;
	}

	public void setConcurrentLoginPolicy(String concurrentLoginPolicy) {
		this.concurrentLoginPolicy = concurrentLoginPolicy;
	}

	@XmlElement(name = "access-policy")
	public String getAccessPolicy() {
		return accessPolicy;
	}

	public void setAccessPolicy(String accessPolicy) {
		this.accessPolicy = accessPolicy;
	}

	@XmlElement(name = "authorization-policy")
	public String getAuthorizationPolicy() {
		return authorizationPolicy;
	}

	public void setAuthorizationPolicy(String authorizationPolicy) {
		this.authorizationPolicy = authorizationPolicy;
	}

	@XmlElement(name = "ip-pool-name")
	public String getIpPoolName() {
		return ipPoolName;
	}

	public void setIpPoolName(String ipPoolName) {
		this.ipPoolName = ipPoolName;
	}

	@XmlElement(name = "param1")
	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	@XmlElement(name = "param2")
	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	@XmlElement(name = "param3")
	public String getParam3() {
		return param3;
	}

	public void setParam3(String param3) {
		this.param3 = param3;
	}

	@XmlElement(name = "param4")
	public String getParam4() {
		return param4;
	}

	public void setParam4(String param4) {
		this.param4 = param4;
	}

	@XmlElement(name = "param5")
	public String getParam5() {
		return param5;
	}

	public void setParam5(String param5) {
		this.param5 = param5;
	}

	@XmlElement(name = "group-name")
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@XmlElement(name = "email-id")
	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	@XmlElement(name = "customer-services")
	public String getCustomerServices() {
		return customerServices;
	}

	public void setCustomerServices(String customerServices) {
		this.customerServices = customerServices;
	}

	@XmlElement(name = "customer-type")
	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	@XmlElement(name = "password-check")
	public String getPasswordCheck() {
		return passwordCheck;
	}

	public void setPasswordCheck(String passwordCheck) {
		this.passwordCheck = passwordCheck;
	}

	@XmlElement(name = "service-type")
	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	@XmlElement(name = "calling-station-id")
	public String getCallingStationId() {
		return callingStationId;
	}

	public void setCallingStationId(String callingStationId) {
		this.callingStationId = callingStationId;
	}

	@XmlElement(name = "called-station-id")
	public String getCalledStationId() {
		return calledStationId;
	}

	public void setCalledStationId(String calledStationId) {
		this.calledStationId = calledStationId;
	}

	@XmlElement(name = "max-session-time")
	public long getMaxSessionTime() {
		return maxSessionTime;
	}

	public void setMaxSessionTime(long maxSessionTime) {
		this.maxSessionTime = maxSessionTime;
	}

	@XmlElement(name = "cui")
	public String getCui() {
		return cui;
	}

	public void setCui(String cui) {
		this.cui = cui;
	}

	@XmlElement(name = "hotline-policy")
	public String getHotlinePolicy() {
		return hotlinePolicy;
	}

	public void setHotlinePolicy(String hotlinePolicy) {
		this.hotlinePolicy = hotlinePolicy;
	}

	@XmlElement(name = "user-identity")
	public String getUserIdentity() {
		return userIdentity;
	}

	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}

	@XmlElement(name = "grace-policy")
	public String getGracePolicy() {
		return gracePolicy;
	}

	public void setGracePolicy(String gracePolicy) {
		this.gracePolicy = gracePolicy;
	}

	@XmlElement(name = "callback-id")
	public String getCallbackId() {
		return callbackId;
	}

	public void setCallbackId(String callbackId) {
		this.callbackId = callbackId;
	}

	@XmlElement(name = "additional-policy")
	public String getAdditionalPolicy() {
		return additionalPolicy;
	}

	public void setAdditionalPolicy(String additionalPolicy) {
		this.additionalPolicy = additionalPolicy;
	}

	@XmlElement(name = "grace-period-applicable")
	public boolean isbGracePeriodApplicable() {
		return bGracePeriodApplicable;
	}

	public void setbGracePeriodApplicable(boolean bGracePeriodApplicable) {
		this.bGracePeriodApplicable = bGracePeriodApplicable;
	}

	@XmlElement(name = "mac-validation")
	public boolean isMacValidation() {
		return macValidation;
	}

	public void setMacValidation(boolean macValidation) {
		this.macValidation = macValidation;
	}

	@XmlElement(name = "dynamic-check-items")
	public String getDynamicCheckItems() {
		return dynamicCheckItems;
	}

	public void setDynamicCheckItems(String dynamicCheckItems) {
		this.dynamicCheckItems = dynamicCheckItems;
	}

	@XmlElement(name = "imsi")
	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	@XmlElement(name = "meid")
	public String getMeid() {
		return meid;
	}

	public void setMeid(String meid) {
		this.meid = meid;
	}

	@XmlElement(name = "msisdn")
	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	@XmlElement(name = "mdn")
	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	@XmlElement(name = "imei")
	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	@XmlElement(name = "device-vendor")
	public String getDeviceVendor() {
		return deviceVendor;
	}

	public void setDeviceVendor(String deviceVendor) {
		this.deviceVendor = deviceVendor;
	}

	@XmlElement(name = "device-name")
	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	@XmlElement(name = "device-port")
	public String getDevicePort() {
		return devicePort;
	}

	public void setDevicePort(String devicePort) {
		this.devicePort = devicePort;
	}

	@XmlElement(name = "geo-location")
	public String getGeoLocation() {
		return geoLocation;
	}

	public void setGeoLocation(String geoLocation) {
		this.geoLocation = geoLocation;
	}

	@XmlElement(name = "device-vlan")
	public String getDeviceVLAN() {
		return deviceVLAN;
	}

	public void setDeviceVLAN(String deviceVLAN) {
		this.deviceVLAN = deviceVLAN;
	}

	@XmlElement(name = "authorization-policy-group")
	public String getAuthorizationPolicyGroup() {
		return authorizationPolicyGroup;
	}

	public void setAuthorizationPolicyGroup(String authorizationPolicyGroup) {
		this.authorizationPolicyGroup = authorizationPolicyGroup;
	}

	@XmlElement(name = "framed-ipv4-address")
	public String getFramedIPv4Address() {
		return framedIPv4Address;
	}

	public void setFramedIPv4Address(String framedIPv4Address) {
		this.framedIPv4Address = framedIPv4Address;
	}

	@XmlElement(name = "framed-ipv6-prefix")
	public String getFramedIPv6Prefix() {
		return framedIPv6Prefix;
	}

	public void setFramedIPv6Prefix(String framedIPv6Prefix) {
		this.framedIPv6Prefix = framedIPv6Prefix;
	}

	@XmlElement(name = "framed-pool")
	public String getFramedPool() {
		return framedPool;
	}

	public void setFramedPool(String framedPool) {
		this.framedPool = framedPool;
	}
}
