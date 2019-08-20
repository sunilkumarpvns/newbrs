package com.elitecore.elitesm.web.servermgr.server.forms;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ViewUserfileDatasourceUserAccountForm extends BaseWebForm{
	
	String action;
	String passwordCheckEnable=null;
	String password;
	String encryptionType;
	String accountStatus;
	int creditLimit;
	String expiryDate;
	String customerService;
	String customerType;
	String serviceType;
	String callingStationId;
	String calledStationId;
	String emailId;
	String userGroup;
	String userCheckItem;
	String userRejectItem;
	String userReplyItem;
	String accessPolicy;
	String radiusPolicy;
	String concurrentLoginPolicy;
	String ipPoolName;
	String cui;
	String hotlinePolicy;
	String param1;
	String param2;
	String param3;
	String param4;
	String param5;
	String gracePolicy;
	String id;
	String netServerId;
	String fileName;
	String attributeId;
	String attributeIdName;
	String callbackId;
	String profileUserName;
	String macValidation;
	String groupBandwidth;
	String imsi;
	String meid;
	String msIsdn;
	String mdn;
	String imei;
	String deviceVendor;
	String deviceName;
	String devicePort;
	String geoLocation;
	String deviceVLAN;
	private String framedIPV4Address;
	private String framedIPV6Prefix;
	private String framedPool;
	private String authorizationPolicyGroup;
	

	List listAttributeId=new ArrayList(); 
	List listAttrbuteValue=new ArrayList();
	List listAttributeName=new ArrayList(); 
	
	
	
	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getMeid() {
		return meid;
	}

	public void setMeid(String meid) {
		this.meid = meid;
	}

	public String getMsIsdn() {
		return msIsdn;
	}

	public void setMsIsdn(String msIsdn) {
		this.msIsdn = msIsdn;
	}

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getPasswordCheckEnable() {
		return passwordCheckEnable;
	}

	public void setPasswordCheckEnable(String passwordCheckEnable) {
		this.passwordCheckEnable = passwordCheckEnable;
	}

	public String getAccessPolicy() {
		return accessPolicy;
	}

	public void setAccessPolicy(String accessPolicy) {
		this.accessPolicy = accessPolicy;
	}

	public String getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}

	public String getCalledStationId() {
		return calledStationId;
	}

	public void setCalledStationId(String calledStationId) {
		this.calledStationId = calledStationId;
	}

	public String getCallingStationId() {
		return callingStationId;
	}

	public void setCallingStationId(String callingStationId) {
		this.callingStationId = callingStationId;
	}

	public String getConcurrentLoginPolicy() {
		return concurrentLoginPolicy;
	}

	public void setConcurrentLoginPolicy(String concurrentLoginPolicy) {
		this.concurrentLoginPolicy = concurrentLoginPolicy;
	}

	public int getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(int creditLimit) {
		this.creditLimit = creditLimit;
	}

	public String getCustomerService() {
		return customerService;
	}

	public void setCustomerService(String customerService) {
		this.customerService = customerService;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getEncryptionType() {
		return encryptionType;
	}

	public void setEncryptionType(String encryptionType) {
		this.encryptionType = encryptionType;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getIpPoolName() {
		return ipPoolName;
	}

	public void setIpPoolName(String ipPoolName) {
		this.ipPoolName = ipPoolName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRadiusPolicy() {
		return radiusPolicy;
	}

	public void setRadiusPolicy(String radiusPolicy) {
		this.radiusPolicy = radiusPolicy;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getUserCheckItem() {
		return userCheckItem;
	}

	public void setUserCheckItem(String userCheckItem) {
		this.userCheckItem = userCheckItem;
	}

	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	public String getUserRejectItem() {
		return userRejectItem;
	}

	public void setUserRejectItem(String userRejectItem) {
		this.userRejectItem = userRejectItem;
	}

	public String getUserReplyItem() {
		return userReplyItem;
	}

	public void setUserReplyItem(String userReplyItem) {
		this.userReplyItem = userReplyItem;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNetServerId() {
		return netServerId;
	}

	public void setNetServerId(String netServerId) {
		this.netServerId = netServerId;
	}

	public String getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}

	public List getListAttrbuteValue() {
		return listAttrbuteValue;
	}

	public void setListAttrbuteValue(List listAttrbuteValue) {
		this.listAttrbuteValue = listAttrbuteValue;
	}

	public List getListAttributeId() {
		return listAttributeId;
	}

	public void setListAttributeId(List listAttributeId) {
		this.listAttributeId = listAttributeId;
	}
	
	public String getFieldId(int index) {
		return (String)listAttributeId.get(index);
	}
	public void setFieldId(int index,String fieldId) {
		
		while(listAttributeId.size() - 1 < index)
			listAttributeId.add(new String());
		
		listAttributeId.set(index, fieldId);
	}
	public String getFieldValue(int index) {
		return (String)listAttrbuteValue.get(index);
	}

	public void setFieldValue(int index,String fieldId) {
		while(listAttrbuteValue.size() - 1 < index)
			listAttrbuteValue.add(new String());
		
		listAttrbuteValue.set(index, fieldId);
	}
	public String getAttributeName(int index) {
		return (String)listAttributeName.get(index);
	}

	public void setAttributeName(int index,String fieldId) {
		while(listAttributeName.size() - 1 < index)
			listAttributeName.add(new String());
		
		listAttributeName.set(index, fieldId);
	}

	public List getListAttributeName() {
		return listAttributeName;
	}

	public String getAttributeIdName() {
		return attributeIdName;
	}

	public void setAttributeIdName(String attributeIdName) {
		this.attributeIdName = attributeIdName;
	}

	public String getCui() {
		return cui;
	}

	public void setCui(String cui) {
		this.cui = cui;
	}

	public String getHotlinePolicy() {
		return hotlinePolicy;
	}

	public void setHotlinePolicy(String hotlinePolicy) {
		this.hotlinePolicy = hotlinePolicy;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	public String getParam3() {
		return param3;
	}

	public void setParam3(String param3) {
		this.param3 = param3;
	}

	public String getParam4() {
		return param4;
	}

	public void setParam4(String param4) {
		this.param4 = param4;
	}

	public String getParam5() {
		return param5;
	}

	public void setParam5(String param5) {
		this.param5 = param5;
	}

	public String getGracePolicy() {
		return gracePolicy;
	}

	public void setGracePolicy(String gracePolicy) {
		this.gracePolicy = gracePolicy;
	}

	public String getCallbackId() {
		return callbackId;
	}

	public void setCallbackId(String callbackId) {
		this.callbackId = callbackId;
	}

	public String getProfileUserName() {
		return profileUserName;
	}

	public void setProfileUserName(String profileUserName) {
		this.profileUserName = profileUserName;
	}
	
	public String getMacValidation() {
		return macValidation;
	}

	public void setMacValidation(String macValidation) {
		this.macValidation = macValidation;
	}

	public String getGroupBandwidth() {
		return groupBandwidth;
	}

	public void setGroupBandwidth(String groupBandwidth) {
		this.groupBandwidth = groupBandwidth;
	}

	public String getDeviceVendor() {
		return deviceVendor;
	}

	public void setDeviceVendor(String deviceVendor) {
		this.deviceVendor = deviceVendor;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDevicePort() {
		return devicePort;
	}

	public void setDevicePort(String devicePort) {
		this.devicePort = devicePort;
	}

	public String getGeoLocation() {
		return geoLocation;
	}

	public void setGeoLocation(String geoLocation) {
		this.geoLocation = geoLocation;
	}

	public String getDeviceVLAN() {
		return deviceVLAN;
	}

	public void setDeviceVLAN(String deviceVLAN) {
		this.deviceVLAN = deviceVLAN;
	}

	public String getFramedIPV4Address() {
		return framedIPV4Address;
	}

	public void setFramedIPV4Address(String framedIPV4Address) {
		this.framedIPV4Address = framedIPV4Address;
	}

	
	public String getFramedIPV6Prefix() {
		return framedIPV6Prefix;
	}
	
	public void setFramedIPV6Prefix(String framedIPV6Prefix) {
		this.framedIPV6Prefix = framedIPV6Prefix;
	}

	public String getFramedPool() {
		return framedPool;
	}

	public void setFramedPool(String framedPool) {
		this.framedPool = framedPool;
	}

	public String getAuthorizationPolicyGroup() {
		return authorizationPolicyGroup;
	}

	public void setAuthorizationPolicyGroup(String authorizationPolicyGroup) {
		this.authorizationPolicyGroup = authorizationPolicyGroup;
	}
}
