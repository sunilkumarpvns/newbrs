package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.util.constants.Vendor;
import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.corenetvertex.constants.GatewayComponent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.UsageReportingType;
import com.elitecore.corenetvertex.sm.gateway.RadiusGatewayProfileData;
import com.elitecore.netvertex.core.util.InterimIntervalPredicate;
import com.elitecore.netvertex.gateway.radius.conf.impl.RadiusGatewayProfileConfigurationImpl;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusChargingRuleDefinitionPacketMapping;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class RadiusGatewayProfileFactory {

	private static final String MODULE = "RAD-GW-PRF-FCTRY";
	private ScriptDataFactory scriptDataFactory;
	private RadiusPacketMappingFactory radiusPacketMappingFactory;
	private RadiusPCCRuleMappingFactory radiusPCCRuleMappingFactory;
	private ServiceGuideFactory serviceGuideFactory;

	public RadiusGatewayProfileFactory(RadiusPCCRuleMappingFactory radiusPCCRuleMappingFactory,
									   RadiusPacketMappingFactory radiusPacketMappingFactory,
									   ScriptDataFactory scriptDataFactory, ServiceGuideFactory serviceGuideFactory) {
		this.radiusPCCRuleMappingFactory = radiusPCCRuleMappingFactory;
		this.radiusPacketMappingFactory = radiusPacketMappingFactory;
		this.scriptDataFactory = scriptDataFactory;
		this.serviceGuideFactory = serviceGuideFactory;
	}
	
	public RadiusGatewayProfileConfigurationImpl create(RadiusGatewayProfileData radiusGatewayProfileData) throws LoadConfigurationException {

        Vendor vendor = Vendor.fromID(Long.parseLong(radiusGatewayProfileData.getVendorId()));
        String firmware = radiusGatewayProfileData.getFirmware();
        if(firmware == null){
            firmware = " ";
        }
        
        String description = radiusGatewayProfileData.getDescription();
        if(description == null){
            description = " ";
        }
        
        boolean isAccountingResponseEnable = radiusGatewayProfileData.getSendAccountingResponse();
        
        String supportedVendors = radiusGatewayProfileData.getSupportedVendorList();
        if(supportedVendors == null){
            supportedVendors = " ";
        }
        
        PCRFKeyValueConstants usageReportingType = PCRFKeyValueConstants.USAGE_REPORTING_TYPE_CUMULATIVE;
        if(Strings.isNullOrBlank(radiusGatewayProfileData.getUsageReportingType()) == false) {
			UsageReportingType tempUsageReportingType = UsageReportingType.valueOf(radiusGatewayProfileData.getUsageReportingType());
			if(tempUsageReportingType == UsageReportingType.NON_CUMULATIVE) {
				usageReportingType = PCRFKeyValueConstants.USAGE_REPORTING_TYPE_NON_CUMULATIVE;
			}
        }

        int interimIntervalInMin = 0;
        if (radiusGatewayProfileData.getInterimInterval() != null) {
        	interimIntervalInMin = radiusGatewayProfileData.getInterimInterval();
		}
		if(interimIntervalInMin <= 0) {
            getLogger().warn(MODULE, "Considering default value" + 60 + ". Reason: negative value("+ interimIntervalInMin +" received");
			interimIntervalInMin = 60;
		}
		
        InterimIntervalPredicate interimIntervalPredicate = new InterimIntervalPredicate(interimIntervalInMin);
		
		String strRevalidationMode = radiusGatewayProfileData.getRevalidationMode();
		PCRFKeyValueConstants tempRevalidationMode = PCRFKeyValueConstants.fromString(PCRFKeyConstants.REVALIDATION_MODE, strRevalidationMode);
		PCRFKeyValueConstants revalidationMode = PCRFKeyValueConstants.REVALIDATION_MODE_SERVER_INITIATED;
		if(tempRevalidationMode != null) {
			revalidationMode = tempRevalidationMode;
		} else {
			LogManager.getLogger().warn(MODULE, "Considering REVALIDATION_MODE as " + revalidationMode.val + ". Reason: Invalid configuration value: " + strRevalidationMode);
		}

		RadiusChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping = radiusPCCRuleMappingFactory.create(radiusGatewayProfileData.getId(),
				radiusGatewayProfileData.getName(),
				radiusGatewayProfileData.getRadiusGwProfilePCCRuleMappings());

		radiusPacketMappingFactory.create(radiusGatewayProfileData, radiusGatewayProfileData.getRadiusGwProfilePacketMappings(), chargingRuleDefinitionPacketMapping);

		return new RadiusGatewayProfileConfigurationImpl(radiusGatewayProfileData.getId(), CommunicationProtocol.RADIUS, vendor, firmware
				, isAccountingResponseEnable, description, radiusGatewayProfileData.getName(), supportedVendors, usageReportingType, radiusGatewayProfileData.getTimeout()
				, radiusGatewayProfileData.getMaxRequestTimeout(), radiusGatewayProfileData.getStatusCheckDuration(), radiusGatewayProfileData.getIcmpPingEnabled()
				, radiusGatewayProfileData.getRetryCount(), interimIntervalInMin, revalidationMode, interimIntervalPredicate
				, GatewayComponent.valueOf(radiusGatewayProfileData.getGatewayType())
				, radiusPacketMappingFactory.getPCCToRadiusMappings(), radiusPacketMappingFactory.getRadiusToPCCMappings(),
				scriptDataFactory.create(radiusGatewayProfileData.getGroovyScriptDatas()) /* pccRuleMappings*/
				, serviceGuideFactory.create(radiusGatewayProfileData.getServiceGuidingDatas())
				);
	}
}
