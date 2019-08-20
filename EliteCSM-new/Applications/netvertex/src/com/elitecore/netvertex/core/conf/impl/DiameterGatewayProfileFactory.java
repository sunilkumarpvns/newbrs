package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.util.constants.Vendor;
import com.elitecore.corenetvertex.constants.*;
import com.elitecore.corenetvertex.sm.gateway.DiameterGatewayProfileData;
import com.elitecore.corenetvertex.sm.gateway.VendorData;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx.HuaweiUMBuilder;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx.MSCCUMBuilder;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx.TgppR9Builder;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx.UMBuilder;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx.ChargingRuleDefinitionPacketMapping;
import com.elitecore.netvertex.gateway.diameter.conf.impl.DiameterGatewayProfileConfigurationImpl;

import java.util.Arrays;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * 
 * @author Jay Trivedi
 *
 */
public class DiameterGatewayProfileFactory{

	private static final String MODULE = "DIA-GW-PROFILE-FCTRY";
	private static final Splitter COMMA_BASE_SPLITTER = Splitter.on(',').trimTokens();
	private PCCMappingFactory pccMappingFactory;
	private DiameterPacketMappingFactory diameterPacketMappingFactory;
	private ScriptDataFactory scriptDataFactory;
	private ServiceGuideFactory serviceGuideFactory;

	public DiameterGatewayProfileFactory(PCCMappingFactory pccMappingFactory,
										 DiameterPacketMappingFactory diameterPacketMappingFactory, ScriptDataFactory scriptDataFactory, ServiceGuideFactory serviceGuideFactory) {
		this.pccMappingFactory = pccMappingFactory;
		this.diameterPacketMappingFactory = diameterPacketMappingFactory;
		this.scriptDataFactory = scriptDataFactory;
		this.serviceGuideFactory = serviceGuideFactory;
	}

	public DiameterGatewayProfileConfigurationImpl create(DiameterGatewayProfileData diameterGatewayProfileData) throws LoadConfigurationException {

		ChargingRuleInstallMode chargingRuleInstallMode = ChargingRuleInstallMode.fromValue(diameterGatewayProfileData.getChargingRuleInstallMode());
		if (chargingRuleInstallMode == null) {
			LogManager.getLogger().warn(MODULE, "Considering CHARGING_RULE_INSTALL_MODE as " + ChargingRuleInstallMode.GROUPED
					+ ". Reason: CHARGING_RULE_INSTALL_MODE not configured");
			chargingRuleInstallMode = ChargingRuleInstallMode.GROUPED;
		}

		SupportedStandard supportedStandard = SupportedStandard.valueOf(diameterGatewayProfileData.getSupportedStandard());
		if (supportedStandard == null) {
			LogManager.getLogger().warn(MODULE, "Considering SUPPORTED_STANDARD as " + SupportedStandard.RELEASE_9
					+ ". Reason: SUPPORTED_STANDARD not configured");
			supportedStandard = SupportedStandard.RELEASE_9;
		}

		PCRFKeyValueConstants usageReportingType = PCRFKeyValueConstants.USAGE_REPORTING_TYPE_NON_CUMULATIVE;
		if (Strings.isNullOrBlank(diameterGatewayProfileData.getUsageReportingType()) == false) {
			PCRFKeyValueConstants tempUsageReportingType = PCRFKeyValueConstants
					.fromString(PCRFKeyConstants.USAGE_REPORTING_TYPE, diameterGatewayProfileData.getUsageReportingType());
			if (tempUsageReportingType != null) {
				usageReportingType = tempUsageReportingType;
			} else {
				LogManager.getLogger().warn(MODULE, "Considering USAGE_REPORTING_TYPE as " + usageReportingType.val
						+ ". Reason: Invalid configuration value: " + diameterGatewayProfileData.getUsageReportingType());
			}
		}

		PCRFKeyValueConstants revalidationMode = PCRFKeyValueConstants.REVALIDATION_MODE_SERVER_INITIATED;
		PCRFKeyValueConstants tempRevalidationMode = PCRFKeyValueConstants.fromString(PCRFKeyConstants.REVALIDATION_MODE, diameterGatewayProfileData
				.getRevalidationMode());
		if (tempRevalidationMode != null) {
			revalidationMode = tempRevalidationMode;
		} else {
			LogManager.getLogger().warn(MODULE, "Considering REVALIDATION_MODE as " + revalidationMode.val
					+ ". Reason: Invalid configuration value: " + diameterGatewayProfileData.getRevalidationMode());
		}

		UMStandard usageMeteringStandard = UMStandard.TGPPR9;
		if (diameterGatewayProfileData.getUmStandard() == null) {
			LogManager.getLogger().warn(MODULE, "Considering USAGE METERING STANDARD as " + usageMeteringStandard.value + " for gateway profile: "
					+ diameterGatewayProfileData.getName() + ".Reason: USAGE METERING STANDARD not configured");
		} else {
			usageMeteringStandard = UMStandard.valueOf(diameterGatewayProfileData.getUmStandard());
		}

		boolean pccMonitoringSupported = true;

		if (usageMeteringStandard == UMStandard.TGPPR9_SESSION_ONLY
				|| usageMeteringStandard == UMStandard.MSCC
				|| usageMeteringStandard == UMStandard.HUAWEI) {

			pccMonitoringSupported = false;
		}

		long gxVendorId;
		long gxApplicationId;
		String[] tempGxStr = diameterGatewayProfileData.getGxApplicationId().split(":");
		try {
			if (tempGxStr.length > 1) {
				gxVendorId = Integer.parseInt(tempGxStr[0].trim());
				gxApplicationId = Integer.parseInt(tempGxStr[1].trim());
			} else {
				gxVendorId = DiameterConstants.VENDOR_3GPP_ID;
				gxApplicationId = Integer.parseInt(tempGxStr[0].trim());
			}
		} catch (NumberFormatException nfExe) {
			LogManager
					.getLogger()
					.warn(MODULE, "Either Vendor-Id or Application-Id does not properly configured for Gx interface. So using default identifier. Reason : "
							+ nfExe.getMessage());
			gxVendorId = DiameterConstants.VENDOR_3GPP_ID;
			gxApplicationId = ApplicationIdentifier.TGPP_GX_29_212_18.applicationId;
		}

		long syVendorId;
		long syApplicationId;
		String[] tempSyAppId = diameterGatewayProfileData.getSyApplicationId().split(":");
		try {
			if (tempSyAppId.length > 1) {
				syVendorId = Integer.parseInt(tempSyAppId[0].trim());
				syApplicationId = Integer.parseInt(tempSyAppId[1].trim());
			} else {
				syVendorId = ApplicationIdentifier.TGPP_SY.getVendorId();
				syApplicationId = Integer.parseInt(tempSyAppId[0].trim());
			}
		} catch (NumberFormatException nfExe) {
			LogManager.getLogger().warn(MODULE, "Either Vendor-Id or Application-Id does not properly configured for Sy interface. " +
					"So using default identifier. Reason : " + nfExe.getMessage());
			syVendorId = ApplicationIdentifier.TGPP_SY.getVendorId();
			syApplicationId = ApplicationIdentifier.TGPP_SY.getApplicationId();
		}

		long rxVendorId;
		long rxApplicationId;
		String[] tempRxStr = diameterGatewayProfileData.getRxApplicationId().split(":");
		try {
			if (tempRxStr.length > 1) {
				rxVendorId = Integer.parseInt(tempRxStr[0].trim());
				rxApplicationId = Integer.parseInt(tempRxStr[1].trim());
			} else {
				rxVendorId = DiameterConstants.VENDOR_3GPP_ID;
				rxApplicationId = Integer.parseInt(tempRxStr[0].trim());
			}
		} catch (NumberFormatException nfExe) {
			LogManager.getLogger().warn(MODULE, "Either Vendor-Id or Application-Id does not properly configured for Rx interface. So using default identifier. Reason : "
					+ nfExe.getMessage());
			rxVendorId = DiameterConstants.VENDOR_3GPP_ID;
			rxApplicationId = ApplicationIdentifier.TGPP_RX_29_214_18.applicationId;
		}

		long s9VendorId;
		long s9ApplicationId;
		String[] tempS9Str = diameterGatewayProfileData.getS9ApplicationId().split(":");
		try {
			if (tempS9Str.length > 1) {
				s9VendorId = Integer.parseInt(tempS9Str[0].trim());
				s9ApplicationId = Integer.parseInt(tempS9Str[1].trim());
			} else {
				s9VendorId = DiameterConstants.VENDOR_STANDARD;
				s9ApplicationId = Integer.parseInt(tempS9Str[0].trim());
			}
		} catch (NumberFormatException nfExe) {
			LogManager.getLogger().warn(MODULE, "Either Vendor-Id or Application-Id does not properly configured for Cisco Gx interface. So using default identifier. Reason : "
					+ nfExe.getMessage());
			s9VendorId = DiameterConstants.VENDOR_STANDARD;
			s9ApplicationId = DiameterConstants.CREDIT_CONTROL_APPLICATION_ID;
		}

		long gyVendorId;
		long gyApplicationId;
		String[] tempGyStr = diameterGatewayProfileData.getGyApplicationId().split(":");
		try {
			if (tempGyStr.length > 1) {
				gyVendorId = Integer.parseInt(tempGyStr[0].trim());
				gyApplicationId = Integer.parseInt(tempGyStr[1].trim());
			} else {
				gyVendorId = DiameterConstants.VENDOR_STANDARD;
				gyApplicationId = Integer.parseInt(tempGyStr[0].trim());
			}
		} catch (NumberFormatException nfExe) {
			LogManager.getLogger().warn(MODULE, "Either Vendor-Id or Application-Id does not properly configured for Cisco Gx interface. So using default identifier. Reason : "
					+ nfExe.getMessage());
			gyVendorId = DiameterConstants.VENDOR_STANDARD;
			gyApplicationId = DiameterConstants.CREDIT_CONTROL_APPLICATION_ID;
		}



		List<String> sessionLookupKeys = Arrays.asList(PCRFKeyConstants.CS_SESSION_IPV4.val, PCRFKeyConstants.CS_SESSION_IPV6.val);
		if (diameterGatewayProfileData.getSessionLookUpKey() != null) {
			String sessionLookupKey = diameterGatewayProfileData.getSessionLookUpKey();
			if (sessionLookupKey != null) {
				List<String> tempSessionLookupKeys = COMMA_BASE_SPLITTER.split(sessionLookupKey);
				if (tempSessionLookupKeys != null && tempSessionLookupKeys.isEmpty() == false) {
					sessionLookupKeys = tempSessionLookupKeys;
				} else {
					if (getLogger().isDebugLogLevel())
						getLogger().debug(MODULE, "Considering session look up keys as "
								+ PCRFKeyConstants.CS_SESSION_IPV4.val + " and " + PCRFKeyConstants.CS_SESSION_IPV6.val
								+ ".Reason: invalid value:" + sessionLookupKey + " configured");
				}
			} else {
				if (getLogger().isDebugLogLevel())
					getLogger().debug(MODULE, "Considering session look up keys as "
							+ PCRFKeyConstants.CS_SESSION_IPV4.val + " and " + PCRFKeyConstants.CS_SESSION_IPV6.val
							+ ".Reason: No value configured");
			}
		}


		Boolean sessCleanupOnCER = diameterGatewayProfileData.getSessionCleanUpCER();
		Boolean sessCleanupOnDPR = diameterGatewayProfileData.getSessionCleanUpDPR();
		Boolean sendDPROnCloseEvent = diameterGatewayProfileData.getSendDPRCloseEvent();
		Boolean tcpNagleAlgo = diameterGatewayProfileData.getTcpNagleAlgorithm();

		ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping = pccMappingFactory.create(diameterGatewayProfileData.getId(),
				diameterGatewayProfileData.getName(),
				chargingRuleInstallMode,
				diameterGatewayProfileData.getDiameterGwProfilePCCRuleMappings());


		UMBuilder umBuilder = new TgppR9Builder();

		if(usageMeteringStandard == UMStandard.HUAWEI) {
			umBuilder = new HuaweiUMBuilder();
		} else if(usageMeteringStandard == UMStandard.MSCC) {
			umBuilder = new MSCCUMBuilder();
		}

		diameterPacketMappingFactory.create(diameterGatewayProfileData,
				chargingRuleDefinitionPacketMapping, umBuilder);
		
		return new DiameterGatewayProfileConfigurationImpl(diameterGatewayProfileData.getId(), CommunicationProtocol.DIAMETER
				, createVendor(diameterGatewayProfileData.getVendorData()), diameterGatewayProfileData.getFirmware()
				, diameterGatewayProfileData.getDescription()
				, diameterGatewayProfileData.getName(), diameterGatewayProfileData.getSupportedVendorList()
				, usageReportingType, revalidationMode, chargingRuleInstallMode, supportedStandard, usageMeteringStandard
				, diameterGatewayProfileData.getTimeout().intValue(), diameterGatewayProfileData
				.getInitConnectionDuration().intValue()
				, gxApplicationId, rxApplicationId, gyApplicationId, syApplicationId, s9ApplicationId
				, gxVendorId, rxVendorId, gyVendorId, syVendorId, s9VendorId
				, diameterGatewayProfileData.getDwInterval(), GatewayComponent.valueOf(diameterGatewayProfileData.getGatewayType())
				, diameterPacketMappingFactory.getPCCToDiameterMappings()
				, diameterPacketMappingFactory.getDiameterToPCCMappings()
				, sessCleanupOnCER, sessCleanupOnDPR, sendDPROnCloseEvent, tcpNagleAlgo, diameterGatewayProfileData.getCerAvps()
				, diameterGatewayProfileData.getDprAvps(), diameterGatewayProfileData.getDwrAvps(), diameterGatewayProfileData
				.getSocketReceiveBufferSize()
				, diameterGatewayProfileData.getSocketSendBufferSize(), sessionLookupKeys
                , serviceGuideFactory.create(diameterGatewayProfileData.getServiceGuidingDatas())
                , scriptDataFactory.create(diameterGatewayProfileData.getGroovyScriptDatas())
				, pccMonitoringSupported);

	}

	public Vendor createVendor(VendorData vendorData) {
		return Vendor.fromName(vendorData.getName());
	}
}
