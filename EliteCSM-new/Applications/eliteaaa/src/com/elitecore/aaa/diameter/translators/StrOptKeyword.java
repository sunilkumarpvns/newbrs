package com.elitecore.aaa.diameter.translators;

import com.elitecore.aaa.diameter.translators.BaseTranslator.KeywordContext;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.commons.util.StringOperationUtility;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;

public class StrOptKeyword extends TranslationKeyword{

	public StrOptKeyword(String name,KeywordContext context) {
		super(name,context);
	}

	@Override
	public String getKeywordValue(TranslatorParams params,String strKeyword,boolean isRequest,ValueProvider valueProvider) {
		
		String arg1 = getKeywordArgument(strKeyword);
		if(arg1!=null){
			if(context.isKeyword(arg1)){
				return StringOperationUtility.getValue(strKeyword, valueProvider, context.getKeywordValue(arg1, params, isRequest, valueProvider));
			}else {
				return StringOperationUtility.getValue(strKeyword, valueProvider, valueProvider.getStringValue(arg1));
			}	
		}else {
			return StringOperationUtility.getValue(strKeyword, valueProvider, arg1);
		}
		
	}

}
