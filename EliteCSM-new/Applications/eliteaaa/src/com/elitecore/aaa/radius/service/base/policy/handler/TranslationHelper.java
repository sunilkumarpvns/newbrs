package com.elitecore.aaa.radius.service.base.policy.handler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.translator.TranslationAgent;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;

/**
 * 
 * @author kuldeep.panchal
 * @author narendra.pathai
 *
 */
public class TranslationHelper {
	private static final String MODULE = "TRANSLATION-HELPER";
	private boolean dummyMapping = false;
	private @Nullable String selectedTranslationMapping;
	private @Nonnull final RadServiceRequest radServiceRequest;
	private @Nonnull final RadServiceResponse radServiceResponse;
	private final boolean includeInfoAttribute;
	private @Nullable String translationMapping;
	
	public TranslationHelper(@Nullable String translationMapping, 
			@Nonnull RadServiceRequest request, @Nonnull RadServiceResponse response,
			boolean includeInfoAttributes) {
		this.translationMapping = translationMapping;
		this.radServiceRequest = request;
		this.radServiceResponse = response;
		this.includeInfoAttribute = includeInfoAttributes;
	}

	public boolean isDummyMappingApplicable() {
		return dummyMapping;
	}
	
	public RadiusPacket translate() {
		
		RadiusPacket sourceRadiusPacket = new RadiusPacket();
		sourceRadiusPacket.setBytes(radServiceRequest.getRequestBytes(includeInfoAttribute));

		if (Strings.isNullOrBlank(translationMapping)) {
			return sourceRadiusPacket;
		}

		try {
			return translateRequest();
		} catch (TranslationFailedException e) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Failed to translate Request using mapping: " 
						+ translationMapping + ", Reason: " + e.getMessage());
			}
			LogManager.getLogger().trace(MODULE, e);
			return sourceRadiusPacket;
		}
	}
	
	private RadiusPacket translateRequest() 
			throws TranslationFailedException {
		
		RadiusPacket translatedPacket = new RadiusPacket();
		translatedPacket.setPacketType(radServiceRequest.getPacketType());
		translatedPacket.setIdentifier(radServiceRequest.getIdentifier());
		translatedPacket.setAuthenticator(radServiceRequest.getAuthenticator());
		
		
		TranslatorParams params = new TranslatorParamsImpl(radServiceRequest, translatedPacket);
		params.setParam(TranslatorConstants.SRCRES, radServiceResponse);
		
		/** Set shared secret of own AAA(LHS) will be used to translate request of radius translator */
		String sharedSecret = radServiceResponse.getClientData().getSharedSecret(radServiceRequest.getPacketType());
		params.setParam(TranslatorConstants.SHARED_SECRET, sharedSecret);
		
		TranslationAgent.getInstance().translate(translationMapping, params, TranslatorConstants.REQUEST_TRANSLATION);
		selectedTranslationMapping = String.valueOf(params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING));
		dummyMapping = Boolean.parseBoolean(String.valueOf(params.getParam(TranslatorConstants.DUMMY_MAPPING)));
		translatedPacket.refreshPacketHeader();
		return translatedPacket;
	}
	
	public byte[] getDummyResponseBytes() {
		
		byte[] finalRequestBytes = null;
		RadiusPacket translatedPacket;
		try {
			translatedPacket = translateDummyResponse();
			if(translatedPacket!=null) {
				finalRequestBytes = translatedPacket.getBytes(); 
			}
		} catch (TranslationFailedException e) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Failed to translate Response using mapping: "
						+ translationMapping + ", Reason: "+e.getMessage());
			}
			LogManager.getLogger().trace(MODULE, e);
		}
		return finalRequestBytes;
	}
	
	private RadiusPacket translateDummyResponse() throws TranslationFailedException {
		
		RadiusPacket translatedPacket = new RadiusPacket();
		translatedPacket.setPacketType(radServiceResponse.getPacketType());
		
		TranslatorParams params = new TranslatorParamsImpl(null, translatedPacket, radServiceRequest, null);
		params.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, selectedTranslationMapping);
		
		/** Set shared secret of own AAA(LHS) will be used to translate response(for dummy response) of radius translator */
		String sharedSecret = radServiceResponse.getClientData().getSharedSecret(radServiceRequest.getPacketType());
		params.setParam(TranslatorConstants.SHARED_SECRET, sharedSecret);
		TranslationAgent.getInstance().translate(translationMapping, params, TranslatorConstants.RESPONSE_TRANSLATION);
		translatedPacket.refreshPacketHeader();
		
		return translatedPacket;
	}
	
	public byte[] getResponseBytes(RadiusPacket sourcePacket, 
			RadiusPacket translatedDestinationPacket, final String sharedSecret) {
		
		byte[] finalRequestBytes = null;
		if (Strings.isNullOrBlank(translationMapping)) {
			return null;
		}
		
		RadiusPacket translatedPacket;
		try {
			translatedPacket = translateResponse(translationMapping, sourcePacket,
					translatedDestinationPacket, sharedSecret);
			if (translatedPacket!=null) {
				finalRequestBytes = translatedPacket.getBytes(); 
			}
		} catch (TranslationFailedException e) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Failed to translate Response using mapping: "
						+ translationMapping + ", Reason: " + e.getMessage());
			}
			LogManager.getLogger().trace(MODULE, e);
		}
		
		return finalRequestBytes;
	}
	
	private RadiusPacket translateResponse(String translationMapping, RadiusPacket sourcePacket, 
			RadiusPacket translatedDestinationPacket, String sharedSecret) throws TranslationFailedException {
		
		RadiusPacket translatedPacket = new RadiusPacket();
		
		translatedPacket.setPacketType(sourcePacket.getPacketType());
		translatedPacket.setIdentifier(sourcePacket.getIdentifier());
		
		TranslatorParams params = new TranslatorParamsImpl(sourcePacket, translatedPacket, 
				radServiceRequest, translatedDestinationPacket);
		params.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, selectedTranslationMapping);
		
		/** set shared secret in translator params used for translate response of radius translator*/
		params.setParam(TranslatorConstants.SHARED_SECRET,sharedSecret);
		
		TranslationAgent.getInstance().translate(translationMapping,params, TranslatorConstants.RESPONSE_TRANSLATION);
		translatedPacket.refreshPacketHeader();
		
		return translatedPacket;
	}
}