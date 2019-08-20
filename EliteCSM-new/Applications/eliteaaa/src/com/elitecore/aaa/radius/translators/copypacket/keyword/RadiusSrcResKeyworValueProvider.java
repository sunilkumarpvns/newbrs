package com.elitecore.aaa.radius.translators.copypacket.keyword;

import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.translators.RadServiceResponseValueProvider;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.diameter.translator.keyword.KeywordValueProvider;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

/**
 * <b>Keyword Syntax</b> 
 * <br/>
 * ${SRCRES}:argument 
 * <br/>
 * here argument is attribute id ( grouped or non-grouped) 
 * <br/>
 * <b>Example</b> 
 * <br/>
 * ${SRCRES}:21067:118 - will provide Src-Addr from original response to request packet
 * <br/>
 * <p>
 * This keyword is used in request mapping when any attribute received from an external 
 * system and needed to include it in request to second external system. 
 * (for further details - JIRA-2813)
 * </p>
 * @author malav.desai
 *
 */
public class RadiusSrcResKeyworValueProvider extends KeywordValueProvider {
	
	public RadiusSrcResKeyworValueProvider(String argument) {
		super(argument);
	}

	/**
	 * @param params parameters that must contain {@link RadServiceResponse} value with key ${SRCRES}
	 */
	@Override
	protected ValueProvider getValueProvider(TranslatorParams params)
			throws MissingIdentifierException {
		
		final RadServiceResponse originalServiceResponse = (RadServiceResponse) params.getParam(TranslatorConstants.SRCRES);
		if (originalServiceResponse == null) {
			throw new MissingIdentifierException("Can not evaluate keyword: " + TranslatorConstants.SRCRES + ", Reason: Radius Packet not found.");
		}

		return new RadServiceResponseValueProvider(originalServiceResponse);
	}
	
}