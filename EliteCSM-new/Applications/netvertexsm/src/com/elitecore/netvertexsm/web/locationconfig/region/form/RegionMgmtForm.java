package com.elitecore.netvertexsm.web.locationconfig.region.form;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.CountryData;
import com.elitecore.netvertexsm.datamanager.locationconfig.region.data.RegionData;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class RegionMgmtForm extends BaseWebForm {
	
	private long regionId;
	private String regionName;
	
	private long pageNumber;		
	private long totalPages;
	private long totalRecords;
	private String action;
	private long countryId;
	private List<CountryData> countryList;
	private List<RegionData>  regionDataList;
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
	public List<RegionData> getRegionDataList() {
		return regionDataList;
	}
	public void setRegionDataList(List<RegionData> regionDataList) {
		this.regionDataList = regionDataList;
	}
	public long getCountryId() {
		return countryId;
	}
	public void setCountryId(long countryId) {
		this.countryId = countryId;
	}

	

}
