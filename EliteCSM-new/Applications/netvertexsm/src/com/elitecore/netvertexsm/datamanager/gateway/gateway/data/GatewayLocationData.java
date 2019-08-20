package com.elitecore.netvertexsm.datamanager.gateway.gateway.data;

import java.util.Set;

public class GatewayLocationData implements IGatewayLocationData {
	private Long locationId;
	private String locationName;
	private String decription;
	private Set<GatewayData> gatewaySet;
	private GatewayData gatewayData;
	
	public Set<GatewayData> getGatewaySet() {
		return gatewaySet;
	}
	public void setGatewaySet(Set<GatewayData> gatewaySet) {
		this.gatewaySet = gatewaySet;
	}
	public GatewayData getGatewayData() {
		return gatewayData;
	}
	public void setGatewayData(GatewayData gatewayData) {
		this.gatewayData = gatewayData;
	}
	public Long getLocationId() {
		return locationId;
	}
	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public String getDecription() {
		return decription;
	}
	public void setDecription(String decription) {
		this.decription = decription;
	}
}
