package com.elitecore.corenetvertex.pd.partner;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pd.account.AccountData;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.elitecore.corenetvertex.sm.location.city.CityData;
import com.elitecore.corenetvertex.sm.location.region.RegionData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Entity(name = "com.elitecore.corenetvertex.pd.partner.PartnerData")
@Table(name = "TBLM_PARTNER")
public class PartnerData extends DefaultGroupResourceData implements Serializable {

	private static final long serialVersionUID = -4518812073152087251L;
	private String name;
	private String partnerLegalName;
	private Timestamp registraionDate;
	private String registrationNum;
	private CountryData country;
	private RegionData region;
	private CityData city;
	private Long postCode;
	private String primaryContactName;
	private String primaryContactDesignation;
	private Long primaryContactNumber;
	private String primaryEmailAddress;
	private String helpDesk;
	private String webSiteUrl;
	private String secondaryContactName;
	private String secondaryContactDesignation;
	private Long secondaryContactNumber;
	private String secondaryEmailAddress;
	private String faxNumber;
	private Boolean isUnsignedPartner;

	private List<AccountData> accountData;

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "partnerData", orphanRemoval = false)
	@Fetch(FetchMode.SUBSELECT)
	@Where(clause = "STATUS != 'DELETED'")
	public List<AccountData> getAccountData() {
		return accountData;
	}

	public void setAccountData(List<AccountData> accountData) {
		this.accountData = accountData;
	}

	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "PARTNER_LEGAL_NAME")
	public String getPartnerLegalName() {
		return partnerLegalName;
	}

	public void setPartnerLegalName(String partnerLegalName) {
		this.partnerLegalName = partnerLegalName;
	}

	@Column(name = "REGISTRATION_DATE")
	public Timestamp getRegistraionDate() {
		return registraionDate;
	}

	public void setRegistraionDate(Timestamp registraionDate) {
		this.registraionDate = registraionDate;
	}

	@Column(name = "REGISTRATION_NUMBER")
	public String getRegistrationNum() {
		return registrationNum;
	}

	public void setRegistrationNum(String registrationNum) {
		this.registrationNum = registrationNum;
	}

	@JsonIgnore
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "COUNTRY_ID")
	public CountryData getCountry() {
		return country;
	}

	public void setCountry(CountryData country) {
		this.country = country;
	}

	@JsonIgnore
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "REGION_ID")
	public RegionData getRegion() {
		return region;
	}

	public void setRegion(RegionData region) {
		this.region = region;
	}

	@JsonIgnore
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CITY_ID")
	public CityData getCity() {
		return city;
	}

	public void setCity(CityData city) {
		this.city = city;
	}

	@Transient
	public String getCountryId() {
		if (this.getCountry() != null) {
			return getCountry().getId();
		}
		return null;
	}

	public void setCountryId(String countryId) {
		if (Strings.isNullOrBlank(countryId) == false) {
			CountryData countryIdObj = new CountryData();
			countryIdObj.setId(countryId);
			this.country = countryIdObj;
		}
	}

	@Transient
	public String getRegionId() {
		if (this.getRegion() != null) {
			return getRegion().getId();
		}
		return null;
	}

	public void setRegionId(String regionId) {
		if (Strings.isNullOrBlank(regionId) == false) {
			RegionData regionIdObj = new RegionData();
			regionIdObj.setId(regionId);
			this.region = regionIdObj;
		}
	}

	@Transient
	public String getCityId() {
		if (this.getCity() != null) {
			return getCity().getId();
		}
		return null;
	}

	public void setCityId(String cityId) {
		if (Strings.isNullOrBlank(cityId) == false) {
			CityData cityIdObj = new CityData();
			cityIdObj.setId(cityId);
			this.city = cityIdObj;
		}
	}

	@Column(name = "POSTCODE")
	public Long getPostCode() {
		return postCode;
	}

	public void setPostCode(Long postCode) {
		this.postCode = postCode;
	}

	@Column(name = "PRIMARY_CONTACT_NAME")
	public String getPrimaryContactName() {
		return primaryContactName;
	}

	public void setPrimaryContactName(String primaryContactName) {
		this.primaryContactName = primaryContactName;
	}

	@Column(name = "PRIMARY_CONTACT_DESIG")
	public String getPrimaryContactDesignation() {
		return primaryContactDesignation;
	}

	public void setPrimaryContactDesignation(String primaryContactDesignation) {
		this.primaryContactDesignation = primaryContactDesignation;
	}

	@Column(name = "PRIMARY_CONTACT_NUMBER")
	public Long getPrimaryContactNumber() {
		return primaryContactNumber;
	}

	public void setPrimaryContactNumber(Long primaryContactNumber) {
		this.primaryContactNumber = primaryContactNumber;
	}

	@Column(name = "PRIMARY_EMAIL_ADDRESS")
	public String getPrimaryEmailAddress() {
		return primaryEmailAddress;
	}

	public void setPrimaryEmailAddress(String primaryEmailAddress) {
		this.primaryEmailAddress = primaryEmailAddress;
	}

	@Column(name = "HELP_DESK")
	public String getHelpDesk() {
		return helpDesk;
	}

	public void setHelpDesk(String helpDesk) {
		this.helpDesk = helpDesk;
	}

	@Column(name = "WEBSITE_URL")
	public String getWebSiteUrl() {
		return webSiteUrl;
	}

	public void setWebSiteUrl(String webSiteUrl) {
		this.webSiteUrl = webSiteUrl;
	}

	@Column(name = "SECONDARY_CONTACT_NAME")
	public String getSecondaryContactName() {
		return secondaryContactName;
	}

	public void setSecondaryContactName(String secondaryContactName) {
		this.secondaryContactName = secondaryContactName;
	}

	@Column(name = "SECONDARY_CONTACT_DESIG")
	public String getSecondaryContactDesignation() {
		return secondaryContactDesignation;
	}

	public void setSecondaryContactDesignation(String secondaryContactDesignation) {
		this.secondaryContactDesignation = secondaryContactDesignation;
	}

	@Column(name = "SECONDARY_CONTACT_NUMBER")
	public Long getSecondaryContactNumber() {
		return secondaryContactNumber;
	}

	public void setSecondaryContactNumber(Long secondaryContactNumber) {
		this.secondaryContactNumber = secondaryContactNumber;
	}

	@Column(name = "SECONDARY_EMAIL_ADDRESS")
	public String getSecondaryEmailAddress() {
		return secondaryEmailAddress;
	}

	public void setSecondaryEmailAddress(String secondaryEmailAddress) {
		this.secondaryEmailAddress = secondaryEmailAddress;
	}

	@Column(name = "FAX_NUMBER")
	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}
	
	@Column(name = "IS_UNSIGNED_PARTNER")
	public Boolean getIsUnsignedPartner() {
		return isUnsignedPartner;
	}

	public void setIsUnsignedPartner(Boolean isUnsignedPartner) {
		this.isUnsignedPartner = isUnsignedPartner;
	}

	@Override
	@Column(name = "STATUS")
	public String getStatus() {
		return super.getStatus();
	}

	@JsonIgnore
	@Transient
	@Override
	public String getResourceName() {
		return getName();
	}

	@Override
	public JsonObject toJson() {

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.NAME, name);
		jsonObject.addProperty(FieldValueConstants.PARTNER_LEGAL_NAME, partnerLegalName);
		jsonObject.addProperty(FieldValueConstants.REGISTRATION_DATE, String.valueOf(registraionDate));
		jsonObject.addProperty(FieldValueConstants.REGISTRATION_NUMBER, registrationNum);
		if (getCountry() != null) {
			jsonObject.addProperty(FieldValueConstants.COUNTRY_ID, country.getName());
		}
		if (getRegion() != null) {
			jsonObject.addProperty(FieldValueConstants.REGION_ID, region.getName());
		}
		if (getCity() != null) {
			jsonObject.addProperty(FieldValueConstants.CITY_ID, city.getName());
		}
		jsonObject.addProperty(FieldValueConstants.POSTCODE, postCode);
		jsonObject.addProperty(FieldValueConstants.PRIMARY_CONTACT_NAME, primaryContactName);
		jsonObject.addProperty(FieldValueConstants.PRIMARY_CONTACT_DESIG, primaryContactDesignation);
		jsonObject.addProperty(FieldValueConstants.PRIMARY_CONTACT_NUMBER, primaryContactNumber);
		jsonObject.addProperty(FieldValueConstants.PRIMARY_EMAIL_ADDRESS, primaryEmailAddress);
		jsonObject.addProperty(FieldValueConstants.HELP_DESK, helpDesk);
		jsonObject.addProperty(FieldValueConstants.WEBSITE_URL, webSiteUrl);
		jsonObject.addProperty(FieldValueConstants.SECONDARY_CONTACT_NAME, secondaryContactName);
		jsonObject.addProperty(FieldValueConstants.SECONDARY_CONTACT_DESIG, secondaryContactDesignation);
		jsonObject.addProperty(FieldValueConstants.SECONDARY_CONTACT_NUMBER, secondaryContactNumber);
		jsonObject.addProperty(FieldValueConstants.SECONDARY_EMAIL_ADDRESS, secondaryEmailAddress);
		jsonObject.addProperty(FieldValueConstants.FAX_NUMBER, faxNumber);
		jsonObject.addProperty(FieldValueConstants.FAX_NUMBER, faxNumber);
		jsonObject.addProperty(FieldValueConstants.IS_UNSIGNED_PARTNER, isUnsignedPartner);
		jsonObject.addProperty(FieldValueConstants.STATUS, getStatus());
		jsonObject.addProperty(FieldValueConstants.GROUPS, getGroups());

		if (accountData != null) {
			JsonArray jsonArray = new JsonArray();
			for (AccountData account : accountData) {
				jsonArray.add(account.toJson());
			}
			jsonObject.add("Account Data", jsonArray);
		}

		return jsonObject;
	}
}