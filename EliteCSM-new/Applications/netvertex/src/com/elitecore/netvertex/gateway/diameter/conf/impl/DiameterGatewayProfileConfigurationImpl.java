package com.elitecore.netvertex.gateway.diameter.conf.impl;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.util.constants.Vendor;
import com.elitecore.corenetvertex.constants.ChargingRuleInstallMode;
import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.corenetvertex.constants.GatewayComponent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.constants.SupportedStandard;
import com.elitecore.corenetvertex.constants.UMStandard;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.mibs.constants.TransportProtocols;
import com.elitecore.netvertex.core.data.ScriptData;
import com.elitecore.netvertex.core.data.ServiceGuide;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayProfileConfiguration;
import com.elitecore.netvertex.gateway.diameter.mapping.ApplicationPacketType;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCCToDiameterMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Harsh Patel
 *
 */
public class DiameterGatewayProfileConfigurationImpl implements DiameterGatewayProfileConfiguration {

	private String profileId;
	private CommunicationProtocol commProtocol;
	private Vendor vendor;
	private String firmware;
	private String description;
	private String profileName;
	private String supportedVendors;
	//Not-Relodable
	private PCRFKeyValueConstants usageReportingType;
	private PCRFKeyValueConstants revalidationMode;
	private ChargingRuleInstallMode chargingRuleInstallMode;
	private SupportedStandard supportedStandard;
	private UMStandard usageMeteringStandard;
	
	private int timeout;
	private int initConnectionDurationInSec;
	//Not-Relodable
	private long gxApplicationId;
	private long rxApplicationId;
	private long gyApplicationId;
	private long syApplicationId = ApplicationIdentifier.TGPP_SY.getApplicationId();
	private long gxVendorId;
	private long rxVendorId;
	private long gyVendorId;
	private long syVendorId = ApplicationIdentifier.TGPP_SY.getVendorId();
	private int dwIntervalinSec = 60;
	private GatewayComponent gatewayType;
	private TransportProtocols transportProtocol = TransportProtocols.TCP;
	
	private boolean sessCleanupOnCER = false;
	private boolean sessCleanupOnDPR = false;
	private boolean sendDPROnCloseEvent;
	private boolean tcpNagleAlgo = false;
	
	private String additionalCERAVPs;
	private String additionalDPRAVPs;
	private String additionalDWRAVPs;
	
	private int socketReceiveBufferSize = -1;
	private int socketSendBufferSize = -1;
	private List<String> sessionLookupKeys = Arrays.asList(PCRFKeyConstants.CS_SESSION_IPV4.val, PCRFKeyConstants.CS_SESSION_IPV6.val);

	private List<ServiceGuide> serviceGuides;
	private List<ScriptData> scriptDatas;

	private boolean pccMonitoringSupported = true;
	private long s9ApplicationId;
	private long s9VendorId;

	private Map<ApplicationPacketType, PCCToDiameterMapping> pccToDiameterPacketMappings;
	private Map<ApplicationPacketType, DiameterToPCCMapping> diameterToPCCPacketMappings;

	public DiameterGatewayProfileConfigurationImpl(String profileId,
												   CommunicationProtocol commProtocol,
												   Vendor vendor,
												   String firmware,
												   String description,
												   String profileName,
												   String supportedVendors,
												   PCRFKeyValueConstants usageReportingType,
												   PCRFKeyValueConstants revalidationMode,
												   ChargingRuleInstallMode chargingRuleInstallMode,
												   SupportedStandard supportedStandard,
												   UMStandard usageMeteringStandard,
												   int timeout,
												   int initConnectionDurationInSec,
												   long gxApplicationId,
												   long rxApplicationId,
												   long gyApplicationId,
												   long syApplicationId,
												   long s9ApplicationId,
												   long gxVendorId,
												   long rxVendorId,
												   long gyVendorId,
												   long syVendorId,
												   long s9VendorId,
												   int dwIntervalinSec,
												   GatewayComponent gatewayType,
												   Map<ApplicationPacketType, PCCToDiameterMapping> pccToDiameterPacketMappings,
												   Map<ApplicationPacketType, DiameterToPCCMapping> diameterToPCCPacketMappings,
												   boolean sessCleanupOnCER,
												   boolean sessCleanupOnDPR,
												   boolean sendDPROnCloseEvent,
												   boolean tcpNagleAlgo,
												   String additionalCERAVPs,
												   String additionalDPRAVPs,
												   String additionalDWRAVPs,
												   int socketReceiveBufferSize,
												   int socketSendBufferSize,
												   List<String> sessionLookupKeys,
												   List<ServiceGuide> serviceGuides,
												   List<ScriptData> scriptDatas,
												   boolean pccMonitoringSupported) {
		this.profileId = profileId;
		this.commProtocol = commProtocol;
		this.vendor = vendor;
		this.firmware = firmware;
		this.description = description;
		this.profileName = profileName;
		this.supportedVendors = supportedVendors;
		this.usageReportingType = usageReportingType;
		this.revalidationMode = revalidationMode;
		this.chargingRuleInstallMode = chargingRuleInstallMode;
		this.supportedStandard = supportedStandard;
		this.usageMeteringStandard = usageMeteringStandard;
		this.timeout = timeout;
		this.initConnectionDurationInSec = initConnectionDurationInSec;
		this.gxApplicationId = gxApplicationId;
		this.rxApplicationId = rxApplicationId;
		this.gyApplicationId = gyApplicationId;
		this.syApplicationId = syApplicationId;
		this.s9ApplicationId = s9ApplicationId;
		this.gxVendorId = gxVendorId;
		this.rxVendorId = rxVendorId;
		this.gyVendorId = gyVendorId;
		this.syVendorId = syVendorId;
		this.s9VendorId = s9VendorId;
		this.dwIntervalinSec = dwIntervalinSec;
		this.gatewayType = gatewayType;
		this.pccToDiameterPacketMappings = pccToDiameterPacketMappings;
		this.diameterToPCCPacketMappings = diameterToPCCPacketMappings;
		this.sessCleanupOnCER = sessCleanupOnCER;
		this.sessCleanupOnDPR = sessCleanupOnDPR;
		this.sendDPROnCloseEvent = sendDPROnCloseEvent;
		this.tcpNagleAlgo = tcpNagleAlgo;
		this.additionalCERAVPs = additionalCERAVPs;
		this.additionalDPRAVPs = additionalDPRAVPs;
		this.additionalDWRAVPs = additionalDWRAVPs;
		this.socketReceiveBufferSize = socketReceiveBufferSize;
		this.sessionLookupKeys = sessionLookupKeys;
		this.serviceGuides = serviceGuides;
		this.scriptDatas = scriptDatas;
		this.pccMonitoringSupported = pccMonitoringSupported;
		this.socketSendBufferSize  = socketSendBufferSize;
	}

	public DiameterGatewayProfileConfigurationImpl() {

	}

	public DiameterGatewayProfileConfigurationImpl setProfileId(String profileId) {
		this.profileId = profileId;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setCommProtocol(CommunicationProtocol commProtocol) {
		this.commProtocol = commProtocol;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setVendor(Vendor vendor) {
		this.vendor = vendor;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setFirmware(String firmware) {
		this.firmware = firmware;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setDescription(String description) {
		this.description = description;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setProfileName(String profileName) {
		this.profileName = profileName;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setSupportedVendors(String supportedVendors) {
		this.supportedVendors = supportedVendors;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setUsageReportingTypeWithBuild(PCRFKeyValueConstants usageReportingType) {
		this.usageReportingType = usageReportingType;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setRevalidationMode(PCRFKeyValueConstants revalidationMode) {
		this.revalidationMode = revalidationMode;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setChargingRuleInstallMode(ChargingRuleInstallMode chargingRuleInstallMode) {
		this.chargingRuleInstallMode = chargingRuleInstallMode;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setSupportedStandard(SupportedStandard supportedStandard) {
		this.supportedStandard = supportedStandard;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setUsageMeteringStandard(UMStandard usageMeteringStandard) {
		this.usageMeteringStandard = usageMeteringStandard;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setTimeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setInitConnectionDurationInSec(int initConnectionDurationInSec) {
		this.initConnectionDurationInSec = initConnectionDurationInSec;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setGxApplicationId(long gxApplicationId) {
		this.gxApplicationId = gxApplicationId;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setRxApplicationId(long rxApplicationId) {
		this.rxApplicationId = rxApplicationId;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setGyApplicationId(long gyApplicationId) {
		this.gyApplicationId = gyApplicationId;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setSyApplicationId(long syApplicationId) {
		this.syApplicationId = syApplicationId;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setGxVendorId(long gxVendorId) {
		this.gxVendorId = gxVendorId;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setRxVendorId(long rxVendorId) {
		this.rxVendorId = rxVendorId;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setGyVendorId(long gyVendorId) {
		this.gyVendorId = gyVendorId;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setSyVendorId(long syVendorId) {
		this.syVendorId = syVendorId;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setDwIntervalinSec(int dwIntervalinSec) {
		this.dwIntervalinSec = dwIntervalinSec;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setGatewayType(GatewayComponent gatewayType) {
		this.gatewayType = gatewayType;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setTransportProtocol(TransportProtocols transportProtocol) {
		this.transportProtocol = transportProtocol;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setSessCleanupOnCER(boolean sessCleanupOnCER) {
		this.sessCleanupOnCER = sessCleanupOnCER;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setSessCleanupOnDPR(boolean sessCleanupOnDPR) {
		this.sessCleanupOnDPR = sessCleanupOnDPR;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setSendDPROnCloseEvent(boolean sendDPROnCloseEvent) {
		this.sendDPROnCloseEvent = sendDPROnCloseEvent;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setTcpNagleAlgo(boolean tcpNagleAlgo) {
		this.tcpNagleAlgo = tcpNagleAlgo;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setAdditionalCERAVPs(String additionalCERAVPs) {
		this.additionalCERAVPs = additionalCERAVPs;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setAdditionalDPRAVPs(String additionalDPRAVPs) {
		this.additionalDPRAVPs = additionalDPRAVPs;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setAdditionalDWRAVPs(String additionalDWRAVPs) {
		this.additionalDWRAVPs = additionalDWRAVPs;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setSocketReceiveBufferSize(int socketReceiveBufferSize) {
		this.socketReceiveBufferSize = socketReceiveBufferSize;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setSocketSendBufferSize(int socketSendBufferSize) {
		this.socketSendBufferSize = socketSendBufferSize;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setSessionLookupKeys(List<String> sessionLookupKeys) {
		this.sessionLookupKeys = sessionLookupKeys;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setScriptDatas(List<ScriptData> scriptDatas) {
		this.scriptDatas = scriptDatas;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setPccMonitoringSupported(boolean pccMonitoringSupported) {
		this.pccMonitoringSupported = pccMonitoringSupported;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setS9ApplicationId(long s9ApplicationId) {
		this.s9ApplicationId = s9ApplicationId;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setS9VendorId(long s9VendorId) {
		this.s9VendorId = s9VendorId;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setPccToDiameterPacketMappings(Map<ApplicationPacketType, PCCToDiameterMapping> pccToDiameterPacketMappings) {
		this.pccToDiameterPacketMappings = pccToDiameterPacketMappings;
		return this;
	}

	public DiameterGatewayProfileConfigurationImpl setDiameterToPCCPacketMappings(Map<ApplicationPacketType, DiameterToPCCMapping> diameterToPCCPacketMappings) {
		this.diameterToPCCPacketMappings = diameterToPCCPacketMappings;
		return this;
	}

	@Override
	public CommunicationProtocol getCommProtocol() {
		return commProtocol;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getProfileId() {
		return profileId;
	}

	@Override
	public String getProfileName() {
		return profileName;
	}

	@Override
	public String getSupportedVendorList() {
		return supportedVendors;
	}

	@Override
	public Vendor getVendor() {
		return vendor;
	}
	
	@Override
	public SupportedStandard getSupportedStandard() {
		return supportedStandard;
	}

	
	@Override
	public ChargingRuleInstallMode getChargingRuleInstallMode() {
		return chargingRuleInstallMode;
	}
	

	@Override
	public int getTimeout() {
		return timeout;
	}

	@Override
	public int getInitConnectionDuration() {
		return initConnectionDurationInSec;
	}
	

	@Override
	public long getGxApplicationId() {
		return gxApplicationId;
	}
	
	@Override
	public PCRFKeyValueConstants getUsageReportingType() {
		return usageReportingType;
	}

	@Override
	public long getRxApplicationId() {
		return rxApplicationId;
	}
	
	@Override
	public long getGyApplicationId() {
		return gyApplicationId;
	}

	@Override
	public long getGxVendorId() {
		return gxVendorId;
	}

	@Override
	public long getRxVendorId() {
		return rxVendorId;
	}
	
	@Override
	public long getGyVendorId() {
		return gyVendorId;
	}
	
	@Override
	public int getDWInterval() {
		return dwIntervalinSec;
	}
	
	@Override
	public GatewayComponent getGatewayType() {
		return gatewayType;
	}
	
	@Override
	public boolean isSessCleanupOnCER() {
		return sessCleanupOnCER;
	}

	@Override
	public boolean isSessCleanupOnDPR() {
		return sessCleanupOnDPR;
	}

	@Override
	public boolean isSendDPROnCloseEvent() {
		return sendDPROnCloseEvent;
	}
	
	@Override
	public boolean isTCPNagleAlgo() {
		return tcpNagleAlgo;
	}

	@Override
	public String getAdditionalCERAVPs() {
		return additionalCERAVPs;
	}
	
	@Override
	public String getAdditionalDWRAVPs() {
		return additionalDWRAVPs;
	}

	@Override
	public String getAdditionalDPRAVPs() {
		return additionalDPRAVPs;
	}

	@Override
	public int getSocketReceiveBufferSize() {
		return socketReceiveBufferSize;
	}

	@Override
	public int getSocketSendBufferSize() {
		return socketSendBufferSize;
	}
	
	@Override
	public TransportProtocols getTransportProtocol() {
		return transportProtocol;
	}
	
	@Override
	public PCRFKeyValueConstants getRevalidationMode() {
		return revalidationMode;
	}
	
	@Override
	public List<ScriptData> getScriptsConfigs() {
		return scriptDatas;
	}

	public List<ServiceGuide> getServiceGuides() {
		return serviceGuides;
	}

	public void setServiceGuides(List<ServiceGuide> serviceGuides) {
		this.serviceGuides = serviceGuides;
	}

	@Override
	public long getSyApplicationId() {
		return syApplicationId;
	}


	@Override
	public long getSyVendorId() {
		return syVendorId;
	}

	@Override
	public long getS9ApplicationId() {
		return s9ApplicationId;
	}

	@Override
	public long getS9VendorId() {
		return s9VendorId;
	}

	@Override
	public UMStandard getUsageMeteringStandard() {
		return usageMeteringStandard;
	}

	@Override
	public List<String> getSessionLookupKey() {
		return sessionLookupKeys;
	}

	@Override
	public boolean isPCCLevelMonitoringSupported() {
		return pccMonitoringSupported;
	}

	
	public SessionTypeConstant getSessionType(long applicationId) {
		
		if (applicationId == gxApplicationId) {
			return SessionTypeConstant.GX;
		} else if (applicationId == gyApplicationId && GatewayComponent.APPLICATION_FUNCTION == getGatewayType()) {
			return SessionTypeConstant.RO;
		} else if (applicationId == gyApplicationId) {
			return SessionTypeConstant.GY;
		} else if (applicationId == rxApplicationId) {
			return SessionTypeConstant.RX;
		} else if (applicationId == syApplicationId) {
			return SessionTypeConstant.SY;
		}
		
		return null;
	}
	
	@Override
	public void setusageReportingType(PCRFKeyValueConstants usageReporting) {
		this.usageReportingType = usageReporting;
	}
	
	// PCC to Diameter mappings

	@Override
	public PCCToDiameterMapping getGxCCAMappings() {
		return pccToDiameterPacketMappings.get(ApplicationPacketType.GX_CCA);
	}

	@Override
	public PCCToDiameterMapping getGyCCAMappings() {
		return pccToDiameterPacketMappings.get(ApplicationPacketType.GY_CCA);
	}

	@Override
	public PCCToDiameterMapping getGxRARMappings() {
		return pccToDiameterPacketMappings.get(ApplicationPacketType.GX_RAR);
	}

	@Override
	public PCCToDiameterMapping getGyRARMappings() {
		return pccToDiameterPacketMappings.get(ApplicationPacketType.GY_RAR);
	}

	@Override
	public PCCToDiameterMapping getSNAMappings() {
		return pccToDiameterPacketMappings.get(ApplicationPacketType.SNA);
	}

	@Override
	public PCCToDiameterMapping getSLRMappings() {
		return pccToDiameterPacketMappings.get(ApplicationPacketType.SLR);
	}

	@Override
	public PCCToDiameterMapping getSTAMappings() {
		return pccToDiameterPacketMappings.get(ApplicationPacketType.STA);
	}

	@Override
	public PCCToDiameterMapping getASRMappings() {
		return pccToDiameterPacketMappings.get(ApplicationPacketType.ASR);
	}

	@Override
	public PCCToDiameterMapping getAAAMappings() {
		return pccToDiameterPacketMappings.get(ApplicationPacketType.AAA);
	}

	// Diameter to PCC mappings

	@Override
	public DiameterToPCCMapping getGxCCRMappings() {
		return diameterToPCCPacketMappings.get(ApplicationPacketType.GX_CCR);
	}

	@Override
	public DiameterToPCCMapping getGyCCRMappings() {
		return diameterToPCCPacketMappings.get(ApplicationPacketType.GY_CCR);
	}

	@Override
	public DiameterToPCCMapping getGxRAAMappings() {
		return diameterToPCCPacketMappings.get(ApplicationPacketType.GX_RAA);
	}

	@Override
	public DiameterToPCCMapping getGyRAAMappings() {
		return diameterToPCCPacketMappings.get(ApplicationPacketType.GY_RAA);
	}

	@Override
	public DiameterToPCCMapping getAARMappings() {
		return diameterToPCCPacketMappings.get(ApplicationPacketType.AAR);
	}

	@Override
	public DiameterToPCCMapping getASAMappings() {
		return diameterToPCCPacketMappings.get(ApplicationPacketType.ASA);
	}

	@Override
	public DiameterToPCCMapping getSLAMappings() {
		return diameterToPCCPacketMappings.get(ApplicationPacketType.SLA);
	}

	@Override
	public DiameterToPCCMapping getSNRMappings() {
		return diameterToPCCPacketMappings.get(ApplicationPacketType.SNR);
	}

	@Override
	public DiameterToPCCMapping getSTRMappings() {
		return diameterToPCCPacketMappings.get(ApplicationPacketType.RX_STR);
	}

	@Override
	public PCCToDiameterMapping getSySTRMapping() {
		return pccToDiameterPacketMappings.get(ApplicationPacketType.SY_STR);

	}

	@Override
	public Map<ApplicationPacketType, PCCToDiameterMapping> getPCCToDiameterPacketMappings() {
		return pccToDiameterPacketMappings;
	}

	@Override
	public Map<ApplicationPacketType, DiameterToPCCMapping> getDiameterToPCCPacketMappings() {
		return diameterToPCCPacketMappings;
	}

	@Override
	public String toString(){
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- Diameter Gateway Profile Configuration -- ");
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {

		builder.append("Id", profileId);
		builder.append("Name", profileName);
		builder.append("description", description);
		builder.append("Gateway Type", gatewayType);
		builder.append("Vendor", vendor.getName());
		builder.append("Communication Protocol", commProtocol.getName());
		builder.append("Firmware", firmware);
		builder.append("Usage Reporting Type", usageReportingType.val);
		builder.append("Revalidation Mode", revalidationMode.val);

		builder.appendHeading(" -- Connection Parameters -- ");

		builder.append("Timeout(ms)", timeout);
		builder.append("Session Cleanup on CER", sessCleanupOnCER);
		builder.append("Session Cleanup on DPR", sessCleanupOnDPR);
		builder.append("Additional CER AVPs", additionalCERAVPs);
		builder.append("Additional DPR AVPs", additionalDPRAVPs);
		builder.append("Additional DWR AVPs", additionalDWRAVPs);
		builder.append("Send DPR on Close Event", sendDPROnCloseEvent);
		if (socketReceiveBufferSize > 0) {
			builder.append("Socket Send Buffer Size", socketSendBufferSize);
		} else {
			builder.append("Socket Send Buffer Size", socketSendBufferSize + " (OS Default buffer size)");
		}

		if (socketReceiveBufferSize > 0) {
			builder.append("Socket Receiver Buffer Size", socketReceiveBufferSize);
		} else {
			builder.append("Socket Receiver Buffer Size", socketReceiveBufferSize + " (OS Default buffer size)");
		}

		builder.append("TCP Nagle Algorithm", tcpNagleAlgo);
		builder.append("DW Interval(sec)", dwIntervalinSec);
		builder.append("Init Connection Duration(sec)", initConnectionDurationInSec);
		builder.append("Transport Protocol", transportProtocol);

		builder.appendHeading(" -- Application Parameters -- ");

		builder.append("Gx Application Id (vendorID:AppID)", gxVendorId + ":" + gxApplicationId);
		builder.append("Rx Application Id (vendorID:AppID)", rxVendorId + ":" + rxApplicationId);
		builder.append("Gy Application Id (vendorID:AppID)", gyVendorId + ":" + gyApplicationId);
		builder.append("Sy Application Id (vendorID:AppID)", syVendorId + ":" + syApplicationId);
		builder.append("S9 Application Id (vendorID:AppID)", s9VendorId + ":" + s9ApplicationId);
		builder.append("Charging Rule Install Mode", chargingRuleInstallMode);

		if (supportedStandard != null) {
			builder.append("Supported Standard", supportedStandard.getName());
		}

		if (usageMeteringStandard != null) {
			builder.append("UM Standard", usageMeteringStandard.displayValue);
		}

		builder.append("Supported Vendors", supportedVendors);

		if (GatewayComponent.APPLICATION_FUNCTION == gatewayType) {
			builder.appendChild("Session LookUp Key", sessionLookupKeys);
		}

		if(serviceGuides.isEmpty()==false){
			builder.newline();
			builder.appendHeading("Service Guiding");
			builder.incrementIndentation();
			for (ServiceGuide serviceGuide : this.getServiceGuides()) {
				builder.append("Service", serviceGuide.getServiceId());
				builder.append("Condition", serviceGuide.getConditionStr());
				builder.newline();
			}
			builder.decrementIndentation();
		}

		if (Collectionz.isNullOrEmpty(scriptDatas) == false) {
			builder.appendHeading(" -- Groovy scripts -- ");
			for (ScriptData scriptData : scriptDatas) {
				builder.append("Script", scriptData.getScriptName());
				if (scriptData.getScriptArgumet() != null) {
					builder.append("Script Argument", scriptData.getScriptArgumet());
				}
			}
		}
	}
}
