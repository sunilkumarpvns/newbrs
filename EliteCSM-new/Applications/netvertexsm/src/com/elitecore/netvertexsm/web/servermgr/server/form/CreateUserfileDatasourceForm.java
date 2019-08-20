package com.elitecore.netvertexsm.web.servermgr.server.form;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class CreateUserfileDatasourceForm extends BaseWebForm {

	String action;
	String passwordCheckEnable=null;
	String password;
	String encryptionType;
	String accountStatus;
	int creditLimit;
	String expiryDate="00/00/0000";
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
	String gracePeriod;
	String callbackId;
	String profileUserName;
	String macValidation;
	String groupBandwidth;
	

	String id;
	long netServerId;
	String fileName;
	String attributeId;
	String attributeName;
	String attributeIdName;
	List listAttributeId=new ArrayList(); 
	List listAttrbuteValue=new ArrayList();
	List listAttributeName=new ArrayList(); 

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

	public long getNetServerId() {
		return netServerId;
	}

	public void setNetServerId(long netServerId) {
		this.netServerId = netServerId;
	}

	public String getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}

	
	public String getFieldId(int index) {
		return (String)listAttributeId.get(index);
	}
	
	public void setFieldId(int index,String fieldId) {
		
		while(listAttributeId.size() - 1 < index)
			listAttributeId.add(new String());
		
		listAttributeId.set(index, fieldId);
	}

	public List getListAttributeId() {
		return listAttributeId;
	}

	public void setListAttributeId(List listAttributeId) {
		this.listAttributeId = listAttributeId;
	}

	public String getFieldValue(int index) {
		return (String)listAttrbuteValue.get(index);
	}

	public void setFieldValue(int index,String fieldId) {
		while(listAttrbuteValue.size() - 1 < index)
			listAttrbuteValue.add(new String());
		
		listAttrbuteValue.set(index, fieldId);
	}

	public List getListAttrbuteValue() {
		return listAttrbuteValue;
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

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
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

	public String getGracePeriod() {
		return gracePeriod;
	}

	public void setGracePeriod(String gracePeriod) {
		this.gracePeriod = gracePeriod;
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
}
