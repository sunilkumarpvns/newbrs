package com.elitecore.aaa.diameter.translators;

import java.util.List;

import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.aaa.diameter.translators.BaseTranslator.KeywordContext;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

public class DBSessionKeyword extends TranslationKeyword {
	
	private static final String MODULE = "DBSESSION-KEYWORD";

	public DBSessionKeyword(String name, KeywordContext keywordContext) {
		super(name, keywordContext);
	}

	@Override
	public String getKeywordValue(TranslatorParams params, String strKeyword,
			boolean isRequest, ValueProvider valueProvider) {
		
		DiameterRequest diameterRequest;
		
		if (isRequest) {
			diameterRequest = (DiameterRequest) params.getParam(AAATranslatorConstants.FROM_PACKET);
		} else {
			diameterRequest = (DiameterRequest) params.getParam(AAATranslatorConstants.SOURCE_REQUEST);
		}
		
		List<SessionData> locatedSessionDatas = diameterRequest.getLocatedSessionData();
		
		String[] argument = getOperatorArgument(strKeyword);
		if (argument == null || argument.length != 1) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, TranslatorConstants.DBSESSION + 
						" keyword must have only one operator argument");
			}
			return null;
		}
		if (Collectionz.isNullOrEmpty(locatedSessionDatas)) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "No session data was found from source packet with HbH Id: "
						+ diameterRequest.getHop_by_hopIdentifier() + " and EtE Id: " + diameterRequest.getEnd_to_endIdentifier());
			}
			return null;
		}
		for (SessionData sessionData : locatedSessionDatas) {
			String value = sessionData.getValue(argument[0].trim());
			if (Strings.isNullOrBlank(value) == false) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Value: " + value + " was found for the Argument: " + argument[0]);
				}
				return value;
			}
		}
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, argument[0] + " was not found in Session Data");
		}
		return null;
	}

}
