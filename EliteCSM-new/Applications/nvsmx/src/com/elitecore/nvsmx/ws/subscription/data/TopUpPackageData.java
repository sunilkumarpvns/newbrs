package com.elitecore.nvsmx.ws.subscription.data;

import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;


/**
 * 
 * @author Ishani Bhatt
 *
 */
public class TopUpPackageData {
	private String topUpPackageId;
	private String topUpPackageName;
	private String description;
	private Double price;
	private String packageType;
	private Integer validityPeriod;
	private ValidityPeriodUnit validityPeriodUnit;
	private String param1;
	private String param2;

	private String quotaType;
	private String unitType;
	private Long volumeBalance;
	private String volumeBalanceUnit;
	private Long timeBalance;
	private String timeBalanceUnit;
	private String applicablePCCProfiles;

	public TopUpPackageData(){}
	public TopUpPackageData(String topUpPackageId, String topUpPackageName, String description ,
			Double price, String packageType, Integer validityPeriod, ValidityPeriodUnit validityPeriodUnit,
			 String param1, String param2, String quotaType, String unitType, Long volumeBalance,
			String volumeBalanceUnit, Long timeBalance, String timeBalanceUnit,String applicablePCCProfiles) {
		this.topUpPackageId = topUpPackageId;
		this.topUpPackageName = topUpPackageName;
		this.price = price;
		this.validityPeriod = validityPeriod;
		this.validityPeriodUnit = validityPeriodUnit;
		this.description = description;
		this.packageType = packageType;
		this.param1 = param1;
		this.param2 = param2;

		this.quotaType = quotaType;
		this.unitType = unitType;
		this.volumeBalance = volumeBalance;
		this.volumeBalanceUnit = volumeBalanceUnit;
		this.timeBalance = timeBalance;
		this.timeBalanceUnit = timeBalanceUnit;
		this.applicablePCCProfiles = applicablePCCProfiles;
	}

	public String getTopUpPackageId() {
		return topUpPackageId;
	}

	public void setTopUpPackageId(String topUpPackageId) {
		this.topUpPackageId = topUpPackageId;
	}

	public String getTopUpPackageName() {
		return topUpPackageName;
	}

	public void setTopUpPackageName(String topUpPackageName) {
		this.topUpPackageName = topUpPackageName;
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

	public String getQuotaType() {
		return quotaType;
	}

	public void setQuotaType(String quotaType) {
		this.quotaType = quotaType;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	public Long getVolumeBalance() {
		return volumeBalance;
	}

	public void setVolumeBalance(Long volumeBalance) {
		this.volumeBalance = volumeBalance;
	}

	public String getVolumeBalanceUnit() {
		return volumeBalanceUnit;
	}

	public void setVolumeBalanceUnit(String volumeBalanceUnit) {
		this.volumeBalanceUnit = volumeBalanceUnit;
	}

	public Long getTimeBalance() {
		return timeBalance;
	}

	public void setTimeBalance(Long timeBalance) {
		this.timeBalance = timeBalance;
	}

	public String getTimeBalanceUnit() {
		return timeBalanceUnit;
	}

	public void setTimeBalanceUnit(String timeBalanceUnit) {
		this.timeBalanceUnit = timeBalanceUnit;
	}

	public String getApplicablePCCProfiles() {
		return applicablePCCProfiles;
	}

	public void setApplicablePCCProfiles(String applicablePCCProfiles) {
		this.applicablePCCProfiles = applicablePCCProfiles;
	}
}