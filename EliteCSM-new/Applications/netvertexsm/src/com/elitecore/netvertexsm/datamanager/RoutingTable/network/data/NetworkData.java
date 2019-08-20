package com.elitecore.netvertexsm.datamanager.RoutingTable.network.data;

import com.elitecore.netvertexsm.web.core.base.BaseData;

public class NetworkData extends BaseData{
	private Long networkID;
	private Long operatorID;
	private String networkName;
	private Long countryID;
	private Integer mcc;
	private Integer mnc;
	private Long brandID;
	private String technology;	
	private CountryData countryData;
	private OperatorData operatorData;
	private BrandData brandData;


	public BrandData getBrandData() {
		return brandData;
	}

	public void setBrandData(BrandData brandData) {
		this.brandData = brandData;
	}

	public Long getBrandID() {
		return brandID;
	}

	public void setBrandID(Long brandID) {
		this.brandID = brandID;
	}

	public String getTechnology() {
		return technology;
	}

	public void setTechnology(String technology) {
		this.technology = technology;
	}

	public CountryData getCountryData() {
		return countryData;
	}

	public void setCountryData(CountryData countryData) {
		this.countryData = countryData;
	}

	public OperatorData getOperatorData() {
		return operatorData;
	}

	public void setOperatorData(OperatorData operatorData) {
		this.operatorData = operatorData;
	}

	public Long getNetworkID() {
		return networkID;
	}
	
	public void setNetworkID(Long networkID) {
		this.networkID = networkID;
	}
	
	public Long getOperatorID() {
		return operatorID;
	}
	
	public void setOperatorID(Long operatorID) {
		this.operatorID = operatorID;
	}
	
	public String getNetworkName() {
		return networkName;
	}
	
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
	
	public Long getCountryID() {
		return countryID;
	}
	
	public void setCountryID(Long countryID) {
		this.countryID = countryID;
	}
	
	public Integer getMcc() {
		return mcc;
	}
	
	public void setMcc(Integer mcc) {
		this.mcc = mcc;
	}
	
	public Integer getMnc() {
		return mnc;
	}
	
	public void setMnc(Integer mnc) {
		this.mnc = mnc;
	}
}