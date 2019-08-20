package com.elitecore.aaa.diameter.translators;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.aaa.diameter.translators.BaseTranslator.KeywordContext;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;

public class WsSrcDstKeyword extends TranslationKeyword{

	
	private static String MODULE = "WS-SRCDDST-KEYWORD";
	
	public WsSrcDstKeyword(String keyword,KeywordContext context) {
		super(keyword,context);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getKeywordValue(TranslatorParams params,String strKeyword,boolean userTranslationKeyword,ValueProvider valueProvider){
		
		Object object = params.getParam(getName());
		
		if(object!=null){
			final Map<String, String> keyValueMap = (HashMap<String, String>)object;
			String arg1 = getKeywordArgument(strKeyword);
			if(arg1!=null){
				if(context.isKeyword(arg1)){
					arg1 = context.getKeywordValue(arg1, params, userTranslationKeyword, new ValueProvider(){

						@Override
						public String getStringValue(String identifier) {
							if(identifier!=null){
								return keyValueMap.get(identifier);
							}	
							return null;	
							
						}
						
					});
					if(arg1!=null){
						return keyValueMap.get(arg1);
					}
				}else{
					return keyValueMap.get(arg1);
				}
			}
			
		}else {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Can't evaluate keyword :"+strKeyword+" Reason Webservice Request MAP not found");
		}
		return null;
		
	}

}
