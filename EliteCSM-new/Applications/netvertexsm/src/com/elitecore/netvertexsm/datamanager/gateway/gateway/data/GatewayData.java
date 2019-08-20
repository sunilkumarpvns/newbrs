package com.elitecore.netvertexsm.datamanager.gateway.gateway.data;

import java.util.Set;

import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;
import com.elitecore.netvertexsm.web.core.base.BaseData;

public class GatewayData extends BaseData implements IGatewayData {
	private long gatewayId;
	private long profileId;
	
	private String commProtocol;
	private String policyEnforcementMethodName;	
	private String description;
	private Integer  locationId;
	private String areaName;
	private String connectionUrl;
	private String gatewayName;
		
	private Set<DiameterGatewayData> diameterGatewayDataSet;
	private Set<RadiusGatewayData> radiusGatewayDataSet; 

//	Radius Properties	
	private String sharedSecret;
	private Integer timeout;
	private Integer maxRequestTimeout;
	
//	Diameter Properties
	private String hostId;
	private String realm;
	//private boolean tlsEnable;
	private String tlsEnable;
	private Integer retransmissionCnt;

//	Page list related properties	
	private String status;
	private long totalPages;
	private long totalRecords;
	
	private RadiusGatewayData radiusGatewayData;
	private DiameterGatewayData diameterGatewayData;
	
	private GatewayProfileData gatewayProfileData;
	private GatewayLocationData gatewayLocationData;
	
	private Set<GatewayData> gatewaySet;
	
	public String getPolicyEnforcementMethodName() {
		return policyEnforcementMethodName;
	}
	public void setPolicyEnforcementMethodName(String policyEnforcementMethodName) {
		this.policyEnforcementMethodName = policyEnforcementMethodName;
	}
	
	public Set<DiameterGatewayData> getDiameterGatewayDataSet() {
		return diameterGatewayDataSet;
	}
	public void setDiameterGatewayDataSet(
			Set<DiameterGatewayData> diameterGatewayDataSet) {
		this.diameterGatewayDataSet = diameterGatewayDataSet;
	}
	public Set<RadiusGatewayData> getRadiusGatewayDataSet() {
		return radiusGatewayDataSet;
	}
	public void setRadiusGatewayDataSet(Set<RadiusGatewayData> radiusGatewayDataSet) {
		this.radiusGatewayDataSet = radiusGatewayDataSet;
	}
	
	public RadiusGatewayData getRadiusGatewayData() {
		return radiusGatewayData;
	}
	public void setRadiusGatewayData(RadiusGatewayData radiusGatewayData) {
		this.radiusGatewayData = radiusGatewayData;
	}
	public DiameterGatewayData getDiameterGatewayData() {
		return diameterGatewayData;
	}
	public void setDiameterGatewayData(DiameterGatewayData diameterGatewayData) {
		this.diameterGatewayData = diameterGatewayData;
	}
	public GatewayProfileData getGatewayProfileData() {
		return gatewayProfileData;
	}
	public void setGatewayProfileData(GatewayProfileData gatewayProfileData) {
		this.gatewayProfileData = gatewayProfileData;
	}
	public GatewayLocationData getGatewayLocationData() {
		return gatewayLocationData;
	}
	public void setGatewayLocationData(GatewayLocationData gatewayLocationData) {
		this.gatewayLocationData = gatewayLocationData;
	}
	
	public Integer getMaxRequestTimeout() {
		return maxRequestTimeout;
	}
	public void setMaxRequestTimeout(Integer maxRequestTimeout) {
		this.maxRequestTimeout = maxRequestTimeout;
	}
	public Integer getTimeout() {
		return timeout;
	}
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	public Integer getRetransmissionCnt() {
		return retransmissionCnt;
	}
	public void setRetransmissionCnt(Integer retransmissionCnt) {
		this.retransmissionCnt = retransmissionCnt;
	}
	public String getSharedSecret() {
		return sharedSecret;
	}
	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}
	public String getHostId() {
		return hostId;
	}
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
	public String getRealm() {
		return realm;
	}
	public void setRealm(String realm) {
		this.realm = realm;
	}
	public String getTlsEnable() {
		return tlsEnable;
	}
	public void setTlsEnable(String tlsEnable) {
		this.tlsEnable = tlsEnable;
	}
	public long getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(long gatewayId) {
		this.gatewayId = gatewayId;
	}
	public long getProfileId() {
		return profileId;
	}
	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getLocationId() {
		return locationId;
	}
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public String getConnectionUrl() {
		return connectionUrl;
	}
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	public String getCommProtocol() {
		return this.commProtocol;
	}
	public void setCommProtocol(String commProtocol) {
		this.commProtocol = commProtocol;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
	}
	public long getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}
	public String getGatewayName() {
		return gatewayName;
	}
	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}
	@Override
	public Set<GatewayData> getGatewaySet() {
		return this.gatewaySet;
	}
	@Override
	public void setGatewaySet(Set<GatewayData> gatewaySet) {
		this.gatewaySet = gatewaySet;
	}
 
}