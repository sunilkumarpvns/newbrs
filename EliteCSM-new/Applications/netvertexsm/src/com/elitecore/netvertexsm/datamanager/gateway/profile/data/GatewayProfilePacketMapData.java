package com.elitecore.netvertexsm.datamanager.gateway.profile.data;

import com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData;

public class GatewayProfilePacketMapData {
	private long gppmId;
	private long profileId;
	private Long packetMapId;
	private String condition;
	private int orderNumber;
	
	private PacketMappingData packetMappingData;
	
	
	public Long getPacketMapId() {
		return packetMapId;
	}
	public void setPacketMapId(Long packetMapId) {
		this.packetMapId = packetMapId;
	}
	public PacketMappingData getPacketMappingData() {
		return packetMappingData;
	}
	public void setPacketMappingData(PacketMappingData packetMappingData) {
		this.packetMappingData = packetMappingData;
	}
	public long getGppmId() {
		return gppmId;
	}
	public void setGppmId(long gppmId) {
		this.gppmId = gppmId;
	}
	public long getProfileId() {
		return profileId;
	}
	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public int getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}
}
