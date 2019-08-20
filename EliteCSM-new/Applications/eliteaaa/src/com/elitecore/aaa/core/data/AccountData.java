package com.elitecore.aaa.core.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import com.elitecore.commons.base.Arrayz;
import com.elitecore.commons.base.Strings;
import com.elitecore.coreeap.data.ICustomerAccountInfo;
import com.elitecore.coreeap.data.sim.PDPContext;

public class AccountData implements Serializable,ICustomerAccountInfo,Cloneable{
	
	private static final String COMMA=",";
	private static final String AND="&&";
	private static final String OR = "||";
	private static final long serialVersionUID = 1L;
	private String userName;
	private String password;
	private String encryptionType;
	private String accountStatus;
	private Date expiryDate;
	private boolean bExpiryDateCheck = false;
	private boolean bCreditLimitCheck;
	private String checkItem = "";
	private String replyItem = "";
	private String rejectItem = "";
	private long creditLimit;
	private String concurrentLoginPolicy;	
	private String accessPolicy;
	private String authorizationPolicy = "";
	private String ipPoolName;
	private String param1;
	private String param2;
	private String param3;
	private String param4;
	private String param5;
	private String groupName;
	private String emailId = "";
	private String customerServices;
	private String customerType;
	private String passwordCheck = "YES"; //NOSONAR - Reason: Credentials should not be hard-coded
	private String serviceType = "";
	private String callingStationId = "";
	private String calledStationId = "";
	private long maxSessionTime;
	private String cui;
	private String hotlinePolicy;
	private String strUserIdentity;
	private String gracePolicy;
	private String callbackId;
	private String additionalPolicy = "";
	
		
	private boolean bGracePeriodApplicable;
	
	private boolean macValidation;
	
	private String dynamicCheckItems;
	
	private String imsi = "";
	private String meid = "";
	private String msisdn = "";
	private String mdn = "";
	private String imei = "";
	
	private String deviceVendor;
	private String deviceName;
	private String devicePort ;
	private String geoLocation;
	private String deviceVLAN;
	private String authorizationPolicyGroup;
	
	
	private String framedIPv4Address;
	private String framedIPv6Prefix = "";
	private String framedPool;
	
	private PDPContext[] pdpContexts;
	
	public PDPContext[] getPdpContexts() {
		return pdpContexts;
	}
	
	public void setPdpContexts(PDPContext[] pdpContexts) {
		this.pdpContexts = pdpContexts;
	}

	public String getDeviceVendor() {
		return deviceVendor;
	}

	public void setDeviceVendor(String ...deviceVendor) {
		if(deviceVendor!=null && deviceVendor.length>0)
			this.deviceVendor = deviceVendor[0];
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String ...deviceName) {
		if(deviceName!=null && deviceName.length>0)
			this.deviceName = deviceName[0];
	}

	public String getDevicePort() {
		return devicePort;
	}

	public void setDevicePort(String ...devicePort) {
		if(devicePort!=null && devicePort.length>0)
			this.devicePort = devicePort[0];
	}

	public String getGeoLocation() {
		return geoLocation;
	}

	public void setGeoLocation(String ...geoLocation) {
		if(geoLocation!=null && geoLocation.length>0)
			this.geoLocation = geoLocation[0];
	}

	public String getDeviceVLAN() {
		return deviceVLAN;
	}

	public void setDeviceVLAN(String ...deviceVLAN) {
		if(deviceVLAN!=null && deviceVLAN.length>0)
			this.deviceVLAN = deviceVLAN[0];
	}
	
	public String getImei() {
		return imei;
	}

	public void setImei(String ...imei) {
		this.imei = getCommaSeparatedValue(imei);
	}

	public String getUserName() {
		return userName;
	}
	
	public String getImsi() {
		return imsi;
	}

	public void setImsi(String ...imsi) {
		this.imsi = getCommaSeparatedValue(imsi);
	}

	public String getMeid() {
		return meid;
	}

	public void setMeid(String ...meid) {
		this.meid = getCommaSeparatedValue(meid);
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String ...msisdn) {
		this.msisdn = getCommaSeparatedValue(msisdn);
	}

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String ...mdn) {
		this.mdn = getCommaSeparatedValue(mdn);
	}

	public void setUserName(String ...strUserName) {
		if (strUserName != null)
			this.userName = strUserName[0];
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String ...strUserPassword) {
		if (strUserPassword != null)
			this.password = strUserPassword[0];
	}
	public String getEncryptionType() {
		return encryptionType;
	}
	public void setEncryptionType(String ...strEncryptionType) {
		if (strEncryptionType != null)
			this.encryptionType = strEncryptionType[0];
	}
	public String getAccountStatus() {
		return accountStatus;
	}
	public void setAccountStatus(String ...strStatus) {
		if (strStatus != null)
			this.accountStatus = strStatus[0];
	}
	public Date getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Date ...dtExpiryDate) {
		if (dtExpiryDate != null)
			this.expiryDate = dtExpiryDate[0];
	}
	public void setExpiryDateCheckRequired(boolean bExpiryDateCheck) {
		this.bExpiryDateCheck = bExpiryDateCheck;
	}
	
	public void setCreditLimitCheckRequired(boolean bCreditLimitCheck) {
		this.bCreditLimitCheck = bCreditLimitCheck;
	}

	public boolean isExpiryDateCheckRequired() {
		return bExpiryDateCheck;
	}
	public boolean isCreditLimitCheckRequired() {
		return bCreditLimitCheck;
	}

	public String getCheckItem() {
		return checkItem;
	}
	public void setCheckItem(String ...strCheckItem) {
		this.checkItem = getCommaSeparatedValue(strCheckItem);
	}
	public String getReplyItem() {
		return replyItem;
	}
	public void setReplyItem(String ...strReplyItem) {
		this.replyItem = getCommaSeparatedValue(strReplyItem);
	}
	public String getRejectItem() {
		return rejectItem;
	}
	public void setRejectItem(String ...strRejectItem) {
		this.rejectItem = getCommaSeparatedValue(strRejectItem);
	}
	public long getCreditLimit() {
		return creditLimit;
	}
	public void setCreditLimit(long strCreditLimit) {
		this.creditLimit = strCreditLimit;
	}
	public String getConcurrentLoginPolicy() {
		return concurrentLoginPolicy;
	}
	public void setConcurrentLoginPolicy(String ...strLoginPolicy) {
		if (strLoginPolicy != null)
			this.concurrentLoginPolicy = strLoginPolicy[0];
	}
	public String getAccessPolicy() {
		return accessPolicy;
	}
	public void setAccessPolicy(String ...strAccessPolicy) {
		if (strAccessPolicy != null)
			this.accessPolicy = strAccessPolicy[0];
	}
	public String getAuthorizationPolicy() {
		return authorizationPolicy;
	}
	public void setAuthorizationPolicy(String ...strAuthorizationPolicy) {
		this.authorizationPolicy = getANDSeparatedValue(strAuthorizationPolicy);
	}

	public String getIPPoolName() {
		return ipPoolName;
	}
	public void setIPPoolName(String ...strIPPoolName) {
		if (strIPPoolName != null)
			this.ipPoolName = strIPPoolName[0];
	}
	public String getParam1() {
		return param1;
	}
	public void setParam1(String ...strParam1) {
		if (strParam1 != null)
			this.param1 = strParam1[0];	
	}
	public String getParam2() {
		return param2;
	}
	public void setParam2(String ...strParam2) {
		if (strParam2 != null)
			this.param2 = strParam2[0];
	}
	public String getParam3() {
		return param3;
	}
	public void setParam3(String ...strParam3) {
		if (strParam3 != null)
			this.param3 = strParam3[0];
	}
	
	public String getParam4() {
		return param4;
	}
	public void setParam4(String ...strParam4) {
		if (strParam4 != null && strParam4.length>0)
			this.param4 = strParam4[0];	
	}
	public String getParam5() {
		return param5;
	}
	public void setParam5(String ...strParam5) {
		if (strParam5 != null && strParam5.length>0)
			this.param5 = strParam5[0];	
	}
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String ...strGroupName) {
		if (strGroupName != null)
			this.groupName = strGroupName[0];
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String ...strEmailId) {
		this.emailId = getCommaSeparatedValue(strEmailId);
	}
	public String getCustomerServices() {
		return customerServices;
	}
	public void setCustomerServices(String ...strCustomerServices) {
		if (strCustomerServices != null)
			this.customerServices = strCustomerServices[0];
	}
	public String getCustomerType() {
		return customerType;
	}
	public void setCustomerType(String ...strCustomerType) {
		if (strCustomerType != null)
			this.customerType = strCustomerType[0];
	}
	public String getPasswordCheck() {
		return passwordCheck;
	}
	public void setPasswordCheck(String ...strPasswordCheck) {
		if (strPasswordCheck != null)
			this.passwordCheck = strPasswordCheck[0];	
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String ...strServiceType) {
		this.serviceType = getCommaSeparatedValue(strServiceType);
	}
	public String getCallingStationId() {
		return callingStationId;
	}
	public void setCallingStationId(String ...strCallingStationId) {
		this.callingStationId = getCommaSeparatedValue(strCallingStationId);
	}
	public String getCalledStationId() {
		return calledStationId;
	}
	public void setCalledStationId(String ...strCalledStationId) {
		this.calledStationId = getCommaSeparatedValue(strCalledStationId);
	}
	public String getCUI() {
		return cui;
	}
	public void setCUI(String ...cui) {
		if (cui != null)
			this.cui = cui[0];
	}
	public String getHotlinePolicy(){
		return hotlinePolicy;
	}
	public void setHotlinePolicy(String ...hotlinePolicy){
		if (hotlinePolicy != null)
			this.hotlinePolicy = hotlinePolicy[0];
	}
	public long getMaxSessionTime() {
		return maxSessionTime;
	}
	public void setMaxSessionTime(long maxSessionTime) {
		this.maxSessionTime = maxSessionTime;
	}
	public String getGracePolicy() {
		return gracePolicy;
	}
	public void setGracePolicy(String ...gracePeriod) {
		if (gracePeriod != null)
			this.gracePolicy = gracePeriod[0];
	}
    public String toString(){
		
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		
		out.println();
		out.println("        User-Identity         : " + ((strUserIdentity != null)?strUserIdentity:""));
		out.println("        Username              : " + ((userName != null)?userName:""));
		out.println("        Group Name            : " + ((groupName!=null)?groupName:""));
		out.println("        CUI                   : " + ((cui!=null)?cui:""));
		out.println("        Password Check        : " + ((passwordCheck!=null)?passwordCheck:""));
		out.println("        Encryption Type       : " + ((encryptionType!=null)?encryptionType:""));
		out.println("        Customer Type         : " + ((customerType!=null)?customerType:""));
		out.println("        Service Type          : " + ((serviceType!=null)?serviceType:""));
		out.println("        Calling Station Id    : " + ((callingStationId!=null)?callingStationId:""));
		out.println("        IMSI                  : " + ((imsi!=null)?imsi:""));
		out.println("        MEID                  : " + ((meid!=null)?meid:""));
		out.println("        MSISDN                : " + ((msisdn!=null)?msisdn:""));
		out.println("        MDN                   : " + ((mdn!=null)?mdn:""));
		out.println("        IMEI                  : " + ((imei!=null)?imei:""));
		out.println("        Device Vendor         : " + ((deviceVendor!=null)?deviceVendor:""));
		out.println("        Device Name           : " + ((deviceName!=null)?deviceName:""));
		out.println("        Device Port           : " + ((devicePort!=null)?devicePort:""));
		out.println("        GEO Location          : " + ((geoLocation!=null)?geoLocation:""));
		out.println("        Device VLAN           : " + ((deviceVLAN!=null)?deviceVLAN:""));
		out.println("        Called Station Id     : " + ((calledStationId!=null)?calledStationId:""));
		out.println("        Max Session Time      : " + maxSessionTime);
		out.println("        Check Items           : " + ((checkItem!=null)?checkItem:""));
		out.println("        Dynamic Check Items   : " + ((dynamicCheckItems!=null)?dynamicCheckItems:""));
		out.println("        Reject Items          : " + ((rejectItem!=null)?rejectItem:""));
		out.println("        Reply Items           : " + ((replyItem!=null)?replyItem:""));
		out.println("        Conc Login Policy     : " + ((concurrentLoginPolicy!=null)?concurrentLoginPolicy:""));
		out.println("        Access Policy         : " + ((accessPolicy!=null)?accessPolicy:""));
		out.println("        Authorization Policy  : " + ((authorizationPolicy!=null)?authorizationPolicy:""));
		out.println("        Additional Policy     : " + ((additionalPolicy!=null)?additionalPolicy:""));
		out.println("        IP Pool Name          : " + ((ipPoolName!=null)?ipPoolName:""));
		out.println("        Credit Limit          : " +  creditLimit);
		out.println("        Account Expiry Date   : " +  ((expiryDate!=null)?expiryDate:""));
		out.println("        Account Status        : " + ((accountStatus!=null)?accountStatus:""));
		out.println("        Param1                : " + ((param1!=null)?param1:""));
		out.println("        Param2                : " + ((param2!=null)?param2:""));
		out.println("        Param3                : " + ((param3!=null)?param3:""));
		out.println("        Param4                : " + ((param4!=null)?param4:""));
		out.println("        Param5                : " + ((param5!=null)?param5:""));
		out.println("        Hotline Policy        : " + ((hotlinePolicy!=null)?hotlinePolicy:""));
		out.println("        Grace Policy          : " + ((gracePolicy!=null)?gracePolicy:""));
		out.println("        Callback Id           : " + ((callbackId!=null)?callbackId:""));
		out.println("        Framed IPv4 Address   : " + ((framedIPv4Address != null) ? framedIPv4Address : ""));
		out.println("        Framed IPv6 Prefix(s) : " + ((framedIPv6Prefix != null) ? framedIPv6Prefix : ""));
		out.println("        Framed Pool           : " + ((framedPool != null) ? framedPool : ""));
		out.println("        MAC Validation        : " + macValidation);
		out.println("        Policy Group          : " + ((authorizationPolicyGroup != null) ? authorizationPolicyGroup : ""));
		out.close();
		return stringBuffer.toString();
	}	
    
	public String getUserIdentity() {
		return strUserIdentity;
	}
	public void setUserIdentity(String strUserIdentity) {
		this.strUserIdentity = strUserIdentity;
	}
	public boolean isGracePeriodApplicable() {
		return bGracePeriodApplicable;
	}
	public void setGracePeriodApplicable(boolean gracePeriodApplicable) {
		bGracePeriodApplicable = gracePeriodApplicable;
	}
	public String getCallbackId() {
		return callbackId;
	}
	public void setCallbackId(String ...callbackId) {
		if (callbackId != null && callbackId.length > 0)
			this.callbackId = callbackId[0];
	}
	public void setMacValidation(boolean macValidation) {
		this.macValidation = macValidation;
	}
	@Override
	public boolean isMacValidation() {
		return  this.macValidation;
	}

	public void setDynamicCheckItems(String ...dynamicCheckItem) {
		if(dynamicCheckItem!=null && dynamicCheckItem.length>0){
			String tempString;
			for(int i=dynamicCheckItem.length-1 ; i>=0 ; i--){
				tempString = dynamicCheckItem[i];
				if(tempString!=null){
					this.dynamicCheckItems  = tempString;
					break;
				}	
			}
		}
	}

	public String getDynamicCheckItems() {
		return dynamicCheckItems;
	}
	
	public AccountData clone() throws CloneNotSupportedException{
			return (AccountData)super.clone();
	}

	private String getCommaSeparatedValue(String[] strArray) {
		if(strArray == null || !(strArray.length>0)){
			return "";
		}
		String tempString;
		int j=0;
		StringBuilder stringBuilder = new StringBuilder();
		for(int i=0 ; i<strArray.length ; i++){
			tempString = strArray[i];
			if(tempString!=null && tempString.length()>0){
				j = j+1;
				if(j!=1){
					stringBuilder.append(COMMA);
				}
				stringBuilder.append(tempString);
			}
		}
		return stringBuilder.toString();
	}
	private String getANDSeparatedValue(String[] strArray) {
		if(strArray == null || !(strArray.length>0)){
			return "";
		}
		String tempString;
		int j=0;
		StringBuilder stringBuilder = new StringBuilder();
		for(int i=0 ; i<strArray.length ; i++){
			tempString = strArray[i];
			if(tempString!=null && tempString.length()>0){
				tempString = "("+tempString+")";
				j = j+1;
				if(j!=1){
					stringBuilder.append(AND);
				}
				stringBuilder.append(tempString);
			}	
		}
		return stringBuilder.toString();
	}
	
	private String getORSeparatedValue(String[] strArray) {
		if(strArray==null || !(strArray.length>0)){
			return "";
		}
		String tempString;
		int j=0;
		StringBuilder stringBuilder = new StringBuilder();
		for(int i=0 ; i<strArray.length ; i++){
			tempString = strArray[i];
			if(tempString!=null && tempString.length() > 0){
				tempString = "("+tempString+")";
				j = j+1;
				if(j!=1){
					stringBuilder.append(OR);
				}
				stringBuilder.append(tempString);
			}	
		}
		return stringBuilder.toString();
	}

	public void setAdditionalPolicy(String ...additionalPolicy) {
		this.additionalPolicy = getORSeparatedValue(additionalPolicy);
	}

	public String getAdditionalPolicy() {
		return additionalPolicy;
	}

	public void append(AccountData otherAccountData) {
		if (otherAccountData == null){
			return;
		}
		
		if (this.strUserIdentity == null) {
			this.strUserIdentity = otherAccountData.getUserIdentity();
		}
		
		if (this.userName == null) {
			this.userName = otherAccountData.getUserName();
		}
		
		if (otherAccountData.getAccountStatus() != null && otherAccountData.getAccountStatus().trim().length() > 0){
			accountStatus = otherAccountData.getAccountStatus();
		}
		
		if (otherAccountData.isExpiryDateCheckRequired()){
			if ( otherAccountData.getExpiryDate() != null && expiryDate != null){
				if (expiryDate.after(otherAccountData.getExpiryDate())){
					expiryDate = otherAccountData.getExpiryDate();
				}
			} else {
				expiryDate = otherAccountData.getExpiryDate();
				bExpiryDateCheck = true;
			}
		}
		
		if (otherAccountData.getCheckItem() != null && otherAccountData.getCheckItem().trim().length() > 0){
			if (checkItem != null && checkItem.trim().length() > 0){
				checkItem += COMMA + otherAccountData.getCheckItem();
			} else {
				checkItem = otherAccountData.getCheckItem();
			}
		}
		
		if (otherAccountData.getRejectItem() != null && otherAccountData.getRejectItem().trim().length() > 0){
			if (rejectItem != null && rejectItem.trim().length() > 0){
				rejectItem += COMMA + otherAccountData.getRejectItem();
			} else {
				rejectItem = otherAccountData.getRejectItem();
			}
		}
		
		if (otherAccountData.getReplyItem() != null && otherAccountData.getReplyItem().trim().length() > 0){
			if (replyItem != null && replyItem.trim().length() > 0){
				replyItem += COMMA + otherAccountData.getReplyItem();
			} else {
				replyItem = otherAccountData.getReplyItem();
			}
		}
		
		if ( otherAccountData.isCreditLimitCheckRequired()){
			if (bCreditLimitCheck){
				if (otherAccountData.getCreditLimit() < creditLimit){
					creditLimit = otherAccountData.getCreditLimit();
				}
			} else {
				creditLimit = otherAccountData.getCreditLimit();
				bCreditLimitCheck = true;
			}
		}

		if (otherAccountData.getConcurrentLoginPolicy() != null && otherAccountData.getConcurrentLoginPolicy().trim().length() > 0){
			concurrentLoginPolicy = otherAccountData.getConcurrentLoginPolicy();
		}
		
		if (otherAccountData.getAccessPolicy() != null && otherAccountData.getAccessPolicy().trim().length() > 0){
			accessPolicy = otherAccountData.getAccessPolicy();
		}
		
		if (otherAccountData.getAuthorizationPolicy() != null && otherAccountData.getAuthorizationPolicy().trim().length() > 0){
			if (authorizationPolicy != null && authorizationPolicy.trim().length() > 0){
				authorizationPolicy += AND + otherAccountData.getAuthorizationPolicy();
			} else {
				authorizationPolicy = otherAccountData.getAuthorizationPolicy();
			}
		}
		
		if (otherAccountData.getIPPoolName() != null && otherAccountData.getIPPoolName().trim().length() > 0){
			ipPoolName = otherAccountData.getIPPoolName();
		}

		if (otherAccountData.getParam1() != null && otherAccountData.getParam1().trim().length() > 0){
			param1 = otherAccountData.getParam1();
		}
		
		if (otherAccountData.getParam2() != null && otherAccountData.getParam2().trim().length() > 0){
			param2 = otherAccountData.getParam2();
		}
		
		if (otherAccountData.getParam3() != null && otherAccountData.getParam3().trim().length() > 0){
			param3 = otherAccountData.getParam3();
		}
		
		if (otherAccountData.getParam4() != null && otherAccountData.getParam4().trim().length() > 0){
			param4 = otherAccountData.getParam4();
		}
		
		if (otherAccountData.getParam5() != null && otherAccountData.getParam5().trim().length() > 0){
			param5 = otherAccountData.getParam5();
		}
		
		if (otherAccountData.getDeviceVendor() != null && otherAccountData.getDeviceVendor().trim().length() > 0){
			deviceVendor = otherAccountData.getDeviceVendor();
		}
		if (otherAccountData.getDeviceName() != null && otherAccountData.getDeviceName().trim().length() > 0){
			deviceName = otherAccountData.getDeviceName();
		}
		if (otherAccountData.getDeviceVLAN() != null && otherAccountData.getDeviceVLAN().trim().length() > 0){
			deviceVLAN = otherAccountData.getDeviceVLAN();
		}
		if (otherAccountData.getGeoLocation() != null && otherAccountData.getGeoLocation().trim().length() > 0){
			geoLocation = otherAccountData.getGeoLocation();
		}
		if (otherAccountData.getDevicePort()!=null && otherAccountData.getDevicePort().trim().length()>0){
			devicePort = otherAccountData.getDevicePort();
		}
		
		if (otherAccountData.getGroupName() != null && otherAccountData.getGroupName().trim().length() > 0){
			groupName = otherAccountData.getGroupName();
		}
		
		if (otherAccountData.getEmailId() != null && otherAccountData.getEmailId().trim().length() > 0){
			if (emailId != null && emailId.trim().length() > 0){
				emailId += COMMA + otherAccountData.getEmailId();
			} else {
				emailId = otherAccountData.getEmailId();
			}
		}

		if (otherAccountData.getCustomerServices() != null && otherAccountData.getCustomerServices().trim().length() > 0){
			if (customerServices != null && customerServices.trim().length() > 0){
				customerServices += COMMA + otherAccountData.getCustomerServices();
			} else {
				customerServices = otherAccountData.getCustomerServices();
			}
		}
		
		if (otherAccountData.getCustomerType() != null && otherAccountData.getCustomerType().trim().length() > 0){
			customerType = otherAccountData.getCustomerType();
		}
		
		if (otherAccountData.getServiceType() != null && otherAccountData.getServiceType().trim().length() > 0){
			serviceType = otherAccountData.getServiceType();
		}

		if (otherAccountData.getCallingStationId() != null && otherAccountData.getCallingStationId().trim().length() > 0){
			if (callingStationId != null && callingStationId.trim().length() > 0){
				callingStationId += COMMA + otherAccountData.getCallingStationId();
			} else {
				callingStationId = otherAccountData.getCallingStationId();
			}
		}
		
		if (otherAccountData.getCalledStationId() != null && otherAccountData.getCalledStationId().trim().length() > 0){
			if (calledStationId != null && calledStationId.trim().length() > 0){
				calledStationId += COMMA + otherAccountData.getCalledStationId();
			} else {
				calledStationId = otherAccountData.getCalledStationId();
			}
		}
		
		if (otherAccountData.getMaxSessionTime() < maxSessionTime){
			maxSessionTime = otherAccountData.getMaxSessionTime();
		}
		
		if (otherAccountData.getCUI() != null && otherAccountData.getCUI().trim().length() > 0){
			cui = otherAccountData.getCUI();
		}
		
		if (otherAccountData.getHotlinePolicy() != null && otherAccountData.getHotlinePolicy().trim().length() > 0){
			hotlinePolicy = otherAccountData.getHotlinePolicy();
		}
		
		if (Strings.isNullOrBlank(this.gracePolicy) && Strings.isNullOrBlank(otherAccountData.getGracePolicy()) == false) {
			gracePolicy = otherAccountData.getGracePolicy();
		}
		
		if (otherAccountData.getCallbackId() != null && otherAccountData.getCallbackId().trim().length() > 0){
			callbackId = otherAccountData.getCallbackId();
		}
		
		if (otherAccountData.getAdditionalPolicy() != null && otherAccountData.getAdditionalPolicy().trim().length() > 0){
			if (additionalPolicy != null && additionalPolicy.trim().length() > 0){
				additionalPolicy += OR + otherAccountData.getAdditionalPolicy();
			} else {
				additionalPolicy = otherAccountData.getAdditionalPolicy();
			}
		}
		
		if (otherAccountData.getDynamicCheckItems() != null && otherAccountData.getDynamicCheckItems().trim().length() > 0){
			dynamicCheckItems = otherAccountData.getDynamicCheckItems();
		}
		
		if (otherAccountData.getImsi() != null && otherAccountData.getImsi().trim().length() > 0){
			if (imsi != null && imsi.trim().length() > 0){
				imsi += COMMA + otherAccountData.getImsi();
			} else {
				imsi = otherAccountData.getImsi();
			}
		}
		
		if (otherAccountData.getMeid() != null && otherAccountData.getMeid().trim().length() > 0){
			if (meid != null && meid.trim().length() > 0){
				meid += COMMA + otherAccountData.getMeid();
			} else {
				meid = otherAccountData.getMeid();
			}
		}
		
		if (otherAccountData.getMsisdn() != null && otherAccountData.getMsisdn().trim().length() > 0){
			if (msisdn != null && msisdn.trim().length() > 0){
				msisdn += COMMA + otherAccountData.getMsisdn();
			} else {
				msisdn = otherAccountData.getMsisdn();
			}
		}
			
		if (otherAccountData.getMdn() != null && otherAccountData.getMdn().trim().length() > 0){
			if (mdn != null && mdn.trim().length() > 0){
				mdn += COMMA + otherAccountData.getMdn();
			} else {
				mdn = otherAccountData.getMdn();
			}
		}
		
		if (otherAccountData.getImei() != null && otherAccountData.getImei().trim().length() > 0){
			if (imei != null && imei.trim().length() > 0){
				imei += COMMA + otherAccountData.getImei();
			} else {
				imei = otherAccountData.getImei();
			}
		}
		
		if (Strings.isNullOrEmpty(this.framedIPv4Address)) {
			this.framedIPv4Address = otherAccountData.getFramedIPv4Address();
		}
		
		if (Strings.isNullOrEmpty(this.framedIPv6Prefix)) {
			this.framedIPv6Prefix = otherAccountData.getFramedIPv6Prefix();
		} else {
			this.framedIPv6Prefix += COMMA + otherAccountData.getFramedIPv6Prefix();
		}
		
		if (Strings.isNullOrEmpty(this.framedPool)) {
			this.framedPool = otherAccountData.getFramedPool();
		}
		
		if (Strings.isNullOrEmpty(this.authorizationPolicyGroup)) {
			this.authorizationPolicyGroup = otherAccountData.getAuthorizationPolicyGroup();
		}
		
		if (this.macValidation == false && otherAccountData.isMacValidation()) {
			this.macValidation = true;
		}
	}
	
	public String getFramedIPv4Address() {
		return framedIPv4Address;
	}
	
	public void setFramedIPv4Address(String... framedIPv4Address) {
		if (Arrayz.isNullOrEmpty(framedIPv4Address) == false) {
			this.framedIPv4Address = framedIPv4Address[0];
		}
	}
	
	public String getFramedIPv6Prefix() {
		return framedIPv6Prefix;
	}
	
	public void setFramedIPv6Prefix(String... framedIPv6Prefix) {
		this.framedIPv6Prefix = getCommaSeparatedValue(framedIPv6Prefix);
	}
	
	public String getFramedPool() {
		return framedPool;
	}
	
	public void setFramedPool(String... framedPool) {
		if (Arrayz.isNullOrEmpty(framedPool) == false) {
			this.framedPool = framedPool[0];
		}
	}
	

	public String getAuthorizationPolicyGroup() {
		return authorizationPolicyGroup;
	}
	
	public void setAuthorizationPolicyGroup(String... authorizationPolicyGroup) {
		if (Arrayz.isNullOrEmpty(authorizationPolicyGroup) == false) {
			this.authorizationPolicyGroup = authorizationPolicyGroup[0];
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessPolicy == null) ? 0 : accessPolicy.hashCode());
		result = prime * result + ((accountStatus == null) ? 0 : accountStatus.hashCode());
		result = prime * result + ((additionalPolicy == null) ? 0 : additionalPolicy.hashCode());
		result = prime * result + ((authorizationPolicy == null) ? 0 : authorizationPolicy.hashCode());
		result = prime * result + ((authorizationPolicyGroup == null) ? 0 : authorizationPolicyGroup.hashCode());
		result = prime * result + (bCreditLimitCheck ? 1231 : 1237);
		result = prime * result + (bExpiryDateCheck ? 1231 : 1237);
		result = prime * result + (bGracePeriodApplicable ? 1231 : 1237);
		result = prime * result + ((callbackId == null) ? 0 : callbackId.hashCode());
		result = prime * result + ((calledStationId == null) ? 0 : calledStationId.hashCode());
		result = prime * result + ((callingStationId == null) ? 0 : callingStationId.hashCode());
		result = prime * result + ((checkItem == null) ? 0 : checkItem.hashCode());
		result = prime * result + ((concurrentLoginPolicy == null) ? 0 : concurrentLoginPolicy.hashCode());
		result = prime * result + (int) (creditLimit ^ (creditLimit >>> 32));
		result = prime * result + ((cui == null) ? 0 : cui.hashCode());
		result = prime * result + ((customerServices == null) ? 0 : customerServices.hashCode());
		result = prime * result + ((customerType == null) ? 0 : customerType.hashCode());
		result = prime * result + ((deviceName == null) ? 0 : deviceName.hashCode());
		result = prime * result + ((devicePort == null) ? 0 : devicePort.hashCode());
		result = prime * result + ((deviceVLAN == null) ? 0 : deviceVLAN.hashCode());
		result = prime * result + ((deviceVendor == null) ? 0 : deviceVendor.hashCode());
		result = prime * result + ((dynamicCheckItems == null) ? 0 : dynamicCheckItems.hashCode());
		result = prime * result + ((emailId == null) ? 0 : emailId.hashCode());
		result = prime * result + ((encryptionType == null) ? 0 : encryptionType.hashCode());
		result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
		result = prime * result + ((framedIPv4Address == null) ? 0 : framedIPv4Address.hashCode());
		result = prime * result + ((framedIPv6Prefix == null) ? 0 : framedIPv6Prefix.hashCode());
		result = prime * result + ((framedPool == null) ? 0 : framedPool.hashCode());
		result = prime * result + ((geoLocation == null) ? 0 : geoLocation.hashCode());
		result = prime * result + ((gracePolicy == null) ? 0 : gracePolicy.hashCode());
		result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
		result = prime * result + ((hotlinePolicy == null) ? 0 : hotlinePolicy.hashCode());
		result = prime * result + ((imei == null) ? 0 : imei.hashCode());
		result = prime * result + ((imsi == null) ? 0 : imsi.hashCode());
		result = prime * result + ((ipPoolName == null) ? 0 : ipPoolName.hashCode());
		result = prime * result + (macValidation ? 1231 : 1237);
		result = prime * result + (int) (maxSessionTime ^ (maxSessionTime >>> 32));
		result = prime * result + ((mdn == null) ? 0 : mdn.hashCode());
		result = prime * result + ((meid == null) ? 0 : meid.hashCode());
		result = prime * result + ((msisdn == null) ? 0 : msisdn.hashCode());
		result = prime * result + ((param1 == null) ? 0 : param1.hashCode());
		result = prime * result + ((param2 == null) ? 0 : param2.hashCode());
		result = prime * result + ((param3 == null) ? 0 : param3.hashCode());
		result = prime * result + ((param4 == null) ? 0 : param4.hashCode());
		result = prime * result + ((param5 == null) ? 0 : param5.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((passwordCheck == null) ? 0 : passwordCheck.hashCode());
		result = prime * result + Arrays.hashCode(pdpContexts);
		result = prime * result + ((rejectItem == null) ? 0 : rejectItem.hashCode());
		result = prime * result + ((replyItem == null) ? 0 : replyItem.hashCode());
		result = prime * result + ((serviceType == null) ? 0 : serviceType.hashCode());
		result = prime * result + ((strUserIdentity == null) ? 0 : strUserIdentity.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		AccountData other = (AccountData) obj;

		if (accessPolicy == null) {
			if (other.accessPolicy != null)
				return false;
		} else if (!accessPolicy.equals(other.accessPolicy)) {
			return false;
		}
		
		if (accountStatus == null) {
			if (other.accountStatus != null)
				return false;
		} else if (!accountStatus.equals(other.accountStatus))
			return false;
		
		if (additionalPolicy == null) {
			if (other.additionalPolicy != null)
				return false;
		} else if (!additionalPolicy.equals(other.additionalPolicy))
			return false;
		
		if (authorizationPolicy == null) {
			if (other.authorizationPolicy != null)
				return false;
		} else if (!authorizationPolicy.equals(other.authorizationPolicy))
			return false;
		
		if (authorizationPolicyGroup == null) {
			if (other.authorizationPolicyGroup != null)
				return false;
		} else if (!authorizationPolicyGroup.equals(other.authorizationPolicyGroup))
			return false;
		
		if (bCreditLimitCheck != other.bCreditLimitCheck)
			return false;
		
		if (bExpiryDateCheck != other.bExpiryDateCheck)
			return false;
		
		if (bGracePeriodApplicable != other.bGracePeriodApplicable)
			return false;
		
		if (callbackId == null) {
			if (other.callbackId != null)
				return false;
		} else if (!callbackId.equals(other.callbackId))
			return false;
		
		if (calledStationId == null) {
			if (other.calledStationId != null)
				return false;
		} else if (!calledStationId.equals(other.calledStationId))
			return false;
		
		if (callingStationId == null) {
			if (other.callingStationId != null)
				return false;
		} else if (!callingStationId.equals(other.callingStationId))
			return false;
		
		if (checkItem == null) {
			if (other.checkItem != null)
				return false;
		} else if (!checkItem.equals(other.checkItem))
			return false;
		
		if (concurrentLoginPolicy == null) {
			if (other.concurrentLoginPolicy != null)
				return false;
		} else if (!concurrentLoginPolicy.equals(other.concurrentLoginPolicy))
			return false;
		
		if (creditLimit != other.creditLimit)
			return false;
		
		if (cui == null) {
			if (other.cui != null)
				return false;
		} else if (!cui.equals(other.cui))
			return false;
		
		if (customerServices == null) {
			if (other.customerServices != null)
				return false;
		} else if (!customerServices.equals(other.customerServices))
			return false;
		
		if (customerType == null) {
			if (other.customerType != null)
				return false;
		} else if (!customerType.equals(other.customerType))
			return false;
		
		if (deviceName == null) {
			if (other.deviceName != null)
				return false;
		} else if (!deviceName.equals(other.deviceName))
			return false;
		
		if (devicePort == null) {
			if (other.devicePort != null)
				return false;
		} else if (!devicePort.equals(other.devicePort))
			return false;
		
		if (deviceVLAN == null) {
			if (other.deviceVLAN != null)
				return false;
		} else if (!deviceVLAN.equals(other.deviceVLAN))
			return false;
		
		if (deviceVendor == null) {
			if (other.deviceVendor != null)
				return false;
		} else if (!deviceVendor.equals(other.deviceVendor))
			return false;
		
		if (dynamicCheckItems == null) {
			if (other.dynamicCheckItems != null)
				return false;
		} else if (!dynamicCheckItems.equals(other.dynamicCheckItems))
			return false;
		
		if (emailId == null) {
			if (other.emailId != null)
				return false;
		} else if (!emailId.equals(other.emailId))
			return false;
		
		if (encryptionType == null) {
			if (other.encryptionType != null)
				return false;
		} else if (!encryptionType.equals(other.encryptionType))
			return false;
		
		if (expiryDate == null) {
			if (other.expiryDate != null)
				return false;
		} else if (!expiryDate.equals(other.expiryDate))
			return false;
		
		if (framedIPv4Address == null) {
			if (other.framedIPv4Address != null)
				return false;
		} else if (!framedIPv4Address.equals(other.framedIPv4Address))
			return false;
		
		if (framedIPv6Prefix == null) {
			if (other.framedIPv6Prefix != null)
				return false;
		} else if (!framedIPv6Prefix.equals(other.framedIPv6Prefix))
			return false;
		
		if (framedPool == null) {
			if (other.framedPool != null)
				return false;
		} else if (!framedPool.equals(other.framedPool))
			return false;
		
		if (geoLocation == null) {
			if (other.geoLocation != null)
				return false;
		} else if (!geoLocation.equals(other.geoLocation))
			return false;
		
		if (gracePolicy == null) {
			if (other.gracePolicy != null)
				return false;
		} else if (!gracePolicy.equals(other.gracePolicy))
			return false;
		
		if (groupName == null) {
			if (other.groupName != null)
				return false;
		} else if (!groupName.equals(other.groupName))
			return false;
		
		if (hotlinePolicy == null) {
			if (other.hotlinePolicy != null)
				return false;
		} else if (!hotlinePolicy.equals(other.hotlinePolicy))
			return false;
		
		if (imei == null) {
			if (other.imei != null)
				return false;
		} else if (!imei.equals(other.imei))
			return false;
		
		if (imsi == null) {
			if (other.imsi != null)
				return false;
		} else if (!imsi.equals(other.imsi))
			return false;
		
		if (ipPoolName == null) {
			if (other.ipPoolName != null)
				return false;
		} else if (!ipPoolName.equals(other.ipPoolName))
			return false;
		
		if (macValidation != other.macValidation)
			return false;
		
		if (maxSessionTime != other.maxSessionTime)
			return false;
		
		if (mdn == null) {
			if (other.mdn != null)
				return false;
		} else if (!mdn.equals(other.mdn))
			return false;
		
		if (meid == null) {
			if (other.meid != null)
				return false;
		} else if (!meid.equals(other.meid))
			return false;
		
		if (msisdn == null) {
			if (other.msisdn != null)
				return false;
		} else if (!msisdn.equals(other.msisdn))
			return false;
		
		if (param1 == null) {
			if (other.param1 != null)
				return false;
		} else if (!param1.equals(other.param1))
			return false;
		
		if (param2 == null) {
			if (other.param2 != null)
				return false;
		} else if (!param2.equals(other.param2))
			return false;
		
		if (param3 == null) {
			if (other.param3 != null)
				return false;
		} else if (!param3.equals(other.param3))
			return false;
		
		if (param4 == null) {
			if (other.param4 != null)
				return false;
		} else if (!param4.equals(other.param4))
			return false;
		
		if (param5 == null) {
			if (other.param5 != null)
				return false;
		} else if (!param5.equals(other.param5))
			return false;
		
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		
		if (passwordCheck == null) {
			if (other.passwordCheck != null)
				return false;
		} else if (!passwordCheck.equals(other.passwordCheck))
			return false;
		
		if (!Arrays.equals(pdpContexts, other.pdpContexts))
			return false;
		
		if (rejectItem == null) {
			if (other.rejectItem != null)
				return false;
		} else if (!rejectItem.equals(other.rejectItem))
			return false;
		
		if (replyItem == null) {
			if (other.replyItem != null)
				return false;
		} else if (!replyItem.equals(other.replyItem))
			return false;
		
		if (serviceType == null) {
			if (other.serviceType != null)
				return false;
		} else if (!serviceType.equals(other.serviceType))
			return false;
		
		if (strUserIdentity == null) {
			if (other.strUserIdentity != null)
				return false;
		} else if (!strUserIdentity.equals(other.strUserIdentity))
			return false;
		
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		
		return true;
	}
	
}