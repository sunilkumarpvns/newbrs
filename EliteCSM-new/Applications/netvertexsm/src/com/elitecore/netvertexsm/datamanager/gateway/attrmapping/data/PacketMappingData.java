package com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data;

import java.util.List;
import java.util.Set;

import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData;
import com.elitecore.netvertexsm.web.core.base.BaseData;

public class PacketMappingData extends BaseData{
	private long packetMapId;
	private String name;
	private String description;
	private String commProtocol;
	private String packetType;
	private String type;
	private List<AttributeMappingData> attributeMappings;
	private Set<AttributeMappingData> attributeMapSet;
/*	private Set<GatewayProfilePacketMapData> gatewayProfilePacketMapDataSet;
	
	public Set<GatewayProfilePacketMapData> getGatewayProfilePacketMapDataSet() {
		return gatewayProfilePacketMapDataSet;
	}
	public void setGatewayProfilePacketMapDataSet(
			Set<GatewayProfilePacketMapData> gatewayProfilePacketMapDataSet) {
		this.gatewayProfilePacketMapDataSet = gatewayProfilePacketMapDataSet;
	}*/
	public Set<AttributeMappingData> getAttributeMapSet() {
		return attributeMapSet;
	}
	public void setAttributeMapSet(Set<AttributeMappingData> attributeMapSet) {
		this.attributeMapSet = attributeMapSet;
	}
	public List<AttributeMappingData> getAttributeMappings() {
		return attributeMappings;
	}
	public void setAttributeMappings(List<AttributeMappingData> attributeMappings) {
		this.attributeMappings = attributeMappings;
	}
	public long getPacketMapId() {
		return packetMapId;
	}
	public void setPacketMapId(long packetMapId) {
		this.packetMapId = packetMapId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCommProtocol() {
		return commProtocol;
	}
	public void setCommProtocol(String commProtocol) {
		this.commProtocol = commProtocol;
	}
	public String getPacketType() {
		return packetType;
	}
	public void setPacketType(String packetType) {
		this.packetType = packetType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}