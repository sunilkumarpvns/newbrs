package com.elitecore.netvertexsm.datamanager.locationconfig.city.data;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.locationconfig.area.data.AreaData;
import com.elitecore.netvertexsm.datamanager.locationconfig.region.data.RegionData;

public class CityData {

	private long cityId;
	private String cityName;
	private long regionId;
	private RegionData region;
	private List<AreaData> areaList;
	
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
	public long getRegionId() {
		return regionId;
	}
	public void setRegionId(long regionId) {
		this.regionId = regionId;
	}
	public RegionData getRegion() {
		return region;
	}
	public void setRegion(RegionData region) {
		this.region = region;
	}
	public List<AreaData> getAreaList() {
		return areaList;
	}
	public void setAreaList(List<AreaData> areaList) {
		this.areaList = areaList;
	}
}
