package com.elitecore.nvsmx.ws.subscription.data;

import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;

/**
 * 
 * @author Jay Trivedi
 *
 */

public class PackageData {
	private String addOnPackageId;
	private String addOnPackageName;
	private String description;
	private Double price;
	private String addOnPackageType;
	private Integer validityPeriod;
	private ValidityPeriodUnit validityPeriodUnit;
	private Long availabilityEndDate;
	private String param1;
	private String param2;
	
	public PackageData(){}
	public PackageData(String addOnPackageId, String addOnPackageName, String description ,
			Double price, String addOnPackageType, Integer validityPeriod, ValidityPeriodUnit validityPeriodUnit,
			Long availabilityEndDate, String param1, String param2) {
		this.addOnPackageId = addOnPackageId;
		this.addOnPackageName = addOnPackageName;
		this.price = price;
		this.validityPeriod = validityPeriod;
		this.validityPeriodUnit = validityPeriodUnit;
		this.availabilityEndDate = availabilityEndDate;
		this.description = description;
		this.addOnPackageType = addOnPackageType;
		this.param1 = param1;
		this.param2 = param2;
	}

	public String getAddOnPackageId() {
		return addOnPackageId;
	}

	public void setAddOnPackageId(String addOnPackageId) {
		this.addOnPackageId = addOnPackageId;
	}

	public String getAddOnPackageName() {
		return addOnPackageName;
	}

	public void setAddOnPackageName(String addOnPackageName) {
		this.addOnPackageName = addOnPackageName;
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

	public String getAddOnPackageType() {
		return addOnPackageType;
	}

	public void setAddOnPackageType(String addOnPackageType) {
		this.addOnPackageType = addOnPackageType;
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

	public Long getAvailabilityEndDate() {
		return availabilityEndDate;
	}

	public void setAvailabilityEndDate(Long availabilityEndDate) {
		this.availabilityEndDate = availabilityEndDate;
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
}
