package com.elitecore.netvertexsm.datamanager.gateway.profile.data;

import java.util.List;

public class RadiusProfileData {
	private long profileId;
	private Long timeout;
	private Long maxRequestTimeout;
	private Long statusCheckDuration;
	private Long retryCount;
	private String icmpPingEnabled;
	private String supportedVendorList;
	private String sendAccountingResponse;
	private List<GatewayProfilePacketMapData> gwProfilePacketMapList;
	private List<PCCRuleMappingData> pccRuleMappingDataList;
	private List<GroovyScriptData> groovyScriptsList;
	private GatewayProfileData gatewayProfileData;
	private List<GatewayProfileRuleMappingData> gatewayProfileRuleMappingList;
	private Integer interimInterval;
	
	public Long getTimeout() {
		return timeout;
	}
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
	public Long getMaxRequestTimeout() {
		return maxRequestTimeout;
	}
	public void setMaxRequestTimeout(Long maxRequestTimeout) {
		this.maxRequestTimeout = maxRequestTimeout;
	}
	public Long getStatusCheckDuration() {
		return statusCheckDuration;
	}
	public void setStatusCheckDuration(Long statusCheckDuration) {
		this.statusCheckDuration = statusCheckDuration;
	}
	public Long getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(Long retryCount) {
		this.retryCount = retryCount;
	}
	public String getIcmpPingEnabled() {
		return icmpPingEnabled;
	}
	public void setIcmpPingEnabled(String icmpPingEnabled) {
		this.icmpPingEnabled = icmpPingEnabled;
	}
	public List<PCCRuleMappingData> getPccRuleMappingDataList() {
		return pccRuleMappingDataList;
	}
	public void setPccRuleMappingDataList(
			List<PCCRuleMappingData> pccRuleMappingDataList) {
		this.pccRuleMappingDataList = pccRuleMappingDataList;
	}
	public String getSendAccountingResponse() {
		return sendAccountingResponse;
	}
	public void setSendAccountingResponse(String sendAccountingResponse) {
		this.sendAccountingResponse = sendAccountingResponse;
	}
	public List<GatewayProfilePacketMapData> getGwProfilePacketMapList() {
		return gwProfilePacketMapList;
	}
	public void setGwProfilePacketMapList(
			List<GatewayProfilePacketMapData> gwProfilePacketMapList) {
		this.gwProfilePacketMapList = gwProfilePacketMapList;
	}
	public long getProfileId() {
		return profileId;
	}
	public List<GroovyScriptData> getGroovyScriptsList() {
		return groovyScriptsList;
	}
	public void setGroovyScriptsList(List<GroovyScriptData> groovyScriptsList) {
		this.groovyScriptsList = groovyScriptsList;
	}
	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}
	public String getSupportedVendorList() {
		return supportedVendorList;
	}
	public void setSupportedVendorList(String supportedVendorList) {
		this.supportedVendorList = supportedVendorList;
	}
	public GatewayProfileData getGatewayProfileData() {
		return gatewayProfileData;
	}
	public void setGatewayProfileData(GatewayProfileData gatewayProfileData) {
		this.gatewayProfileData = gatewayProfileData;
	}
	public void setGatewayProfileRuleMappingList(
			List<GatewayProfileRuleMappingData> profileRuleMappingList) {
		this.gatewayProfileRuleMappingList=profileRuleMappingList;
		
	}
	public List<GatewayProfileRuleMappingData> getGatewayProfileRuleMappingList() {
		return gatewayProfileRuleMappingList;
	}

	public Integer getInterimInterval() {
		return interimInterval;
	}

	public void setInterimInterval(Integer interimInterval) {
		this.interimInterval = interimInterval;
	}
}
