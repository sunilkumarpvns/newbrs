package com.elitecore.netvertexsm.datamanager.RoutingTable.network.data;

import java.util.List;

public class CountryMCCRelationData {
	private int countryMCCrelationId;
	
	private int countryId;
	private String mcc;
	private List<String> mccList;
	
	public int getCountryMCCrelationId() {
		return countryMCCrelationId;
	}
	public void setCountryMCCrelationId(int countryMCCrelationId) {
		this.countryMCCrelationId = countryMCCrelationId;
	}
	public int getCountryId() {
		return countryId;
	}
	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}
	public List<String> getMccList() {
		return mccList;
	}
	public void setMccList(List<String> mccList) {
		this.mccList = mccList;
	}
	
	

}
