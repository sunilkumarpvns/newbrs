package com.elitecore.corenetvertex.spr.data;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.EnumMap;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.QueryBuilder;

@Entity
@Table(name = "TBLM_SUBSCRIBER")
@com.elitecore.corenetvertex.spr.Table(name = "TBLM_SUBSCRIBER")
public class SubscriberProfileData implements DMLProvider {

    private String userName;
    private String password;
    private String imsi;
    private String subscriberIdentity;
    private String msisdn;
    private String phone;
    private String customerType;
    private String status = SubscriberStatus.ACTIVE.name();
    private String subscriberPackage;
    private Timestamp expiryDate;
    private String billingDate;
    private String area;
    private String city;
    private String param1;
    private String param2;
    private String param3;
    private String param4;
    private String param5;
    private String zone;
    private String country;
    private Timestamp birthdate;
    private String role;
    private String company;
    private String department;
    private String arpu;
    private String cadre;
    private String email;
    private String sipURL;
    private String cui;
    private String imei;
    private String mac;
    private String eUI64;
    private String mODIFIED_EUI64;
    private String eSN;
    private String mEID;
    private String parentId;
    private String groupName;
    private String encryptionType;
    private String subscriberLevelMetering;
    private String productOffer;
    private String imsPackage;
    private String callingStationId;
    private boolean passwordCheck = false;
    private boolean syInterface = true;
    private boolean paygInternationalDataRoaming = true;
    private String hsqMultiplier;
    private String fupMultiplier;
    private String nasPortId;
    private String framedIp;
    private String billingAccountId;
    private String serviceInstanceId;
    private Timestamp createdDate;
    private Timestamp modifiedDate;
    private Timestamp nextBillDate;
    private Timestamp billChangeDate;

    private Map<SPRFields, String> fieldToValue;

    public SubscriberProfileData() {
        fieldToValue = new EnumMap<>(SPRFields.class);
    }

    public void setCallingStationId(String callingStationId) {
        this.callingStationId = callingStationId;
    }

    @javax.persistence.Column(name = "HSQ_MULTIPLIER")
    @Column(name = "HSQ_MULTIPLIER", type = Types.VARCHAR)
    public String getHsqMultiplier() {
        return hsqMultiplier;
    }

	@javax.persistence.Column(name = "FUP_MULTIPLIER")
    @Column(name = "FUP_MULTIPLIER", type = Types.VARCHAR)
    public String getFupMultiplier() {
        return fupMultiplier;
    }

	@javax.persistence.Column(name = "PRODUCT_OFFER")
    @Column(name = "PRODUCT_OFFER", type = Types.VARCHAR)
    public String getProductOffer() {
        return productOffer;
    }

	@javax.persistence.Column(name = "IMSPACKAGE")
    @Column(name = "IMSPACKAGE", type = Types.VARCHAR)
    public String getImsPackage() {
        return imsPackage;
    }

	@javax.persistence.Column(name = "PASSWORD_CHECK")
    @Column(name = "PASSWORD_CHECK", type = Types.VARCHAR)
    public boolean getPasswordCheck() {
        return passwordCheck;
    }

	@javax.persistence.Column(name = "SY_INTERFACE")
    @Column(name = "SY_INTERFACE", type = Types.VARCHAR)
    public boolean getSyInterface() {
        return syInterface;
    }

	@javax.persistence.Column(name = "PAYG_INTL_DATA_ROAMING")
    @Column(name = "PAYG_INTL_DATA_ROAMING", type = Types.VARCHAR)
    public boolean isPaygInternationalDataRoaming() {
        return paygInternationalDataRoaming;
    }

	@javax.persistence.Column(name = "USERNAME")
    @Column(name = "USERNAME", type = Types.VARCHAR)
    public String getUserName() {
        return userName;
    }

	@javax.persistence.Column(name = "PASSWORD")
    @Column(name = "PASSWORD", type = Types.VARCHAR)
    public String getPassword() {
        return password;
    }

	@javax.persistence.Column(name = "IMSI")
    @Column(name = "IMSI", type = Types.VARCHAR)
    public String getImsi() {
        return imsi;
    }

	@Id
	@javax.persistence.Column(name = "SUBSCRIBERIDENTITY")
    @Column(name = "SUBSCRIBERIDENTITY", type = Types.VARCHAR)
    public String getSubscriberIdentity() {
        return subscriberIdentity;
    }

	public void setSubscriberIdentity(String subscriberIdentity) {
		this.subscriberIdentity = subscriberIdentity;
	}

	@javax.persistence.Column(name = "MSISDN")
    @Column(name = "MSISDN", type = Types.VARCHAR)
    public String getMsisdn() {
        return msisdn;
    }

	@javax.persistence.Column(name = "PHONE")
    @Column(name = "PHONE", type = Types.VARCHAR)
    public String getPhone() {
        return phone;
    }

	@javax.persistence.Column(name = "CUSTOMERTYPE")
    @Column(name = "CUSTOMERTYPE", type = Types.VARCHAR)
    public String getCustomerType() {
        return customerType;
    }

	@javax.persistence.Column(name = "STATUS")
    @Column(name = "STATUS", type = Types.VARCHAR)
    public String getStatus() {
        return status;
    }

	@javax.persistence.Column(name = "SUBSCRIBERPACKAGE")
    @Column(name = "SUBSCRIBERPACKAGE", type = Types.VARCHAR)
    public String getSubscriberPackage() {
        return subscriberPackage;
    }

	@javax.persistence.Column(name = "EXPIRYDATE")
    @Column(name = "EXPIRYDATE", type = Types.TIMESTAMP)
    public Timestamp getExpiryDate() {
        return expiryDate;
    }

	@javax.persistence.Column(name = "BILLINGDATE")
    @Column(name = "BILLINGDATE", type = Types.NUMERIC)
    public String getBillingDate() {
        return billingDate;
    }

	@javax.persistence.Column(name = "AREA")
    @Column(name = "AREA", type = Types.VARCHAR)
    public String getArea() {
        return area;
    }

	@javax.persistence.Column(name = "CITY")
    @Column(name = "CITY", type = Types.VARCHAR)
    public String getCity() {
        return city;
    }

	@javax.persistence.Column(name = "PARAM1")
    @Column(name = "PARAM1", type = Types.VARCHAR)
    public String getParam1() {
        return param1;
    }

	@javax.persistence.Column(name = "PARAM2")
    @Column(name = "PARAM2", type = Types.VARCHAR)
    public String getParam2() {
        return param2;
    }

	@javax.persistence.Column(name = "PARAM3")
    @Column(name = "PARAM3", type = Types.VARCHAR)
    public String getParam3() {
        return param3;
    }

	@javax.persistence.Column(name = "PARAM4")
    @Column(name = "PARAM4", type = Types.VARCHAR)
    public String getParam4() {
        return param4;
    }

	@javax.persistence.Column(name = "PARAM5")
    @Column(name = "PARAM5", type = Types.VARCHAR)
    public String getParam5() {
        return param5;
    }

	@javax.persistence.Column(name = "ZONE")
    @Column(name = "ZONE", type = Types.VARCHAR)
    public String getZone() {
        return zone;
    }

	@javax.persistence.Column(name = "COUNTRY")
    @Column(name = "COUNTRY", type = Types.VARCHAR)
    public String getCountry() {
        return country;
    }

	@javax.persistence.Column(name = "BIRTHDATE")
    @Column(name = "BIRTHDATE", type = Types.TIMESTAMP)
    public Timestamp getBirthdate() {
        return birthdate;
    }

	@javax.persistence.Column(name = "ROLE")
    @Column(name = "ROLE", type = Types.VARCHAR)
    public String getRole() {
        return role;
    }

	@javax.persistence.Column(name = "COMPANY")
    @Column(name = "COMPANY", type = Types.VARCHAR)
    public String getCompany() {
        return company;
    }

	@javax.persistence.Column(name = "DEPARTMENT")
    @Column(name = "DEPARTMENT", type = Types.VARCHAR)
    public String getDepartment() {
        return department;
    }

	@javax.persistence.Column(name = "ARPU")
    @Column(name = "ARPU", type = Types.NUMERIC)
    public String getArpu() {
        return arpu;
    }

	@javax.persistence.Column(name = "CADRE")
    @Column(name = "CADRE", type = Types.VARCHAR)
    public String getCadre() {
        return cadre;
    }

	@javax.persistence.Column(name = "EMAIL")
    @Column(name = "EMAIL", type = Types.VARCHAR)
    public String getEmail() {
        return email;
    }

	@javax.persistence.Column(name = "SIPURL")
    @Column(name = "SIPURL", type = Types.VARCHAR)
    public String getSipURL() {
        return sipURL;
    }

	@javax.persistence.Column(name = "CUI")
    @Column(name = "CUI", type = Types.VARCHAR)
    public String getCui() {
        return cui;
    }

	@javax.persistence.Column(name = "IMEI")
    @Column(name = "IMEI", type = Types.VARCHAR)
    public String getImei() {
        return imei;
    }

	@javax.persistence.Column(name = "MAC")
    @Column(name = "MAC", type = Types.VARCHAR)
    public String getMac() {
        return mac;
    }

	@javax.persistence.Column(name = "EUI64")
    @Column(name = "EUI64", type = Types.VARCHAR)
    public String geteUI64() {
        return eUI64;
    }

	@javax.persistence.Column(name = "MODIFIED_EUI64")
    @Column(name = "MODIFIED_EUI64", type = Types.VARCHAR)
    public String getmODIFIED_EUI64() {
        return mODIFIED_EUI64;
    }

	@javax.persistence.Column(name = "ESN")
    @Column(name = "ESN", type = Types.VARCHAR)
    public String geteSN() {
        return eSN;
    }

	@javax.persistence.Column(name = "MEID")
    @Column(name = "MEID", type = Types.VARCHAR)
    public String getmEID() {
        return mEID;
    }

	@javax.persistence.Column(name = "PARENTID")
    @Column(name = "PARENTID", type = Types.VARCHAR)
    public String getParentId() {
        return parentId;
    }

	@javax.persistence.Column(name = "GROUPNAME")
    @Column(name = "GROUPNAME", type = Types.VARCHAR)
    public String getGroupName() {
        return groupName;
    }

	@javax.persistence.Column(name = "ENCRYPTIONTYPE")
    @Column(name = "ENCRYPTIONTYPE", type = Types.NUMERIC)
    public String getEncryptionType() {
        return encryptionType;
    }

	@javax.persistence.Column(name = "SUBSCRIBERLEVELMETERING")
    @Column(name = "SUBSCRIBERLEVELMETERING", type = Types.VARCHAR)
    public String getSubscriberLevelMetering() {
        return subscriberLevelMetering;
    }


	@javax.persistence.Column(name = "CALLING_STATION_ID")
    @Column(name = "CALLING_STATION_ID", type = Types.VARCHAR)
    public String getCallingStationId() {
        return callingStationId;
    }

	@javax.persistence.Column(name = "NAS_PORT_ID")
    @Column(name = "NAS_PORT_ID", type = Types.VARCHAR)
    public String getNasPortId() {
        return nasPortId;
    }

	@javax.persistence.Column(name = "FRAMED_IP")
    @Column(name = "FRAMED_IP", type = Types.VARCHAR)
    public String getFramedIp() {
        return framedIp;
    }

	@javax.persistence.Column(name = "CREATED_DATE")
    @Column(name = "CREATED_DATE", type = Types.TIMESTAMP)
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    @javax.persistence.Column(name = "NEXTBILLDATE")
    @Column(name = "NEXTBILLDATE", type = Types.TIMESTAMP)
    public Timestamp getNextBillDate() {
        return nextBillDate;
    }

    public void setNextBillDate(Timestamp nextBillDate) {
        this.nextBillDate = nextBillDate;
    }

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setSubscriberPackage(String subscriberPackage) {
		this.subscriberPackage = subscriberPackage;
	}

	public void setExpiryDate(Timestamp expiryDate) {
		this.expiryDate = expiryDate;
	}

	public void setBillingDate(String billingDate) {
		this.billingDate = billingDate;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	public void setParam3(String param3) {
		this.param3 = param3;
	}

	public void setParam4(String param4) {
		this.param4 = param4;
	}

	public void setParam5(String param5) {
		this.param5 = param5;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setBirthdate(Timestamp birthdate) {
		this.birthdate = birthdate;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public void setArpu(String arpu) {
		this.arpu = arpu;
	}

	public void setCadre(String cadre) {
		this.cadre = cadre;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setSipURL(String sipURL) {
		this.sipURL = sipURL;
	}

	public void setCui(String cui) {
		this.cui = cui;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public void seteUI64(String eUI64) {
		this.eUI64 = eUI64;
	}

	public void setmODIFIED_EUI64(String mODIFIED_EUI64) {
		this.mODIFIED_EUI64 = mODIFIED_EUI64;
	}

	public void seteSN(String eSN) {
		this.eSN = eSN;
	}

	public void setmEID(String mEID) {
		this.mEID = mEID;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setEncryptionType(String encryptionType) {
		this.encryptionType = encryptionType;
	}

	public void setSubscriberLevelMetering(String subscriberLevelMetering) {
		this.subscriberLevelMetering = subscriberLevelMetering;
	}

	public void setProductOffer(String productOffer) {
		this.productOffer = productOffer;
	}

	public void setImsPackage(String imsPackage) {
		this.imsPackage = imsPackage;
	}

	public void setPasswordCheck(boolean passwordCheck) {
		this.passwordCheck = passwordCheck;
	}

	public void setSyInterface(boolean syInterface) {
		this.syInterface = syInterface;
	}

	public void setPaygInternationalDataRoaming(boolean paygInternationalDataRoaming) {
		this.paygInternationalDataRoaming = paygInternationalDataRoaming;
	}

	public void setHsqMultiplier(String hsqMultiplier) {
		this.hsqMultiplier = hsqMultiplier;
	}

	public void setFupMultiplier(String fupMultiplier) {
		this.fupMultiplier = fupMultiplier;
	}

	public void setNasPortId(String nasPortId) {
		this.nasPortId = nasPortId;
	}

	public void setFramedIp(String framedIp) {
		this.framedIp = framedIp;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@javax.persistence.Column(name = "MODIFIED_DATE")
    @Column(name = "MODIFIED_DATE", type = Types.TIMESTAMP)
    public Timestamp getModifiedDate() {
        return modifiedDate;
    }

    @javax.persistence.Column(name = "BILLING_ACCOUNT_ID")
    @Column(name = "BILLING_ACCOUNT_ID", type = Types.VARCHAR)
    public String getBillingAccountId() {
        return billingAccountId;
    }

    public void setBillingAccountId(String billingAccountId) {
        this.billingAccountId = billingAccountId;
    }

    @javax.persistence.Column(name = "SERVICE_INSTANCE_ID")
    @Column(name = "SERVICE_INSTANCE_ID", type = Types.VARCHAR)
    public String getServiceInstanceId() {
        return serviceInstanceId;
    }

    public void setServiceInstanceId(String serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    @javax.persistence.Column(name = "BILLCHANGEDATE")
    @Column(name = "BILLCHANGEDATE", type = Types.TIMESTAMP)
    public Timestamp getBillChangeDate() {
        return billChangeDate;
    }

    public void setBillChangeDate(Timestamp billChangeDate) {
        this.billChangeDate = billChangeDate;
    }

    public static class SubscriberProfileDataBuilder {
        private SubscriberProfileData data;

        public SubscriberProfileDataBuilder() {
            data = new SubscriberProfileData();
        }

        public SubscriberProfileDataBuilder withSubscriberIdentity(String subscriberIdentity) {
            data.subscriberIdentity = subscriberIdentity;
            data.fieldToValue.put(SPRFields.SUBSCRIBER_IDENTITY, subscriberIdentity);
            return this;
        }

        public SubscriberProfileDataBuilder withUserName(String userName) {
            data.userName = userName;
            data.fieldToValue.put(SPRFields.USERNAME, userName);
            return this;
        }

		public SubscriberProfileDataBuilder withProductOffer(String productOffer) {
			data.productOffer = productOffer;
			data.fieldToValue.put(SPRFields.PRODUCT_OFFER, productOffer);
			return this;
		}

        public SubscriberProfileDataBuilder withPassword(String password) {
            data.password = password;
            data.fieldToValue.put(SPRFields.PASSWORD, password);
            return this;
        }

        public SubscriberProfileDataBuilder withImsi(String imsi) {
            data.imsi = imsi;
            data.fieldToValue.put(SPRFields.IMSI, imsi);
            return this;
        }

        public SubscriberProfileDataBuilder withMsisdn(String msisdn) {
            data.msisdn = msisdn;
            data.fieldToValue.put(SPRFields.MSISDN, msisdn);
            return this;
        }

        public SubscriberProfileDataBuilder withPhone(String phone) {
            data.phone = phone;
            data.fieldToValue.put(SPRFields.PHONE, phone);
            return this;
        }

        public SubscriberProfileDataBuilder withCustomerType(String customerType) {
            data.customerType = customerType;
            data.fieldToValue.put(SPRFields.CUSTOMER_TYPE, customerType);
            return this;
        }

        public SubscriberProfileDataBuilder withStatus(String status) {
            data.status = status;
            data.fieldToValue.put(SPRFields.STATUS, status);
            return this;
        }

        public SubscriberProfileDataBuilder withSubscriberPackage(String subscriberPackage) {
            data.subscriberPackage = subscriberPackage;
            data.fieldToValue.put(SPRFields.PRODUCT_OFFER, subscriberPackage);
            return this;
        }

        public SubscriberProfileDataBuilder withExpiryDate(Timestamp expiryDate) {
            data.expiryDate = expiryDate;
            data.fieldToValue.put(SPRFields.EXPIRY_DATE, expiryDate.toString());
            return this;
        }

        public SubscriberProfileDataBuilder withBillingDate(String billingDate) {
            data.billingDate = billingDate;
            data.fieldToValue.put(SPRFields.BILLING_DATE, billingDate);
            return this;
        }

        public SubscriberProfileDataBuilder withNextBillDate(Timestamp nextBillDate) {
            data.nextBillDate = nextBillDate;
            data.fieldToValue.put(SPRFields.NEXT_BILL_DATE, nextBillDate.toString());
            return this;
        }

        public SubscriberProfileDataBuilder withBillChangeDate(Timestamp billChangeDate) {
            data.billChangeDate = billChangeDate;
            data.fieldToValue.put(SPRFields.BILL_CHANGE_DATE, billChangeDate.toString());
            return this;
        }

        public SubscriberProfileDataBuilder withArea(String area) {
            data.area = area;
            data.fieldToValue.put(SPRFields.AREA, area);
            return this;
        }

        public SubscriberProfileDataBuilder withCity(String city) {
            data.city = city;
            data.fieldToValue.put(SPRFields.CITY, city);
            return this;
        }

        public SubscriberProfileDataBuilder withParam1(String param1) {
            data.param1 = param1;
            data.fieldToValue.put(SPRFields.PARAM1, param1);
            return this;
        }

        public SubscriberProfileDataBuilder withParam2(String param2) {
            data.param2 = param2;
            data.fieldToValue.put(SPRFields.PARAM2, param2);
            return this;
        }

        public SubscriberProfileDataBuilder withParam3(String param3) {
            data.param3 = param3;
            data.fieldToValue.put(SPRFields.PARAM3, param3);
            return this;
        }

        public SubscriberProfileDataBuilder withParam4(String param4) {
            data.param4 = param4;
            data.fieldToValue.put(SPRFields.PARAM4, param4);
            return this;
        }

        public SubscriberProfileDataBuilder withParam5(String param5) {
            data.param5 = param5;
            data.fieldToValue.put(SPRFields.PARAM5, param5);
            return this;
        }

        public SubscriberProfileDataBuilder withZone(String zone) {
            data.zone = zone;
            data.fieldToValue.put(SPRFields.ZONE, zone);
            return this;
        }

        public SubscriberProfileDataBuilder withCountry(String country) {
            data.country = country;
            data.fieldToValue.put(SPRFields.COUNTRY, country);
            return this;
        }

        public SubscriberProfileDataBuilder withBirthdate(Timestamp birthdate) {
            data.birthdate = birthdate;
            data.fieldToValue.put(SPRFields.BIRTH_DATE, birthdate.toString());
            return this;
        }

        public SubscriberProfileDataBuilder withRole(String role) {
            data.role = role;
            data.fieldToValue.put(SPRFields.ROLE, role);
            return this;
        }

        public SubscriberProfileDataBuilder withCompany(String company) {
            data.company = company;
            data.fieldToValue.put(SPRFields.COMPANY, company);
            return this;
        }

        public SubscriberProfileDataBuilder withDepartment(String department) {
            data.department = department;
            data.fieldToValue.put(SPRFields.DEPARTMENT, department);
            return this;
        }

        public SubscriberProfileDataBuilder withArpu(String arpu) {
            data.arpu = arpu;
            data.fieldToValue.put(SPRFields.ARPU, arpu);
            return this;
        }

        public SubscriberProfileDataBuilder withCadre(String cadre) {
            data.cadre = cadre;
            data.fieldToValue.put(SPRFields.CADRE, cadre);
            return this;
        }

        public SubscriberProfileDataBuilder withEmail(String email) {
            data.email = email;
            data.fieldToValue.put(SPRFields.EMAIL, email);
            return this;
        }

        public SubscriberProfileDataBuilder withSipURL(String sipURL) {
            data.sipURL = sipURL;
            data.fieldToValue.put(SPRFields.SIP_URL, sipURL);
            return this;
        }

        public SubscriberProfileDataBuilder withCui(String cui) {
            data.cui = cui;
            data.fieldToValue.put(SPRFields.CUI, cui);
            return this;
        }

        public SubscriberProfileDataBuilder withImei(String imei) {
            data.imei = imei;
            data.fieldToValue.put(SPRFields.IMEI, imei);
            return this;
        }

        public SubscriberProfileDataBuilder withMac(String mac) {
            data.mac = mac;
            data.fieldToValue.put(SPRFields.MAC, mac);
            return this;
        }

        public SubscriberProfileDataBuilder withEUI64(String eUI64) {
            data.eUI64 = eUI64;
            data.fieldToValue.put(SPRFields.EUI64, eUI64);
            return this;
        }

        public SubscriberProfileDataBuilder withMODIFIED_EUI64(String mODIFIED_EUI64) {
            data.mODIFIED_EUI64 = mODIFIED_EUI64;
            data.fieldToValue.put(SPRFields.MODIFIED_EUI64, mODIFIED_EUI64);
            return this;
        }

        public SubscriberProfileDataBuilder withESN(String eSN) {
            data.eSN = eSN;
            data.fieldToValue.put(SPRFields.ESN, eSN);
            return this;
        }

        public SubscriberProfileDataBuilder withMEID(String mEID) {
            data.mEID = mEID;
            data.fieldToValue.put(SPRFields.MEID, mEID);
            return this;
        }

        public SubscriberProfileDataBuilder withParentId(String parentId) {
            data.parentId = parentId;
            data.fieldToValue.put(SPRFields.PARENT_ID, parentId);
            return this;
        }

        public SubscriberProfileDataBuilder withGroupName(String groupName) {
            data.groupName = groupName;
            data.fieldToValue.put(SPRFields.GROUP_NAME, groupName);
            return this;
        }

        public SubscriberProfileDataBuilder withEncryptionType(String encryptionType) {
            data.encryptionType = encryptionType;
            data.fieldToValue.put(SPRFields.ENCRYPTION_TYPE, encryptionType);
            return this;
        }


        public SubscriberProfileDataBuilder withSubscriberLevelMetering(String subscriberLevelMetering) {
            data.subscriberLevelMetering = subscriberLevelMetering;
            data.fieldToValue.put(SPRFields.SUBSCRIBER_LEVEL_METERING, subscriberLevelMetering);
            return this;
        }

        public SubscriberProfileDataBuilder withCallingStationId(String callingStationId) {
            data.callingStationId = callingStationId;
            data.fieldToValue.put(SPRFields.CALLING_STATION_ID, callingStationId);
            return this;
        }

        public SubscriberProfileDataBuilder withNasPortId(String nasPortId) {
            data.nasPortId = nasPortId;
            data.fieldToValue.put(SPRFields.NAS_PORT_ID, nasPortId);
            return this;
        }

        public SubscriberProfileDataBuilder withFramedIp(String framedIp) {
            data.framedIp = framedIp;
            data.fieldToValue.put(SPRFields.FRAMED_IP, framedIp);
            return this;
        }

        public SubscriberProfileDataBuilder withCreatedDate(Timestamp createdDate) {
            data.createdDate = createdDate;
            data.fieldToValue.put(SPRFields.CREATED_DATE, createdDate.toString());
            return this;
        }

        public SubscriberProfileDataBuilder withModifiedDate(Timestamp modifiedDate) {
            data.modifiedDate = modifiedDate;
            data.fieldToValue.put(SPRFields.MODIFIED_DATE, modifiedDate.toString());
            return this;
        }

        public SubscriberProfileData build() {
            return data;
        }
    }



    @Override
    public String insertQuery() throws IllegalArgumentException, NullPointerException, IllegalAccessException, InvocationTargetException {
        return QueryBuilder.buildInsertQuery(this);
    }

    public static String createTableQuery() {
        return QueryBuilder.buildCreateQuery(SubscriberProfileData.class);
    }

    public static String dropTableQuery() {
        return QueryBuilder.buildDropQuery(SubscriberProfileData.class);
    }


    @Transient
    public SPRInfoImpl getSPRInfo() throws OperationFailedException {
        SPRInfoImpl info = new SPRInfoImpl();
        boolean validate = false;

        for (SPRFields sprFields : SPRFields.values()) {
            sprFields.setStringValue(info, fieldToValue.get(sprFields), validate);
        }

        return info;
    }

}
