package com.elitecore.netvertexsm.web.locationconfig.area.form;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.CountryData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.CountryMCCRelationData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.NetworkData;
import com.elitecore.netvertexsm.datamanager.locationconfig.area.data.LacData;
import com.elitecore.netvertexsm.datamanager.locationconfig.area.data.AreaData;
import com.elitecore.netvertexsm.datamanager.locationconfig.city.data.CityData;
import com.elitecore.netvertexsm.datamanager.locationconfig.region.data.RegionData;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class AreaMgmtForm extends BaseWebForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long areaId;
	private String area;
	private String description;
	//Search Parameters
    private long pageNumber;		
	private long totalPages;
	private long totalRecords;
	private String actionName;
	private long regionId;
	private long cityId;
	private Long networkId;
	private long countryId;
	private List<AreaData> areaDataList;
	private List<CountryData> listCountryData;
	private List<CountryMCCRelationData> listCountryMCCRelData;
	private List<RegionData> listRegionData;
	private List<CityData> listCityData;
	private List<NetworkData> listNetworkData;
	
	private List<LacData> lacList;
	private String strWiFiSSIDs;
	private String strCallingStationIds;
	private String param1;
	private String param2;
	private String param3;		
	
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
	public String getParam3() {
		return param3;
	}
	public void setParam3(String param3) {
		this.param3 = param3;
	}
	public long getCountryId() {
		return countryId;
	}
	public void setCountryId(long countryId) {
		this.countryId = countryId;
	}
	public Long getAreaId() {
		return areaId;
	}
	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public long getRegionId() {
		return regionId;
	}
	public void setRegionId(long regionId) {
		this.regionId = regionId;
	}
	public long getCityId() {
		return cityId;
	}
	public void setCityId(long cityId) {
		this.cityId = cityId;
	}
	public Long getNetworkId() {
		return networkId;
	}
	public void setNetworkId(Long networkId) {
		this.networkId = networkId;
	}
	public List<CountryData> getListCountryData() {
		return listCountryData;
	}
	public void setListCountryData(List<CountryData> listCountryData) {
		this.listCountryData = listCountryData;
	}
	public List<CountryMCCRelationData> getListCountryMCCRelData() {
		return listCountryMCCRelData;
	}
	public void setListCountryMCCRelData(
			List<CountryMCCRelationData> listCountryMCCRelData) {
		this.listCountryMCCRelData = listCountryMCCRelData;
	}
	public List<RegionData> getListRegionData() {
		return listRegionData;
	}
	public void setListRegionData(List<RegionData> listRegionData) {
		this.listRegionData = listRegionData;
	}
	public List<CityData> getListCityData() {
		return listCityData;
	}
	public void setListCityData(List<CityData> listCityData) {
		this.listCityData = listCityData;
	}
	public List<NetworkData> getListNetworkData() {
		return listNetworkData;
	}
	public void setListNetworkData(List<NetworkData> listNetworkData) {
		this.listNetworkData = listNetworkData;
	}
	public List<LacData> getLacList() {
		return lacList;
	}
	public void setLacList(List<LacData> lacList) {
		this.lacList = lacList;
	}
	public String getStrWiFiSSIDs() {
		return strWiFiSSIDs;
	}
	public void setStrWiFiSSIDs(String strWiFiSSIDs) {
		this.strWiFiSSIDs = strWiFiSSIDs;
	}
	public String getStrCallingStationIds() {
		return strCallingStationIds;
	}
	public void setStrCallingStationIds(String strCallingStationIds) {
		this.strCallingStationIds = strCallingStationIds;
	}
	public List<AreaData> getAreaDataList() {
		return areaDataList;
	}
	public void setAreaDataList(List<AreaData> areaDataList) {
		this.areaDataList = areaDataList;
	} 	
}
	
	