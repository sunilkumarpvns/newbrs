package com.elitecore.netvertexsm.datamanager.gateway.profile.data;

import java.util.List;
import java.util.Set;

public class DiameterPacketMapData {
	private long diameterPacketMapId;
	private String condition;
	private String packetType;
	private long gatewayProfileId;
	
	private List<DiameterAttributeMapData> attributeMapList;
	private Set<DiameterAttributeMapData> diameterAttrMapSet;
	
	public Set<DiameterAttributeMapData> getDiameterAttrMapSet() {
		return diameterAttrMapSet;
	}
	public void setDiameterAttrMapSet(Set<DiameterAttributeMapData> diameterAttrMapSet) {
		this.diameterAttrMapSet = diameterAttrMapSet;
	}
	public List<DiameterAttributeMapData> getAttributeMapList() {
		return attributeMapList;
	}
	public void setAttributeMapList(List<DiameterAttributeMapData> attributeMapList) {
		this.attributeMapList = attributeMapList;
	}
	public long getDiameterPacketMapId() {
		return diameterPacketMapId;
	}
	public void setDiameterPacketMapId(long diameterPacketMapId) {
		this.diameterPacketMapId = diameterPacketMapId;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getPacketType() {
		return packetType;
	}
	public void setPacketType(String packetType) {
		this.packetType = packetType;
	}
	public long getGatewayProfileId() {
		return gatewayProfileId;
	}
	public void setGatewayProfileId(long gatewayProfileId) {
		this.gatewayProfileId = gatewayProfileId;
	}
}
