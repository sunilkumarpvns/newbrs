package com.elitecore.aaa.radius.translators.copypacket.keyword;

import javax.annotation.Nonnull;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.translators.RadServiceRequestValueProvider;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.diameter.translator.keyword.KeywordValueProvider;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;

/**
 * 
 * It handles keyword based on the values from Radius Service Request.
 * Radius Service Request will be fetched from Params, based on KeywordType parameter,
 * All the keywords that need Value from Radius Service Request,
 * can be handled via <code>RadiusServiceRequestKeywordValueProvider</code>
 * 
 * Example, ${SRCREQ} keyword uses Values from Radius Packet. 
 * 
 * 
 * @author monica.lulla
 *
 */
public class RadiusServiceRequestKeywordValueProvider extends KeywordValueProvider {

	public RadiusServiceRequestKeywordValueProvider(String argument, String keywordType) {
		super(argument);
		this.argument = argument;
		this.keywordType = keywordType;
	}

	private String argument;
	private String keywordType;

	@Override
	protected @Nonnull RadServiceRequestValueProvider getValueProvider(TranslatorParams params)
			throws MissingIdentifierException {
		
		final RadServiceRequest sourceRequest = (RadServiceRequest) params.getParam(keywordType);
		
		if (sourceRequest == null) {
			throw new MissingIdentifierException("Can't evaluate "+ keywordType +
					":" + argument + ", Reason: Packet not found");
		}
		return new RadServiceRequestValueProvider(sourceRequest);
	}

}
