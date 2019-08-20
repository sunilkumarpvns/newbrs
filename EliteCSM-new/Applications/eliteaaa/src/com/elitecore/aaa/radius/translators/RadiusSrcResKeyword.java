package com.elitecore.aaa.radius.translators;

import static com.elitecore.commons.logging.LogManager.getLogger;

import javax.annotation.Nullable;

import com.elitecore.aaa.diameter.translators.BaseTranslator.KeywordContext;
import com.elitecore.aaa.diameter.translators.TranslationKeyword;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;

/**
 * Keyword provides requested attribute, passed as keyword argument, from original response.
 * <br/>
 * Keyword is meant to be used only in request translation mapping 
 * <br/>
 *	<b>Keyword Syntax</b> 
 * <br/>
 * ${SRCRES}:argument 
 * <br/>
 * here argument can be attribute id or any other keyword 
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

public class RadiusSrcResKeyword extends TranslationKeyword {
	
	private static final String MODULE = "RADIUS-SRC-RES-KEYWORD";
	
	public RadiusSrcResKeyword(String keyword, KeywordContext context) {
		super(keyword, context);
	}

	/**
	 * @param params parameters that must contain {@link RadServiceResponse} value with key ${SRCRES}  
	 * @return attribute value if found, null otherwise
	 */
	@Override
	public @Nullable String getKeywordValue(TranslatorParams params, String strKeyword, boolean isRequest, ValueProvider valueProvider) {
		
		if (isRequest == false) {
			if(getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Keyword: " + TranslatorConstants.SRCRES + " is unavailable in response translation.");
			}
			return null;
		}
		
		final RadServiceResponse originalServiceResponse = (RadServiceResponse) params.getParam(TranslatorConstants.SRCRES);
		if (originalServiceResponse == null) {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Can not evaluate keyword: " + strKeyword + ", Reason: Radius Packet not found.");
			}
			return null;
		}

		String arg1 = getKeywordArgument(strKeyword);
		if (arg1 == null) {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Can not evaluate keyword: " + strKeyword + ", Reason: Missing keyword argument");
			}
			return null;
		}

		if (context.isKeyword(arg1)) {
			String resolvedArg1 = context.getKeywordValue(arg1, params, isRequest, valueProvider);
			if (resolvedArg1 == null) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Can not evaluate keyword: " + strKeyword + ", Reason: Can't evaluate keyword: " +
							arg1 + " from argument");
				}
				return null;
			}
			arg1 = resolvedArg1;
		}
		
		IRadiusAttribute radiusAttribute = originalServiceResponse.getRadiusAttribute(true, arg1);
		if( radiusAttribute == null){
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Radius packet does not have required attribute");
			}
			return null;
		}
		return radiusAttribute.getStringValue();
	}
}
