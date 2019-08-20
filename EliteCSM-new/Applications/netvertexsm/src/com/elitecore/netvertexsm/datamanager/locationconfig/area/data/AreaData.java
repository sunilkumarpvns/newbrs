package com.elitecore.netvertexsm.datamanager.locationconfig.area.data;

import java.util.List;
import java.util.Set;

import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.NetworkData;
import com.elitecore.netvertexsm.datamanager.locationconfig.city.data.CityData;



public class AreaData {
	private Long areaId;				 
    private String area;
    private Long cityId;
    private Long networkId;
    private Set<LacData> lacDataSet;
    private List<LacData> lacDataList;
    private CityData cityData;
    private NetworkData networkData;
    private WiFiCallingStationInfoData wifiCallingStationInfoData;
    private CallingStationInfoData callingStationInfoData;
    private List<LacData> deleteLacIds;
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
	public NetworkData getNetworkData() {
		return networkData;
	}
	public void setNetworkData(NetworkData networkData) {
		this.networkData = networkData;
	}
	public WiFiCallingStationInfoData getWifiCallingStationInfoData() {
		return wifiCallingStationInfoData;
	}
	public void setWifiCallingStationInfoData(
			WiFiCallingStationInfoData wifiCallingStationInfoData) {
		this.wifiCallingStationInfoData = wifiCallingStationInfoData;
	}
	public CallingStationInfoData getCallingStationInfoData() {
		return callingStationInfoData;
	}
	public void setCallingStationInfoData(
			CallingStationInfoData callingStationInfoData) {
		this.callingStationInfoData = callingStationInfoData;
	}
	public List<LacData> getLacDataList() {
		return lacDataList;
	}
	public void setLacDataList(List<LacData> lacDataList) {
		this.lacDataList = lacDataList;
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
	public Long getCityId() {
		return cityId;
	}
	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}
	public Set<LacData> getLacDataSet() {
		return lacDataSet;
	}
	public void setLacDataSet(Set<LacData> lacDataSet) {
		this.lacDataSet = lacDataSet;
	}
	public CityData getCityData() {
		return cityData;
	}
	public void setCityData(CityData cityData) {
		this.cityData = cityData;
	}
	public List<LacData> getDeleteLacIds() {
		return deleteLacIds;
	}
	public void setDeleteLacIds(List<LacData> deleteLacIds) {
		this.deleteLacIds = deleteLacIds;
	}
	public Long getNetworkId() {
		return networkId;
	}
	public void setNetworkId(Long networkId) {
		this.networkId = networkId;
	}	
}