package com.elitecore.corenetvertex.spr.data;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.corenetvertex.annotation.Lazy;
import com.elitecore.corenetvertex.constants.CustomerType;
import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.spr.BalanceProvider;
import com.elitecore.corenetvertex.spr.BalanceProvider.DummyBalanceProvider;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.SubscriptionProvider;
import com.elitecore.corenetvertex.spr.SubscriptionProvider.DummySubscriptionProvider;
import com.elitecore.corenetvertex.spr.UsageProvider;
import com.elitecore.corenetvertex.spr.UsageProvider.DummyUsageProvider;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;

import javax.persistence.Transient;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static com.elitecore.commons.logging.LogManager.getLogger;


public class SPRInfoImpl implements SPRInfo,Serializable {


	private static final String MODULE = "SUB-PROFILE";

	private String subscriberIdentity;
	private String userName;
	private String password;
	private String customerType = CustomerType.PREPAID.val;
	private String status = SubscriberStatus.ACTIVE.name();
	private String productOffer;
	private String imsPackage;
	private Integer billingDate;
	private String area;
	private String city;
	private String param1;
	private String param2;
	private String param3;
	private String param4;
	private String param5;
	private String zone;
	private String country;
	private String role;
	private String company;
	private String department;
	private String cadre;
	private String email;
	private String phone;
	private String sipURL;
	private String callingStationId;
	private String nasPortId;
	private String framedIp;
	private boolean paygInternationalDataRoaming = true;
	private Timestamp nextBillDate;
	private Timestamp billChangeDate;

	/**
	 * Chargeable User Identity is unique identity for a user for Accounting
	 * Purpose / Quota Management.
	 */
	private String cui;
	private String imsi;
	private String msisdn;
	private String imei;
	private String mac;
	private String eui64;
	private String modifiedEui64;
	private String esn;
	private String meid;
	private String parentId;
	private String groupName;
	private String encryptionType;
	private SubscriberLevelMetering subscriberLevelMetering = SubscriberLevelMetering.DISABLE;
	private int age;
	private boolean isUnknownUser;
	private Timestamp createdDate;
	private Timestamp modifiedDate;
	private String billingAccountId;
	private String serviceInstanceId;

	private SubscriberMode subscriberMode = SubscriberMode.LIVE;
	/**
	 * Average Revenue per User(ARPU), generally its in percentage
	 */
	private Long arpu;

	private Timestamp expiryDate;
	private static final int PROFILE_NOT_EXPIRED = -1;
	private Timestamp birthdate;
	
	private boolean passwordCheck = false;
	private boolean syInterface = true;
	@Lazy private LinkedHashMap<String, Subscription> subscriptions; 
	
	private transient SubscriptionProvider subscriptionProvider;
	private transient UsageProvider usageProvider;
	private transient BalanceProvider balanceProvider;

	private List<String> sprGroupIds;
	private long sprLoadTime = -1;
	private long sprReadTime = -1;
	private long usageLoadTime = -1;
	private long usageReadTime = -1;
	private long subscriptionsLoadTime = -1;
	private long subscriptionsReadTime = -1;

	public  SPRInfoImpl() {
		this.subscriptionProvider = DummySubscriptionProvider.instance();
		this.usageProvider = DummyUsageProvider.instance();
		this.balanceProvider = DummyBalanceProvider.instance();
	}

	@Override
	public String getSubscriberIdentity() {
		return subscriberIdentity;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getCustomerType() {
		return customerType;
	}

	@Override
	public String getStatus() {
		return status;
	}

	@Override
	public String getProductOffer() {
		return productOffer;
	}

	@Override
	public void setProductOffer(String productOffer) {
		this.productOffer = trim(productOffer);
	}

	@Override
	public String getImsPackage() {
		return imsPackage;
	}
	
	@Override
	public void setImsPackage(String imsPackage) {
		this.imsPackage = trim(imsPackage);
	}

	@Override
	public Timestamp getExpiryDate() {
		return expiryDate;
	}
	
	/*
	 * IF expiry date is not provided THEN
	 * 	returns -1
	 * IF profile is not expired THEN
	 * 	returns -1
	 * ELSE 
	 * 	returns number of hours since profile expired.
	 */
	@Override
	public int getProfileExpiredHours() {
		if (expiryDate == null || System.currentTimeMillis() - expiryDate.getTime()  < 0) {
			return PROFILE_NOT_EXPIRED;
		}
		
		return (int) TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - expiryDate.getTime());
	}

	@Override
	public Integer getBillingDate() {
		return billingDate;
	}

	@Override
	public Timestamp getNextBillDate() {
		return nextBillDate;
	}

	@Override
	public Timestamp getBillChangeDate() {
		return billChangeDate;
	}

	@Override
	public String getArea() {
		return area;
	}

	@Override
	public String getCity() {
		return city;
	}

	@Override
	public String getParam1() {
		return param1;
	}

	@Override
	public String getParam2() {
		return param2;
	}

	@Override
	public String getParam3() {
		return param3;
	}

	@Override
	public String getParam4() {
		return param4;
	}

	@Override
	public String getParam5() {
		return param5;
	}

	@Override
	public String getZone() {
		return zone;
	}

	@Override
	public String getCountry() {
		return country;
	}

	@Override
	public Timestamp getBirthdate() {
		return birthdate;
	}

	@Override
	public String getRole() {
		return role;
	}

	@Override
	public String getCompany() {
		return company;
	}

	@Override
	public String getDepartment() {
		return department;
	}

	@Override
	public Long getArpu() {
		return arpu;
	}

	@Override
	public String getCadre() {
		return cadre;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public String getPhone() {
		return phone;
	}

	@Override
	public String getSipURL() {
		return sipURL;
	}

	@Override
	public String getCui() {
		return cui;
	}

	@Override
	public String getImsi() {
		return imsi;
	}

	@Override
	public String getMsisdn() {
		return msisdn;
	}

	@Override
	public String getImei() {
		return imei;
	}

	@Override
	public String getMac() {
		return mac;
	}

	@Override
	public String getEui64() {
		return eui64;
	}

	@Override
	public String getModifiedEui64() {
		return modifiedEui64;
	}

	@Override
	public int getAge() {
		if (age == 0) {
			age = calculateAge();
		}
		return age;
	}

	private int calculateAge() {
		int age = -1;
		if (birthdate == null) {
			return age;
		}
		
		Calendar brithdateCal = Calendar.getInstance();
		Calendar currentCal = Calendar.getInstance();
		brithdateCal.setTime(this.birthdate);
		// DoB should be less than current date
		if (brithdateCal.compareTo(currentCal) < 1) {
			age = currentCal.get(Calendar.YEAR)
					- brithdateCal.get(Calendar.YEAR);
			// When current month is less than DoB month we required to less
			// age by 1
			if (currentCal.get(Calendar.DAY_OF_YEAR) < brithdateCal
					.get(Calendar.DAY_OF_YEAR))
				age--;
		} else {
			return -1;
		}

		return age;
	}

	@Override
	public String getEsn() {
		return esn;
	}

	@Override
	public String getMeid() {
		return meid;
	}

	@Override
	public String getParentId() {
		return parentId;
	}

	@Override
	public String getGroupName() {
		return groupName;
	}

	@Override
	public String getEncryptionType() {
		return encryptionType;
	}

	@Override
	public SubscriberLevelMetering getSubscriberLevelMetering() {
		return subscriberLevelMetering;
	}

	@Override
	public boolean isUnknownUser() {
		return isUnknownUser;
	}

	@Override
	public void setUnknownUser(boolean isUnknownUser) {
		this.isUnknownUser = isUnknownUser;
	}

	public void setSubscriberIdentity(String subscriberIdentity) {
		this.subscriberIdentity = trim(subscriberIdentity);
	}

	public void setUserName(String userName) {
		this.userName = trim(userName);
	}

	public void setPassword(String password) {
		this.password = trim(password);
	}

	public void setCustomerType(String customerType) {
		this.customerType = trim(customerType);
	}

	@Override
	public void setStatus(String status) {
		if (Strings.isNullOrEmpty(status)) {
			status = SubscriberStatus.ACTIVE.name();
		}

		this.status = status;
	}

	public void setExpiryDate(Timestamp expiryDate) {
		this.expiryDate = expiryDate;
	}

	public void setBillingDate(Integer val) {
		this.billingDate = val;
	}

	public void setArea(String area) {
		this.area = trim(area);
	}

	public void setCity(String city) {
		this.city = trim(city);
	}

	public void setParam1(String param1) {
		this.param1 = trim(param1);
	}

	public void setParam2(String param2) {
		this.param2 = trim(param2);
	}

	public void setParam3(String param3) {
		this.param3 = trim(param3);
	}

	public void setParam4(String param4) {
		this.param4 = trim(param4);
	}

	public void setParam5(String param5) {
		this.param5 = trim(param5);
	}

	public void setZone(String zone) {
		this.zone = trim(zone);
	}

	public void setCountry(String country) {
		this.country = trim(country);
	}

	public void setBirthdate(Timestamp birthdate) {
		this.birthdate = birthdate;
	}

	public void setRole(String role) {
		this.role = trim(role);
	}

	public void setCompany(String company) {
		this.company = trim(company);
	}

	public void setDepartment(String department) {
		this.department = trim(department);
	}

	public void setArpu(Long arpu) {
		this.arpu = arpu;
	}

	public void setCadre(String cadre) {
		this.cadre = trim(cadre);
	}

	public void setEmail(String email) {
		this.email = trim(email);
	}

	public void setPhone(String phone) {
		this.phone = trim(phone);
	}

	public void setSipURL(String sipURL) {
		this.sipURL = trim(sipURL);
	}

	public void setCui(String cui) {
		this.cui = trim(cui);
	}

	public void setImsi(String imsi) {
		this.imsi = trim(imsi);
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = trim(msisdn);
	}

	public void setImei(String imei) {
		this.imei = trim(imei);
	}

	public void setMac(String mac) {
		this.mac = trim(mac);
	}

	public void setEui64(String eUI64) {
		this.eui64 = trim(eUI64);
	}

	public void setModifiedEui64(String modifiedEui64) {
		this.modifiedEui64 = trim(modifiedEui64);
	}

	public void setEsn(String esn) {
		this.esn = trim(esn);
	}

	public void setMeid(String meid) {
		this.meid = trim(meid);
	}

	public void setParentId(String parentId) {
		this.parentId = trim(parentId);
	}

	public void setGroupName(String groupName) {
		this.groupName = trim(groupName);
	}

	public void setEncryptionType(String encryptionType) {
		this.encryptionType = trim(encryptionType);
	}

	public void setSubscriberLevelMetering(SubscriberLevelMetering subscriberLevelMetering) {
		this.subscriberLevelMetering = subscriberLevelMetering;
	}
	public void setNextBillDate(Timestamp nextBillDate) {
		this.nextBillDate = nextBillDate;
	}

	public void setBillingAccountId(String billingAccountId) { this.billingAccountId = billingAccountId; }

	@Override
	public SubscriberMode getSubscriberMode() {
		return subscriberMode;
	}

	@Override
	public void setSubscriberMode(SubscriberMode subscriberMode) {
		this.subscriberMode = subscriberMode;
	}

	@Override
	public String getBillingAccountId() {
		return billingAccountId;
	}

	public void setServiceInstanceId(String serviceInstanceId) { this.serviceInstanceId = serviceInstanceId; }

	@Override
	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	@Override
	public boolean getPasswordCheck() {
		return passwordCheck;
	}

	public void setPasswordCheck(boolean passwordCheck) {
		this.passwordCheck = passwordCheck;
	}

	@Override
	public boolean getSyInterface() {
		return syInterface;
	}

	public void setSyInterface(boolean syInterface) {
		this.syInterface = syInterface;
	}

	@Override
	public Timestamp getModifiedDate() {
		return modifiedDate;
	}

	@Override
	public long getSprLoadTime() {
		return sprLoadTime;
	}

	@Override
	public void setSprLoadTime(long sprLoadTime) {
		this.sprLoadTime = sprLoadTime;
	}

	public void setSprReadTime(long sprReadTime) {
		this.sprReadTime = sprReadTime;
	}

	@Override
	public long getSprReadTime() {
		return this.sprReadTime;
	}

	@Override
	public long getUsageLoadTime() {
		return usageLoadTime;
	}

	@Override
	public void setUsageLoadTime(long usageLoadTime) {
		this.usageLoadTime = usageLoadTime;
	}

	@Override
	public long getUsageReadTime() {
		return usageReadTime;
	}

	@Override
	public long setUsageReadTime(long usageReadTime) {
		this.usageReadTime = usageReadTime;
		return this.usageReadTime;
	}

	@Override
	public long getSubscriptionsLoadTime() {
		return subscriptionsLoadTime;
	}

	@Override
	public void setSubscriptionsLoadTime(long subscriptionsLoadTime) {
		this.subscriptionsLoadTime = subscriptionsLoadTime;
	}

	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Override
	public void setSubscriptionsReadTime(long subscriptionsReadTime) {
		this.subscriptionsReadTime = subscriptionsReadTime;
	}

	@Override
	public long getSubscriptionsReadTime() {
		return subscriptionsReadTime;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public boolean getPaygInternationalDataRoaming() {
		return paygInternationalDataRoaming;
	}

	public void setPaygInternationalDataRoaming(boolean paygInternationalDataRoaming) {
		this.paygInternationalDataRoaming = paygInternationalDataRoaming;
	}

	public void setBillChangeDate(Timestamp billChangeDate) {
		this.billChangeDate = billChangeDate;
	}

	@Override
	public Timestamp getCreatedDate() {
		return createdDate;
	}

	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter();
		IndentingWriter out = new IndentingPrintWriter(stringWriter);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		out.println();
		out.println(" -- Subscriber Profile -- ");
		out.incrementIndentation();
		
		if (userName != null)
			out.println("User Name = " + userName);
		if (status != null)
			out.println("Status = " + status);
		if (productOffer != null)
			out.println("Product Offer = " + productOffer);
		if (imsPackage != null)
			out.println("IMS Package = " + imsPackage);
		if (expiryDate != null)
			out.println("Expiry Date (dd/MM/yyyy HH:mm:ss) = "
					+ simpleDateFormat.format(expiryDate));
		if (customerType != null)
			out.println("Customer Type = " + customerType);
		if (billingDate != null)
			out.println("Billing Date = " + billingDate);
		if (area != null)
			out.println("Area = " + area);
		if (city != null)
			out.println("City = " + city);
		if (zone != null)
			out.println("Zone = " + zone);
		if (country != null)
			out.println("Country = " + country);
		if (birthdate != null)
			out.println("Birthdate (dd/MM/yyyy HH:mm:ss) = "
					+ simpleDateFormat.format(birthdate));
		if (role != null)
			out.println("Role = " + role);
		if (company != null)
			out.println("Company = " + company);
		if (department != null)
			out.println("Department = " + department);
		if (cadre != null)
			out.println("Crade = " + cadre);
		if (arpu != null)
			out.println("ARPU = " + arpu);
		if (email != null)
			out.println("E-Mail = " + email);
		if (phone != null)
			out.println("Phone = " + phone);
		if (sipURL != null)
			out.println("SIP URL = " + sipURL);
		if (param1 != null)
			out.println("Parameter1 = " + param1);
		if (param2 != null)
			out.println("Parameter2 = " + param2);
		if (param3 != null)
			out.println("Parameter3 = " + param3);
		if (param4 != null)
			out.println("Parameter4 = " + param4);
		if (param5 != null)
			out.println("Parameter5 = " + param5);
		if (cui != null)
			out.println("CUI = " + cui);
		if (imsi != null)
			out.println("IMSI = " + imsi);
		if (imei != null)
			out.println("IMEI = " + imei);
		if (msisdn != null)
			out.println("MSISDN = " + msisdn);
		if (mac != null)
			out.println("MAC = " + mac);
		if (eui64 != null)
			out.println("EUI64 = " + eui64);
		if (modifiedEui64 != null)
			out.println("MODIFIED_EUI64 = " + modifiedEui64);
		if (esn != null)
			out.println("ESN = " + esn);
		if (meid != null)
			out.println("MEID = " + meid);
		if (parentId != null)
			out.println("Parent Id = " + parentId);
		if (groupName != null)
			out.println("Group Name = " + groupName);
		if (subscriberIdentity != null)
			out.println("Subscriber Identity = " + subscriberIdentity);

		if (nasPortId != null)
			out.println("NAS-Port-ID = " + nasPortId);
		if (callingStationId != null)
			out.println("Calling-Station-ID = " + callingStationId);
		if (billingAccountId != null)
			out.println("Billing-Account-ID = " + billingAccountId);
		if (serviceInstanceId != null)
			out.println("Service-Instance-ID = " + serviceInstanceId);

		List<String> sprGroupIds = getSPRGroupIds();
		if (sprGroupIds != null)
			out.println("SPR Group Ids = " + sprGroupIds);
		out.println("Password Check = " + passwordCheck);
		out.println("Sy Interface = " + syInterface);
		out.println("PAYG International Data Roaming = " + syInterface);
		out.println("isUnknownUser = " + isUnknownUser);
		out.println("subscriberMode = " + subscriberMode);
		out.println("Next Bill Date = " + nextBillDate);
		out.println("Bill Change Date = " + billChangeDate);
		out.println("Created Date = " + createdDate);
		out.println("Modified Date = " + modifiedDate);

		out.decrementIndentation();
		out.println();
		out.close();
		return stringWriter.toString();
	}

	public static class SPRInfoBuilder {

		private SPRInfoImpl sprInfo;
		
		public SPRInfoBuilder() {
			this.sprInfo = new SPRInfoImpl();
		}

		public SPRInfoBuilder withSubscriberIdentity(String subscriberIdentity) {
			sprInfo.subscriberIdentity = subscriberIdentity;
			return this;
		}

		public SPRInfoBuilder withUserName(String userName) {
			sprInfo.userName = userName;
			return this;
		}

		public SPRInfoBuilder withPassword(String password) {
			sprInfo.password = password;
			return this;
		}

		public SPRInfoBuilder withCustomerType(String customerType) {
			sprInfo.customerType = customerType;
			return this;
		}

		public SPRInfoBuilder withStatus(String status) {
			sprInfo.status = status;
			return this;
		}

		public SPRInfoBuilder withProductOffer(String productOffer) {
			sprInfo.productOffer = productOffer;
			return this;
		}
		
		public SPRInfoBuilder withImsPackage(String imsPackage) {
			sprInfo.imsPackage = imsPackage;
			return this;
		}

		public SPRInfoBuilder withExpiryDate(Timestamp expiryDate) {
			sprInfo.expiryDate = expiryDate;
			return this;
		}

		public SPRInfoBuilder withBillingDate(Integer billingDate) {
			sprInfo.billingDate = billingDate;
			return this;
		}

		public SPRInfoBuilder withNextBillDate(Timestamp nextBillDate) {
			sprInfo.nextBillDate = nextBillDate;
			return this;
		}

		public SPRInfoBuilder withBillChangeDate(Timestamp billChangeDate) {
			sprInfo.billChangeDate = billChangeDate;
			return this;
		}

		public SPRInfoBuilder withArea(String area) {
			sprInfo.area = area;
			return this;
		}

		public SPRInfoBuilder withCity(String city) {
			sprInfo.city = city;
			return this;
		}

		public SPRInfoBuilder withParam1(String param1) {
			sprInfo.param1 = param1;
			return this;
		}

		public SPRInfoBuilder withParam2(String param2) {
			sprInfo.param2 = param2;
			return this;
		}

		public SPRInfoBuilder withParam3(String param3) {
			sprInfo.param3 = param3;
			return this;
		}

		public SPRInfoBuilder withParam4(String param4) {
			sprInfo.param4 = param4;
			return this;
		}

		public SPRInfoBuilder withParam5(String param5) {
			sprInfo.param5 = param5;
			return this;
		}

		public SPRInfoBuilder withZone(String zone) {
			sprInfo.zone = zone;
			return this;
		}

		public SPRInfoBuilder withCountry(String country) {
			sprInfo.country = country;
			return this;
		}

		public SPRInfoBuilder withBirthdate(Timestamp birthdate) {
			sprInfo.birthdate = birthdate;
			return this;
		}

		public SPRInfoBuilder withRole(String role) {
			sprInfo.role = role;
			return this;
		}

		public SPRInfoBuilder withCompany(String company) {
			sprInfo.company = company;
			return this;
		}

		public SPRInfoBuilder withDepartment(String department) {
			sprInfo.department = department;
			return this;
		}

		public SPRInfoBuilder withArpu(Long arpu) {
			sprInfo.arpu = arpu;
			return this;
		}

		public SPRInfoBuilder withCadre(String cadre) {
			sprInfo.cadre = cadre;
			return this;
		}

		public SPRInfoBuilder withEmail(String email) {
			sprInfo.email = email;
			return this;
		}

		public SPRInfoBuilder withPhone(String phone) {
			sprInfo.phone = phone;
			return this;
		}

		public SPRInfoBuilder withSipURL(String sipURL) {
			sprInfo.sipURL = sipURL;
			return this;
		}

		public SPRInfoBuilder withCui(String cui) {
			sprInfo.cui = cui;
			return this;
		}

		public SPRInfoBuilder withImsi(String imsi) {
			sprInfo.imsi = imsi;
			return this;
		}

		public SPRInfoBuilder withMsisdn(String msisdn) {
			sprInfo.msisdn = msisdn;
			return this;
		}

		public SPRInfoBuilder withImei(String imei) {
			sprInfo.imei = imei;
			return this;
		}

		public SPRInfoBuilder withMac(String mac) {
			sprInfo.mac = mac;
			return this;
		}

		public SPRInfoBuilder withEUI64(String eUI64) {
			sprInfo.eui64 = eUI64;
			return this;
		}

		public SPRInfoBuilder withModifiedEUI64(String modifiedEui64) {
			sprInfo.modifiedEui64 = modifiedEui64;
			return this;
		}

		public SPRInfoBuilder withESN(String eSN) {
			sprInfo.esn = eSN;
			return this;
		}

		public SPRInfoBuilder withMEID(String mEID) {
			sprInfo.meid = mEID;
			return this;
		}

		public SPRInfoBuilder withParentId(String parentId) {
			sprInfo.parentId = parentId;
			return this;
		}
		
		public SPRInfoBuilder withSyInterface(boolean syInterface) {
			sprInfo.syInterface = syInterface;
			return this;
		}
		
		public SPRInfoBuilder withPasswordCheck(boolean passwordCheck) {
			sprInfo.passwordCheck = passwordCheck;
			return this;
		}

		public SPRInfoBuilder withGroupName(String groupName) {
			sprInfo.groupName = groupName;
			return this;
		}

		public SPRInfoBuilder withEncryptionType(String encryptionType) {
			sprInfo.encryptionType = encryptionType;
			return this;
		}

		public SPRInfoBuilder withSubscriberLevelMetering(SubscriberLevelMetering subscriberLevelMetering) {
			sprInfo.subscriberLevelMetering = subscriberLevelMetering;
			return this;
		}

		public SPRInfo build() {
			return sprInfo;
		}

        public SPRInfoBuilder withGroupIds(List<String> sprGroupIds) {
            sprInfo.sprGroupIds = sprGroupIds;
            return this;
        }

        public SPRInfoBuilder withPaygInternationalDataRoaming(boolean dataRoaming) {
			sprInfo.paygInternationalDataRoaming = dataRoaming;
			return this;
		}

        public SPRInfoBuilder withUsageProvider(UsageProvider usageProvider) {
            sprInfo.usageProvider = usageProvider;
            return this;
        }
	}
	/* safe trim, returns null if parameter string is null otherwise trimed value */
	private String trim(String string) {
		return string == null ? null : string.trim();
	}

	public void setUsageProvider(UsageProvider usageProvider) {
		this.usageProvider = usageProvider;
	}

	public void setSubscriptionProvider(SubscriptionProvider subscriptionProvider) {
		this.subscriptionProvider = subscriptionProvider;
	}

	public void setBalanceProvider(BalanceProvider balanceProvider) {
		this.balanceProvider = balanceProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.data.SPRInfo#getActiveSubscriptions(long)
	 * 
	 * 
	 */
	@Transient 
	public LinkedHashMap<String, Subscription> getActiveSubscriptions(long currentTimeInMillies) throws OperationFailedException {
		
		/* 
		 * - null subscription means subscription is not retrieved yet.
		 * - empty subscription means subscription not available from DB, 
		 * So DON'T CHECK FOR EMPTY 
		 */
		if (subscriptions == null) {
			
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Fetching subscriptions from datasource for subscriber ID: " + subscriberIdentity);
			}
			subscriptions = subscriptionProvider.getSubscriptions(this);
		} else {
			
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Fetching subscriptions from cache for subscriber ID: " + subscriberIdentity);
			}
			
			Set<Entry<String, Subscription>> subscriptionEntries = subscriptions.entrySet();
			Iterator<Entry<String, Subscription>> iteratorSubscriptionEntries = subscriptionEntries.iterator();
			
			while (iteratorSubscriptionEntries.hasNext()) {
				Entry<String, Subscription> subscriptionEntry = iteratorSubscriptionEntries.next();
				if (subscriptionEntry.getValue().isExpired(currentTimeInMillies)) {
					if (getLogger().isDebugLogLevel()) {
						getLogger().debug(MODULE, "Skipping subscription(ID:" + subscriptionEntry.getValue().getId() 
								+ ", Package id: " + subscriptionEntry.getValue().getPackageId() + ") of subscriber ID: " 
								+ subscriberIdentity + ". Reason: Subscription is expired on " + subscriptionEntry.getValue().getEndTime().toString());
					}
					iteratorSubscriptionEntries.remove();
				}
			}
		} 
		
		return subscriptions;
	}
	
	@Override
	public Map<String, Map<String, SubscriberUsage>> getCurrentUsage() throws OperationFailedException {
		return usageProvider.getCurrentUsage(this);
	}
	
	@Transient public void addToExistingUsage(Collection<SubscriberUsage> usages) throws OperationFailedException {
		usageProvider.addToExisting(subscriberIdentity, usages);
	}
	
	@Transient public void insertNewUsage(Collection<SubscriberUsage> usages) throws OperationFailedException {
		usageProvider.insertNew(subscriberIdentity, usages);
	}
	
	@Transient public void replaceUsage(Collection<SubscriberUsage> usages) throws OperationFailedException {
		usageProvider.replace(subscriberIdentity, usages);
	}

	@Override
	public String getCallingStationId() {
		return callingStationId;
	}

	public void setCallingStationId(String callingStationId) {
		this.callingStationId = trim(callingStationId);
	}

	@Override
	public String getNasPortId() {
		return nasPortId;
	}

	public void setNasPortId(String nasPortId) {
		this.nasPortId = trim(nasPortId);
	}

	@Override
	public String getFramedIp() {
		return framedIp;
	}

	public void setFramedIp(String framedIp) {
		this.framedIp = trim(framedIp);
	}

    public void setSPRGroupIds(List<String> sprGroupIds) {
		this.sprGroupIds = sprGroupIds;
	}
	
	@Override
	public List<String> getSPRGroupIds() {
		return this.sprGroupIds;
	}

	@Override
	public SubscriberMonetaryBalance getMonetaryBalance(Predicate<MonetaryBalance> predicate) throws OperationFailedException {
		return balanceProvider.getMonetaryBalance(subscriberIdentity, predicate);
	}

	@Override
	public SubscriberNonMonitoryBalance getNonMonetaryBalance() throws OperationFailedException {
		return balanceProvider.getNonMonetaryBalance(this);
	}

	@Override
	public SubscriberRnCNonMonetaryBalance getRnCNonMonetaryBalance() throws OperationFailedException {
		return balanceProvider.getRnCNonMonetaryBalance(subscriberIdentity);

	}
}
