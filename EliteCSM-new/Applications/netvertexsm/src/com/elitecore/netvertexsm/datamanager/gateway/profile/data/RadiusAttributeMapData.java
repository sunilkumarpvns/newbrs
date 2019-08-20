package com.elitecore.netvertexsm.datamanager.gateway.profile.data;

import java.util.List;
import java.util.Set;

public class RadiusAttributeMapData {
	private long radiusAttributeId;
	private String radiusAttribute;
	private String policyKey;
	private String type;
	private long gatewayProfileId;
	
	private List<RadiusValueMapData> radiusValueMapList;
	private Set<RadiusValueMapData> radiusValueMapSet;
	
	public Set<RadiusValueMapData> getRadiusValueMapSet() {
		return radiusValueMapSet;
	}
	public void setRadiusValueMapSet(Set<RadiusValueMapData> radiusValueMapSet) {
		this.radiusValueMapSet = radiusValueMapSet;
	}
	public List<RadiusValueMapData> getRadiusValueMap() {
		return radiusValueMapList;
	}
	public void setRadiusValueMap(List<RadiusValueMapData> radiusValueMapList) {
		this.radiusValueMapList = radiusValueMapList;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getRadiusAttributeId() {
		return radiusAttributeId;
	}
	public void setRadiusAttributeId(long radiusAttributeId) {
		this.radiusAttributeId = radiusAttributeId;
	}
	public String getRadiusAttribute() {
		return radiusAttribute;
	}
	public void setRadiusAttribute(String radiusAttribute) {
		this.radiusAttribute = radiusAttribute;
	}
	public String getPolicyKey() {
		return policyKey;
	}
	public void setPolicyKey(String policyKey) {
		this.policyKey = policyKey;
	}
	public long getGatewayProfileId() {
		return gatewayProfileId;
	}
	public void setGatewayProfileId(long gatewayProfileId) {
		this.gatewayProfileId = gatewayProfileId;
	}
}
