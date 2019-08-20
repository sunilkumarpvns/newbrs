package com.elitecore.aaa.radius.translators.copypacket.keyword;

import javax.annotation.Nonnull;

import com.elitecore.aaa.radius.translators.RadiusAttributeValueProvider;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.diameter.translator.keyword.KeywordValueProvider;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;

/**
 * 
 * It handles keyword based on the values from Radius Packet.
 * Radius Packet will be fetched from Params, based on KeywordType parameter,
 * All the keywords that need Value from Radius Packet,
 * can be handled via <code>RadiusPacketKeywordValueProvider</code>
 * 
 * Example, ${DSTREQ} keyword uses Values from Radius Packet. 
 * 
 * 
 * @author monica.lulla
 *
 */
public class RadiusPacketKeywordValueProvider extends KeywordValueProvider {

	public RadiusPacketKeywordValueProvider(String argument, String keywordType) {
		super(argument);
		this.argument = argument;
		this.keywordType = keywordType;
	}

	private String argument;
	private String keywordType;

	@Override
	protected @Nonnull RadiusAttributeValueProvider getValueProvider(TranslatorParams params)
			throws MissingIdentifierException {
		
		final RadiusPacket destinationRequest = (RadiusPacket) params.getParam(keywordType);
		
		if (destinationRequest == null) {
			throw new MissingIdentifierException("Can't evaluate "+ keywordType +
					":" + argument + ", Reason: Packet not found");
		}
		return new RadiusAttributeValueProvider(destinationRequest);
	}

}
