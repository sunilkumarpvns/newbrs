package com.elitecore.netvertexsm.datamanager.locationconfig.area.data;

public class CallingStationInfoData {

	private long csId;
	private String callingStaionIds;
	private long areaId;
	private AreaData areaData;
	
	public long getCsId() {
		return csId;
	}
	public void setCsId(long csId) {
		this.csId = csId;
	}
	public String getCallingStaionIds() {
		return callingStaionIds;
	}
	public void setCallingStaionIds(String callingStaionIds) {
		this.callingStaionIds = callingStaionIds;
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
