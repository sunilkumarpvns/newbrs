package com.elitecore.netvertexsm.web.gateway.profile.form;

import java.util.List;

import org.apache.struts.action.ActionForm;

import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping.data.RuleMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.DiameterAttributeMapData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileRuleMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GroovyScriptData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.RadiusAttributeMapData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.VendorData;

public class EditGatewayProfileForm extends ActionForm {
	private long profileId;
	private String commProtocolId;
	private long vendorId;
	private String firmware;
	private int maxThroughtput;
	private int bufferBW;
	private String description;
	private String gatewayProfileName;
	private String gatewayType;
	private Integer maxIPCANSession;
	private Integer orderNumber;
	private int supportedStandard;
	private String action;
	private String umStandard;
	private List<VendorData> vendorList;
	private List<DatabaseDSData> dataSourceList;
	
	
//	Radius & Diameter Properties
	private Long maxRequestTimeout;
	private Long statusCheckDuration;
	private Long retryCount;
	private String icmpPingEnabled;
	private Long timeout;
	private String tlsEnable;
	private Long retransmissionCnt;
	private String initConnection;
	private Boolean isCustomGxAppId=false;
	private Boolean isCustomRxAppId=false;
	private String gxApplicationId;
	private String rxApplicationId;
	private String gyApplicationId;
	private Boolean isCustomGyAppId=false;
	private String s9ApplicationId;
	private String syApplicationId;
	private Boolean isCustomS9AppId = false;
	private Boolean isCustomSyAppId = false;
	private int dwrInterval;
	private Boolean isDWRGatewayLevel=false;
	private Integer pccProvision;
	private String diameterSupportedVendorList;
	private String radiusSupportedVendorList;
	private List<RadiusAttributeMapData> radiusAttributeMapList;
	private List<DiameterAttributeMapData>diameterAttributeMapList;
	private String sendAccountingResponse;
	private String multiChargingRuleEnabled;
	
	private List<GatewayProfilePacketMapData> profilePacketMapList;
	private List<GatewayProfilePacketMapData> profilePacketDPMapList;
	private List<GatewayProfilePacketMapData> profilePacketPDMapList;
	private List<PacketMappingData> packetMappingList;
	private List<PCCRuleMappingData> pccRuleMapList;
	private List<PacketMappingData> pcrfToDiameterPacketMappingList;
	private List<PacketMappingData> diameterToPCRFPacketMappingList;
	
	private List<GatewayProfilePacketMapData> profilePacketRPMapList;
	private List<GatewayProfilePacketMapData> profilePacketPRMapList;
	private List<PacketMappingData> pcrfToRadiusPacketMappingList;
	private List<PacketMappingData> radiusToPCRFPacketMappingList;	
	private Boolean sessionCleanUpCER = false;
    private Boolean sessionCleanUpDPR = false;
	private String cerAvps;
	private String dprAvps;
	private String transportProtocol;
	private Integer socketReceiveBufferSize;
	private String sendDPRCloseEvent;
	private String usageReportingTime;
	private Integer socketSendBufferSize;
	private String tcpNagleAlgorithm;
	private Long dwrDuration;
	private Long initConnectionDuration;
	private String dwrAvps;
	private String revalidationMode;
	private List<GroovyScriptData> groovyScriptsList;
	private List<RuleMappingData> ruleMappingList;
	private List<GatewayProfileRuleMappingData> gatewayProfileRuleMappingList;
	private Integer interimInterval;
	
	//Session lookup
	private String sessionLookupKey;
		
	
	public List<GroovyScriptData> getGroovyScriptsList() {
		return groovyScriptsList;
	}
	public void setGroovyScriptsList(List<GroovyScriptData> groovyScriptsList) {
		this.groovyScriptsList = groovyScriptsList;
	}
	public String getS9ApplicationId() {
		return s9ApplicationId;
	}
	public void setS9ApplicationId(String s9ApplicationId) {
		this.s9ApplicationId = s9ApplicationId;
	}
	public Boolean getIsCustomS9AppId() {
		return isCustomS9AppId;
	}
	public void setIsCustomS9AppId(Boolean isCustomS9AppId) {
		this.isCustomS9AppId = isCustomS9AppId;
	}
	public Integer getSocketSendBufferSize() {
		return socketSendBufferSize;
	}
	public void setSocketSendBufferSize(Integer socketSendBufferSize) {
		this.socketSendBufferSize = socketSendBufferSize;
	}
	public String getTcpNagleAlgorithm() {
		return tcpNagleAlgorithm;
	}
	public void setTcpNagleAlgorithm(String tcpNagleAlgorithm) {
		this.tcpNagleAlgorithm = tcpNagleAlgorithm;
	}
	public Long getDwrDuration() {
		return dwrDuration;
	}
	public void setDwrDuration(Long dwrDuration) {
		this.dwrDuration = dwrDuration;
	}
	public Long getInitConnectionDuration() {
		return initConnectionDuration;
	}
	public void setInitConnectionDuration(Long initConnectionDuration) {
		this.initConnectionDuration = initConnectionDuration;
	}
	public String getDwrAvps() {
		return dwrAvps;
	}
	public void setDwrAvps(String dwrAvps) {
		this.dwrAvps = dwrAvps;
	}
	public Boolean getSessionCleanUpCER() {
		return sessionCleanUpCER;
	}
	public void setSessionCleanUpCER(Boolean sessionCleanUpCER) {
		this.sessionCleanUpCER = sessionCleanUpCER;
	}
	public Boolean getSessionCleanUpDPR() {
		return sessionCleanUpDPR;
	}
	public void setSessionCleanUpDPR(Boolean sessionCleanUpDPR) {
		this.sessionCleanUpDPR = sessionCleanUpDPR;
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
	public String getSendDPRCloseEvent() {
		return sendDPRCloseEvent;
	}
	public void setSendDPRCloseEvent(String sendDPRCloseEvent) {
		this.sendDPRCloseEvent = sendDPRCloseEvent;
	}
	public void setPccProvision(Integer pccProvision) {
		this.pccProvision = pccProvision;
	}
	public String getMultiChargingRuleEnabled() {
		return multiChargingRuleEnabled;
	}
	public void setMultiChargingRuleEnabled(String multiChargingRuleEnabled) {
		this.multiChargingRuleEnabled = multiChargingRuleEnabled;
	}
	public String getSendAccountingResponse() {
		return sendAccountingResponse;
	}
	public void setSendAccountingResponse(String sendAccountingResponse) {
		this.sendAccountingResponse = sendAccountingResponse;
	}
	public String getUsageReportingTime() {
		return usageReportingTime;
	}
	public void setUsageReportingTime(String usageReportingTime) {
		this.usageReportingTime = usageReportingTime;
	}
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	public List<PCCRuleMappingData> getPccRuleMapList() {
		return pccRuleMapList;
	}
	public void setPccRuleMapList(List<PCCRuleMappingData> pccRuleMapList) {
		this.pccRuleMapList = pccRuleMapList;
	}
	public List<PacketMappingData> getPacketMappingList() {
		return packetMappingList;
	}
	public void setPacketMappingList(List<PacketMappingData> packetMappingList) {
		this.packetMappingList = packetMappingList;
	}
	public List<GatewayProfilePacketMapData> getProfilePacketMapList() {
		return profilePacketMapList;
	}
	public void setProfilePacketMapList(
			List<GatewayProfilePacketMapData> profilePacketMapList) {
		this.profilePacketMapList = profilePacketMapList;
	}
	public Integer getMaxIPCANSession() {
		return maxIPCANSession;
	}
	public void setMaxIPCANSession(Integer maxIPCANSession) {
		this.maxIPCANSession = maxIPCANSession;
	}
	public long getProfileId() {
		return profileId;
	}
	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}
	public String getCommProtocolId() {
		return commProtocolId;
	}
	public void setCommProtocolId(String commProtocolId) {
		this.commProtocolId = commProtocolId;
	}
	public long getVendorId() {
		return vendorId;
	}
	public void setVendorId(long vendorId) {
		this.vendorId = vendorId;
	}
	public String getFirmware() {
		return firmware;
	}
	public void setFirmware(String firmware) {
		this.firmware = firmware;
	}
	public int getMaxThroughtput() {
		return maxThroughtput;
	}
	public void setMaxThroughtput(int maxThroughtput) {
		this.maxThroughtput = maxThroughtput;
	}
	public int getBufferBW() {
		return bufferBW;
	}
	public void setBufferBW(int bufferBw) {
		this.bufferBW = bufferBw;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getGatewayProfileName() {
		return gatewayProfileName;
	}
	public void setGatewayProfileName(String gatewayProfileName){
		this.gatewayProfileName = gatewayProfileName;
	}
	public String getGatewayType() {
		return gatewayType;
	}
	public void setGatewayType(String gatewayType) {
		this.gatewayType = gatewayType;
	}
	public List<VendorData> getVendorList(){
		return vendorList;
	}
	public void setVendorList(List<VendorData> vendorList) {
		this.vendorList = vendorList;
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
	public Boolean getIsCustomGxAppId() {
		return isCustomGxAppId;
	}
	public void setIsCustomGxAppId(Boolean isCustomGxAppId) {
		this.isCustomGxAppId = isCustomGxAppId;
	}
	public Boolean getIsCustomRxAppId() {
		return isCustomRxAppId;
	}
	public void setIsCustomRxAppId(Boolean isCustomRxAppId) {
		this.isCustomRxAppId = isCustomRxAppId;
	}
	public String getGxApplicationId() {
		return gxApplicationId;
	}
	public void setGxApplicationId(String gxApplicationId) {
		this.gxApplicationId = gxApplicationId;
	}
	public String getRxApplicationId() {
		return rxApplicationId;
	}
	public void setRxApplicationId(String rxApplicationId) {
		this.rxApplicationId = rxApplicationId;
	}
	public String getGyApplicationId() {
		return gyApplicationId;
	}
	public void setGyApplicationId(String gyApplicationId) {
		this.gyApplicationId = gyApplicationId;
	}
	public Boolean getIsCustomGyAppId() {
		return isCustomGyAppId;
	}
	public void setIsCustomGyAppId(Boolean isCustomGyAppId) {
		this.isCustomGyAppId = isCustomGyAppId;
	}
	public int getDwrInterval() {
		return dwrInterval;
	}
	public void setDwrInterval(int dwrInterval) {
		this.dwrInterval = dwrInterval;
	}
	public Boolean getIsDWRGatewayLevel() {
		return isDWRGatewayLevel;
	}
	public void setIsDWRGatewayLevel(Boolean isDWRGatewayLevel) {
		this.isDWRGatewayLevel = isDWRGatewayLevel;
	}
	public int getPccProvision() {
		return pccProvision;
	}
	public void setPccProvision(int pccProvision) {
		this.pccProvision = pccProvision;
	}
	public void setDiameterSupportedVendorList(
			String diameterSupportedVendorList) {
		this.diameterSupportedVendorList = diameterSupportedVendorList;
	}
	public String getDiameterSupportedVendorList() {
		return diameterSupportedVendorList;
	}
	public void setRadiusSupportedVendorList(String radiusSupportedVendorList) {
		this.radiusSupportedVendorList = radiusSupportedVendorList;
	}
	public String getRadiusSupportedVendorList() {
		return radiusSupportedVendorList;
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
	public void setDataSourceList(List<DatabaseDSData> dataSourceList) {
		this.dataSourceList = dataSourceList;
	}
	public List<DatabaseDSData> getDataSourceList() {
		return dataSourceList;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getSupportedStandard() {
		return supportedStandard;
	}
	public void setSupportedStandard(int supportedStandard) {
		this.supportedStandard = supportedStandard;
	}
	public List<PacketMappingData> getPcrfToDiameterPacketMappingList() {
		return pcrfToDiameterPacketMappingList;
	}
	public void setPcrfToDiameterPacketMappingList(
			List<PacketMappingData> pcrfToDiameterPacketMappingList) {
		this.pcrfToDiameterPacketMappingList = pcrfToDiameterPacketMappingList;
	}
	public List<PacketMappingData> getDiameterToPCRFPacketMappingList() {
		return diameterToPCRFPacketMappingList;
	}
	public void setDiameterToPCRFPacketMappingList(
			List<PacketMappingData> diameterToPCRFPacketMappingList) {
		this.diameterToPCRFPacketMappingList = diameterToPCRFPacketMappingList;
	}
	public List<GatewayProfilePacketMapData> getProfilePacketDPMapList() {
		return profilePacketDPMapList;
	}
	public void setProfilePacketDPMapList(
			List<GatewayProfilePacketMapData> profilePacketDPMapList) {
		this.profilePacketDPMapList = profilePacketDPMapList;
	}
	public List<GatewayProfilePacketMapData> getProfilePacketPDMapList() {
		return profilePacketPDMapList;
	}
	public void setProfilePacketPDMapList(List<GatewayProfilePacketMapData> profilePacketPDMapList) {
		this.profilePacketPDMapList = profilePacketPDMapList;
	}
	public List<GatewayProfilePacketMapData> getProfilePacketRPMapList() {
		return profilePacketRPMapList;
	}
	public void setProfilePacketRPMapList(
			List<GatewayProfilePacketMapData> profilePacketRPMapList) {
		this.profilePacketRPMapList = profilePacketRPMapList;
	}
	public List<GatewayProfilePacketMapData> getProfilePacketPRMapList() {
		return profilePacketPRMapList;
	}
	public void setProfilePacketPRMapList(
			List<GatewayProfilePacketMapData> profilePacketPRMapList) {
		this.profilePacketPRMapList = profilePacketPRMapList;
	}
	public List<PacketMappingData> getPcrfToRadiusPacketMappingList() {
		return pcrfToRadiusPacketMappingList;
	}
	public void setPcrfToRadiusPacketMappingList(
			List<PacketMappingData> pcrfToRadiusPacketMappingList) {
		this.pcrfToRadiusPacketMappingList = pcrfToRadiusPacketMappingList;
	}
	public List<PacketMappingData> getRadiusToPCRFPacketMappingList() {
		return radiusToPCRFPacketMappingList;
	}
	public void setRadiusToPCRFPacketMappingList(
			List<PacketMappingData> radiusToPCRFPacketMappingList) {
		this.radiusToPCRFPacketMappingList = radiusToPCRFPacketMappingList;
	}
	public String getRevalidationMode() {
		return revalidationMode;
	}
	public void setRevalidationMode(String revalidationMode) {
		this.revalidationMode = revalidationMode;
	}
	public String getUmStandard() {
		return umStandard;
	}
	public void setUmStandard(String umStandard) {
		this.umStandard = umStandard;
	}
	public String getSyApplicationId() {
		return syApplicationId;
	}
	public void setSyApplicationId(String syApplicationId) {
		this.syApplicationId = syApplicationId;
	}
	public Boolean getIsCustomSyAppId() {
		return isCustomSyAppId;
	}
	public void setIsCustomSyAppId(Boolean isCustomSyAppId) {
		this.isCustomSyAppId = isCustomSyAppId;
	}
	public List<RuleMappingData> getRuleMappingList() {
		return ruleMappingList;
	}
	public void setRuleMappingList(List<RuleMappingData> ruleMappingList) {
		this.ruleMappingList = ruleMappingList;
	}
	public List<GatewayProfileRuleMappingData> getGatewayProfileRuleMappingList() {
		return gatewayProfileRuleMappingList;
	}
	public void setGatewayProfileRuleMappingList(
			List<GatewayProfileRuleMappingData> gatewayProfileRuleMappingList) {
		this.gatewayProfileRuleMappingList = gatewayProfileRuleMappingList;
	}
	public String getSessionLookupKey() {
		return sessionLookupKey;
	}
	public void setSessionLookupKey(String sessionLookupKey) {
		this.sessionLookupKey = sessionLookupKey;
	}

	public Integer getInterimInterval() {
		return interimInterval;
	}

	public void setInterimInterval(Integer interimInterval) {
		this.interimInterval = interimInterval;
	}
}