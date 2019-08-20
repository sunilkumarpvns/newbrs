package com.elitecore.netvertexsm.datamanager.locationconfig.region.data;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.CountryData;
import com.elitecore.netvertexsm.datamanager.locationconfig.city.data.CityData;

public class RegionData {

	
	private long regionId;
	private String regionName;
	private long countryId;
	private CountryData countryData;
	private List<CityData> cityDataList;
	
	
	public long getRegionId() {
		return regionId;
	}
	public void setRegionId(long regionId) {
		this.regionId = regionId;
	}
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	public long getCountryId() {
		return countryId;
	}
	public void setCountryId(long countryId) {
		this.countryId = countryId;
	}
	public CountryData getCountryData() {
		return countryData;
	}
	public void setCountryData(CountryData countryData) {
		this.countryData = countryData;
	}
	public List<CityData> getCityDataList() {
		return cityDataList;
	}
	public void setCityDataList(List<CityData> cityDataList) {
		this.cityDataList = cityDataList;
	}
	
	
	
}
