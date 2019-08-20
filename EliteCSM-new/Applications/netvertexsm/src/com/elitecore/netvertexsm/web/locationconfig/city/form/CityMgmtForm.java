package com.elitecore.netvertexsm.web.locationconfig.city.form;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.CountryData;
import com.elitecore.netvertexsm.datamanager.locationconfig.city.data.CityData;
import com.elitecore.netvertexsm.datamanager.locationconfig.region.data.RegionData;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class CityMgmtForm extends BaseWebForm{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long cityId;
	private String cityName;
	
	private long pageNumber;		
	private long totalPages;
	private long totalRecords;
	private String action;
	private long countryId;
	private long regionId;
	private List<CountryData> countryList;
	private List<RegionData>  regionList;
	private List<CityData>     cityList;
	public long getCityId() {
		return cityId;
	}
	public void setCityId(long cityId) {
		this.cityId = cityId;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public long getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
	}
	public long getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
	}
	public long getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List<CountryData> getCountryList() {
		return countryList;
	}
	public void setCountryList(List<CountryData> countryList) {
		this.countryList = countryList;
	}
	public List<RegionData> getRegionList() {
		return regionList;
	}
	public void setRegionList(List<RegionData> regionList) {
		this.regionList = regionList;
	}
	public List<CityData> getCityList() {
		return cityList;
	}
	public void setCityList(List<CityData> cityList) {
		this.cityList = cityList;
	}
	public long getCountryId() {
		return countryId;
	}
	public void setCountryId(long countryId) {
		this.countryId = countryId;
	}
	public long getRegionId() {
		return regionId;
	}
	public void setRegionId(long regionId) {
		this.regionId = regionId;
	}
	
	
	
	
}
