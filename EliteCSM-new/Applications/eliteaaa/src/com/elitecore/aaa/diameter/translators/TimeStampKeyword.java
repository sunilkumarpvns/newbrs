package com.elitecore.aaa.diameter.translators;


import java.text.SimpleDateFormat;
import java.util.Date;

import com.elitecore.aaa.diameter.translators.BaseTranslator.KeywordContext;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;

public class TimeStampKeyword extends TranslationKeyword{

	private static final String MODULE = "TIME-STAMP-KYWRD";

	public TimeStampKeyword(String name, KeywordContext context) {
		super(name, context);
	}

	@Override
	public String getKeywordValue(TranslatorParams params,String strKeyword,boolean isRequest,ValueProvider valueProvider) {
		String returnValue = null; 
		String[] operatorArguments = getOperatorArgument(strKeyword);
		
		String keywordArg = getKeywordArgument(strKeyword);
		
		if (keywordArg != null){
			if(context.isKeyword(keywordArg)){
				keywordArg = context.getKeywordValue(keywordArg, params, isRequest,valueProvider);
			}else{
				keywordArg = valueProvider.getStringValue(keywordArg);
			}
		}
		
		if (operatorArguments != null && operatorArguments[0] != null && operatorArguments[0].trim().length() > 0){

			SimpleDateFormat dateFormat = new SimpleDateFormat("E M dd HH:mm:ss yyyy");
			try {
				dateFormat.applyPattern(operatorArguments[0]);
			} catch (IllegalArgumentException e){
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(MODULE, "Given Pattern " + operatorArguments[0] + "Could not be parsed. Reason: "+ e.getMessage()); 
					LogManager.getLogger().info(MODULE, "Using default pattern: " + dateFormat.toPattern());
				}
			}
			Date date = null;
			if (keywordArg != null ){
				date = new Date(Long.parseLong(keywordArg));
			} else {
				date = new Date();
			}
			returnValue = dateFormat.format(date);
			
		} else {
			if (keywordArg != null){
				returnValue = keywordArg;
			} else {
				long currentTime = (System.currentTimeMillis());
				returnValue = String.valueOf(currentTime);
			}
			
		}
		return returnValue;
	}
}
