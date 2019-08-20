
package com.elitecore.corenetvertex.spr.data;

import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface SPRInfo {
	
	String UNAVAILABLE = "UNAVAILABLE";
	String ANONYMOUS = "ANONYMOUS";

	String getSubscriberIdentity();
    String getUserName();
    String getPassword();
    String getCustomerType();
    String getStatus();
    Timestamp getExpiryDate();
    int getProfileExpiredHours();
    Integer getBillingDate();
	Timestamp getNextBillDate();
	void setNextBillDate(Timestamp nextBillDate);
	Timestamp getBillChangeDate();
	void setBillChangeDate(Timestamp billChangeDate);
    String getArea();
    String getCity();
    String getParam1();
    String getParam2();
    String getParam3();
    String getParam4();
    String getParam5();
    String getZone();
    String getCountry();
    Timestamp getBirthdate();
    String getRole();
    String getCompany();
    String getDepartment();
    Long getArpu();
    String getCadre();
    String getEmail();
    String getPhone();
    String getSipURL();
    String getCui();
    String getImsi();
    String getMsisdn();
    String getImei();
    String getMac();
    String getEui64();
    String getModifiedEui64();
    int getAge();
    String getEsn();
    String getMeid();
    String getParentId();
    String getGroupName();
    String getEncryptionType();
    SubscriberLevelMetering getSubscriberLevelMetering();
    boolean isUnknownUser();
    void setUnknownUser(boolean isUnknownUser);
    SubscriberMode getSubscriberMode();
    void setSubscriberMode(SubscriberMode subscriberMode);
    String getBillingAccountId();
	String getServiceInstanceId();
	Timestamp getCreatedDate();
	Timestamp getModifiedDate();
    SubscriberNonMonitoryBalance getNonMonetaryBalance() throws OperationFailedException;
	SubscriberRnCNonMonetaryBalance getRnCNonMonetaryBalance() throws OperationFailedException;
	long getSprLoadTime();
	void setSprLoadTime(long sprLoadTime);
	void setSprReadTime(long sprReadTime);
	long getSprReadTime();
	long getUsageLoadTime();
	void setUsageLoadTime(long usageLoadTime);
	long getUsageReadTime();
	long setUsageReadTime(long usageReadTime);
	long getSubscriptionsLoadTime();
	void setSubscriptionsLoadTime(long subscriptionsLoadTime);
	void setSubscriptionsReadTime(long subscriptionsReadTime);
	long getSubscriptionsReadTime();
	boolean getPaygInternationalDataRoaming();
	void setPaygInternationalDataRoaming(boolean paygInternationalDataRoaming);
	SubscriberMonetaryBalance getMonetaryBalance(Predicate<MonetaryBalance> predicate) throws OperationFailedException;
	String getImsPackage();
	void setProductOffer(String productOffer);
	String getProductOffer();
	void setImsPackage(String imsPackage);
	LinkedHashMap<String, Subscription> getActiveSubscriptions(long currentTimeInMillies) throws OperationFailedException;
	Map<String, Map<String, SubscriberUsage>> getCurrentUsage() throws OperationFailedException;
	void addToExistingUsage(Collection<SubscriberUsage> usages) throws OperationFailedException;
	void insertNewUsage(Collection<SubscriberUsage> usages) throws OperationFailedException;
	void replaceUsage(Collection<SubscriberUsage> usages) throws OperationFailedException;
	boolean getSyInterface();
	boolean getPasswordCheck();
	void setStatus(String status);
	String getCallingStationId();
	String getNasPortId();
	String getFramedIp();
	List<String> getSPRGroupIds();


	enum SubscriberLevelMetering {
		ENABLE("Enable"), DISABLE("Disable");
		public String status;

		SubscriberLevelMetering(String status) {
			this.status = status;
		}

		public static SubscriberLevelMetering fromStatus(String status) {

			if (ENABLE.status.equalsIgnoreCase(status)) {
				return ENABLE;
			} else if (DISABLE.status.equalsIgnoreCase(status)) {
				return DISABLE;
			} else {
				return null;
			}
		}
	}
    
    
	enum SubscriberMode {
		TEST("TEST"), 
		LIVE("LIVE");

		public String val;

		SubscriberMode(String val) {
			this.val = val;
		}

		public static SubscriberMode fromValue(String val){
			if (SubscriberMode.LIVE.name().equalsIgnoreCase(val)) {
				return SubscriberMode.LIVE;
			} else if (SubscriberMode.TEST.name().equalsIgnoreCase(val)) {
				return SubscriberMode.TEST;
			}else {
				return null;
			}
		}
	}

}