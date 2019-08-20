package com.elitecore.aaa.radius.translators.copypacket;

import java.util.Map.Entry;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.script.TranslationMappingScript;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusGroupedAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.diameter.translator.BaseCopyPacketTranslator;
import com.elitecore.diameterapi.diameter.translator.data.CopyPacketTranslatorPolicyData;

public class RadiusCopyPacketTranslator extends BaseCopyPacketTranslator<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute> {

	private static final String MODULE = "RAD-CPY-PCK-TRANSLATR";

	public RadiusCopyPacketTranslator(
			CopyPacketTranslatorPolicyData policyData,
			ServerContext serverContext) {
		super(policyData, serverContext.getExternalScriptsManager(), TranslationMappingScript.class);
	}

	@Override
	protected RadiusCopyPacketTranslatorPolicy createCopyPacketTranslationPolicy() {
		return new RadiusCopyPacketTranslatorPolicy();
	}

	@Override
	protected void copyRequest(TranslatorParams params)
			throws CloneNotSupportedException {
		
		RadServiceRequest fromPacket = (RadServiceRequest) params.getParam(TranslatorConstants.FROM_PACKET);
		
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Copying Radius Request with Identifier=" + 
					fromPacket.getIdentifier());
		}
		
		RadiusPacket toPacket = (RadiusPacket) params.getParam(TranslatorConstants.TO_PACKET);
		toPacket.setBytes(fromPacket.getRequestBytes());
		toPacket.addInfoAttributes(fromPacket.getInfoAttributes());
		toPacket.refreshPacketHeader();
		toPacket.refreshInfoPacketHeader();
	}

	@Override
	protected void copyResponse(TranslatorParams params)
			throws CloneNotSupportedException {
		
		RadiusPacket fromPacket = (RadiusPacket) params.getParam(TranslatorConstants.FROM_PACKET);
		if(fromPacket == null) {
			if(LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "From Response Packet not available for Translation for Mapping: " + getName());
			}
			return;
		}
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Copying Radius Response with Identifier=" + 
					fromPacket.getIdentifier());
		}
		RadiusPacket toPacket = (RadiusPacket) params.getParam(TranslatorConstants.TO_PACKET);
		toPacket.setBytes(fromPacket.getBytes());
		toPacket.addInfoAttributes(fromPacket.getRadiusInfoAttributes());
	}

	@Override
	protected void applyDummyResponse(TranslatorParams params) {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Applying Dummy Mappings for Mapping: " + getName());
		}
		if (Maps.isNullOrEmpty(getDummyMappings())) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "No Dummy Mappings available for: " + getName());
			}	
		}
		RadiusPacket toPacket = (RadiusPacket) params.getParam(TranslatorConstants.TO_PACKET);
		for (Entry<String, String> dummyEntry : getDummyMappings().entrySet()) {
			IRadiusAttribute radiusAttribute = Dictionary.getInstance().getKnownAttribute(dummyEntry.getKey());
			if(radiusAttribute == null){
				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(MODULE, "Attribute: " + dummyEntry.getKey() + 
							" not found in Dictionary.");
				}
				continue;
			}
			try {
				radiusAttribute.setStringValue(dummyEntry.getValue());
				toPacket.addAttribute(radiusAttribute);
			} catch (Exception e) {
				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(MODULE, "Error Adding Attribute: " + dummyEntry.getKey() + 
							" with Value: " + dummyEntry.getValue() + ", Reason: " + e.getMessage());
				}
			}
		}
	}

}
