package com.elitecore.aaa.rm.translator;

import com.elitecore.aaa.diameter.translators.BaseTranslator.KeywordContext;
import com.elitecore.aaa.diameter.translators.TranslationKeyword;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;

public class RadiusSrcDstKeyword extends TranslationKeyword{

	private static String MODULE = "RADIUS-SRCDST-KEYWORD";
	public RadiusSrcDstKeyword(String keyword,KeywordContext context) {
		super(keyword,context);
	}

	@Override
	public String getKeywordValue(TranslatorParams params,String strKeyword,boolean isRequest,ValueProvider valueProvider){
		
		Object object = params.getParam(getName());
		if(object!=null){
			final RadServiceRequest radServiceRequest = (RadServiceRequest)object;
			String arg1 = getKeywordArgument(strKeyword);
			if(arg1!=null){
				if(context.isKeyword(arg1)){
					arg1 = context.getKeywordValue(arg1, params, isRequest,valueProvider);
					if(arg1!=null){
						IRadiusAttribute radiusAttribute = radServiceRequest.getRadiusAttribute(arg1, true);
						if(radiusAttribute!=null){
							return radiusAttribute.getStringValue();
						}
					}
				}else{
					IRadiusAttribute radiusAttribute = radServiceRequest.getRadiusAttribute(arg1, true);
					if(radiusAttribute!=null){
						return radiusAttribute.getStringValue();
					}
				}
			}
		}else {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Can't evaluate keyword :"+strKeyword+" Reason Radius Packet not found");
		}
		return null;
		
	}

}
