package com.elitecore.aaa.diameter.translators;

import com.elitecore.aaa.diameter.translators.BaseTranslator.KeywordContext;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;

public abstract class TranslationKeyword {
	
	private String name="";
	protected KeywordContext context;
	
	private static final String OPERATION_ARGUMENT_SEPARATOR  = "-";
	
	public TranslationKeyword(String name,KeywordContext keywordContext) {
		if(name!=null && name.trim().length()>0)
			this.name = name;
		this.context = keywordContext;
	}
	

	public String getName() {
		return name;
	}
	
	public abstract String getKeywordValue(TranslatorParams params,String strKeyword,boolean isRequest,ValueProvider valueProvider);
	
	@Override
	public String toString() {
		return "Keyword : "+name;
	}
	
	protected String getKeywordArgument(String strKeyword){
		 String keywordArgument = null;
		 if(strKeyword!=null){
			 int index = strKeyword.indexOf("}:");
			 if(index!=-1){
				 keywordArgument = strKeyword.substring(index+2);
				 if(keywordArgument.length()>0)
					 return keywordArgument;
			 }	 
		 }
		 return null;
	}
	
	protected String[] getOperatorArgument(String strKeyword){

        String[] operatorArguments = null;
        int index = strKeyword.indexOf(OPERATION_ARGUMENT_SEPARATOR);

        if(index != -1){
            int braceIndex = strKeyword.indexOf("}");
            if (braceIndex != -1){
                 String tempString = strKeyword.substring(index+1,braceIndex);
                 operatorArguments = tempString.split(",");
            }
        }

        return operatorArguments;
   }


	

}
     