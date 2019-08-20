package com.elitecore.netvertexsm.web.gateway.profile.form;

import java.util.List;

import org.apache.struts.action.ActionForm;

import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping.data.RuleMappingData;

public class CreateProfileForm extends ActionForm {

	private static final long serialVersionUID = 1L;
	
	private String gatewayProfileName;
	private String gatewayType;
	private long vendorId;
	private String commProtocol;
	private String description;
	private String firmware;
	private int maxThroughtput;
	private int bufferBandwidth;
	private String status;
	private List vendorList;
	private Integer maxIPCANSession;
	private String multiChargingRuleEnabled;
	
  
	
	//	Radius & Diameter Properties
	private Long maxRequestTimeout;
	private Long statusCheckDuration;
	private Long retryCount;
	private String icmpPingEnabled;
	private Long timeout;
	private String tlsEnable;
	private Long retransmissionCnt;
	private String initConnection;
	private Boolean isCustomGxAppId = false;
	private Boolean isCustomRxAppId = false;
	private Boolean isCustomS9AppId = false;
	private Boolean isCustomSyAppId = false;
	private String gxApplicationId;
	private String rxApplicationId;
	private String gyApplicationId;
	private String s9ApplicationId;
	private String syApplicationId;
	private Boolean isCustomGyAppId = false;
	private int dwrInterval;
	private Boolean isDWRGatewayLevel = false;
	private int pccProvision;
	private String supportedVendorList;
	private int supportedStandard;
	private String sendAccountingResponse;
	private Integer interimInterval;
	
// Session Manager Properties
	private List<DatabaseDSData> datasourceList;
	private Long datasourceId;
	private String tableName = "TBLMUSERSESSIONS";
	private String sessionIdFieldName = "ACCTSESSIONID";
	private String policyKey = "ACCT-SESSION-ID";
	private String defaultValue;
	private String dataType;
	private String dbFieldName;
	private String identityFieldName = "CONCUSERID";
	private String startTimeField = "STARTTIME";
	private String lastUpdatedTimeField = "LASTUPDATEDTIME";
	private String searchAttributeField = "USERNAME";
	
//	Mapping Properties	
	private String condition;
	private List<PacketMappingData> packetMappingList;
	private List<PacketMappingData> pcrfToDiameterPacketMappingList;
	private List<PacketMappingData> diameterToPCRFPacketMappingList;
	private List<PacketMappingData> pcrfToRadiusPacketMappingList;
	private List<PacketMappingData> radiusToPCRFPacketMappingList;
	private String usageReportingTime;
	private Boolean sessionCleanUpCER = false;
    private Boolean sessionCleanUpDPR = false;
	private String cerAvps;
	private String dprAvps;
	private String transportProtocol;
	private Integer socketReceiveBufferSize;
	private String sendDPRCloseEvent;
	private Integer socketSendBufferSize;
	private String tcpNagleAlgorithm;
	private Long dwrDuration;
	private Long initConnectionDuration;
	private String dwrAvps;
	private String revalidationMode;
	private String umStandard;
    
	private String accessNetworkType;
	
	//Session lookup
	private String sessionLookupKey;
	
	private List<RuleMappingData> ruleMappingList;
	
	public String getAccessNetworkType() {
		return accessNetworkType;
	}
	public void setAccessNetworkType(String accessNetworkType) {
		this.accessNetworkType = accessNetworkType;
	}
	public Boolean getIsCustomS9AppId() {
		return isCustomS9AppId;
	}
	public void setIsCustomS9AppId(Boolean isCustomS9AppId) {
		this.isCustomS9AppId = isCustomS9AppId;
	}
	public String getS9ApplicationId() {
		return s9ApplicationId;
	}
	public void setS9ApplicationId(String s9ApplicationId) {
		this.s9ApplicationId = s9ApplicationId;
	}
	
	public Boolean getIsCustomSyAppId() {
		return isCustomSyAppId;
	}
	public void setIsCustomSyAppId(Boolean isCustomSyAppId) {
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
	public List<PacketMappingData> getDiameterToPCRFPacketMappingList() {
		return diameterToPCRFPacketMappingList;
	}
	public void setDiameterToPCRFPacketMappingList(
			List<PacketMappingData> diameterToPCRFPacketMappingList) {
		this.diameterToPCRFPacketMappingList = diameterToPCRFPacketMappingList;
	}
	public List<PacketMappingData> getPcrfToDiameterPacketMappingList() {
		return pcrfToDiameterPacketMappingList;
	}
	public void setPcrfToDiameterPacketMappingList(List<PacketMappingData> pcrfToDiameterPacketMappingList) {
		this.pcrfToDiameterPacketMappingList = pcrfToDiameterPacketMappingList;
	}
	public List<PacketMappingData> getPacketMappingList() {
		return packetMappingList;
	}
	public void setPacketMappingList(List<PacketMappingData> packetMappingList) {
		this.packetMappingList = packetMappingList;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public Integer getMaxIPCANSession() {
		return maxIPCANSession;
	}
	public void setMaxIPCANSession(Integer maxIPCANSession) {
		this.maxIPCANSession = maxIPCANSession;
	}
	public String getIdentityFieldName() {
		return identityFieldName;
	}
	public void setIdentityFieldName(String identityFieldName) {
		this.identityFieldName = identityFieldName;
	}
	public String getStartTimeField() {
		return startTimeField;
	}
	public void setStartTimeField(String startTimeField) {
		this.startTimeField = startTimeField;
	}
	public String getLastUpdatedTimeField() {
		return lastUpdatedTimeField;
	}
	public void setLastUpdatedTimeField(String lastUpdateTimeField) {
		this.lastUpdatedTimeField = lastUpdateTimeField;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
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
	public String getSupportedVendorList() {
		return supportedVendorList;
	}
	public void setSupportedVendorList(String supportedVendorList) {
		this.supportedVendorList = supportedVendorList;
	}
	public List getVendorList() {
		return vendorList;
	}
	public void setVendorList(List vendorList) {
		this.vendorList = vendorList;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getGatewayProfileName() {
		return gatewayProfileName;
	}
	public void setGatewayProfileName(String gatewayProfileName) {
		this.gatewayProfileName = gatewayProfileName;
	}
	public String getGatewayType() {
		return gatewayType;
	}
	public void setGatewayType(String gatewayType) {
		this.gatewayType = gatewayType;
	}
	public long getVendorId() {
		return vendorId;
	}
	public void setVendorId(long vendorId) {
		this.vendorId = vendorId;
	}
	public String getCommProtocol() {
		return commProtocol;
	}
	public void setCommProtocol(String commProtocol) {
		this.commProtocol = commProtocol;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public int getBufferBandwidth() {
		return bufferBandwidth;
	}
	public void setBufferBandwidth(int bufferBandwidth) {
		this.bufferBandwidth = bufferBandwidth;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDbFieldName(String dbFieldName) {
		this.dbFieldName = dbFieldName;
	}
	public String getDbFieldName() {
		return dbFieldName;
	}
	
	public void setDatasourceList(List<DatabaseDSData> datasourceList) {
		this.datasourceList = datasourceList;
	}
	public List<DatabaseDSData> getDatasourceList() {
		return datasourceList;
	}
	public void setSessionIdFieldName(String sessionIdFieldName) {
		this.sessionIdFieldName = sessionIdFieldName;
	}
	public String getSessionIdFieldName() {
		return sessionIdFieldName;
	}
	public void setDatasourceId(Long datasourceId) {
		this.datasourceId = datasourceId;
	}
	public Long getDatasourceId() {
		return datasourceId;
	}
	public void setPolicyKey(String policyKey) {
		this.policyKey = policyKey;
	}
	public String getPolicyKey() {
		return policyKey;
	}
	public void setSearchAttributeField(String searchAttributeField) {
		this.searchAttributeField = searchAttributeField;
	}
	public String getSearchAttributeField() {
		return searchAttributeField;
	}
	public int getSupportedStandard() {
		return supportedStandard;
	}
	public void setSupportedStandard(int supportedStandard) {
		this.supportedStandard = supportedStandard;
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
	public List<RuleMappingData> getRuleMappingList() {
		return ruleMappingList;
	}
	public void setRuleMappingList(List<RuleMappingData> ruleMappingList) {
		this.ruleMappingList = ruleMappingList;
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
