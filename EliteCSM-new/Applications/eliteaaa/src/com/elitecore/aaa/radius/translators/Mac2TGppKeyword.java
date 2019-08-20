package com.elitecore.aaa.radius.translators;

import java.util.List;

import com.elitecore.aaa.diameter.translators.BaseTranslator.KeywordContext;
import com.elitecore.aaa.diameter.translators.TranslationKeyword;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.commons.util.TranslationOperationUtility;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;

public class Mac2TGppKeyword extends TranslationKeyword{
	
	private static final String MODULE = "MAC2TGPP-KEYWORD";
	private static final String MAC2TGPP = "mac2tgpp";

	public Mac2TGppKeyword(String name, KeywordContext keywordContext) {
		super(name, keywordContext);
	}

	@Override
	public String getKeywordValue(TranslatorParams params, String strKeyword,
			boolean isRequest, final ValueProvider valueProvider) {
		
		String argument = getKeywordArgument(strKeyword);
		String expressionString = null;
		
		if(argument!=null){
			if(context.isKeyword(argument))
				expressionString = getValue(strKeyword, context.getKeywordValue(argument,params,isRequest, valueProvider));
			else 
				expressionString = getValue(strKeyword, argument);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "No source argument is configured for expression : " + strKeyword + " in translation mapping.");
			return null;
		}

		if(expressionString != null){
			try {
				Expression  valueExpression = Compiler.getDefaultCompiler().parseExpression(expressionString);
				expressionString = valueExpression.getStringValue(new com.elitecore.exprlib.parser.expression.ValueProvider() {

					@Override
					public List<String> getStringValues(String identifier)
							throws InvalidTypeCastException, MissingIdentifierException {
						return null;
					}

					@Override
					public String getStringValue(String identifier)
							throws InvalidTypeCastException, MissingIdentifierException {
						if(identifier !=  null)
							return valueProvider.getStringValue(identifier);
						else 
							return null;
					}

					@Override
					public List<Long> getLongValues(String identifier)
							throws InvalidTypeCastException, MissingIdentifierException {
						return null;
					}

					@Override
					public long getLongValue(String identifier)
							throws InvalidTypeCastException, MissingIdentifierException {
						return 0;
					}

					@Override
					public Object getValue(String key) {
						return null;
					}
				});
			} catch (Exception ex) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Configured expression : " + expressionString + " for translation mapping is invalid.");
				}
			}
			return expressionString;
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Configured expression : " + strKeyword + " for translation mapping is invalid.");
			return null;
		}
	}
	
	private String getValue(String strKeyword,String stringValue) {
		
		String expression = "";
		if(strKeyword != null && strKeyword.trim().length() > 0){
			List<String> tokens = TranslationOperationUtility.getTokens(strKeyword);
			int size = tokens.size();
			if(tokens != null && size > 0){
				for(int tokenIndex=0;tokenIndex < size;tokenIndex++){
					if(tokenIndex > 1)
						break;
					
					String currentToken = tokens.get(tokenIndex);
					String[] keywordArg = TranslationOperationUtility.getTokenArguments(currentToken, strKeyword);
					
					if(keywordArg.length == 3){
						expression = MAC2TGPP + "(" + keywordArg[0] +"," + stringValue + ","+keywordArg[1]+","+keywordArg[2] +")";
					}else if(keywordArg.length >= 4){
						expression = MAC2TGPP + "(" + keywordArg[0] +"," + stringValue + ","+keywordArg[1]+","+keywordArg[2] +"," +keywordArg[3] +")";
					}
				}
			}
		}

		if(expression != null && expression.trim().length()>0){
			return expression;
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "No. of arguments for configure Expression : " + strKeyword + " for Translation Mapping in invalid.");
			}
			return null;
		}
	}
}