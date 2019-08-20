package com.elitecore.netvertexsm.datamanager.gateway.profile.data;

import java.util.List;
import java.util.Set;

public class DiameterAttributeMapData {
	private long diameterAttributeId;
	private String diameterAttribute;
	private String policyKey;
	private String defaultValue;
	private String type;
	private long diameterPacketMapId;
	
	private List<DiameterValueMapData> diameterValueMapList;
	private Set<DiameterValueMapData> diameterValueMapSet;
	
	public long getDiameterAttributeId() {
		return diameterAttributeId;
	}
	public void setDiameterAttributeId(long diameterAttributeId) {
		this.diameterAttributeId = diameterAttributeId;
	}
	public String getDiameterAttribute() {
		return diameterAttribute;
	}
	public void setDiameterAttribute(String diameterAttribute) {
		this.diameterAttribute = diameterAttribute;
	}
	public List<DiameterValueMapData> getDiameterValueMapList() {
		return diameterValueMapList;
	}
	public void setDiameterValueMapList(List<DiameterValueMapData> diameterValueMapList) {
		this.diameterValueMapList = diameterValueMapList;
	}
	public Set<DiameterValueMapData> getDiameterValueMapSet() {
		return diameterValueMapSet;
	}
	public void setDiameterValueMapSet(Set<DiameterValueMapData> diameterValueMapSet) {
		this.diameterValueMapSet = diameterValueMapSet;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPolicyKey() {
		return policyKey;
	}
	public void setPolicyKey(String policyKey) {
		this.policyKey = policyKey;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public long getDiameterPacketMapId() {
		return diameterPacketMapId;
	}
	public void setDiameterPacketMapId(long diameterPacketMapId) {
		this.diameterPacketMapId = diameterPacketMapId;
	}
	
}
