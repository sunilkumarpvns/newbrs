package com.elitecore.nvsmx.ws.subscription.data;

import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;

import java.util.List;


/**
 *
 * @author Kartik Prajapati
 * Data class for service response
 *
 */
public class BoDPackageData {
	private String id;
	private String name;
	private String description;
	private String packageMode;
	private Integer validityPeriod;
	private ValidityPeriodUnit validityPeriodUnit;
	private List<BoDQoSMultiplierData> qosMultipliers;
	public BoDPackageData(String id, String name, String description, String packageMode,
						  Integer validityPeriod, ValidityPeriodUnit validityPeriodUnit,
						  List<BoDQoSMultiplierData> qosMultipliers) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.packageMode = packageMode;
		this.validityPeriod = validityPeriod;
		this.validityPeriodUnit = validityPeriodUnit;
		this.qosMultipliers = qosMultipliers;
	}

	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public String getPackageMode() {
		return packageMode;
	}
	public Integer getValidityPeriod() {
		return validityPeriod;
	}
	public ValidityPeriodUnit getValidityPeriodUnit() {
		return validityPeriodUnit;
	}
	public List<BoDQoSMultiplierData> getQosMultipliers() {
		return qosMultipliers;
	}
}