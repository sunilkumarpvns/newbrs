package com.elitecore.diameterapi.diameter.translator.keyword;

import javax.annotation.Nonnull;

import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.util.DiameterAVPValueProvider;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;

/**
 * 
 * Handling of Diameter Packet Keywords, 
 * like ${SRCREQ} and ${DSTREQ}.
 * 
 * KeywordType decides the key to be fetched from Params.
 * 
 * @author monica.lulla
 *
 */
public class DiameterKeywordValueProvider extends KeywordValueProvider {

	private String keywordType;
	private String argument;

	public DiameterKeywordValueProvider(String keywordType, String argument) {
		super(argument);
		this.keywordType = keywordType;
		this.argument = argument;
	}

	@Override
	protected @Nonnull DiameterAVPValueProvider getValueProvider(TranslatorParams params)
			throws MissingIdentifierException {
		final DiameterPacket packet = (DiameterPacket) params.getParam(keywordType);
		
		if (packet == null) {
			throw new MissingIdentifierException("Can't evaluate "+ keywordType +
					" for: " + argument + ", Reason: Packet not found");
		}
		return new DiameterAVPValueProvider(packet);
	}

}
