package com.elitecore.netvertexsm.datamanager.gateway.profile.data;

import java.util.List;
import java.util.Set;

import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.web.core.base.BaseData;

public class GatewayProfileData extends BaseData{
	private Long profileId;
	private String profileName;
	private String gatewayType;
	private String commProtocolId;
	private Long vedorId;
	private String firmware;
	private Integer maxThroughput;
	private Integer bufferBW;
	private String description;
	private Integer maxIPCANSession; 
	private int supportedStandard;
	
	private GatewayData gatewayData;
	private Set<GatewayData> gatewaySet;
	
	
	private RadiusProfileData radiusProfileData;
	private DiameterProfileData diameterProfileData;
	private RadiusAttributeMapData radiusAttributeMapData;

	private Set<RadiusProfileData> radiusProfileSet;
	private Set<DiameterProfileData> diameterProfileSet;
	private Set<RadiusAttributeMapData> radiusAttributeMapSet;
	private List<RadiusAttributeMapData> radiusAttributeMapList;
	private Set<DiameterPacketMapData> diameterPacketMapSet;
	private List<DiameterAttributeMapData> diameterAttributeMapList;
	
	private VendorData vendorData;
	private String usageReportingTime;
	private String revalidationMode;
	
	public String getUsageReportingTime() {
		return usageReportingTime;
	}
	public void setUsageReportingTime(String usageReportingTime) {
		this.usageReportingTime = usageReportingTime;
	}
	public Set<DiameterPacketMapData> getDiameterPacketMapSet() {
		return diameterPacketMapSet;
	}
	public void setDiameterPacketMapSet(Set<DiameterPacketMapData> diameterPacketMapSet) {
		this.diameterPacketMapSet = diameterPacketMapSet;
	}
	public Integer getMaxIPCANSession() {
		return maxIPCANSession;
	}
	public void setMaxIPCANSession(Integer maxIPCANSession) {
		this.maxIPCANSession = maxIPCANSession;
	}
	public VendorData getVendorData() {
		return vendorData;
	}
	public void setVendorData(VendorData vendorData) {
		this.vendorData = vendorData;
	}
	public RadiusProfileData getRadiusProfileData() {
		return radiusProfileData;
	}
	public void setRadiusProfileData(RadiusProfileData radiusProfileData) {
		this.radiusProfileData = radiusProfileData;
	}
	public RadiusAttributeMapData getRadiusAttributeMapData() {
		return radiusAttributeMapData;
	}
	public void setRadiusAttributeMapData(RadiusAttributeMapData radiusAttributeMapData) {
		this.radiusAttributeMapData = radiusAttributeMapData;
	}
	public DiameterProfileData getDiameterProfileData() {
		return diameterProfileData;
	}
	public void setDiameterProfileData(DiameterProfileData diameterProfileData) {
		this.diameterProfileData = diameterProfileData;
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public String getGatewayType() {
		return gatewayType;
	}
	public void setGatewayType(String gatewayType) {
		this.gatewayType = gatewayType;
	}
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
	public Long getProfileId() {
		return profileId;
	}
	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}
	public String getCommProtocolId() {
		return commProtocolId;
	}
	public void setCommProtocolId(String commProtocolId) {
		this.commProtocolId = commProtocolId;
	}
	public Long getVedorId() {
		return vedorId;
	}
	public void setVedorId(Long vedorId) {
		this.vedorId = vedorId;
	}
	public String getFirmware() {
		return firmware;
	}
	public void setFirmware(String firmware) {
		this.firmware = firmware;
	}
	public Integer getMaxThroughput() {
		return maxThroughput;
	}
	public void setMaxThroughput(Integer maxThroughput) {
		this.maxThroughput = maxThroughput;
	}
	public Integer getBufferBW() {
		return bufferBW;
	}
	public void setBufferBW(Integer bufferBW) {
		this.bufferBW = bufferBW;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Set<RadiusProfileData> getRadiusProfileSet() {
		return radiusProfileSet;
	}
	public void setRadiusProfileSet(Set<RadiusProfileData> radiusProfileSet) {
		this.radiusProfileSet = radiusProfileSet;
	}
	public Set<DiameterProfileData> getDiameterProfileSet() {
		return diameterProfileSet;
	}
	public void setDiameterProfileSet(Set<DiameterProfileData> diameterProfileSet) {
		this.diameterProfileSet = diameterProfileSet;
	}
	public Set<RadiusAttributeMapData> getRadiusAttributeMapSet() {
		return radiusAttributeMapSet;
	}
	public void setRadiusAttributeMapSet(Set<RadiusAttributeMapData> radiusAttributeMapSet) {
		this.radiusAttributeMapSet = radiusAttributeMapSet;
	}
	public List<RadiusAttributeMapData> getRadiusAttributeMapList() {
		return radiusAttributeMapList;
	}
	public void setRadiusAttributeMapList(List<RadiusAttributeMapData> radiusAttributeMapList) {
		this.radiusAttributeMapList = radiusAttributeMapList;
	}
	public List<DiameterAttributeMapData> getDiameterAttributeMapList() {
		return diameterAttributeMapList;
	}
	public void setDiameterAttributeMapList(List<DiameterAttributeMapData> diameterAttributeMapList) {
		this.diameterAttributeMapList = diameterAttributeMapList;
	}
	public int getSupportedStandard() {
		return supportedStandard;
	}
	public void setSupportedStandard(int supportedStandard) {
		this.supportedStandard = supportedStandard;
	}
	public String getRevalidationMode() {
		return revalidationMode;
	}
	public void setRevalidationMode(String revalidationMode) {
		this.revalidationMode = revalidationMode;
	}	
}