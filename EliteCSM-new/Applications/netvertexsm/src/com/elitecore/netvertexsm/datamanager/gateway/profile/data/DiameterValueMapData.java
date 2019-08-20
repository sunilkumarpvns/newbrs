package com.elitecore.netvertexsm.datamanager.gateway.profile.data;

public class DiameterValueMapData {
	private long valueMapId;
	private String attributeVal;
	private String keyVal;
	private long diameterAttributeId;
	
	private DiameterAttributeMapData attributeMapData; 
	
	public long getDiameterAttributeId() {
		return diameterAttributeId;
	}
	public void setDiameterAttributeId(long diameterAttributeId) {
		this.diameterAttributeId = diameterAttributeId;
	}
	public DiameterAttributeMapData getAttributeMapData() {
		return attributeMapData;
	}
	public void setAttributeMapData(DiameterAttributeMapData attributeMapData) {
		this.attributeMapData = attributeMapData;
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
