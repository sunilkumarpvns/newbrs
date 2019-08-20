package com.elitecore.netvertexsm.datamanager.locationconfig.area.data;

public class LacAreaRelationData {
	
	private long lacId;
	private long areaId;
    private LacData lacData;
    private AreaData areaData;
	public long getLacId() {
		return lacId;
	}
	public void setLacId(long lacId) {
		this.lacId = lacId;
	}
	public long getAreaId() {
		return areaId;
	}
	public void setAreaId(long areaId) {
		this.areaId = areaId;
	}
	public LacData getLacData() {
		return lacData;
	}
	public void setLacData(LacData lacData) {
		this.lacData = lacData;
	}
	public AreaData getAreaData() {
		return areaData;
	}
	public void setAreaData(AreaData areaData) {
		this.areaData = areaData;
	}
}
