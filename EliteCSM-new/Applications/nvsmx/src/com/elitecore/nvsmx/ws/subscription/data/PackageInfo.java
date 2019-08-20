package com.elitecore.nvsmx.ws.subscription.data;

import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;

import java.util.List;


/**
 * Created by aditya on 9/6/16.
 */
public class PackageInfo  {
	private String packageId;
	private String packageName;
	private String description;
	private Double price;
	private String packageType;
	private Long availabilityStartDate;
	private Long availabilityEndDate;
	private Integer validityPeriod;
	private ValidityPeriodUnit validityPeriodUnit;
	private List<QuotaProfileData> quotaProfiles;
	private String param1;
	private String param2;
	private String packageMode;
	private String packageStatus;
	private String currency;
	private List<String> groupNames;


	public PackageInfo(){}


	public PackageInfo(String packageId, String packageName,
					   String description, Double price, String packageType,
					   Long availabilityStartDate, Long availabilityEndDate, String packageMode, String packageStatus, List<QuotaProfileData> quotaProfiles,List<String> groupNames, String currency) {
		super();
		this.packageId = packageId;
		this.packageName = packageName;
		this.description = description;
		this.price = price;
		this.packageType = packageType;
		this.availabilityStartDate = availabilityStartDate;
		this.availabilityEndDate = availabilityEndDate;
		this.quotaProfiles = quotaProfiles;
		this.packageMode=packageMode;
		this.packageStatus = packageStatus;
		this.groupNames = groupNames;
		this.currency=currency;
	}

	public PackageInfo(String packageId, String packageName,
					   String description, Double price, String packageType,
					   Long availabilityStartDate, Long availabilityEndDate, String packageMode, String packageStatus,List<String> groupNames, String param1, String param2, String currency) {
		super();
		this.packageId = packageId;
		this.packageName = packageName;
		this.description = description;
		this.price = price;
		this.packageType = packageType;
		this.availabilityStartDate = availabilityStartDate;
		this.availabilityEndDate = availabilityEndDate;
		this.param1 = param1;
		this.param2 = param2;
		this.packageMode=packageMode;
		this.packageStatus = packageStatus;
		this.groupNames=groupNames;
		this.currency=currency;
	}

	public Integer getValidityPeriod() {
		return validityPeriod;
	}

	public void setValidityPeriod(Integer validityPeriod) {
		this.validityPeriod = validityPeriod;
	}

	public ValidityPeriodUnit getValidityPeriodUnit() {
		return validityPeriodUnit;
	}

	public void setValidityPeriodUnit(ValidityPeriodUnit validityPeriodUnit) {
		this.validityPeriodUnit = validityPeriodUnit;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public Long getAvailabilityStartDate() {
		return availabilityStartDate;
	}

	public void setAvailabilityStartDate(Long availabilityStartDate) {
		this.availabilityStartDate = availabilityStartDate;
	}

	public Long getAvailabilityEndDate() {
		return availabilityEndDate;
	}

	public void setAvailabilityEndDate(Long availabilityEndDate) {
		this.availabilityEndDate = availabilityEndDate;
	}

	public List<QuotaProfileData> getQuotaProfiles() {
		return quotaProfiles;
	}

	public void setQuotaProfiles(List<QuotaProfileData> quotaProfiles) {
		this.quotaProfiles = quotaProfiles;
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

	public String getPackageMode() {
		return packageMode;
	}

	public void setPackageMode(String packageMode) {
		this.packageMode = packageMode;
	}

	public String getPackageStatus() {
		return packageStatus;
	}

	public void setPackageStatus(String packageStatus) {
		this.packageStatus = packageStatus;
	}

	public List<String> getGroupNames() { return groupNames; }

	public void setGroupNames(List<String> groupNames) { this.groupNames = groupNames; }

	public String getCurrency() { return currency; }

	public void setCurrency(String currency) { this.currency = currency; }
}
