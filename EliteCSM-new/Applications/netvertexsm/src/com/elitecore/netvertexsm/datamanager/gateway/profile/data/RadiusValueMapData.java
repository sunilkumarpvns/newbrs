package com.elitecore.netvertexsm.datamanager.gateway.profile.data;

public class RadiusValueMapData {
	private long valueMapId;
	private String attributeVal;
	private String keyVal;
	private long radiusAttributeId;
	private RadiusAttributeMapData attributeMapData; 
	
	public RadiusAttributeMapData getAttributeMapData() {
		return attributeMapData;
	}
	public void setAttributeMapData(RadiusAttributeMapData attributeMapData) {
		this.attributeMapData = attributeMapData;
	}
	public long getRadiusAttributeId() {
		return radiusAttributeId;
	}
	public void setRadiusAttributeId(long radiusAttributeId) {
		this.radiusAttributeId = radiusAttributeId;
	}
	public long getValueMapId() {
		return valueMapId;
	}
	public void setValueMapId(long valueMapId) {
		this.valueMapId = valueMapId;
	}
	public String getAttributeVal() {
		return attributeVal;
	}
	public void setAttributeVal(String attributeVal) {
		this.attributeVal = attributeVal;
	}
	public String getKeyVal() {
		return keyVal;
	}
	public void setKeyVal(String keyVal) {
		this.keyVal = keyVal;
	}
}
