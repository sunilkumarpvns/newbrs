package com.elitecore.netvertexsm.datamanager.RoutingTable.network.data;

import com.elitecore.netvertexsm.web.core.base.BaseData;

public class CountryData extends BaseData {
	private Long countryID;
	private String name;
	private String code;
	
	public Long getCountryID() {
		return countryID;
	}
	
	public void setCountryID(Long countryID) {
		this.countryID = countryID;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
}
