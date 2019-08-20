package com.elitecore.netvertexsm.datamanager.gateway.gateway.data;

import java.util.Set;

public interface IGatewayLocationData {	
	public Long getLocationId();
	public void setLocationId(Long locationId);
	public String getLocationName();
	public void setLocationName(String locationName);
	public String getDecription();
	public void setDecription(String decription);
	public Set<GatewayData> getGatewaySet();
	public void setGatewaySet(Set<GatewayData> gatewaySet);
	public GatewayData getGatewayData();
	public void setGatewayData(GatewayData gatewayData);
}
