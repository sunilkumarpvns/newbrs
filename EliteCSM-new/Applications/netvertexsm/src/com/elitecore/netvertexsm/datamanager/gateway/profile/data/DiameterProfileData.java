package com.elitecore.netvertexsm.datamanager.gateway.profile.data;

import java.util.List;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;

public class DiameterProfileData {
	private long profileId;
	private Long timeout;
	private String tlsEnable;
	private Long retransmissionCnt;
	private String initConnection;
	private String isCustomGxAppId;
	private String isCustomGyAppId;
	private String isCustomRxAppId;
	private String isCustomS9AppId;
	private String isCustomSyAppId;
	private String gxApplicationId;
	private String gyApplicationId;
	private String rxApplicationId;
	private String s9ApplicationId;
	private String syApplicationId;
	private Integer dwInterval;
	private String isDWGatewayLevel;
	private Integer pccProvision;
	private String supportedVendorList;
	private Integer supportedStandard;
	private String multiChargingRuleEnabled;
	private List<GatewayProfilePacketMapData> gwProfilePacketMapList;
	private List<PCCRuleMappingData> pccRuleMappingList;
	private String sessionCleanUpCER;
	private String sessionCleanUpDPR;
	private String cerAvps;
	private String dprAvps;
	private String transportProtocol;
	private Integer socketReceiveBufferSize;
	private String sendDPRCloseEvent;
	private Integer socketSendBufferSize;
	private String tcpNagleAlgorithm;
	//private Long dwrDuration;
	private Long initConnectionDuration;
	private String dwrAvps;
	private List<GroovyScriptData> groovyScriptsList;
	private String umStandard;
	private String sessionLookUpKey = PCRFKeyConstants.CS_SESSION_IPV4.val+','+PCRFKeyConstants.CS_SESSION_IPV6.val;
	
	private List<GatewayProfileRuleMappingData> gatewayProfileRuleMappingList;
	
	public List<GroovyScriptData> getGroovyScriptsList() {
		return groovyScriptsList;
	}
	public void setGroovyScriptsList(List<GroovyScriptData> groovyScriptsList) {
		this.groovyScriptsList = groovyScriptsList;
	}
	public String getIsCustomS9AppId() {
		return isCustomS9AppId;
	}
	public void setIsCustomS9AppId(String isCustomS9AppId) {
		this.isCustomS9AppId = isCustomS9AppId;
	}
	public String getS9ApplicationId() {
		return s9ApplicationId;
	}
	public void setS9ApplicationId(String s9ApplicationId) {
		this.s9ApplicationId = s9ApplicationId;
	}		
	public String getIsCustomSyAppId() {
		return isCustomSyAppId;
	}
	public void setIsCustomSyAppId(String isCustomSyAppId) {
		this.isCustomSyAppId = isCustomSyAppId;
	}
	public String getSyApplicationId() {
		return syApplicationId;
	}
	public void setSyApplicationId(String syApplicationId) {
		this.syApplicationId = syApplicationId;
	}
	public String getDwrAvps() {
		return dwrAvps;
	}
	public void setDwrAvps(String dwrAvps) {
		this.dwrAvps = dwrAvps;
	}
	public Long getInitConnectionDuration() {
		return initConnectionDuration;
	}
	public void setInitConnectionDuration(Long initConnectionDuration) {
		this.initConnectionDuration = initConnectionDuration;
	}
	public String getTcpNagleAlgorithm() {
		return tcpNagleAlgorithm;
	}
	public void setTcpNagleAlgorithm(String tcpNagleAlgorithm) {
		this.tcpNagleAlgorithm = tcpNagleAlgorithm;
	}
	public Integer getSocketSendBufferSize() {
		return socketSendBufferSize;
	}
	public void setSocketSendBufferSize(Integer socketSendBufferSize) {
		this.socketSendBufferSize = socketSendBufferSize;
	}
	public String getSendDPRCloseEvent() {
		return sendDPRCloseEvent;
	}
	public void setSendDPRCloseEvent(String sendDPRCloseEvent) {
		this.sendDPRCloseEvent = sendDPRCloseEvent;
	}
	public String getTransportProtocol() {
		return transportProtocol;
	}
	public void setTransportProtocol(String transportProtocol) {
		this.transportProtocol = transportProtocol;
	}
	public Integer getSocketReceiveBufferSize() {
		return socketReceiveBufferSize;
	}
	public void setSocketReceiveBufferSize(Integer socketReceiveBufferSize) {
		this.socketReceiveBufferSize = socketReceiveBufferSize;
	}
	public String getCerAvps() {
		return cerAvps;
	}
	public void setCerAvps(String cerAvps) {
		this.cerAvps = cerAvps;
	}
	public String getDprAvps() {
		return dprAvps;
	}
	public void setDprAvps(String dprAvps) {
		this.dprAvps = dprAvps;
	}
	public String getSessionCleanUpCER() {
		return sessionCleanUpCER;
	}
	public void setSessionCleanUpCER(String sessionCleanUpCER) {
		this.sessionCleanUpCER = sessionCleanUpCER;
	}
	public String getSessionCleanUpDPR() {
		return sessionCleanUpDPR;
	}
	public void setSessionCleanUpDPR(String sessionCleanUpDPR) {
		this.sessionCleanUpDPR = sessionCleanUpDPR;
	}
	public String getMultiChargingRuleEnabled() {
		return multiChargingRuleEnabled;
	}
	public void setMultiChargingRuleEnabled(String multiChargingRuleEnabled) {
		this.multiChargingRuleEnabled = multiChargingRuleEnabled;
	}
	public List<PCCRuleMappingData> getPccRuleMappingList() {
		return pccRuleMappingList;
	}
	public void setPccRuleMappingList(List<PCCRuleMappingData> pccRuleMappingList) {
		this.pccRuleMappingList = pccRuleMappingList;
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
	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}
	public Long getTimeout() {
		return timeout;
	}
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
	public String getTlsEnable() {
		return tlsEnable;
	}
	public void setTlsEnable(String tlsEnable) {
		this.tlsEnable = tlsEnable;
	}
	public Long getRetransmissionCnt() {
		return retransmissionCnt;
	}
	public void setRetransmissionCnt(Long retransmissionCnt) {
		this.retransmissionCnt = retransmissionCnt;
	}
	public String getInitConnection() {
		return initConnection;
	}
	public void setInitConnection(String initConnection) {
		this.initConnection = initConnection;
	}
	public String getIsCustomGxAppId() {
		return isCustomGxAppId;
	}
	public void setIsCustomGxAppId(String isCustomGxAppId) {
		this.isCustomGxAppId = isCustomGxAppId;
	}
	public String getIsCustomGyAppId() {
		return isCustomGyAppId;
	}
	public void setIsCustomGyAppId(String isCustomGyAppId) {
		this.isCustomGyAppId = isCustomGyAppId;
	}
	public String getIsCustomRxAppId() {
		return isCustomRxAppId;
	}
	public void setIsCustomRxAppId(String isCustomRxAppId) {
		this.isCustomRxAppId = isCustomRxAppId;
	}
	public String getGxApplicationId() {
		return gxApplicationId;
	}
	public void setGxApplicationId(String gxApplicationId) {
		this.gxApplicationId = gxApplicationId;
	}
	public String getGyApplicationId() {
		return gyApplicationId;
	}
	public void setGyApplicationId(String gyApplicationId) {
		this.gyApplicationId = gyApplicationId;
	}
	public String getRxApplicationId() {
		return rxApplicationId;
	}
	public void setRxApplicationId(String rxApplicationId) {
		this.rxApplicationId = rxApplicationId;
	}
	public Integer getDwInterval() {
		return dwInterval;
	}
	public void setDwInterval(Integer dwInterval) {
		this.dwInterval = dwInterval;
	}
	public String getIsDWGatewayLevel() {
		return isDWGatewayLevel;
	}
	public void setIsDWGatewayLevel(String isDWGatewayLevel) {
		this.isDWGatewayLevel = isDWGatewayLevel;
	}
	public Integer getPccProvision() {
		return pccProvision;
	}
	public void setPccProvision(Integer pccProvision) {
		this.pccProvision = pccProvision;
	}
	public String getSupportedVendorList() {
		return supportedVendorList;
	}
	public void setSupportedVendorList(String supportedVendorList) {
		this.supportedVendorList = supportedVendorList;
	}
	public Integer getSupportedStandard() {
		return supportedStandard;
	}
	public void setSupportedStandard(Integer supportedStandard) {
		this.supportedStandard = supportedStandard;
	}
	public String getUmStandard() {
		return umStandard;
	}
	public void setUmStandard(String umStandard) {
		if(umStandard==null || umStandard.trim().length()==0){
			this.umStandard = "3GPPR9";
		}else{
			this.umStandard = umStandard;
		}
	}
	public List<GatewayProfileRuleMappingData> getGatewayProfileRuleMappingList() {
		return gatewayProfileRuleMappingList;
	}
	public void setGatewayProfileRuleMappingList(
			List<GatewayProfileRuleMappingData> gatewayProfileRuleMappingList) {
		this.gatewayProfileRuleMappingList = gatewayProfileRuleMappingList;
	}
	public String getSessionLookUpKey() {
		return sessionLookUpKey;
	}
	public void setSessionLookUpKey(String sessionLookUpKey) {
		this.sessionLookUpKey = sessionLookUpKey;
	}
	
	
}