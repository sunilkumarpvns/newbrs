package com.elitecore.netvertexsm.datamanager.locationconfig.area.data;

public class WiFiCallingStationInfoData {
   
	private long wifissidinfo_id;
    private String ssids;
    private long areaId;
    private AreaData areaData;
    
	public long getWifissidinfo_id() {
		return wifissidinfo_id;
	}
	public void setWifissidinfo_id(long wifissidinfo_id) {
		this.wifissidinfo_id = wifissidinfo_id;
	}
	public String getSsids() {
		return ssids;
	}
	public void setSsids(String ssids) {
		this.ssids = ssids;
	}
	public long getAreaId() {
		return areaId;
	}
	public void setAreaId(long areaId) {
		this.areaId = areaId;
	}
	public AreaData getAreaData() {
		return areaData;
	}
	public void setAreaData(AreaData areaData) {
		this.areaData = areaData;
	}   
	
}
