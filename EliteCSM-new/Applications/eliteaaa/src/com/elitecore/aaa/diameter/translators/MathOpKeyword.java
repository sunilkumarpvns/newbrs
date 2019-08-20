package com.elitecore.aaa.diameter.translators;

import com.elitecore.aaa.diameter.translators.BaseTranslator.KeywordContext;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.commons.util.MathOperationUtility;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;

public class MathOpKeyword extends TranslationKeyword{
	
	public MathOpKeyword(String name, KeywordContext context){
		super(name, context);
	}

	@Override
	public String getKeywordValue(TranslatorParams params, String strKeyword, boolean isRequest, ValueProvider valueProvider){
		
		String keyworgArgument = getKeywordArgument(strKeyword);
		if (keyworgArgument != null){
			String sourceKeyArg;
			if (context.isKeyword(keyworgArgument)){
				sourceKeyArg = context.getKeywordValue(keyworgArgument, params, isRequest, valueProvider);
			}else {
				sourceKeyArg = valueProvider.getStringValue(keyworgArgument);
			}
			return MathOperationUtility.getValue(strKeyword, valueProvider, sourceKeyArg);
		} 
		return null;
	}
}
